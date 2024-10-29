package com.pe.controller;

import com.pe.model.entidad.Tamanio;
import com.pe.model.html.TamanioHtml;
import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/tamanio/listar")
public class TamaniosServlet extends BaseServlet{
    private final TamanioService tamanioService;

    public TamaniosServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_tamanio.html"; // Cambia a la ruta de tu HTML
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recargar tamaños desde la base de datos en cada petición
            tamanioService.cargarTamanios();

            // Evitar que el navegador almacene en caché la respuesta
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // Obtener los parámetros de búsqueda
            String nombre = request.getParameter("nombre");
            String estadoStr = request.getParameter("estado");

            // Convertir el estado de String a EstadoTamanio
            Tamanio.EstadoTamanio estado = null;
            if (estadoStr != null && !estadoStr.isEmpty()) {
                try {
                    estado = Tamanio.EstadoTamanio.valueOf(estadoStr);
                } catch (IllegalArgumentException e) {
                    // Manejar el caso donde el estado no es válido
                    estado = null; // O asigna un valor por defecto si es necesario
                }
            }

            // Filtrar los datos de los tamaños según los parámetros de búsqueda
            TreeSet<Tamanio> tamaniosFiltrados = tamanioService.buscarTamanios(nombre, estado);

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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar tamaños: " + e.getMessage());
        }
    }
}
