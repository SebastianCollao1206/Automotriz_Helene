package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.service.CategoriaService;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.SliderService;
import com.pe.model.administrador.service.VarianteService;

import java.sql.SQLException;
import java.util.TreeSet;

public class IndexHtml {

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
}
