package com.bybick.lacteosjolla.ObjectOUT;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bicktor on 16/06/2016.
 */
public class Venta {
    String id_venta;
    String id_visita;
    String id_cliente;
    double subtotal;
    double impuestos;
    double total;
    String empresa;
    int enviado;
    String forma_venta;
    int id_forma_venta;
    String serie;
    int folio;
    String fecha;
    ArrayList<Det_Venta> det_venta;
    String metodo_pago;
    int id_metodo_pago;
    String GpsVenta;

    public String getId_venta() {
        return id_venta;
    }

    public void setId_venta(String id_venta) {
        this.id_venta = id_venta;
    }

    public String getId_visita() {
        return id_visita;
    }

    public void setId_visita(String id_visita) {
        this.id_visita = id_visita;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public String getForma_venta() {
        return forma_venta;
    }

    public void setForma_venta(String forma_venta) {
        this.forma_venta = forma_venta;
    }

    public int getId_forma_venta() {
        return id_forma_venta;
    }

    public void setId_forma_venta(int id_forma_venta) {
        this.id_forma_venta = id_forma_venta;
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

    public ArrayList<Det_Venta> getDet_venta() {
        return det_venta;
    }

    public void setDet_venta(ArrayList<Det_Venta> det_venta) {
        this.det_venta = det_venta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public int getId_metodo_pago() {
        return id_metodo_pago;
    }

    public void setId_metodo_pago(int id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }

    public String getGpsVenta() {
        return GpsVenta;
    }

    public void setGpsVenta(String gpsVenta) {
        GpsVenta = gpsVenta;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        return object;
    }
}
