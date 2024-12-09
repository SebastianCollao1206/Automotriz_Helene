package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Comentario;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.service.*;
import com.pe.model.cliente.entidad.Cliente;
import com.pe.model.cliente.service.ClienteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;

public class IndexHtml {

    public static String generarHtmlCompleto(TreeSet<Slider> slidersActivos,
                                             TreeSet<Comentario> comentariosActivos,
                                             ClienteService clienteService, ProductoService productoService
    , VarianteService varianteService) throws SQLException, IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/index.html")));

        String slidersHtml = generarHtmlSliderCliente(slidersActivos);
        String comentariosHtml = generarHtmlComentariosCliente(comentariosActivos, clienteService);
        String productosMasVendidosHtml = generarHtmlProductosMasVendidos(productoService, varianteService);

        htmlTemplate = htmlTemplate.replace("${sliders}", slidersHtml);
        htmlTemplate = htmlTemplate.replace("${comentarios}", comentariosHtml);
        htmlTemplate = htmlTemplate.replace("${productos_mas_vendidos}", productosMasVendidosHtml);

        return htmlTemplate;
    }

    //SLIDERS
    public static String generarHtmlSliderCliente(TreeSet<Slider> slidersActivos) throws SQLException {
        if (slidersActivos.isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder();
        html.append("<section>\n");
        html.append("<div id=\"carouselExampleIndicators\" class=\"carousel slide\" data-bs-ride=\"carousel\" data-bs-interval=\"4000\">\n");

        html.append("<div class=\"carousel-indicators \">\n");
        int sliderCount = slidersActivos.size();
        for (int i = 0; i < sliderCount; i++) {
            html.append("<button type=\"button\" data-bs-target=\"#carouselExampleIndicators\" ")
                    .append("data-bs-slide-to=\"").append(i).append("\" ")
                    .append(i == 0 ? "class=\"active\" aria-current=\"true\" " : "")
                    .append("aria-label=\"Slide ").append(i + 1).append("\"></button>\n");
        }
        html.append("</div>\n");

        html.append("<div class=\"carousel-inner\">\n");

        VarianteService varianteService = new VarianteService();
        ProductoService productoService = new ProductoService();
        CategoriaService categoriaService = new CategoriaService();

        int index = 0;
        for (Slider slider : slidersActivos) {
            html.append(generarSlideHtml(slider, index, varianteService, productoService, categoriaService));
            index++;
        }

        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</section>");

        return html.toString();
    }

    private static String generarSlideHtml(Slider slider, int index,
                                           VarianteService varianteService,
                                           ProductoService productoService,
                                           CategoriaService categoriaService) throws SQLException {
        StringBuilder slideHtml = new StringBuilder();

        String activeClass = index == 0 ? "active" : "";

        slideHtml.append("<div class=\"carousel-item ").append(activeClass).append("\">\n");
        slideHtml.append("<div class=\"container-fluid\">\n");
        slideHtml.append("<div class=\"row text-start d-flex align-items-center justify-content-center\">\n");

        String imageSrc = slider.getImagen().startsWith("http") ? slider.getImagen() : "/" + slider.getImagen();
        slideHtml.append("<div class=\"col-md-5 order-2 d-flex justify-content-center p-lg-5\">\n");
        slideHtml.append("<div class=\"image-container\">\n");
        slideHtml.append("<img src=\"").append(imageSrc).append("\" alt=\"Slider Image\" class=\"img-fluid rounded\">\n");
        slideHtml.append("</div>\n");
        slideHtml.append("</div>\n");
        slideHtml.append("<div class=\"col-md-5 text-white order-1 texto\">\n");
        slideHtml.append("<h1 class=\"titulo-promo\">").append(slider.getTitulo()).append("</h1>\n");
        slideHtml.append("<h2 class=\"content-promo mt-5\">").append(slider.getEslogan()).append("</h2>\n");

        String enlace = SliderService.generarEnlaceVerMas(slider, varianteService, productoService, categoriaService);
        slideHtml.append("<a href=\"").append(enlace).append("\" ")
                .append("class=\"btn btn-link text-white text-decoration-none d-flex align-items-center mt-5\">\n");
        slideHtml.append("Ver más <i class=\"bi bi-chevron-right ms-2\"></i>\n");
        slideHtml.append("</a>\n");

        slideHtml.append("</div>\n");
        slideHtml.append("</div>\n");
        slideHtml.append("</div>\n");
        slideHtml.append("</div>\n");

        return slideHtml.toString();
    }

    //COMENTARIOS
    public static String generarHtmlComentariosCliente(TreeSet<Comentario> comentariosActivos,
                                                       ClienteService clienteService) {
        if (comentariosActivos.isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder();
        html.append("<section class=\"comentarios-slider mt-5\">\n");
        html.append("<h1 class=\"text-center mb-5\">Nuestros clientes hablan</h1>\n");
        html.append("<div id=\"carouselComentarios\" class=\"carousel slide\" data-bs-ride=\"carousel\" data-bs-interval=\"5000\">\n");
        html.append("<div class=\"carousel-inner\" style=\"background-image: url('../IMG/fondo-borroso.png');\">\n");

        int index = 0;
        for (Comentario comentario : comentariosActivos) {
            Cliente cliente = clienteService.obtenerClientePorId(comentario.getIdCliente());
            html.append(generarSlideComentarioHtml(comentario, cliente, index == 0));
            index++;
        }

        html.append("</div>\n");
        html.append(generarControlesComentarios());
        html.append("</div>\n");
        html.append("</section>\n");

        return html.toString();
    }

    private static String generarSlideComentarioHtml(Comentario comentario, Cliente cliente, boolean isFirst) {
        StringBuilder slideHtml = new StringBuilder();
        String activeClass = isFirst ? "active" : "";

        slideHtml.append("<div class=\"carousel-item ").append(activeClass).append(" carrusel-comentarios text-white text-center justify-content-center\">\n");
        slideHtml.append("<div class=\"container flex-column d-flex justify-content-center align-items-center h-100\">\n");
        slideHtml.append("<div class=\"d-flex flex-column w-75\">\n");

        slideHtml.append("<div class=\"comentario mb-1 text-justify\">\n");
        slideHtml.append("<h2 class=\"texto-comentario\">\"").append(comentario.getComentario()).append("\"</h2>\n");
        slideHtml.append("</div>\n");

        slideHtml.append("<div class=\"autor mt-3\">\n");
        slideHtml.append("<hr class=\"linea-autor mx-auto\">\n");
        slideHtml.append("<p class=\"nombre-autor fs-4\">")
                .append(cliente != null ? cliente.getNombre() : "Cliente Anónimo")
                .append("</p>\n");
        slideHtml.append("</div>\n");

        slideHtml.append("</div>\n");
        slideHtml.append("</div>\n");
        slideHtml.append("</div>\n");

        return slideHtml.toString();
    }

    private static String generarControlesComentarios() {
        return "<button class=\"carousel-control-prev\" type=\"button\" data-bs-target=\"#carouselComentarios\" data-bs-slide=\"prev\">\n" +
                "    <span class=\"carousel-control-prev-icon\" aria-hidden=\"true\"></span>\n" +
                "</button>\n" +
                "<button class=\"carousel-control-next\" type=\"button\" data-bs-target=\"#carouselComentarios\" data-bs-slide=\"next\">\n" +
                "    <span class=\"carousel-control-next-icon\" aria-hidden=\"true\"></span>\n" +
                "</button>\n";
    }

    public static String generarHtmlProductosMasVendidos(ProductoService productoService,
                                                         VarianteService varianteService) throws SQLException {
        List<Producto> productosMasVendidos = productoService.obtenerProductosMasVendidosDeMes();

        if (productosMasVendidos.isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder();
        html.append("<section>\n");
        html.append("    <h1 class=\"text-center mt-5\">Productos mas vendidos del mes</h1>\n");
        html.append("    <div class=\"row justify-content-center text-center mx-3 mt-5 mb-4\">\n");

        int maxProductos = Math.min(4, productosMasVendidos.size());
        for (int i = 0; i < maxProductos; i++) {
            Producto producto = productosMasVendidos.get(i);
            Variante varianteDisponible = varianteService.encontrarVarianteConStock(producto);

            if (varianteDisponible != null) {
                html.append(generarTarjetaProductoMasVendido(producto, varianteDisponible));
            }
        }

        // "Ver más" button
        html.append("        <div class=\"d-flex justify-content-center \">\n");
        html.append("            <a href=\"productos\" class=\"btn btn-ver-mas border-0 btn-primary btn-lg\">Ver más</a>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</section>");

        return html.toString();
    }

    private static String generarTarjetaProductoMasVendido(Producto producto, Variante variante) {
        StringBuilder tarjeta = new StringBuilder();
        tarjeta.append("        <div class=\"col-lg-3 col-md-6 col-sm-6 mb-5\">\n");
        tarjeta.append("            <div class=\"producto p-4 h-100\">\n");
        tarjeta.append("                <div class=\"imagen-producto d-flex justify-content-center align-items-center\">\n");
        tarjeta.append("                    <a href=\"detalle-producto?id=").append(producto.getIdProducto())
                .append("&variantId=").append(variante.getIdVariante()).append("\">\n");
        tarjeta.append("                        <img class=\"img-fluid\" src=\"").append(variante.getImagen())
                .append("\" alt=\"").append(producto.getNombre()).append("\">\n");
        tarjeta.append("                    </a>\n");
        tarjeta.append("                </div>\n");
        tarjeta.append("                <h4 class=\"mt-4\">").append(producto.getNombre()).append("</h4>\n");
        tarjeta.append("                <p class=\"mt-4 text-danger\">$").append(variante.getPrecio()).append("</p>\n");
        tarjeta.append("                <div class=\"d-flex justify-content-center\">\n");
        tarjeta.append("                    <a href=\"detalle-producto?id=").append(producto.getIdProducto())
                .append("&variantId=").append(variante.getIdVariante()).append("\"\n")
                .append("                       class=\"btn btn-producto text-decoration-none d-flex align-items-center justify-content-center\">\n");
        tarjeta.append("                        Ver producto <i class=\"bi bi-chevron-right ms-2\"></i>\n");
        tarjeta.append("                    </a>\n");
        tarjeta.append("                </div>\n");
        tarjeta.append("            </div>\n");
        tarjeta.append("        </div>\n");

        return tarjeta.toString();
    }
}
