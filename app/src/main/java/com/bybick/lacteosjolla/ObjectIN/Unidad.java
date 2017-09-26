package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 26/05/2016.
 */
public class Unidad {
    String id_unidad;
    String unidad;

    public Unidad() {
    }

    public Unidad(String id_unidad, String unidad) {
        this.id_unidad = id_unidad;
        this.unidad = unidad;
    }

    public String getId_unidad() {
        return id_unidad;
    }

    public void setId_unidad(String id_unidad) {
        this.id_unidad = id_unidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    @Override
    public String toString() {
        return unidad;
    }

}
