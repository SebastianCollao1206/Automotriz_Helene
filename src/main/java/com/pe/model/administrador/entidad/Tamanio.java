package com.pe.model.administrador.entidad;

import java.util.Comparator;
import java.util.Objects;

public class Tamanio implements Comparable<Tamanio> {
    public static final Comparator<Tamanio> TAMANIO_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Tamanio::getIdTamanio).thenComparing(Tamanio::getUnidadMedida).
            thenComparing(Tamanio::getEstado);
    private int idTamanio;
    private String unidadMedida;
    private EstadoTamanio estado;

    public Tamanio() {
    }

    public Tamanio(int idTamanio, String unidadMedida, EstadoTamanio estado) {
        this.idTamanio = idTamanio;
        this.unidadMedida = unidadMedida;
        this.estado = estado;
    }

    public int getIdTamanio() {
        return idTamanio;
    }

    public void setIdTamanio(int idTamanio) {
        this.idTamanio = idTamanio;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public EstadoTamanio getEstado() {
        return estado;
    }

    public void setEstado(EstadoTamanio estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tamanio tamanio)) return false;
        return getIdTamanio() == tamanio.getIdTamanio() && Objects.equals(getUnidadMedida(), tamanio.getUnidadMedida()) && getEstado() == tamanio.getEstado();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdTamanio(), getUnidadMedida(), getEstado());
    }

    @Override
    public String toString() {
        return "Tamanio{" +
                "idTamanio=" + idTamanio +
                ", unidadMedida='" + unidadMedida + '\'' +
                ", estado=" + estado +
                '}';
    }

    @Override
    public int compareTo(Tamanio o) {
        return TAMANIO_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    public enum EstadoTamanio {
        Activo, Inactivo
    }
}
