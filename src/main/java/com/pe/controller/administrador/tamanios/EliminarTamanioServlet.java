package com.pe.controller.administrador.tamanios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/tamanio/eliminar")
public class EliminarTamanioServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EliminarTamanioServlet.class);
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
            tamanioService.eliminarTamanio(Integer.parseInt(id));
            mensaje = "Tamaño cambiado a inactivo exitosamente!";
            logger.info("Tamaño eliminado con ID: {}", id);
        } catch (Exception e) {
            mensaje = "Error al cambiar el estado del tamaño: " + e.getMessage();
            logger.error("Error al eliminar tamaño: ID = {}, Error = {}", id, e.getMessage(), e);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        response.sendRedirect(redirigirUrl); // Redirigir a la lista de tamaños
    }
}
