package gestor;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class PanelCalendario {

    // METODO: mostrar  calendario para que el usuario seleccione un día
    public void mostrarCalendario(Long chatId, Integer messageId, AbsSender sender) {
        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> filas = new ArrayList<>();

        // Fila con mes y año
        InlineKeyboardButton btnTitulo = new InlineKeyboardButton();
        btnTitulo.setText(traducirMes(hoy.getMonthValue()) + " " + hoy.getYear());
        btnTitulo.setCallbackData("IGNORE");
        filas.add(List.of(btnTitulo));

        // Fila con días semana
        List<InlineKeyboardButton> filaDias = new ArrayList<>();
        for (String dia : new String[]{"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"}) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(dia);
            btn.setCallbackData("IGNORE");
            filaDias.add(btn);
        }
        filas.add(filaDias);

        // Para calcular en qué día de la semana empieza el mes (1=Lunes, 7=Domingo)
        int primerDia = mesActual.atDay(1).getDayOfWeek().getValue();
        int totalDias = mesActual.lengthOfMonth();

        List<InlineKeyboardButton> filaActual = new ArrayList<>();

        // Rellenar los huecos vacíos antes del primer día
        for (int i = 1; i < primerDia; i++) {
            InlineKeyboardButton vacio = new InlineKeyboardButton();
            vacio.setText(" ");
            vacio.setCallbackData("IGNORE");
            filaActual.add(vacio);
        }

        // Botón por cada día del mes
        for (int dia = 1; dia <= totalDias; dia++) {
            InlineKeyboardButton btnDia = new InlineKeyboardButton();
            btnDia.setText(String.valueOf(dia));
            btnDia.setCallbackData("RES_DIA_" + mesActual.atDay(dia));
            filaActual.add(btnDia);

            // Cada fila tiene 7 días
            if (filaActual.size() == 7) {
                filas.add(filaActual);
                filaActual = new ArrayList<>();
            }
        }

        // Rellenar los huecos vacíos al final del mes
        if (!filaActual.isEmpty()) {
            while (filaActual.size() < 7) {
                InlineKeyboardButton vacio = new InlineKeyboardButton();
                vacio.setText(" ");
                vacio.setCallbackData("IGNORE");
                filaActual.add(vacio);
            }
            filas.add(filaActual);
        }

        // Botón de volver al menú principal
        InlineKeyboardButton btnVolver = new InlineKeyboardButton();
        btnVolver.setText("↩️ Volver");
        btnVolver.setCallbackData("ACCION_VOLVER");
        filas.add(List.of(btnVolver));

        markup.setKeyboard(filas);

        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText("📅 Selecciona el día de tu reserva:");
        edicion.setReplyMarkup(markup);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: mostrar los botones para elegir Comida o Cena
    public void mostrarTipoReserva(Long chatId, Integer messageId, AbsSender sender) {
        InlineKeyboardButton btnComida = new InlineKeyboardButton();
        btnComida.setText("🍽️ Comida");
        btnComida.setCallbackData("RES_TIPO_COMIDA");

        InlineKeyboardButton btnCena = new InlineKeyboardButton();
        btnCena.setText("🌙 Cena");
        btnCena.setCallbackData("RES_TIPO_CENA");

        InlineKeyboardButton btnVolver = new InlineKeyboardButton();
        btnVolver.setText("↩️ Volver");
        btnVolver.setCallbackData("ACCION_RESERVAR");

        List<List<InlineKeyboardButton>> filas = new ArrayList<>();
        filas.add(List.of(btnComida, btnCena));
        filas.add(List.of(btnVolver));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(filas);

        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText("¿Para qué momento quieres la reserva?");
        edicion.setReplyMarkup(markup);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: mostrar las horas disponibles según el tipo de reserva
    public void mostrarHoras(Long chatId, Integer messageId, String tipo, AbsSender sender) {
        String[] horas;

        if (tipo.equals("COMIDA")) {
            horas = new String[]{"13:00", "13:30", "14:00", "14:30", "15:00", "15:30"};
        } else {
            horas = new String[]{"20:30", "21:00", "21:30", "22:00", "22:30"};
        }

        List<List<InlineKeyboardButton>> filas = new ArrayList<>();

        // Metemos las horas de 3 en 3 por fila
        List<InlineKeyboardButton> filaActual = new ArrayList<>();
        for (String hora : horas) {
            InlineKeyboardButton btnHora = new InlineKeyboardButton();
            btnHora.setText(hora);
            btnHora.setCallbackData("RES_HORA_" + hora);
            filaActual.add(btnHora);

            if (filaActual.size() == 3) {
                filas.add(filaActual);
                filaActual = new ArrayList<>();
            }
        }
        if (!filaActual.isEmpty()) filas.add(filaActual);

        InlineKeyboardButton btnVolver = new InlineKeyboardButton();
        btnVolver.setText("↩️ Volver");
        btnVolver.setCallbackData("RES_VOLVER_TIPO");
        filas.add(List.of(btnVolver));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(filas);

        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText("¿A qué hora quieres la reserva?");
        edicion.setReplyMarkup(markup);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: mostrar los botones para elegir el número de comensales
    public void mostrarComensales(Long chatId, Integer messageId, AbsSender sender) {
        List<List<InlineKeyboardButton>> filas = new ArrayList<>();

        // Primera fila: 1, 2, 3
        List<InlineKeyboardButton> fila1 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(String.valueOf(i));
            btn.setCallbackData("RES_COM_" + i);
            fila1.add(btn);
        }
        filas.add(fila1);

        // Segunda fila: 4, 5, 6+
        List<InlineKeyboardButton> fila2 = new ArrayList<>();
        for (int i = 4; i <= 5; i++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(String.valueOf(i));
            btn.setCallbackData("RES_COM_" + i);
            fila2.add(btn);
        }
        InlineKeyboardButton btn6 = new InlineKeyboardButton();
        btn6.setText("6+");
        btn6.setCallbackData("RES_COM_6");
        fila2.add(btn6);
        filas.add(fila2);

        InlineKeyboardButton btnVolver = new InlineKeyboardButton();
        btnVolver.setText("↩️ Volver");
        btnVolver.setCallbackData("RES_VOLVER_HORA");
        filas.add(List.of(btnVolver));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(filas);

        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText("¿Para cuántas personas será la mesa?");
        edicion.setReplyMarkup(markup);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: formatear fecha en formato legible para el usuario
    public String formatearFecha(LocalDate fecha) {
        return fecha.getDayOfMonth() + " de " + traducirMes(fecha.getMonthValue()) + " de " + fecha.getYear();
    }

    // METODO: traducir número del mes a castellano
    public String traducirMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
}