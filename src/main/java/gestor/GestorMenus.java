package gestor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class GestorMenus {

	private AbsSender sender;

	public void setSender(AbsSender sender) {
		this.sender = sender;
	}

	// METODO: enviar el menú principal con opciones de pedido, reserva y finalizar
	public void enviarMenuPrincipal(Long chatId, String texto) {
		SendMessage mensaje = new SendMessage();
		mensaje.setChatId(chatId.toString());
		mensaje.setText(texto);
		mensaje.setReplyMarkup(crearTecladoMenuPrincipal());

		try {
			sender.execute(mensaje);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// METODO: editar el mensaje existente con el menú principal
	public void editarMenuPrincipal(Long chatId, Integer messageId, String texto) {
		EditMessageText edicion = new EditMessageText();
		edicion.setChatId(chatId.toString());
		edicion.setMessageId(messageId);
		edicion.setText(texto);
		edicion.setReplyMarkup(crearTecladoMenuPrincipal());

		try {
			sender.execute(edicion);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// METODO: construir el teclado del menú principal
	private InlineKeyboardMarkup crearTecladoMenuPrincipal() {
		InlineKeyboardButton btnPedido = new InlineKeyboardButton();
		btnPedido.setText("🍴🍗 Hacer un Pedido");
		// Asignar una etiqueta a btnPedido
		btnPedido.setCallbackData("ACCION_PEDIR");

		InlineKeyboardButton btnReserva = new InlineKeyboardButton();
		btnReserva.setText("📆 Reservar Mesa");
		btnReserva.setCallbackData("ACCION_RESERVAR");

		InlineKeyboardButton btnFinalizar = new InlineKeyboardButton();
		btnFinalizar.setText("》 Finalizar");
		btnFinalizar.setCallbackData("ACCION_FINALIZAR");

		List<InlineKeyboardButton> fila1 = new ArrayList<>();
		fila1.add(btnPedido);
		fila1.add(btnReserva);

		List<InlineKeyboardButton> fila2 = new ArrayList<>();
		fila2.add(btnFinalizar);

		List<List<InlineKeyboardButton>> filas = new ArrayList<>();
		filas.add(fila1);
		filas.add(fila2);

		// Asignar las filas al markup
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		markup.setKeyboard(filas);
		return markup;
	}

	// METODO: editar el mensaje actual con la despedida y eliminar botones
	public void enviarDespedida(Long chatId, Integer messageId, String nombre) {
		// Teclado vacío para eliminar los botones del mensaje
		InlineKeyboardMarkup sinBotones = new InlineKeyboardMarkup();
		sinBotones.setKeyboard(new ArrayList<>());

		EditMessageText edicion = new EditMessageText();
		edicion.setChatId(chatId.toString());
		edicion.setMessageId(messageId);
		edicion.setText("¡Hasta pronto, " + nombre + "! Ha sido un placer atenderte. "
				+ "Si quieres volver a usar el menú escribe /start ✌️");
		edicion.setReplyMarkup(sinBotones);

		try {
			sender.execute(edicion);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// METODO: enviar mensajes al chat
	public void enviarMensaje(Long chatId, String texto) {
		SendMessage mensaje = new SendMessage();
		mensaje.setChatId(chatId.toString());
		mensaje.setText(texto);

		try {
			sender.execute(mensaje);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
