package com.pe.model.administrador.entidad;

import java.util.Comparator;
import java.util.Objects;

public class DetalleCompra implements Comparable<DetalleCompra>{

    public static final Comparator<DetalleCompra> DETALLE_COMPRA_COMPARATOR_NATURAL_ORDER = Comparator.comparing(DetalleCompra::getIdCompra).thenComparing(DetalleCompra::getCantidad)
            .thenComparing(DetalleCompra::getPrecioUnitario).thenComparing(DetalleCompra::getIdCompra);

    @Override
    public int compareTo(DetalleCompra o) {
        return DETALLE_COMPRA_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    private int idDetalleCompra;
    private int cantidad;
    private double precioUnitario;
    private Integer idCompra;
    private Integer idVariante;

    public DetalleCompra(int idDetalleCompra, int cantidad, double precioUnitario, Integer idCompra, Integer idVariante) {
        this.idDetalleCompra = idDetalleCompra;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idCompra = idCompra;
        this.idVariante = idVariante;
    }

    public int getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(int idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(Integer idVariante) {
        this.idVariante = idVariante;
    }

    public DetalleCompra() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleCompra that)) return false;
        return getIdDetalleCompra() == that.getIdDetalleCompra() && getCantidad() == that.getCantidad() && Double.compare(getPrecioUnitario(), that.getPrecioUnitario()) == 0 && Objects.equals(getIdCompra(), that.getIdCompra()) && Objects.equals(getIdVariante(), that.getIdVariante());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdDetalleCompra(), getCantidad(), getPrecioUnitario(), getIdCompra(), getIdVariante());
    }

    @Override
    public String toString() {
        return "DetalleCompra{" +
                "idDetalleCompra=" + idDetalleCompra +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", idCompra=" + idCompra +
                ", idVariante=" + idVariante +
                '}';
    }
}
