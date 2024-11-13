package com.pe.controller.administrador.usuarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.model.administrador.html.UsuarioHtml;
import com.pe.model.administrador.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/usuario/listar")
public class UsuariosServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(UsuariosServlet.class);
    private final UsuarioService usuarioService;

    public UsuariosServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_usuario.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            usuarioService.cargarUsuarios();
            String nombre = request.getParameter("nombre");
            String dni = request.getParameter("dni");
            String tipo = request.getParameter("tipo");
            String estado = request.getParameter("estado");
            TreeSet<Usuario> usuariosFiltrados = usuarioService.buscarUsuarios(nombre, dni, tipo, estado);
            logger.info("Usuarios filtrados: nombre={}, dni={}, tipo={}, estado={}, total={}", nombre, dni, tipo, estado, usuariosFiltrados.size());

            String html = UsuarioHtml.generarHtmlUsuarios(usuariosFiltrados, usuarioService);

            request.setAttribute("content", html);
            super.doGet(request, response);
        } catch (SQLException e) {
            logger.error("Error al cargar usuarios: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar usuarios: " + e.getMessage());
        }
    }
}
