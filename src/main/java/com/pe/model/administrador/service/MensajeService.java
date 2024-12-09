package com.pe.model.administrador.service;

import com.pe.model.administrador.entidad.Mensaje;
import com.pe.model.administrador.entidad.MensajeConfirmacion;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

public class MensajeService {

    public static void mensajeJson(HttpServletResponse response, String tipoMensaje, String mensaje, String redirectUrl) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("tipoMensaje", tipoMensaje);
        jsonResponse.put("mensaje", mensaje);
        jsonResponse.put("redirectUrl", redirectUrl);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }

    public static void mensajeConfirmacioJson(HttpServletResponse response, Mensaje mensaje, MensajeConfirmacion mensajeConfirmacion) throws IOException {
        JSONObject responseObject = new JSONObject();
        responseObject.put("tipoMensaje", mensaje.getTipoMensaje());
        responseObject.put("mensaje", mensaje.getMensaje());
        responseObject.put("redirectUrl", mensaje.getRedirectUrl());
        responseObject.put("titulo", mensajeConfirmacion.getTitulo());
        responseObject.put("texto", mensajeConfirmacion.getTexto());
        responseObject.put("icono", mensajeConfirmacion.getIcono());
        responseObject.put("textoBotonConfirmar", mensajeConfirmacion.getTextoBotonConfirmar());
        responseObject.put("textoBotonCancelar", mensajeConfirmacion.getTextoBotonCancelar());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseObject.toString());
    }

    public static void mensajeJsonCart(HttpServletResponse response, String tipoMensaje, String mensaje, String redirectUrl, Integer cartCounter) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("tipoMensaje", tipoMensaje);
        jsonResponse.put("mensaje", mensaje);
        jsonResponse.put("redirectUrl", redirectUrl);
        jsonResponse.put("cartCounter", cartCounter != null ? cartCounter : 0);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }

    public static void mensajeJsonNoti(HttpServletResponse response, String tipoMensaje, String mensaje, String redirectUrl, Integer notiCounter) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("tipoMensaje", tipoMensaje);
        jsonResponse.put("mensaje", mensaje);
        jsonResponse.put("redirectUrl", redirectUrl);
        jsonResponse.put("notiCounter", notiCounter != null ? notiCounter : 0);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
}
