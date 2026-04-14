package gestor;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import db.UsuarioRepository;
import models.Usuario;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GestorUsuarios {

	private static final Logger log = LoggerFactory.getLogger(GestorUsuarios.class);

	private final UsuarioRepository usuarioRepository;

	// Para almacenar el estado de la conversación del usuario
	private final Map<Long, String> estadoConversacion = new ConcurrentHashMap<>();
	private final Map<Long, String> nombreTemporal = new ConcurrentHashMap<>();

	public GestorUsuarios(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public boolean manejar(Long chatId, String textoRecibido, GestorMenus gestorMenus) {

		if (textoRecibido.equals("/start")) {
			estadoConversacion.put(chatId, "ESPERANDO_NOMBRE");
			gestorMenus.enviarMensaje(chatId, "¡Hola! ¿Cuál es tu nombre?");
			return true;

		} else if ("ESPERANDO_NOMBRE".equals(estadoConversacion.get(chatId))) {
			// Guardar el nombre temporalmente y pedimos el email
			nombreTemporal.put(chatId, textoRecibido);
			estadoConversacion.put(chatId, "ESPERANDO_EMAIL");
			gestorMenus.enviarMensaje(chatId, "¡Encantado, " + textoRecibido + "! ¿Cuál es tu email?");
			return true;

		} else if ("ESPERANDO_EMAIL".equals(estadoConversacion.get(chatId))) {
			// Validacon correcta del mail
			if (!esEmailValido(textoRecibido)) {
				gestorMenus.enviarMensaje(chatId,
						"❌ El email introducido no es válido. Por favor escribe un email correcto. Ejemplo: nombre@correo.com");
				return true;
			}

			// Almacenar datos en la base de datos
			String nombre = nombreTemporal.get(chatId);
			String email = textoRecibido;

			guardarUsuario(chatId, nombre, email);

			// Limpiamos mapas para liberar memoria
			estadoConversacion.remove(chatId);
			nombreTemporal.remove(chatId);

			gestorMenus.enviarMenuPrincipal(chatId,
					"¡Perfecto, " + nombre + "! Ya estás registrado. ¿Qué quieres hacer?");
			return true;
		}

		return false;
	}

	// METODO: recuperar nombre del usuario desde la base de datos usando el chatId
	public String getNombre(Long chatId) {
		return usuarioRepository.findByChatId(chatId).map(Usuario::getNombre).orElse("usuario");
	}

	private void guardarUsuario(Long chatId, String nombre, String email) {
		log.info("[guardarUsuario] chatId={}, nombre={}, email={}", chatId, nombre, email);
		Optional<Usuario> existente = usuarioRepository.findById(email);

		if (existente.isEmpty()) {
			// Si el chatId ya pertenece a otro email, liberar esa asociación para evitar
			// duplicados
			usuarioRepository.findByChatId(chatId).ifPresent(previo -> {
				log.info("[guardarUsuario] Liberando chatId={} del email previo={}", chatId, previo.getEmail());
				previo.setChatId(null);
				usuarioRepository.save(previo);
			});

			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setEmail(email);
			nuevoUsuario.setNombre(nombre);
			nuevoUsuario.setFechaRegistro(LocalDate.now());
			nuevoUsuario.setChatId(chatId);
			log.info("[guardarUsuario] Guardando nuevo usuario con chatId={}", nuevoUsuario.getChatId());
			usuarioRepository.save(nuevoUsuario);

		} else {
			Usuario usuario = existente.get();
			log.info("[guardarUsuario] Usuario ya existe. chatId previo={}, nuevo chatId={}", usuario.getChatId(),
					chatId);
			usuario.setChatId(chatId);
			usuarioRepository.save(usuario);
		}
	}

	// METODO: validar que el texto tiene formato de email correcto
	private boolean esEmailValido(String email) {
		return email.contains("@") && email.contains(".") && email.indexOf("@") < email.lastIndexOf(".");
	}
}