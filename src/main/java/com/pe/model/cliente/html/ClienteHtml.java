package com.pe.model.cliente.html;

import com.pe.model.cliente.entidad.Cliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClienteHtml {

    public static String generarMensajeAlerta(String mensaje, String redireccion) {
        StringBuilder html = new StringBuilder();
        html.append("<script type='text/javascript'>");
        html.append("    alert('").append(mensaje).append("');");
        html.append("    window.location='").append(redireccion).append("';");
        html.append("</script>");
        return html.toString();
    }

    public static String generarHeader(Cliente cliente, HttpServletRequest request) throws IOException {
        String headerHtml = new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/estatic/header.html")));

        HttpSession session = request.getSession(true);
        Integer cartCounter = (Integer) session.getAttribute("cartCounter");
        int cantidad = cartCounter != null ? cartCounter : 0;

        headerHtml = headerHtml.replace(
                "${cartCounter}",
                String.valueOf(cantidad)
        );

        if (cliente != null) {
            String userCircleHtml = String.format(
                    "<div class='dropdown'>" +
                            "<a class='nav-link px-lg-4 dropdown-toggle' href='#' id='userDropdown' role='button' " +
                            "data-bs-toggle='dropdown' aria-expanded='false'>" +
                            "<div class='user-circle'>%s</div>" +
                            "</a>" +
                            "<ul class='dropdown-menu dropdown-menu-end mt-3' aria-labelledby='userDropdown'>" +
                            "<li><a class='dropdown-item' href='mi-cuenta.html'>Mi cuenta</a></li>" +
                            "<li><hr class='dropdown-divider'></li>" +
                            "<li><a class='dropdown-item' href='/cliente/logout'>Cerrar sesión</a></li>" +
                            "</ul>" +
                            "</div>",
                    cliente.getCorreo().substring(0, 1).toUpperCase()
            );

            headerHtml = headerHtml.replaceAll(
                    "<div>\\s*<a class=\"nav-link px-lg-4\" href=\"/cliente/login\"><i class=\"bi bi-person-fill\"></i></a>\\s*</div>",
                    userCircleHtml
            );
        }
        return headerHtml;
    }

    public static String generarFooter() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/html/cliente/estatic/footer.html")));
    }
}
