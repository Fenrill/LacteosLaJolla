package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectView.Efectividad;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class Adapter_Efectividad extends BaseAdapter {
    Context context;
    ArrayList<Efectividad> data;
    int filtro;

    //Vistas
    TextView nombre;
    TextView clave;
    TextView rfc;
    TextView razon_social;

    RelativeLayout ventas;
    RelativeLayout cambios;
    RelativeLayout devs;

    public Adapter_Efectividad(Context context, ArrayList<Efectividad> data, int filtro) {
        this.context = context;
        this.data = data;
        this.filtro = filtro;
    }

    public Adapter_Efectividad(Context context, ArrayList<Efectividad> data) {
        this.context = context;
        this.data = data;
        filtro = -1;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if(v == null){
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_efectividad, null);
        }

        nombre = (TextView) v.findViewById(R.id.txtNombre);
        clave = (TextView) v.findViewById(R.id.txtClave);
        rfc = (TextView) v.findViewById(R.id.txtRfc);
        razon_social = (TextView) v.findViewById(R.id.txtRazonSocial);

        ventas = (RelativeLayout) v.findViewById(R.id.RVentas);
        cambios = (RelativeLayout) v.findViewById(R.id.RCambios);
        devs = (RelativeLayout) v.findViewById(R.id.RDevoluciones);

        nombre.setText(data.get(position).getNombre());
        clave.setText("Clave: " + data.get(position).getId_cliente());
        rfc.setText("RFC: " + data.get(position).getRfc());
        razon_social.setText("R.S.: " + data.get(position).getRazon_social());

        if(data.get(position).isVentas())
            ventas.setBackgroundResource(R.drawable.item_entrante);
        else
            ventas.setBackgroundResource(R.drawable.item_saliente);

        if(data.get(position).isCambios())
            cambios.setBackgroundResource(R.drawable.item_entrante);
        else
            cambios.setBackgroundResource(R.drawable.item_saliente);

        if(data.get(position).isDevoluciones())
            devs.setBackgroundResource(R.drawable.item_entrante);
        else
            devs.setBackgroundResource(R.drawable.item_saliente);


        return v;
    }
}
