package com.pe.model.administrador.entidad;

import java.util.Comparator;
import java.util.Objects;

public class MetodoPago implements Comparable<MetodoPago>{

    public static final Comparator<MetodoPago> METODO_PAGO_COMPARATOR_NATURAL_ORDER = Comparator.comparing(MetodoPago::getIdMetodoPago).thenComparing(MetodoPago::getEstado)
            .thenComparing(MetodoPago::getEstado);
    private int idMetodoPago;
    private String nombre;
    private MetodoPago.EstadoMetodoPago estado;

    @Override
    public int compareTo(MetodoPago o) {
        return METODO_PAGO_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    public enum EstadoMetodoPago {
        Activo, Inactivo
    }

    public MetodoPago(int idMetodoPago, String nombre, EstadoMetodoPago estado) {
        this.idMetodoPago = idMetodoPago;
        this.nombre = nombre;
        this.estado = estado;
    }

    public MetodoPago() {
    }

    public int getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(int idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoMetodoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoMetodoPago estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetodoPago that)) return false;
        return getIdMetodoPago() == that.getIdMetodoPago() && Objects.equals(getNombre(), that.getNombre()) && getEstado() == that.getEstado();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdMetodoPago(), getNombre(), getEstado());
    }

    @Override
    public String toString() {
        return "MetodoPago{" +
                "idMetodoPago=" + idMetodoPago +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}
