package com.pe.controller;

import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.html.ProductoHtml;
import com.pe.model.html.TamanioHtml;
import com.pe.model.html.VarianteHtml;
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

@WebServlet("/variante/agregar")
public class AgregarVarianteServlet extends BaseServlet{

    private final VarianteService varianteService;
    private final ProductoService productoService;
    private final TamanioService tamanioService;

    public AgregarVarianteServlet() throws SQLException {
        this.productoService = new ProductoService();
        this.tamanioService = new TamanioService();
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_variante.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Cargar tamaños
            tamanioService.cargarTamanios();
            TreeSet<Tamanio> tamanios = tamanioService.getTamanios();

            // Generar opciones de tamaños
            String opcionesTamanios = TamanioHtml.generarOpcionesTamanios(tamanios);

            // Leer el HTML del contenido específico
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/agregar_variante.html")));

            // Reemplazar las opciones de tamaños
            html = html.replace("${tamaniosOptions}", opcionesTamanios);

            // Reemplazar el nombre del producto seleccionado si existe
            String productoNombre = (String) request.getAttribute("productoNombre");
            String mensajeError = (String) request.getAttribute("mensajeError");
            if (productoNombre != null) {
                html = html.replace("${selectedProduct}", productoNombre);
                html = html.replace("${productoId}", request.getAttribute("productoId").toString());
            } else if (mensajeError != null) {
                html = html.replace("${selectedProduct}", mensajeError);
            } else {
                html = html.replace("${selectedProduct}", ""); // Si no hay producto ni error
            }

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar tamaños: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mensaje;
        String redirigirUrl = "/variante/agregar"; // URL para redirigir después de mostrar la alerta

        try {
            // Recoger los datos del formulario
            String codigo = request.getParameter("codigo");
            String tamanioId = request.getParameter("tamanio"); // Suponiendo que el valor del tamaño es el ID
            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            String imagen = request.getParameter("imagen");
            int stock = Integer.parseInt(request.getParameter("stock"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            String productoId = request.getParameter("productoId"); // ID del producto seleccionado

            // Crear una nueva variante
            Variante nuevaVariante = new Variante();
            nuevaVariante.setCodigo(codigo);
            nuevaVariante.setIdTamanio(Integer.parseInt(tamanioId));
            nuevaVariante.setPrecio(precio);
            nuevaVariante.setImagen(imagen);
            nuevaVariante.setStock(stock);
            nuevaVariante.setCantidad(cantidad);
            nuevaVariante.setIdProducto(Integer.parseInt(productoId));

            // Insertar la nueva variante en la base de datos
            varianteService.agregarVariante(nuevaVariante);
            mensaje = "Variante agregada exitosamente!";

        } catch (SQLException e) {
            // Manejo de errores
            mensaje = "Error al agregar la variante: " + e.getMessage();
        } catch (NumberFormatException e) {
            // Manejo de errores si hay un problema con la conversión de números
            mensaje = "Error en los datos proporcionados: " + e.getMessage();
        }

        // Generar el script de alerta
        String scriptAlerta = VarianteHtml.generarMensajeAlerta(mensaje, redirigirUrl);

        // Enviar el script de alerta como respuesta
        response.setContentType("text/html");
        response.getWriter().write(scriptAlerta);
    }
}
