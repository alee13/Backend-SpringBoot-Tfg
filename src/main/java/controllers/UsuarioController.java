package controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import db.UsuarioRepository;
import models.Usuario;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@PostMapping
	public Usuario createUsuario(@RequestBody Usuario newUsuario) {
		return usuarioRepository.save(newUsuario);
	}

	@GetMapping("{email}")
	public Usuario getUsuarioByEmail(@PathVariable String email) {
		return usuarioRepository.findById(email).orElse(null);
	}

}
