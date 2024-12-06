package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.ItemCarrito;
import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.html.CarritoHtml;
import com.pe.model.administrador.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.TreeSet;

@WebServlet("/cliente/carrito/eliminar")
public class EliminarItemCarritoServlet extends BaseClientServlet {
    private static final Logger logger = LoggerFactory.getLogger(EliminarItemCarritoServlet.class);

    @Override
    protected String getContentPage() {
        return "/carrito.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            logger.info("Iniciando eliminación de item del carrito");

            String varianteId = request.getParameter("varianteId");
            logger.info("Parámetro recibido - varianteId: {}", varianteId);

            int idVariante = Integer.parseInt(varianteId);

            CarritoService.eliminarDelCarrito(request, idVariante);

            TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) request.getSession().getAttribute("carrito");

            if (carrito == null || carrito.isEmpty()) {
                String htmlCarritoVacio = CarritoHtml.generarHtmlCarritoVacio();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("tipoMensaje", "success");
                jsonResponse.put("mensaje", "Item eliminado del carrito");
                jsonResponse.put("cartCounter", 0); // Actualizar el contador a 0
                jsonResponse.put("carritoHtml", htmlCarritoVacio);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse.toString());
            } else {
                VarianteService varianteService = new VarianteService();
                TamanioService tamanioService = new TamanioService();
                ProductoService productoService = new ProductoService();

                String htmlCarrito = CarritoHtml.generarHtmlCarrito(carrito, varianteService, tamanioService, productoService);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("tipoMensaje", "success");
                jsonResponse.put("mensaje", "Item eliminado del carrito");
                jsonResponse.put("cartCounter", request.getSession().getAttribute("cartCounter"));
                jsonResponse.put("carritoHtml", htmlCarrito);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (NumberFormatException e) {
            logger.error("Error al parsear varianteId", e);
            MensajeService.mensajeJson(response, "error", "ID de variante inválido", null);
        } catch (Exception e) {
            logger.error("Error al eliminar item del carrito", e);
            MensajeService.mensajeJson(response, "error", "Error al procesar la solicitud", null);
        }
    }
}
