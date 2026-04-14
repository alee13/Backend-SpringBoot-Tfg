package db;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import models.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // METODO: buscar un usuario por su chatId de Telegram
    // Spring Data genera la consulta automáticamente a partir del nombre del método
    Optional<Usuario> findByChatId(Long chatId);

}
