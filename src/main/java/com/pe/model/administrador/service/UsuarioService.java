package com.pe.model.administrador.service;

import com.google.common.base.Preconditions;
import com.pe.model.administrador.dao.UsuarioDAO;
import com.pe.model.administrador.entidad.PermisoUsuario;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.util.Validaciones;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

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

    public boolean tienePermiso(Usuario usuario, PermisoUsuario permisoRequerido) {
        switch (permisoRequerido) {
            case TODOS:
                return true;
            case SOLO_JEFE:
                return usuario.getTipoUsuario() == Usuario.TipoUsuario.Jefe;
            case SOLO_TRABAJADOR:
                return usuario.getTipoUsuario() == Usuario.TipoUsuario.Trabajador;
            default:
                return false;
        }
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarios.stream()
                .filter(usuario -> usuario.getCorreo().equalsIgnoreCase(correo) &&
                        usuario.getEstado() == Usuario.EstadoUsuario.Activo)
                .findFirst()
                .orElse(null);
    }

    //recuperacion de contras
    private static final String SENDER_EMAIL = "tallerhelene@gmail.com";
    private static final String SENDER_PASSWORD = "zlfz xnci qesh imqo";

    public String generarCodigoVerificacion() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    public void enviarCodigoVerificacion(String destinatario, String codigo) throws Exception {
        SimpleEmail email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));
        email.setStartTLSEnabled(true);
        email.setFrom(SENDER_EMAIL);
        email.setSubject("Código de verificación - Recuperación de contraseña");
        email.setMsg("Tu código de verificación es: " + codigo);
        email.addTo(destinatario);
        email.send();
    }

    public void actualizarContrasena(String correo, String nuevaContrasena) throws Exception {
        Usuario usuario = obtenerUsuarioPorCorreo(correo);
        if (usuario != null) {
            Validaciones.validarPassword(nuevaContrasena);
            byte[] contrasenaEncriptada = encryptPassword(nuevaContrasena);
            usuario.setContrasena(contrasenaEncriptada);
            usuarioDAO.actualizarContrasenaUsuario(usuario, nuevaContrasena);
            cargarUsuarios();
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }

    public List<Integer> obtenerTodosLosUsuarioIds() {
        List<Integer> usuarioIdsActivos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.getEstado() == Usuario.EstadoUsuario.Activo) {
                usuarioIdsActivos.add(usuario.getIdUsuario());
            }
        }
        return usuarioIdsActivos;
    }

}
