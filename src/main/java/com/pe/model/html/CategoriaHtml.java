package com.pe.model.html;

import com.pe.model.entidad.Categoria;

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

    /**
     * Generar filas de la tabla de categorías
     * @param categorias
     * @return
     */
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
