package com.pe.controller.administrador.productos;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.html.CategoriaHtml;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/producto/listar")
public class ProductosServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductosServlet.class);
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
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.SOLO_TRABAJADOR;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            productoService.cargarProductos();
            categoriaService.cargarCategorias();

            String nombre = request.getParameter("nombre");
            String categoriaId = request.getParameter("categoria");
            String idProducto = request.getParameter("id");

            TreeSet<Producto> productosFiltrados = productoService.buscarProductos(nombre, categoriaId);

            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_producto.html")));

            if (productosFiltrados.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='5'>No se encontraron productos que coincidan.</td></tr>");
            } else {
                html = html.replace("${tableRows}", ProductoHtml.generarFilasTablaProductos(productosFiltrados, productoService));
            }

            TreeSet<Categoria> categorias = categoriaService.getCategorias();
           html = html.replace("${categoriasOptions}", CategoriaHtml.generarOpcionesCategorias(categorias));

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            logger.error("Error al cargar productos: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar productos: " + e.getMessage());
        }
    }
}
