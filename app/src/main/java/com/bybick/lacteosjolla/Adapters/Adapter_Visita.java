package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 09/06/2016.
 */
public class Adapter_Visita  extends BaseAdapter {
    Context context;
    ArrayList<Visita> data;

    //Vistas
    TextView fecha;
    TextView inicio;
    TextView fin;
    TextView modificacion;

    public Adapter_Visita(Context context, ArrayList<Visita> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(convertView == null){
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_visita, null);
        }
        Visita item = data.get(position);

        fecha = (TextView) v.findViewById(R.id.txtFecha);
        inicio = (TextView) v.findViewById(R.id.txtHoraInicio);
        fin = (TextView) v.findViewById(R.id.txtHoraFin);
        modificacion = (TextView) v.findViewById(R.id.txtModificacion);

        fecha.setText(item.getFecha());
        inicio.setText(item.getHora_inicio());
        fin.setText(item.getHora_fin());
        if(!item.getUlt_modificacion().equals("n/a"))
            modificacion.setText(item.getUlt_modificacion());
        else
            modificacion.setText("Sin Modificar");

        return v;
    }
}
