package com.pe.controller.administrador.usuarios.recuperarContraseña;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.service.MensajeService;
import com.pe.model.administrador.service.UsuarioService;
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

@WebServlet("/recuperar/cambiar")
public class CambiarContrasenaServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CambiarContrasenaServlet.class);
    private UsuarioService usuarioService;

    public CambiarContrasenaServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/nueva_contrasena.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String correo = (String) session.getAttribute("correo");
        String nuevaContrasena = request.getParameter("nuevaContrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        Mensaje mensaje;

        try {
            if (nuevaContrasena.equals(confirmarContrasena)) {
                logger.info("Intentando cambiar la contraseña para el correo: {}", correo);

                usuarioService.actualizarContrasena(correo, nuevaContrasena);

                logger.info("Contraseña cambiada exitosamente para el correo: {}", correo);
                mensaje = new Mensaje("success", "Contraseña cambiada exitosamente.", "/admin/index.html");
            } else {
                logger.warn("Las contraseñas no coinciden para el correo: {}", correo);
                mensaje = new Mensaje("error", "Las contraseñas no coinciden.", "/recuperar/cambiar");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al cambiar la contraseña para el correo: {}. Error: {}", correo, e.getMessage());
            mensaje = new Mensaje("error", e.getMessage(), "/recuperar/cambiar");
        } catch (Exception e) {
            logger.error("Error al cambiar la contraseña para el correo: {}. Error: {}", correo, e.getMessage(), e);
            mensaje = new Mensaje("error", "Error al cambiar la contraseña.", "/recuperar/cambiar");
        }
        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
