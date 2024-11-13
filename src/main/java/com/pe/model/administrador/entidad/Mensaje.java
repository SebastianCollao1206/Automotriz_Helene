package com.pe.model.administrador.entidad;

public class Mensaje {
    private String tipoMensaje;
    private String mensaje;
    private String redirectUrl;

    public Mensaje(String tipoMensaje, String mensaje, String redirectUrl) {
        this.tipoMensaje = tipoMensaje;
        this.mensaje = mensaje;
        this.redirectUrl = redirectUrl;
    }

    public Mensaje() {
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
