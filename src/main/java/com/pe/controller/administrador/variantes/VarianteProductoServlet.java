package com.pe.controller.administrador.variantes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Variante;
import com.pe.model.html.VarianteHtml;
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
import java.nio.file.Files;
import java.nio.file.Paths;
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
            String idProductoStr = request.getParameter("id");
            if (idProductoStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de producto no especificado");
                return;
            }
            int idProducto = Integer.parseInt(idProductoStr);
            String nombreProducto = productoService.obtenerNombreProductoPorId(idProducto);
            TreeSet<Variante> variantes = variantesService.obtenerVariantesPorProducto(idProducto);
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/variante_producto.html")));
            if (variantes.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='6'>No se encontraron variantes para este producto.</td></tr>");
                logger.info("No se encontraron variantes para el producto ID: {}", idProducto);
            } else {
                html = html.replace("${tableRows}", VarianteHtml.generarFilasTablaVariantes(variantes, variantesService));
                logger.info("Se encontraron {} variantes para el producto ID: {}", variantes.size(), idProducto);
            }
            html = html.replace("${nombreProducto}", nombreProducto != null ? nombreProducto : "Producto no encontrado");
            html = html.replace("${scriptConfirmacionActualizacion}", VarianteHtml.generarScriptConfirmacion());
            request.setAttribute("content", html);
            super.doGet(request, response);
        } catch (SQLException e) {
            logger.error("Error al cargar variantes: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar variantes: " + e.getMessage());
        }
    }
}
