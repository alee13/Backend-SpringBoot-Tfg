package main;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.Reserva;

@RestController
@RequestMapping("/api/reserva/")
public class ReservaController {
	
	@GetMapping("{idReserva}")
	public Reserva getReservaById(@PathVariable String idReserva) {
		Reserva reserva = new Reserva(idReserva, null, null, 0, "en proceso");
		return reserva;
	}
	
	@PostMapping
	public Reserva createReserva(@RequestBody Reserva newReserva) {
		return newReserva;
	}
}
