package com.pe.controller.cliente;

import com.pe.model.cliente.entidad.Cliente;
import com.pe.model.cliente.html.ClienteHtml;
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

@WebServlet("/baseClient")
public abstract class BaseClientServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseClientServlet.class);
    protected abstract String getContentPage();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            logger.info("Cliente encontrado en la sesión: {}", cliente.getCorreo());
        } else {
            logger.warn("No hay cliente en la sesión.");
        }

        try (PrintWriter out = response.getWriter()) {
            String headerHtml = ClienteHtml.generarHeader(cliente, request);
            out.println(headerHtml);

            String contentPage = getContentPage();
            String content = (String) request.getAttribute("content");

            if (content == null) {
                content = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente" + getContentPage())));
            }

            out.println(content);

            String footerHtml = ClienteHtml.generarFooter();
            out.println(footerHtml);

            String mensaje = (String) request.getAttribute("mensaje");
            if (mensaje != null) {
                out.println(ClienteHtml.generarMensajeAlerta(mensaje, (String) request.getAttribute("redirigirUrl")));
            }

        } catch (IOException e) {
            logger.error("Error al cargar el contenido: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar el contenido: " + e.getMessage());
        }
    }
}
