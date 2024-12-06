package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.html.DetalleProductoClienteHtml;
import com.pe.model.administrador.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cliente/detalle-producto")
public class DetalleProductoClienteServlet extends BaseClientServlet{
    private static final Logger logger = LoggerFactory.getLogger(DetalleProductoClienteServlet.class);

    @Override
    protected String getContentPage() {
        return "/detalle-producto.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ProductoService productoService = new ProductoService();
            VarianteService varianteService = new VarianteService();
            TamanioService tamanioService = new TamanioService();
            CategoriaService categoriaService = new CategoriaService();

            String productoId = request.getParameter("id");
            String varianteId = request.getParameter("variantId");

            Producto producto = productoService.obtenerProductoPorId(Integer.parseInt(productoId));

            ProductosRelacionadosService relacionadosService = new ProductosRelacionadosService(
                    categoriaService, productoService, varianteService);

            Variante varianteSeleccionada = varianteService.obtenerVariantePorId(Integer.parseInt(varianteId));
            List<Variante> variantesDisponibles = new ArrayList<>(varianteService.obtenerVariantesPorProducto(Integer.parseInt(productoId)));

            String htmlTemplate = DetalleProductoClienteHtml.generarHtmlCompleto(
                    producto, varianteSeleccionada, variantesDisponibles, tamanioService,
                    relacionadosService, varianteService);

            request.setAttribute("content", htmlTemplate);
            super.doGet(request, response);

            logger.info("Generado HTML de detalle producto {} con variante {} de categoría {}",
                    productoId, varianteId, producto.getIdCategoria());

        } catch (SQLException e) {
            logger.error("Error al cargar detalle del producto", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al cargar detalle del producto");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Mensaje mensaje = null;
        Integer cartCounter = 0;

        try {
            logger.info("Iniciando proceso de agregar al carrito");

            VarianteService varianteService = new VarianteService();
            TamanioService tamanioService = new TamanioService();

            String varianteId = request.getParameter("variantId");
            String cantidadStr = request.getParameter("cantidad");

            logger.info("Parámetros recibidos - varianteId: {}, cantidad: {}", varianteId, cantidadStr);

            int idVariante = Integer.parseInt(varianteId);
            int cantidad;
            try {
                cantidad = Integer.parseInt(cantidadStr);
            } catch (NumberFormatException e) {
                logger.error("Error al parsear cantidad", e);
                mensaje = new Mensaje("error", "Cantidad inválida", null);
                throw new Exception("Cantidad inválida");
            }

            Variante variante = varianteService.obtenerVariantePorId(idVariante);

            if (variante.getStock() < cantidad) {
                logger.warn("Stock insuficiente. Solicitado: {}, Disponible: {}", cantidad, variante.getStock());
                mensaje = new Mensaje("error", "No hay suficiente stock", null);
                throw new Exception("Stock insuficiente");
            }

            CarritoService.agregarAlCarrito(request, variante, cantidad);
            logger.info("Producto agregado al carrito exitosamente. Variante: {}, Cantidad: {}", idVariante, cantidad);

            HttpSession session = request.getSession();
            cartCounter = (Integer) session.getAttribute("cartCounter");
            cartCounter = cartCounter != null ? cartCounter : 0;

            Tamanio tamanio = tamanioService.obtenerTamanioPorId(variante.getIdTamanio());
            String mensajeExito = String.format("Producto agregado al carrito", cantidad, tamanio.getUnidadMedida());

            mensaje = new Mensaje("success", mensajeExito, null);

        } catch (Exception e) {
            logger.error("Error al agregar al carrito", e);
            if (mensaje == null) {
                mensaje = new Mensaje("error", "Error al procesar la solicitud", null);
            }
        } finally {
            if (mensaje != null) {
                if (mensaje.getTipoMensaje().equals("success")) {
                    MensajeService.mensajeJsonCart(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl(), cartCounter);
                } else {
                    MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
                }
            }
        }
    }
}
