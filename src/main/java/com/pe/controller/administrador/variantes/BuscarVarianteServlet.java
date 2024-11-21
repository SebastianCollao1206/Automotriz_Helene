package com.pe.controller.administrador.variantes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Variante;
import com.pe.model.administrador.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buscarVariante")
public class BuscarVarianteServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(BuscarVarianteServlet.class);
    private final VarianteService varianteService;

    public BuscarVarianteServlet() throws SQLException {
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_slider.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreVariante = request.getParameter("nombre");
        String relacionarCon = request.getParameter("relacionarCon");

        if (nombreVariante != null && !nombreVariante.isEmpty()) {
            try {
                Variante variante = varianteService.buscarVariantePorNombre(nombreVariante);
                if (variante != null) {
                    request.setAttribute("opcionSeleccionada", variante.getCodigo());
                    request.setAttribute("idOpcionSeleccionada", variante.getIdVariante());
                    request.getSession().setAttribute("relacionarCon", relacionarCon);
                    logger.info("Variante encontrada: {}", variante.getCodigo());
                } else {
                    request.setAttribute("mensajeError", "Variante no encontrada");
                    logger.warn("Variante no encontrada: {}", nombreVariante);
                }
            } catch (SQLException e) {
                request.setAttribute("mensajeError", "Error al buscar la variante: " + e.getMessage());
                logger.error("Error al buscar la variante: {}", e.getMessage(), e);
            }
        } else {
            request.setAttribute("mensajeError", "Por favor, ingresa un nombre de variante.");
            logger.warn("Nombre de variante no proporcionado.");
        }
        request.getRequestDispatcher("/slider/agregar").forward(request, response);
    }
}
