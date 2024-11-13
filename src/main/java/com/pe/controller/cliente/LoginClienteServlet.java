package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.service.MensajeService;
import com.pe.model.cliente.entidad.Cliente;
import com.pe.model.cliente.service.ClienteService;
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

@WebServlet("/cliente/login")
public class LoginClienteServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginClienteServlet.class);
    private final ClienteService clienteService;

    public LoginClienteServlet() throws SQLException {
        this.clienteService = new ClienteService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/cliente/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        Mensaje mensaje;

        logger.info("Intento de inicio de sesión para el correo: {}", correo);

        try {
            Cliente cliente = clienteService.autenticarCliente(correo, password);
            if (cliente != null) {
                HttpSession session = request.getSession();
                session.setAttribute("cliente", cliente);
                logger.info("Cliente autenticado exitosamente: {}", correo);
                mensaje = new Mensaje("success", "Inicio de sesión exitoso.", "/cliente/");
            } else {
                logger.warn("Intento de inicio de sesión fallido para el correo: {}", correo);
                mensaje = new Mensaje("error", "Correo o contraseña incorrectos.", null);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación en el inicio de sesión: {}", e.getMessage());
            mensaje = new Mensaje("error", e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud de inicio de sesión para el correo: {}", correo, e);
            mensaje = new Mensaje("error", "Error al procesar la solicitud.", null);
        }

        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());

    }
}
