package com.bybick.lacteosjolla.Fragments;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Efectivo;
import com.bybick.lacteosjolla.ObjectOUT.VentaResumen;
import com.bybick.lacteosjolla.Printers.P_Liquidacion;
import com.bybick.lacteosjolla.Printers.P_ReportSobrantes;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;

/**
 * Created by Clowny on 9/12/2017.
 */

public class F_Liquid extends Fragment implements View.OnClickListener {

    Context context;
    FragmentManager fmMain;
    FragmentManager fm;

    RelativeLayout vista;

    ActionBar tb;
    DBData dbd;
    DBConfig dbc;

    FloatingActionButton btnPrint;

    boolean flag;

    Efectivo efectivo;

    //Views
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    TextView textView10;
    TextView textView11;
    TextView textView12;
    TextView textView13;
    TextView textView14;

    TextView Practicaja;
    TextView txtBilletes1;
    TextView txtBilletes2;
    TextView txtBilletes3;
    TextView txtBilletes4;
    TextView txtBilletes5;
    TextView txtBilletes6;

    TextView txtMonedas1;
    TextView txtMonedas2;
    TextView txtMonedas3;
    TextView txtMonedas4;
    TextView txtMonedas5;
    TextView txtMonedas6;
    TextView txtMonedas7;
    TextView txtMonedas8;
    TextView txtMonedas9;
    TextView txtMonedas10;

    int billetes1 = 1000;
    int billetes2 = 500;
    int billetes3 = 200;
    int billetes4 = 100;
    int billetes5 = 50;
    int billetes6 = 20;
    int monedas1 = 100;
    int monedas2 = 50;
    int monedas3 = 20;
    int monedas4 = 10;
    int monedas5 = 5;
    int monedas6 = 2;
    int monedas7 = 1;
    double monedas8 = 0.5;
    double monedas9 = 0.2;
    double monedas10 = 0.1;

    ArrayList<VentaResumen> resumen = new ArrayList<>();

    //variables ventas
    double VC_Jolla = 0;
    double VCR_Jolla = 0;

    double VC_Cobasur = 0;
    double VCR_Cobasur = 0;

    double VCJOLLA_Totales = 0;
    double VCCBS_Totales = 0;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFmMain(FragmentManager fmMain) {
        this.fmMain = fmMain;
    }

    public void setFm(FragmentManager fmChild) {
        this.fm = fmChild;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_liquid, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();
    }


    public void getViews(View v){
        //set title
        tb.setTitle("Practicajas");

        btnPrint = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);

        //select and set textviews
        textView1 = (TextView) v.findViewById(R.id.textView1);
        textView2 = (TextView) v.findViewById(R.id.textView2);
        textView3 = (TextView) v.findViewById(R.id.textView3);
//        textView4 = (TextView) v.findViewById(R.id.textView4);
//        textView5 = (TextView) v.findViewById(R.id.textView5);
//        textView6 = (TextView) v.findViewById(R.id.textView6);
        textView7 = (TextView) v.findViewById(R.id.textView7);
//        textView8 = (TextView) v.findViewById(R.id.textView8);
        textView9 = (TextView) v.findViewById(R.id.textView9);
        textView10 = (TextView) v.findViewById(R.id.textView10);
        textView11 = (TextView) v.findViewById(R.id.textView11);
//        textView12 = (TextView) v.findViewById(R.id.textView12);
//        textView13 = (TextView) v.findViewById(R.id.textView13);
//        textView14 = (TextView) v.findViewById(R.id.textView14);

        Practicaja = (TextView) v.findViewById(R.id.cntPracticaja);
        txtBilletes1 = (TextView) v.findViewById(R.id.txtCantidad1);
        txtBilletes2 = (TextView) v.findViewById(R.id.txtCantidad2);
        txtBilletes3 = (TextView) v.findViewById(R.id.txtCantidad3);
        txtBilletes4 = (TextView) v.findViewById(R.id.txtCantidad4);
        txtBilletes5 = (TextView) v.findViewById(R.id.txtCantidad5);
        txtBilletes6 = (TextView) v.findViewById(R.id.txtCantidad6);

        txtMonedas1 = (TextView) v.findViewById(R.id.txtCantidad19);
        txtMonedas2 = (TextView) v.findViewById(R.id.txtCantidad20);
        txtMonedas3 = (TextView) v.findViewById(R.id.txtCantidad21);
        txtMonedas4 = (TextView) v.findViewById(R.id.txtCantidad22);
        txtMonedas5 = (TextView) v.findViewById(R.id.txtCantidad23);
        txtMonedas6 = (TextView) v.findViewById(R.id.txtCantidad24);
        txtMonedas7 = (TextView) v.findViewById(R.id.txtCantidad25);
        txtMonedas8 = (TextView) v.findViewById(R.id.txtCantidad26);
        txtMonedas9 = (TextView) v.findViewById(R.id.txtCantidad27);
        txtMonedas10 = (TextView) v.findViewById(R.id.txtCantidad28);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //Obtener Ventas
        resumen = dbd.getResumenVentas();

        //Obtener Efectivo
        efectivo = dbd.getEfectivo();

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
        VCCBS_Totales = VC_Cobasur + dbd.getTotalCRCSEfectivo();

        DecimalFormatSymbols sim = new DecimalFormatSymbols();
        sim.setDecimalSeparator('.');
        sim.setGroupingSeparator(',');
        DecimalFormat fd = new DecimalFormat("###,##0.00", sim);

        Practicaja.setText("$" + fd.format(efectivo.getPracticaja()));
        textView7.setText("$" + fd.format(VCR_Jolla));
        textView1.setText("$" + fd.format(VC_Jolla));
        textView2.setText("$" + fd.format(dbd.getTotalCobranzaLP()));
        textView9.setText("$" + fd.format(dbd.getTotalCRLPEfectivo()));
        textView10.setText("$" + fd.format(dbd.getTotalCRLPCheques()));
        textView11.setText("$" + fd.format(dbd.getTotalCRLPTranferencia()));
        textView3.setText("$" + fd.format(VCJOLLA_Totales - efectivo.getPracticaja()));
//        textView8.setText("$" + fd.format(VCR_Cobasur));
//        textView4.setText("$" + fd.format(VC_Cobasur));
//        textView5.setText("$" + fd.format(dbd.getTotalCobranzaCS()));
//        textView12.setText("$" + fd.format(dbd.getTotalCRCSEfectivo()));
//        textView13.setText("$" + fd.format(dbd.getTotalCRCSCheques()));
//        textView14.setText("$" + fd.format(dbd.getTotalCRCSTranferencia()));
//        textView6.setText("$" + fd.format(VCCBS_Totales));

//        txtBilletes1.setText("" + efectivo.getBillete_1000() + " x " + billetes1 + " = " + FormatNumber(efectivo.getBillete_1000()*billetes1));
//        txtBilletes2.setText("" + efectivo.getBillete_500() + " x " + billetes2 + " = " + FormatNumber(efectivo.getBillete_500()*billetes2));
//        txtBilletes3.setText("" + efectivo.getBillete_200() + " x " + billetes3 + " = " + FormatNumber(efectivo.getBillete_200()*billetes3));
//        txtBilletes4.setText("" + efectivo.getBillete_100() + " x " + billetes4 + " = " + FormatNumber(efectivo.getBillete_100()*billetes4));
//        txtBilletes5.setText("" + efectivo.getBillete_50() + " x " + billetes5 + " = " + FormatNumber(efectivo.getBillete_50()*billetes5));
//        txtBilletes6.setText("" + efectivo.getBillete_20() + " x " + billetes6 + " = " + FormatNumber(efectivo.getBillete_20()*billetes6));
//
//        txtMonedas1.setText("" + efectivo.getMoneda_100() + " x " + monedas1 + " = " + FormatNumber(efectivo.getMoneda_100()*monedas1));
//        txtMonedas2.setText("" + efectivo.getMoneda_50() + " x " + monedas2 + " = " + FormatNumber(efectivo.getMoneda_50()*monedas2));
//        txtMonedas3.setText("" + efectivo.getMoneda_20() + " x " + monedas3 + " = " + FormatNumber(efectivo.getMoneda_20()*monedas3));
//        txtMonedas4.setText("" + efectivo.getMoneda_10() + " x " + monedas4 + " = " + FormatNumber(efectivo.getMoneda_10()*monedas4));
//        txtMonedas5.setText("" + efectivo.getMoneda_5() + " x " + monedas5 + " = " + FormatNumber(efectivo.getMoneda_5()*monedas5));
//        txtMonedas6.setText("" + efectivo.getMoneda_2() + " x " + monedas6 + " = " + FormatNumber(efectivo.getMoneda_2()*monedas6));
//        txtMonedas7.setText("" + efectivo.getMoneda_1() + " x " + monedas7 + " = " + FormatNumber(efectivo.getMoneda_1()*monedas7));
//        txtMonedas8.setText("" + efectivo.getMoneda_05() + " x " + monedas8 + " = " + FormatNumber(efectivo.getMoneda_05()*monedas8));
//        txtMonedas9.setText("" + efectivo.getMoneda_02() + " x " + monedas9 + " = " + FormatNumber(efectivo.getMoneda_02()*monedas9));
//        txtMonedas10.setText("" + efectivo.getMoneda_01() + " x " + monedas10 + " = " + FormatNumber(efectivo.getMoneda_01()*monedas10));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrint : {
                P_Liquidacion print = new P_Liquidacion(context);

                F_Ticket ticket = new F_Ticket();
                ticket.setContext(context);
                ticket.setTb(tb);
                ticket.setTicket(print.imprimir());
                ticket.setTitle("Liquidacion");
                ticket.setPrint(print.getBT());

                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.replace(R.id.Container, ticket).addToBackStack("F_Clientes").commit();

                dbd.setTerminado();
                flag = true;
            }
            break;
        }
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

