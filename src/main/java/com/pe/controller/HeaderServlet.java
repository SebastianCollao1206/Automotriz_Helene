package com.pe.controller;

import com.pe.model.entidad.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/header")
public class HeaderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Leer el contenido de header.html
        StringBuilder headerHtml = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getServletContext().getResourceAsStream("/estatic/header.html")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Reemplazar los marcadores de posición
                if (usuario != null) {
                    line = line.replace("${usuario.nombre}", usuario.getNombre());
                    line = line.replace("${usuario.tipoUsuario}", usuario.getTipoUsuario().name()); // Convertir a String
                } else {
                    line = line.replace("${usuario.nombre}", "Invitado");
                    line = line.replace("${usuario.tipoUsuario}", "N/A");
                }
                headerHtml.append(line).append("\n");
            }
        }

        // Enviar el HTML generado al cliente
        try (PrintWriter out = response.getWriter()) {
            out.println(headerHtml.toString());
        }
    }

}
