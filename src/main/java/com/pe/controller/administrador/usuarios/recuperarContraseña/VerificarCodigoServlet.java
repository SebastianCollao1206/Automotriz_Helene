package com.pe.controller.administrador.usuarios.recuperarContraseña;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.service.MensajeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/recuperar/verificar")
public class VerificarCodigoServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(VerificarCodigoServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/verificar_codigo.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String correoGuardado = (String) session.getAttribute("correo");
        String codigoGenerado = (String) session.getAttribute("codigoVerificacion");
        String codigoIngresado = request.getParameter("codigo");

        Integer intentosFallidos = (Integer) session.getAttribute("intentosFallidos");
        if (intentosFallidos == null) {
            intentosFallidos = 0;
        }

        Mensaje mensaje;

        if (correoGuardado != null && codigoGenerado != null &&
                codigoGenerado.equals(codigoIngresado)) {
            mensaje = new Mensaje("success", "Código de verificación correcto. Puedes cambiar tu contraseña.", "/recuperar/cambiar");
            MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
            logger.info("Código de verificación correcto para el correo: {}", correoGuardado);
        } else {
            intentosFallidos++;
            session.setAttribute("intentosFallidos", intentosFallidos);
            logger.warn("Código de verificación incorrecto para el correo: {}. Intento número: {}", correoGuardado, intentosFallidos);

            if (intentosFallidos >= 2) {
                mensaje = new Mensaje("error", "Has agotado tus intentos de verificación. Por favor, solicita un nuevo código.", null);
                MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
                logger.warn("Se han agotado los intentos de verificación para el correo: {}", correoGuardado);
                session.removeAttribute("intentosFallidos");
            } else {
                mensaje = new Mensaje("error", "Código de verificación incorrecto. Te quedan " + (2 - intentosFallidos) + " intento(s).", null);
                MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
            }
        }
    }
}
