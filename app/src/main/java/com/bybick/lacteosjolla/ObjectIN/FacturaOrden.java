package com.bybick.lacteosjolla.ObjectIN;

public class FacturaOrden {
    String id_pago;
    String id_det_pago;
    String id_cliente;
    double importe_pago;
    double saldo;
    double total;
    String serie;
    int folio;
    int orden;

    public String getId_pago() {
        return id_pago;
    }

    public void setId_pago(String id_pago) {
        this.id_pago = id_pago;
    }

    public String getId_det_pago() {
        return id_det_pago;
    }

    public void setId_det_pago(String id_det_pago) {
        this.id_det_pago = id_det_pago;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public double getImporte_pago() {
        return importe_pago;
    }

    public void setImporte_pago(double importe_pago) {
        this.importe_pago = importe_pago;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getFolio() {
        return folio;
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
