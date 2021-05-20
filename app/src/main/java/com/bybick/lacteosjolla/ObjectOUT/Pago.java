package com.bybick.lacteosjolla.ObjectOUT;

import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class Pago {
    String id_pago;
    String id_cliente;
    String id_visita;
    String fecha;
    int enviado;
    ArrayList<Det_Pago> detalles;
    String forma_pago;
    String id_banco;
    String referencia;
    double monto;


    public ArrayList<Det_Pago> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<Det_Pago> detalles) {
        this.detalles = detalles;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public Pago() {
    }

    public String getId_pago() {
        return id_pago;
    }

    public void setId_pago(String id_pago) {
        this.id_pago = id_pago;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_visita() {
        return id_visita;
    }

    public void setId_visita(String id_visita) {
        this.id_visita = id_visita;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getForma_pago() {
        return forma_pago;
    }

    public void setForma_pago(String forma_pago) {
        this.forma_pago = forma_pago;
    }

    public String getId_banco() {
        return id_banco;
    }

    public void setId_banco(String id_banco) {
        this.id_banco = id_banco;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
