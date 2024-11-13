package com.pe.controller.administrador.categorias;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/categoria/agregar")
public class AgregarCategoriaServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AgregarCategoriaServlet.class);
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
            categoriaService.agregarCategoria(nombre, Categoria.EstadoCategoria.valueOf(estado));
            mensaje = "Categoría agregada exitosamente!";
            logger.info("Categoría agregada: {}", nombre);
        } catch (Exception e) {
            mensaje = "Error al agregar la categoría: " + e.getMessage();
            logger.warn("Intento de agregar categoría con estado no válido: {}", estado);
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        super.doGet(request, response);
    }
}
