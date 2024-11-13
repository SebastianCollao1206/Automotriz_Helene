package com.pe.controller.administrador.productos;

import com.pe.controller.administrador.BaseServlet;
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

@WebServlet("/buscarProducto")
public class BuscarProductoServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BuscarProductoServlet.class);
    private final ProductoService productoService;

    public BuscarProductoServlet() throws SQLException {
        this.productoService = new ProductoService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_variante.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreProducto = request.getParameter("nombre");

        if (nombreProducto != null && !nombreProducto.isEmpty()) {
            try {
                Producto producto = productoService.buscarProductoPorNombre(nombreProducto);
                if (producto != null) {
                    request.setAttribute("productoNombre", producto.getNombre());
                    request.setAttribute("productoId", producto.getIdProducto());
                    logger.info("Producto encontrado: {}", producto.getNombre());
                } else {
                    // Si no se encuentra, establecer un mensaje de error
                    request.setAttribute("mensajeError", "Producto no encontrado");
                    logger.warn("Producto no encontrado: {}", nombreProducto);
                }
//            } catch (SQLException e) {
//                request.setAttribute("mensajeError", "Error al buscar el producto: " + e.getMessage());
//                logger.error("Error al buscar el producto: {}", e.getMessage(), e);
            } finally {

            }
        } else {
            request.setAttribute("mensajeError", "Por favor, ingresa un nombre de producto.");
            logger.warn("Nombre de producto no proporcionado.");
        }

        request.getRequestDispatcher("/variante/agregar").forward(request, response);
    }
}
