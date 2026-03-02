package models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fecha_hora")
	private LocalDateTime fechaHora;

	private String estado;
	// IMPORTANTEEE!!!!!!!!!!!
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	// Permite que el usuario se cree o actualice automáticamente al realizar un
	// pedido, evitando errores si el cliente no existe previamente en la base de
	// datos.
	@JoinColumn(name = "usuario_email")
	private Usuario user;
}