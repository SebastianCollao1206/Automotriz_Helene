package com.pe.controller.administrador.usuarios.recuperarContraseña;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.Usuario;
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

@WebServlet("/recuperar/solicitar")
public class RecuperarContrasenaServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RecuperarContrasenaServlet.class);
    private UsuarioService usuarioService;

    public RecuperarContrasenaServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/recuperar_contrasena.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = request.getParameter("correo");
        Mensaje mensaje;

        logger.info("Solicitud de recuperación de contraseña para el correo: {}", correo);

        try {
            usuarioService.cargarUsuarios();
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correo);
            if (usuario != null) {
                String codigoVerificacion = usuarioService.generarCodigoVerificacion();
                usuarioService.enviarCodigoVerificacion(correo, codigoVerificacion);

                HttpSession session = request.getSession();
                session.setAttribute("correo", correo);
                session.setAttribute("codigoVerificacion", codigoVerificacion);

                logger.info("Código de verificación generado y enviado para el correo: {}", correo);
                mensaje = new Mensaje("success", "Código de verificación enviado a su correo.", "/recuperar/verificar");
            } else {
                logger.warn("Intento de recuperación de contraseña con correo no registrado: {}", correo);
                mensaje = new Mensaje("error", "Correo no registrado en el sistema, o el correo esta inactivo.", null);
            }
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud de recuperación de contraseña para el correo: {}", correo, e);
            mensaje = new Mensaje("error", "Error al enviar código de verificación.", null);
        }

        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
