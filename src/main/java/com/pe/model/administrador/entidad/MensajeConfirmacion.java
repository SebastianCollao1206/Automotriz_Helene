package com.pe.model.administrador.entidad;

public class MensajeConfirmacion {
    private String titulo;
    private String texto;
    private String icono;
    private String textoBotonConfirmar;
    private String textoBotonCancelar;

    public MensajeConfirmacion(String titulo, String texto, String icono, String textoBotonConfirmar, String textoBotonCancelar) {
        this.titulo = titulo;
        this.texto = texto;
        this.icono = icono;
        this.textoBotonConfirmar = textoBotonConfirmar;
        this.textoBotonCancelar = textoBotonCancelar;
    }

    public MensajeConfirmacion() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getTextoBotonConfirmar() {
        return textoBotonConfirmar;
    }

    public void setTextoBotonConfirmar(String textoBotonConfirmar) {
        this.textoBotonConfirmar = textoBotonConfirmar;
    }

    public String getTextoBotonCancelar() {
        return textoBotonCancelar;
    }

    public void setTextoBotonCancelar(String textoBotonCancelar) {
        this.textoBotonCancelar = textoBotonCancelar;
    }
}
