package com.pe.controller.administrador.notificaciones;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.model.administrador.service.MensajeService;
import com.pe.model.administrador.service.NotificacionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/marcar-notificaciones")
public class MarcarNotificacionesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MarcarNotificacionesServlet.class);
    private final NotificacionService notificacionService;

    public MarcarNotificacionesServlet() throws SQLException {
        this.notificacionService = new NotificacionService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Mensaje mensaje;

        if (usuario == null) {
            mensaje = new Mensaje("error", "Usuario no autenticado.", "/");
            MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
        } else {
            try {
                notificacionService.marcarTodasNotificacionesComoLeidas(usuario.getIdUsuario());

                int contadorNotificaciones = notificacionService.contarNotificacionesNoLeidas(usuario.getIdUsuario());

                MensajeService.mensajeJsonNoti(response, "success", "Todas las notificaciones marcadas como leídas.", null, contadorNotificaciones);
            } catch (SQLException e) {
                logger.error("Error al marcar notificaciones como leídas", e);
                mensaje = new Mensaje("error", "Error al marcar notificaciones.", null);
                MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
            }
        }
    }
}
