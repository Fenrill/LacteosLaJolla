package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectView.Result;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 20/07/2016.
 */
public class Adapter_Result extends BaseAdapter {
    Context context;
    ArrayList<Result> data;

    //Vistas
    TextView descripcion;
    TextView clave;
    TextView marca;
    TextView familia;
    FloatingActionButton info;

    public Adapter_Result(Context context, ArrayList<Result> data) {
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
            v = inf.inflate(R.layout.item_respuesta, null);
        }
        Result item = data.get(position);

        //Obtener Vistas
        descripcion = (TextView) v.findViewById(R.id.txtTipo);
        clave = (TextView) v.findViewById(R.id.txtOk);
        marca = (TextView) v.findViewById(R.id.txtFail);
        familia = (TextView) v.findViewById(R.id.txtAll);
        info = (FloatingActionButton) v.findViewById(R.id.btnInfo);


        //Mostrar Valores
        descripcion.setText(item.getNombre());
        clave.setText("" + item.getOk());
        marca.setText("" + item.getFail());
        familia.setText("" + item.getAll());

        if(item.getCode() == -123) {
            info.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BD3020")));
            info.setImageResource(R.mipmap.ic_cancel);
        } else {
            info.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1F8D2E")));
            info.setImageResource(R.mipmap.ic_done);
        }

        return v;
    }

}
