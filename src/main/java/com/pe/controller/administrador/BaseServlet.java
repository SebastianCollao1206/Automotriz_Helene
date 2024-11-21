package com.pe.controller.administrador;

import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.model.administrador.html.UsuarioHtml;
import com.pe.model.administrador.service.UsuarioService;
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


@WebServlet("/base")
public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
    protected final UsuarioService usuarioService;

    protected abstract String getContentPage();
    protected abstract PermisoUsuario getPermiso();

    public BaseServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("/");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            String headerHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/estatic/header.html")));

            headerHtml = headerHtml.replace("${usuarioNombre}", usuario.getNombre());
            headerHtml = headerHtml.replace("${usuarioTipo}", usuario.getTipoUsuario().name());

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

            // Imprimimos el footer con los cierres y scripts
//            String footerHtml = new String(Files.readAllBytes(
//                    Paths.get("src/main/resources/html/admin/estatic/footer.html")));
//            out.println(footerHtml);

        } catch (IOException e) {
            logger.error("Error al cargar el contenido: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar el contenido: " + e.getMessage());
        }
    }
}
