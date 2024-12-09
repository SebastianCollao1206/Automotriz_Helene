package com.pe.controller.cliente;

import com.pe.model.administrador.entidad.Comentario;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.html.IndexHtml;
import com.pe.model.administrador.service.ComentarioService;
import com.pe.model.administrador.service.ProductoService;
import com.pe.model.administrador.service.SliderService;
import com.pe.model.administrador.service.VarianteService;
import com.pe.model.cliente.service.ClienteService;
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

@WebServlet("/cliente/")
public class IndexClienteServlet extends BaseClientServlet {
    private static final Logger logger = LoggerFactory.getLogger(IndexClienteServlet.class);

    @Override
    protected String getContentPage() {
        return "/index.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            SliderService sliderService = new SliderService();
            ComentarioService comentarioService = new ComentarioService();
            ClienteService clienteService = new ClienteService();
            ProductoService productoService = new ProductoService();
            VarianteService varianteService = new VarianteService();

            TreeSet<Slider> slidersActivos = sliderService.cargarSlidersActivos();
            TreeSet<Comentario> comentariosActivos = comentarioService.cargarComentariosActivos();

            String htmlTemplate = IndexHtml.generarHtmlCompleto(slidersActivos, comentariosActivos, clienteService
            , productoService, varianteService);

            request.setAttribute("content", htmlTemplate);

            super.doGet(request, response);

            logger.info("Generado HTML de index con {} sliders activos y {} comentarios activos",
                    slidersActivos.size(), comentariosActivos.size());
        } catch (SQLException e) {
            logger.error("Error al cargar datos para index", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar datos");
        }
    }
}
