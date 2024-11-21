package com.pe.controller.administrador.Slider;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buscarProduct")
public class BuscarProductServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BuscarProductServlet.class);
    private final ProductoService productoService;

    public BuscarProductServlet() throws SQLException {
        this.productoService = new ProductoService();
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
        String nombreProducto = request.getParameter("nombre");
        String relacionarCon = request.getParameter("relacionarCon");

        if (nombreProducto != null && !nombreProducto.isEmpty()) {
            try {
                Producto producto = productoService.buscarProductoPorNombre(nombreProducto);
                if (producto != null) {
                    request.setAttribute("opcionSeleccionada", producto.getNombre());
                    request.setAttribute("idOpcionSeleccionada", producto.getIdProducto());

                    request.getSession().setAttribute("relacionarCon", relacionarCon);

                    logger.info("Producto encontrado: {}", producto.getNombre());
                } else {
                    request.setAttribute("mensajeError", "Producto no encontrado");
                    logger.warn("Producto no encontrado: {}", nombreProducto);
                }
            } catch (SQLException e) {
                request.setAttribute("mensajeError", "Error al buscar el producto: " + e.getMessage());
                logger.error("Error al buscar el producto: {}", e.getMessage(), e);
            }
        } else {
            request.setAttribute("mensajeError", "Por favor, ingresa un nombre de producto.");
            logger.warn("Nombre de producto no proporcionado.");
        }
        request.getRequestDispatcher("/slider/agregar").forward(request, response);
    }
}
