package com.pe.controller.administrador.comentarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.MensajeConfirmacion;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.html.ComentarioHtml;
import com.pe.model.administrador.service.ComentarioService;
import com.pe.model.administrador.service.MensajeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/comentario/estado")
public class EstadoComentarioServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(EstadoComentarioServlet.class);
    private final ComentarioService comentarioService;

    public EstadoComentarioServlet() throws SQLException {
        this.comentarioService = new ComentarioService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_comentario.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] ids = request.getParameterValues("ids");
        String accion = request.getParameter("accion");
        String redirigirUrl = "/comentario/listar";
        Mensaje mensaje;

        boolean activar = "activar".equals(accion);

        try {
            if (ids != null) {
                for (String idStr : ids) {
                    int id = Integer.parseInt(idStr);
                    comentarioService.cambiarEstadoComentario(id, activar);
                    logger.info("Comentario {}: ID = {}", activar ? "activado" : "desactivado", id);
                }
                mensaje = new Mensaje("success", "Comentarios actualizados exitosamente!", redirigirUrl);
            } else {
                mensaje = new Mensaje("error", "No se seleccionaron comentarios.", null);
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar comentarios: {}", e.getMessage(), e);
            mensaje = new Mensaje("error", "Error al actualizar comentarios: " + e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            mensaje = new Mensaje("error", "Error inesperado: " + e.getMessage(), null);
        }

        MensajeConfirmacion mensajeConfirmacion = ComentarioHtml.crearMensajeConfirmacion();

        MensajeService.mensajeConfirmacioJson(response, mensaje, mensajeConfirmacion);
    }
}
