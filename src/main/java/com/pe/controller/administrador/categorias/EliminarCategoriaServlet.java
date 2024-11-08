package com.pe.controller.administrador.categorias;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/categoria/eliminar")
public class EliminarCategoriaServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EliminarCategoriaServlet.class);
    private final CategoriaService categoriaService;

    public EliminarCategoriaServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_categoria.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String mensaje;
        String redirigirUrl = "/categoria/listar";

        try {
            categoriaService.cargarCategorias();
            categoriaService.eliminarCategoria(Integer.parseInt(id));
            mensaje = "Categoría cambiada a inactiva exitosamente!";
            logger.info("Categoría eliminada: ID = {}", id);
        } catch (Exception e) {
            mensaje = "Error al cambiar el estado de la categoría: " + e.getMessage();
            logger.error("Error al eliminar categoría: ID inválido: {}", id, e);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        response.sendRedirect(redirigirUrl);
    }
}
