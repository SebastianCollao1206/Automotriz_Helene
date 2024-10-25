package com.pe.model.service;

import com.pe.model.dao.UsuarioDAO;
import com.pe.model.entidad.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void agregarUsuario(String nombre, String correo, String dni, String tipo, String estado, String contrasena) throws Exception {
        byte[] contrasenaEncriptada = encryptPassword(contrasena);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setDni(dni);
        usuario.setContrasena(contrasenaEncriptada);
        usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(tipo));
        usuario.setEstado(Usuario.EstadoUsuario.valueOf(estado));
        usuario.setFechaRegistro(LocalDate.now());

        usuarioDAO.agregarUsuario(usuario);
    }

    //Encriptacion SHA-256
    private byte[] encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes());
    }

    // Autenticacion
    public Usuario autenticarUsuario(String correo, String password) throws Exception {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorCorreo(correo);
        if (usuario != null) {
            byte[] contrasenaEncriptada = encryptPassword(password);
            if (Arrays.equals(usuario.getContrasena(), contrasenaEncriptada)) {
                return usuario;
            }
        }
        return null;
    }

}
