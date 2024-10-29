package com.pe.controller;

import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/tamanio/eliminar")
public class EliminarTamanioServlet extends BaseServlet{
    private final TamanioService tamanioService;

    public EliminarTamanioServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_tamanio.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String mensaje;
        String redirigirUrl = "/tamanio/listar";

        try {
            tamanioService.eliminarTamanio(Integer.parseInt(id)); // Cambia el estado a inactivo
            mensaje = "Tamaño cambiado a inactivo exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al cambiar el estado del tamaño: " + e.getMessage();
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        response.sendRedirect(redirigirUrl); // Redirigir a la lista de tamaños
    }
}
