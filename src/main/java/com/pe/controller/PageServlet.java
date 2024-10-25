package com.pe.controller;

import com.pe.model.entidad.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/pagina")
public class PageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        String page = request.getParameter("page");
        if (page == null) {
            page = "agregar_usuario.html"; // Página por defecto
        }

        try (PrintWriter out = response.getWriter()) {
            // Incluir el encabezado y pasar los datos del usuario
            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/estatic/header.html").include(request, response);
            request.getRequestDispatcher("/estatic/sidebar.html").include(request, response);
            request.getRequestDispatcher("/" + page).include(request, response);
        }
    }
}
