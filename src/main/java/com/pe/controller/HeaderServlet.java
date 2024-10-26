package com.pe.controller;

import com.pe.model.entidad.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/header/usuario-info")
public class HeaderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        PrintWriter out = response.getWriter();

        out.println("<div class='texto-admin mx-2'>");
        if (usuario != null) {
            out.println("    <div class='small fw-bold'>Hola, " + usuario.getNombre() + "</div>");
            out.println("    <div class='small text-muted'>" + usuario.getTipoUsuario() + "</div>");
        } else {
            out.println("    <div class='small fw-bold'>Usuario no identificado</div>");
        }
        out.println("</div>");
    }
}
