package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 26/05/2016.
 */
public class Listas_Precios {
    String tipo_lista;
    String lista;
    String id_cliente;
    String id_producto;
    double sub_precio;
    double impuestos;
    double precio;
    String id_unidad;
    String unidad;

    public Listas_Precios(){}

    public Listas_Precios(String tipo_lista, String lista, String id_cliente, String id_producto, double sub_precio, double impuestos, double precio, String id_unidad) {
        this.tipo_lista = tipo_lista;
        this.lista = lista;
        this.id_cliente = id_cliente;
        this.id_producto = id_producto;
        this.sub_precio = sub_precio;
        this.impuestos = impuestos;
        this.precio = precio;
        this.id_unidad = id_unidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getTipo_lista() {
        return tipo_lista;
    }

    public void setTipo_lista(String tipo_lista) {
        this.tipo_lista = tipo_lista;
    }

    public String getLista() {
        return lista;
    }

    public void setLista(String lista) {
        this.lista = lista;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public double getSub_precio() {
        return sub_precio;
    }

    public void setSub_precio(double sub_precio) {
        this.sub_precio = sub_precio;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getId_unidad() {
        return id_unidad;
    }

    public void setId_unidad(String id_unidad) {
        this.id_unidad = id_unidad;
    }

    @Override
    public String toString() {
        return this.unidad;
    }

}
