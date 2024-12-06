package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.html.ProductosClienteHtml;
import com.pe.model.administrador.service.CategoriaService;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

@WebServlet("/cliente/productos")
public class ProductosClienteServlet extends BaseClientServlet{
    private static final Logger logger = LoggerFactory.getLogger(ProductosClienteServlet.class);

    @Override
    protected String getContentPage() {
        return "/productos.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ProductoService productoService = new ProductoService();
            VarianteService varianteService = new VarianteService();
            CategoriaService categoriaService = new CategoriaService();

            String categoriaId = request.getParameter("categoria");
            String ordenPrecio = request.getParameter("orden");

            int paginaActual = 1;
            try {
                String paginaParam = request.getParameter("pagina");
                if (paginaParam != null && !paginaParam.isEmpty()) {
                    paginaActual = Integer.parseInt(paginaParam);
                }
            } catch (NumberFormatException e) {
                logger.warn("Parámetro de página inválido", e);
            }

            TreeSet<Producto> productos = productoService.obtenerProductosPorCategoriaOTodos(categoriaId);
            TreeSet<Producto> productosDisponibles = new TreeSet<>(ProductoService.filtrarProductosDisponibles(productos, varianteService));

            if (ordenPrecio != null) {
                boolean mayorAMenor = ordenPrecio.equals("mayor");
                productosDisponibles = varianteService.ordenarProductosPorPrecio(productosDisponibles, mayorAMenor);
            }

            String htmlTemplate = ProductosClienteHtml.generarHtmlCompleto(productos, productoService,
                    varianteService, paginaActual, new ArrayList<>(productosDisponibles), categoriaId, categoriaService);

            request.setAttribute("content", htmlTemplate);
            super.doGet(request, response);

            logger.info("Generado HTML de productos con {} productos disponibles, página {}",
                    productosDisponibles.size(), paginaActual);

        } catch (SQLException e) {
            logger.error("Error al cargar productos", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar productos");
        }
    }
}
