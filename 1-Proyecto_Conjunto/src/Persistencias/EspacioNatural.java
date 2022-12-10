package Persistencias;

public class EspacioNatural {
	
	private String categoria, provincia;
	
	public EspacioNatural(String categoria, String provincia) {
		super();
		this.categoria = categoria;
		this.provincia = provincia;
	}
	
	public EspacioNatural() {
		
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Override
	public String toString() {
		return "EspacioNatural [categoria=" + categoria + ", provincia=" + provincia + "]";
	}
	
	

}
