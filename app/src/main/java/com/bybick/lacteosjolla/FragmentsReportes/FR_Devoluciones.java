package com.bybick.lacteosjolla.FragmentsReportes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bybick.lacteosjolla.Adapters.Adapter_Sobrantes;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Ticket;
import com.bybick.lacteosjolla.ObjectView.Sobrantes;
import com.bybick.lacteosjolla.Printers.P_ReportEfectividad;
import com.bybick.lacteosjolla.Printers.P_ReportSobrantes;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class FR_Devoluciones extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{
    Context context;
    ActionBar tb;
    FragmentManager fm;

    DBData dbd;

    ArrayList<Sobrantes> data;

    FloatingActionButton btn_print;
    ListView lstSobrantes;


    public void setContext(Context context){
        this.context=context;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.report_sobrantes,container,false);
        getViews(v);

        data = dbd.getSobrantes();
        lstSobrantes.setAdapter(new Adapter_Sobrantes(context, data));

        return v;
    }
    public void getViews(View v){
        lstSobrantes = (ListView) v.findViewById(R.id.lstSobrantes);
        lstSobrantes.setOnItemClickListener(this);

        btn_print = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btn_print.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
        ArrayList<Det_Sobrantes> det=db.getDet_Sobrantes(data.get(position).getId_producto());
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflate=getActivity().getLayoutInflater();
        View vista=inflate.inflate(R.layout.dialog_det_sobrantes,null);
        final ListView detalles=(ListView) vista.findViewById(R.id.lstDetSobrantes);
        detalles.setAdapter(new Adapter_Detalle_Sobrantes(context,det));
        builder.setView(vista)
                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPrint:{
                //Mostrar Ticket
                P_ReportSobrantes print = new P_ReportSobrantes(context);
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

                dbd.setTerminado();
/*
                btn_print.setBackgroundResource(R.drawable.ic_print);
                Fragment_Main.ticket.setVisibility(TextView.VISIBLE);
                Fragment_Main.close_ticket.setVisibility(TextView.VISIBLE);
                Fragment_Main.ticket.setText(print.imprimir());
*/
            }break;
        }
    }
}
