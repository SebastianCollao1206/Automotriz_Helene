package com.pe.controller.administrador.variantes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/variante/actualizarStock")
public class ActualizarStockServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(ActualizarStockServlet.class);
    private final VarianteService varianteService;

    public ActualizarStockServlet() throws SQLException {
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/variante_producto.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idVarianteParam = request.getParameter("idVariante");
        String nuevoStockParam = request.getParameter("stock");
        String mensaje;
        String redirigirUrl = "/variante/producto?id=";

        try {

            int idVariante = Integer.parseInt(idVarianteParam);
            int nuevoStock = Integer.parseInt(nuevoStockParam);

            varianteService.actualizarStock(idVariante, nuevoStock);

            int idProducto = varianteService.obtenerIdProductoPorVariante(idVariante);
            redirigirUrl += idProducto;

            mensaje = "Stock actualizado exitosamente!";
            logger.info("Stock actualizado: ID Variante = {}, Nuevo Stock = {}", idVariante, nuevoStock);
        } catch (NumberFormatException e) {
            mensaje = "Error: ID de variante o stock no válido.";
            logger.error("Error al parsear ID de variante o stock: idVariante={}, stock={}", idVarianteParam, nuevoStockParam, e);
            e.printStackTrace();
        } catch (SQLException e) {
            mensaje = "Error al actualizar el stock: " + e.getMessage();
            logger.error("Error al actualizar el stock para ID Variante = {}: {}", idVarianteParam, e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            mensaje = "Error inesperado: " + e.getMessage();
            logger.error("Error inesperado: {}", e.getMessage(), e);
            e.printStackTrace();
        }

        System.out.println("Mensaje: " + mensaje);
        request.setAttribute("mensaje", mensaje);
        response.sendRedirect(redirigirUrl);
    }
}
