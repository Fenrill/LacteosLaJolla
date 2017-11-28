package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 09/06/2016.
 */
public class Visita {
    String id_visita;
    String id_jornada;
    String id_usuario;
    String id_cliente;
    String fecha;
    String hora_inicio;
    String gps_inicio;
    int bateria_inicio;
    String hora_fin;
    String gps_fin;
    int bateria_fin;
    String ult_modificacion;
    boolean modificado = false;
    boolean terminado = false;

    public Visita() {
    }

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    public String getUlt_modificacion() {
        return ult_modificacion;
    }

    public void setUlt_modificacion(String ult_modificacion) {
        this.ult_modificacion = ult_modificacion;
    }

    public String getId_visita() {
        return id_visita;
    }

    public void setId_visita(String id_visita) {
        this.id_visita = id_visita;
    }

    public String getId_jornada() {
        return id_jornada;
    }

    public void setId_jornada(String id_jornada) {
        this.id_jornada = id_jornada;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getGps_inicio() {
        return gps_inicio;
    }

    public void setGps_inicio(String gps_inicio) {
        this.gps_inicio = gps_inicio;
    }

    public int getBateria_inicio() {
        return bateria_inicio;
    }

    public void setBateria_inicio(int bateria_inicio) {
        this.bateria_inicio = bateria_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getGps_fin() {
        return gps_fin;
    }

    public void setGps_fin(String gps_fin) {
        this.gps_fin = gps_fin;
    }

    public int getBateria_fin() {
        return bateria_fin;
    }

    public void setBateria_fin(int bateria_fin) {
        this.bateria_fin = bateria_fin;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

}
