package com.bybick.lacteosjolla.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
 * Created by bicktor on 08/06/2016.
 */
public class F_Visita extends Fragment implements TabLayout.OnTabSelectedListener{
    Context context;
    ActionBar tb;
    FragmentManager fmMain;

    //Cliente Seleccionado
    Cliente cliente;

    //Visita Seleccionada o Visita Nueva
    Visita visita = null;

    //Comprobar si esta actualizando Visita
    boolean actulizando;
    public static boolean movimientos = false;

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

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setVisita(Visita visita) {
        this.visita = visita;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fmChild = getChildFragmentManager();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        //VISITA
        if(visita != null) {
            actulizando = true;
        } else {
            //Generar Inicio de Visita
            visita = new Visita();
            visita.setId_visita(Main.NEWID());
            visita.setId_jornada(dbd.getJornada(dbc.getlogin().getId_usuario()).getId_jornada());
            visita.setId_usuario(dbc.getlogin().getId_usuario());
            visita.setId_cliente(cliente.getId_cliente());
            visita.setFecha(Main.getFecha());
            visita.setHora_inicio(Main.getHora());
            visita.setGps_inicio(Main.getGPS());
            visita.setBateria_inicio(Main.batteryLevel(context));

            //Manadar el Inicio de la Visita
            //dbd.setVisita(visita);

            actulizando = false;
        }


        //Activas Menus
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_visita, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem exit = menu.add("Fin Visita");
        exit.setIcon(R.mipmap.ic_finish);
        exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        exit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (actulizando) {
                    //Mostrar Alerta
                    AlertDialog.Builder bul = new AlertDialog.Builder(context);
                    bul.setIcon(R.mipmap.ic_alert)
                            .setTitle("Actualizar Visita")
                            .setMessage("¿Estas seguro que deseas terminar la actualización de la visita? Los datos no guardados se perderán.")
                            .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    visita.setUlt_modificacion(Main.getFecha() + " a las " + Main.getHora());
                                    //actualizar Modificacion
                                    dbd.setModificacion(visita);
                                    //Regreasra a Visitas
                                    fmMain.popBackStack();
                                }
                            })
                            .setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();

                } else {
                    //Mostrar Alerta
                    AlertDialog.Builder bul = new AlertDialog.Builder(context);
                    bul.setIcon(R.mipmap.ic_alert)
                            .setTitle("Terminar Visita")
                            .setMessage("¿Estas seguro que deseas terminar la visita? Los datos no guardados se perderán.")
                            .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Terminar Visita
                                    visita.setHora_fin(Main.getHora());
                                    visita.setGps_fin(Main.getGPS());
                                    visita.setBateria_fin(Main.batteryLevel(context));

                                    //Guardar Visita
                                    dbd.setVisita(visita);

                                    //Regreasra a Visitas
                                    fmMain.popBackStack();
                                }
                            })
/*
                            .setNegativeButton("Omitir Visita", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Descartar la Visita y su Movimientos
                                    dbd.OmitirVisita(visita.getId_visita());
                                    Log.e("ID-Visita", visita.getId_visita());
                                    //Regresar A clientes
                                    F_Visitas.check = false;
                                    for(int i = 0; i < fmMain.getBackStackEntryCount(); i ++) {
                                        fmMain.popBackStack();
                                    }
                                }
                            })
*/
                            .setNeutralButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                }

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getViews(View v) {
        tabs = (TabLayout) v.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_venta).setText("Venta"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_cambio).setText("Cambio"), false);
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_devolucion).setText("Devolucion"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_cobranza).setText("Cobrar"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_noventa).setText("No Venta"), false);

        tabs.setOnTabSelectedListener(this);
        //Mostrar Ventas x Default
        tb.setTitle("Selecciona una opción");


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle("Selecciona una opción");

        tabs.removeAllTabs();
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_venta).setText("Venta"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_cambio).setText("Cambio"), false);
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_devolucion).setText("Devolucion"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_cobranza).setText("Cobrar"), false);
        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_noventa).setText("No Venta"), false);

        tabs.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            //Venta
            case 0 : {
                tb.setTitle("Venta");

                //Mostrar Ventas
                F_Venta frag = new F_Venta();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fmMain);
                frag.setAuxiliares(visita, cliente);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerVisita, frag, "Venta").commit();

            }break;

            //Cambio
            case 1 : {
                tb.setTitle("Cambio");

                //Mostrar Cambio
                F_Cambio frag = new F_Cambio();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fmMain);
                frag.setAuxiliares(visita, cliente);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);

                ft.replace(R.id.ContainerVisita, frag, "Cambio").commit();
            }break;

            //Devolucion
//            case 2 : {
//                tb.setTitle("Devolución");
//
//                //Mostrar Devolucion
//                F_Devolucion frag = new F_Devolucion();
//
//                frag.setContext(context);
//                frag.setTb(tb);
//                frag.setFmMain(fmMain);
//                frag.setAuxiliares(visita, cliente);
//
//                FragmentTransaction ft = fmChild.beginTransaction();
//                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
//                        R.animator.enter_anim, R.animator.out_anim);
//
////                ft.replace(R.id.ContainerVisita, frag, "Devolucion").commit();
//            }break;

            //Cobranza
            case 2 : {
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
            case 3 : {
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
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            //Venta
            case 0: {
                tb.setTitle("Venta");

                //Mostrar Ventas
                F_Venta frag = new F_Venta();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fmMain);
                frag.setAuxiliares(visita, cliente);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);
                ft.replace(R.id.ContainerVisita, frag, "Venta").commit();

            }
            break;

            //Cambio
            case 1: {
                tb.setTitle("Cambio");

                //Mostrar Cambio
                F_Cambio frag = new F_Cambio();

                frag.setContext(context);
                frag.setTb(tb);
                frag.setFmMain(fmMain);
                frag.setAuxiliares(visita, cliente);

                FragmentTransaction ft = fmChild.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                        R.animator.enter_anim, R.animator.out_anim);

                ft.replace(R.id.ContainerVisita, frag, "Cambio").commit();
            }
            break;

//            //Devolucion
//            case 2: {
//                tb.setTitle("Devolución");

//                //Mostrar Devolucion
//                F_Devolucion frag = new F_Devolucion();

//                frag.setContext(context);
//                frag.setTb(tb);
//                frag.setFmMain(fmMain);
//                frag.setAuxiliares(visita, cliente);

//                FragmentTransaction ft = fmChild.beginTransaction();
//                ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
//                        R.animator.enter_anim, R.animator.out_anim);

//                ft.replace(R.id.ContainerVisita, frag, "Devolucion").commit();
//            }
//            break;
        }
    }

    @Override
    public void onDestroyView() {
        //visita = null;
        super.onDestroyView();
    }

}
