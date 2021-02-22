package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.NoVenta;
import com.bybick.lacteosjolla.ObjectOUT.Venta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class P_NoVenta {
    Context context;
    DBData dbd;
    DBConfig dbc;

    NoVenta noVenta;
    Cliente cliente;

    ConnectionBT bt;
    OutputStream ons;

    public P_NoVenta(Context context, NoVenta noVenta,Cliente cliente){
        this.context = context;
        this.noVenta = noVenta;
        this.cliente = cliente;
    }

    public String imprimir(){
        dbc = new DBConfig(context);
        String txt = "";
        String title = "== NO VENTA ==";
        title = center(title);
        boolean x = true;

        try{
            OutputStreamWriter ticket = new OutputStreamWriter(context.openFileOutput("tiket.txt", Context.MODE_PRIVATE));
            ticket.write(center("== CARACOL =="));
            ticket.write("Usuario: " + dbc.getlogin().getId_usuario() + "\r\n");
            ticket.write("No Cliente: "+ cliente.getId_cliente() + "\r\n");
            ticket.write("Cliente: " + cliente.getNombre() + "\r\n");
            ticket.write("Fecha: " + Main.getFecha() + "\r\n");
            ticket.write("Hora: " + Main.getHora() + "\r\n");

            ticket.write("--------------------------------\r\n");
            ticket.write(center("== NO VENTA =="));
            ticket.write("--------------------------------\r\n");

            ticket.write(center("Motivo de No Venta:"));
            ticket.write(" " + noVenta.getMotivo() + "\r\n");
            ticket.write(center("Comentario:"));
            ticket.write(" " + noVenta.getComentario() + "\r\n");

            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
            ticket.write("                                \r\n");
//            ticket.write(center("!Gracias por su Compra!"));
            ticket.write(center("Tel. Atencion al Cliente"));
            ticket.write(center("01-800-849-8820"));
            ticket.write(center("www.caracolmexico.com"));
            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");


            ticket.close();

            //Escribr Ticket
//            File file = new File("/data/user/0/com.bybick.lacteosjolla/files/tiket.txt");
            File f = new File(Environment.getDataDirectory() + "/data/data/com.bybick.lacteosjolla/files/ticket.txt");
            File file;
            if (f.isDirectory()){
                file = new File("/data/data/com.bybick.lacteosjolla/files/ticket.txt");
            } else{
                file = new File("/data/user/0/com.bybick.lacteosjolla/files/tiket.txt");
            }
            byte[] bites = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bites);
            txt = new String(bites);

            //Conexion
            bt = new ConnectionBT(context);
            ons = bt.connect();

            if (ons != null){
                ons.write(bites);
            }

            //borrar
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
        int spaces=11;
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
