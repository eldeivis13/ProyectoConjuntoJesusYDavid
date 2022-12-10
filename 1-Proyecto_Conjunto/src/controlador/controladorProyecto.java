package controlador;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import Persistencias.EspacioNatural;
import Persistencias.Parque;
import Persistencias.Parques;
import vista.vistaProyecto;

public class controladorProyecto implements ActionListener{
	
	vistaProyecto vista = new vistaProyecto();
	DefaultListModel<String> modelo = new DefaultListModel<>();
	ArrayList<Parques> parque = new ArrayList<Parques>();
	static Parques parques = new Parques();
	boolean btnProvincia = false, btnTipo = false;

	public controladorProyecto(vistaProyecto vista) {
		this.vista = vista;
		this.vista.rdbtnPorProvincia.addActionListener(this);
		this.vista.rdbtnPorTipo.addActionListener(this);
		this.vista.cBFiltros.addActionListener(this);
		this.vista.btnIniciar.addActionListener(this);
		
	}
	
	public Connection createConnection() throws ClassNotFoundException, SQLException {
		Connection connection = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ESPACIOS_NATURALES_PROTEGIDOS?serverTimezone=UTC", "root", "root");
			connection.setAutoCommit(false);
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		}
		
		return connection;
	}
	
	public Connection createConnByProp() throws ClassNotFoundException, SQLException, IOException {
		
		Connection connection = null;
		
		try {
			Properties propiedades = new Properties();
			propiedades.load(new FileReader("src/resources/database.properties"));
			String driver = propiedades.getProperty("database.driver");
			String url = propiedades.getProperty("database.url");
			String user = propiedades.getProperty("database.user");
			String password = propiedades.getProperty("database.password");
			
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			
			connection.setAutoCommit(false);
		}catch (ClassNotFoundException | SQLException | IOException e) {
			
		}
		
		return connection;
	}
	
	public void disconnect(Connection connection) throws SQLException {
		
		if (null != connection) {
			try {
				connection.close();
				connection = null;
			}catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	public static String leerFichero(String pathname) throws SQLException, IOException {
		String fichero = "";
		
		BufferedReader br = null;
		FileReader fr = null;
		
		try {
			fr = new FileReader(pathname);
			br = new BufferedReader(fr);
			
			String linea = "";
			while((linea = br.readLine()) != null) {
				fichero = fichero + linea;
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return fichero;
	}
	
	public static void convertirStringToArrayJSON (String fichero) throws Exception {
		JSONArray jsonArray = new JSONArray(fichero);
		
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject explrObject = jsonArray.getJSONObject(i);
			
			String categoria = (String) ((JSONObject)jsonArray.get(i)).get("ESPACIOS NATURALES PROTEGIDOS EN CASTILLA-LA MANCHA (2018)");
			String provincia = (String) ((JSONObject)jsonArray.get(i)).get("");
			
			Parque parque = new Parque(categoria, provincia);
			parques.getListaParques().add(parque);
			//System.out.println(parques.getListaParques());
		}
	}

	public static String peticionHttpGet(String urlParaVisitar) throws Exception {
	   StringBuilder resultado = new StringBuilder();
	   URL url = new URL(urlParaVisitar);
	
	   HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
	   conexion.setRequestMethod("GET");
	   BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
	   String linea;
	   while ((linea = rd.readLine()) != null) {
	     resultado.append("\r\n" + linea);
	   }
	   rd.close();
	   return resultado.toString();
	}
	
	public List<EspacioNatural> getParquesOfProvincia(Connection connection, String provincia) throws ClassNotFoundException, SQLException{
		
		List<EspacioNatural> listaParques = new ArrayList<EspacioNatural>();
		
		String consultaSQL = "SELECT CATEGORIA, PROVINCIA FROM ESPACIOS_NATURALES WHERE PROVINCIA = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		
		try {
			connection = createConnection();
			
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setString(1, provincia);
			resultset = preparedStatement.executeQuery();
			
			EspacioNatural parques;
			while(resultset.next()) {
				parques = new EspacioNatural();
				parques.setCategoria(resultset.getString("CATEGORIA"));
				parques.setProvincia(resultset.getString("PROVINCIA"));;
				
				listaParques.add(parques);
			}
			
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			//cerramos todos los resources
			if (null != resultset) {
				try {
					resultset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return listaParques;
	} 
	
	
	public long insertPaques(Connection connection, EspacioNatural espacioNatural) throws SQLException {
		long idParque = 0;

		// Consulta SQL
		String consultaSQL = "INSERT INTO ESPACIOS_NATURALES (CATEGORIA, PROVINCIA) VALUES (?,?)";
		
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			preparedStatement = connection.prepareStatement(consultaSQL, Statement.RETURN_GENERATED_KEYS);
			
			//añado los valores para cada uno de los parametros
			preparedStatement.setString(1, espacioNatural.getCategoria());
			preparedStatement.setString(2, espacioNatural.getProvincia());
			if(preparedStatement.executeUpdate() > 0) {
				generatedKeys = preparedStatement.getGeneratedKeys();
				if(generatedKeys.next()) {
					idParque = generatedKeys.getLong(1);
				}
			}
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
			throw e;
		} finally {
			//cerramos todos los resources
			if (null != generatedKeys) {
				try {
					generatedKeys.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return idParque;
	}
	
	public static void inicializarParques(Parques parques, Connection connection, PaquesNaturalesHelper helper)
			throws SQLException {
		for(int i = 0; i < parques.getListaParques().size(); i++) {
		    EspacioNatural espacioNatural = new EspacioNatural();
		    espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
		    espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
			helper.insertPaques(connection, espacioNatural);
		}
	}
	
	 public void valueChanged(ListSelectionEvent e) {
	       int selectedIndex = this.vista.listaParques.getSelectedIndex();
	       this.vista.lblCategoriaMostrar.setText(parques.getListaParques().get(selectedIndex).getCategoria());
	       
	    }
	
	public void inicializarArrayLists() {	
		modelo.removeAllElements();
		for (int i = 0; i < parques.getListaParques().size(); i++) {
			modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
		}
		vista.listaParques.setModel(modelo);
		
		
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == this.vista.btnIniciar) {
			Connection connection;
			PaquesNaturalesHelper helper = new PaquesNaturalesHelper();
		
			 String url = "https://datosabiertos.castillalamancha.es/sites/datosabiertos.castillalamancha.es/files/espacios%20naturales.json";
		     String json = "";
		     String aJson = "";
		
		     FileWriter fw = null;
		
		    try {
		    	json = peticionHttpGet(url);
		
		       	File file = new File("parques_naturales.json");
		       	fw =  new FileWriter(file);
		       	fw.write(json);
		       	fw.flush();
		                
			   	aJson = leerFichero("parques_naturales.json");
		       	convertirStringToArrayJSON(aJson);
		            
		       	connection = helper.createConnection();
		             
		        inicializarParques(parques, connection, helper);
		        inicializarArrayLists();
			} catch (Exception s) {
				s.printStackTrace();
			}
		    this.vista.panelPortada.setVisible(false);
		    this.vista.panelInicio.setVisible(true);
		}
		
		if(e.getSource() == this.vista.rdbtnPorProvincia) {
			if(this.vista.rdbtnPorProvincia.isSelected()) {
				this.vista.cBFiltros.addItem("");
				this.vista.cBFiltros.addItem("Ciudad Real");
				this.vista.cBFiltros.addItem("Cuenca");
				this.vista.cBFiltros.addItem("Albacete");
				this.vista.cBFiltros.addItem("Toledo");
				this.vista.cBFiltros.addItem("Guadalajara");
			}else {
				this.vista.cBFiltros.removeAllItems();
			}
		}
		
		if(e.getSource() == this.vista.rdbtnPorTipo) {
			if(this.vista.rdbtnPorTipo.isSelected()) {
				this.vista.cBFiltros.addItem("");
				this.vista.cBFiltros.addItem("Reserva Natural");
				this.vista.cBFiltros.addItem("Parque Natural");
				this.vista.cBFiltros.addItem("Parque Nacional");
				this.vista.cBFiltros.addItem("Microrreserva");
				this.vista.cBFiltros.addItem("Reserva Fluvial");
				this.vista.cBFiltros.addItem("Monumento Natural");
				this.vista.cBFiltros.addItem("Paisaje Protegido");
			}else {
				this.vista.cBFiltros.removeAllItems();
				inicializarArrayLists();
			}
		}
		
		if(e.getSource() == this.vista.cBFiltros) {
			int num = this.vista.cBFiltros.getSelectedIndex();
			
			
			if(this.vista.cBFiltros.getSelectedItem().equals("Ciudad Real")) {
				
				modelo.removeAllElements();
				for (int i = 0; i < parques.getListaParques().size(); i++) {
					if(parques.getListaParques().get(i).getProvincia().contains("Ciudad Real")) {
						modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					}
				}
				vista.listaParques.setModel(modelo);
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Cuenca")) {
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Albacete")) {
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Toledo")) {
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Guadalajara")) {
				
			}
			
		}
		
		this.vista.listaParques.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent me) {
				 if (me.getClickCount() == 1) {
		               JList target = (JList)me.getSource();
		               int index = target.locationToIndex(me.getPoint());
		               if (index >= 0) {
		                  //Object item = target.getModel().getElementAt(index);
		                  vista.lblCategoriaMostrar.setText(parques.getListaParques().get(index).getCategoria());
		                  vista.lblProvinciaMostrar.setText(parques.getListaParques().get(index).getProvincia());
		                  
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Ciudad Real")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadReal.png"));
			      				ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
			      				vista.lblFotoParque.setIcon(ico);
			              }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Toledo")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Toledo.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Cuenca")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Cuenca.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Guadalajara")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Guadalajara.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Albacete")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Albacete.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Ciudad Real y Cuenca")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyCuenca.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Albacete y Ciudad Real")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyAlbacete.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Cuenca y Guadalajara")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/GudalajarayCuenca.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		                  
		                  if(parques.getListaParques().get(index).getProvincia().equals("Ciudad Real y Toledo")) {
		                	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyToledo.png"));
				      			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				      			vista.lblFotoParque.setIcon(ico);
		                  }
		               }
		            }
		         }
		});
		
	}
	
	

}
