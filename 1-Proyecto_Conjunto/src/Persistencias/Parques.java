package Persistencias;

import java.util.ArrayList;

public class Parques {
	
	private ArrayList<Parque> listaparque = null;
	
	public Parques() {
		
	}

	public ArrayList<Parque> getListaParques() {
		if(listaparque == null) {
			listaparque = new ArrayList<Parque>();
		}
		return listaparque;
	}

	public void setListaParques(ArrayList<Parque> listaparque) {
		this.listaparque = listaparque;
	}

	@Override
	public String toString() {
		return "Parques [listaparque=" + listaparque + "]";
	}
}
