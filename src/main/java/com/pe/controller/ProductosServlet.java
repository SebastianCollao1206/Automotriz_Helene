package com.pe.controller;

import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Producto;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.html.ProductoHtml;
import com.pe.model.service.CategoriaService;
import com.pe.model.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/producto/listar")
public class ProductosServlet extends BaseServlet{
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductosServlet() throws SQLException {
        this.productoService = new ProductoService();
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_producto.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Cargar productos y categorías desde la base de datos en cada petición
            productoService.cargarProductos();
            categoriaService.cargarCategorias();

            // Evitar que el navegador almacene en caché la respuesta
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // Obtener los parámetros de búsqueda
            String nombre = request.getParameter("nombre");
            String categoriaId = request.getParameter("categoria");
            String idProducto = request.getParameter("id"); // Obtener el ID del producto si se ha pasado

            // Filtrar los datos de los productos según los parámetros de búsqueda
            TreeSet<Producto> productosFiltrados = productoService.buscarProductos(nombre, categoriaId);

            // Leer el HTML del contenido específico
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_producto.html")));

            // Reemplazar la parte dinámica de la tabla
            if (productosFiltrados.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='5'>No se encontraron productos que coincidan.</td></tr>");
            } else {
                html = html.replace("${tableRows}", ProductoHtml.generarFilasTablaProductos(productosFiltrados, productoService));
            }

            // Reemplazar las opciones de categorías
            TreeSet<Categoria> categorias = categoriaService.getCategorias();
            html = html.replace("${categoriasOptions}", CategoriaHtml.generarOpcionesCategorias(categorias));

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar productos: " + e.getMessage());
        }
    }
}
