package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.service.CategoriaService;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.VarianteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;

public class ProductosClienteHtml {

    private static final int PRODUCTOS_POR_PAGINA = 12;
    private static final int MAX_BOTONES_PAGINACION = 1;

    public static String generarHtmlCompleto(TreeSet<Producto> productos, ProductoService productoService, VarianteService varianteService, int paginaActual, List<Producto> productosDisponibles, String categoriaId,
                                             CategoriaService categoriaService) throws SQLException, IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/productos.html")));

        String productosHtml = generarHtmlProductos(productos, productoService, varianteService, paginaActual, productosDisponibles);
        String paginacionHtml = generarHtmlPaginacion(productosDisponibles, paginaActual, categoriaId);
        String categoriasHtml = generarHtmlCategorias(categoriaService, productoService, varianteService);
        String tituloHtml = generarHtmlTitulo(categoriaId, categoriaService, productosDisponibles);
        String dropdownHtml = generarDropdownOrdenamiento(categoriaId);

        htmlTemplate = htmlTemplate.replace("${productos}", productosHtml);
        htmlTemplate = htmlTemplate.replace("${categorias}", categoriasHtml);
        htmlTemplate = htmlTemplate.replace("${paginacion}", paginacionHtml);
        htmlTemplate = htmlTemplate.replace("${titulo}", tituloHtml);
        htmlTemplate = htmlTemplate.replace("${ordenamiento}", dropdownHtml);

        return htmlTemplate;
    }

    public static String generarHtmlProductos(TreeSet<Producto> productos,
                                              ProductoService productoService,
                                              VarianteService varianteService,
                                              int paginaActual,
                                              List<Producto> productosDisponibles) throws SQLException {
        StringBuilder html = new StringBuilder();

        int inicio = (paginaActual - 1) * PRODUCTOS_POR_PAGINA;
        int fin = Math.min(inicio + PRODUCTOS_POR_PAGINA, productosDisponibles.size());

        html.append("<div class=\"row text-center mt-5\">\n");

        for (int i = inicio; i < fin; i++) {
            Producto producto = productosDisponibles.get(i);
            Variante varianteDisponible = varianteService.encontrarVarianteConStock(producto);
            if (varianteDisponible != null) {
                html.append(generarTarjetaProducto(producto, varianteDisponible));
            }
        }
        html.append("</div>\n");
        return html.toString();
    }

    public static String generarHtmlPaginacion(List<Producto> productosDisponibles, int paginaActual, String categoriaId) {

        int totalProductos = productosDisponibles.size();
        int totalPaginas = calcularTotalPaginas(totalProductos);

        if (totalPaginas <= 1) {
            return "";
        }

        StringBuilder paginacion = new StringBuilder();
        paginacion.append("<nav aria-label=\"Page navigation\">\n");
        paginacion.append("    <ul class=\"pagination justify-content-center gap-2\">\n");

        String disabledPrev = paginaActual == 1 ? " disabled" : "";
        paginacion.append("        <li class=\"page-item").append(disabledPrev).append("\">");
        paginacion.append("<a class=\"page-link\" href=\"productos?pagina=").append(paginaActual - 1);
        if (categoriaId != null && !categoriaId.isEmpty()) {
            paginacion.append("&categoria=").append(categoriaId);
        }
        paginacion.append("\">Anterior</a></li>\n");

        int inicio = calcularInicioPaginacion(paginaActual, totalPaginas);
        int fin = Math.min(totalPaginas, inicio + MAX_BOTONES_PAGINACION - 1);

        if (inicio > 1) {
            paginacion.append(generarBotonPagina(1, paginaActual, categoriaId));
            if (inicio > 2) {
                paginacion.append("        <li class=\"page-item disabled\"><span class=\"page-link\">...</span></li>\n");
            }
        }

        for (int i = inicio; i <= fin; i++) {
            paginacion.append(generarBotonPagina(i, paginaActual, categoriaId));
        }

        if (fin < totalPaginas) {
            if (fin < totalPaginas - 1) {
                paginacion.append("        <li class=\"page-item disabled\"><span class=\"page-link\">...</span></li>\n");
            }
            paginacion.append(generarBotonPagina(totalPaginas, paginaActual, categoriaId));
        }

        String disabledNext = paginaActual == totalPaginas ? " disabled" : "";
        paginacion.append("        <li class=\"page-item").append(disabledNext).append("\">");
        paginacion.append("<a class=\"page-link\" href=\"productos?pagina=").append(paginaActual + 1);
        if (categoriaId != null && !categoriaId.isEmpty()) {
            paginacion.append("&categoria=").append(categoriaId);
        }
        paginacion.append("\">Siguiente</a></li>\n");

        paginacion.append("    </ul>\n");
        paginacion.append("</nav>");

        return paginacion.toString();
    }

    private static int calcularTotalPaginas(int totalProductos) {
        return (int) Math.ceil((double) totalProductos / PRODUCTOS_POR_PAGINA);
    }

    private static int calcularInicioPaginacion(int paginaActual, int totalPaginas) {
        int inicio = Math.max(1, paginaActual - (MAX_BOTONES_PAGINACION / 2));
        int fin = Math.min(totalPaginas, inicio + MAX_BOTONES_PAGINACION - 1);

        if (fin == totalPaginas) {
            inicio = Math.max(1, fin - MAX_BOTONES_PAGINACION + 1);
        }

        return inicio;
    }


    public static String generarHtmlTitulo(String categoriaId, CategoriaService categoriaService,
                                           List<Producto> productosDisponibles) throws SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<h4 class=\"mt-3\">");

        if (categoriaId != null && !categoriaId.isEmpty()) {
            Categoria categoria = categoriaService.obtenerCategoriaPorId(Integer.parseInt(categoriaId));
            html.append(categoria.getNombre());
        } else {
            html.append("Todos los productos");
        }

        html.append(" (").append(productosDisponibles.size()).append(")</h4>");
        return html.toString();
    }

    public static String generarHtmlCategorias(CategoriaService categoriaService,
                                               ProductoService productoService,
                                               VarianteService varianteService) throws SQLException {
        StringBuilder html = new StringBuilder();

        html.append("<div id=\"collapseOne\" class=\"accordion-collapse collapse show\" ");
        html.append("aria-labelledby=\"headingOne\" data-bs-parent=\"#accordionExample\">\n");
        html.append("    <div class=\"accordion-body\">\n");
        html.append("        <ul class=\"list-unstyled p-2\">\n");
        html.append("            <li class=\"mb-3\">");
        html.append("<a href=\"productos\" class=\"text-decoration-none lista-categoria\">");
        html.append("Todos los productos");
        html.append("</a></li>\n");

        TreeSet<Categoria> todasLasCategorias = categoriaService.getCategorias();

        for (Categoria categoria : todasLasCategorias) {
            if (productoService.tieneCategoriaProductosDisponibles(categoria,productoService ,varianteService)) {
                html.append("            <li class=\"mb-3\">");
                html.append("<a href=\"productos?categoria=").append(categoria.getIdCategoria()).append("\" ");
                html.append("class=\"text-decoration-none lista-categoria\">");
                html.append(categoria.getNombre());
                html.append("</a></li>\n");
            }
        }
        html.append("        </ul>\n");
        html.append("    </div>\n");
        html.append("</div>");

        return html.toString();
    }

    private static String generarBotonPagina(int numeroPagina, int paginaActual, String categoriaId) {
        StringBuilder boton = new StringBuilder();
        String active = numeroPagina == paginaActual ? " active" : "";
        boton.append("        <li class=\"page-item").append(active).append("\">");
        boton.append("<a class=\"page-link\" href=\"productos?pagina=").append(numeroPagina);
        if (categoriaId != null && !categoriaId.isEmpty()) {
            boton.append("&categoria=").append(categoriaId);
        }
        boton.append("\">").append(numeroPagina).append("</a></li>\n");
        return boton.toString();
    }

    private static String generarTarjetaProducto(Producto producto, Variante variante) {
        StringBuilder tarjeta = new StringBuilder();
        tarjeta.append("<div class=\"col-6 col-md-4 col-lg-3 mb-5\">\n");
        tarjeta.append("    <div class=\"producto p-4 h-100\">\n");
        tarjeta.append("        <div class=\"imagen-producto\">\n");
        tarjeta.append("            <a href=\"detalle-producto?id=").append(producto.getIdProducto())
                .append("&variantId=").append(variante.getIdVariante()).append("\">\n"); // Agregar el ID de la variante
        tarjeta.append("                <img class=\"img-fluid\" src=\"").append(variante.getImagen()).append("\" alt=\"").append(producto.getNombre()).append("\">\n");
        tarjeta.append("            </a>\n");
        tarjeta.append("        </div>\n");
        tarjeta.append("        <h4 class=\"mt-4 producto-nombre\">").append(producto.getNombre()).append("</h4>\n");
        tarjeta.append("        <p class=\"mt-4 text-danger producto-precio\">$").append(variante.getPrecio()).append("</p>\n");
        tarjeta.append("        <div class=\"d-flex justify-content-center\">\n");
        tarjeta.append("            <a href=\"detalle-producto?id=").append(producto.getIdProducto())
                .append("&variantId=").append(variante.getIdVariante()).append("\"\n") // Agregar el ID de la variante
                .append("               class=\"btn btn-productos text-decoration-none d-flex align-items-center justify-content-center\">\n");
        tarjeta.append("                Ver producto <i class=\"bi bi-chevron-right ms-2\"></i>\n");
        tarjeta.append("            </a>\n");
        tarjeta.append("        </div>\n");
        tarjeta.append("    </div>\n");
        tarjeta.append("</div>\n");

        return tarjeta.toString();
    }

    private static String generarDropdownOrdenamiento(String categoriaId) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"col-12 col-md-4 text-center text-md-center mt-2 \">\n");
        html.append("    <div class=\"dropdown\">\n");
        html.append("        <button class=\"btn btn-precio dropdown-toggle border-0\" type=\"button\" ");
        html.append("id=\"dropdownMenuButton1\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">\n");
        html.append("            Ordenar por precio\n");
        html.append("        </button>\n");
        html.append("        <ul class=\"dropdown-menu mt-2 mx-2\" aria-labelledby=\"dropdownMenuButton1\">\n");

        String baseUrl = "productos?orden=mayor";
        String baseUrl2 = "productos?orden=menor";
        if (categoriaId != null && !categoriaId.isEmpty()) {
            baseUrl += "&categoria=" + categoriaId;
            baseUrl2 += "&categoria=" + categoriaId;
        }

        html.append("            <li><a class=\"dropdown-item\" href=\"").append(baseUrl).append("\">Por mayor precio</a></li>\n");
        html.append("            <li><a class=\"dropdown-item\" href=\"").append(baseUrl2).append("\">Por menor precio</a></li>\n");
        html.append("        </ul>\n");
        html.append("    </div>\n");
        html.append("</div>\n");

        return html.toString();
    }
}
