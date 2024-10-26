package com.pe.model.html;

import com.pe.model.entidad.Usuario;

import java.util.TreeSet;

public class UsuarioHtml {

    public static String generarMensajeAlerta(String mensaje, String redireccion) {
        StringBuilder html = new StringBuilder();
        html.append("<script type='text/javascript'>");
        html.append("    alert('").append(mensaje).append("');");
        html.append("    window.location='").append(redireccion).append("';");
        html.append("</script>");
        return html.toString();
    }

    /**
     * Generar opciones para el filtro de tipo de usuario
     * @param tiposUsuario
     * @return
     */
    public static String generarOpcionesTipoUsuario(TreeSet<String> tiposUsuario) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;' selected>Tipo de Usuario</option>");
        for (String tipo : tiposUsuario) {
            options.append("<option value=\"").append(tipo).append("\">").append(tipo).append("</option>");
        }
        return options.toString();
    }

    /**
     * Generar opciones para el filtro de estado
     * @param estadosUsuario
     * @return
     */
    public static String generarOpcionesEstadoUsuario(TreeSet<String> estadosUsuario) {
        StringBuilder options = new StringBuilder();
        options.append("<option value='' style='display: none;' selected>Estado</option>");
        for (String estado : estadosUsuario) {
            options.append("<option value=\"").append(estado).append("\">").append(estado).append("</option>");
        }
        return options.toString();
    }

    /**
     * Generar filas de la tabla de usuarios
     * @param usuarios
     * @return
     */
    public static String generarFilasTablaUsuarios(TreeSet<Usuario> usuarios) {
        StringBuilder tableRows = new StringBuilder();
        for (Usuario usuario : usuarios) {
            tableRows.append("<tr>");
            tableRows.append("<td>").append(usuario.getNombre()).append("</td>");
            tableRows.append("<td>").append(usuario.getCorreo()).append("</td>");
            tableRows.append("<td>").append(usuario.getFechaRegistro()).append("</td>");
            tableRows.append("<td>").append(usuario.getDni()).append("</td>");
            tableRows.append("<td>").append(usuario.getTipoUsuario().name()).append("</td>");
            tableRows.append("<td>").append(usuario.getEstado().name()).append("</td>");
            tableRows.append("<td>");
            tableRows.append("<button class='btn btn-warning btn-sm m-1'>");
            tableRows.append("<i class='bi bi-pencil'></i>");
            tableRows.append("</button>");
            tableRows.append("<button class='btn btn-danger btn-sm m-1'>");
            tableRows.append("<i class='bi bi-trash'></i>");
            tableRows.append("</button>");
            tableRows.append("</td>");
            tableRows.append("</tr>");
        }
        return tableRows.toString();
    }

}
