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
        String tipoMensaje;
        String redirigirUrl = "/usuario/agregar";
        try {
            usuarioService.agregarUsuario(nombre, correo, dni, tipo, estado, contrasena);
            mensaje = "Usuario agregado exitosamente!";
            tipoMensaje = "success";
            logger.info("Usuario agregado: Nombre = {}, Correo = {}", nombre, correo);
        } catch (IllegalArgumentException e) {
            mensaje = e.getMessage();
            tipoMensaje = "error";
            logger.warn("Error de validación al agregar usuario: {}", e.getMessage());
        } catch (SecurityException e) {
            mensaje = "Error de seguridad: " + e.getMessage();
            tipoMensaje = "error";
            logger.error("Error de seguridad al agregar usuario: {}", e.getMessage());
        } catch (Exception e) {
            mensaje = "Error interno del servidor al procesar la solicitud.";
            tipoMensaje = "error";
            logger.error("Error inesperado al agregar usuario: {}", e.getMessage(), e);
        }
        if ("error".equals(tipoMensaje)) {
            request.setAttribute("nombrePrevio", nombre);
            request.setAttribute("correoPrevio", correo);
            request.setAttribute("dniPrevio", dni);
            request.setAttribute("tipoPrevio", tipo);
            request.setAttribute("estadoPrevio", estado);
            request.setAttribute("contrasenaPrevio", contrasena);
        }
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", tipoMensaje);
        request.setAttribute("redirigirUrl", redirigirUrl);
        super.doGet(request, response);
    }
}
