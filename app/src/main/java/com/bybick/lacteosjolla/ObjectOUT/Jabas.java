package com.bybick.lacteosjolla.ObjectOUT;

import java.util.ArrayList;

public class Jabas {
    String id_jabas;
    String id_visita;
    String id_cliente;
    String location;
    int enviado;
//    double subtotal;
//    double impuestos;
    ArrayList<Det_Jabas> det_jabas;
//    double total;

    public String getId_jabas() {
        return id_jabas;
    }

    public void setId_jabas(String id_jabas) {
        this.id_jabas = id_jabas;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

//    public double getSubtotal() {
//        return subtotal;
//    }
//
//    public void setSubtotal(double subtotal) {
//        this.subtotal = subtotal;
//    }
//
//    public double getImpuestos() {
//        return impuestos;
//    }
//
//    public void setImpuestos(double impuestos) {
//        this.impuestos = impuestos;
//    }

    public ArrayList<Det_Jabas> getDet_jabas() {
        return det_jabas;
    }

    public void setDet_jabas(ArrayList<Det_Jabas> det_jabas) {
        this.det_jabas = det_jabas;
    }

//    public double getTotal() {
//        return total;
//    }
//
//    public void setTotal(double total) {
//        this.total = total;
//    }
}
