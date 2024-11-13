package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.service.VarianteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

public class VarianteHtml {

    public static String generarMensajeAlerta(String mensaje, String redireccion) {
        String mensajeEscapado = mensaje.replace("'", "\\'").replace("\"", "\\\"");

        StringBuilder html = new StringBuilder();
        html.append("<script type='text/javascript'>");
        html.append("    alert('").append(mensajeEscapado).append("');");
        html.append("    window.location='").append(redireccion).append("';");
        html.append("</script>");
        return html.toString();
    }

    public static String generarScriptConfirmacion() {
        return """
        <script>
            function confirmarActualizacion(idVariante) {
                if (confirm('¿Estás seguro de que deseas actualizar el stock de este producto?')) {
                    var form = document.getElementById('form-actualizar-stock-' + idVariante);
                    var stockInput = form.previousElementSibling.querySelector('input[name="stock"]');
                    // Actualiza el valor del campo oculto con el valor actual del stock
                    form.querySelector('input[name="stock"]').value = stockInput.value;
                    form.submit();
                }
            }
        </script>
        """;
    }

    public static String generarHtmlVariantes(TreeSet<Variante> variantes, VarianteService varianteService, String nombreProducto) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/variante_producto.html")));
        String tableRows;
        if (variantes.isEmpty()) {
            tableRows = "<tr><td colspan='6'>No se encontraron variantes para este producto.</td></tr>";
        } else {
            tableRows = generarFilasTablaVariantes(variantes, varianteService);
        }
        htmlTemplate = htmlTemplate.replace("${tableRows}", tableRows);
        htmlTemplate = htmlTemplate.replace("${nombreProducto}", nombreProducto != null ? nombreProducto : "Producto no encontrado");
        htmlTemplate = htmlTemplate.replace("${scriptConfirmacionActualizacion}", generarScriptConfirmacion());

        return htmlTemplate;
    }

    public static String generarHtmlEdicionVariante(Variante variante, String nombreProducto, TreeSet<Tamanio> tamanios) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_variante.html")));

        String opcionesTamanios = TamanioHtml.generarOpcionesTamanios2(tamanios, variante.getIdTamanio());

        htmlTemplate = htmlTemplate.replace("${variante.id}", String.valueOf(variante.getIdVariante()));
        htmlTemplate = htmlTemplate.replace("${variante.codigo}", variante.getCodigo());
        htmlTemplate = htmlTemplate.replace("${variante.precio}", String.valueOf(variante.getPrecio()));
        htmlTemplate = htmlTemplate.replace("${variante.imagen}", variante.getImagen());
        htmlTemplate = htmlTemplate.replace("${variante.stock}", String.valueOf(variante.getStock()));
        htmlTemplate = htmlTemplate.replace("${variante.cantidad}", String.valueOf(variante.getCantidad()));
        htmlTemplate = htmlTemplate.replace("${selectedProduct}", nombreProducto);
        htmlTemplate = htmlTemplate.replace("${productoId}", String.valueOf(variante.getIdProducto()));
        htmlTemplate = htmlTemplate.replace("${selectedTamanio}", opcionesTamanios);

        return htmlTemplate;
    }

    public static String generarHtmlAgregarVariante(TreeSet<Tamanio> tamaniosActivos, String productoNombre, String productoId, String mensajeError) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/agregar_variante.html")));

        String opcionesTamanios = TamanioHtml.generarOpcionesTamanios(tamaniosActivos);
        htmlTemplate = htmlTemplate.replace("${tamaniosOptions}", opcionesTamanios);

        if (productoNombre != null) {
            htmlTemplate = htmlTemplate.replace("${selectedProduct}", productoNombre);
            htmlTemplate = htmlTemplate.replace("${productoId}", productoId);
        } else if (mensajeError != null) {
            htmlTemplate = htmlTemplate.replace("${selectedProduct}", mensajeError);
        } else {
            htmlTemplate = htmlTemplate.replace("${selectedProduct}", "");
        }
        return htmlTemplate;
    }

    public static String generarFilasTablaVariantes(TreeSet<Variante> variantes, VarianteService varianteService) {
        StringBuilder filas = new StringBuilder();
        for (Variante variante : variantes) {
            try {
                Tamanio tamanio = varianteService.obtenerTamanioPorId(variante.getIdTamanio());
                String tamanioTexto = tamanio != null ? tamanio.getUnidadMedida() : "Desconocido";

                filas.append("<tr>")
                        .append("<td>").append(variante.getCodigo()).append("</td>")
                        .append("<td><img src='").append(variante.getImagen()).append("' alt='").append(variante.getCodigo()).append("' class='border-0 img-thumbnail' style='width: 50px;'></td>")
                        .append("<td>$").append(variante.getPrecio()).append("</td>")
                        .append("<td>")
                        .append("<div class='d-flex align-items-center justify-content-center'>")
                        .append("<form action='/variante/producto' method='POST' style='display:inline;'>")
                        .append("<button class='btn btn-outline-secondary btn-sm' type='button' onclick='adjustStock(this, -1)'>-</button>")
                        .append("<input type='number' name='stock' value='").append(variante.getStock()).append("' style='width: 60px;' min='0' required>")
                        .append("<input type='hidden' name='idVariante' value='").append(variante.getIdVariante()).append("'>")
                        .append("<button class='btn btn-outline-secondary btn-sm' type='button' onclick='adjustStock(this, 1)'>+</button>")
                        .append("</form>")
                        .append("<form id='form-actualizar-stock-").append(variante.getIdVariante()).append("' action='/variante/actualizarStock' method='POST' style='display:inline;'>")
                        .append("<input type='hidden' name='idVariante' value='").append(variante.getIdVariante()).append("'>")
                        .append("<input type='hidden' name='stock' id='hidden-stock-").append(variante.getIdVariante()).append("' value='").append(variante.getStock()).append("'>")
                        .append("<button type='button' class='btn btn-success btn-sm m-1' onclick='confirmarActualizacion(").append(variante.getIdVariante()).append(")'><i class='bi bi-arrow-clockwise'></i></button>")
                        .append("</form>")
                        .append("</div>")
                        .append("</td>")
                        .append("<td>").append(variante.getCantidad()).append(" ").append(tamanioTexto).append("</td>")
                        .append("<td>")
                        .append("<form action='/variante/editar' method='GET' style='display:inline;'>")
                        .append("<input type='hidden' name='id' value='").append(variante.getIdVariante()).append("'>")
                        .append("<button class='btn btn-warning btn-sm m-1 m-lg-1' type='submit'><i class='bi bi-pencil'></i></button>")
                        .append("</form>")
                        .append("</td>")
                        .append("</tr>");
            } catch (SQLException e) {
                e.printStackTrace();
                filas.append("<tr><td colspan='6'>Error al cargar tamaño</td></tr>");
            }
        }
        return filas.toString();
    }
}
