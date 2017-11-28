package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 26/05/2016.
 */
public class Cliente {
    String id_cliente;
    String nombre;
    String rfc;
    String razon_social;
    int id_forma;
    String forma_venta;
    boolean visitado;
    boolean bloqueado;

    public Cliente(){}

    public Cliente(String id_cliente, String nombre, String rfc, String razon_social,boolean visiatdo) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.rfc = rfc;
        this.razon_social = razon_social;
        this.visitado=visiatdo;
    }

    public int getId_forma() {
        return id_forma;
    }

    public void setId_forma(int id_forma) {
        this.id_forma = id_forma;
    }

    public String getForma_venta() {
        return forma_venta;
    }

    public void setForma_venta(String forma_venta) {
        this.forma_venta = forma_venta;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

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

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}
