package com.bybick.lacteosjolla.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Clientes;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Formas;
import com.bybick.lacteosjolla.ObjectOUT.Forma_Venta;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 06/06/2016.
 */

public class Adapter_Cliente extends BaseAdapter {
    Context context;
    ArrayList<Cliente> data;
    F_Clientes f_clientes;
    ArrayList<Forma_Venta> formas;
    int filtro;

    ArrayList id_forma;

    //Vistas
    TextView nombre;
    TextView clave;
    TextView rfc;
    TextView razon_social;
    TextView forma_pago;
    RelativeLayout container;


    public Adapter_Cliente(Context context, ArrayList<Cliente> data, int filtro) {
        this.context = context;
        this.data = data;
        this.filtro=filtro;
    }

    public Adapter_Cliente(Context context, ArrayList<Cliente> data) {
        this.context = context;
        this.data = data;
        filtro =-1;
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

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_cliente, null);
        }
        Cliente item = data.get(position);

        //Obtener Vistas
        nombre = (TextView) v.findViewById(R.id.txtNombre);
        clave = (TextView) v.findViewById(R.id.txtClave);
        rfc = (TextView) v.findViewById(R.id.txtRfc);
        razon_social = (TextView) v.findViewById(R.id.txtRazonSocial);
        forma_pago = (TextView) v.findViewById(R.id.FormaPago);

        container = (RelativeLayout) v.findViewById(R.id.Container_DataClient);

        //Mostrar Valores
        nombre.setText(item.getNombre());
        clave.setText(item.getId_cliente());
        rfc.setText(item.getRfc());
        razon_social.setText(item.getRazon_social());
        forma_pago.setText(item.getForma_venta());

        if (item.isBloqueado()) {
            container.setBackgroundResource(R.drawable.item_saliente);
        }
        else if(item.isVisitado()) {
            container.setBackgroundResource(R.drawable.item_select);
        } else {
            container.setBackgroundResource(R.drawable.item_object);
        }

        return v;
    }

}
