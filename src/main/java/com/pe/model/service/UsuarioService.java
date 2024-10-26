package com.pe.model.service;

import com.pe.model.dao.UsuarioDAO;
import com.pe.model.entidad.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.TreeSet;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;
    private final TreeSet<Usuario> usuarios;

    public UsuarioService() throws SQLException {
        this.usuarioDAO = new UsuarioDAO();
        this.usuarios = new TreeSet<>(Usuario.USUARIO_COMPARATOR_NATURAL_ORDER);
    }

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void cargarUsuarios() throws SQLException {
        usuarioDAO.cargarUsuarios(usuarios);
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

    public TreeSet<Usuario> getUsuarios() {
        return usuarios;
    }

    // Métodos para buscar usuarios
    public TreeSet<Usuario> buscarUsuarios(String nombre, String dni, String tipo, String estado) {
        TreeSet<Usuario> usuariosFiltrados = new TreeSet<>(Usuario.USUARIO_COMPARATOR_NATURAL_ORDER);
        for (Usuario usuario : usuarios) {
            if (verificarUsuario(usuario, nombre, dni, tipo, estado)) {
                usuariosFiltrados.add(usuario);
            }
        }
        return usuariosFiltrados;
    }

    // Verificar si el usuario cumple con los filtros
    private boolean verificarUsuario(Usuario usuario, String nombre, String dni, String tipo, String estado) {
        boolean valido = true;
        if (nombre != null && !nombre.isEmpty() && !usuario.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
            valido = false;
        }
        if (dni != null && !dni.isEmpty() && !usuario.getDni().equals(dni)) {
            valido = false;
        }
        if (tipo != null && !tipo.isEmpty() && !usuario.getTipoUsuario().name().equals(tipo)) {
            valido = false;
        }
        if (estado != null && !estado.isEmpty() && !usuario.getEstado().name().equals(estado)) {
            valido = false;
        }
        return valido;
    }

    public TreeSet<String> getTiposUsuarioSet() {
        TreeSet<String> tiposUsuario = new TreeSet<>();
        for (Usuario.TipoUsuario tipo : Usuario.TipoUsuario.values()) {
            tiposUsuario.add(tipo.name());
        }
        return tiposUsuario;
    }

    public TreeSet<String> getEstadosUsuarioSet() {
        TreeSet<String> estadosUsuario = new TreeSet<>();
        for (Usuario.EstadoUsuario estado : Usuario.EstadoUsuario.values()) {
            estadosUsuario.add(estado.name());
        }
        return estadosUsuario;
    }
}
