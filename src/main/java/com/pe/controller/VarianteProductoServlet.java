package com.pe.controller;

import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.html.UsuarioHtml;
import com.pe.model.html.VarianteHtml;
import com.pe.model.service.ProductoService;
import com.pe.model.service.TamanioService;
import com.pe.model.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/variante/producto")
public class VarianteProductoServlet extends BaseServlet{
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

            // Leer el HTML del contenido específico
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/variante_producto.html")));

            // Reemplazar la parte dinámica de la tabla
            if (variantes.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='6'>No se encontraron variantes para este producto.</td></tr>");
            } else {
                html = html.replace("${tableRows}", VarianteHtml.generarFilasTablaVariantes(variantes, variantesService));
            }

            // Reemplazar el marcador ${nombreProducto} con el nombre del producto
            html = html.replace("${nombreProducto}", nombreProducto != null ? nombreProducto : "Producto no encontrado");
            html = html.replace("${scriptConfirmacionActualizacion}", VarianteHtml.generarScriptConfirmacion());

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar variantes: " + e.getMessage());
        }
    }
}
