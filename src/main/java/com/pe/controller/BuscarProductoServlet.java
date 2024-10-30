package com.pe.controller;

import com.pe.model.entidad.Producto;
import com.pe.model.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buscarProducto")
public class BuscarProductoServlet extends BaseServlet {
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
                    // Si se encuentra el producto, redirigir a agregar variante
                    request.setAttribute("productoNombre", producto.getNombre());
                    request.setAttribute("productoId", producto.getIdProducto());
                } else {
                    // Si no se encuentra, establecer un mensaje de error
                    request.setAttribute("mensajeError", "Producto no encontrado");
                }
            } catch (SQLException e) {
                request.setAttribute("mensajeError", "Error al buscar el producto: " + e.getMessage());
            }
        } else {
            request.setAttribute("mensajeError", "Por favor, ingresa un nombre de producto.");
        }

        // Redirigir de vuelta al formulario de agregar variante
        request.getRequestDispatcher("/variante/agregar").forward(request, response);
    }
}
