package com.pe.controller.administrador;

import com.pe.model.entidad.Usuario;
import com.pe.model.html.UsuarioHtml;
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


@WebServlet("/base")
public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
    protected abstract String getContentPage();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Obtener la sesión actual
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verificar si el usuario está autenticado
        if (usuario == null) {
            response.sendRedirect("/");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            // Leer el HTML del header desde el archivo
            String headerHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/estatic/header.html")));

            // Reemplazar los marcadores con la información del usuario
            headerHtml = headerHtml.replace("${usuarioNombre}", usuario.getNombre());
            headerHtml = headerHtml.replace("${usuarioTipo}", usuario.getTipoUsuario().name());

            // Escribir el HTML del header modificado en la respuesta
            out.println(headerHtml);

            // Incluir el sidebar
            request.getRequestDispatcher("/estatic/sidebar.html").include(request, response);

            String contentPage = getContentPage();
            String content = (String) request.getAttribute("content");

            // Si no hay contenido establecido, cargar el HTML del archivo
            if (content == null) {
                content = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin" + getContentPage())));
            }

            // Escribir el contenido específico de la página
            out.println(content);

            // Mostrar mensaje si existe
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
