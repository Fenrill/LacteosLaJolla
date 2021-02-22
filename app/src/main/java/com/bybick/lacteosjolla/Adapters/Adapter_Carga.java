package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 * Created by bicktor on 04/06/2016.
 */
public class Adapter_Carga extends BaseAdapter {

    Context context;
    ArrayList<CargaView> data;

    //Vistas
    TextView descripcion;
    TextView clave;
    TextView marca;
    TextView cantidad;
    TextView piezas;
    TextView unidad;
    RelativeLayout back;


    public Adapter_Carga(Context context, ArrayList<CargaView> data) {
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
            v = inf.inflate(R.layout.item_carga, null);
        }
        clave = (TextView) v.findViewById(R.id.txtClave);
        marca = (TextView) v.findViewById(R.id.txtMarca);
        descripcion = (TextView) v.findViewById(R.id.txtDescripcion);
        cantidad = (TextView) v.findViewById(R.id.txtCantidad);
        piezas = (TextView) v.findViewById(R.id.txtPiezas);
        unidad = (TextView) v.findViewById(R.id.txtUnidad);
        back = (RelativeLayout) v.findViewById(R.id.lyBack);

        descripcion.setText(data.get(position).getDescripcion());
        clave.setText("Clave: " + data.get(position).getClave());
        marca.setText("Marca: " + data.get(position).getMarca());
        cantidad.setText("Cantidad: " + data.get(position).getCantidad());
        unidad.setText("Unidad: " + data.get(position).getUnidad());

        if(data.get(position).isReporte())
            back.setBackgroundResource(R.drawable.item_entrante);
        else
            back.setBackgroundResource(R.drawable.item_object);

        return v;

    }

}
