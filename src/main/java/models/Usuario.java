package models;

import java.time.LocalDate;
import jakarta.persistence.Column;
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
	@Column(unique = true)
	private Long chatId;

	public Usuario() {
	}

	public Usuario(String email, String nombre, LocalDate fechaRegistro, Long chatId) {
		this.email = email;
		this.nombre = nombre;
		this.fechaRegistro = fechaRegistro;
		this.chatId = chatId;
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

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}
}