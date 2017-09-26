package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 25/06/2016.
 */
public class NoVenta {
    String id_noventa;
    String id_visita;
    String id_cliente;
    int id_motivo;
    String comentario;
    String imagen;
    int enviado;
    String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public NoVenta() {
    }

    public String getId_noventa() {
        return id_noventa;
    }

    public void setId_noventa(String id_noventa) {
        this.id_noventa = id_noventa;
    }

    public String getId_visita() {
        return id_visita;
    }

    public void setId_visita(String id_visita) {
        this.id_visita = id_visita;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_motivo() {
        return id_motivo;
    }

    public void setId_motivo(int id_motivo) {
        this.id_motivo = id_motivo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}
