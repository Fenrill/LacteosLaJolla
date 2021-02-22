package com.bybick.lacteosjolla.ObjectOUT;

public class VentaLiquidacion {
    int id_cliente;
    String nombre;
    String forma_venta;
    String metodo_venta;
    int id_forma_venta;
    int id_metodo_venta;
    double total;

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getForma_venta() {
        return forma_venta;
    }

    public void setForma_venta(String forma_venta) {
        this.forma_venta = forma_venta;
    }

    public String getMetodo_venta() {
        return metodo_venta;
    }

    public void setMetodo_venta(String metodo_venta) {
        this.metodo_venta = metodo_venta;
    }

    public int getId_forma_venta() {
        return id_forma_venta;
    }

    public void setId_forma_venta(int id_forma_venta) {
        this.id_forma_venta = id_forma_venta;
    }

    public int getId_metodo_venta() {
        return id_metodo_venta;
    }

    public void setId_metodo_venta(int id_metodo_venta) {
        this.id_metodo_venta = id_metodo_venta;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
