package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 18/07/2016.
 */
public class Adapter_Ventas extends BaseAdapter {
    Context context;
    DBData dbd;
    private SparseBooleanArray mSelectedItemsIds;
    ArrayList<CargaView> data;

    //Vistas
    TextView descripcion;
    TextView clave;
    TextView marca;
    TextView percent;



    public Adapter_Ventas(Context context, ArrayList<CargaView> data) {
        this.context = context;
        this.data = data;

        dbd = new DBData(context);
        dbd.open();
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
    public View getView(final int position, View v, ViewGroup parent) {
        if(v == null){
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_ventas_report, null);
        }
        //Obtener Vistas

        clave = (TextView) v.findViewById(R.id.txtClave_producto);
        marca = (TextView) v.findViewById(R.id.txtMarca_producto);
        descripcion = (TextView) v.findViewById(R.id.txtDesc_producto);
        percent = (TextView) v.findViewById(R.id.txtPercent);

        //Mostrar Valores
        descripcion.setText(data.get(position).getDescripcion());
        clave.setText("Clave: " + data.get(position).getClave());
        marca.setText("Marca: " + data.get(position).getMarca());
        percent.setText(dbd.getVentasxProd(data.get(position).getClave()));


        return v;

    }


}

