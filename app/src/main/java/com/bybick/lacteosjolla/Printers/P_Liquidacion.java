package com.bybick.lacteosjolla.Printers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Efectivo;
import com.bybick.lacteosjolla.ObjectOUT.CobranzaEfectivo;
import com.bybick.lacteosjolla.ObjectOUT.VentaLiquidacion;
import com.bybick.lacteosjolla.ObjectOUT.VentaResumen;
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
 * Created by Clowny on 9/21/2017.
 */

public class P_Liquidacion {
    Context context;
    DBData dbd;
    DBConfig dbc;

    ConnectionBT bt;
    OutputStream ons;

    double VCJOLLA_Totales = 0;
    double VCCS_Totales = 0;

    double var1 = 1000;
    double var2 = 500;
    double var3 = 200;
    double var4 = 100;
    double var5 = 50;
    double var6 = 20;
    double var7 = 10;
    double var8 = 5;
    double var9 = 2;
    double var10 = 1;
    double var11 = 0.5;
    double var12 = 0.2;
    double var13 = 0.1;

    double sumEfectivo;

    public P_Liquidacion(Context context) {
        this.context = context;
    }

    public String imprimir(){

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        String txt="";

        String title="** Reporte Liquidacion **";

        Efectivo efectivo = dbd.getEfectivo();

        try {
            OutputStreamWriter ticket = new OutputStreamWriter(context.openFileOutput("ticket.txt", Context.MODE_PRIVATE));

            //Escribir en el ticket  CABECERA GENERAL
            ticket.write(center(title) + "\r\n");
            ticket.write("Usuario: " + dbc.getlogin().getId_usuario() + "\r\n");
            ticket.write("Fecha: " + Main.getFecha() + "\r\n");
            ticket.write("Hora: " + Main.getHora() + "\r\n");
            ticket.write("Ultimo Folio: " + dbd.getLastFolio() + "\r\n");
            ticket.write("                               \r\n");
            ticket.write("-------------------------------\r\n");
            ticket.write(center("-Bancomer-") + "\r\n");
//            ticket.write("                               \r\n");
            ticket.write("La Jolla: 0444191781           \r\n");
//            ticket.write("Cobasur:  0448927014           \r\n");


            //  CABECERA DE LOS PRODUCTOS
            ticket.write("--------------------------------\r\n");
            String line;
//            ticket.write("\r\n");
//            ticket.write("-------RESUMEN DE VENTAS--------\r\n");
//            ticket.write("\r\n");

            //Obtener Ventas
            ArrayList<VentaResumen> resumen = dbd.getResumenVentas();
            ArrayList<VentaLiquidacion> liquidacion = dbd.getResumenVentasLiquidacion();
            ArrayList<CobranzaEfectivo> cobrae = dbd.getCobranzaEfectivo();
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

            VCJOLLA_Totales = VC_Jolla + dbd.getTotalCRLPEfectivo();
            VCCS_Totales = VC_Cobasur + dbd.getTotalCRCSEfectivo();

            //RESUMEN GENERAL
            for(int i = 0; i < resumen.size(); i ++){
                if(resumen.get(i).getId_forma_venta() == 1)
                    VC_Totales += resumen.get(i).getTotal();
                else if(resumen.get(i).getId_forma_venta() == 2)
                    VCR_Totales += resumen.get(i).getTotal();
            }

            ticket.write(" - VENTAS LA JOLLA -\r\n");
//            ticket.write("     Practicaja      $ " + efectivo.getPracticaja() + "\r\n");
            ticket.write("     Credito         $ " + FormatNumber(VCR_Jolla) + "\r\n");
            ticket.write("     Contado         $ " + FormatNumber(VC_Jolla) + "\r\n");
            ticket.write("       -Efectivo     $ " + FormatNumber(dbd.getTotalContadoDesglose(1)) + "\r\n");
            ticket.write("       -Cheques      $ " + FormatNumber(dbd.getTotalContadoDesglose(2)) + "\r\n");
            ticket.write("       -Tranferencia $ " + FormatNumber(dbd.getTotalContadoDesglose(3)) + "\r\n");
            ticket.write("     Cobranza        $ " + FormatNumber(dbd.getTotalCobranzaLP()) + "\r\n");
            ticket.write("       -Efectivo     $ " + FormatNumber(dbd.getTotalCRLPEfectivo()) + "\r\n");
            ticket.write("       -Cheques      $ " + FormatNumber(dbd.getTotalCRLPCheques()) + "\r\n");
            ticket.write("       -Tranferencia $ " + FormatNumber(dbd.getTotalCRLPTranferencia()) + "\r\n");
            ticket.write("Total Efectivo       $ " + FormatNumber(VCJOLLA_Totales-efectivo.getPracticaja()) + "\r\n");

            ticket.write("\r\n");

//            ticket.write("********************************\r\n");
//
//            ticket.write("DESGLOSE EFFECTIVO \r\n");
//            ticket.write("  BILLETES:            " +  "\r\n");
//            ticket.write(display(efectivo.getBillete_1000(), var1));
//            ticket.write(display(efectivo.getBillete_500(), var2));
//            ticket.write(display(efectivo.getBillete_200(), var3));
//            ticket.write(display(efectivo.getBillete_100(), var4));
//            ticket.write(display(efectivo.getBillete_50(), var5));
//            ticket.write(display(efectivo.getBillete_20(), var6));
//            ticket.write("  MONEDAS:             " + "\r\n");
//            ticket.write(display(efectivo.getMoneda_100(),var4));
//            ticket.write(display(efectivo.getMoneda_50(), var5));
//            ticket.write(display(efectivo.getMoneda_20(), var6));
//            ticket.write(display(efectivo.getMoneda_10(), var7));
//            ticket.write(display(efectivo.getMoneda_5(), var8));
//            ticket.write(display(efectivo.getMoneda_2(), var9));
//            ticket.write(display(efectivo.getMoneda_1(), var10));
//            ticket.write(display(efectivo.getMoneda_05(), var11));
//            ticket.write(display(efectivo.getMoneda_02(), var12));
//            ticket.write(display(efectivo.getMoneda_01(), var13));
//            ticket.write("                ------------    \r\n");
//            ticket.write(" Total Efectivo:     $ " + sumEfectivo + "\r\n");


            ticket.write("********************************\r\n");
//
//            double cheque = 0.00;
//            ticket.write("--------------------------------\r\n");
//            ticket.write(center("VENTAS - CHEQUE"));
//            ticket.write("--------------------------------\r\n");
//            for (int i = 0;i < liquidacion.size(); i++){
//                if (liquidacion.get(i).getId_metodo_venta() == 2) {
//                    ticket.write("-" + liquidacion.get(i).getId_cliente() + " - " + liquidacion.get(i).getNombre() + "\r\n");
//                    ticket.write(" Importe: $" + liquidacion.get(i).getTotal() + "\r\n");
//                    cheque += liquidacion.get(i).getTotal();
//                }
//            }
//            ticket.write("           --------------       \r\n");
//
//            ticket.write(" Total:     $ " + cheque + "\r\n");
//            ticket.write("\r\n");
//
//
//            double trans = 0.00;
//            ticket.write("--------------------------------\r\n");
//            ticket.write(center("VENTAS - TRANSFERENCIA"));
//            ticket.write("--------------------------------\r\n");
//            for (int i = 0;i < liquidacion.size(); i++){
//                if (liquidacion.get(i).getId_metodo_venta() == 3) {
//                    ticket.write("-" + liquidacion.get(i).getId_cliente() + " - " + liquidacion.get(i).getNombre() + "\r\n");
//                    ticket.write(" Importe: $" + liquidacion.get(i).getTotal() + "\r\n");
//                    trans += liquidacion.get(i).getTotal();
//                }
//            }
//            ticket.write("           --------------       \r\n");
//
//            ticket.write(" Total:     $ " + trans + "\r\n");
//            ticket.write("\r\n");
//
//            double credito = 0.00;
//            ticket.write("--------------------------------\r\n");
//            ticket.write(center("CREDITO"));
//            ticket.write("--------------------------------\r\n");
//            for (int i = 0;i < liquidacion.size(); i++){
//                if (liquidacion.get(i).getId_forma_venta() == 2) {
//                    ticket.write("-" + liquidacion.get(i).getId_cliente() + " - " + liquidacion.get(i).getNombre() + "\r\n");
//                    ticket.write(" Importe: $" + liquidacion.get(i).getTotal() + "\r\n");
//                    credito += liquidacion.get(i).getTotal();
//                }
//            }
//            ticket.write("           --------------       \r\n");
//
//            ticket.write(" Total:     $ " + credito + "\r\n");
//            ticket.write("\r\n");
//
//            double cobranza = 0.00;
//            ticket.write("--------------------------------\r\n");
//            ticket.write(center("COBRANZA"));
//            ticket.write("--------------------------------\r\n");
//            for (int i = 0;i < cobrae.size(); i++){
//                ticket.write("-" + cobrae.get(i).getId_cliente() + " - " + cobrae.get(i).getNombre() + "\r\n");
//                ticket.write(" Importe: $" + cobrae.get(i).getImporte_pago() + "\r\n");
//                cobranza += cobrae.get(i).getImporte_pago();
//            }
//            ticket.write("           --------------       \r\n");
//
//            ticket.write(" Total:     $ " + cobranza + "\r\n");
//            ticket.write("\r\n");
//
//
//            ticket.write("********************************\r\n");
//            ticket.write("\r\n");
//            ticket.write("\r\n");
//            ticket.write("\r\n");
//            ticket.write("\r\n");
//
//            ticket.write("--------------------------------\r\n");
//            ticket.write(center("FIRMA DEL ENCARGADO"));

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

    public String display(double data, double var){
        double x = data * var;
        String line = "";
        if (x == 0){
            line = "";
        }else{
            line = "    $" + var + " x " + data + " = " + FormatNumber(x) + "\r\n";
            sumEfectivo += x;
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
