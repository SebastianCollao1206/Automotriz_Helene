package com.pe.model.administrador.entidad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

public class Compra implements Comparable<Compra>{

    public static final Comparator<Compra> COMPRA_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Compra::getIdCompra).thenComparing(Compra::getFecha)
            .thenComparing(Compra::getTotal).thenComparing(Compra::getTotal)
            .thenComparing(Compra::getFechaRecojo);

    @Override
    public int compareTo(Compra o) {
        return COMPRA_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    private int idCompra;
    private LocalDate fecha;
    private double total;
    private LocalDate fechaRecojo;
    private Integer idCliente;
    private Integer idUsuario;
    private Integer idMetodoPago;

    public Compra(int idCompra, LocalDate fecha, double total, LocalDate fechaRecojo, Integer idCliente, Integer idUsuario, Integer idMetodoPago) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.total = total;
        this.fechaRecojo = fechaRecojo;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.idMetodoPago = idMetodoPago;
    }

    public Compra() {
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDate getFechaRecojo() {
        return fechaRecojo;
    }

    public void setFechaRecojo(LocalDate fechaRecojo) {
        this.fechaRecojo = fechaRecojo;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compra compra)) return false;
        return getIdCompra() == compra.getIdCompra() && Double.compare(getTotal(), compra.getTotal()) == 0 && Objects.equals(getFecha(), compra.getFecha()) && Objects.equals(getFechaRecojo(), compra.getFechaRecojo()) && Objects.equals(getIdCliente(), compra.getIdCliente()) && Objects.equals(getIdUsuario(), compra.getIdUsuario()) && Objects.equals(getIdMetodoPago(), compra.getIdMetodoPago());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCompra(), getFecha(), getTotal(), getFechaRecojo(), getIdCliente(), getIdUsuario(), getIdMetodoPago());
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", fecha=" + fecha +
                ", total=" + total +
                ", fechaRecojo=" + fechaRecojo +
                ", idCliente=" + idCliente +
                ", idUsuario=" + idUsuario +
                ", idMetodoPago=" + idMetodoPago +
                '}';
    }
}
