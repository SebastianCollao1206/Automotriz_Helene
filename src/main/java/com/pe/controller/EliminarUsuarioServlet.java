package com.pe.controller;

import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
            mensaje = "Usuario cambiado a inactivo exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al cambiar el estado del usuario: " + e.getMessage();
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        response.sendRedirect(redirigirUrl);
    }
}
