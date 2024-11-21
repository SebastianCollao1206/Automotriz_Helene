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

@WebServlet("/slider/agregar")
public class AgregarSliderServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AgregarSliderServlet.class);
    private final SliderService sliderService;

    public AgregarSliderServlet() throws SQLException {
        this.sliderService = new SliderService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_slider.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String opcionSeleccionada = (String) request.getAttribute("opcionSeleccionada");
        String idOpcionSeleccionada = request.getAttribute("idOpcionSeleccionada") != null ?
                request.getAttribute("idOpcionSeleccionada").toString() : null;
        String mensajeError = (String) request.getAttribute("mensajeError");

        String relacionarCon = request.getParameter("relacionarCon");
        if(relacionarCon == null) {
            relacionarCon = (String) request.getSession().getAttribute("relacionarCon");
        }
        if(relacionarCon != null) {
            request.getSession().setAttribute("relacionarCon", relacionarCon);
        }

        String htmlTemplate = SliderHtml.generarHtmlAgregarSlider(opcionSeleccionada, idOpcionSeleccionada, relacionarCon, mensajeError);

        request.setAttribute("content", htmlTemplate);
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, SliderService.getMultipartConfig());

        String titulo = request.getParameter("titulo");
        String eslogan = request.getParameter("eslogan");
        LocalDate fechaInicio = LocalDate.parse(request.getParameter("fechaInicio"));
        String imagen;
        LocalDate fechaFin = LocalDate.parse(request.getParameter("fechaFin"));
        String estadoStr = request.getParameter("estado");

        if (estadoStr == null || estadoStr.isEmpty()) {
            estadoStr = "Activo";
        }

        Slider.EstadoSlider estado = Slider.EstadoSlider.valueOf(estadoStr);
        String relacionarCon = request.getParameter("relacionarCon");

        logger.info("Tipo de relación seleccionado: {}", relacionarCon);
        String idOpcionSeleccionada = request.getParameter("selectedOptionId");

        Mensaje mensaje;

        try {
            imagen = sliderService.manejarCargaDeImagen(request);
            sliderService.agregarSliderConRelacion(titulo, eslogan, fechaInicio, imagen, fechaFin, estadoStr, relacionarCon, idOpcionSeleccionada);
            logger.info("Slider agregado: Título = {}", titulo);
            mensaje = new Mensaje("success", "Slider agregado exitosamente.", "/slider/agregar");

        } catch (IllegalArgumentException e) {
            mensaje = new Mensaje("error", "Error de validación: " + e.getMessage(), null);
            logger.warn("Error de validación al agregar slider: {}", e.getMessage());
        } catch (SQLException e) {
            mensaje = new Mensaje("error", "Error al agregar el slider: " + e.getMessage(), null);
            logger.error("Error de base de datos al agregar slider: {}", e.getMessage());
        } catch (Exception e) {
            mensaje = new Mensaje("error", "Error inesperado al agregar slider: " + e.getMessage(), null);
            logger.error("Error inesperado: {}", e.getMessage(), e);
        }
        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
