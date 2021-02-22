package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectOUT.Det_Jabas;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class Adapter_Detalle_Jabas extends BaseAdapter {
    Context context;
    ArrayList<Det_Jabas> data;

    TextView descripcion;
    TextView cantidad;
    TextView piezas;
    TextView unidad;
    TextView subtotal;
    TextView impuestos;
    TextView total;


    public Adapter_Detalle_Jabas(Context context, ArrayList<Det_Jabas> data) {
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
            v = inf.inflate(R.layout.item_detalle, null);
        }

        Det_Jabas item = data.get(position);

        //OBTENER VISTAS
        descripcion = (TextView) v.findViewById(R.id.txtDescripcion);
        cantidad = (TextView) v.findViewById(R.id.txtCantidad);
//        piezas = (TextView) v.findViewById(R.id.txtPiezas);
        unidad = (TextView) v.findViewById(R.id.txtUnidad);
//        subtotal = (TextView) v.findViewById(R.id.txtSubTotal);
//        impuestos = (TextView) v.findViewById(R.id.txtImpuestos);
        total = (TextView) v.findViewById(R.id.txtTotal);

//        descripcion.setText(item.getDescripcion());
        cantidad.setText(item.getCantidad() + "");
//        piezas.setText(item.getPiezaB() + "");
//        unidad.setText(item.getUnidad());
//        subtotal.setText(FormatNumber(item.getSubtotal()));
//        impuestos.setText(FormatNumber(item.getImpuestos()));
//        total.setText(FormatNumber(item.getTotal()));

        return  v;
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
