package com.pe.model.administrador.entidad;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class Slider implements Comparable<Slider>{

    public static final Comparator<Slider> SLIDER_COMPARATOR_NATURAL_ORDER = Comparator.comparing(Slider::getIdSlider).thenComparing(Slider::getTitulo)
            .thenComparing(Slider::getEslogan).thenComparing(Slider::getFechaInicio)
            .thenComparing(Slider::getFechaFin).thenComparing(Slider::getEstado)
            .thenComparing(Slider::getRelacionarCon);

    @Override
    public int compareTo(Slider o) {
        return SLIDER_COMPARATOR_NATURAL_ORDER
                .compare(this, o);
    }

    private int idSlider;
    private String titulo;
    private String eslogan;
    private LocalDate fechaInicio;
    private String imagen;
    private LocalDate fechaFin;
    private Slider.EstadoSlider estado;
    private int idVariante;
    private String relacionarCon;

    public Slider() {
    }

    public Slider(int idSlider, String titulo, String eslogan, LocalDate fechaInicio, String imagen, LocalDate fechaFin, EstadoSlider estado, int idVariante, String relacionarCon) {
        this.idSlider = idSlider;
        this.titulo = titulo;
        this.eslogan = eslogan;
        this.fechaInicio = fechaInicio;
        this.imagen = imagen;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idVariante = idVariante;
        this.relacionarCon = relacionarCon;
    }

    public enum EstadoSlider {
        Activo, Inactivo
    }

    public int getIdSlider() {
        return idSlider;
    }

    public void setIdSlider(int idSlider) {
        this.idSlider = idSlider;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEslogan() {
        return eslogan;
    }

    public void setEslogan(String eslogan) {
        this.eslogan = eslogan;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoSlider getEstado() {
        return estado;
    }

    public void setEstado(EstadoSlider estado) {
        this.estado = estado;
    }

    public int getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(int idVariante) {
        this.idVariante = idVariante;
    }

    public String getRelacionarCon() {
        return relacionarCon;
    }

    public void setRelacionarCon(String relacionarCon) {
        this.relacionarCon = relacionarCon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slider slider)) return false;
        return getIdSlider() == slider.getIdSlider() && getIdVariante() == slider.getIdVariante() && Objects.equals(getTitulo(), slider.getTitulo()) && Objects.equals(getEslogan(), slider.getEslogan()) && Objects.equals(getFechaInicio(), slider.getFechaInicio()) && Objects.equals(getFechaFin(), slider.getFechaFin()) && getEstado() == slider.getEstado() && Objects.equals(getRelacionarCon(), slider.getRelacionarCon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdSlider(), getTitulo(), getEslogan(), getFechaInicio(), getFechaFin(), getEstado(), getIdVariante(), getRelacionarCon());
    }

    @Override
    public String toString() {
        return "Slider{" +
                "idSlider=" + idSlider +
                ", titulo='" + titulo + '\'' +
                ", eslogan='" + eslogan + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", imagen='" + imagen + '\'' +
                ", fechaFin=" + fechaFin +
                ", estado=" + estado +
                ", idVariante=" + idVariante +
                ", relacionarCon='" + relacionarCon + '\'' +
                '}';
    }
}
