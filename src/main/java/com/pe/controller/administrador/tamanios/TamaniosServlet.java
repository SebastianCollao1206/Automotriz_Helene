package com.pe.controller.administrador.tamanios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.html.TamanioHtml;
import com.pe.model.administrador.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/tamanio/listar")
public class TamaniosServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(TamaniosServlet.class);
    private final TamanioService tamanioService;

    public TamaniosServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_tamanio.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            tamanioService.cargarTamanios();
            String nombre = request.getParameter("nombre");
            String estadoStr = request.getParameter("estado");

            Tamanio.EstadoTamanio estado = null;
            try {
                estado = tamanioService.obtenerEstadoTamanio(estadoStr);
            } catch (IllegalArgumentException e) {
                logger.warn(e.getMessage());
            }

            TreeSet<Tamanio> tamaniosFiltrados = tamanioService.buscarTamanios(nombre, estado);
            logger.info("Se han encontrado {} tamaños que coinciden con los criterios de búsqueda.", tamaniosFiltrados.size());

            String html = TamanioHtml.generarHtmlTamanios(tamaniosFiltrados, tamanioService);

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            logger.error("Error al cargar tamaños: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar tamaños: " + e.getMessage());
        }
    }
}
