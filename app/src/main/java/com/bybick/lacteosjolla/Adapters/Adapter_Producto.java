package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 16/06/2016.
 */
public class Adapter_Producto extends BaseAdapter {
    Context context;
    ArrayList<Producto> data;

    //Vistas
    TextView descripcion;
    TextView clave;
    TextView marca;
    TextView familia;

    public Adapter_Producto(Context context, ArrayList<Producto> data) {
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
            v = inf.inflate(R.layout.item_producto, null);
        }
        Producto item = data.get(position);

        //Obtener Vistas
        descripcion = (TextView) v.findViewById(R.id.txtDescripcion);
        clave = (TextView) v.findViewById(R.id.txtClave);
        marca = (TextView) v.findViewById(R.id.txtMarca);
        familia = (TextView) v.findViewById(R.id.txtFamila);


        //Mostrar Valores
        descripcion.setText(item.getDescripcion());
        clave.setText(item.getId_producto());
        marca.setText(item.getMarca());
        familia.setText(item.getFamilia());

        return v;
    }

}
