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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

/**
 * Created by bicktor on 15/07/2016.
 */
public class F_Config extends Fragment implements TabLayout.OnTabSelectedListener{
    Context context;
    ActionBar tb;
    FragmentManager fmMain;


    FragmentManager fmChild;
    DBData dbd;
    DBConfig dbc;

    TabLayout tabs;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFmMain(FragmentManager fmMain) {
        this.fmMain = fmMain;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fmChild = getChildFragmentManager();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        //Activas Menus
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_config, container, false);
        getViews(v);

        return v;
    }

    public void getViews(View v) {
        tabs = (TabLayout) v.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_actualizar).setText("Actualizar"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_reportes).setText("Reportes"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_enviar).setText("Enviar"), false);
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_cobranza).setText("Cobrar"), false);
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_noventa).setText("No Venta"), false);

        tabs.setOnTabSelectedListener(this);
        //Mostrar Ventas x Default
        tb.setTitle("Herramientas");


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle("Herramientas");

        tabs.removeAllTabs();
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_actualizar).setText("Actualizar"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_reportes).setText("Reportes"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_enviar).setText("Enviar"), false);
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_cobranza).setText("Cobrar"), false);
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_noventa).setText("No Venta"), false);

        tabs.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            //Venta
            case 0 : {
                tb.setTitle("Actualizar Listas");

                //Mostrar Ventas
                F_Actualizar frag = new F_Actualizar();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fmMain);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Actualizar").commit();

            }break;

            //Cambio
            case 1 : {
                tb.setTitle("Reportes");

                F_Reportes frag = new F_Reportes();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fmMain);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Reportes").commit();

            }break;

            //Devolucion
            case 2 : {
                tb.setTitle("Información");

                F_Enviar frag = new F_Enviar();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFm(fmMain);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerConfig, frag, "Reportes").commit();

            }break;
/*
            //Cobranza
            case 3 : {
                tb.setTitle("Cobranza");
                //Mostrar Devolucion
                F_Cobranza frag = new F_Cobranza();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fmMain);
                frag.setAuxiliares(visita, cliente);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);

                ft.replace(R.id.ContainerVisita, frag, "Cobranza").commit();
            }break;

            //No Venta
            case 4 : {
                tb.setTitle("No Venta");

                //Mostrar Ventas
                F_noVenta frag = new F_noVenta();
                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fmMain);
                frag.setAuxiliares(visita, cliente);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerVisita, frag, "NoVenta").commit();
            }break;
*/
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onDestroyView() {
        //visita = null;
        super.onDestroyView();
    }

}
