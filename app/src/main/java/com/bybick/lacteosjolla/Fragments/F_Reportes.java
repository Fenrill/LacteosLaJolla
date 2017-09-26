package com.bybick.lacteosjolla.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.bybick.lacteosjolla.Adapters.Adapter_Actualizar;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Devoluciones;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Efectividad;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Ventas;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Visitas;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Carga;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Equivalencia;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.Formas;
import com.bybick.lacteosjolla.ObjectIN.Lista;
import com.bybick.lacteosjolla.ObjectIN.Listas_Precios;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Producto_unidad;
import com.bybick.lacteosjolla.ObjectIN.Secuencia;
import com.bybick.lacteosjolla.ObjectIN.Serie;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bicktor on 17/07/2016.
 */
public class F_Reportes extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    Context context;
    ActionBar tb;

    FragmentManager fm;
    FragmentManager fmChild;
    DBConfig dbc;
    DBData dbd;

    ArrayList<Lista> data;

    //Vistas
    ListView lstItems;
    FloatingActionButton btnNext;


    public void setContext(Context context) {
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
        fmChild = getChildFragmentManager();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        tb.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_descarga, container, false);
        getViews(v);

        data = new ArrayList<>();

        data.add(new Lista(R.mipmap.ic_reportes, "Sobrantes y Devoluciones", "Reporte de los Productos sobrados o con devolucion"));
        data.add(new Lista(R.mipmap.ic_reportes, "Visitas", "Cuantos clientes se visitaron y el porcentaje correspondiente."));
        data.add(new Lista(R.mipmap.ic_reportes, "Efectividad de Ventas", "De los clientes visitados cuales tienen Venta, Cambio o Devolución."));
        data.add(new Lista(R.mipmap.ic_reportes, "Ventas por Producto", "De los productos que se traen, a que clientes se les vendieron.."));

        lstItems.setAdapter(new Adapter_Actualizar(context, data));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle("Reportes");
    }


    public void getViews(View v) {

        lstItems = (ListView) v.findViewById(R.id.lstOptions);
        lstItems.setOnItemClickListener(this);

        btnNext = (FloatingActionButton) v.findViewById(R.id.btnCarga);
        btnNext.setVisibility(FloatingActionButton.GONE);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            //Descargar Datos del Cliente
            case 0 : {
                FR_Devoluciones frag = new FR_Devoluciones();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fm);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Devoluciones").addToBackStack("Reportes").commit();
            }break;

            case 1 : {
                FR_Visitas frag = new FR_Visitas();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fm);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Visitas").addToBackStack("Reportes").commit();
            }break;

            case 2 : {
                FR_Efectividad frag = new FR_Efectividad();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fm);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Efectividad").addToBackStack("Reportes").commit();
            }break;

            case 3 : {
                FR_Ventas frag = new FR_Ventas();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fm);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Ventas").addToBackStack("Reportes").commit();
            }break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCarga : {

            }break;
        }
    }



}
