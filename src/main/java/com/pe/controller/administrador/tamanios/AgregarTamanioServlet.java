package com.pe.controller.administrador.tamanios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/tamanio/agregar")
public class AgregarTamanioServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AgregarTamanioServlet.class);
    private final TamanioService tamanioService;

    public AgregarTamanioServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_tamanio.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String unidadMedida = request.getParameter("nombre");
        String estado = request.getParameter("estado");

        String mensaje;
        String redirigirUrl = "/tamanio/agregar";

        try {
            tamanioService.agregarTamanio(unidadMedida, Tamanio.EstadoTamanio.valueOf(estado));
            mensaje = "Tamaño agregado exitosamente!";
            logger.info("Tamaño agregado: {}", unidadMedida);
        } catch (Exception e) {
            mensaje = "Error al agregar el tamaño: " + e.getMessage();
            logger.warn("Intento de agregar tamaño con estado no válido: {}", estado);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        super.doGet(request, response);
    }
}
