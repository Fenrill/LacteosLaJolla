package com.bybick.lacteosjolla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 15/07/2016.
 */
public class Adapter_Pago extends BaseAdapter {
    Context context;
    ArrayList<Det_Pago> data;

    //Vistas
    TextView serie;
    TextView folio;
    TextView importe;
    TextView forma;
    TextView Banco;
    TextView Referencia;

    public Adapter_Pago(Context context, ArrayList<Det_Pago> data) {
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
            v = inf.inflate(R.layout.item_pago, null);
        }
        //Obtener Item
        Det_Pago item = data.get(position);

        //Obtener Vistas
        serie = (TextView) v.findViewById(R.id.txtSerie);
        importe = (TextView) v.findViewById(R.id.txtAbono);
        forma = (TextView) v.findViewById(R.id.txtForma);
        Banco = (TextView) v.findViewById(R.id.txtBancoEdit);
        Referencia = (TextView) v.findViewById(R.id.txtReferencia);

        //Enviar Valores
        serie.setText(item.getSerie() + " - " + item.getFolio());
        importe.setText("$ " + item.getImporte_pago());
        forma.setText(item.getForma_pago());
        Banco.setText(item.getBancos());
        Referencia.setText(item.getReferencia());

        return v;
    }
}
