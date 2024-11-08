package com.pe.controller.administrador.productos;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.html.ProductoHtml;
import com.pe.model.service.CategoriaService;
import com.pe.model.service.ProductoService;
import com.pe.model.service.TamanioService;
import com.pe.model.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/producto/agregar")
public class AgregarProductoServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(AgregarProductoServlet.class);
    private final CategoriaService categoriaService;
    private final TamanioService tamanioService;
    private final ProductoService productoService;
    private final VarianteService varianteService;

    public AgregarProductoServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
        this.tamanioService = new TamanioService();
        this.productoService = new ProductoService();
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_producto.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            String action = request.getParameter("action");

            if ("generarCamposVariantes".equals(action)) {
                int numVariantes = Integer.parseInt(request.getParameter("numVariantes"));
                TreeSet<Tamanio> tamaniosActivos = tamanioService.cargarTamaniosActivos();
                String html = ProductoHtml.generarCamposVariantes(numVariantes, tamaniosActivos);
                response.setContentType("text/html");
                response.getWriter().write(html);
            } else {
                categoriaService.cargarCategorias();
                tamanioService.cargarTamanios();

                TreeSet<Categoria> categoriasActivas = categoriaService.cargarCategoriasActivas();
                TreeSet<Tamanio> tamaniosActivos = tamanioService.cargarTamaniosActivos();

                String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/agregar_producto.html")));
                html = html.replace("${categoriasOptions}", CategoriaHtml.generarOpcionesCategorias(categoriasActivas));

                request.setAttribute("content", html);
                super.doGet(request, response);
            }
        } catch (SQLException e) {
            logger.error("Error al cargar datos: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar datos: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mensaje;
        String redirigirUrl = "/producto/agregar";

        try {
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String categoriaStr = request.getParameter("categoria");
            String numVariantesStr = request.getParameter("num-variantes");

            int idCategoria = Integer.parseInt(categoriaStr);
            int numVariantes = Integer.parseInt(numVariantesStr);

            productoService.agregarProducto(nombre, descripcion, idCategoria, numVariantes, request);
            mensaje = "Producto agregado exitosamente!";
            logger.info("Producto agregado: {}", nombre);

        } catch (SQLException e) {
            mensaje = "Error al agregar el producto";
            logger.warn(mensaje);
        } catch (IllegalArgumentException e) {
            mensaje = "Error: " + e.getMessage();
            logger.warn(mensaje);
        } catch (Exception e) {
            mensaje = "Error inesperado: " + e.getMessage();
            logger.error(mensaje, e);
        }
        String alertScript = ProductoHtml.generarMensajeAlerta(mensaje, redirigirUrl);
        response.setContentType("text/html");
        response.getWriter().write(alertScript);
    }
}
