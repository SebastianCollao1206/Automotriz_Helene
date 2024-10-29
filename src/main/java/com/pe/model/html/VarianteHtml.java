package com.pe.model.html;

import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.service.VarianteService;

import java.sql.SQLException;
import java.util.TreeSet;

public class VarianteHtml {

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
                        .append("<form action='/variante/producto' method='POST'>")
                        .append("<div class='d-flex align-items-center justify-content-center'>")
                        .append("<button class='btn btn-outline-secondary btn-sm' type='button' onclick='adjustStock(this, -1)'>-</button>")
                        .append("<input type='number' name='stock' value='").append(variante.getStock()).append("' style='width: 60px;' min='0' required>")
                        .append("<input type='hidden' name='idVariante' value='").append(variante.getIdVariante()).append("'>")
                        .append("<button class='btn btn-outline-secondary btn-sm' type='button' onclick='adjustStock(this, 1)'>+</button>")
                        .append("<button class='btn btn-warning btn-sm m-1' type='submit'>Actualizar Stock</button>")
                        .append("</div>")
                        .append("</form>")
                        .append("</td>")
                        .append("<td>").append(variante.getCantidad()).append(" ").append(tamanioTexto).append("</td>")
                        .append("<td>")
                        .append("<button class='btn btn-warning btn-sm m-1 m-lg-1'><i class='bi bi-pencil'></i></button>")
                        .append("<button class='btn btn-success btn-sm m-1 m-lg-1'><i class='bi bi-arrow-clockwise'></i></button>")
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
