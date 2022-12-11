package persistencias;

public class EspacioNatural {
	
	private long idEspacio;
	private String categoria, provincia;
	
	public EspacioNatural(String categoria, String provincia) {
		super();
		this.categoria = categoria;
		this.provincia = provincia;
	}
	
	public EspacioNatural() {
		
	}

	public long getIdEspacio() {
		return idEspacio;
	}

	public void setIdEspacio(long idEspacio) {
		this.idEspacio = idEspacio;
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
		return "EspacioNatural [idEspacio=" + idEspacio + ", categoria=" + categoria + ", provincia=" + provincia + "]";
	}
}
