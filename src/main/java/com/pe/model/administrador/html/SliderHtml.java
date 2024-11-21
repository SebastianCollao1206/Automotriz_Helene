package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.MensajeConfirmacion;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.service.SliderService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

public class SliderHtml {

    public static String generarHtmlAgregarSlider(String opcionSeleccionada, String idOpcionSeleccionada, String relacionarCon, String mensajeError) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/agregar_slider.html")));

        if (mensajeError != null) {
            htmlTemplate = htmlTemplate.replace("${selectedOption}", mensajeError);
        } else {
            htmlTemplate = htmlTemplate.replace("${selectedOption}", opcionSeleccionada != null ? opcionSeleccionada : "");
        }
        htmlTemplate = htmlTemplate.replace("${selectedOptionId}", idOpcionSeleccionada != null ? idOpcionSeleccionada : "");

        if (relacionarCon != null) {
            switch (relacionarCon) {
                case "variante":
                    htmlTemplate = htmlTemplate.replace("value=\"variante\"", "value=\"variante\" selected");
                    break;
                case "producto":
                    htmlTemplate = htmlTemplate.replace("value=\"producto\"", "value=\"producto\" selected");
                    break;
                case "categoria":
                    htmlTemplate = htmlTemplate.replace("value=\"categoria\"", "value=\"categoria\" selected");
                    break;
            }
        }

        return htmlTemplate;
    }

    public static String generarHtmlSliders(TreeSet<Slider> sliders, SliderService sliderService) {
        StringBuilder html = new StringBuilder();
        try {
            String baseHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_slider.html")));

            String slidersHtml;
            if (sliders.isEmpty()) {
                slidersHtml = "<div class=\"alert alert-warning\">No se encontraron sliders que coincidan.</div>";
            } else {
                slidersHtml = generarFilasSliders(sliders);
            }

            baseHtml = baseHtml.replace("${sliders}", slidersHtml);
            baseHtml = baseHtml.replace("${estadosSliderOptions}", generarOpcionesEstadoSlider(sliderService.getEstadosSliderSet()));
            baseHtml = baseHtml.replace("${tiposSliderOptions}", generarOpcionesTipoSlider());

            html.append(baseHtml);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el HTML base", e);
        }
        return html.toString();
    }

    public static String generarOpcionesEstadoSlider(TreeSet<String> estadosSlider) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;' selected>Seleccionar estado</option>");
        for (String estado : estadosSlider) {
            options.append("<option value=\"").append(estado).append("\">").append(estado).append("</option>");
        }
        return options.toString();
    }

    public static String generarOpcionesTipoSlider() {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;' selected>Seleccionar</option>");
        options.append("<option value=\"categoria\">Categoría</option>");
        options.append("<option value=\"producto\">Producto</option>");
        options.append("<option value=\"variante\">Variante</option>");
        return options.toString();
    }

    public static String generarHtmlEdicionSlider(Slider slider) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_slider.html")));

        String imagenSrc = slider.getImagen();
        String imagenPath = imagenSrc.startsWith("http") ? imagenSrc : "/" + imagenSrc;

        htmlTemplate = htmlTemplate.replace("${slider.idSlider}", String.valueOf(slider.getIdSlider()));
        htmlTemplate = htmlTemplate.replace("${slider.titulo}", slider.getTitulo());
        htmlTemplate = htmlTemplate.replace("${slider.eslogan}", slider.getEslogan());
        htmlTemplate = htmlTemplate.replace("${slider.imagen}", imagenPath);
        htmlTemplate = htmlTemplate.replace("${slider.fechaInicio}", slider.getFechaInicio().toString());
        htmlTemplate = htmlTemplate.replace("${slider.fechaFin}", slider.getFechaFin().toString());

        htmlTemplate = htmlTemplate.replace("${estado.activoSelected}", slider.getEstado().name().equals("Activo") ? "selected" : "");
        htmlTemplate = htmlTemplate.replace("${estado.inactivoSelected}", slider.getEstado().name().equals("Inactivo") ? "selected" : "");


        return htmlTemplate;
    }

    public static String generarFilasSliders(TreeSet<Slider> sliders) {
        StringBuilder html = new StringBuilder();

        for (Slider slider : sliders) {
            html.append("<div class=\"card mb-5 shadow-sm\">");
            html.append("<div class=\"row g-0 align-items-center p-3 d-flex justify-content-center\">");

            html.append("<div class=\"col-md-2 d-flex justify-content-center\">");
            String imagenSrc = slider.getImagen();

            if (imagenSrc.startsWith("http://") || imagenSrc.startsWith("https://")) {
                html.append("<img src=\"").append(imagenSrc).append("\" alt=\"Slider\" class=\"img-fluid rounded\" style=\"height: 150px;\">");
            } else {
                html.append("<img src=\"/").append(imagenSrc).append("\" alt=\"Slider\" class=\"img-fluid rounded\" style=\"height: 150px;\">");
            }
            html.append("</div>");

            html.append("<div class=\"col-md-9\">");
            html.append("<div class=\"card-body\">");

            html.append("<div class=\"d-flex justify-content-between align-items-start\">");
            html.append("<h5 class=\"card-title mb-4\">").append(slider.getTitulo()).append("</h5>");
            html.append("<span class=\"badge ").append(slider.getEstado() == Slider.EstadoSlider.Activo ? "bg-success" : "bg-danger").append(" rounded-pill\">")
                    .append(slider.getEstado().name()).append("</span>");
            html.append("</div>");

            html.append("<p class=\"card-text text-muted mb-4\">").append(slider.getEslogan()).append("</p>");

            html.append("<div class=\"d-flex gap-4 text-muted small\">");
            html.append("<span><i class=\"bi bi-calendar3 me-1\"></i> Inicio: ").append(slider.getFechaInicio()).append("</span>");
            html.append("<span><i class=\"bi bi-calendar3 me-1\"></i> Fin: ").append(slider.getFechaFin()).append("</span>");
            html.append("<span><i class=\"bi bi-tag me-1\"></i> Tipo: ").append(slider.getRelacionarCon()).append("</span>");
            html.append("</div>");

            html.append("<div class=\"d-flex justify-content-end gap-2\">");

            html.append("<a href='/slider/editar?id=").append(slider.getIdSlider())
                    .append("' title='Editar' style='text-decoration:none;'>");
            html.append("<button class=\"btn btn-outline-primary btn-sm\" title=\"Editar\">");
            html.append("<i class=\"bi bi-pencil\"></i>");
            html.append("</button>");
            html.append("</a>");

            html.append("<form method=\"POST\" action=\"/slider/eliminar\" class=\"confirm-form d-inline\">");
            html.append("<input type=\"hidden\" name=\"id\" value=\"").append(slider.getIdSlider()).append("\">");
            html.append("<button type=\"submit\" class=\"btn btn-outline-danger btn-sm\" title=\"Eliminar\">");
            html.append("<i class=\"bi bi-trash\"></i>");
            html.append("</button>");
            html.append("</form>");

            html.append("</div>");

            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div>");
        }
        return html.toString();
    }
}
