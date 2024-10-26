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


@WebServlet("/base")
public abstract class BaseServlet extends HttpServlet {
    protected abstract String getContentPage();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Obtener la sesión actual
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verificar si el usuario está autenticado
        if (usuario == null) {
            // Si el usuario no está autenticado, redirigir a la página de inicio de sesión
            response.sendRedirect("/");
            return;
        }

        try (PrintWriter out = response.getWriter()) {

            //Incluir el header
            request.getRequestDispatcher("/estatic/header.html").include(request, response);

            // Inyectar script para cargar la información dinámica del usuario
            out.println("<script>");
            out.println("fetch('/header/usuario-info')");
            out.println("  .then(response => response.text())");
            out.println("  .then(data => {");
            out.println("    document.getElementById('usuario-info').innerHTML = data;");
            out.println("  });");
            out.println("</script>");

            // Incluir el sidebar
            request.getRequestDispatcher("/estatic/sidebar.html").include(request, response);

            // Incluir el contenido específico de la página
            request.getRequestDispatcher(getContentPage()).include(request, response);
        }
    }
}
