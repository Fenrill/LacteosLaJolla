package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectIN.Lista;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 25/05/2016.
 */
public class Adapter_Actualizar extends BaseAdapter {
    Context context;
    ArrayList<Lista> data;
    TextView texto;
    ImageView img;
    TextView descripcion;

    public Adapter_Actualizar(Context context, ArrayList<Lista> data){
        this.context=context;
        this.data=data;
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
        if(convertView == null){
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.item_descarga, null);
        }
        texto = (TextView) convertView.findViewById(R.id.txtTitulo);
        img = (ImageView) convertView.findViewById(R.id.img_logo);
        descripcion = (TextView) convertView.findViewById(R.id.txtDescrip);

        texto.setText(data.get(position).getTitulo());
        img.setImageResource(data.get(position).getImagen());
        descripcion.setText(data.get(position).getDescripcion());

        return convertView;
    }
}
