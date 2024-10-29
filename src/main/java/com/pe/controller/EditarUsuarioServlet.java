package com.pe.controller;

import com.pe.model.entidad.Usuario;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet("/usuario/editar")
public class EditarUsuarioServlet extends BaseServlet {
    private final UsuarioService usuarioService;

    public EditarUsuarioServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected String getContentPage() {
        return "/editar_usuario.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
                if (usuario != null) {
                    String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/editar_usuario.html")));
                    html = html.replace("${usuario.idUsuario}", String.valueOf(usuario.getIdUsuario()));
                    html = html.replace("${usuario.nombre}", usuario.getNombre());
                    html = html.replace("${usuario.correo}", usuario.getCorreo());
                    html = html.replace("${usuario.dni}", usuario.getDni());
                    html = html.replace("${usuario.tipoUsuario}", usuario.getTipoUsuario().name());
                    html = html.replace("${usuario.estado}", usuario.getEstado().name());

                    html = html.replace("${tipo.jefeSelected}", usuario.getTipoUsuario().name().equals("Jefe") ? "selected" : "");
                    html = html.replace("${tipo.trabajadorSelected}", usuario.getTipoUsuario().name().equals("Trabajador") ? "selected" : "");
                    html = html.replace("${estado.activoSelected}", usuario.getEstado().name().equals("Activo") ? "selected" : "");
                    html = html.replace("${estado.inactivoSelected}", usuario.getEstado().name().equals("Inactivo") ? "selected" : "");

                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario no proporcionado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String dni = request.getParameter("dni");
        String tipo = request.getParameter("tipo");
        String estado = request.getParameter("estado");

        String mensaje;
        String redirigirUrl = "/usuario/listar";

        try {
            int id = Integer.parseInt(idParam);
            usuarioService.actualizarUsuario(id, nombre, correo, dni, tipo, estado);
            mensaje = "Usuario actualizado exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al actualizar el usuario: " + e.getMessage();
        }

        // Establecer atributos para el mensaje y la redirección
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Redirigir a la lista de usuarios
        response.sendRedirect(redirigirUrl);
    }
}
