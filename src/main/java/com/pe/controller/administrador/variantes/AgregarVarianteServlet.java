package com.pe.controller.administrador.variantes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.html.TamanioHtml;
import com.pe.model.html.VarianteHtml;
import com.pe.model.service.ProductoService;
import com.pe.model.service.TamanioService;
import com.pe.model.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/variante/agregar")
public class AgregarVarianteServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(AgregarVarianteServlet.class);
    private final VarianteService varianteService;
    private final ProductoService productoService;
    private final TamanioService tamanioService;

    public AgregarVarianteServlet() throws SQLException {
        this.productoService = new ProductoService();
        this.tamanioService = new TamanioService();
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_variante.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            tamanioService.cargarTamanios();

            TreeSet<Tamanio> tamaniosActivos = tamanioService.cargarTamaniosActivos();

            String opcionesTamanios = TamanioHtml.generarOpcionesTamanios(tamaniosActivos);

            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/agregar_variante.html")));

            html = html.replace("${tamaniosOptions}", opcionesTamanios);

            String productoNombre = (String) request.getAttribute("productoNombre");
            String mensajeError = (String) request.getAttribute("mensajeError");
            if (productoNombre != null) {
                html = html.replace("${selectedProduct}", productoNombre);
                html = html.replace("${productoId}", request.getAttribute("productoId").toString());
            } else if (mensajeError != null) {
                html = html.replace("${selectedProduct}", mensajeError);
            } else {
                html = html.replace("${selectedProduct}", "");
            }

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            logger.error("Error al cargar tamaños: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar tamaños: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mensaje;
        String redirigirUrl = "/variante/agregar";

        try {
            String codigo = request.getParameter("codigo");
            String tamanioId = request.getParameter("tamanio");
            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            String imagen = request.getParameter("imagen");
            int stock = Integer.parseInt(request.getParameter("stock"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            String productoId = request.getParameter("productoId");

            Variante nuevaVariante = new Variante();
            nuevaVariante.setCodigo(codigo);
            nuevaVariante.setIdTamanio(Integer.parseInt(tamanioId));
            nuevaVariante.setPrecio(precio);
            nuevaVariante.setImagen(imagen);
            nuevaVariante.setStock(stock);
            nuevaVariante.setCantidad(cantidad);
            nuevaVariante.setIdProducto(Integer.parseInt(productoId));

            varianteService.agregarVariante(nuevaVariante);
            mensaje = "Variante agregada exitosamente!";
            logger.info("Variante agregada: {}", nuevaVariante);
        } catch (SQLException e) {
            mensaje = "Error al agregar la variante: " + e.getMessage();
            logger.error("Error al agregar la variante: {}", e.getMessage(), e);
        } catch (NumberFormatException e) {
            mensaje = "Error en los datos proporcionados: " + e.getMessage();
            logger.error("Error en el formato de los datos: {}", e.getMessage(), e);
        }

        // Generar el script de alerta
        String scriptAlerta = VarianteHtml.generarMensajeAlerta(mensaje, redirigirUrl);

        // Enviar el script de alerta como respuesta
        response.setContentType("text/html");
        response.getWriter().write(scriptAlerta);
    }
}
