package controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import models.Pedido;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

	@GetMapping
	public List<Pedido> getPedidos() {
		List<Pedido> listaPedidos = new ArrayList<>();
		Pedido pedido = new Pedido(1L, LocalDateTime.now(), "PENDIENTE");
		listaPedidos.add(pedido);
		return listaPedidos;
	}

	@GetMapping("{idPedido}")
	public Pedido getPedidoById(@PathVariable Long idPedido) {
		return new Pedido(idPedido, LocalDateTime.now(), "EN PREPARACION");
	}

	@PostMapping
	public Pedido createPedido(@RequestBody Pedido newPedido) {
		return newPedido;
	}

	@PutMapping("{idPedido}")
	public Pedido updatePedido(@PathVariable Long idPedido, @RequestBody Pedido updatedPedido) {
		updatedPedido.setId(idPedido);
		return updatedPedido;
	}

	@DeleteMapping("{idPedido}")
	public String deletePedido(@PathVariable Long idPedido) {
		return "Pedido eliminado";
	}

}