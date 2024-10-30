package com.pe.controller.administrador.usuarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/usuario/eliminar")
public class EliminarUsuarioServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EliminarUsuarioServlet.class);
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
            logger.info("Usuario cambiado a inactivo: ID = {}", id);
        } catch (Exception e) {
            mensaje = "Error al cambiar el estado del usuario: " + e.getMessage();
            logger.error("Error al cambiar el estado del usuario: {}", e.getMessage(), e);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        response.sendRedirect(redirigirUrl);
    }
}
