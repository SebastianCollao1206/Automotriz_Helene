package com.pe.controller.administrador.tamanios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Tamanio;
import com.pe.model.html.TamanioHtml;
import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            tamanioService.cargarTamanios();

            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            String nombre = request.getParameter("nombre");
            String estadoStr = request.getParameter("estado");

            Tamanio.EstadoTamanio estado = null;
            if (estadoStr != null && !estadoStr.isEmpty()) {
                try {
                    estado = Tamanio.EstadoTamanio.valueOf(estadoStr);
                } catch (IllegalArgumentException e) {
                    estado = null;
                    logger.warn("Estado de tamaño no válido: {}", estadoStr);
                }
            }

            // Filtrar los datos de los tamaños según los parámetros de búsqueda
            TreeSet<Tamanio> tamaniosFiltrados = tamanioService.buscarTamanios(nombre, estado);
            logger.info("Se han encontrado {} tamaños que coinciden con los criterios de búsqueda.", tamaniosFiltrados.size());

            // Leer el HTML del contenido específico
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_tamanio.html")));

            // Reemplazar la parte dinámica de la tabla
            if (tamaniosFiltrados.isEmpty()) {
                html = html.replace("${tableRows}", "<tr><td colspan='4'>No se encontraron tamaños que coincidan.</td></tr>");
            } else {
                html = html.replace("${tableRows}", TamanioHtml.generarFilasTablaTamanios(tamaniosFiltrados));
            }

            html = html.replace("${scriptConfirmacionEliminacion}", TamanioHtml.generarScriptConfirmacionEliminacion());

            request.setAttribute("content", html);
            super.doGet(request, response);

        } catch (SQLException e) {
            logger.error("Error al cargar tamaños: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar tamaños: " + e.getMessage());
        }
    }
}
