package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Categoria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

public class CategoriaHtml {

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
            if (confirm('¿Estás seguro de que deseas cambiar el estado de esta categoría a inactiva?')) {
                var form = button.closest('form');
                form.submit();
            }
        }
        </script>
        """;
    }

    public static String generarOpcionesCategorias(TreeSet<Categoria> categorias) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;'>Selecciona una categoría</option>");
        for (Categoria categoria : categorias) {
            options.append("<option value=\"").append(categoria.getIdCategoria()).append("\">").append(categoria.getNombre()).append("</option>");
        }
        return options.toString();
    }

    public static String generarHtmlCategorias(TreeSet<Categoria> categoriasFiltradas) {
        StringBuilder html = new StringBuilder();
        try {
            String baseHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_categoria.html")));
            String tableRows;

            if (categoriasFiltradas.isEmpty()) {
                tableRows = "<tr><td colspan='4'>No se encontraron categorías que coincidan.</td></tr>";
            } else {
                tableRows = generarFilasTablaCategorias(categoriasFiltradas);
            }
            baseHtml = baseHtml.replace("${tableRows}", tableRows);
            baseHtml = baseHtml.replace("${scriptConfirmacionEliminacion}", generarScriptConfirmacionEliminacion());
            html.append(baseHtml);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el HTML base", e);
        }
        return html.toString();
    }

    public static String generarHtmlEdicionCategoria(Categoria categoria) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_categoria.html")));
        htmlTemplate = htmlTemplate.replace("${categoria.idCategoria}", String.valueOf(categoria.getIdCategoria()));
        htmlTemplate = htmlTemplate.replace("${categoria.nombre}", categoria.getNombre());
        htmlTemplate = htmlTemplate.replace("${categoria.estado}", categoria.getEstado().name());
        htmlTemplate = htmlTemplate.replace("${estado.activoSelected}", categoria.getEstado().name().equals("Activo") ? "selected" : "");
        htmlTemplate = htmlTemplate.replace("${estado.inactivoSelected}", categoria.getEstado().name().equals("Inactivo") ? "selected" : "");
        return htmlTemplate;
    }

    public static String generarOpcionesCategorias2(TreeSet<Categoria> categorias, int categoriaSeleccionada) {
        StringBuilder opciones = new StringBuilder();
        for (Categoria categoria : categorias) {
            String selected = (categoria.getIdCategoria() == categoriaSeleccionada) ? "selected" : "";
            opciones.append(String.format("<option value=\"%d\" %s>%s</option>",
                    categoria.getIdCategoria(), selected, categoria.getNombre()));
        }
        return opciones.toString();
    }

    public static String generarFilasTablaCategorias(TreeSet<Categoria> categorias) {
        StringBuilder tableRows = new StringBuilder();
        for (Categoria categoria : categorias) {
            tableRows.append("<tr>");
            tableRows.append("<td>").append(categoria.getIdCategoria()).append("</td>");
            tableRows.append("<td>").append(categoria.getNombre()).append("</td>");
            tableRows.append("<td>").append(categoria.getEstado().name()).append("</td>");
            tableRows.append("<td>");

            // Botón de editar
            tableRows.append("<a href='/categoria/editar?id=").append(categoria.getIdCategoria())
                    .append("' class='btn btn-warning btn-sm m-1' title='Modificar'>");
            tableRows.append("<i class='bi bi-pencil'></i>");
            tableRows.append("</a>");

            // Botón de eliminar
            tableRows.append("<form action='/categoria/eliminar' method='POST' style='display:inline;'>");
            tableRows.append("<input type='hidden' name='id' value='").append(categoria.getIdCategoria()).append("'/>");
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
