package controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import models.Pedido;
import db.PedidoRepository;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;

	@GetMapping
	public List<Pedido> getPedidos() {
		return pedidoRepository.findAll();
	}

	@GetMapping("/{idPedido}")
	public Pedido getPedidoById(@PathVariable("idPedido") Long idPedido) {
		if (pedidoRepository.existsById(idPedido)) {
			return pedidoRepository.findById(idPedido).get();
		} else {
			throw new RuntimeException("Error: El pedido con ID " + idPedido + " no existe en la base de datos.");
		}
	}

	@PostMapping
	public Pedido createPedido(@RequestBody Pedido newPedido) {
		return pedidoRepository.save(newPedido);
	}

	@PutMapping("/{idPedido}")
	public Pedido updatePedido(@PathVariable("idPedido")Long idPedido, @RequestBody Pedido updatedPedido) {
		if (pedidoRepository.existsById(idPedido)) {
			updatedPedido.setId(idPedido);
			return pedidoRepository.save(updatedPedido);
		}
		return null;
	}

	@DeleteMapping("/{idPedido}")
	public String deletePedido(@PathVariable("idPedido")Long idPedido) {
		if (pedidoRepository.existsById(idPedido)) {
			pedidoRepository.deleteById(idPedido);
			return "Pedido " + idPedido + " eliminado";
		}
		return "El pedido no existe";
	}
}