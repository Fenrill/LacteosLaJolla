package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 20/06/2016.
 */
public class Forma_Venta {
    int id_forma;
    String forma;
    String id_cliente;
    Boolean bloqueado;


    public Forma_Venta() {
    }

    public int getId_forma() {
        return id_forma;
    }

    public void setId_forma(int id_forma) {
        this.id_forma = id_forma;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    @Override
    public String toString() {
        return forma;
    }

}
