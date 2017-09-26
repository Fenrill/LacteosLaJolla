package com.bybick.lacteosjolla.ObjectOUT;

import java.util.ArrayList;

/**
 * Created by bicktor on 08/07/2016.
 */
public class Cambio {
    String id_cambio;
    String id_cliente;
    String id_visita;
    String empresa;
    int enviado;
    ArrayList<Det_Cambio> detalles;

    public ArrayList<Det_Cambio> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<Det_Cambio> detalles) {
        this.detalles = detalles;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public Cambio() {
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getId_cambio() {
        return id_cambio;
    }

    public void setId_cambio(String id_cambio) {
        this.id_cambio = id_cambio;
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

}
