package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 27/05/2016.
 */
public class Producto_unidad {
    String id_producto;
    String id_unidad;
    double conversion;
    int minima;

    public Producto_unidad() {
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getId_unidad() {
        return id_unidad;
    }

    public void setId_unidad(String id_unidad) {
        this.id_unidad = id_unidad;
    }

    public double getConversion() {
        return conversion;
    }

    public void setConversion(double conversion) {
        this.conversion = conversion;
    }

    public int getMinima() {
        return minima;
    }

    public void setMinima(int minima) {
        this.minima = minima;
    }

}
