package com.pe.model.entidad;

import java.util.Comparator;
import java.util.Objects;

public class Producto implements Comparable<Producto> {
    public static final Comparator<Producto> PRODUCTO_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Producto::getIdProducto).thenComparing(Producto::getNombre)
            .thenComparing(Producto::getDescripcion).thenComparing(Producto::getIdCategoria);
    private int idProducto;
    private String nombre;
    private String descripcion;
    private int idCategoria;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, String descripcion, int idCategoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto producto)) return false;
        return getIdProducto() == producto.getIdProducto() && getIdCategoria() == producto.getIdCategoria() && Objects.equals(getNombre(), producto.getNombre()) && Objects.equals(getDescripcion(), producto.getDescripcion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdProducto(), getNombre(), getDescripcion(), getIdCategoria());
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", idCategoria=" + idCategoria +
                '}';
    }

    @Override
    public int compareTo(Producto o) {
        return PRODUCTO_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }
}
