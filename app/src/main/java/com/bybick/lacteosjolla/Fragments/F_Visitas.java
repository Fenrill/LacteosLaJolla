package com.bybick.lacteosjolla.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bybick.lacteosjolla.Adapters.Adapter_Visita;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 07/06/2016.
 */
public class F_Visitas extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    Context context;
    ActionBar tb;
    Cliente cliente;

    ArrayList<Visita> data;

    FragmentManager fm;
    DBConfig dbc;
    DBData dbd;

    FloatingActionButton btnAgregar;
    ListView lstVisitas;

    public static boolean check = true;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();

        dbc = new DBConfig(context);
        dbc.open();

        dbd = new DBData(context);
        dbd.open();

        data = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_visitas, container, false);
        getViews(v);

        return v;
    }

    public void getViews(View v) {
        tb.setTitle(cliente.getNombre());

        //Obtener Boton Agregar
        btnAgregar = (FloatingActionButton) v.findViewById(R.id.btnAddVisita);
        btnAgregar.setOnClickListener(this);

        //Obtener Lista de Visitas
        lstVisitas = (ListView) v.findViewById(R.id.lstVisitas);
        lstVisitas.setOnItemClickListener(this);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle(cliente.getNombre());

        data = dbd.getVisitaCliente(cliente.getId_cliente());
        lstVisitas.setAdapter(new Adapter_Visita(context, data));

        if(check) {
            if (data.size() == 0) {
                F_Visita frag = new F_Visita();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fm);
                frag.setCliente(cliente);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.Container, frag, "Visita").addToBackStack("Visitas").commit();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddVisita : {
                F_Visita frag = new F_Visita();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fm);
                frag.setCliente(cliente);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.Container, frag, "Visita").addToBackStack("Visitas").commit();

            }break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        F_Visita frag = new F_Visita();
        frag.setContext(context);
        frag.setTb(tb);
        frag.setFmMain(fm);
        frag.setCliente(cliente);
        frag.setVisita(data.get(position));

        Log.e("ID_Visita SELECT", "ID: " + data.get(position).getId_visita());

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                R.animator.enter_anim, R.animator.out_anim);
        ft.replace(R.id.Container, frag, "Visita").addToBackStack("Visitas").commit();

    }
}
