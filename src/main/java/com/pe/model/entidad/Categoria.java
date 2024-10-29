package com.pe.model.entidad;

import java.util.Comparator;
import java.util.Objects;

public class Categoria implements Comparable<Categoria> {
    public static final Comparator<Categoria> CATEGORIA_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Categoria::getIdCategoria).thenComparing(Categoria::getNombre)
            .thenComparing(Categoria::getEstado);
    private int idCategoria;
    private String nombre;
    private EstadoCategoria estado;

    public Categoria(int idCategoria, String nombre, EstadoCategoria estado) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.estado = estado;
    }

    public Categoria() {
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoCategoria getEstado() {
        return estado;
    }

    public void setEstado(EstadoCategoria estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria categoria)) return false;
        return getIdCategoria() == categoria.getIdCategoria() && Objects.equals(getNombre(), categoria.getNombre()) && getEstado() == categoria.getEstado();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCategoria(), getNombre(), getEstado());
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }

    @Override
    public int compareTo(Categoria o) {
        return CATEGORIA_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }


    public enum EstadoCategoria {
        Activo, Inactivo
    }
}
