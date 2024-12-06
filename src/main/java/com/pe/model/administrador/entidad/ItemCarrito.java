package com.pe.model.administrador.entidad;

import java.util.Comparator;
import java.util.Objects;

public class ItemCarrito implements Comparable<ItemCarrito> {
    public static final Comparator<ItemCarrito> ITEM_CARRITO_COMPARATOR_NATURAL_ORDER = Comparator.comparing(ItemCarrito::getNombreProducto).thenComparing(ItemCarrito::getPrecio);
    private String imagen;
    private String nombreProducto;
    private double precio;
    private int cantidad;
    private int idVariante;

    public ItemCarrito() {
    }

    public ItemCarrito(String imagen, String nombreProducto, double precio, int cantidad, int idVariante) {
        this.imagen = imagen;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.idVariante = idVariante;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(int idVariante) {
        this.idVariante = idVariante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCarrito that)) return false;
        return Double.compare(getPrecio(), that.getPrecio()) == 0 && getCantidad() == that.getCantidad() && getIdVariante() == that.getIdVariante() && Objects.equals(getImagen(), that.getImagen()) && Objects.equals(getNombreProducto(), that.getNombreProducto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getImagen(), getNombreProducto(), getPrecio(), getCantidad(), getIdVariante());
    }

    @Override
    public String toString() {
        return "ItemCarrito{" +
                "imagen='" + imagen + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", idVariante=" + idVariante +
                '}';
    }

    @Override
    public int compareTo(ItemCarrito o) {
        return ITEM_CARRITO_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }
}
