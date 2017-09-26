package com.bybick.lacteosjolla.ObjectView;

/**
 * Created by bicktor on 18/07/2016.
 */
public class Sobrantes {
    String id_producto;
    String descripcion;
    double carga;
    double ventas;
    double cambios;
    double devoluciones;
    double inv_final;
    double inv_mal;
    double inv_buen;
    String marca;

    public Sobrantes() {
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCarga() {
        return carga;
    }

    public void setCarga(double carga) {
        this.carga = carga;
    }

    public double getVentas() {
        return ventas;
    }

    public void setVentas(double ventas) {
        this.ventas = ventas;
    }

    public double getCambios() {
        return cambios;
    }

    public void setCambios(double cambios) {
        this.cambios = cambios;
    }

    public double getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(double devoluciones) {
        this.devoluciones = devoluciones;
    }

    public double getInv_final() {
        return inv_final;
    }

    public void setInv_final(double inv_final) {
        this.inv_final = inv_final;
    }

    public double getInv_mal() {
        return inv_mal;
    }

    public void setInv_mal(double inv_mal) {
        this.inv_mal = inv_mal;
    }

    public double getInv_buen() {
        return inv_buen;
    }

    public void setInv_buen(double inv_buen) {
        this.inv_buen = inv_buen;
    }

}
