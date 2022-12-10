package Persistencias;

public class InformacionEspacioNatural {
	private int idEspacio;
	private String nombre, superficie, fechaDeclaracion;

	public InformacionEspacioNatural(int idEspacio, String nombre, String superficie, String fechaDeclaracion) {
		super();
		this.idEspacio = idEspacio;
		this.nombre = nombre;
		this.superficie = superficie;
		this.fechaDeclaracion = fechaDeclaracion;
	}
	
	public InformacionEspacioNatural() {
		
	}

	public int getIdEspacio() {
		return idEspacio;
	}

	public void setIdEspacio(int idEspacio) {
		this.idEspacio = idEspacio;
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
		return "Informacion [nombre=" + nombre + ", superficie=" + superficie + ", fechaDeclaracion=" + fechaDeclaracion
				+ "]";
	}
}
