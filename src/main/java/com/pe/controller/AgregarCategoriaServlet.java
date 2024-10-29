package com.pe.controller;

import com.pe.model.entidad.Categoria;
import com.pe.model.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/categoria/agregar")
public class AgregarCategoriaServlet extends BaseServlet{
    private final CategoriaService categoriaService;

    public AgregarCategoriaServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_categoria.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String estado = request.getParameter("estado");

        String mensaje;
        String redirigirUrl = "/categoria/agregar";

        try {
            // Agregar la categoría
            categoriaService.agregarCategoria(nombre, Categoria.EstadoCategoria.valueOf(estado));
            mensaje = "Categoría agregada exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al agregar la categoría: " + e.getMessage();
        }

        // Establecer el mensaje en el request
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Llamar al metodo doGet para redirigir al usuario
        super.doGet(request, response);
    }
}
