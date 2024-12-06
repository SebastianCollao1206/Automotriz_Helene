package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.MetodoPago;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

public class CompraHtml {

    public static String generarHtmlAgregarMetodoPago(TreeSet<MetodoPago> metodosPagoActivos, double totalCarrito) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/compra.html")));

        String opcionesMetodosPago = MetodoPagoHtml.generarOpcionesMetodosPago(metodosPagoActivos);
        htmlTemplate = htmlTemplate.replace("${metodosPagoOptions}", opcionesMetodosPago);

      //  htmlTemplate = htmlTemplate.replace("${clienteCorreo}", clienteCorreo != null ? clienteCorreo : "");

        // Reemplaza el marcador de posición con el total del carrito
        htmlTemplate = htmlTemplate.replace("${totalCarrito}", String.format("%.2f", totalCarrito));

        return htmlTemplate;
    }

    public static String generarHtmlLlenadoDatos(String clienteCorreo) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/llenado_datos.html")));
        htmlTemplate = htmlTemplate.replace("${clienteCorreo}", clienteCorreo != null ? clienteCorreo : "");

        return htmlTemplate;
    }
}
