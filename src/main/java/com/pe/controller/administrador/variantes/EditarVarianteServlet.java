package com.pe.controller.administrador.variantes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.html.VarianteHtml;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.TamanioService;
import com.pe.model.administrador.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/variante/editar")
public class EditarVarianteServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditarVarianteServlet.class);
    private final VarianteService varianteService;
    private final ProductoService productoService;
    private final TamanioService tamanioService;

    public EditarVarianteServlet() throws SQLException {
        this.varianteService = new VarianteService();
        this.productoService = new ProductoService();
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_variante.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                varianteService.cargarVariantes();
                int id = Integer.parseInt(idParam);
                Variante variante = varianteService.obtenerVariantePorId(id);
                if (variante != null) {
                    String nombreProducto = productoService.obtenerNombreProductoPorId(variante.getIdProducto());
                    String nombreTamanio = tamanioService.obtenerNombreTamanioPorId(variante.getIdTamanio());

                    tamanioService.cargarTamanios();
                    TreeSet<Tamanio> tamaniosActivos = tamanioService.cargarTamaniosActivos();

                    String html = VarianteHtml.generarHtmlEdicionVariante(variante, nombreProducto, tamaniosActivos);

                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    logger.warn("Variante no encontrada: ID {}", id);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Variante no encontrada");
                }
            } catch (NumberFormatException e) {
                logger.error("ID de variante inválido: {}", idParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de variante inválido");
            } catch (SQLException e) {
                logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            logger.warn("ID de variante no proporcionado");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de variante no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String codigo = request.getParameter("codigo");
        String tamanio = request.getParameter("tamanio");
        String productoIdParam = request.getParameter("productoId");
        String precioParam = request.getParameter("precio");
        String imagen = request.getParameter("imagen");
        String stockParam = request.getParameter("stock");
        String cantidadParam = request.getParameter("cantidad");

        String mensaje;
        String redirigirUrl = String.format("http://localhost:8081/variante/producto?id=%s", productoIdParam);

        try {
            int id = Integer.parseInt(idParam);
            int idTamanio = Integer.parseInt(tamanio);
            int idProducto = Integer.parseInt(productoIdParam);
            BigDecimal precio = new BigDecimal(precioParam);
            int stock = Integer.parseInt(stockParam);
            int cantidad = Integer.parseInt(cantidadParam);

            varianteService.actualizarVariante(id, codigo, idTamanio, idProducto, precio, imagen, stock, cantidad);
            varianteService.cargarVariantes();
            mensaje = "Variante actualizada exitosamente!";
            logger.info("Variante actualizada: ID = {}, Código = {}, Tamaño ID = {}, Producto ID = {}, Precio = {}, Stock = {}, Cantidad = {}",
                    id, codigo, idTamanio, idProducto, precio, stock, cantidad);
        } catch (Exception e) {
            mensaje = "Error al actualizar la variante: " + e.getMessage();
            logger.error("Error inesperado: {}", e.getMessage(), e);
            String scriptAlerta = VarianteHtml.generarMensajeAlerta(mensaje, "/variante/editar?id=" + idParam);
            response.setContentType("text/html");
            response.getWriter().write(scriptAlerta);
            return;
        }
        response.sendRedirect(redirigirUrl);
    }
}
