package controlador;

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

import org.json.JSONArray;
import org.json.JSONObject;

import CodigoJson.CodigoJSON;
import persistencias.EspacioNatural;
import persistencias.Parque;
import persistencias.Parques;

public class PaquesNaturalesHelper {
	
	static Parques parques = new Parques();
	
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
	
	
	public static void main(String [] args) throws Exception {
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
	                
	          //System.out.println(parques.getListaParques());
	        
	        
	        aJson = leerFichero("parques_naturales.json");
            convertirStringToArrayJSON(aJson);
            
            connection = helper.createConnection();
             
            inicializarParques(parques, connection, helper);
            
			} catch (Exception e) {
		        e.printStackTrace();
			}
		
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
	
	

}
