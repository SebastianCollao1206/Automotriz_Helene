package com.pe.controller.administrador.Slider;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.html.SliderHtml;
import com.pe.model.administrador.service.MensajeService;
import com.pe.model.administrador.service.SliderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/slider/editar")
public class EditarSliderServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditarSliderServlet.class);
    private final SliderService sliderService;

    public EditarSliderServlet() throws SQLException {
        this.sliderService = new SliderService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_slider.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                sliderService.cargarSliders();
                int id = Integer.parseInt(idParam);
                Slider slider = sliderService.obtenerSliderPorId(id);
                if (slider != null) {
                    String html = SliderHtml.generarHtmlEdicionSlider(slider);
                    request.setAttribute("content", html);
                    super.doGet(request, response);
                    logger.info("Se ha cargado el formulario de edición para el slider con ID: {}", id);
                } else {
                    logger.warn("Slider no encontrado para ID: {}", id);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Slider no encontrado");
                }
            } catch (NumberFormatException e) {
                logger.error("ID de slider inválido: {}", idParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de slider inválido");
            } catch (SQLException e) {
                logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            logger.warn("ID de slider no proporcionado");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de slider no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, SliderService.getMultipartConfig());

        String idParam = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String eslogan = request.getParameter("eslogan");
        String fechaInicioStr = request.getParameter("fechaInicio");
        String fechaFinStr = request.getParameter("fechaFin");
        String estadoStr = request.getParameter("estado");

        Mensaje mensaje;
        try {

            int id = Integer.parseInt(idParam);
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);
            Slider.EstadoSlider estado = Slider.EstadoSlider.valueOf(estadoStr);

            String imagen = sliderService.manejarCargaDeImagen(request);

            sliderService.actualizarSlider(id, titulo, eslogan, imagen, fechaInicio, fechaFin, estado);

            mensaje = new Mensaje("success", "Slider actualizado exitosamente.", "/slider/listar");
            logger.info("Slider actualizado exitosamente: ID = {}", id);

        } catch (IllegalArgumentException e) {
            mensaje = new Mensaje("error", "Error de validación: " + e.getMessage(), null);
            logger.warn("Error de validación al actualizar slider: {}", e.getMessage());
        } catch (SQLException e) {
            mensaje = new Mensaje("error", "Error al actualizar el slider: " + e.getMessage(), null);
            logger.error("Error de base de datos al actualizar slider: {}", e.getMessage());
        } catch (Exception e) {
            mensaje = new Mensaje("error", "Error inesperado al actualizar slider: " + e.getMessage(), null);
            logger.error("Error inesperado al actualizar slider: {}", e.getMessage(), e);
        }
        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
