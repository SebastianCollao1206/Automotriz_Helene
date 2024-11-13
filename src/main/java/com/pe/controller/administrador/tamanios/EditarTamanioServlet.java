package com.pe.controller.administrador.tamanios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.html.TamanioHtml;
import com.pe.model.administrador.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/tamanio/editar")
public class EditarTamanioServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditarTamanioServlet.class);
    private final TamanioService tamanioService;

    public EditarTamanioServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_tamanio.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                tamanioService.cargarTamanios();
                int id = Integer.parseInt(idParam);
                Tamanio tamanio = tamanioService.obtenerTamanioPorId(id);
                if (tamanio != null) {
                    String html = TamanioHtml.generarHtmlEdicionTamanio(tamanio);
                    request.setAttribute("content", html);
                    super.doGet(request, response);
                    logger.info("Se ha cargado el formulario de edición para el tamaño con ID: {}", id);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tamaño no encontrado");
                    logger.warn("Tamaño no encontrado para el ID: {}", id);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de tamaño inválido");
                logger.error("ID de tamaño inválido: {}", idParam, e);
            }
            catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
                logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
        }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de tamaño no proporcionado");
            logger.warn("ID de tamaño no proporcionado en la solicitud");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String estado = request.getParameter("estado");
        String mensaje;
        String redirigirUrl = "/tamanio/listar";

        try {
            int id = Integer.parseInt(idParam);
            tamanioService.actualizarTamanio(id, nombre, estado);
            tamanioService.cargarTamanios();
            mensaje = "Tamaño actualizado exitosamente!";
            logger.info("Tamaño actualizado: ID = {}, Nombre = {}, Estado = {}", id, nombre, estado);
        } catch (IllegalArgumentException e) {
            mensaje = "Estado inválido: " + e.getMessage();
            logger.warn("Intento de actualizar tamaño con estado no válido: {}", estado);
        } catch (Exception e) {
            mensaje = "Error al actualizar el tamaño: " + e.getMessage();
            logger.error("Error al actualizar el tamaño: {}", e.getMessage(), e);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        response.sendRedirect(redirigirUrl);
    }
}
