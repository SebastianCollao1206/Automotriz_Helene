package com.pe.controller.administrador.Slider;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.html.SliderHtml;
import com.pe.model.administrador.service.SliderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/slider/listar")
public class SlidersServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(SlidersServlet.class);
    private final SliderService sliderService;

    public SlidersServlet() throws SQLException {
        this.sliderService = new SliderService();
    }

    @Override
    protected String getContentPage() {
        return "/lista_slider.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            sliderService.cargarSliders();

            String estadoParam = request.getParameter("estado");
            String tipoParam = request.getParameter("tipo");

            Slider.EstadoSlider estado = (estadoParam != null && !estadoParam.isEmpty()) ? Slider.EstadoSlider.valueOf(estadoParam) : null;
            TreeSet<Slider> slidersFiltrados = sliderService.buscarSliders(tipoParam, estado);

            logger.info("Sliders filtrados: relacionarCon={}, estado={}, total={}", tipoParam, estadoParam, slidersFiltrados.size());

            String html = SliderHtml.generarHtmlSliders(slidersFiltrados, sliderService);

            request.setAttribute("content", html);
            super.doGet(request, response);
        } catch (SQLException e) {
            logger.error("Error al cargar sliders: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar sliders: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error inesperado: " + e.getMessage());
        }
    }
}
