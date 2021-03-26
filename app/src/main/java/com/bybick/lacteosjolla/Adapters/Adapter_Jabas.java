package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectOUT.Det_Jabas;
import com.bybick.lacteosjolla.ObjectOUT.Jabas;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class Adapter_Jabas extends BaseAdapter {
    Context context;
    ArrayList<Det_Jabas> data;

    //Vistas
    TextView descripcion;
    TextView clave;
    TextView marca;
    TextView lbMarca;
    TextView familia;
    TextView lbFamilia;
    TextView cantidad;
    TextView lbCantidad;
    TextView ent_sal;
    RelativeLayout rlColor;

    public Adapter_Jabas(Context context, ArrayList<Det_Jabas> jabas) {
        this.context = context;
        this.data = jabas;
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
        Det_Jabas item = data.get(position);

        rlColor = (RelativeLayout) v.findViewById(R.id.rlColor);
        if (item.getEnt_sal().equals("Recoger")){
            rlColor.setBackground(ContextCompat.getDrawable(context, R.drawable.item_entrante));
        } else if (item.getEnt_sal().equals("Dejar")){
            rlColor.setBackground(ContextCompat.getDrawable(context, R.drawable.item_saliente));
        }

        //Obtener Vistas
        descripcion = (TextView) v.findViewById(R.id.txtDescripcion);
        clave = (TextView) v.findViewById(R.id.txtClave);
        marca = (TextView) v.findViewById(R.id.txtMarca);
        familia = (TextView) v.findViewById(R.id.txtFamila);
        cantidad = (TextView) v.findViewById(R.id.txtCantidad);
        lbCantidad = (TextView) v.findViewById(R.id.lbCantidad);
        lbMarca = (TextView) v.findViewById(R.id.lbMarca);
        lbFamilia = (TextView) v.findViewById(R.id.lbFamilia);
        ent_sal = (TextView) v.findViewById(R.id.txtEntSal);


        //Mostrar Valores
        descripcion.setText(item.getDescripcion());
        clave.setText(item.getId_producto());
        lbCantidad.setVisibility(View.VISIBLE);
        cantidad.setVisibility(View.VISIBLE);
        cantidad.setText(FormatNumber(item.getCantidad()));
        lbMarca.setVisibility(View.GONE);
        marca.setVisibility(View.GONE);
        lbFamilia.setVisibility(View.GONE);
        familia.setVisibility(View.GONE);
        ent_sal.setVisibility(View.VISIBLE);
        ent_sal.setText(item.getEnt_sal());

        return v;
    }

    public String FormatNumber(double number) {
        String result = "";

        DecimalFormatSymbols sim = new DecimalFormatSymbols();
        sim.setDecimalSeparator('.');
        sim.setGroupingSeparator(',');
        DecimalFormat fd = new DecimalFormat("###,##0.00", sim);

        result = fd.format(number);

        return result;
    }


}
