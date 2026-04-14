package models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "pedidos")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fecha_hora")
	private LocalDateTime fechaHora;

	private String estado;

	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "usuario_email")
	private Usuario user;

	public Pedido() {
	}

	public Pedido(Long id, LocalDateTime fechaHora, String estado, Usuario user) {
		this.id = id;
		this.fechaHora = fechaHora;
		this.estado = estado;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}
}