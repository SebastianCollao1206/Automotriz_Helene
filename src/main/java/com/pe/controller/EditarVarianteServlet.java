package com.pe.controller;

import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.html.TamanioHtml;
import com.pe.model.service.ProductoService;
import com.pe.model.service.TamanioService;
import com.pe.model.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/variante/editar")
public class EditarVarianteServlet extends BaseServlet{
    private final VarianteService varianteService;
    private final ProductoService productoService;
    private final TamanioService tamanioService;

    public EditarVarianteServlet() throws SQLException {
        this.varianteService = new VarianteService();
        this.productoService = new ProductoService();
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_variante.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);

                Variante variante = varianteService.obtenerVariantePorId(id);
                if (variante != null) {
                    String nombreProducto = productoService.obtenerNombreProductoPorId(variante.getIdProducto());
                    String nombreTamanio = tamanioService.obtenerNombreTamanioPorId(variante.getIdTamanio());

                    // Cargar tamaños
                    tamanioService.cargarTamanios();
                    TreeSet<Tamanio> tamanios = tamanioService.getTamanios();

                    // Generar opciones de tamaños
                    String opcionesTamanios = TamanioHtml.generarOpcionesTamanios2(tamanios, variante.getIdTamanio());

                    // Cargar el HTML y reemplazar los valores
                    String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_variante.html")));
                    html = html.replace("${variante.id}", String.valueOf(variante.getIdVariante()));
                    html = html.replace("${variante.codigo}", variante.getCodigo());
                    html = html.replace("${variante.precio}", String.valueOf(variante.getPrecio()));
                    html = html.replace("${variante.imagen}", variante.getImagen());
                    html = html.replace("${variante.stock}", String.valueOf(variante.getStock()));
                    html = html.replace("${variante.cantidad}", String.valueOf(variante.getCantidad()));
                    html = html.replace("${selectedProduct}", nombreProducto);
                    html = html.replace("${productoId}", String.valueOf(variante.getIdProducto()));
                    html = html.replace("${selectedTamanio}", opcionesTamanios); // Reemplazar con las opciones generadas

                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Variante no encontrada");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de variante inválido");
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de variante no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String codigo = request.getParameter("codigo");
        String tamanio = request.getParameter("tamanio");
        String productoIdParam = request.getParameter("productoId");
        String precioParam = request.getParameter("precio");
        String imagen = request.getParameter("imagen");
        String stockParam = request.getParameter("stock");
        String cantidadParam = request.getParameter("cantidad");

        String mensaje;
        String redirigirUrl; // Cambiamos esto para construir la URL después

        try {
            int id = Integer.parseInt(idParam);
            int idTamanio = Integer.parseInt(tamanio);
            int idProducto = Integer.parseInt(productoIdParam);
            BigDecimal precio = new BigDecimal(precioParam);
            int stock = Integer.parseInt(stockParam);
            int cantidad = Integer.parseInt(cantidadParam);

            varianteService.actualizarVariante(id, codigo, idTamanio, idProducto, precio, imagen, stock, cantidad);
            mensaje = "Variante actualizada exitosamente!";

            // Construir la URL de redirección usando el ID del producto
            redirigirUrl = String.format("http://localhost:8081/variante/producto?id=%d", idProducto);
        } catch (Exception e) {
            mensaje = "Error al actualizar la variante: " + e.getMessage();
            redirigirUrl = "/variante/listar"; // Redirigir a la lista en caso de error
        }

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Redirigir a la URL construida
        response.sendRedirect(redirigirUrl);
    }
}
