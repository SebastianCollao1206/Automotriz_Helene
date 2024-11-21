package com.pe.controller.administrador.Slider;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.MensajeConfirmacion;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.html.SliderHtml;
import com.pe.model.administrador.service.MensajeService;
import com.pe.model.administrador.service.SliderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/slider/eliminar")
public class EliminarSliderServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EliminarSliderServlet.class);
    private final SliderService sliderService;

    public EliminarSliderServlet() throws SQLException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Mensaje mensaje;

        try {
            int id = Integer.parseInt(idParam);
            sliderService.eliminarSlider(id);
            mensaje = new Mensaje("success", "Slider eliminado exitosamente.", "/slider/listar");
        } catch (SQLException e) {
            mensaje = new Mensaje("error", "Error al eliminar el slider: " + e.getMessage(), null);
            logger.error("Error al eliminar slider: {}", e.getMessage(), e);
        } catch (NumberFormatException e) {
            mensaje = new Mensaje("error", "ID de slider no válido.", null);
            logger.error("ID de slider no válido: {}", e.getMessage(), e);
        }

        MensajeService.mensajeJson(response, mensaje.getTipoMensaje(), mensaje.getMensaje(), mensaje.getRedirectUrl());
    }
}
