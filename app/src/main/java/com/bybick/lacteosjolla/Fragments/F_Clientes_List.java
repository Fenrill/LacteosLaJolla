package com.bybick.lacteosjolla.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.bybick.lacteosjolla.Adapters.Adapter_Cliente;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 06/06/2016.
 */
public class F_Clientes_List extends Fragment {
    Context context;
    String tipo;

    FragmentManager fm;
    DBData dbd;
    DBConfig dbc;

    //Vistas
    EditText editSearch;
    ListView lstClientes;

    //Data
    ArrayList<Cliente> data;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_clientes_list, container, false);
        getViews(v);

        return v;
    }

    public void getViews(View v) {

        editSearch = (EditText) v.findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Buscar en la Lista
                if(count > 0) {
                    ArrayList<Cliente> data_search = new ArrayList<>();
                    for (Cliente item : data) {
                        if (item.getId_cliente().toLowerCase().indexOf(s.toString().toLowerCase()) != -1
                                || item.getNombre().toLowerCase().indexOf(s.toString().toLowerCase()) != -1
                                || s.toString().equalsIgnoreCase("")) {

                            data_search.add(item);
                        }
                    }
                    lstClientes.setAdapter(new Adapter_Cliente(context, data_search));
                } else {
                    lstClientes.setAdapter(new Adapter_Cliente(context, data));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lstClientes = (ListView) v.findViewById(R.id.lstClientes);

        Log.e("Tipo Clientes", tipo);
        data = dbd.getClientes(tipo);
        lstClientes.setAdapter(new Adapter_Cliente(context, data));

    }
}
