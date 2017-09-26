package com.bybick.lacteosjolla.FragmentsReportes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bybick.lacteosjolla.Adapters.Adapter_Efectividad;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Ticket;
import com.bybick.lacteosjolla.ObjectView.Efectividad;
import com.bybick.lacteosjolla.Printers.P_ReportEfectividad;
import com.bybick.lacteosjolla.Printers.P_ReportVisitas;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class FR_Efectividad extends Fragment {
    Context context;
    ActionBar tb;
    FragmentManager fm;

    DBData dbd;

    TextView txtVetas;
    TextView txtCambios;
    TextView txtDevol;

    FloatingActionButton btnPrint;
    ListView lstClientes;

    ArrayList<Efectividad> data;

    public void setContext(Context context){
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbd = new DBData(context);
        dbd.open();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_efectividad, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tb.setTitle("Reportes");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tb.setTitle("Reporte Efectividad");

        data = dbd.getEfectividad();

        lstClientes.setAdapter(new Adapter_Efectividad(context, data));

        //Obtener porcentajes
        double total = data.size();
        double ventas = 0;
        double cambios = 0;
        double devol = 0;

        //Recorrer y sumar ventas, cambios y devoluciones
        for(int i = 0; i < data.size(); i ++){
            if(data.get(i).isVentas())
                ventas ++;
            if(data.get(i).isCambios())
                cambios ++;
            if(data.get(i).isDevoluciones())
                devol ++;
        }

        txtVetas.setText("Ventas: " + getPercent(total, ventas) + "% (" + (int) ventas + ")");
        txtCambios.setText("Cambios: " + getPercent(total, cambios) + "% (" + (int) cambios + ")");
        txtDevol.setText("Devoluciones: " + getPercent(total, devol) + "% (" + (int) devol + ")");

    }

    //Obtener Vistas
    public void getViews(View v) {
        //Vistas
        txtVetas = (TextView) v.findViewById(R.id.txtVendidos);
        txtCambios = (TextView) v.findViewById(R.id.txtCambios);
        txtDevol = (TextView) v.findViewById(R.id.txtDevoluciones);

        btnPrint = (FloatingActionButton) v.findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar Ticket
                P_ReportEfectividad print = new P_ReportEfectividad(context);
                print.setData(dbd.getEfectividad(),
                        txtVetas.getText().toString(),
                        txtCambios.getText().toString(),
                        txtDevol.getText().toString());

                F_Ticket ticket = new F_Ticket();
                ticket.setContext(context);
                ticket.setTb(tb);
                ticket.setTicket(print.imprimir());
                ticket.setPrint(print.getBT());

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.replace(R.id.ContainerConfig, ticket).addToBackStack("Reporte").commit();
            }
        });

        lstClientes = (ListView) v.findViewById(R.id.lstClientes);

    }

    public double getPercent(double total, double num){
        double porcentaje = (num * 100);

        porcentaje = porcentaje / total;

        return porcentaje;
    }
}
