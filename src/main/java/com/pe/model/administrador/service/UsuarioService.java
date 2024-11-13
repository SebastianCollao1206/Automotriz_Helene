package com.pe.model.administrador.service;

import com.google.common.base.Preconditions;
import com.pe.model.administrador.dao.UsuarioDAO;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.util.Validaciones;

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
        usuarioDAO.cargarUsuarios(this.usuarios);
    }

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void cargarUsuarios() throws SQLException {
        usuarioDAO.cargarUsuarios(usuarios);
    }

    public TreeSet<Usuario> getUsuarios() {
        return usuarios;
    }

    public void eliminarUsuario(int id) throws SQLException {
        cargarUsuarios();
        Usuario usuario = obtenerUsuarioPorId(id);
        if (usuario != null && usuario.getEstado() == Usuario.EstadoUsuario.Activo) {
            usuario.setEstado(Usuario.EstadoUsuario.Inactivo);
            usuarioDAO.actualizarUsuario(usuario);
            cargarUsuarios();
        } else {
            throw new SQLException("El usuario no se puede cambiar a inactivo o no existe.");
        }
    }

    public void agregarUsuario(String nombre, String correo, String dni, String tipo, String estado, String contrasena) throws Exception {
        Validaciones.validarNombre(nombre);
        Validaciones.validarCorreo(correo);
        Validaciones.validarDNI(dni);
        Validaciones.validarPassword(contrasena);
        if (existeCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado en el sistema");
        }
        if (existeDNI(dni)) {
            throw new IllegalArgumentException("El DNI ya está registrado en el sistema");
        }
        nombre = Validaciones.sanitizarEntrada(nombre);
        correo = Validaciones.sanitizarEntrada(correo);

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
        cargarUsuarios();
    }
    private boolean existeCorreo(String correo) {
        return usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo));
    }
    private boolean existeDNI(String dni) {
        return usuarios.stream()
                .anyMatch(u -> u.getDni().equals(dni));
    }

    //Encriptacion SHA-256
    private byte[] encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes());
    }

    // Autenticacion
    public Usuario autenticarUsuario(String correo, String password) throws Exception {
        Validaciones.validarCorreo(correo);
        Validaciones.validarPassword(password);
        correo = Validaciones.sanitizarEntrada(correo);
        usuarioDAO.cargarUsuarios(usuarios);
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equals(correo)) {
                Preconditions.checkArgument(usuario.getEstado() == Usuario.EstadoUsuario.Activo, "El usuario debe estar activo");
                byte[] contrasenaEncriptada = encryptPassword(password);
                if (Arrays.equals(usuario.getContrasena(), contrasenaEncriptada)) {
                    return usuario;
                }
            }
        }
        return null;
    }

    // Metodo para buscar usuarios
    public TreeSet<Usuario> buscarUsuarios(String nombre, String dni, String tipo, String estado) {
        TreeSet<Usuario> usuariosFiltrados = new TreeSet<>(Usuario.USUARIO_COMPARATOR_NATURAL_ORDER);
        for (Usuario usuario : usuarios) {
            if (verificarUsuario(usuario, nombre, dni, tipo, estado)) {
                usuariosFiltrados.add(usuario);
            }
        }
        return usuariosFiltrados;
    }

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

    public Usuario obtenerUsuarioPorId(int id) {
        return usuarios.stream()
                .filter(usuario -> usuario.getIdUsuario() == id)
                .findFirst()
                .orElse(null);
    }

    public void actualizarUsuario(int id, String nombre, String correo, String dni,
                                  String tipo, String estado) throws SQLException {
        Validaciones.validarNombre(nombre);
        Validaciones.validarCorreo(correo);
        Validaciones.validarDNI(dni);
        if (existeCorreo(correo) && !correo.equals(obtenerUsuarioPorId(id).getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado en el sistema");
        }
        if (existeDNI(dni) && !dni.equals(obtenerUsuarioPorId(id).getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado en el sistema");
        }
        nombre = Validaciones.sanitizarEntrada(nombre);
        correo = Validaciones.sanitizarEntrada(correo);

        Usuario usuario = obtenerUsuarioPorId(id);
        if (usuario != null) {
            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setDni(dni);
            usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(tipo));
            usuario.setEstado(Usuario.EstadoUsuario.valueOf(estado));
            usuarioDAO.actualizarUsuario(usuario);
            cargarUsuarios();
        } else {
            throw new SQLException("Usuario no encontrado");
        }
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
