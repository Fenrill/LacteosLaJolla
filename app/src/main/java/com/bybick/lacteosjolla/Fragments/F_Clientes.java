package com.bybick.lacteosjolla.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Cliente;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 04/06/2016.
 */
public class F_Clientes extends Fragment implements TabLayout.OnTabSelectedListener, AdapterView.OnItemClickListener{
    Context context;
    ActionBar tb;

    //Views
    TabLayout tabs;
    EditText editSearch;
    ListView lstClientes;

    //Data
    ArrayList<Cliente> data;
    ArrayList<Cliente> data_search;

    //Managers
    FragmentManager fm;
    DBData dbd;
    DBConfig dbc;

    //Menu
    MenuItem config;
    MenuItem liquid;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();

        dbd= new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_clientes, container, false);
        getViews(v);

        return v;
    }

    //Obtener Vistas
    public void getViews(View v) {
        if(!tb.isShowing())
            tb.show();
        tb.setTitle("Mis Clientes");

        editSearch = (EditText) v.findViewById(R.id.editSearch);
        //Listener del EditText
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Buscar en la Lista
                if (count > 0) {
                    data_search = new ArrayList<>();
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

        //Lista y Listener
        lstClientes = (ListView) v.findViewById(R.id.lstClientes);
        lstClientes.setOnItemClickListener(this);

        //Tabs
        tabs = (TabLayout) v.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Sin Eventos"));
        tabs.addTab(tabs.newTab().setText("Todos"));

        //Marcar la Primera Tab de Sin Eventos
        tabs.setOnTabSelectedListener(this);
        tabs.getTabAt(0).select();

        //Obtener Clientes si Eneventos
        data = dbd.getClientes("SE");
        lstClientes.setAdapter(new Adapter_Cliente(context, data));

/*
        //Mostrar Clientes
        F_Clientes_List list = new F_Clientes_List();
        list.setContext(context);
        list.setTb(tb);
        list.setFm(fm);
        list.setTipo("SE");

        getChildFragmentManager().beginTransaction()
                .replace(R.id.Container_Tab, list, "ListaSE").commit();
*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tb.setTitle("Mis Clientes");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        config = menu.add("Config");
        config.setIcon(R.mipmap.ic_config);
        config.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        config.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.d_password, null);
                final EditText editPass = (EditText) v.findViewById(R.id.editPass);
                builder.setTitle("Contraseña")
                        .setIcon(R.mipmap.ic_alert)
                        .setView(v)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Comprobar COntraseña para abrir Config
                                String pass = editPass.getText().toString();
                                if(dbc.isModule(pass, "herramienta")) {
                                    //Mostrar Config
                                    F_Config frag = new F_Config();

                                    frag.setContext(context);
                                    frag.setTb(tb);
                                    frag.setFmMain(fm);

                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                                            R.animator.enter_anim, R.animator.out_anim);
                                    ft.replace(R.id.Container, frag, "Config").addToBackStack("Clientes").commit();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();

                return false;
            }
        });
        liquid = menu.add("Liquidacion");
        liquid.setIcon(R.mipmap.ic_cobranza);
        liquid.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        liquid.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                F_Liquid frag = new F_Liquid();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fm);

                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.Container, frag, "Liquidacion").addToBackStack("Clientes").commit();

                return false;
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();

        switch (pos) {
            case 0 : {
                data = dbd.getClientes("SE");
                lstClientes.setAdapter(new Adapter_Cliente(context, data));

/*
                F_Clientes_List list = new F_Clientes_List();
                list.setContext(context);
                list.setTb(tb);
                list.setFm(fm);
                list.setTipo("SE");

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.Container_Tab, list, "ListaSE").commit();
*/
            }break;

            case 1 : {
                data = dbd.getClientes("ALL");
                lstClientes.setAdapter(new Adapter_Cliente(context, data));

/*
                F_Clientes_List list = new F_Clientes_List();
                list.setContext(context);
                list.setTb(tb);
                list.setFm(fm);
                list.setTipo("ALL");

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.Container_Tab, list, "ListaTodos").commit();
*/
            }break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cliente item = new Cliente();
        if(editSearch.getText().toString().isEmpty())
            item = data.get(position);
        else
            item = data_search.get(position);

        F_Visitas frag = new F_Visitas();
        frag.setContext(context);
        frag.setTb(tb);
        frag.setCliente(item);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                R.animator.enter_anim, R.animator.out_anim);
        ft.replace(R.id.Container, frag, "Visitas").addToBackStack("Clientes").commit();

    }
}
