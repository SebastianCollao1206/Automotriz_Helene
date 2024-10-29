package com.pe.controller;

import com.pe.model.entidad.Categoria;
import com.pe.model.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet("/categoria/editar")
public class EditarCategoriaServlet extends BaseServlet{
    private final CategoriaService categoriaService;

    public EditarCategoriaServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_categoria.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
                if (categoria != null) {
                    String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_categoria.html")));
                    html = html.replace("${categoria.idCategoria}", String.valueOf(categoria.getIdCategoria()));
                    html = html.replace("${categoria.nombre}", categoria.getNombre());
                    html = html.replace("${categoria.estado}", categoria.getEstado().name());

                    // Manejar la selección del estado
                    html = html.replace("${estado.activoSelected}", categoria.getEstado().name().equals("Activo") ? "selected" : "");
                    html = html.replace("${estado.inactivoSelected}", categoria.getEstado().name().equals("Inactivo") ? "selected" : "");

                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de categoría inválido");
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de categoría no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String estado = request.getParameter("estado");

        String mensaje;
        String redirigirUrl = "/categoria/listar";

        try {
            int id = Integer.parseInt(idParam);
            categoriaService.actualizarCategoria(id, nombre, estado);
            mensaje = "Categoría actualizada exitosamente!";
        } catch (IllegalArgumentException e) {
            mensaje = "Estado inválido: " + e.getMessage();
        } catch (Exception e) {
            mensaje = "Error al actualizar la categoría: " + e.getMessage();
        }

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Redirigir a la lista de categorías
        response.sendRedirect(redirigirUrl);
    }
}
