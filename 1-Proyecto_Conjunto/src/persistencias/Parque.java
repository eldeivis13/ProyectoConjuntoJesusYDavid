package persistencias;

public class Parque {
	
	private String categoria, provincia, nombre, superficie, fechaDeclarada;
	
	public Parque() {
		
	}

	public Parque(String categoria, String provincia) {
		
		this.categoria = categoria;
		this.provincia = provincia;
	}

	public Parque(String nombre, String superficie, String fechaDeclarada) {
		
		this.nombre = nombre;
		this.superficie = superficie;
		this.fechaDeclarada = fechaDeclarada;
	}

	public Parque(String categoria, String provincia, String nombre, String superficie, String fechaDeclarada) {
		
		this.categoria = categoria;
		this.provincia = provincia;
		this.nombre = nombre;
		this.superficie = superficie;
		this.fechaDeclarada = fechaDeclarada;
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

	public String getFechaDeclarada() {
		return fechaDeclarada;
	}

	public void setFechaDeclarada(String fechaDeclarada) {
		this.fechaDeclarada = fechaDeclarada;
	}

	@Override
	public String toString() {
		return "Parque [categoria=" + categoria + ", provincia=" + provincia + ", nombre=" + nombre + ", superficie="
				+ superficie + ", fechaDeclarada=" + fechaDeclarada + "]";
	}
}
