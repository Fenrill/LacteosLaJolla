package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.ObjectOUT.Pago;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 * Created by bicktor on 15/07/2016.
 */
public class P_Pago {
    Context context;
    DBData dbd;
    DBConfig dbc;

    Pago pago;
    Cliente cliente;
    double importeAnterior;

    ConnectionBT bt;
    OutputStream ons;

    public P_Pago(Context context, Pago pago, Cliente cliente) {
        this.context = context;
        this.pago = pago;
        this.cliente = cliente;
        this.importeAnterior = importeAnterior;
    }

    public String imprimir() {
        dbc = new DBConfig(context);
        //main = new Main();
        String txt = "";
        String title = "== PAGO DE " + cliente.getNombre() + " ==";
        title = center(title);
        boolean x = true;
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


            // * CABECERA DE LOS PRODUCTOS *
            ticket.write("--------------------------------\r\n");
            ticket.write("Serie     |   Folio  |  Importe \r\n");
            ticket.write("--------------------------------\r\n");

            ArrayList<Det_Pago> data = pago.getDetalles();
            for (int i = 0; i < data.size(); i++) {
                ticket.write("" + data.get(i).getSerie());
                ticket.write("      " + data.get(i).getFolio());
                ticket.write("       " + data.get(i).getImporte_pago() + "\r\n");
                ticket.write("\r\n");
                ticket.write("Forma de Pago: " + data.get(i).getForma_pago() + "\r\n");
                ticket.write("Saldo Anterior: $ " + FormatNumber(data.get(i).getSaldo_ant()) + "\r\n");
                ticket.write("Saldo Actual: $ " + FormatNumber(data.get(i).getSaldo()));
                ticket.write("\r\n================================");
                ticket.write("\r\n");
            }

            String line;
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

    //Formatear Numero de double a String
    public String FormatNumber(double number) {
        String result = "";

        DecimalFormatSymbols sim = new DecimalFormatSymbols();
        sim.setDecimalSeparator('.');
        sim.setGroupingSeparator(',');
        DecimalFormat fd = new DecimalFormat("###,##0.00", sim);

        result = fd.format(number);

        return result;
    }


}
