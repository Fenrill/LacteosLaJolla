package com.bybick.lacteosjolla.ObjectView;

import org.json.JSONArray;

/**
 * Created by bicktor on 20/07/2016.
 */
public class Result {
    String nombre;
    int ok;
    int fail;
    int all;
    int code;
    String msg;

    JSONArray correctos;


    public JSONArray getCorrectos() {
        return correctos;
    }

    public void setCorrectos(JSONArray correctos) {
        this.correctos = correctos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
