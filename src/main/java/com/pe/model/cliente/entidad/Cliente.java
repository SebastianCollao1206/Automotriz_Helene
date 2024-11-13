package com.pe.model.cliente.entidad;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Cliente implements Comparable<Cliente> {

    public static final Comparator<Cliente> CLIENTE_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Cliente::getIdCliente).thenComparing(Cliente::getCorreo)
            .thenComparing(Cliente::getFechaRegistro);

    private int idCliente;
    private String correo;
    private byte[] contrasena;
    private LocalDate fechaRegistro;
    private String dni;
    private String nombre;

    @Override
    public int compareTo(Cliente o) {
        return CLIENTE_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    public Cliente() {
    }

    public Cliente(int idCliente, String correo, byte[] contrasena, LocalDate fechaRegistro, String dni, String nombre) {
        this.idCliente = idCliente;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaRegistro = fechaRegistro;
        this.dni = dni;
        this.nombre = nombre;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return getIdCliente() == cliente.getIdCliente() && Objects.equals(getCorreo(), cliente.getCorreo()) && Objects.deepEquals(getContrasena(), cliente.getContrasena()) && Objects.equals(getFechaRegistro(), cliente.getFechaRegistro()) && Objects.equals(getDni(), cliente.getDni()) && Objects.equals(getNombre(), cliente.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCliente(), getCorreo(), Arrays.hashCode(getContrasena()), getFechaRegistro(), getDni(), getNombre());
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", correo='" + correo + '\'' +
                ", contrasena=" + Arrays.toString(contrasena) +
                ", fechaRegistro=" + fechaRegistro +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
