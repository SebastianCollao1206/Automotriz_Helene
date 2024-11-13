package com.pe.controller.administrador.productos;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.html.ProductoHtml;
import com.pe.model.administrador.service.CategoriaService;
import com.pe.model.administrador.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
                productoService.cargarProductos();
                int id = Integer.parseInt(idParam);
                Producto producto = productoService.obtenerProductoPorId(id);

                if (producto != null) {
                    categoriaService.cargarCategorias();
                    TreeSet<Categoria> categoriasActivas = categoriaService.cargarCategoriasActivas();

                    String html = ProductoHtml.generarHtmlEdicionProducto(producto, categoriasActivas);

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
            productoService.cargarProductos();
            mensaje = "Producto actualizado exitosamente!";
            logger.info("Producto actualizado: {}", nombre);

            String alertScript = ProductoHtml.generarMensajeAlerta(mensaje, redirigirUrl);
            response.setContentType("text/html");
            response.getWriter().write(alertScript);
            return;

        } catch (SQLException e) {
            mensaje = "Error al acceder a la base de datos: " + e.getMessage();
            logger.error(mensaje, e);
        } catch (IllegalArgumentException e) {
            mensaje = "Error: " + e.getMessage();
            logger.warn(mensaje);
        } catch (Exception e) {
            mensaje = "Error inesperado: " + e.getMessage();
            logger.error(mensaje, e);
        }
        String alertScript = ProductoHtml.generarMensajeAlerta(mensaje, "/producto/editar?id=" + idParam);
        response.setContentType("text/html");
        response.getWriter().write(alertScript);
    }
}
