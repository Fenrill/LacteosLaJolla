package com.bybick.lacteosjolla.ObjectIN;

public class Cliente_Promos {
    String promoCliente;
    double cantidadVenta;
    double cantidadEntrega;
    String promoProductoVenta;
    String promoProductoEntrega;

//    public Cliente_Promos(String promoCliente, double cantidadVenta, double cantidadEntrega, String promoProductoVenta, String promoProductoEntrega) {
//        this.promoCliente = promoCliente;
//        this.cantidadVenta = cantidadVenta;
//        this.cantidadEntrega = cantidadEntrega;
//        this.promoProductoVenta = promoProductoVenta;
//        this.promoProductoEntrega = promoProductoEntrega;
//    }

    public String getPromoCliente() {
        return promoCliente;
    }

    public void setPromoCliente(String promoCliente) {
        this.promoCliente = promoCliente;
    }

    public double getCantidadVenta() {
        return cantidadVenta;
    }

    public void setCantidadVenta(double cantidadVenta) {
        this.cantidadVenta = cantidadVenta;
    }

    public double getCantidadEntrega() {
        return cantidadEntrega;
    }

    public void setCantidadEntrega(double cantidadEntrega) {
        this.cantidadEntrega = cantidadEntrega;
    }

    public String getPromoProductoVenta() {
        return promoProductoVenta;
    }

    public void setPromoProductoVenta(String promoProductoVenta) {
        this.promoProductoVenta = promoProductoVenta;
    }

    public String getPromoProductoEntrega() {
        return promoProductoEntrega;
    }

    public void setPromoProductoEntrega(String promoProductoEntrega) {
        this.promoProductoEntrega = promoProductoEntrega;
    }
}
