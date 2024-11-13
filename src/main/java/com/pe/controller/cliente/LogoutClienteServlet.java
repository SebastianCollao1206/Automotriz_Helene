package com.pe.controller.cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/cliente/logout")
public class LogoutClienteServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutClienteServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("Cliente ha cerrado sesión: {}", session.getAttribute("cliente"));
            session.invalidate();
        }
        response.sendRedirect("/cliente/login");
    }
}
