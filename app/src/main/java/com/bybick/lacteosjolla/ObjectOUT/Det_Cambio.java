package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 08/07/2016.
 */
public class Det_Cambio {
    String id_det_cambio;
    String id_cambio;
    int id_motivo;
    String motivo;
    String id_unidad_in;
    String id_unidad_out;
    String unidad_out;
    String unidad_in;
    String id_producto_entregado;
    String nombre_entregado;
    String id_producto_recibido;
    String nombre_recibido;
    double cantidad_in;
    double cantidad_out;
    int partida;

    public int getIdex_motivo() {
        return idex_motivo;
    }

    public void setIdex_motivo(int idex_motivo) {
        this.idex_motivo = idex_motivo;
    }

    public int getIndex_in() {
        return index_in;
    }

    public void setIndex_in(int index_in) {
        this.index_in = index_in;
    }

    public int getIndex_out() {
        return index_out;
    }

    public void setIndex_out(int index_out) {
        this.index_out = index_out;
    }

    int idex_motivo;
    int index_in;
    int index_out;

    public Det_Cambio() {
    }

    public String getId_unidad_out() {
        return id_unidad_out;
    }

    public void setId_unidad_out(String id_unidad_out) {
        this.id_unidad_out = id_unidad_out;
    }

    public String getUnidad_out() {
        return unidad_out;
    }

    public void setUnidad_out(String unidad_out) {
        this.unidad_out = unidad_out;
    }

    public double getCantidad_out() {
        return cantidad_out;
    }

    public void setCantidad_out(double cantidad_out) {
        this.cantidad_out = cantidad_out;
    }

    public String getId_det_cambio() {
        return id_det_cambio;
    }

    public void setId_det_cambio(String id_det_cambio) {
        this.id_det_cambio = id_det_cambio;
    }

    public String getId_cambio() {
        return id_cambio;
    }

    public void setId_cambio(String id_cambio) {
        this.id_cambio = id_cambio;
    }

    public int getId_motivo() {
        return id_motivo;
    }

    public void setId_motivo(int id_motivo) {
        this.id_motivo = id_motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getId_unidad_in() {
        return id_unidad_in;
    }

    public void setId_unidad_in(String id_unidad_in) {
        this.id_unidad_in = id_unidad_in;
    }

    public String getUnidad_in() {
        return unidad_in;
    }

    public void setUnidad_in(String unidad_in) {
        this.unidad_in = unidad_in;
    }

    public String getId_producto_entregado() {
        return id_producto_entregado;
    }

    public void setId_producto_entregado(String id_producto_entregado) {
        this.id_producto_entregado = id_producto_entregado;
    }

    public String getNombre_entregado() {
        return nombre_entregado;
    }

    public void setNombre_entregado(String nombre_entregado) {
        this.nombre_entregado = nombre_entregado;
    }

    public String getId_producto_recibido() {
        return id_producto_recibido;
    }

    public void setId_producto_recibido(String id_producto_recibido) {
        this.id_producto_recibido = id_producto_recibido;
    }

    public String getNombre_recibido() {
        return nombre_recibido;
    }

    public void setNombre_recibido(String nombre_recibido) {
        this.nombre_recibido = nombre_recibido;
    }

    public double getCantidad_in() {
        return cantidad_in;
    }

    public void setCantidad_in(double cantidad_in) {
        this.cantidad_in = cantidad_in;
    }

    public int getPartida() {
        return partida;
    }

    public void setPartida(int partida) {
        this.partida = partida;
    }
}
