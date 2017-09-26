package com.bybick.lacteosjolla.Printers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by bicktor on 04/06/2016.
 */
public class ConnectionBT {
    Context context;
    DBConfig dbc;
    ArrayList<BluetoothDevice> dispositivos;
    BluetoothAdapter adaptador;
    BluetoothDevice dispositivo;
    BluetoothSocket socket;
    int estado;
    InputStream ins;
    OutputStream ons;
    boolean registrado = false;
    String error;

    public ConnectionBT(Context context) {
        this.context = context;

        dbc = new DBConfig(context);
        dbc.open();
    }

    public OutputStream connect() {
        adaptador = BluetoothAdapter.getDefaultAdapter();
        getDevices();
        JSONObject printer = dbc.getPrinter();
        //Obtneer Dispositivo
        for(BluetoothDevice device : dispositivos) {
            try {
                if(device.getAddress().equals(printer.getString("mac"))) {
                    dispositivo = device;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR JSON", e.getMessage());
            }
        }
        //Conectar
        try {
            socket = dispositivo.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
            ins = socket.getInputStream();
            ons = socket.getOutputStream();
            estado=3;
            Log.e("INFO_CONNECT", "Conectado exitosamente!");
            Toast toast = Toast.makeText(context, "Conectado a: " + dispositivo.getName() , Toast.LENGTH_SHORT);
            toast.show();

            return ons;
        } catch (Exception e) {
            // TODO: handle exception
            estado = 4;
            error = e.toString();
            Log.e("Error al conectar", error);
            Toast toast = Toast.makeText(context, "Error al conectar con: " + dispositivo.getName(), Toast.LENGTH_SHORT);
            toast.show();

            return null;
        }
    }

    public void getDevices() {
        dispositivos = new ArrayList<>();

        for(BluetoothDevice disp : adaptador.getBondedDevices()) {
            dispositivos.add(disp);
        }
    }

    public void close() {
        try {
            if(socket != null)
                socket.close();
            if(ins != null)
                ins.close();
            if(ons != null)
                ons.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
