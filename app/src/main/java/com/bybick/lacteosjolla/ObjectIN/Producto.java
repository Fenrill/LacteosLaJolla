package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 26/05/2016.
 */
public class Producto {
    String id_producto;
    String descripcion;
    String marca;
    String familia;
    int decimales;
    int orden;
    int inventario;

    //parte de inventario
    public int getInventario() {
        return inventario;
    }

    public void setInventario(int inventario) {
        this.inventario = inventario;
    }
    //---------

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public Producto() {
    }

    public Producto(String id_producto, String descripcion, String marca, String familia, int decimales) {
        this.id_producto = id_producto;
        this.descripcion = descripcion;
        this.marca = marca;
        this.familia = familia;
        this.decimales = decimales;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public int getDecimales() {
        return decimales;
    }

    public void setDecimales(int decimales) {
        this.decimales = decimales;
    }

}
