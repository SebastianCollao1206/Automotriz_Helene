package com.pe.controller.administrador.productos;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Producto;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.service.CategoriaService;
import com.pe.model.service.ProductoService;
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

@WebServlet("/producto/editar")
public class EditarProductoServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditarProductoServlet.class);
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public EditarProductoServlet() throws SQLException {
        this.productoService = new ProductoService();
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_producto.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Producto producto = productoService.obtenerProductoPorId(id);

                if (producto != null) {
                    // Cargar categorías
                    categoriaService.cargarCategorias();
                    TreeSet<Categoria> categorias = categoriaService.getCategorias();

                    String opcionesCategorias = CategoriaHtml.generarOpcionesCategorias2(categorias, producto.getIdCategoria());

                    // Cargar el HTML y reemplazar los valores
                    String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_producto.html")));
                    html = html.replace("${producto.id}", String.valueOf(producto.getIdProducto()));
                    html = html.replace("${producto.nombre}", producto.getNombre());
                    html = html.replace("${producto.descripcion}", producto.getDescripcion());
                    html = html.replace("${selectedCategoria}", opcionesCategorias); // Reemplazar con las opciones generadas

                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    logger.warn("Producto no encontrado con ID: {}", id);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                }
            } catch (NumberFormatException e) {
                logger.error("ID de producto inválido: {}", idParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de producto inválido");
            } catch (SQLException e) {
                logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            logger.warn("ID de producto no proporcionado");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de producto no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String categoriaIdParam = request.getParameter("categoria");

        String mensaje;
        String redirigirUrl = "/producto/listar";

        try {
            int id = Integer.parseInt(idParam);
            int idCategoria = Integer.parseInt(categoriaIdParam);

            productoService.actualizarProducto(id, nombre, descripcion, idCategoria);
            mensaje = "Producto actualizado exitosamente!";
        } catch (Exception e) {
            logger.error("Error de formato en los parámetros: {}", e.getMessage(), e);
            mensaje = "Error al actualizar el producto: " + e.getMessage();
        }

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Redirigir a la URL construida
        response.sendRedirect(redirigirUrl);
    }
}
