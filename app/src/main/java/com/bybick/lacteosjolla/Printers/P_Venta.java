package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Venta;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Venta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by bicktor on 20/06/2016.
 */
public class P_Venta {
    Context context;
    DBData dbd;
    DBConfig dbc;

    Venta venta;
    Cliente cliente;

    ConnectionBT bt;
    OutputStream ons;


    public P_Venta(Context context, Venta venta, Cliente cliente) {
        this.context = context;
        this.venta = venta;
        this.cliente = cliente;
    }

    public String imprimir() {
        dbc = new DBConfig(context);
        //main = new Main();
        String txt = "";
        String title = "== VENTA DE " + venta.getEmpresa() + " ==";
        title = center(title);
        boolean x = true;
        try{
//            do {
            OutputStreamWriter ticket = new OutputStreamWriter(context.openFileOutput("ticket.txt", Context.MODE_PRIVATE));
            //Escribir en el ticket * CABECERA GENERAL *
            ticket.write(center(title) + "\r\n\r\n");
            ticket.write("Usuario: " + dbc.getlogin().getId_usuario() + "\r\n");
            ticket.write("Cliente: " + cliente.getNombre() + "\r\n");
            ticket.write("Fecha: " + Main.getFecha() + "\r\n");
            ticket.write("Hora: " + Main.getHora() + "\r\n\r\n");

            ticket.write("Serie y Folio: " + venta.getSerie() + "  -  " + venta.getFolio());
            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");

            ticket.write("--------------------------------\r\n");
            ticket.write(center("Venta a " + venta.getForma_venta()));


            // * CABECERA DE LOS PRODUCTOS *
            ticket.write("--------------------------------\r\n");
            ticket.write(center("Descripcion") + "\r\n");
            ticket.write("Cant.                Precio    \r\n");
            ticket.write("Unidad               Importe\r\n");
            ticket.write("--------------------------------\r\n");
            ArrayList<Det_Venta> det_venta = venta.getDet_venta();

            for (int i = 0; i < det_venta.size(); i++) {
                ticket.write(center(det_venta.get(i).getDescripcion()) + "\r\n");
                ticket.write(" " + det_venta.get(i).getCantidad());
                ticket.write("             $" + det_venta.get(i).getPrecio() + "\r\n");
                ticket.write(" " + det_venta.get(i).getUnidad());
                ticket.write("             $" + det_venta.get(i).getTotal() + "\r\n");
            }
            String line;
            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");

            line = "SUBTOTAL:  $ " + venta.getSubtotal();
            line = align(line);
            ticket.write(line);
//                ticket.write("                                \r\n");
            ticket.write("                                \r\n");
//
            line = "IMPUESTOS: $ " + venta.getImpuestos();
            line = align(line);
            ticket.write(line);
            //              ticket.write("                                \r\n");
            ticket.write("                                \r\n");

            line = "TOTAL:     $ " + venta.getTotal();
            line = align(line);
            ticket.write(line);
            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");
//                ticket.write("                                \r\n");
//                ticket.write("                                \r\n");

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
        int spaces=12;
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
