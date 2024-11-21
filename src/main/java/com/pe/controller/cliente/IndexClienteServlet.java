package com.pe.controller.cliente;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.html.IndexHtml;
import com.pe.model.administrador.service.SliderService;
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
            TreeSet<Slider> slidersActivos = sliderService.cargarSlidersActivos();
            String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/index.html")));
            String slidersHtml = IndexHtml.generarHtmlSliderCliente(slidersActivos);
            htmlTemplate = htmlTemplate.replace("${sliders}", slidersHtml);
            request.setAttribute("content", htmlTemplate);

            super.doGet(request, response);

            logger.info("Generado HTML de index con {} sliders activos", slidersActivos.size());
        } catch (SQLException e) {
            logger.error("Error al cargar sliders", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar sliders");
        }
    }

}
