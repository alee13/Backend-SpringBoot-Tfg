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

	@GetMapping
	public List<Reserva> getReservas() {
		return reservaRepository.findAll();
	}

	@GetMapping("/{idReserva}")
	public Reserva getReservaById(@PathVariable("idReserva")Long idReserva) {
		if (reservaRepository.existsById(idReserva)) {
			return reservaRepository.findById(idReserva).get();
		} else {
			throw new RuntimeException("Error: La reserva con ID " + idReserva + " no existe.");
		}
	}

	@PostMapping
	public Reserva createReserva(@RequestBody Reserva newReserva) {
		return reservaRepository.save(newReserva);
	}
}