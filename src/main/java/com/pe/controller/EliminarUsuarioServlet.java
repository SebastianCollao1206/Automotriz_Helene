package com.pe.controller;

import com.pe.model.html.UsuarioHtml;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/usuario/eliminar")
public class EliminarUsuarioServlet extends HttpServlet {
    private final UsuarioService usuarioService;

    public EliminarUsuarioServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String mensaje;
        String redirigirUrl = "/usuario/listar";

        try {
            usuarioService.eliminarUsuario(Integer.parseInt(id));
            mensaje = "Usuario eliminado exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al eliminar el usuario: " + e.getMessage();
        }

        // Generar el mensaje de alerta y la redirección
        String alertScript = "<script type='text/javascript'>" +
                "alert('" + mensaje + "');" +
                "window.location='" + redirigirUrl + "';" +
                "</script>";

        // Enviar el script de alerta como respuesta
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(alertScript);
    }
}
