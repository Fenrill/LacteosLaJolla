package com.bybick.lacteosjolla.ObjectIN;

public class Promociones {

    String id_cliente;
    String nombre;
    String producto_venta;
    String producto_entrega;
    double cantidad_venta;
    double cantidad_entrega;

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProducto_venta() {
        return producto_venta;
    }

    public void setProducto_venta(String producto_venta) {
        this.producto_venta = producto_venta;
    }

    public String getProducto_entrega() {
        return producto_entrega;
    }

    public void setProducto_entrega(String producto_entrega) {
        this.producto_entrega = producto_entrega;
    }

    public double getCantidad_venta() {
        return cantidad_venta;
    }

    public void setCantidad_venta(double cantidad_venta) {
        this.cantidad_venta = cantidad_venta;
    }

    public double getCantidad_entrega() {
        return cantidad_entrega;
    }

    public void setCantidad_entrega(double cantidad_entrega) {
        this.cantidad_entrega = cantidad_entrega;
    }
}
