package com.pe.controller;

import com.pe.model.html.UsuarioHtml;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/usuario/agregar")
public class AgregarUsuarioServlet extends BaseServlet{

    private final UsuarioService usuarioService;

    public AgregarUsuarioServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_usuario.html";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String dni = request.getParameter("dni");
        String tipo = request.getParameter("tipo");
        String estado = request.getParameter("estado");
        String contrasena = request.getParameter("contrasena");

        String mensaje;
        String redirigirUrl = "/usuario/listar";

        try {
            usuarioService.agregarUsuario(nombre, correo, dni, tipo, estado, contrasena);
            mensaje = "Usuario agregado exitosamente!";
        } catch (Exception e) {
            mensaje = "Error al agregar el usuario";
        }

        // Establecer el mensaje en el request
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Llamar al metodo
        super.doGet(request, response);
    }
}
