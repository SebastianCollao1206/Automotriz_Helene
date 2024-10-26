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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UsuarioService usuarioService;

    public LoginServlet() {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        try {
            Usuario usuario = usuarioService.autenticarUsuario(correo, password);
            if (usuario != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                response.sendRedirect("/usuario/agregar");
            } else {
                // Si la autenticación falla, redirigir con un mensaje de alerta
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<script type='text/javascript'>");
                out.println("alert('Correo o contraseña incorrectos.');");
                out.println("window.location='index.html';");
                out.println("</script>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, mostrar un mensaje de alerta
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("alert('Error al procesar la solicitud.');");
            out.println("window.location='index.html';");
            out.println("</script>");
        }
    }
}
