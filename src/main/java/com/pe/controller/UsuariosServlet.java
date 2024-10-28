package com.pe.controller;

import com.pe.model.entidad.Usuario;
import com.pe.model.html.UsuarioHtml;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/usuario/listar")
public class UsuariosServlet extends BaseServlet {

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
            // Recargar usuarios desde la base de datos en cada petición
            usuarioService.cargarUsuarios();

            // Evitar que el navegador almacene en caché la respuesta
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // Obtener los parámetros de búsqueda
            String nombre = request.getParameter("nombre");
            String dni = request.getParameter("dni");
            String tipo = request.getParameter("tipo");
            String estado = request.getParameter("estado");

            // Filtrar los datos de los usuarios según los parámetros de búsqueda
            TreeSet<Usuario> usuariosFiltrados = usuarioService.buscarUsuarios(nombre, dni, tipo, estado);

            // Leer el HTML del contenido específico
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_usuario.html")));

            // Reemplazar la parte dinámica de la tabla
            if (usuariosFiltrados.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='7'>No se encontraron usuarios que coincidan.</td></tr>");
            } else {
                html = html.replace("${tableRows}", UsuarioHtml.generarFilasTablaUsuarios(usuariosFiltrados));
            }

            // Reemplazar las opciones de los filtros
            html = html.replace("${tiposUsuarioOptions}", UsuarioHtml.generarOpcionesTipoUsuario(usuarioService.getTiposUsuarioSet()));
            html = html.replace("${estadosOptions}", UsuarioHtml.generarOpcionesEstadoUsuario(usuarioService.getEstadosUsuarioSet()));
            html = html.replace("${scriptConfirmacionEliminacion}", UsuarioHtml.generarScriptConfirmacionEliminacion());

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar usuarios: " + e.getMessage());
        }

    }
}
