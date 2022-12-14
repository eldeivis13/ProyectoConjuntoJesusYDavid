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
import vista.VistaProyecto;

public class ControladorProyecto implements ActionListener{
	
	VistaProyecto vista = new VistaProyecto();
	DefaultListModel<String> modelo = new DefaultListModel<>();
	ArrayList<Parques> litsaParque = new ArrayList<Parques>();
	Parque parque = new Parque();
	List<EspacioNatural> listaProvincias = new ArrayList<EspacioNatural>();
	List<EspacioNatural> listaCategorias = new ArrayList<EspacioNatural>();
	static Parques parques = new Parques();
	ArrayList<Informaciones> listInformaciones = new ArrayList<Informaciones>();
	static Informaciones informaciones = new Informaciones();
	boolean btnProvincia = false, btnTipo = false, btnSinfiltro = true;
	String aJson, infoJson;
	EspacioNatural espacioNatural = new EspacioNatural();
	InformacionEspacioNatural informacionEn = new InformacionEspacioNatural();

	public ControladorProyecto(VistaProyecto vista) {
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
	
	public static long convertirStringToArrayJSON (String fichero1) throws Exception {
		
		JSONArray jsonArray1 = new JSONArray(fichero1);
		
		long posicion = 0;
		
		for(int i = 0; i < jsonArray1.length(); i++) {
			JSONObject explrObjectParque = jsonArray1.getJSONObject(i);
			String categoria = (String) (explrObjectParque).get("ESPACIOS NATURALES PROTEGIDOS EN CASTILLA-LA MANCHA (2018)");
			String provincia = (String) (explrObjectParque).get("");
			
			posicion = i;
			Parque parque = new Parque(categoria, provincia);
			//parques.getListaParques().add(parque);
		}
		return posicion;
	}

	public static long convertirStringToArrayJsonInformacion(String fichero2) {
		
		JSONArray jsonArray2 = new JSONArray(fichero2);
		
		long posicion2 = 0;
		
		for(int j = 0; j < jsonArray2.length(); j++) {
			JSONObject explrObjectInfo = jsonArray2.getJSONObject(j);
			
			String nombre = (String) (explrObjectInfo).getString("NOMBRE");
			String superficie = (String) (explrObjectInfo).getString("SUPERFICIE");
			String fecha = (String) (explrObjectInfo).getString("FECHA DECLARACION");
			
			posicion2 = j;
			Parque parque = new Parque(nombre, superficie, fecha);
		}
		return posicion2;
	}
	
	public void relacionarFicheros (long posicion, long posicion2) throws Exception{
		
		String aJson = leerFichero("parques_naturales.json");
	   	String infoJson = leerFichero("informacion_parques.json");
		
		convertirStringToArrayJSON(aJson);
		convertirStringToArrayJsonInformacion(infoJson);
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
	
	public static boolean existeParque(Connection connection, long idParque) throws Exception {
		boolean existe = false;
		
		String consultaSQL = "SELECT PROVINCIA FROM ESPACIOS_NATURALES WHERE ID_ESPACIO = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		
		try {
			connection = createConnection();
			
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setLong(1, idParque);
			resultset = preparedStatement.executeQuery();
			
			if(resultset.next()) {
				existe = true;
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
		
		return existe;
	}

	public static long insertPaques(Connection connection, EspacioNatural espacioNatural) throws SQLException {
		
		long idParque = 0;

		String consultaSQL = "INSERT INTO ESPACIOS_NATURALES (CATEGORIA, PROVINCIA, NOMBRE, SUPERFICIE_DECLARADA_TOTAL, FECHA_DECLARACION) VALUES (?,?,?,?,?)";
		
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			preparedStatement = connection.prepareStatement(consultaSQL, Statement.RETURN_GENERATED_KEYS);
			
			preparedStatement.setString(1, espacioNatural.getCategoria());
			preparedStatement.setString(2, espacioNatural.getProvincia());
			preparedStatement.setString(3, espacioNatural.getNombre());
			preparedStatement.setString(4, espacioNatural.getSuperficie());
			preparedStatement.setString(5, espacioNatural.getFechaDeclaracion());
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
	
	/*public static boolean existeInformacion(Connection connection, long idParque) throws Exception {
		
		boolean exite = false;
		
		String consultaSQL = "SELECT NOMBRE FROM INFORMACION WHERE ID_ESPACIO = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		
		try {
			connection = createConnection();
			
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setLong(1, idParque);
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
	}*/

	/*public static List<InformacionEspacioNatural> getIdParque (Connection connection) throws Exception {
		
		List<InformacionEspacioNatural> listaInfoEspacios = new ArrayList<InformacionEspacioNatural>();
		
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
	}*/
	
	public static void inicializarParques(Connection connection, Parques parques) throws Exception {
		
		boolean existeParque = false;
		for(int i = 1; i < parques.getListaParques().size(); i++) {
		    EspacioNatural espacioNatural = new EspacioNatural();
		    espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
		    espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
		    espacioNatural.setNombre(parques.getListaParques().get(i).getNombre());
		    espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
		    espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
		    
		    existeParque = existeParque(connection, i);
		    if(!existeParque) {
		    	insertPaques(connection, espacioNatural);
		    }
		}
	}
	
	/*public static void inicializarInformacion(Connection connection, Informaciones informaciones) throws Exception {
		
		boolean existeInfo = false;
		
		List<InformacionEspacioNatural> listInfo = new ArrayList<>();
		listInfo = getIdParque(connection);
		for(int i = 0; i < informaciones.getListaInformacion().size(); i++) {
		    InformacionEspacioNatural informacionEN = new InformacionEspacioNatural();
		    informacionEN.setNombre(informaciones.getListaInformacion().get(i).getNombre());
		    informacionEN.setSuperficie(informaciones.getListaInformacion().get(i).getSuperficie());
		    informacionEN.setFechaDeclaracion(informaciones.getListaInformacion().get(i).getFechaDeclaracion());
		    informacionEN.setIdEspacio(listInfo.get(i).getIdEspacio());
		    
		    existeInfo = existeInformacion(connection, informacionEN.getIdEspacio());
		    if(!existeInfo) {
		    	insertInformacion(connection, informacionEN);
		    }
		}
	}*/
	
	public void inicializarListaParques() {	
		modelo.removeAllElements();
		for (int i = 0; i < parques.getListaParques().size(); i++) {
			modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
		}
		vista.listaParques.setModel(modelo);
		
	}
	
	/*public void insertEspacioInformacion(Connection connection, long idParque, String nombre) throws Exception{
		String consultaSQL = "INSERT INTO ESPACIO_INFORMACION (ID_ESPACIO, NOMBRE) VALUES (?,?)";
		
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(consultaSQL);
			preparedStatement.setLong(1, idParque);
			preparedStatement.setString(2, nombre);
			preparedStatement.executeUpdate();
			
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
	}*/
	
	/*public void asignarEspacioInformacion(Connection connection, Parques parques, Informaciones informaciones) throws Exception{
		
		long idParque = insertPaques(connection, espacioNatural);
		
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
		
	    String nombre = insertInformacion(connection, informacionEn);
	    boolean existeInfo = false;
		
		List<InformacionEspacioNatural> listInfo = new ArrayList<>();
	    listInfo = getIdParque(connection);
		
		for(int i = 0; i < informaciones.getListaInformacion().size(); i++) {
			InformacionEspacioNatural informacionEn = new InformacionEspacioNatural();
		    informacionEn.setNombre(informaciones.getListaInformacion().get(i).getNombre());
		    informacionEn.setSuperficie(informaciones.getListaInformacion().get(i).getSuperficie());
		    informacionEn.setFechaDeclaracion(informaciones.getListaInformacion().get(i).getFechaDeclaracion());
		    informacionEn.setIdEspacio(listInfo.get(i).getIdEspacio());
		    
		    existeInfo = existeInformacion(connection, i);
		    if(!existeInfo) {
		    	insertInformacion(connection, informacionEn);
		    }
		}
		insertEspacioInformacion(connection, idParque, nombre);
	}*/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == this.vista.btnIniciar) {
			Connection connection = null;
				
		     try {  
		    	connection = createConnection();
		    	generarFichero();
			   	//aJson = leerFichero("parques_naturales.json");
			   	//infoJson = leerFichero("informacion_parques.json");
		       	//convertirStringToArrayJSON(aJson, infoJson);
		       	
		        //convertirStringInfoToArrayJSON(aJson, infoJson);
		        
		       	//asignarEspacioInformacion(connection, parques, informaciones);
		        inicializarParques(connection, parques);
		        //inicializarInformacion(connection, informaciones);
		       	
		        inicializarListaParques();
		        
		        connection.commit();
			} catch (Exception s) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
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
				this.btnProvincia = false;
				this.vista.cBFiltros.removeAllItems();
				//inicializarListaParques();
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
				this.btnTipo = false;
				this.vista.cBFiltros.removeAllItems();
				//inicializarListaParques();
			}
		}
		
		if(e.getSource() == this.vista.cBFiltros) {
			if(btnProvincia == true) {
				filtrarProvincia();
			}else if(btnTipo == true)  {
				filtrarCategoria();
			}
			
		}
		
		
		this.vista.listaParques.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent me) {
				 if (me.getClickCount() == 1) {
		               int index = vista.listaParques.locationToIndex(me.getPoint());
		               if(btnSinfiltro == true) {
		            	   vista.lblCategoriaMostrar.setText(parques.getListaParques().get(index).getCategoria());
			               vista.lblProvinciaMostrar.setText(parques.getListaParques().get(index).getProvincia());
			               vista.lblNombreMostrar.setText(parques.getListaParques().get(index).getNombre());
			               vista.lblSDTMostrar.setText(parques.getListaParques().get(index).getSuperficie());
			               vista.lblFechaDeclaracionMostrar.setText(parques.getListaParques().get(index).getFechaDeclarada());
			               
			               getFotosSinFiltro(index);
			               
		               }else if(btnProvincia == true) {
		            	   vista.lblCategoriaMostrar.setText(listaProvincias.get(index).getCategoria());
			               vista.lblProvinciaMostrar.setText(listaProvincias.get(index).getProvincia());
			               vista.lblNombreMostrar.setText(listaProvincias.get(index).getNombre());
			               vista.lblSDTMostrar.setText(listaProvincias.get(index).getSuperficie());
			               vista.lblFechaDeclaracionMostrar.setText(listaProvincias.get(index).getFechaDeclaracion());
			               getFotosProvincias(index);
			               
		               }else if(btnTipo == true) {
		            	   vista.lblCategoriaMostrar.setText(listaCategorias.get(index).getCategoria());
			               vista.lblProvinciaMostrar.setText(listaCategorias.get(index).getProvincia());
			               vista.lblNombreMostrar.setText(listaCategorias.get(index).getNombre());
			               vista.lblSDTMostrar.setText(listaCategorias.get(index).getSuperficie());
			               vista.lblFechaDeclaracionMostrar.setText(listaCategorias.get(index).getFechaDeclaracion());
			               getFotosCategorias(index);
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
			
			public void getFotosCategorias(int index) {
				
				if(listaCategorias.get(index).getProvincia().contains("Ciudad Real")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadReal.png"));
							ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
							vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Toledo")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Toledo.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Cuenca")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Cuenca.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Guadalajara")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Guadalajara.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Albacete")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/Albacete.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Ciudad Real y Cuenca")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyCuenca.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Albacete y Ciudad Real")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/CiudadRealyAlbacete.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Cuenca y Guadalajara")) {
				    	  ImageIcon img = new ImageIcon(getClass().getResource("/resources/GudalajarayCuenca.png"));
				  			ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(vista.lblFotoParque.getWidth(), vista.lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
				  			vista.lblFotoParque.setIcon(ico);
				      }
				      
				      if(listaCategorias.get(index).getProvincia().equals("Ciudad Real y Toledo")) {
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

	public void filtrarCategoria() {
		listaCategorias.removeAll(listaCategorias);
		if(this.vista.cBFiltros.getSelectedItem().equals("Parque Natural")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Parque Natural")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					listaCategorias.add(espacioNatural);
			}
			//vista.listaParques.setModel(modelo);
		}
		vista.listaParques.setModel(modelo);
			
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Parque Nacional")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Parque Nacional")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getFechaDeclarada());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaCategorias.add(espacioNatural);
			}
				vista.listaParques.setModel(modelo);
			}
		
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Reserva Fluvial")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Reserva Fluvial")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getFechaDeclarada());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaCategorias.add(espacioNatural);
			}
			vista.listaParques.setModel(modelo);
			}
		
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Reserva Natural")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Reserva Natural")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getFechaDeclarada());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaCategorias.add(espacioNatural);
			}
			vista.listaParques.setModel(modelo);
			}
			
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Microrreserva")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Microrreserva")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getFechaDeclarada());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaCategorias.add(espacioNatural);
			}
			vista.listaParques.setModel(modelo);
			}
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Paisaje Protegido")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Paisaje Protegido")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getFechaDeclarada());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaCategorias.add(espacioNatural);
			}
			vista.listaParques.setModel(modelo);
			}
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Monumento Natural")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getCategoria().contains("Monumento Natural")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getFechaDeclarada());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaCategorias.add(espacioNatural);
			}
			vista.listaParques.setModel(modelo);
			}
		}
	}

	public void filtrarProvincia() {
		listaProvincias.removeAll(listaProvincias);
		if(this.vista.cBFiltros.getSelectedItem().equals("Ciudad Real")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getProvincia().contains("Ciudad Real")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getNombre());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaProvincias.add(espacioNatural);
			}
				vista.listaParques.setModel(modelo);
		}
			
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Albacete")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getProvincia().contains("Albacete")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getNombre());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaProvincias.add(espacioNatural);
			}
				vista.listaParques.setModel(modelo);
		}
		
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Cuenca")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getProvincia().contains("Cuenca")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getNombre());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaProvincias.add(espacioNatural);
			}
				vista.listaParques.setModel(modelo);
		}
		
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Guadalajara")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getProvincia().contains("Guadalajara")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getNombre());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaProvincias.add(espacioNatural);
			}
				vista.listaParques.setModel(modelo);
		}
			
		}else if(this.vista.cBFiltros.getSelectedItem().equals("Toledo")) {
			modelo.removeAllElements();
			EspacioNatural espacioNatural;
			for (int i = 0; i < parques.getListaParques().size(); i++) {
				if(parques.getListaParques().get(i).getProvincia().contains("Toledo")) {
					espacioNatural = new EspacioNatural();
					modelo.addElement("Categoria: " + parques.getListaParques().get(i).getCategoria() + " / Provincia: " + parques.getListaParques().get(i).getProvincia());
					espacioNatural.setCategoria(parques.getListaParques().get(i).getCategoria());
					espacioNatural.setProvincia(parques.getListaParques().get(i).getProvincia());
					espacioNatural.setNombre(parques.getListaParques().get(i).getNombre());
					espacioNatural.setSuperficie(parques.getListaParques().get(i).getSuperficie());
					espacioNatural.setFechaDeclaracion(parques.getListaParques().get(i).getFechaDeclarada());
					
					listaProvincias.add(espacioNatural);
			}
				vista.listaParques.setModel(modelo);
		}
		}
	}
}