package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class P_Cambio {
    Context context;
    DBData dbd;
    DBConfig dbc;

    Cambio cambio;
    Cliente cliente;

    ConnectionBT bt;
    OutputStream ons;

    public P_Cambio(Context context, Cambio cambio, Cliente cliente) {
        this.context = context;
        this.cambio = cambio;
        this.cliente = cliente;
    }

    public String imprimir() {
        dbc = new DBConfig(context);
        //main = new Main();
        String txt = "";
        String title = "== CAMBIO DE " + cambio.getEmpresa() + " ==";
        title = center(title);
        boolean x = true;
        int total = 0;
        try{
//            do {
            OutputStreamWriter ticket = new OutputStreamWriter(context.openFileOutput("ticket.txt", Context.MODE_PRIVATE));
            //Escribir en el ticket * CABECERA GENERAL *
            ticket.write(center(title) + "\r\n\r\n");
            ticket.write("Usuario: " + dbc.getlogin().getId_usuario() + "\r\n");
            ticket.write("No Cliente: " + cliente.getId_cliente() + "\r\n");
            ticket.write("Cliente: " + cliente.getNombre() + "\r\n");
            ticket.write("Fecha: " + Main.getFecha() + "\r\n");
            ticket.write("Hora: " + Main.getHora() + "\r\n\r\n");

            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");


            //Obtener detalles
            ArrayList<Det_Cambio> det_cambio = cambio.getDetalles();

            // * CABECERA DE LOS PRODUCTOS *
            ticket.write("--------------------------------\r\n");
            ticket.write(center("Descripcion") + "\r\n");
            ticket.write("Cant.              Unidad       \r\n");
            ticket.write(center("Motivo") + "\r\n");
            ticket.write("--------------------------------\r\n");

            for (int i = 0; i < det_cambio.size(); i++) {
                Det_Cambio item = det_cambio.get(i);

                //Producto ENtrante
                ticket.write(center("== Producto Entrante ==\n"));
                ticket.write(center(item.getNombre_recibido()) + "\r\n");
                ticket.write("" + item.getCantidad_in());
                ticket.write("         " + item.getUnidad_in() + "\r\n");
                ticket.write("                                \r\n");

                //Producto Saliente
                ticket.write(center("== Producto Saliente ==\n"));
                ticket.write(center(item.getNombre_entregado()) + "\r\n");
                ticket.write("" + item.getCantidad_out());
                ticket.write("         " + item.getUnidad_out() + "\r\n");
                ticket.write("                                \r\n");

                //Motivo
                ticket.write(center("----- " + item.getMotivo() + " -----\r\n"));
                ticket.write("                                \r\n");
                total += item.getCantidad_in();

            }
            ticket.write("Total de Productos: " + total + "\r\n");
            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
            ticket.write("                                \r\n");
            ticket.close();

            //Escribir Ticket
            File file = new File("/data/data/com.bybick.lacteosjolla/files/ticket.txt");
            byte[] bites = new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bites);
            txt = new String(bites);

            //Crear Conexion
            bt = new ConnectionBT(context);
            ons = bt.connect();

            if(ons != null)
                ons.write(bites);

            //Borrar
            file.delete();

        } catch (FileNotFoundException es) {
            Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Create Failed ", es.toString());
        } catch (IOException es) {
            Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Write Failed ",es.toString());
        } catch (Exception es){
            Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Otra Excepcion ", es.toString());
        }

        return txt;
    }

    //Cerrar BT
    public void Close() {
        bt.close();
    }

    //Retornar BT
    public ConnectionBT getBT() {
        return bt;
    }

    //tirnar OnsWri
    public OutputStream getONS() {
        return ons;
    }



    public String align(String line){
        int spaces=14;
        for(int i=0;i<spaces;i++){
            line=" "+line;
        }
        return line;
    }

    public String center(String line){
        if(line.length()<32){
            int x=(32-line.length())/2;
            for(int i=0;i<x;i++){
                line=" "+line;
                line=line+" ";
            }
        }
        return line;

    }


}
