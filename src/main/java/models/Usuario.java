package models;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usuario {
    private String email;
    private Long chatId;
    private String nombre;
    private LocalDate fechaRegistro;

    public Usuario(String email, Long chatId, String nombre, LocalDate fechaRegistro) {
        this.email = email;
        this.chatId = chatId;
        this.nombre = nombre;
        this.fechaRegistro = fechaRegistro;
    }

}
