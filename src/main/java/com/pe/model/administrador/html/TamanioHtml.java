package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.service.TamanioService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

public class TamanioHtml {

    public static String generarMensajeAlerta(String mensaje, String redireccion) {
        StringBuilder html = new StringBuilder();
        html.append("<script type='text/javascript'>");
        html.append("    alert('").append(mensaje).append("');");
        html.append("    window.location='").append(redireccion).append("';");
        html.append("</script>");
        return html.toString();
    }

    public static String generarScriptConfirmacionEliminacion() {
        return """
        <script>
        function confirmarEliminacion(button) {
            if (confirm('¿Estás seguro de que deseas cambiar el estado de este tamaño a inactivo?')) {
                var form = button.closest('form');
                form.submit();
            }
        }
        </script>
        """;
    }

    public static String generarHtmlTamanios(TreeSet<Tamanio> tamaniosFiltrados, TamanioService tamanioService) {
        StringBuilder html = new StringBuilder();
        try {
            String baseHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_tamanio.html")));
            String tableRows;

            if (tamaniosFiltrados.isEmpty()) {
                tableRows = "<tr><td colspan='4'>No se encontraron tamaños que coincidan.</td></tr>";
            } else {
                tableRows = generarFilasTablaTamanios(tamaniosFiltrados);
            }
            baseHtml = baseHtml.replace("${tableRows}", tableRows);
            baseHtml = baseHtml.replace("${scriptConfirmacionEliminacion}", generarScriptConfirmacionEliminacion());
            html.append(baseHtml);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el HTML base", e);
        }
        return html.toString();
    }

    public static String generarHtmlEdicionTamanio(Tamanio tamanio) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_tamanio.html")));
        htmlTemplate = htmlTemplate.replace("${tamanio.idtamanio}", String.valueOf(tamanio.getIdTamanio()));
        htmlTemplate = htmlTemplate.replace("${tamanio.nombre}", tamanio.getUnidadMedida());
        htmlTemplate = htmlTemplate.replace("${estado.activoSelected}", tamanio.getEstado().name().equals("Activo") ? "selected" : "");
        htmlTemplate = htmlTemplate.replace("${estado.inactivoSelected}", tamanio.getEstado().name().equals("Inactivo") ? "selected" : "");
        return htmlTemplate;
    }

    public static String generarOpcionesTamanios(TreeSet<Tamanio> tamanios) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;'>Selecciona un tamaño</option>");
        for (Tamanio tamanio : tamanios) {
            options.append("<option value=\"").append(tamanio.getIdTamanio()).append("\">").append(tamanio.getUnidadMedida()).append("</option>");
        }
        return options.toString();
    }

    public static String generarOpcionesTamanios2(TreeSet<Tamanio> tamanios, int tamanioSeleccionado) {
        StringBuilder opciones = new StringBuilder();
        for (Tamanio tamanio : tamanios) {
            String selected = (tamanio.getIdTamanio() == tamanioSeleccionado) ? "selected" : "";
            opciones.append(String.format("<option value=\"%d\" %s>%s</option>",
                    tamanio.getIdTamanio(), selected, tamanio.getUnidadMedida()));
        }
        return opciones.toString();
    }

    public static String generarFilasTablaTamanios(TreeSet<Tamanio> tamanios) {
        StringBuilder tableRows = new StringBuilder();
        for (Tamanio tamanio : tamanios) {
            tableRows.append("<tr>");
            tableRows.append("<td>").append(tamanio.getIdTamanio()).append("</td>");
            tableRows.append("<td>").append(tamanio.getUnidadMedida()).append("</td>");
            tableRows.append("<td>").append(tamanio.getEstado().name()).append("</td>");
            tableRows.append("<td>");

            // Botón de editar
            tableRows.append("<a href='/tamanio/editar?id=").append(tamanio.getIdTamanio())
                    .append("' class='btn btn-warning btn-sm m-1' title='Modificar'>");
            tableRows.append("<i class='bi bi-pencil'></i>");
            tableRows.append("</a>");

            // Botón de eliminar
            tableRows.append("<form action='/tamanio/eliminar' method='POST' style='display:inline;'>");
            tableRows.append("<input type='hidden' name='id' value='").append(tamanio.getIdTamanio()).append("'/>");
            tableRows.append("<button type='button' class='btn btn-danger btn-sm m-1' onclick='confirmarEliminacion(this)' title='Eliminar'>");
            tableRows.append("<i class='bi bi-trash'></i>");
            tableRows.append("</button>");
            tableRows.append("</form>");

            tableRows.append("</td>");
            tableRows.append("</tr>");
        }
        return tableRows.toString();
    }
}
