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

import org.json.JSONArray;
import org.json.JSONObject;

import persistencias.EspacioNatural;
import persistencias.Informacion;
import persistencias.InformacionEspacioNatural;
import persistencias.Informaciones;
import persistencias.Parque;
import persistencias.Parques;
import vista.vistaProyecto;

public class controladorProyecto implements ActionListener{
	
	vistaProyecto vista = new vistaProyecto();
	DefaultListModel<String> modelo = new DefaultListModel<>();
	ArrayList<Parques> parque = new ArrayList<Parques>();
	List<EspacioNatural> listaProvincias = new ArrayList<EspacioNatural>();
	static Parques parques = new Parques();
	ArrayList<Informaciones> listInformaciones = new ArrayList<Informaciones>();
	static Informaciones informaciones = new Informaciones();
	boolean btnProvincia = false, btnTipo = false, btnSinfiltro = true;
	String aJson, infoJson;
	EspacioNatural espacioNatural = new EspacioNatural();

	public controladorProyecto(vistaProyecto vista) {
		this.vista = vista;
		this.vista.rdbtnPorProvincia.addActionListener(this);
		this.vista.rdbtnPorTipo.addActionListener(this);
		this.vista.cBFiltros.addActionListener(this);
		this.vista.btnIniciar.addActionListener(this);
		
	}
	
	public static Connection createConnection() throws ClassNotFoundException, SQLException {
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
	
	public void generarFichero() throws Exception, IOException {
		
		String url = "https://datosabiertos.castillalamancha.es/sites/datosabiertos.castillalamancha.es/files/espacios%20naturales.json";
		String json = "";

		FileWriter fw = null;

		try {
			json = peticionHttpGet(url);

			File file = new File("parques_naturales.json");
			fw =  new FileWriter(file);
			fw.write(json);
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String leerFichero(String pathname) throws SQLException, IOException {
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
		}
	}
	
	public static void convertirStringInfoToArrayJSON (String fichero) throws Exception {
		JSONArray jsonArray = new JSONArray(fichero);
		
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject explrObject = jsonArray.getJSONObject(i);
			
			String nombre = (String) ((JSONObject)jsonArray.get(i)).get("NOMBRE");
			String superficie = (String) ((JSONObject)jsonArray.get(i)).get("SUPERFICIE");
			String fecha = (String) ((JSONObject)jsonArray.get(i)).get("FECHA DECLARACION");
			
			Informacion informacion = new Informacion(nombre, superficie, fecha);
			informaciones.getListaInformacion().add(informacion);
		}
	}

	public String peticionHttpGet(String urlParaVisitar) throws Exception {
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
	
	public List<EspacioNatural> getParquesOfCategoria(Connection connection, String categoria) throws ClassNotFoundException, SQLException{
		
		String consultaSQL = "SELECT CATEGORIA, PROVINCIA FROM ESPACIOS_NATURALES WHERE PROVINCIA LIKE ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		
		try {
			connection = createConnection();
			
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setString(1, categoria);
			resultset = preparedStatement.executeQuery();
			
			EspacioNatural parques;
			while(resultset.next()) {
				parques = new EspacioNatural();
				parques.setCategoria(resultset.getString("CATEGORIA"));
				parques.setProvincia(resultset.getString("PROVINCIA"));;
				
				listaProvincias.add(parques);
			}
			
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
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
		
		return listaProvincias;
	} 
	
	public List<EspacioNatural> getParquesOfProvincia(Connection connection, String provincia) throws ClassNotFoundException, SQLException{
		
		String consultaSQL = "SELECT CATEGORIA, PROVINCIA FROM ESPACIOS_NATURALES WHERE PROVINCIA LIKE ?";
		
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
				
				listaProvincias.add(parques);
			}
			
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
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
		
		return listaProvincias;
	} 
	
	public static boolean existeParque(Connection connection, int parque) throws Exception {
		boolean exite = false;
		
		String consultaSQL = "SELECT PROVINCIA FROM ESPACIOS_NATURALES WHERE ID_ESPACIO = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		
		try {
			connection = createConnection();
			
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setInt(1, parque);
			resultset = preparedStatement.executeQuery();
			
			if(resultset.next()) {
				exite = true;
			}
			
			
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
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
		
		return exite;
	}

	
	
	public static long insertPaques(Connection connection, EspacioNatural espacioNatural) throws SQLException {
		
		long idParque = 0;

		String consultaSQL = "INSERT INTO ESPACIOS_NATURALES (CATEGORIA, PROVINCIA) VALUES (?,?)";
		
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			preparedStatement = connection.prepareStatement(consultaSQL, Statement.RETURN_GENERATED_KEYS);
			
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
	
	
	public static boolean existeInformacion(Connection connection, int idParque) throws Exception {
		
		boolean exite = false;
		
		String consultaSQL = "SELECT NOMBRE FROM INFORMACION WHERE ID_ESPACIO = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		
		try {
			connection = createConnection();
			
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setInt(1, idParque);
			resultset = preparedStatement.executeQuery();
			
			if(resultset.next()) {
				exite = true;
			}
			
			
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
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
		
		return exite;
	}
	
	public static String insertInformacion(Connection connection, InformacionEspacioNatural informacionEN) throws SQLException {
		
		String nombre = null;

		// Consulta SQL
		String consultaSQL = "INSERT INTO INFORMACION (NOMBRE, SUPERFICIE_DECLARADA_TOTAL, FECHA_DECLARACION, ID_ESPACIO) VALUES (?,?,?,?)";
		
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(consultaSQL);
			
			preparedStatement.setString(1, informacionEN.getNombre());
			preparedStatement.setString(2, informacionEN.getSuperficie());
			preparedStatement.setString(3, informacionEN.getFechaDeclaracion());
			preparedStatement.setLong(4, informacionEN.getIdEspacio());
			if(preparedStatement.executeUpdate() > 0) {
				nombre = informacionEN.getNombre();
			}
			
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
			throw e;
		} finally {
			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nombre;
	}
	

	public static List<InformacionEspacioNatural> getIdParque (Connection connection) throws Exception {
		
		List<InformacionEspacioNatural> listaInfoEspacios = new ArrayList<>();
		
		String consultaSQL = "SELECT ID_ESPACIO FROM ESPACIOS_NATURALES"; 
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(consultaSQL);
			
			InformacionEspacioNatural info;
			while(resultSet.next()) {
				info = new InformacionEspacioNatural();
				info.setIdEspacio(resultSet.getLong("ID_ESPACIO"));
				
				listaInfoEspacios.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return listaInfoEspacios;
	}
	
	public static void inicializarParques(Connection connection, Parques parques) throws Exception {
		
		boolean existeParque = false;
		for(int i = 1; i < parques.getListaParques().size(); i++) {
		    EspacioNatural espacioNatural = new EspacioNatural();
		    espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
		    espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
		    
		    existeParque = existeParque(connection, i);
		    if(!existeParque) {
		    	insertPaques(connection, espacioNatural);
		    }
		}
	}
	
	public static void inicializarInformacion(Connection connection, Informaciones informaciones) throws Exception {
		
		boolean existeInfo = false;
		
		List<InformacionEspacioNatural> listInfoEspacios = new ArrayList<>();
	    listInfoEspacios = getIdParque(connection);
		
		for(int i = 0; i < informaciones.getListaInformacion().size(); i++) {
		    InformacionEspacioNatural informacionEN = new InformacionEspacioNatural();
		    informacionEN.setNombre(informaciones.getListaInformacion().get(i).getNombre());
		    informacionEN.setSuperficie(informaciones.getListaInformacion().get(i).getSuperficie());
		    informacionEN.setFechaDeclaracion(informaciones.getListaInformacion().get(i).getFechaDeclaracion());
		    informacionEN.setIdEspacio(listInfoEspacios.get(i).getIdEspacio());
		    
		    existeInfo = existeInformacion(connection, i);
		    if(!existeInfo) {
		    	insertInformacion(connection, informacionEN);
		    }
		}
	}
	
	public void inicializarListaParques() {	
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
				
		     try {  
		    	generarFichero();
			   	aJson = leerFichero("parques_naturales.json");
		       	convertirStringToArrayJSON(aJson);
		       	
		       	infoJson = leerFichero("informacion_parques.json");
		        convertirStringInfoToArrayJSON(infoJson);
		        
		       	connection = createConnection();
		             
		        inicializarParques(connection, parques);
		        
		        inicializarInformacion(connection, informaciones);
		        
		        inicializarListaParques();
			} catch (Exception s) {
				s.printStackTrace();
			}
		    this.vista.panelPortada.setVisible(false);
		    this.vista.panelInicio.setVisible(true);
		}
		
		if(e.getSource() == this.vista.rdbtnPorProvincia) {
			if(this.vista.rdbtnPorProvincia.isSelected()) {
				this.btnSinfiltro = false;
				this.btnProvincia = true;
				this.vista.cBFiltros.addItem("");
				this.vista.cBFiltros.addItem("Ciudad Real");
				this.vista.cBFiltros.addItem("Cuenca");
				this.vista.cBFiltros.addItem("Albacete");
				this.vista.cBFiltros.addItem("Toledo");
				this.vista.cBFiltros.addItem("Guadalajara");
			}else {
				this.btnSinfiltro = true;
				this.vista.cBFiltros.removeAllItems();
				inicializarListaParques();
			}
		}
		
		if(e.getSource() == this.vista.rdbtnPorTipo) {
			if(this.vista.rdbtnPorTipo.isSelected()) {
				this.btnSinfiltro = false;
				this.btnTipo = true;
				this.vista.cBFiltros.addItem("");
				this.vista.cBFiltros.addItem("Reserva Natural");
				this.vista.cBFiltros.addItem("Parque Natural");
				this.vista.cBFiltros.addItem("Parque Nacional");
				this.vista.cBFiltros.addItem("Microrreserva");
				this.vista.cBFiltros.addItem("Reserva Fluvial");
				this.vista.cBFiltros.addItem("Monumento Natural");
				this.vista.cBFiltros.addItem("Paisaje Protegido");
			}else {
				this.btnSinfiltro = true;
				this.vista.cBFiltros.removeAllItems();
				inicializarListaParques();
			}
		}
		
		if(e.getSource() == this.vista.cBFiltros) {
			Connection connection = null;
			int num = this.vista.cBFiltros.getSelectedIndex();
			
			
			if(this.vista.cBFiltros.getSelectedItem().equals("Ciudad Real")) {
				try {
					connection = createConnection();
					
					List<EspacioNatural> listaProvincias =  getParquesOfProvincia(connection, "%Ciu%");
					
					modelo.removeAllElements();
					for (int i = 0; i < listaProvincias.size(); i++) {
						modelo.addElement("Categoria: " + listaProvincias.get(i).getCategoria() + " / Provincia: " + listaProvincias.get(i).getProvincia());
					}
					vista.listaParques.setModel(modelo);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Cuenca")) {
				
				try {
					connection = createConnection();
					
					List<EspacioNatural> listaProvincias =  getParquesOfProvincia(connection, "%Cue%");
					
					modelo.removeAllElements();
					for (int i = 0; i < listaProvincias.size(); i++) {
						modelo.addElement("Categoria: " + listaProvincias.get(i).getCategoria() + " / Provincia: " + listaProvincias.get(i).getProvincia());
					}
					vista.listaParques.setModel(modelo);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Albacete")) {
				
				try {
					connection = createConnection();
					
					List<EspacioNatural> listaProvincias =  getParquesOfProvincia(connection, "%Alb%");
					
					modelo.removeAllElements();
					for (int i = 0; i < listaProvincias.size(); i++) {
						modelo.addElement("Categoria: " + listaProvincias.get(i).getCategoria() + " / Provincia: " + listaProvincias.get(i).getProvincia());
					}
					vista.listaParques.setModel(modelo);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Toledo")) {
				
				try {
					connection = createConnection();
					
					List<EspacioNatural> listaProvincias =  getParquesOfProvincia(connection, "%Tol%");
					
					modelo.removeAllElements();
					for (int i = 0; i < listaProvincias.size(); i++) {
						modelo.addElement("Categoria: " + listaProvincias.get(i).getCategoria() + " / Provincia: " + listaProvincias.get(i).getProvincia());
					}
					vista.listaParques.setModel(modelo);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}else if(this.vista.cBFiltros.getSelectedItem().equals("Guadalajara")) {
				
				try {
					connection = createConnection();
					
					List<EspacioNatural> listaProvincias =  getParquesOfProvincia(connection, "%Gua%");
					
					modelo.removeAllElements();
					for (int i = 0; i < listaProvincias.size(); i++) {
						modelo.addElement("Categoria: " + listaProvincias.get(i).getCategoria() + " / Provincia: " + listaProvincias.get(i).getProvincia());
					}
					vista.listaParques.setModel(modelo);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
			
		}
		
		this.vista.listaParques.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent me) {
				 if (me.getClickCount() == 1) {
		               int index = vista.listaParques.locationToIndex(me.getPoint());
		               if(btnSinfiltro == true) {
		            	   vista.lblCategoriaMostrar.setText(parques.getListaParques().get(index).getCategoria());
			               vista.lblProvinciaMostrar.setText(parques.getListaParques().get(index).getProvincia());
			               
			               getFotosSinFiltro(index);
			               
		               }else if(btnProvincia == true) {
		            	   vista.lblCategoriaMostrar.setText(listaProvincias.get(index).getCategoria());
			               vista.lblProvinciaMostrar.setText(listaProvincias.get(index).getProvincia());
			               getFotosProvincias(index);
			               
		               }else if(btnTipo == true) {
		            	   
		               }
		            }
		         }

			public void getFotosSinFiltro(int index) {
				if(parques.getListaParques().get(index).getProvincia().contains("Ciudad Real")) {
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

			public void getFotosProvincias(int index) {
				
				if(listaProvincias.get(index).getProvincia().contains("Ciudad Real")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadReal.png"));
							ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
							vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Toledo")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Toledo.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Cuenca")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Cuenca.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Guadalajara")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Guadalajara.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Albacete")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Albacete.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Ciudad Real y Cuenca")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyCuenca.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Albacete y Ciudad Real")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyAlbacete.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Cuenca y Guadalajara")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/GudalajarayCuenca.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaProvincias.get(index).getProvincia().equals("Ciudad Real y Toledo")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyToledo.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
			}
		});
		
	}
}
