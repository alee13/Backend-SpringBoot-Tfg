package bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import gestor.GestorUsuarios;
import gestor.GestorCarta;
import gestor.GestorMenus;
import gestor.GestorReservas;

// Para que SpringBoot gestione esta clase automaticamente
@Component
public class TelegramBot extends TelegramLongPollingBot {

    // Valor del token que telegram nos ha asignado
    @Value("${telegram.bot.username}")
    private String nombreBot;

    private final GestorUsuarios gestorUsuarios;
    private final GestorCarta gestorCarta;
    private final GestorMenus gestorMenus;
    private final GestorReservas gestorReservas;

    public TelegramBot(@Value("${telegram.bot.token}") String token,
            GestorUsuarios gestorUsuarios,
            GestorCarta gestorCarta,
            GestorMenus gestorMenus,
            GestorReservas gestorReservas) {
        super(token);
        this.gestorUsuarios = gestorUsuarios;
        this.gestorCarta = gestorCarta;
        this.gestorMenus = gestorMenus;
        this.gestorReservas = gestorReservas;
        this.gestorMenus.setSender(this);
    }

    // Telegram nos va a pedir el nombre del bot
    @Override
    public String getBotUsername() {
        return nombreBot;
    }

    // Cada vez que escribimos algo al bot se ejecuta este método
    @Override
    public void onUpdateReceived(Update update) {

        // el usuario ha escrito texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            String textoRecibido = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            gestorUsuarios.manejar(chatId, textoRecibido, gestorMenus);
        }

        // el usuario ha pulsado un botón
        if (update.hasCallbackQuery()) {
            String botonPulsado = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            switch (botonPulsado) {
                case "ACCION_PEDIR" ->
                    gestorCarta.mostrarCategorias(chatId, messageId, this);
                case "ACCION_RESERVAR" ->
                    gestorReservas.mostrarCalendario(chatId, messageId, this);
                case "ACCION_VOLVER" ->
                    gestorMenus.editarMenuPrincipal(chatId, messageId, "¿Qué quieres hacer?");
                case "FINALIZAR_PEDIDO" ->
                    gestorCarta.finalizarPedido(chatId, messageId, gestorUsuarios.getNombre(chatId), this);
                case "CANCELAR_PEDIDO" ->
                    gestorCarta.cancelarPedido(chatId, messageId, this);
                case "ACCION_FINALIZAR" ->
                    gestorMenus.enviarDespedida(chatId, messageId, gestorUsuarios.getNombre(chatId));
                case "RES_VOLVER_TIPO" ->
                    gestorReservas.mostrarCalendario(chatId, messageId, this);
                case "RES_VOLVER_HORA" ->
                    gestorReservas.volverAHoras(chatId, messageId, this);
                default ->
                    manejarCallbackConPrefijo(botonPulsado, chatId, messageId);
            }
        }
    }

    // Manejar los callbacks que empiezan por un prefijo conocido CATEGORIA // PRODUCTO // RESERVA
    private void manejarCallbackConPrefijo(String botonPulsado, Long chatId, Integer messageId) {
        if (botonPulsado.startsWith("CAT_")) {
            gestorCarta.mostrarProductos(chatId, messageId, botonPulsado, this);
        } else if (botonPulsado.startsWith("PROD_")) {
            gestorCarta.agregarAlCarrito(chatId, messageId, botonPulsado, this);
        } else if (botonPulsado.startsWith("RES_DIA_")) {
            gestorReservas.seleccionarDia(chatId, messageId, botonPulsado, this);
        } else if (botonPulsado.startsWith("RES_TIPO_")) {
            gestorReservas.seleccionarTipo(chatId, messageId, botonPulsado, this);
        } else if (botonPulsado.startsWith("RES_HORA_")) {
            gestorReservas.seleccionarHora(chatId, messageId, botonPulsado, this);
        } else if (botonPulsado.startsWith("RES_COM_")) {
            gestorReservas.seleccionarComensales(chatId, messageId, botonPulsado, this);
        }
    }
}
