package com.pe.controller;

import com.pe.model.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/variante/actualizarStock")
public class ActualizarStockServlet extends BaseServlet{

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

            // Actualiza el stock
            varianteService.actualizarStock(idVariante, nuevoStock);

            // Obtén el ID del producto asociado a la variante
            int idProducto = varianteService.obtenerIdProductoPorVariante(idVariante);
            redirigirUrl += idProducto;

            mensaje = "Stock actualizado exitosamente!";
        } catch (NumberFormatException e) {
            mensaje = "Error: ID de variante o stock no válido.";
            e.printStackTrace();
        } catch (SQLException e) {
            mensaje = "Error al actualizar el stock: " + e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            mensaje = "Error inesperado: " + e.getMessage();
            e.printStackTrace();
        }

        // Imprimir el mensaje final
        System.out.println("Mensaje: " + mensaje);
        request.setAttribute("mensaje", mensaje);
        response.sendRedirect(redirigirUrl);
    }
}
