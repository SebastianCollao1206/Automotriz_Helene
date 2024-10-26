package com.pe.controller;

import com.pe.model.entidad.Usuario;
import com.pe.model.html.UsuarioHtml;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;


@WebServlet("/base")
public abstract class BaseServlet extends HttpServlet {
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
                content = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin" + contentPage)));
            }

            // Escribir el contenido específico de la página
            out.println(content);

            // Mostrar mensaje si existe
            String mensaje = (String) request.getAttribute("mensaje");
            if (mensaje != null) {
                out.println(UsuarioHtml.generarMensajeAlerta(mensaje, (String) request.getAttribute("redirigirUrl")));
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar el contenido: " + e.getMessage());
        }

    }
}
