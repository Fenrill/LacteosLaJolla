package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 26/05/2016.
 */
public class Carga {
    String id_usuario;
    String id_producto;
    String descripcion;//No DB
    String fecha;
    String marca;
    double cantidad;
    String id_unidad;

    public Carga() {
    }

    public Carga(String id_usuario, String id_producto, String fecha, String marca, double cantidad, String id_unidad) {
        this.id_usuario = id_usuario;
        this.id_producto = id_producto;
        this.fecha = fecha;
        this.marca = marca;
        this.cantidad = cantidad;
        this.id_unidad = id_unidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public String getId_unidad() {
        return id_unidad;
    }

    public void setId_unidad(String id_unidad) {
        this.id_unidad = id_unidad;
    }

}
