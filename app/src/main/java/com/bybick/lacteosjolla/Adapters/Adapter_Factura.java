package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 15/07/2016.
 */
public class Adapter_Factura extends BaseAdapter {
    Context context;
    ArrayList<Factura> data;

    //Vistas
    TextView serie;
    TextView folio;
    TextView total;
    TextView saldo;
    TextView fecha;

    public Adapter_Factura(Context context, ArrayList<Factura> data) {
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
            v = inf.inflate(R.layout.item_factura, null);
        }
        //Item Actual
        Factura item = data.get(position);

        //Obtener Vistas
        serie = (TextView) v.findViewById(R.id.txtSerie);
        total = (TextView) v.findViewById(R.id.txtTotal);
        saldo = (TextView) v.findViewById(R.id.txtSaldo);
        fecha = (TextView) v.findViewById(R.id.txtFecha);

        //Enviar VAlores
        serie.setText(item.getSerie() + " - " + item.getFolio());
        fecha.setText(item.getFecha());
        total.setText("$ " + item.getTotal());
        saldo.setText("$ " + (item.getSaldo() - isAdd(item)));

        return v;
    }

    public double isAdd(Factura item) {
        DBData dbd;
        dbd = new DBData(context);
        dbd.open();
        double importe;

        importe = dbd.getImporteTotalFactura(item.getSerie(), item.getFolio());

        return importe;
    }


}
