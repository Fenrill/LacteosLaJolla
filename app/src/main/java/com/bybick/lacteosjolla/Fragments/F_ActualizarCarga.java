package com.bybick.lacteosjolla.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Carga;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.Printers.P_Carga;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 17/07/2016.
 */
public class F_ActualizarCarga extends Fragment implements View.OnClickListener{
    Context context;
    ActionBar tb;

    FragmentManager fm;
    DBConfig dbc;
    DBData dbd;

    //Menu
    MenuItem config;

    //Victas
    FloatingActionButton btnNext;
    ListView lstCarga;

    ArrayList<CargaView> carga;

    boolean fromConfig = false;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFromConfig(boolean fromConfig) {
        this.fromConfig = fromConfig;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();

        dbc = new DBConfig(context);
        dbc.open();

        dbd = new DBData(context);
        dbd.open();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_carga, container, false);
        getViews(v);
        tb.setTitle("Mis Productos");

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(carga.size() == 0) {
            Toast.makeText(context, "No se descargó Carga.", Toast.LENGTH_SHORT).show();
            fm.popBackStack();
        }
    }

    public void getViews(View v) {
        lstCarga = (ListView) v.findViewById(R.id.lstCarga);
        carga = dbd.getCarga();
        lstCarga.setAdapter(new Adapter_Carga(context, carga));

        btnNext = (FloatingActionButton) v.findViewById(R.id.btnClientes);
        btnNext.setImageResource(R.mipmap.ic_next);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClientes : {

                //Cargar Clientes

                FragmentTransaction ft;
                //Mostrar Ticket
                P_Carga print = new P_Carga(context);

                F_Ticket ticket = new F_Ticket();
                ticket.setContext(context);
                ticket.setTb(tb);
                ticket.setTicket(print.imprimir());
                ticket.setPrint(print.getBT());

                ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.replace(R.id.ContainerConfig, ticket).commit();

            }break;
        }
    }
}
