package com.pe.controller.administrador.variantes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.html.VarianteHtml;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.TamanioService;
import com.pe.model.administrador.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/variante/producto")
public class VarianteProductoServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(VarianteProductoServlet.class);
    private final VarianteService variantesService;
    private final ProductoService productoService;
    private final TamanioService tamanioService;

    public VarianteProductoServlet() throws SQLException {
        this.variantesService = new VarianteService();
        this.productoService = new ProductoService();
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/variante_producto.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            variantesService.cargarVariantes();
            String idProductoStr = request.getParameter("id");

            if (idProductoStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de producto no especificado");
                return;
            }

            int idProducto = Integer.parseInt(idProductoStr);
            String nombreProducto = productoService.obtenerNombreProductoPorId(idProducto);
            TreeSet<Variante> variantes = variantesService.obtenerVariantesPorProducto(idProducto);

            String html = VarianteHtml.generarHtmlVariantes(variantes, variantesService, nombreProducto);

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            logger.error("Error al cargar variantes: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar variantes: " + e.getMessage());
        }
    }
}
