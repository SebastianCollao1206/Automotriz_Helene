package com.pe.controller;

import com.pe.model.entidad.Tamanio;
import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/tamanio/agregar")
public class AgregarTamanioServlet extends BaseServlet{
    private final TamanioService tamanioService;

    public AgregarTamanioServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_tamanio.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String unidadMedida = request.getParameter("nombre");
        String estado = request.getParameter("estado");

        String mensaje;
        String redirigirUrl = "/tamanio/agregar";

        try {
            // Agregar el tamaño
            tamanioService.agregarTamanio(unidadMedida, Tamanio.EstadoTamanio.valueOf(estado));
            mensaje = "Tamaño agregado exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al agregar el tamaño: " + e.getMessage();
        }

        // Establecer el mensaje en el request
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Llamar al metodo doGet para redirigir al usuario
        super.doGet(request, response);
    }
}
