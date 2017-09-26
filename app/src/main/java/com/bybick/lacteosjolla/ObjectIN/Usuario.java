package com.bybick.lacteosjolla.ObjectIN;

/**
 * Created by bicktor on 25/05/2016.
 */
public class Usuario {
    String id_usuario;
    String contraseña;
    int login;

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }
}
