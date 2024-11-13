package com.pe.controller.administrador.usuarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.model.administrador.html.UsuarioHtml;
import com.pe.model.administrador.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/usuario/editar")
public class EditarUsuarioServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditarUsuarioServlet.class);
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
                usuarioService.cargarUsuarios();
                int id = Integer.parseInt(idParam);
                Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
                if (usuario != null) {
                    String html = UsuarioHtml.generarHtmlEdicionUsuario(usuario);
                    request.setAttribute("content", html);
                    super.doGet(request, response);
                } else {
                    logger.warn("Usuario no encontrado con ID: {}", id);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                }
            } catch (NumberFormatException e) {
                logger.error("ID de usuario inválido: {}", idParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            } catch (SQLException e) {
                logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos: " + e.getMessage());
            }
        } else {
            logger.warn("ID de usuario no proporcionado.");
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
            usuarioService.cargarUsuarios();
            mensaje = "Usuario actualizado exitosamente!";
            logger.info("Usuario actualizado: ID = {}", id);
        } catch (IllegalArgumentException e) {
            mensaje = e.getMessage();
            logger.warn("Error de validación al actualizar usuario: {}", e.getMessage());
        } catch (Exception e) {
            mensaje = "Error al actualizar el usuario: " + e.getMessage();
            logger.error("Error al actualizar el usuario: {}", e.getMessage(), e);
        }
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        response.sendRedirect(redirigirUrl);
    }
}
