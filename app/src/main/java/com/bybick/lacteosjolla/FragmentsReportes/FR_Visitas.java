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

import com.bybick.lacteosjolla.Adapters.Adapter_Cliente;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Ticket;
import com.bybick.lacteosjolla.Printers.P_Carga;
import com.bybick.lacteosjolla.Printers.P_ReportVisitas;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class FR_Visitas extends Fragment {
    Context context;
    FragmentManager fm;
    ActionBar tb;

    DBData dbd;


    //Vistas
    TextView txtVisitas;
    TextView txtNoVisitas;
    FloatingActionButton btnPrint;
    ListView lstClientes;

    public void setContext(Context context){
        this.context = context;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbd= new DBData(context);
        dbd.open();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_visitas, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle("Reporte Visitas");

        ArrayList<String> pors = dbd.getPorcentajes();

        txtVisitas.setText("Visitados: " + pors.get(0));
        txtNoVisitas.setText("No Visitados: " + pors.get(1));

        lstClientes.setAdapter(new Adapter_Cliente(context, dbd.getReporteVisitas()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tb.setTitle("Reportes");
    }

    public void getViews(View v) {
        //Vistas
        txtVisitas = (TextView) v.findViewById(R.id.txtVisitados);
        txtNoVisitas = (TextView) v.findViewById(R.id.txtNoVisitados);

        btnPrint = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar Ticket
                P_ReportVisitas print = new P_ReportVisitas(context);
                print.setData(dbd.getReporteVisitas(), txtVisitas.getText().toString(), txtNoVisitas.getText().toString());

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


        lstClientes = (ListView) v.findViewById(R.id.lstVisitados);
    }
}
