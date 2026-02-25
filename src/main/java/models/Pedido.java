package models;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pedido {
	
	private Long id;
	private LocalDateTime fechaHora;
	private String estado;

	public Pedido(Long id, LocalDateTime fechaHora, String estado) {
		this.id = id;
		this.fechaHora = fechaHora;
		this.estado = estado;
	}
	public Pedido() {
		
	}
	
}