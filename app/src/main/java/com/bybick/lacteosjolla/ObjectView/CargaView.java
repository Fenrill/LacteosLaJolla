package com.bybick.lacteosjolla.ObjectView;

/**
 * Created by bicktor on 04/06/2016.
 */
public class CargaView {
    String descripcion;
    String clave;
    String marca;
    double cantidad;
    String unidad;
    boolean reporte;

    public CargaView() {
    }

    public CargaView(String descripcion, String clave, String marca, double cantidad, String unidad) {
        this.descripcion = descripcion;
        this.clave = clave;
        this.marca = marca;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }


    public boolean isReporte() {
        return reporte;
    }

    public void setReporte(boolean reporte) {
        this.reporte = reporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

}
