package persistencias;

public class EspacioNatural {
	
	private long idEspacio;
	private String categoria, provincia, nombre, superficie, fechaDeclaracion;
	
	public EspacioNatural() {
		
	}

	public EspacioNatural(long idEspacio, String categoria, String provincia, String nombre, String superficie, String fechaDeclaracion) {
		
		this.idEspacio = idEspacio;
		this.categoria = categoria;
		this.provincia = provincia;
		this.nombre = nombre;
		this.superficie = superficie;
		this.fechaDeclaracion = fechaDeclaracion;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSuperficie() {
		return superficie;
	}

	public void setSuperficie(String superficie) {
		this.superficie = superficie;
	}

	public String getFechaDeclaracion() {
		return fechaDeclaracion;
	}

	public void setFechaDeclaracion(String fechaDeclaracion) {
		this.fechaDeclaracion = fechaDeclaracion;
	}

	@Override
	public String toString() {
		return "EspacioNatural [idEspacio=" + idEspacio + ", categoria=" + categoria + ", provincia=" + provincia
				+ ", nombre=" + nombre + ", superficie=" + superficie + ", fechaDeclaracion=" + fechaDeclaracion + "]";
	}
}
