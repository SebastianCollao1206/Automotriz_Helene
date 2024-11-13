package com.pe.controller.administrador.categorias;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.html.CategoriaHtml;
import com.pe.model.administrador.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/categoria/editar")
public class EditarCategoriaServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditarCategoriaServlet.class);
    private final CategoriaService categoriaService;

    public EditarCategoriaServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_categoria.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                categoriaService.cargarCategorias();
                int id = Integer.parseInt(idParam);
                Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
                if (categoria != null) {
                    String html = CategoriaHtml.generarHtmlEdicionCategoria(categoria);
                    request.setAttribute("content", html);
                    super.doGet(request, response);
                    logger.info("Se ha cargado el formulario de edición para la categoría con ID: {}", id);
                } else {
                    logger.warn("Categoría no encontrada para ID: {}", id);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
                }
            } catch (NumberFormatException e) {
                logger.error("ID de categoría inválido: {}", idParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de categoría inválido");
            } catch (SQLException e) {
                logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            logger.warn("ID de categoría no proporcionado");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de categoría no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String estado = request.getParameter("estado");
        String mensaje;
        String redirigirUrl = "/categoria/listar";

        try {
            int id = Integer.parseInt(idParam);
            categoriaService.actualizarCategoria(id, nombre, estado);
            categoriaService.cargarCategorias();
            mensaje = "Categoría actualizada exitosamente!";
            logger.info("Categoría actualizada: ID = {}, Nombre = {}", id, nombre);
        } catch (IllegalArgumentException e) {
            mensaje = "Estado inválido: " + e.getMessage();
            logger.warn("Intento de actualizar categoría con estado inválido: {}", estado);
        } catch (Exception e) {
            mensaje = "Error al actualizar la categoría: " + e.getMessage();
            logger.error("Error al actualizar la categoría ID = {}: {}", idParam, e.getMessage(), e);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        response.sendRedirect(redirigirUrl);
    }
}
