package com.pe.model.html;

import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.service.VarianteService;

import java.sql.SQLException;
import java.util.TreeSet;

public class VarianteHtml {

    public static String generarMensajeAlerta(String mensaje, String redireccion) {
        StringBuilder html = new StringBuilder();
        html.append("<script type='text/javascript'>");
        html.append("    alert('").append(mensaje).append("');");
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
                        .append("<form action='/variante/editar' method='GET' style='display:inline;'>") // Cambia a 'GET' para redirigir
                        .append("<input type='hidden' name='id' value='").append(variante.getIdVariante()).append("'>") // Agrega el ID de la variante
                        .append("<button class='btn btn-warning btn-sm m-1 m-lg-1' type='submit'><i class='bi bi-pencil'></i></button>")
                        .append("</form>")
                        .append("</td>")
                        .append("</tr>");
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de excepciones
                // Opcional: agregar un mensaje de error en la fila
                filas.append("<tr><td colspan='6'>Error al cargar tamaño</td></tr>");
            }
        }
        return filas.toString();
    }
}
