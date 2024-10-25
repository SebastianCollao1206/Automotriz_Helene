package com.pe.controller;

import com.pe.model.entidad.Usuario;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/usuario/agregar")
public class AgregarUsuarioServlet extends HttpServlet {
    private final UsuarioService usuarioService;

    public AgregarUsuarioServlet() {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try (PrintWriter out = response.getWriter()) {

            // Incluir el header a través del HeaderServlet
            request.getRequestDispatcher("/estatic/header.html").include(request, response);
            // Incluir sidebar manteniendo la estructura
            request.getRequestDispatcher("/estatic/sidebar.html").include(request, response);
            // Incluir el contenido principal
            request.getRequestDispatcher("/agregar_usuario.html").include(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String dni = request.getParameter("dni");
        String tipo = request.getParameter("tipo");
        String estado = request.getParameter("estado");
        String contrasena = request.getParameter("contrasena");

        try {
            usuarioService.agregarUsuario(nombre, correo, dni, tipo, estado, contrasena);
            // Redirige con mensaje de éxito
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("alert('Usuario agregado exitosamente!');");
            out.println("window.location='/usuario/agregar';"); // Reemplaza con tu página
            out.println("</script>");
        } catch (Exception e) {
            // Redirige con mensaje de error
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("alert('Error al agregar el usuario');");
            out.println("window.location='/usuario/agregar';"); // Reemplaza con tu página
            out.println("</script>");
        }
    }
}
