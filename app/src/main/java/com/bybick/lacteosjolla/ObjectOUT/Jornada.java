package com.bybick.lacteosjolla.ObjectOUT;

/**
 * Created by bicktor on 03/06/2016.
 */
public class Jornada {
    String id_jornada;
    String id_usuario;
    String fecha;
    String hora_inicio;
    String gps_inicio;
    int bateria_inicio;
    String hora_fin;
    String gps_fin;
    int bateria_fin;

    public Jornada() {
    }

    public Jornada(String id_jornada, String id_usuario, String fecha, String hora_inicio, String gps_inicio, int bateria_inicio, String hora_fin, String gps_fin, int bateria_fin) {
        this.id_jornada = id_jornada;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.hora_inicio = hora_inicio;
        this.gps_inicio = gps_inicio;
        this.bateria_inicio = bateria_inicio;
        this.hora_fin = hora_fin;
        this.gps_fin = gps_fin;
        this.bateria_fin = bateria_fin;

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

}
