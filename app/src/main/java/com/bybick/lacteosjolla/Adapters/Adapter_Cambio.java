package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 12/07/2016.
 */
public class Adapter_Cambio extends BaseAdapter {
    Context context;
    ArrayList<Det_Cambio> data;

    TextView desc_in;
    TextView unidad_in;
    TextView cant_in;
    TextView desc_out;
    TextView unidad_out;
    TextView cant_out;
    TextView motivo;

    public Adapter_Cambio(Context context, ArrayList<Det_Cambio> data) {
        this.context = context;
        this.data = data;
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
            v = inf.inflate(R.layout.item_cambio, null);
        }

        desc_in = (TextView) v.findViewById(R.id.txtDescripcionIn);
        unidad_in = (TextView) v.findViewById(R.id.txtUnidadIn);
        cant_in = (TextView) v.findViewById(R.id.txtCantidadIn);
        desc_out = (TextView) v.findViewById(R.id.txtDescripcionOut);
        unidad_out = (TextView) v.findViewById(R.id.txtUnidadOut);
        cant_out = (TextView) v.findViewById(R.id.txtCantidadOut);
        motivo = (TextView) v.findViewById(R.id.txtMotivo);

        //Insertar Datos
        desc_in.setText(data.get(position).getNombre_recibido());
        unidad_in.setText("Unidad: " + data.get(position).getUnidad_in());
        cant_in.setText("Cantidad: " + data.get(position).getCantidad_in());

        desc_out.setText(data.get(position).getNombre_entregado());
        unidad_out.setText("Unidad: " + data.get(position).getUnidad_out());
        cant_out.setText("Cantidad: " + data.get(position).getCantidad_out());

        motivo.setText(data.get(position).getMotivo());

        return v;
    }
}
