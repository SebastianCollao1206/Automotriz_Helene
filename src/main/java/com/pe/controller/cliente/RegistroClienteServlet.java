package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.service.MensajeService;
import com.pe.model.cliente.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/cliente/registro")
public class RegistroClienteServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistroClienteServlet.class);
    private final ClienteService clienteService;

    public RegistroClienteServlet() throws SQLException {
        this.clienteService = new ClienteService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Recibida solicitud GET para /cliente/registro");
        request.getRequestDispatcher("/cliente/registro.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String confirmPassword = request.getParameter("confirmPassword");
//        String dni = null;
//        String nombre = null;

        String dni = request.getParameter("hiddenDni");
        String nombre = request.getParameter("customerName");

        Mensaje mensaje;

        try {
            clienteService.agregarCliente(correo, contrasena, confirmPassword, dni, nombre);
            logger.info("Cliente agregado: Correo = {}", correo);
            mensaje = new Mensaje("success", "Cliente agregado exitosamente!", "/cliente/login");
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al agregar cliente: {}", e.getMessage());
            mensaje = new Mensaje("error", e.getMessage(), null);
        } catch (SecurityException e) {
            logger.error("Error de seguridad al agregar cliente: {}", e.getMessage());
            mensaje = new Mensaje("error", "Error de seguridad: " + e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Error inesperado al agregar cliente: {}", e.getMessage(), e);
            mensaje = new Mensaje("error", "Error interno del servidor al procesar la solicitud.", null);
        }

        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
