package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 02/06/2016.
 */
public class Motivo {
    int id_motivo;
    String descripcion;

    public Motivo(int id_motivo, String descripcion) {
        this.id_motivo = id_motivo;
        this.descripcion = descripcion;
    }

    public Motivo() {
    }

    public int getId_motivo() {
        return id_motivo;
    }

    public void setId_motivo(int id_motivo) {
        this.id_motivo = id_motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return this.descripcion;
    }

}
