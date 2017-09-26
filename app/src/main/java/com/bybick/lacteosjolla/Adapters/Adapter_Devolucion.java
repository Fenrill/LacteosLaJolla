package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectOUT.Det_Devolucion;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class Adapter_Devolucion extends BaseAdapter {
    Context context;
    ArrayList<Det_Devolucion> data;
    TextView descripcion;
    TextView unidad;
    TextView cantidad;
    TextView motivo;
    TextView importe;

    public Adapter_Devolucion(Context context, ArrayList<Det_Devolucion> data) {
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
            v = inf.inflate(R.layout.item_devolucion, null);
        }
        //Vistas
        descripcion = (TextView) v.findViewById(R.id.txtDescripcion);
        unidad = (TextView) v.findViewById(R.id.txtUnidad);
        cantidad = (TextView) v.findViewById(R.id.txtCantidad);
        motivo = (TextView) v.findViewById(R.id.txtMotivo);
        importe = (TextView) v.findViewById(R.id.txtImporte);

        descripcion.setText(data.get(position).getNombre());
        unidad.setText(data.get(position).getUnidad());
        cantidad.setText("" + data.get(position).getCantidad());
        importe.setText("" + data.get(position).getImporte());
        motivo.setText(data.get(position).getMotivo());

        return v;
    }
}
