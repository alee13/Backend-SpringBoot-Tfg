package gestor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import db.PedidoRepository;
import db.ProductRepository;
import db.UsuarioRepository;
import models.Pedido;
import models.Producto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class GestorCarta {

    // Almacena los productos de cada categoría de la carta
    private final Map<String, List<Producto>> cartaProductos = new ConcurrentHashMap<>();

    // Almacena los productos que cada usuario ha añadido al carrito
    private final Map<Long, List<Producto>> carritoUsuarios = new ConcurrentHashMap<>();

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductRepository productRepository;

    public GestorCarta(PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            ProductRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productRepository = productoRepository;
        cargarCarta(); // cargamos los productos al arrancar el bot
    }

    // METODO: cargar los productos de la carta desde la base de datos
    private void cargarCarta() {
        String[] categorias = {"CAT_ENTRANTES", "CAT_PRINCIPAL", "CAT_POSTRE", "CAT_BEBIDA", "CAT_MENU"};
        for (String categoria : categorias) {
            List<Producto> productos = productRepository.findByCategoria(categoria);
            cartaProductos.put(categoria, productos);
        }
    }

    // METODO: generar el resumen visual del carrito del usuario
    private String generarResumenCarrito(Long chatId) {
        List<Producto> carrito = carritoUsuarios.getOrDefault(chatId, new ArrayList<>());

        // Si el carrito está vacío no mostramos nada
        if (carrito.isEmpty()) {
            return "";
        }

        StringBuilder resumen = new StringBuilder("🛒 Tu pedido actual:\n");
        float total = 0;

        for (Producto p : carrito) {
            resumen.append("• ").append(p.getNombre()).append(" ..... ").append(String.format("%.2f", p.getPrecio()))
                    .append("€\n");
            total += p.getPrecio();
        }

        resumen.append("──────────────────\n");
        resumen.append("💰 Total: ").append(String.format("%.2f", total)).append("€\n");
        return resumen.toString();
    }

    // METODO: mostrar las categorías de la carta al usuario
    public void mostrarCategorias(Long chatId, Integer messageId, AbsSender sender) {
        InlineKeyboardButton btnEntrantes = new InlineKeyboardButton();
        btnEntrantes.setText("🌮 Entrantes");
        btnEntrantes.setCallbackData("CAT_ENTRANTES");

        InlineKeyboardButton btnPrincipal = new InlineKeyboardButton();
        btnPrincipal.setText("🍖 Plato Principal");
        btnPrincipal.setCallbackData("CAT_PRINCIPAL");

        InlineKeyboardButton btnPostre = new InlineKeyboardButton();
        btnPostre.setText("🍰 Postre");
        btnPostre.setCallbackData("CAT_POSTRE");

        InlineKeyboardButton btnBebida = new InlineKeyboardButton();
        btnBebida.setText("🍺 Bebida");
        btnBebida.setCallbackData("CAT_BEBIDA");

        InlineKeyboardButton btnMenu = new InlineKeyboardButton();
        btnMenu.setText("🍱 Menú");
        btnMenu.setCallbackData("CAT_MENU");

        InlineKeyboardButton btnVolver = new InlineKeyboardButton();
        btnVolver.setText("↩️ Volver");
        btnVolver.setCallbackData("ACCION_VOLVER");

        List<List<InlineKeyboardButton>> filas = new ArrayList<>();
        filas.add(List.of(btnEntrantes));
        filas.add(List.of(btnPrincipal));
        filas.add(List.of(btnPostre));
        filas.add(List.of(btnBebida));
        filas.add(List.of(btnMenu));

        // mostrar los botones de finalizar y cancelar si el carrito tiene algo
        if (!carritoUsuarios.getOrDefault(chatId, new ArrayList<>()).isEmpty()) {
            InlineKeyboardButton btnFinalizar = new InlineKeyboardButton();
            btnFinalizar.setText("✅ Finalizar pedido");
            btnFinalizar.setCallbackData("FINALIZAR_PEDIDO");

            InlineKeyboardButton btnCancelar = new InlineKeyboardButton();
            btnCancelar.setText("❌ Cancelar pedido");
            btnCancelar.setCallbackData("CANCELAR_PEDIDO");

            filas.add(List.of(btnFinalizar, btnCancelar));
        }

        filas.add(List.of(btnVolver));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(filas);

        // Mostrar el carrito encima de las categorías
        String textoCarrito = generarResumenCarrito(chatId);
        String texto = textoCarrito.isEmpty() ? "¿Qué te apetece pedir?"
                : textoCarrito + "\n¿Qué categoría quieres ver?";

        // Editar el mensaje existente en vez de enviar uno nuevo
        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText(texto);
        edicion.setReplyMarkup(markup);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: mostrar los productos de la categoría que ha pulsado el usuario
    public void mostrarProductos(Long chatId, Integer messageId, String categoria, AbsSender sender) {
        List<Producto> productos = cartaProductos.getOrDefault(categoria, new ArrayList<>());

        // Boton por categoría
        List<List<InlineKeyboardButton>> filas = new ArrayList<>();
        for (Producto p : productos) {
            InlineKeyboardButton btnProducto = new InlineKeyboardButton();
            // El botón muestra nombre y precio
            btnProducto.setText("➕ " + p.getNombre() + " - " + String.format("%.2f", p.getPrecio()) + "€");
            // El callbackData guarda el id del producto para identificarlo de forma segura
            btnProducto.setCallbackData("PROD_" + p.getId() + "_" + categoria);
            filas.add(List.of(btnProducto));
        }

        // Botón volver
        InlineKeyboardButton btnVolver = new InlineKeyboardButton();
        btnVolver.setText("↩️ Volver");
        btnVolver.setCallbackData("ACCION_PEDIR");
        filas.add(List.of(btnVolver));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(filas);

        // Mostrar carrito encima de productos
        String textoCarrito = generarResumenCarrito(chatId);
        String texto = textoCarrito.isEmpty() ? "Selecciona un producto:" : textoCarrito + "\nSelecciona un producto:";

        // Editar el mensaje existente
        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText(texto);
        edicion.setReplyMarkup(markup);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: añadir producto al carrito del usuario
    public void agregarAlCarrito(Long chatId, Integer messageId, String dato, AbsSender sender) {
        // Quitamos el prefijo "PROD_" → nos queda "1_CAT_PRINCIPAL"
        String sinPrefijo = dato.substring("PROD_".length());

        // Buscar el primer _ para separar el id de la categoría
        int separador = sinPrefijo.indexOf("_");
        Long idProducto = Long.parseLong(sinPrefijo.substring(0, separador));
        String categoria = sinPrefijo.substring(separador + 1);

        Producto producto = buscarProductoPorId(idProducto);
        if (producto != null) {
            carritoUsuarios.computeIfAbsent(chatId, k -> new ArrayList<>()).add(producto);
        }

        // Nos quedamos en la misma categoría
        mostrarProductos(chatId, messageId, categoria, sender);
    }

    // METODO: buscar un producto en el catálogo por su id
    private Producto buscarProductoPorId(Long id) {
        for (List<Producto> lista : cartaProductos.values()) {
            for (Producto p : lista) {
                if (p.getId().equals(id)) {
                    return p;
                }
            }
        }
        return null;
    }

    // METODO: confirmar el pedido, guardar en BD y mostrar mensaje personalizado
    public void finalizarPedido(Long chatId, Integer messageId, String nombre, AbsSender sender) {
        List<Producto> carrito = carritoUsuarios.getOrDefault(chatId, new ArrayList<>());

        // Calcular el tiempo de espera: 10 minutos base + 3 mins por producto
        int tiempoEspera = 10 + (carrito.size() * 3);

        // Guardar resumen antes de vaciar el carrito
        String resumenFinal = generarResumenCarrito(chatId);

        // Guardar pedido en base de datos
        usuarioRepository.findByChatId(chatId).ifPresent(usuario -> {
            Pedido pedido = new Pedido();
            pedido.setFechaHora(LocalDateTime.now());
            pedido.setEstado("Pendiente");
            pedido.setUser(usuario);
            pedidoRepository.save(pedido);
        });

        // Vaciar el carrito
        carritoUsuarios.put(chatId, new ArrayList<>());

        // Teclado vacío para eliminar los botones
        InlineKeyboardMarkup sinBotones = new InlineKeyboardMarkup();
        sinBotones.setKeyboard(new ArrayList<>());

        // Confirmación del pedido
        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText(
                "✅ ¡Pedido confirmado, " + nombre + "!\n\n" + resumenFinal + "\n" + "⏱️ Tiempo estimado de espera: "
                + tiempoEspera + " minutos.\n\n" + "Escribe /start cuando quieras volver al menú.");
        edicion.setReplyMarkup(sinBotones);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: cancelar el pedido del usuario y mostrar mensaje de cancelación
    public void cancelarPedido(Long chatId, Integer messageId, AbsSender sender) {
        // Vaciar el carrito
        carritoUsuarios.put(chatId, new ArrayList<>());

        // Teclado vacío para eliminar los botones
        InlineKeyboardMarkup sinBotones = new InlineKeyboardMarkup();
        sinBotones.setKeyboard(new ArrayList<>());

        // Mensaje de cancelación
        EditMessageText edicion = new EditMessageText();
        edicion.setChatId(chatId.toString());
        edicion.setMessageId(messageId);
        edicion.setText("❌ Su pedido ha sido cancelado. Si desea volver a realizar un pedido pulse /start.");
        edicion.setReplyMarkup(sinBotones);

        try {
            sender.execute(edicion);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // METODO: vacíar el carrito del usuario
    public void vaciarCarrito(Long chatId) {
        carritoUsuarios.put(chatId, new ArrayList<>());
    }
}
