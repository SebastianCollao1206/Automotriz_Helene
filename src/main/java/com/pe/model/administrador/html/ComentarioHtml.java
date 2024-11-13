package com.pe.model.administrador.html;

import com.pe.model.administrador.entidad.Comentario;
import com.pe.model.administrador.entidad.MensajeConfirmacion;
import com.pe.model.administrador.service.ComentarioService;
import com.pe.model.cliente.entidad.Cliente;
import com.pe.model.cliente.service.ClienteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

public class ComentarioHtml {

    public static MensajeConfirmacion crearMensajeConfirmacion() {
        return new MensajeConfirmacion(
                "¿Confirmar acción?",
                "¿Está seguro de que desea modificar el estado de los comentarios seleccionados?",
                "warning",
                "Sí, continuar",
                "Cancelar"
        );
    }

    public static String generarHtmlComentarios(TreeSet<Comentario> comentarios, ClienteService clienteService, ComentarioService comentarioService) {
        StringBuilder html = new StringBuilder();
        try {
            String baseHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/lista_comentario.html")));

            String comentariosHtml;
            if (comentarios.isEmpty()) {
                comentariosHtml = "<div class=\"alert alert-warning\">No se encontraron comentarios que coincidan.</div>";
            } else {
                comentariosHtml = generarFilasComentarios(comentarios, clienteService, comentarioService);
            }

            baseHtml = baseHtml.replace("${comentarios}", comentariosHtml);
            baseHtml = baseHtml.replace("${estadosComentarioOptions}", generarOpcionesEstadoComentario(comentarioService.getEstadosComentarioSet()));

            html.append(baseHtml);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el HTML base", e);
        }
        return html.toString();
    }

    public static String generarOpcionesEstadoComentario(TreeSet<String> estadosComentario) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;' selected>Seleccione estado</option>");
        for (String estado : estadosComentario) {
            options.append("<option value=\"").append(estado).append("\">").append(estado).append("</option>");
        }
        return options.toString();
    }

    public static String generarFilasComentarios(TreeSet<Comentario> comentarios, ClienteService clienteService, ComentarioService comentarioService) {
        StringBuilder html = new StringBuilder();

        for (Comentario comentario : comentarios) {
            Cliente cliente = comentarioService.obtenerClientePorComentario(comentario, clienteService);

            if (cliente != null) {
                html.append("<div class=\"col-md-11\">");
                html.append("<div class=\"card mb-4 shadow-sm p-2\">");
                html.append("<div class=\"card-body\">");
                html.append("<div class=\"d-flex justify-content-between align-items-center mb-2\">");
                html.append("<div class=\"form-check\">");

                html.append("<input class=\"form-check-input\" type=\"checkbox\" name=\"ids\" value=\"").append(comentario.getIdComentario()).append("\" id=\"comment").append(comentario.getIdComentario()).append("\">");

                html.append("<label class=\"form-check-label\" for=\"comment").append(comentario.getIdComentario()).append("\">");
                html.append("<span class=\"fw-bold\">").append(cliente.getNombre()).append("</span>");
                html.append("<span class=\"text-muted ms-2\">").append(cliente.getCorreo()).append("</span>");
                html.append("</label>");
                html.append("</div>");
                html.append("<div class=\"d-flex align-items-center gap-2\">");
                html.append("<span class=\"badge ").append(comentario.getEstado() == Comentario.EstadoComentario.Activo ? "bg-success" : "bg-danger").append("\">").append(comentario.getEstado().name()).append("</span>");
                html.append("<small class=\"text-muted\">").append(comentario.getFecha().toString()).append("</small>");
                html.append("</div>");
                html.append("</div>");
                html.append("<p class=\"card-text mt-3\">").append(comentario.getComentario()).append("</p>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            }
        }
        return html.toString();
    }
}
