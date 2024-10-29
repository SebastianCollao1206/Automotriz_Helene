package com.pe.controller;

import com.pe.model.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/categoria/eliminar")
public class EliminarCategoriaServlet extends BaseServlet{
    private final CategoriaService categoriaService;

    public EliminarCategoriaServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_categoria.html"; // Cambia a la ruta de tu HTML
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String mensaje;
        String redirigirUrl = "/categoria/listar";

        try {
            categoriaService.eliminarCategoria(Integer.parseInt(id)); // Cambia el estado a inactivo
            mensaje = "Categoría cambiada a inactiva exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al cambiar el estado de la categoría: " + e.getMessage();
        }

        // Si deseas mostrar un mensaje de alerta después de la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        response.sendRedirect(redirigirUrl); // Redirigir a la lista de categorías
    }
}
