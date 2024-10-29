package com.pe.controller;

import com.pe.model.entidad.Categoria;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.html.UsuarioHtml;
import com.pe.model.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/categoria/listar")
public class CategoriasServlet extends BaseServlet{
    private final CategoriaService categoriaService;

    public CategoriasServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_categoria.html"; // Cambia a la ruta de tu HTML
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recargar categorías desde la base de datos en cada petición
            categoriaService.cargarCategorias();

            // Evitar que el navegador almacene en caché la respuesta
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // Obtener los parámetros de búsqueda
            String nombre = request.getParameter("nombre");
            String estadoStr = request.getParameter("estado");

            // Convertir el estado de String a EstadoCategoria
            Categoria.EstadoCategoria estado = null;
            if (estadoStr != null && !estadoStr.isEmpty()) {
                try {
                    estado = Categoria.EstadoCategoria.valueOf(estadoStr);
                } catch (IllegalArgumentException e) {
                    // Manejar el caso donde el estado no es válido
                    estado = null; // O asigna un valor por defecto si es necesario
                }
            }

            // Filtrar los datos de las categorías según los parámetros de búsqueda
            TreeSet<Categoria> categoriasFiltradas = categoriaService.buscarCategorias(nombre, estado);

            // Leer el HTML del contenido específico
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_categoria.html")));

            // Reemplazar la parte dinámica de la tabla
            if (categoriasFiltradas.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='4'>No se encontraron categorías que coincidan.</td></tr>");
            } else {
                html = html.replace("${tableRows}", CategoriaHtml.generarFilasTablaCategorias(categoriasFiltradas));
            }

            html = html.replace("${scriptConfirmacionEliminacion}", CategoriaHtml.generarScriptConfirmacionEliminacion());

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar categorías: " + e.getMessage());
        }
    }
}
