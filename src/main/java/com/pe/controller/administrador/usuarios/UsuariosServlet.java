package com.pe.controller.administrador.usuarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Usuario;
import com.pe.model.html.UsuarioHtml;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_usuario.html")));
            if (usuariosFiltrados.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='7'>No se encontraron usuarios que coincidan.</td></tr>");
                logger.info("No se encontraron usuarios que coincidan con los criterios de búsqueda.");
            } else {
                html = html.replace("${tableRows}", UsuarioHtml.generarFilasTablaUsuarios(usuariosFiltrados));
                logger.info("Se encontraron {} usuarios que coinciden con los criterios de búsqueda.", usuariosFiltrados.size());
            }
            html = html.replace("${tiposUsuarioOptions}", UsuarioHtml.generarOpcionesTipoUsuario(usuarioService.getTiposUsuarioSet()));
            html = html.replace("${estadosOptions}", UsuarioHtml.generarOpcionesEstadoUsuario(usuarioService.getEstadosUsuarioSet()));
            html = html.replace("${scriptConfirmacionEliminacion}", UsuarioHtml.generarScriptConfirmacionEliminacion());
            request.setAttribute("content", html);
            super.doGet(request, response);
        } catch (SQLException e) {
            logger.error("Error al cargar usuarios: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar usuarios: " + e.getMessage());
        }
    }
}
