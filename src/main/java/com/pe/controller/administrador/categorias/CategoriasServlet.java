package com.pe.controller.administrador.categorias;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Categoria;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.service.CategoriaService;
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

@WebServlet("/categoria/listar")
public class CategoriasServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(CategoriasServlet.class);
    private final CategoriaService categoriaService;

    public CategoriasServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_categoria.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            categoriaService.cargarCategorias();

            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            String nombre = request.getParameter("nombre");
            String estadoStr = request.getParameter("estado");

            Categoria.EstadoCategoria estado = null;
            if (estadoStr != null && !estadoStr.isEmpty()) {
                try {
                    estado = Categoria.EstadoCategoria.valueOf(estadoStr);
                } catch (IllegalArgumentException e) {
                    logger.warn("Estado de categoría no válido: {}", estadoStr);
                    estado = null;
                }
            }

            // Filtrar los datos
            TreeSet<Categoria> categoriasFiltradas = categoriaService.buscarCategorias(nombre, estado);
            logger.info("Se encontraron {} categorías que coinciden con los criterios de búsqueda.", categoriasFiltradas.size());

            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_categoria.html")));
            logger.info("HTML de lista de categorías cargado correctamente.");

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
            logger.error("Error al cargar categorías: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar categorías: " + e.getMessage());
        }
    }
}
