package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.MetodoPago;

import java.util.TreeSet;

public class MetodoPagoHtml {

    public static String generarOpcionesMetodosPago(TreeSet<MetodoPago> metodosPago) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;'>Seleccione un método de pago</option>");
        for (MetodoPago metodoPago : metodosPago) {
            options.append("<option value=\"").append(metodoPago.getIdMetodoPago()).append("\">")
                    .append(metodoPago.getNombre()).append("</option>");
        }
        return options.toString();
    }
}
