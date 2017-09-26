package com.bybick.lacteosjolla.ObjectView;

/**
 * Created by bicktor on 18/07/2016.
 */
public class Efectividad {
    String id_cliente;
    String nombre;
    String rfc;
    String razon_social;

    boolean ventas;
    boolean cambios;
    boolean devoluciones;


    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public boolean isVentas() {
        return ventas;
    }

    public void setVentas(boolean ventas) {
        this.ventas = ventas;
    }

    public boolean isCambios() {
        return cambios;
    }

    public void setCambios(boolean cambios) {
        this.cambios = cambios;
    }

    public boolean isDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(boolean devoluciones) {
        this.devoluciones = devoluciones;
    }
}
