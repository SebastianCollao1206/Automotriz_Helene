package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.ItemCarrito;
import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.html.CarritoHtml;
import com.pe.model.administrador.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/cliente/carrito")
public class CarritoServlet extends BaseClientServlet{
    private static final Logger logger = LoggerFactory.getLogger(CarritoServlet.class);

    @Override
    protected String getContentPage() {
        return "/carrito.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) session.getAttribute("carrito");

            if (carrito == null || carrito.isEmpty()) {
                request.setAttribute("content", CarritoHtml.generarHtmlCarritoVacio());
            } else {
                VarianteService varianteService = new VarianteService();
                TamanioService tamanioService = new TamanioService();
                ProductoService productoService = new ProductoService();

                String htmlCarrito = CarritoHtml.generarHtmlCarrito(carrito, varianteService, tamanioService, productoService);

                double subtotalCarrito = carrito.stream()
                        .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                        .sum();

                double igv = subtotalCarrito * 0.18;

                double totalCarrito = subtotalCarrito + igv;

                session.setAttribute("totalCarrito", totalCarrito);

                request.setAttribute("content", htmlCarrito);
            }

            super.doGet(request, response);
        } catch (SQLException e) {
            logger.error("Error al cargar carrito", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar carrito");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            logger.info("Iniciando actualización de cantidad en carrito");

            VarianteService varianteService = new VarianteService();
            TamanioService tamanioService = new TamanioService();
            ProductoService productoService = new ProductoService();

            String varianteId = request.getParameter("varianteId");
            String cantidadStr = request.getParameter("cantidad");

            logger.info("Parámetros recibidos - varianteId: {}, cantidad: {}", varianteId, cantidadStr);

            int idVariante = Integer.parseInt(varianteId);
            int cantidad = Integer.parseInt(cantidadStr);

            Mensaje mensaje = CarritoService.actualizarCantidadEnCarrito(request, varianteService, idVariante, cantidad);

            if (mensaje.getTipoMensaje().equals("success")) {
                TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) request.getSession().getAttribute("carrito");
                String htmlCarrito = CarritoHtml.generarHtmlCarrito(carrito, varianteService, tamanioService, productoService);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("tipoMensaje", mensaje.getTipoMensaje());
                jsonResponse.put("mensaje", mensaje.getMensaje());
                jsonResponse.put("redirectUrl", mensaje.getRedirectUrl());
                jsonResponse.put("cartCounter", request.getSession().getAttribute("cartCounter"));

                jsonResponse.put("totalCarrito", request.getSession().getAttribute("totalCarrito"));

                jsonResponse.put("carritoHtml", htmlCarrito);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse.toString());
            } else {
                MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
            }

        } catch (NumberFormatException e) {
            logger.error("Error al parsear cantidad", e);
            MensajeService.mensajeJson(response, "error", "Cantidad inválida", null);
        } catch (Exception e) {
            logger.error("Error al actualizar cantidad en carrito", e);
            MensajeService.mensajeJson(response, "error", "Error al procesar la solicitud", null);
        }
    }
}
