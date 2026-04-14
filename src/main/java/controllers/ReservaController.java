package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import models.Reserva;
import db.ReservaRepository;
import java.util.List;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {

	@Autowired
	private ReservaRepository reservaRepository;

	// ENDPOINT: Listar todo
	@GetMapping
	public List<Reserva> getReservas() {
		return reservaRepository.findAll();
	}

	// ENDPOINT: Buscar por ID
	@GetMapping("/{idReserva}")
	public Reserva getReservaById(@PathVariable("idReserva") Long idReserva) {
		if (reservaRepository.existsById(idReserva)) {
			return reservaRepository.findById(idReserva).get();
		} else {
			throw new RuntimeException("Error: La reserva con ID " + idReserva + " no existe.");
		}
	}

	// ENDPOINT: Crear nuevo
	@PostMapping
	public Reserva createReserva(@RequestBody Reserva newReserva) {
		return reservaRepository.save(newReserva);
	}

	// ENDPOINT: Actualizar existente
	@PutMapping("/{idReserva}")
	public Reserva updateReserva(@PathVariable("idReserva") Long idReserva, @RequestBody Reserva updatedReserva) {
		if (reservaRepository.existsById(idReserva)) {
			updatedReserva.setId(idReserva);
			return reservaRepository.save(updatedReserva);
		} else {
			throw new RuntimeException("Error: La reserva con ID " + idReserva + " no existe.");
		}
	}

	// ENDPOINT: Eliminar
	@DeleteMapping("/{idReserva}")
	public String deleteReserva(@PathVariable("idReserva") Long idReserva) {
		if (reservaRepository.existsById(idReserva)) {
			reservaRepository.deleteById(idReserva);
			return "Reserva " + idReserva + " eliminada";
		} else {
			return "Error: La reserva con ID " + idReserva + " no existe.";
		}
	}
}