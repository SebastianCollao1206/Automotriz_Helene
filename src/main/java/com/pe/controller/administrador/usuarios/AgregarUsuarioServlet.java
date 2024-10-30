package com.pe.controller.administrador.usuarios;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/usuario/agregar")
public class AgregarUsuarioServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(AgregarUsuarioServlet.class);
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
        String redirigirUrl = "/usuario/agregar";

        try {
            usuarioService.agregarUsuario(nombre, correo, dni, tipo, estado, contrasena);
            mensaje = "Usuario agregado exitosamente!";
            logger.info("Usuario agregado: Nombre = {}, Correo = {}", nombre, correo);
        } catch (Exception e) {
            mensaje = "Error al agregar el usuario";
            logger.error("Error al agregar el usuario: {}", e.getMessage(), e);
        }

        // Establecer el mensaje en el request
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);

        // Llamar al metodo
        super.doGet(request, response);
    }
}
