package CodigoJson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import Persistencias.Parque;
import Persistencias.Parques;

public class CodigoJSON {
	
	static Parques parques = new Parques();
	
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
	
	public static void main(String[] args) {
	
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
	                
	        } catch (Exception e) {
	                e.printStackTrace();
	        }
	
	}

}


