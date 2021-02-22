package com.bybick.lacteosjolla.ObjectOUT;

import java.util.ArrayList;

/**
 * Created by Clowny on 06/06/2018.
 */

public class Pedidos {
    String id_pedido;
    String id_visita;
    String id_cliente;
    double subtotal;
    double impuestos;
    double total;
    String empresa;
    int enviado;
    String serie;
    int folio;
    String fecha;
    ArrayList<Det_Pedido> det_pedido;

    public String getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public ArrayList<Det_Pedido> getDet_pedido() {
        return det_pedido;
    }

    public void setDet_pedido(ArrayList<Det_Pedido> det_pedido) {
        this.det_pedido = det_pedido;
    }
}
