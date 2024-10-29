package com.pe.model.entidad;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

public class Variante implements Comparable<Variante>{
    public static final Comparator<Variante> VARIANTE_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Variante::getIdVariante).thenComparing(Variante::getCodigo).
            thenComparing(Variante::getPrecio).thenComparing(Variante::getImagen).thenComparing(Variante::getStock)
            .thenComparing(Variante::getCantidad).thenComparing(Variante::getIdProducto).thenComparing(Variante::getIdTamanio);
    private int idVariante;
    private String codigo;
    private BigDecimal precio;
    private String imagen;
    private int stock;
    private int cantidad;
    private int idProducto;
    private int idTamanio;

    public Variante() {
    }

    public Variante(int idVariante, String codigo, BigDecimal precio, String imagen, int stock, int cantidad, int idProducto, int idTamanio) {
        this.idVariante = idVariante;
        this.codigo = codigo;
        this.precio = precio;
        this.imagen = imagen;
        this.stock = stock;
        this.cantidad = cantidad;
        this.idProducto = idProducto;
        this.idTamanio = idTamanio;
    }

    public int getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(int idVariante) {
        this.idVariante = idVariante;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdTamanio() {
        return idTamanio;
    }

    public void setIdTamanio(int idTamanio) {
        this.idTamanio = idTamanio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variante variante)) return false;
        return getIdVariante() == variante.getIdVariante() && getStock() == variante.getStock() && getCantidad() == variante.getCantidad() && getIdProducto() == variante.getIdProducto() && getIdTamanio() == variante.getIdTamanio() && Objects.equals(getCodigo(), variante.getCodigo()) && Objects.equals(getPrecio(), variante.getPrecio()) && Objects.equals(getImagen(), variante.getImagen());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdVariante(), getCodigo(), getPrecio(), getImagen(), getStock(), getCantidad(), getIdProducto(), getIdTamanio());
    }

    @Override
    public String toString() {
        return "Variante{" +
                "idVariante=" + idVariante +
                ", codigo='" + codigo + '\'' +
                ", precio=" + precio +
                ", imagen='" + imagen + '\'' +
                ", stock=" + stock +
                ", cantidad=" + cantidad +
                ", idProducto=" + idProducto +
                ", idTamanio=" + idTamanio +
                '}';
    }

    @Override
    public int compareTo(Variante o) {
        return VARIANTE_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }
}
