package com.pe.controller.administrador;

import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.model.administrador.entidad.notificaciones.Notificacion;
import com.pe.model.administrador.html.UsuarioHtml;
import com.pe.model.administrador.service.NotificacionService;
import com.pe.model.administrador.service.UsuarioService;
import com.pe.model.administrador.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/base")
public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
    protected final UsuarioService usuarioService;
    private final NotificacionService notificacionService;
    private final VarianteService varianteService;

    protected abstract String getContentPage();
    protected abstract PermisoUsuario getPermiso();

    public BaseServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
        this.notificacionService = new NotificacionService();
        this.varianteService = new VarianteService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<Notificacion> todasNotificaciones = notificacionService.obtenerTodasLasNotificaciones();

        int contadorNotificaciones = 0;
        try {
            contadorNotificaciones = notificacionService.contarNotificacionesNoLeidas(usuario.getIdUsuario());
        } catch (SQLException e) {
            logger.error("Error al contar notificaciones no leídas", e);
            contadorNotificaciones = 0;
        }

        StringBuilder notificacionesHtml = new StringBuilder();
        for (Notificacion notificacion : todasNotificaciones) {
            int idVariante = 0;
            String mensaje = notificacion.getMensaje();
            if (mensaje.contains("variante con código")) {
                String codigo = mensaje.split("código ")[1].split(" ")[0];
                idVariante = varianteService.obtenerIdVariantePorCodigo(codigo);
            }

            notificacionesHtml.append(String.format(
                    "<a href='/variante/editar?id=%d' class='notification text-decoration-none p-2 mb-3'>" +
                            "<h7 class='fw-bold'><i class='bi bi-exclamation-circle me-3'></i>Alerta</h7>" +
                            "<p class='mt-2'>%s</p>" +
                            "<small class='text-muted'>%s</small>" +
                            "</a>",
                    idVariante,
                    notificacion.getMensaje(),
                    notificacion.getFecha().toString()
            ));
        }

        if (usuario == null) {
            response.sendRedirect("/");
            return;
        }

        try {
            notificacionService.cargarNotificaciones();
        } catch (SQLException e) {
            logger.error("Error al cargar notificaciones", e);
        }

        try (PrintWriter out = response.getWriter()) {
            String headerHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/estatic/header.html")));

            headerHtml = headerHtml.replace("${usuarioNombre}", usuario.getNombre());
            headerHtml = headerHtml.replace("${usuarioTipo}", usuario.getTipoUsuario().name());

            headerHtml = headerHtml.replace("${contadorNotificaciones}", String.valueOf(contadorNotificaciones));
            headerHtml = headerHtml.replace("${listaNotificaciones}", notificacionesHtml.toString());


            out.println(headerHtml);

            request.getRequestDispatcher("/admin/estatic/sidebar.html").include(request, response);

            if (!usuarioService.tienePermiso(usuario, getPermiso())) {
                request.getRequestDispatcher("/admin/acceso_denegado.html").include(request, response);
                return;
            }

            String contentPage = getContentPage();
            String content = (String) request.getAttribute("content");

            if (content == null) {
                content = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin" + getContentPage())));
            }

            out.println(content);

            String mensaje = (String) request.getAttribute("mensaje");
            if (mensaje != null) {
                out.println(UsuarioHtml.generarMensajeAlerta(mensaje, (String) request.getAttribute("redirigirUrl")));
            }
        } catch (IOException e) {
            logger.error("Error al cargar el contenido: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar el contenido: " + e.getMessage());
        }
    }
}
