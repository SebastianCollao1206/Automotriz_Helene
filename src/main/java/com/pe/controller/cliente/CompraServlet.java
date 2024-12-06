package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.Compra;
import com.pe.model.administrador.entidad.ItemCarrito;
import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.MetodoPago;
import com.pe.model.administrador.html.CompraHtml;
import com.pe.model.administrador.service.*;
import com.pe.model.cliente.entidad.Cliente;
import com.pe.model.cliente.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TreeSet;

@WebServlet("/cliente/compra")
public class CompraServlet extends BaseClientServlet{

    private static final Logger logger = LoggerFactory.getLogger(CompraServlet.class);

    private final MetodoPagoService metodoPagoService;
    private ClienteService clienteService;
    private CompraService compraService;
    private DetalleCompraService detalleCompraService;
    private VarianteService varianteService;

    public CompraServlet() throws SQLException {
        this.metodoPagoService = new MetodoPagoService();
        this.clienteService = new ClienteService();
        this.compraService = new CompraService();
        this.detalleCompraService = new DetalleCompraService();
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/compra.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            metodoPagoService.cargarMetodosPago();
            TreeSet<MetodoPago> metodosPagoActivos = metodoPagoService.cargarMetodosActivos();

            HttpSession session = request.getSession();

            Double totalCarrito = (Double) session.getAttribute("totalCarrito");
            totalCarrito = totalCarrito != null ? totalCarrito : 0.0;

            String html = CompraHtml.generarHtmlAgregarMetodoPago(metodosPagoActivos, totalCarrito);

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            logger.error("Error al cargar métodos de pago: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar métodos de pago: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Mensaje mensaje;

        try {
            int idMetodoPago = Integer.parseInt(request.getParameter("paymentMethod"));
            LocalDate fechaRecojo = LocalDate.parse(request.getParameter("pickupDate"));
            double total = Double.parseDouble(request.getParameter("totalCarrito"));

            TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) session.getAttribute("carrito");
            if (carrito == null || carrito.isEmpty()) {
                throw new IllegalStateException("El carrito está vacío");
            }

            Cliente cliente = (Cliente) session.getAttribute("cliente");
            if (cliente == null) {
                throw new IllegalStateException("No se ha iniciado sesión");
            }
            int idCliente = cliente.getIdCliente();

            Compra nuevaCompra = compraService.agregarCompra(
                    LocalDate.now(),
                    total,
                    fechaRecojo,
                    idCliente,
                    null,
                    idMetodoPago
            );

            if (nuevaCompra == null) {
                throw new IllegalStateException("No se pudo crear la compra.");
            }

            for (ItemCarrito item : carrito) {
                detalleCompraService.agregarDetalleCompra(
                        item.getCantidad(),
                        item.getPrecio(),
                        nuevaCompra.getIdCompra(),
                        item.getIdVariante()
                );
            }

            for (ItemCarrito item : carrito) {
                varianteService.quitarStock(
                        item.getIdVariante(),
                        item.getCantidad()
                );
            }

            session.removeAttribute("carrito");
            session.removeAttribute("cartCounter");
            session.removeAttribute("totalCarrito");

            mensaje = new Mensaje("success", "Compra realizada exitosamente.", "/cliente/");
        } catch (Exception e) {

            logger.error("Error en finalización de compra", e);
            mensaje = new Mensaje("error", "Error al procesar la compra: " + e.getMessage(), null);
        }
        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
