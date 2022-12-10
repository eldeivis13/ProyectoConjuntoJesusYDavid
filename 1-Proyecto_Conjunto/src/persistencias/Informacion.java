package persistencias;

public class Informacion {
	
	private String nombre, superficie, fechaDeclaracion;

	public Informacion(String nombre, String superficie, String fechaDeclaracion) {
		super();
		this.nombre = nombre;
		this.superficie = superficie;
		this.fechaDeclaracion = fechaDeclaracion;
	}
	
	public Informacion() {
		
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
