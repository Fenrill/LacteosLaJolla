package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectOUT.VentaResumen;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.ObjectView.Sobrantes;

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
 * Created by bicktor on 19/07/2016.
 */
public class P_ReportSobrantes {
    Context context;
    DBData dbd;
    DBConfig dbc;

    ArrayList<Sobrantes> data;

    ConnectionBT bt;
    OutputStream ons;

    public P_ReportSobrantes(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Sobrantes> data) {
        this.data = data;
    }


    public String imprimir(){

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        String txt="";
        String linenew;

        String title="** Sobrantes y Devoluciones **";

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
//            ticket.write(center("Descripcion")+"\r\n");
//            ticket.write("Carga            Malo           \r\n");
//            ticket.write("Ventas           Bueno          \r\n");
//            ticket.write("Cambio           FINAL          \r\n");
//            ticket.write("Devol.                          \r\n");
//            ticket.write("--------------------------------\r\n");

            ticket.write("                                \r\n");
            ticket.write(center("=== COBASUR ===\r\n"));
//            ticket.write("                                \r\n");
            for(int i=0;i<data.size();i++){
                if(data.get(i).getMarca().equals("COBASUR")) {
                    ticket.write("                                \r\n");
                    ticket.write(center(data.get(i).getDescripcion()) + "\r\n");
                    linenew = "Carga: " + FormatNumber(data.get(i).getCarga());
                    ticket.write(align(linenew));
                    ticket.write(" Malo: " + FormatNumber(data.get(i).getInv_mal()) + "\r\n");

                    linenew = "Ventas: " + FormatNumber(data.get(i).getVentas());
                    ticket.write(align(linenew));
                    ticket.write(" Bueno: " + FormatNumber(data.get(i).getInv_buen()) + "\r\n");

                    linenew = "Cambios: " + FormatNumber(data.get(i).getCambios());
                    ticket.write(align(linenew));
                    ticket.write(" FINAL: " + FormatNumber(data.get(i).getInv_final()) + "\r\n");

//                    ticket.write("Devol.: " + data.get(i).getDevoluciones() + "\r\n");
//                    ticket.write("\r\n");
                }
            }

            ticket.write("                                \r\n");
            ticket.write(center("=== JOLLA ===\r\n"));
//            ticket.write("                                \r\n");
            for(int i=0;i<data.size();i++){
                if(data.get(i).getMarca().equals("LA JOLLA")) {
                    ticket.write("                                \r\n");
                    ticket.write(center(data.get(i).getDescripcion()) + "\r\n");
                    linenew = "Carga: " + FormatNumber(data.get(i).getCarga());
                    ticket.write(align(linenew));
                    ticket.write(" Malo: " + FormatNumber(data.get(i).getInv_mal()) + "\r\n");

                    linenew = "Ventas: " + FormatNumber(data.get(i).getVentas());
                    ticket.write(align(linenew));
                    ticket.write(" Bueno: " + FormatNumber(data.get(i).getInv_buen()) + "\r\n");

                    linenew = "Cambios: " + FormatNumber(data.get(i).getCambios());
                    ticket.write(align(linenew));
                    ticket.write(" FINAL: " + FormatNumber(data.get(i).getInv_final()) + "\r\n");

//                    ticket.write("Devol.: " + data.get(i).getDevoluciones() + "\r\n");
//                    ticket.write("\r\n");
                }
            }
            String line;
            ticket.write("\r\n");
            ticket.write("-------RESUMEN DE VENTAS--------\r\n");
            ticket.write("\r\n");

            //Obtener Ventas
            ArrayList<VentaResumen> resumen = dbd.getResumenVentas();
            double VC_Jolla = 0;
            double VCR_Jolla = 0;

            double VC_Cobasur = 0;
            double VCR_Cobasur = 0;

            double VC_Totales = 0;
            double VCR_Totales = 0;

            //Redonder - Math.round( (precios.get(spUnidades.getSelectedItemPosition()).getImpuestos()*Cantidad_Detalle) *100.0)/100.0;

            //RESUMEN DE LA JOLLA
            for(int i = 0; i < resumen.size(); i ++){
                if(resumen.get(i).getEmpresa().equals("LA JOLLA") && resumen.get(i).getId_forma_venta() == 1)
                    VC_Jolla += resumen.get(i).getTotal();
                else if(resumen.get(i).getEmpresa().equals("LA JOLLA") && resumen.get(i).getId_forma_venta() == 2)
                    VCR_Jolla += resumen.get(i).getTotal();
            }

            //RESUMEN DE COBASUR
            for(int i = 0; i < resumen.size(); i ++){
                if(resumen.get(i).getEmpresa().equals("COBASUR") && resumen.get(i).getId_forma_venta() == 1)
                    VC_Cobasur += resumen.get(i).getTotal();
                else if(resumen.get(i).getEmpresa().equals("COBASUR") && resumen.get(i).getId_forma_venta() == 2)
                    VCR_Cobasur += resumen.get(i).getTotal();
            }

            //RESUMEN GENERAL
            for(int i = 0; i < resumen.size(); i ++){
                if(resumen.get(i).getId_forma_venta() == 1)
                    VC_Totales += resumen.get(i).getTotal();
                else if(resumen.get(i).getId_forma_venta() == 2)
                    VCR_Totales += resumen.get(i).getTotal();
            }

            ticket.write(" - VENTAS LA JOLLA -\r\n");
            ticket.write("     Contado         $ " + FormatNumber(VC_Jolla) + "\r\n");
            ticket.write("     Credito         $ " + FormatNumber(VCR_Jolla) + "\r\n");

            ticket.write("\r\n");

            ticket.write(" - VENTAS COBASUR -\r\n");
            ticket.write("     Contado         $ " + FormatNumber(VC_Cobasur) + "\r\n");
            ticket.write("     Credito         $ " + FormatNumber(VCR_Cobasur) + "\r\n");

            ticket.write("\r\n");

            ticket.write(" - TOTAL DE VENTA -\r\n");
            ticket.write("     Contado         $ " + FormatNumber(VC_Totales) + "\r\n");
            ticket.write("     Credito         $ " + FormatNumber(VCR_Totales) + "\r\n");
            ticket.write("                                \r\n");
            ticket.write("                                \r\n");
            ticket.write(" - TOTAL DE COBRANZA -\r\n");
            ticket.write("     Total           $ " + FormatNumber(dbd.getTotalCobranza()) +"\r\n");
//            ticket.write("                                \r\n");
//            ticket.write("                                \r\n");
//            ticket.write(" - TOTAL DE DEVOLUCION -\r\n");
//            ticket.write("     Total           $ " + dbd.getTotalDevolucion() +"\r\n");
            ticket.write("                                \r\n");
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
        int spaces=4;
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
