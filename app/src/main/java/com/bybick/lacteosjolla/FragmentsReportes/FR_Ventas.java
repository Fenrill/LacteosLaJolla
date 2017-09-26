package com.bybick.lacteosjolla.FragmentsReportes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.Adapters.Adapter_Carga;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.Adapters.Adapter_Ventas;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Carga;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class FR_Ventas extends Fragment implements AdapterView.OnItemClickListener{
    Context context;
    DBData dbd;
    ActionBar tb;
    FragmentManager fm;

    Adapter_Carga adapter;

    ArrayList<CargaView> data;
    ArrayList<CargaView> seleccionados;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

        seleccionados = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_ventas_1, container, false);
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
                /*Mostrar lo de Seleccion*/
                FR_VentasResult frag = new FR_VentasResult();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fm);
                frag.setData(seleccionados);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "VentasReport").addToBackStack("Ventas").commit();

            }
        });

        data = dbd.getCarga();
        adapter = new Adapter_Carga(context, data);
        lstProductos.setAdapter(adapter);
        lstProductos.setOnItemClickListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tb.setTitle("Reportes");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        CargaView item = data.get(pos);
        if(item.isReporte()) {
            item.setReporte(false);
            seleccionados.remove(item);
        }else {
            item.setReporte(true);
            seleccionados.add(item);
        }
        data.set(pos, item);

        ((BaseAdapter) lstProductos.getAdapter()).notifyDataSetChanged();
    }
}
