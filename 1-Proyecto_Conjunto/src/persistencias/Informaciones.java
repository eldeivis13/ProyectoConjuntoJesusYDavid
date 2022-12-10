package persistencias;

import java.util.ArrayList;

public class Informaciones {
	
private ArrayList<Informacion> listaInformacion = null;
	
	public Informaciones() {
		
	}

	public ArrayList<Informacion> getListaInformacion() {
		if(listaInformacion == null) {
			listaInformacion = new ArrayList<Informacion>();
		}
		return listaInformacion;
	}

	public void setListaParques(ArrayList<Informacion> listaInformacion) {
		this.listaInformacion = listaInformacion;
	}

	@Override
	public String toString() {
		return "Parques [listaparque=" + listaInformacion + "]";
	}
}
