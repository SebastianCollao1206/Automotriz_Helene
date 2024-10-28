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
public class EliminarUsuarioServlet extends BaseServlet {
    private final UsuarioService usuarioService;

    public EliminarUsuarioServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_usuario.html";
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

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);


        // Redireccionar usando sendRedirect en lugar de JavaScript
        response.sendRedirect(redirigirUrl);
    }
}
