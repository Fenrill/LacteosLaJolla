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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bybick.lacteosjolla.Adapters.Adapter_Carga;
import com.bybick.lacteosjolla.Adapters.Adapter_Ventas;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Ticket;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.Printers.P_ReportVentas;
import com.bybick.lacteosjolla.Printers.P_ReportVisitas;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class FR_VentasResult extends Fragment{
    Context context;
    DBData dbd;
    ActionBar tb;
    FragmentManager fm;

    Adapter_Ventas adapter;

    ArrayList<CargaView> data;

    ListView lstProductos;

    FloatingActionButton btnAceptar;

    public void setContext(Context context){
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    public void setData(ArrayList<CargaView> data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_ventas_2, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle("Reporte Ventas");
    }

    public void getViews(View v){
        lstProductos = (ListView) v.findViewById(R.id.lstProductos);

        btnAceptar = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar Ticket
                P_ReportVentas print = new P_ReportVentas(context);
                print.setData(data);

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

        adapter = new Adapter_Ventas(context, data);
        lstProductos.setAdapter(adapter);

    }


}
