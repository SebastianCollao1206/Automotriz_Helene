package com.pe.controller;

import com.pe.model.entidad.Tamanio;
import com.pe.model.service.TamanioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet("/tamanio/editar")
public class EditarTamanioServlet extends BaseServlet{
    private final TamanioService tamanioService;

    public EditarTamanioServlet() throws SQLException {
        this.tamanioService = new TamanioService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_tamanio.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Tamanio tamanio = tamanioService.obtenerTamanioPorId(id);
                if (tamanio != null) {
                    String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_tamanio.html")));
                    html = html.replace("${tamanio.idtamanio}", String.valueOf(tamanio.getIdTamanio()));
                    html = html.replace("${tamanio.nombre}", tamanio.getUnidadMedida());
                    html = html.replace("${estado.activoSelected}", tamanio.getEstado().name().equals("Activo") ? "selected" : "");
                    html = html.replace("${estado.inactivoSelected}", tamanio.getEstado().name().equals("Inactivo") ? "selected" : "");

                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tamaño no encontrado");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de tamaño inválido");
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de tamaño no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String estado = request.getParameter("estado");

        String mensaje;
        String redirigirUrl = "/tamanio/listar";

        try {
            int id = Integer.parseInt(idParam);
            tamanioService.actualizarTamanio(id, nombre, estado);
            mensaje = "Tamaño actualizado exitosamente!";
        } catch (IllegalArgumentException e) {
            mensaje = "Estado inválido: " + e.getMessage();
        } catch (Exception e) {
            mensaje = "Error al actualizar el tamaño: " + e.getMessage();
        }

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Redirigir a la lista de tamaños
        response.sendRedirect(redirigirUrl);
    }
}
