package controllers;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.Usuario;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @PostMapping
    public Usuario createUsuario(@RequestBody Usuario newUsuario) {
        return newUsuario;
    }

    @GetMapping("{email}")
    public Usuario getUsuarioByEmail(@PathVariable String email) {
        Usuario usuario = new Usuario(email, 12345L, "Usuario Ejemplo", LocalDate.now());
        return usuario;
    }

}
