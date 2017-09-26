package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 13/07/2016.
 */
public class Det_Devolucion {
    String id_det_devolucion;
    String nombre;
    String motivo;
    String id_devolucion;
    int id_motivo;
    String id_producto;
    double cantidad;
    String id_unidad;
    String unidad;
    int partida;
    int indexMotivo;
    int indexUnidad;
    double importe;

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public int getIndexMotivo() {
        return indexMotivo;
    }

    public void setIndexMotivo(int indexMotivo) {
        this.indexMotivo = indexMotivo;
    }

    public int getIndexUnidad() {
        return indexUnidad;
    }

    public void setIndexUnidad(int indexUnidad) {
        this.indexUnidad = indexUnidad;
    }

    public Det_Devolucion() {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getId_det_devolucion() {
        return id_det_devolucion;
    }

    public void setId_det_devolucion(String id_det_devolucion) {
        this.id_det_devolucion = id_det_devolucion;
    }

    public String getId_devolucion() {
        return id_devolucion;
    }

    public void setId_devolucion(String id_devolucion) {
        this.id_devolucion = id_devolucion;
    }

    public int getId_motivo() {
        return id_motivo;
    }

    public void setId_motivo(int id_motivo) {
        this.id_motivo = id_motivo;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public int getPartida() {
        return partida;
    }

    public void setPartida(int partida) {
        this.partida = partida;
    }

}
