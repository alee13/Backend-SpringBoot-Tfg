package gestor;

import db.ReservaRepository;
import db.UsuarioRepository;
import models.Reserva;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GestorReservas {

	private final ReservaRepository reservaRepository;
	private final PanelCalendario calendario;
	private final UsuarioRepository usuarioRepository;

	// Almacena la reserva a medio completar de cada usuario
	private final Map<Long, Reserva> reservaEnProceso = new ConcurrentHashMap<>();

	public GestorReservas(ReservaRepository reservaRepository, PanelCalendario calendario,
			UsuarioRepository usuarioRepository) {
		this.reservaRepository = reservaRepository;
		this.calendario = calendario;
		this.usuarioRepository = usuarioRepository;
	}

	// METODO: Mostrar el calendario
	public void mostrarCalendario(Long chatId, Integer messageId, AbsSender sender) {
		calendario.mostrarCalendario(chatId, messageId, sender);
	}

	// METODO: Guardar la fecha seleccionada e iniciar la reserva en proceso
	public void seleccionarDia(Long chatId, Integer messageId, String dato, AbsSender sender) {
		// Extraer la fecha del callbackData "RES_DIA_"
		String fechaStr = dato.substring("RES_DIA_".length());
		LocalDate fecha = LocalDate.parse(fechaStr);

		// Crear una reserva nueva y guardar la fecha
		Reserva reserva = new Reserva();
		reserva.setFecha(fecha);
		reservaEnProceso.put(chatId, reserva);

		// Mostrar la siguiente pantalla
		calendario.mostrarTipoReserva(chatId, messageId, sender);
	}

	// METODO: Guardar el tipo y mostrar las horas disponibles
	public void seleccionarTipo(Long chatId, Integer messageId, String dato, AbsSender sender) {
		// Tipo cena o tipo comida
		String tipo = dato.substring("RES_TIPO_".length());
		// Mostrar las horas según el tipo elegido
		calendario.mostrarHoras(chatId, messageId, tipo, sender);
	}

	// METODO: Guardar la hora seleccionada y mostrar los comensales
	public void seleccionarHora(Long chatId, Integer messageId, String dato, AbsSender sender) {
		// Extraer la hora del callbackData "RES_HORA_13:00"
		String horaStr = dato.substring("RES_HORA_".length());
		LocalTime hora = LocalTime.parse(horaStr);

		// Guardar la hora en la reserva en proceso
		Reserva reserva = reservaEnProceso.get(chatId);
		if (reserva != null) {
			reserva.setHora(hora);
			reservaEnProceso.put(chatId, reserva);
		}

		// Mostrar la siguiente pantalla
		calendario.mostrarComensales(chatId, messageId, sender);
	}

	// METODO: Volver a mostrar las horas cuando el usuario pulsa volver desde
	// comensales
	public void volverAHoras(Long chatId, Integer messageId, AbsSender sender) {
		Reserva reserva = reservaEnProceso.get(chatId);

		// Determinar el tipo según la hora que tenía guardada
		if (reserva != null && reserva.getHora() != null) {
			String tipo = reserva.getHora().isBefore(LocalTime.of(16, 0)) ? "COMIDA" : "CENA";
			calendario.mostrarHoras(chatId, messageId, tipo, sender);
		} else {
			calendario.mostrarCalendario(chatId, messageId, sender);
		}
	}

	// METODO: Guardar los comensales, persistir la reserva y confirmar al usuario
	public void seleccionarComensales(Long chatId, Integer messageId, String dato, AbsSender sender) {
		// Extraer el número de comensales
		int comensales = Integer.parseInt(dato.substring("RES_COM_".length()));
		// Recuperar la reserva en proceso y completar los datos
		Reserva reserva = reservaEnProceso.get(chatId);
		if (reserva != null) {
			reserva.setComensales(comensales);
			reserva.setEstado("Pendiente");

			// Asociar el usuario a la reserva antes de guardar
			usuarioRepository.findByChatId(chatId).ifPresent(reserva::setUsuario);

			reservaRepository.save(reserva);
			reservaEnProceso.remove(chatId);

			// Teclado vacío para eliminar botones
			InlineKeyboardMarkup sinBotones = new InlineKeyboardMarkup();
			sinBotones.setKeyboard(new ArrayList<>());

			// Confirmar la reserva al usuario
			EditMessageText edicion = new EditMessageText();
			edicion.setChatId(chatId.toString());
			edicion.setMessageId(messageId);
			edicion.setText("✅ ¡Reserva pendiente de confirmación!\n\n" + "📅 Fecha: "
					+ calendario.formatearFecha(reserva.getFecha()) + "\n" + "🕐 Hora: " + reserva.getHora() + "\n"
					+ "👥 Comensales: " + comensales + "\n\n" + "El restaurante confirmará tu reserva en breves. ✌️\n\n"
					+ "Escribe /start para volver al menú.");
			edicion.setReplyMarkup(sinBotones);

			try {
				sender.execute(edicion);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}
}