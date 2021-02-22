package com.bybick.lacteosjolla.ObjectIN;

import com.bybick.lacteosjolla.DataBases.DBData;

/**
 * Created by Ricardo on 21/07/2017.
 */

public class Inventario {
    private String producto;
    private String unidad_minima;

    private int piezas;
    private double cantidad;
    private double ventas_inventario;
    private double cambios_inventario;
    private int ventas_piezas;

    private String unidad;

    private String consultBtn;

    public Inventario(){

    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getConsultBtn() {
        return consultBtn;
    }

    public void setConsultBtn(String consultBtn) {
        this.consultBtn = consultBtn;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getUnidad_minima() {
        return unidad_minima;
    }

    public void setUnidad_minima(String unidad_minima) {
        this.unidad_minima = unidad_minima;
    }

    public double getVentas_inventario() {
        return ventas_inventario;
    }

    public void setVentas_inventario(double ventas_inventario) {
        this.ventas_inventario = ventas_inventario;
    }

    public double getCambios_inventario() {
        return cambios_inventario;
    }

    public void setCambios_inventario(double cambios_inventario) {
        this.cambios_inventario = cambios_inventario;
    }

    public int getPiezas() {
        return piezas;
    }

    public void setPiezas(int piezas) {
        this.piezas = piezas;
    }

    public int getVentas_piezas() {
        return ventas_piezas;
    }

    public void setVentas_piezas(int ventas_piezas) {
        this.ventas_piezas = ventas_piezas;
    }
}
