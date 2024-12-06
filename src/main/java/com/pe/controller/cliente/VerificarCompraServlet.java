package com.pe.controller.cliente;

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

@WebServlet("/cliente/verificar-compra")
public class VerificarCompraServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(VerificarCompraServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);

            if (session != null && session.getAttribute("cliente") != null) {
                Mensaje mensaje = new Mensaje("success", "Redirigiendo a compra", "/cliente/compra");
                MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
            } else {
                Mensaje mensaje = new Mensaje("error", "Debes iniciar sesión para realizar la compra", null);
                MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
            }
        } catch (Exception e) {
            logger.error("Error al verificar sesión de compra", e);
            Mensaje mensaje = new Mensaje("error", "Error al procesar la solicitud", null);
            MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
        }
    }
}
