package com.pe.controller.administrador.comentarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Comentario;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.html.ComentarioHtml;
import com.pe.model.administrador.service.ComentarioService;
import com.pe.model.cliente.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/comentario/listar")
public class ComentariosServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(ComentariosServlet.class);
    private final ComentarioService comentarioService;
    private final ClienteService clienteService;

    public ComentariosServlet() throws SQLException {
        this.comentarioService = new ComentarioService();
        this.clienteService = new ClienteService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_comentario.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            comentarioService.cargarComentarios();

            String estadoParam = request.getParameter("estado");
            String fechaInicioParam = request.getParameter("fecha_inicio");
            String fechaFinParam = request.getParameter("fecha_fin");

            TreeSet<Comentario> comentariosFiltrados = comentarioService.buscarComentarios(estadoParam, fechaInicioParam, fechaFinParam);
            logger.info("Comentarios filtrados: estado={}, fechaInicio={}, fechaFin={}, total={}", estadoParam, fechaInicioParam, fechaFinParam, comentariosFiltrados.size());

            String html = ComentarioHtml.generarHtmlComentarios(comentariosFiltrados, clienteService, comentarioService);

            request.setAttribute("content", html);
            super.doGet(request, response);
        } catch (SQLException e) {
            logger.error("Error al cargar comentarios: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar comentarios: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error inesperado: " + e.getMessage());
        }
    }
}
