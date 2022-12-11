package persistencias;

public class InformacionEspacioNatural {
	private long idEspacio;
	private String nombre, superficie, fechaDeclaracion;

	public InformacionEspacioNatural(String nombre, String superficie, String fechaDeclaracion, int idEspacio) {
		super();
		this.nombre = nombre;
		this.superficie = superficie;
		this.fechaDeclaracion = fechaDeclaracion;
		this.idEspacio = idEspacio;
	}
	
	public InformacionEspacioNatural() {
		
	}

	public long getIdEspacio() {
		return idEspacio;
	}

	public void setIdEspacio(long idEspacio2) {
		this.idEspacio = idEspacio2;
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
		return "InformacionEspacioNatural [idEspacio=" + idEspacio + ", nombre=" + nombre + ", superficie=" + superficie
				+ ", fechaDeclaracion=" + fechaDeclaracion + "]";
	}
}
