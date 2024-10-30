package com.pe.controller.administrador;

import com.pe.model.entidad.Usuario;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UsuarioService usuarioService;

    public LoginServlet() throws SQLException {
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
                logger.info("Usuario autenticado exitosamente: {}", correo);
                response.sendRedirect("/producto/listar");//esto se cambia
            } else {
                logger.warn("Intento de inicio de sesión fallido para el correo: {}", correo);
                // Si la autenticación falla, redirigir con un mensaje de alerta
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<script type='text/javascript'>");
                out.println("alert('Correo o contraseña incorrectos.');");
                out.println("window.location='index.html';");
                out.println("</script>");
            }
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud de inicio de sesión para el correo: {}", correo, e);
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("alert('Error al procesar la solicitud.');");
            out.println("window.location='index.html';");
            out.println("</script>");
        }
    }
}
