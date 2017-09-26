package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 22/07/2016.
 */
public class VentaResumen {
    String empresa;
    String forma_venta;
    int id_forma_venta;
    double total;

    public VentaResumen() {
    }

    public VentaResumen(String empresa, String forma_venta, double total) {
        this.empresa = empresa;
        this.forma_venta = forma_venta;
        this.total = total;
    }

    public int getId_forma_venta() {
        return id_forma_venta;
    }

    public void setId_forma_venta(int id_forma_venta) {
        this.id_forma_venta = id_forma_venta;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getForma_venta() {
        return forma_venta;
    }

    public void setForma_venta(String forma_venta) {
        this.forma_venta = forma_venta;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
