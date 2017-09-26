package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectView.Sobrantes;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 19/07/2016.
 */
public class Adapter_Sobrantes extends BaseAdapter {
    Context context;
    ArrayList<Sobrantes> data;

    TextView descripcion;
    TextView clave;
    TextView carga;
    TextView ventas;
    TextView cambios;
    TextView devoluciones;
    TextView inv_mal;
    TextView inv_buen;
    TextView inv_final;


    public Adapter_Sobrantes(Context context, ArrayList<Sobrantes> data) {
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
            v = inf.inflate(R.layout.item_sobrantes,null);
        }

        descripcion=(TextView) v.findViewById(R.id.txtDescripcion_sobrantes);
        clave=(TextView) v.findViewById(R.id.txtclave_sobrante);
        carga=(TextView) v.findViewById(R.id.txtcarga_sobrante);
        ventas=(TextView) v.findViewById(R.id.txtVentas_sobrante);
        cambios=(TextView) v.findViewById(R.id.txtCambio_sobrante);
        devoluciones=(TextView) v.findViewById(R.id.txtDev_sobrante);
        inv_mal=(TextView) v.findViewById(R.id.txtinvMal_sobrante);
        inv_buen=(TextView) v.findViewById(R.id.txtinvBuen_sobrante);
        inv_final=(TextView) v.findViewById(R.id.txtinvFinal_sobrante);

        descripcion.setText(data.get(position).getDescripcion());
        clave.setText(data.get(position).getId_producto());
        carga.setText("Carga: "+data.get(position).getCarga());
        ventas.setText("Ventas: "+data.get(position).getVentas());
        cambios.setText("Cambios: "+data.get(position).getCambios());
        devoluciones.setText("Devoluciones: "+data.get(position).getDevoluciones());
        inv_mal.setText("Mal estado: "+data.get(position).getInv_mal());
        inv_buen.setText("Buen estado: "+data.get(position).getInv_buen());
        inv_final.setText("Final: "+data.get(position).getInv_final());

        return v;
    }
}
