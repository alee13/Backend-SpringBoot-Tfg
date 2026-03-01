package models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
	@Id
	private String email;
	private String nombre;
	private LocalDate fechaRegistro;

	public Usuario(String email, String nombre, LocalDate fechaRegistro) {
		this.email = email;
		this.nombre = nombre;
		this.fechaRegistro = fechaRegistro;
	}

	public Usuario() {
		this.email = "";
		this.nombre = "";
		this.fechaRegistro = null;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
