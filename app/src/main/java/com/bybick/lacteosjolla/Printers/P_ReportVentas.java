package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.ObjectView.Efectividad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class P_ReportVentas {
    Context context;
    DBData dbd;
    DBConfig dbc;

    ArrayList<CargaView> data;

    ConnectionBT bt;
    OutputStream ons;

    public P_ReportVentas(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<CargaView> data) {
        this.data = data;
    }


    public String imprimir(){

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();
        ;
        String txt="";

        String title="** VENTA POR PRODUCTO **";

        try {
            OutputStreamWriter ticket = new OutputStreamWriter(context.openFileOutput("ticket.txt", Context.MODE_PRIVATE));

            //Escribir en el ticket  CABECERA GENERAL
            ticket.write(center(title) + "\r\n");
            ticket.write("Usuario: " + dbc.getlogin().getId_usuario() + "\r\n");
            ticket.write("Fecha: " + Main.getFecha() + "\r\n");
            ticket.write("Hora: " + Main.getHora() + "\r\n");
            ticket.write("                               \r\n");


            //  CABECERA DE LOS PRODUCTOS
            ticket.write("--------------------------------\r\n");
            ticket.write("DESCRIPCION\r\n");
            ticket.write("CLAVE              MARCA\r\n");
            ticket.write("--------------------------------\r\n");

            ticket.write("                                \r\n");
            ticket.write("                                \r\n");

            for (int i = 0; i < data.size(); i++) {
                ticket.write("" + data.get(i).getDescripcion() + "\r\n");
                ticket.write("" + data.get(i).getCantidad());
                ticket.write("              " + data.get(i).getUnidad() + "\r\n");
                ticket.write("Ventas Clientes: " + dbd.getVentasxProd(data.get(i).getClave()));
                ticket.write("                                \r\n");
                ticket.write("                                \r\n");
            }
            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
            ticket.write("                                \r\n");
            ticket.write("Impreso el " + Main.getFecha() + " a las " + Main.getHora() + "\r\n");
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

            //Escribir en BT
            if(ons != null)
                ons.write(bites);

            //Delete temporal file
            file.delete();
            //ons.close();
            //bt.close();
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
