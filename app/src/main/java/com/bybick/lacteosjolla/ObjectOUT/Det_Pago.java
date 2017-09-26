package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 13/07/2016.
 */
public class Det_Pago {
    String id_det_pago;
    String id_pago;
    String serie;
    int folio;
    String comentarios;
    double importe_pago;
    double saldo;
    double saldo_ant;
    double total_factura;
    String forma_pago;


    public double getTotal_factura() {
        return total_factura;
    }

    public void setTotal_factura(double total_factura) {
        this.total_factura = total_factura;
    }

    public double getSaldo_ant() {
        return saldo_ant;
    }

    public void setSaldo_ant(double saldo_ant) {
        this.saldo_ant = saldo_ant;
    }

    public Det_Pago() {
    }

    public Det_Pago(String id_det_pago, String id_pago, String serie, int folio, String comentarios, double importe_pago, String forma_pago) {
        this.id_det_pago = id_det_pago;
        this.id_pago = id_pago;
        this.serie = serie;
        this.folio = folio;
        this.comentarios = comentarios;
        this.importe_pago = importe_pago;
        this.forma_pago = forma_pago;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getId_det_pago() {
        return id_det_pago;
    }

    public void setId_det_pago(String id_det_pago) {
        this.id_det_pago = id_det_pago;
    }

    public String getId_pago() {
        return id_pago;
    }

    public void setId_pago(String id_pago) {
        this.id_pago = id_pago;
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

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public double getImporte_pago() {
        return importe_pago;
    }

    public void setImporte_pago(double importe_pago) {
        this.importe_pago = importe_pago;
    }

    public String getForma_pago() {
        return forma_pago;
    }

    public void setForma_pago(String forma_pago) {
        this.forma_pago = forma_pago;
    }

}
