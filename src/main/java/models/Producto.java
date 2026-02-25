package models;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class Producto {
	private Long id;
	private String nombre;
	private String descripcion;
	private Float precio;
	private Boolean disponible;
	
	public Producto(Long id, String nombre, String descripcion, Float precio, Boolean disponible) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.disponible = disponible;
	}
	
	
}
