package com.pe.model.administrador.entidad;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class Comentario implements Comparable<Comentario>{
    public static final Comparator<Comentario> COMENTARIO_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Comentario::getIdComentario).thenComparing(Comentario::getComentario)
            .thenComparing(Comentario::getFecha).thenComparing(Comentario::getIdCliente)
            .thenComparing(Comentario::getEstado);

    @Override
    public int compareTo(Comentario o) {
        return COMENTARIO_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    private int idComentario;
    private String comentario;
    private LocalDate fecha;
    private int idCliente;
    private Comentario.EstadoComentario estado;

    public enum EstadoComentario {
        Activo, Inactivo
    }

    public Comentario() {
    }

    public Comentario(int idComentario, String comentario, LocalDate fecha, int idCliente, EstadoComentario estado) {
        this.idComentario = idComentario;
        this.comentario = comentario;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.estado = estado;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public EstadoComentario getEstado() {
        return estado;
    }

    public void setEstado(EstadoComentario estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comentario that)) return false;
        return getIdComentario() == that.getIdComentario() && getIdCliente() == that.getIdCliente() && Objects.equals(getComentario(), that.getComentario()) && Objects.equals(getFecha(), that.getFecha()) && getEstado() == that.getEstado();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdComentario(), getComentario(), getFecha(), getIdCliente(), getEstado());
    }

    @Override
    public String toString() {
        return "Comentario{" +
                "idComentario=" + idComentario +
                ", comentario='" + comentario + '\'' +
                ", fecha=" + fecha +
                ", idCliente=" + idCliente +
                ", estado=" + estado +
                '}';
    }

}
