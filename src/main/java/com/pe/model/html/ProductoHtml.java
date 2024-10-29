package com.pe.model.html;

import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Tamanio;
import com.pe.model.service.ProductoService;

import java.sql.SQLException;
import java.util.TreeSet;

public class ProductoHtml {

    public static String generarMensajeAlerta(String mensaje, String redireccion) {
        StringBuilder html = new StringBuilder();
        html.append("<script type='text/javascript'>");
        html.append("    alert('").append(mensaje).append("');");
        html.append("    window.location='").append(redireccion).append("';");
        html.append("</script>");
        return html.toString();
    }

    /**
     * Genera los campos HTML para las variantes de producto
     * @param numVariantes número de variantes a generar
     * @param tamanios conjunto de tamaños disponibles para las variantes
     * @return String con el HTML generado
     */
    public static String generarCamposVariantes(int numVariantes, TreeSet<Tamanio> tamanios) {
        StringBuilder html = new StringBuilder();

        for (int i = 1; i <= numVariantes; i++) {
            html.append("<div class='variant mb-4 mt-5'>\n")
                    .append("    <h4 class='mb-3 text-center'>Variante ").append(i).append("</h4>\n")
                    .append(generarCampoCodigo(i))
                    .append(generarCampoTamanio(i, tamanios)) // Ahora se pasa un TreeSet<Tamanio>
                    .append(generarCampoPrecio(i))
                    .append(generarCampoImagen(i))
                    .append(generarCampoStock(i))
                    .append(generarCampoCantidad(i))
                    .append("</div>\n");
        }

        return html.toString();
    }

    private static String generarCampoCodigo(int i) {
        return "<div class='row mb-3 d-flex justify-content-center gap-4'>\n" +
                "    <div class='col-md-5'>\n" +
                "        <label for='codigo-" + i + "' class='form-label'>Código</label>\n" +
                "        <input type='text' class='form-control' name='codigo-" + i + "' id='codigo-" + i + "' required>\n" +
                "    </div>\n";
    }

    private static String generarCampoTamanio(int i, TreeSet<Tamanio> tamanios) {
        StringBuilder html = new StringBuilder();
        html.append("    <div class='col-md-5'>\n")
                .append("        <label for='tamaño-").append(i).append("' class='form-label'>Tamaño</label>\n")
                .append("        <select class='form-select' name='id-tamanio-").append(i).append("' id='tamaño-").append(i).append("' required>\n")
                .append(TamanioHtml.generarOpcionesTamanios(tamanios))
                .append("        </select>\n")
                .append("        <div class='form-text'>\n")
                .append("            <a href='/tamanio/agregar'>¿No existe el tamaño?</a>\n")
                .append("        </div>\n")
                .append("    </div>\n")
                .append("</div>\n");
        return html.toString();
    }

    private static String generarCampoPrecio(int i) {
        return "<div class='row mb-3 d-flex justify-content-center gap-4'>\n" +
                "    <div class='col-md-5'>\n" +
                "        <label for='precio-" + i + "' class='form-label'>Precio</label>\n" +
                "        <input type='number' class='form-control' name='precio-" + i + "' id='precio-" + i + "' required>\n" +
                "    </div>\n";
    }

    private static String generarCampoImagen(int i) {
        return "<div class='col-md-5'>\n" +
                "        <label for='imagen-" + i + "' class='form-label'>Imagen (URL)</label>\n" +
                "        <input type='text' class='form-control' name='imagen-" + i + "' id='imagen-" + i + "' required>\n" +
                "    </div>\n" +
                "</div>\n"; // Cerrar la fila
    }

    private static String generarCampoStock(int i) {
        return "<div class='row mb-3 d-flex justify-content-center gap-4'>\n" +
                "    <div class='col-md-5'>\n" +
                "        <label for='stock-" + i + "' class='form-label'>Stock</label>\n" +
                "        <input type='number' class='form-control' name='stock-" + i + "' id='stock-" + i + "' required>\n" +
                "    </div>\n";
    }

    private static String generarCampoCantidad(int i) {
        return "<div class='col-md-5'>\n" +
                "        <label for='cantidad-" + i + "' class='form-label'>Cantidad</label>\n" +
                "        <input type='number' class='form-control' name='cantidad-" + i + "' id='cantidad-" + i + "' required>\n" +
                "    </div>\n" +
                "</div>\n"; // Cerrar la fila
    }

    //PARA LA TABLA DINAMICA
    public static String generarFilasTablaProductos(TreeSet<Producto> productos, ProductoService productoService) {
        StringBuilder tableRows = new StringBuilder();
        for (Producto producto : productos) {
            tableRows.append("<tr>");
            tableRows.append("<td>").append(producto.getNombre()).append("</td>");
            try {
                String nombreCategoria = productoService.obtenerNombreCategoria(producto.getIdCategoria());
                tableRows.append("<td>").append(nombreCategoria).append("</td>");
            } catch (SQLException e) {
                tableRows.append("<td>Sin categoría</td>");
            }

            // Botón de descripción
            tableRows.append("<td>");
            String descripcionEscapada = producto.getDescripcion()
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
            tableRows.append("<button class='btn btn-info btn-sm' ")
                    .append("data-bs-toggle='modal' ")
                    .append("data-bs-target='#descripcionModal' ")
                    .append("onclick=\"setModalContent('")
                    .append(producto.getNombre().replace("'", "\\'"))
                    .append("', '")
                    .append(descripcionEscapada)
                    .append("')\">Ver descripción</button>");
            tableRows.append("</td>");

            // Botón de variantes
            tableRows.append("<td>");
            tableRows.append("<a href='/variante/producto?id=")
                    .append(producto.getIdProducto())
                    .append("'>");
            tableRows.append("<button class='btn btn-outline-secondary btn-sm'>Ver variantes</button>");
            tableRows.append("</a>");
            tableRows.append("</td>");

            tableRows.append("<td>");
            // Botón de editar
            tableRows.append("<a href='/producto/editar?id=").append(producto.getIdProducto())
                    .append("' class='btn btn-warning btn-sm m-1' title='Modificar'>");
            tableRows.append("<i class='bi bi-pencil'></i>");
            tableRows.append("</a>");

            tableRows.append("</td>");
            tableRows.append("</tr>");
        }
        return tableRows.toString();
    }
}

