package com.pe.model.administrador.entidad;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Usuario implements Comparable<Usuario>{

    public static final Comparator<Usuario> USUARIO_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Usuario::getIdUsuario).thenComparing(Usuario::getNombre)
            .thenComparing(Usuario::getCorreo).thenComparing(Usuario::getTipoUsuario).thenComparing(Usuario::getEstado)
            .thenComparing(Usuario::getFechaRegistro).thenComparing(Usuario::getDni);

    private int idUsuario;
    private String nombre;
    private String correo;
    private byte[] contrasena;
    private TipoUsuario tipoUsuario;
    private EstadoUsuario estado;
    private LocalDate fechaRegistro;
    private String dni;

    @Override
    public int compareTo(Usuario o) {
        return USUARIO_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    public enum TipoUsuario {
        Jefe, Trabajador
    }

    public enum EstadoUsuario {
        Activo, Inactivo
    }

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String correo, byte[] contrasena, TipoUsuario tipoUsuario, EstadoUsuario estado, LocalDate fechaRegistro, String dni) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.dni = dni;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public byte[] getContrasena() {
        return contrasena;
    }

    public void setContrasena(byte[] contrasena) {
        this.contrasena = contrasena;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return getIdUsuario() == usuario.getIdUsuario() && Objects.equals(getNombre(), usuario.getNombre()) && Objects.equals(getCorreo(), usuario.getCorreo()) && Objects.deepEquals(getContrasena(), usuario.getContrasena()) && getTipoUsuario() == usuario.getTipoUsuario() && getEstado() == usuario.getEstado() && Objects.equals(getFechaRegistro(), usuario.getFechaRegistro()) && Objects.equals(getDni(), usuario.getDni());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUsuario(), getNombre(), getCorreo(), Arrays.hashCode(getContrasena()), getTipoUsuario(), getEstado(), getFechaRegistro(), getDni());
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", contrasena=" + Arrays.toString(contrasena) +
                ", tipoUsuario=" + tipoUsuario +
                ", estado=" + estado +
                ", fechaRegistro=" + fechaRegistro +
                ", dni='" + dni + '\'' +
                '}';
    }
}
