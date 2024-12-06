package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.ItemCarrito;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.TamanioService;
import com.pe.model.administrador.service.VarianteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

public class CarritoHtml {

    public static String generarHtmlCarrito(TreeSet<ItemCarrito> carrito,
                                            VarianteService varianteService,
                                            TamanioService tamanioService,
                                            ProductoService productoService) throws IOException {
        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/carrito.html")));

        StringBuilder contenidoCarrito = new StringBuilder();
        double subtotalCarrito = 0.0;

        for (ItemCarrito item : carrito) {
            Variante variante = varianteService.obtenerVariantePorId(item.getIdVariante());
            Producto producto = productoService.obtenerProductoPorId(variante.getIdProducto());
            Tamanio tamanio = tamanioService.obtenerTamanioPorId(variante.getIdTamanio());

            double subtotal = item.getPrecio() * item.getCantidad();
            subtotalCarrito += subtotal;

            contenidoCarrito.append(generarItemCarritoHtml(
                    item, variante, producto, tamanio, subtotal
            ));
        }

        double igv = subtotalCarrito * 0.18;
        double totalCarrito = subtotalCarrito + igv;

        htmlTemplate = htmlTemplate.replace("${contenidoCarrito}", contenidoCarrito.toString());
        htmlTemplate = htmlTemplate.replace("${subtotalCarrito}", String.format("%.2f", subtotalCarrito));
        htmlTemplate = htmlTemplate.replace("${igv}", String.format("%.2f", igv));
        htmlTemplate = htmlTemplate.replace("${totalCarrito}", String.format("%.2f", totalCarrito));
        htmlTemplate = htmlTemplate.replace("${resumenCompra}", generarResumenCompraHtml(subtotalCarrito, igv, totalCarrito));

        return htmlTemplate;
    }

    private static String generarItemCarritoHtml(ItemCarrito item,
                                                 Variante variante,
                                                 Producto producto,
                                                 Tamanio tamanio,
                                                 double subtotal) {
        return String.format(
                "<div class=\"product-container mb-5\">" +
                        "  <div class=\"row\">" +
                        "    <div class=\"col-12 col-md-2 d-flex align-items-center justify-content-end justify-content-md-center\">" +

                        "      <form method=\"post\" action=\"/cliente/carrito/eliminar\" class=\"ajax-form\">" +
                        "        <input type=\"hidden\" name=\"varianteId\" value=\"%d\">" +
                        "        <button type=\"submit\" class=\"btn text-danger border-0 eliminar-item\">" +
                        "          <i class=\"bi bi-trash-fill fs-3\"></i>" +
                        "        </button>" +
                        "      </form>" +

                        "    </div>" +
                        "    <div class=\"col-12 col-md-3 text-center d-flex justify-content-center align-items-center\">" +
                        "      <img src=\"%s\" alt=\"%s\" class=\"img-fluid img-carrito\">" +
                        "    </div>" +
                        "    <div class=\"col-12 col-md-7\">" +
                        "      <h5 class=\"text-center mt-3 nombre-carrito\">%s - %d %s</h5>" +
                        "      <div class=\"row text-center mt-4 precios-carrito\">" +
                        "        <div class=\"col-6\">" +
                        "          <p class=\"text-muted mb-1\">P. unitario</p>" +
                        "          <p class=\"fw-bold\">$%.2f</p>" +
                        "        </div>" +
                        "        <div class=\"col-6\">" +
                        "          <p class=\"text-muted mb-1\">Subtotal</p>" +
                        "          <p class=\"fw-bold\">$%.2f</p>" +
                        "        </div>" +
                        "      </div>" +

                        "      <form method=\"post\" action=\"/cliente/carrito\" class=\"ajax-form\">" +
                        "        <input type=\"hidden\" name=\"varianteId\" value=\"%d\">" +
                        "        <div class=\"cantidad-producto d-flex flex-column align-items-center mt-3\">" +
                        "          <div class=\"d-flex align-items-center gap-3\">" +

                        "            <div class=\"input-group\" style=\"width: 150px;\">" +
                        "              <button class=\"btn btn-outline-secondary btn-decrement\" type=\"button\">-</button>" +

                        "              <input type=\"number\" class=\"form-control text-center cantidad\" " +
                        "                     name=\"cantidad\" value=\"%d\" min=\"1\" id=\"quantity3\">" +

                        "              <button class=\"btn btn-outline-secondary btn-increment\" type=\"button\">+</button>" +
                        "            </div>" +

                        "            <button type=\"submit\" class=\"btn btn-success\">" +
                        "              <i class=\"bi bi-arrow-clockwise\"></i>" +
                        "            </button>" +
                        "          </div>" +
                        "        </div>" +
                        "      </form>" +

                        "    </div>" +
                        "  </div>" +
                        "</div>",
                item.getIdVariante(),
                variante.getImagen(),
                producto.getNombre(),
                producto.getNombre(),
                variante.getCantidad(),
                tamanio.getUnidadMedida(),
                item.getPrecio(),
                subtotal,
                item.getIdVariante(),
                item.getCantidad()
        );
    }

    public static String generarHtmlCarritoVacio() {
        return "<div class='container text-center mt-5 mb-5'>" +
                "  <h2>Tu carrito está vacío</h2>" +
                "  <img src='https://cdn-icons-png.flaticon.com/512/2762/2762885.png' class='img-fluid mt-3 w-25'>" +
                "  <p class='mt-5'>Explora nuestros productos y añade algunos al carrito.</p>" +
                "  <a href='/cliente/productos' class='btn btn-primary mt-3'>Ir a productos</a>" +
                "</div>";
    }

    private static String generarResumenCompraHtml(double subtotal, double igv, double total) {
        return String.format(
                "<div class=\"col-md-4 mb-5\">" +
                        "  <div class=\"product-container text-center sticky-summary\">" +
                        "    <h5 class=\"border-bottom pb-3 mb-3\">Resumen de compra</h5>" +
                        "    <div class=\"d-flex justify-content-between mb-2\">" +
                        "      <span>Subtotal:</span>" +
                        "      <span class=\"fw-bold\">$%.2f</span>" +
                        "    </div>" +
                        "    <div class=\"d-flex justify-content-between mb-3\">" +
                        "      <span>IGV:</span>" +
                        "      <span class=\"fw-bold\">$%.2f</span>" +
                        "    </div>" +
                        "    <div class=\"d-flex justify-content-between mb-4 pt-3 border-top\">" +
                        "      <span>Total:</span>" +
                        "      <span class=\"fw-bold fs-5\">$%.2f</span>" +
                        "    </div>" +
                        "    <div class=\"d-grid gap-4 justify-content-center\">" +


                //        "      <a href=\"/cliente/compra\" id=\"finalizar-compra-btn3\" class=\"btn btn-añadir-carrito text-white\">Finalizar compra</a>" +

                        "<form action=\"/cliente/verificar-compra\" method=\"post\" class=\"ajax-form\">" +
                        "    <button type=\"submit\" id=\"finalizar-compra-btn3\" class=\"btn btn-añadir-carrito text-white\">" +
                        "        Finalizar compra" +
                        "    </button>" +
                        "</form>"+

                        "      <a href=\"/cliente/productos\" class=\"btn btn-outline-secondary\">Seguir comprando</a>" +
                        "    </div>" +

                        "  </div>" +
                        "</div>",
                subtotal, igv, total
        );
    }
}
