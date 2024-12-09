package com.pe.model.administrador.entidad.notificaciones;

public class DetalleNotificacion {
    private int idDetalleNotificacion;
    private int idNotificacion;
    private int idUsuario;
    private boolean leida;

    public DetalleNotificacion() {
    }

    public DetalleNotificacion(int idDetalleNotificacion, int idNotificacion, int idUsuario, boolean leida) {
        this.idDetalleNotificacion = idDetalleNotificacion;
        this.idNotificacion = idNotificacion;
        this.idUsuario = idUsuario;
        this.leida = leida;
    }

    public int getIdDetalleNotificacion() {
        return idDetalleNotificacion;
    }

    public void setIdDetalleNotificacion(int idDetalleNotificacion) {
        this.idDetalleNotificacion = idDetalleNotificacion;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    @Override
    public String toString() {
        return "DetalleNotificacion{" +
                "idDetalleNotificacion=" + idDetalleNotificacion +
                ", idNotificacion=" + idNotificacion +
                ", idUsuario=" + idUsuario +
                ", leida=" + leida +
                '}';
    }
}
