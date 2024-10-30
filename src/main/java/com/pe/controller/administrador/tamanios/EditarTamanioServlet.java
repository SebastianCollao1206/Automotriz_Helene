package com.pe.controller.administrador.tamanios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Tamanio;
import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                int id = Integer.parseInt(idParam);
                Tamanio tamanio = tamanioService.obtenerTamanioPorId(id);
                if (tamanio != null) {
                    String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_tamanio.html")));
                    html = html.replace("${tamanio.idtamanio}", String.valueOf(tamanio.getIdTamanio()));
                    html = html.replace("${tamanio.nombre}", tamanio.getUnidadMedida());
                    html = html.replace("${estado.activoSelected}", tamanio.getEstado().name().equals("Activo") ? "selected" : "");
                    html = html.replace("${estado.inactivoSelected}", tamanio.getEstado().name().equals("Inactivo") ? "selected" : "");

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
            } catch (SQLException e) {
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
            mensaje = "Tamaño actualizado exitosamente!";
            logger.info("Tamaño actualizado: ID = {}, Nombre = {}, Estado = {}", id, nombre, estado);
        } catch (IllegalArgumentException e) {
            mensaje = "Estado inválido: " + e.getMessage();
            logger.warn("Intento de actualizar tamaño con estado no válido: {}", estado);
        } catch (Exception e) {
            mensaje = "Error al actualizar el tamaño: " + e.getMessage();
            logger.error("Error al actualizar el tamaño: {}", e.getMessage(), e);
        }

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Redirigir a la lista de tamaños
        response.sendRedirect(redirigirUrl);
    }
}
