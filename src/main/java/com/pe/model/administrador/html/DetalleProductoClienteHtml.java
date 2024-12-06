package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.service.ProductosRelacionadosService;
import com.pe.model.administrador.service.TamanioService;
import com.pe.model.administrador.service.VarianteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class DetalleProductoClienteHtml {

    public static String generarHtmlCompleto(Producto producto, Variante varianteSeleccionada,
                                             List<Variante> variantesDisponibles,
                                             TamanioService tamanioService,
                                             ProductosRelacionadosService relacionadosService,
                                             VarianteService varianteService) throws SQLException, IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/detalle-producto.html")));

        htmlTemplate = htmlTemplate.replace("${imagenVariante}", varianteSeleccionada.getImagen());
        htmlTemplate = htmlTemplate.replace("${nombreProducto}", producto.getNombre());
        htmlTemplate = htmlTemplate.replace("${descripcionCorta}", generarDescripcionCorta(producto.getDescripcion()));
        htmlTemplate = htmlTemplate.replace("${descripcionCompleta}", producto.getDescripcion());
        htmlTemplate = htmlTemplate.replace("${botonesContenido}", generarBotonesContenido(variantesDisponibles, varianteSeleccionada, tamanioService));
        htmlTemplate = htmlTemplate.replace("${precioVariante}", varianteSeleccionada.getPrecio().toString());
        htmlTemplate = htmlTemplate.replace("${idProducto}", String.valueOf(producto.getIdProducto()));
        htmlTemplate = htmlTemplate.replace("${idVariante}", String.valueOf(varianteSeleccionada.getIdVariante()));

        List<Producto> productosRelacionados = relacionadosService.obtenerProductosRelacionados(
                producto.getIdCategoria(), producto.getIdProducto());
        htmlTemplate = htmlTemplate.replace("${productosRelacionados}",
                generarProductosRelacionados(productosRelacionados, varianteService, relacionadosService));

        return htmlTemplate;
    }

    private static String generarDescripcionCorta(String descripcionCompleta) {
        if (descripcionCompleta.length() > 200) {
            int lastSpace = descripcionCompleta.substring(0, 200).lastIndexOf(' ');
            return descripcionCompleta.substring(0, lastSpace) + "...";
        }
        return descripcionCompleta;
    }

    private static String generarBotonesContenido(List<Variante> variantes,
                                                  Variante varianteSeleccionada,
                                                  TamanioService tamanioService) throws SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"btn-group gap-2 mt-2\" role=\"group\" aria-label=\"Lista de opciones\">\n");

        for (Variante variante : variantes) {
            if (variante.getStock() > 0) {
                Tamanio tamanio = tamanioService.obtenerTamanioPorId(variante.getIdTamanio());
                boolean isSelected = variante.getIdVariante() == varianteSeleccionada.getIdVariante();
                String selectedClass = isSelected ? " btn-selected" : "";

                html.append(String.format(
                        "<a href=\"?id=%d&variantId=%d\" " +
                                "class=\"btn btn-contenido mb-2%s\" " +
                                "data-variante-id=\"%d\" " +
                                "data-imagen=\"%s\" " +
                                "data-precio=\"%s\">" +
                                "%d %s" +
                                "</a>\n",
                        varianteSeleccionada.getIdProducto(),
                        variante.getIdVariante(),
                        selectedClass,
                        variante.getIdVariante(),
                        variante.getImagen(),
                        variante.getPrecio(),
                        variante.getCantidad(),
                        tamanio.getUnidadMedida()
                ));
            }
        }

        html.append("</div>");
        return html.toString();
    }

    private static String generarProductosRelacionados(List<Producto> productosRelacionados,
                                                       VarianteService varianteService,
                                                       ProductosRelacionadosService relacionadosService) throws SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<section class=\"productos-relacionados p-5\">\n");
        html.append("    <h2 class=\"text-center mb-3\">Productos Relacionados</h2>\n");
        html.append("    <div class=\"swiper productosSwiper p-5 text-center\">\n");
        html.append("        <div class=\"swiper-wrapper\">\n");

        for (Producto producto : productosRelacionados) {
            Variante variante = relacionadosService.obtenerPrimeraVarianteDisponible(producto);
            if (variante != null) {
                html.append("            <div class=\"swiper-slide\">\n");
                html.append("                <div class=\"producto p-4\">\n");
                html.append("                    <div class=\"imagen-producto\">\n");
                html.append("                        <a href=\"detalle-producto?id=").append(producto.getIdProducto())
                        .append("&variantId=").append(variante.getIdVariante()).append("\">\n");
                html.append("                            <img class=\"img-fluid\" src=\"").append(variante.getImagen())
                        .append("\" alt=\"").append(producto.getNombre()).append("\">\n");
                html.append("                        </a>\n");
                html.append("                    </div>\n");
                html.append("                    <h4 class=\"producto-nombre\">").append(producto.getNombre()).append("</h4>\n");
                html.append("                    <p class=\"mt-4 text-danger producto-precio\">$").append(variante.getPrecio()).append("</p>\n");
                html.append("                    <div class=\"d-flex justify-content-center\">\n");
                html.append("                        <a href=\"detalle-producto?id=").append(producto.getIdProducto())
                        .append("&variantId=").append(variante.getIdVariante())
                        .append("\" class=\"btn btn-productos text-decoration-none d-flex align-items-center justify-content-center\">\n");
                html.append("                            Ver producto <i class=\"bi bi-chevron-right ms-2\"></i>\n");
                html.append("                        </a>\n");
                html.append("                    </div>\n");
                html.append("                </div>\n");
                html.append("            </div>\n");
            }
        }

        html.append("        </div>\n");
        html.append("        <div class=\"swiper-button-next\"></div>\n");
        html.append("        <div class=\"swiper-button-prev\"></div>\n");
        html.append("        <div class=\"swiper-pagination\"></div>\n");
        html.append("    </div>\n");
        html.append("</section>");

        return html.toString();
    }
}
