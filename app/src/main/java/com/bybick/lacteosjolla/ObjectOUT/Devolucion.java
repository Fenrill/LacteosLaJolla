package com.bybick.lacteosjolla.ObjectOUT;

import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class Devolucion {
    String id_cliente;
    String id_devolucion;
    String id_visita;
    String empresa;
    ArrayList<Det_Devolucion> detalles;
    int enviado;


    public ArrayList<Det_Devolucion> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<Det_Devolucion> detalles) {
        this.detalles = detalles;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public Devolucion() {
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_devolucion() {
        return id_devolucion;
    }

    public void setId_devolucion(String id_devolucion) {
        this.id_devolucion = id_devolucion;
    }

    public String getId_visita() {
        return id_visita;
    }

    public void setId_visita(String id_visita) {
        this.id_visita = id_visita;
    }

}
