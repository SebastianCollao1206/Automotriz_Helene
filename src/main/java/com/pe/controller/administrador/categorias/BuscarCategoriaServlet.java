package com.pe.controller.administrador.categorias;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buscarCategoria")
public class BuscarCategoriaServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BuscarCategoriaServlet.class);
    private final CategoriaService categoriaService;

    public BuscarCategoriaServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_slider.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreCategoria = request.getParameter("nombre");
        String relacionarCon = request.getParameter("relacionarCon");

        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            try {
                Categoria categoria = categoriaService.buscarCategoriaPorNombre(nombreCategoria);
                if (categoria != null) {
                    request.setAttribute("opcionSeleccionada", categoria.getNombre());
                    request.setAttribute("idOpcionSeleccionada", categoria.getIdCategoria());

                    request.getSession().setAttribute("relacionarCon", relacionarCon);

                    logger.info("Categoría encontrada: {}", categoria.getNombre());
                } else {
                    request.setAttribute("mensajeError", "Categoría no encontrada");

                    logger.warn("Categoría no encontrada: {}", nombreCategoria);
                }
            } catch (SQLException e) {
                request.setAttribute("mensajeError", "Error al buscar la categoría: " + e.getMessage());
                logger.error("Error al buscar la categoría: {}", e.getMessage(), e);
            }
        } else {
            request.setAttribute("mensajeError", "Por favor, ingresa un nombre de categoría.");

            logger.warn("Nombre de categoría no proporcionado.");
        }
        request.getRequestDispatcher("/slider/agregar").forward(request, response);
    }
}
