package com.bybick.lacteosjolla.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Cambio;
import com.bybick.lacteosjolla.Adapters.Adapter_Devolucion;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsProducts.F_CambioEntrante;
import com.bybick.lacteosjolla.FragmentsProducts.F_ProductosDevolucion;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Devolucion;
import com.bybick.lacteosjolla.ObjectOUT.Devolucion;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.Printers.P_Cambio;
import com.bybick.lacteosjolla.Printers.P_Devolucion;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class F_Devolucion extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener{
    Context context;
    Fragment fragmento;
    ActionBar tb;
    FragmentManager fmMain;
    FragmentManager fmChild;

    //Act
    boolean actualizando;

    //DATA
    Visita visita;
    Cliente cliente;

    //DataBases
    DBData dbd;
    DBConfig dbc;

    //Vistas
    RelativeLayout lytVista;
    public static ListView lstDevoluciones;

    FloatingActionButton btnAdd;
    FloatingActionButton btnEnd;
    FloatingActionButton btnPrint;

    //Productos
    ArrayList<Producto> productos;
    ArrayList<Producto> busqueda;


    String MARCA;

    //Motivo y Unidades
    ArrayList<Motivo> motivos;
    ArrayList<Unidad> unidades;

    //Generada
    Devolucion devolucion;
    public static ArrayList<Det_Devolucion> detalles;


    public void setContext(Context context) {
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFmMain(FragmentManager fmMain) {
        this.fmMain = fmMain;
    }

    public void setAuxiliares(Visita visita, Cliente cliente) {
        this.visita = visita;
        this.cliente = cliente;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fmChild = getChildFragmentManager();

        //Iniciar Listas
        productos = new ArrayList<>();
        busqueda = new ArrayList<>();

        //Data DB
        dbd = new DBData(context);
        dbd.open();

        //COnfig DB
        dbc = new DBConfig(context);
        dbc.open();

        fragmento = this;

        //Arrays
        motivos = dbd.getMotivosCambio();
        unidades = dbd.getUnidades();

        detalles = new ArrayList<>();
        devolucion = new Devolucion();

    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v =inflater.inflate(R.layout.f_devolucion, container, false);
//        getViews(v);
//        return v;
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tb.setTitle("Devoluciones");
        //Mostrar Dialogo de Seleccion de Empresa
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = new String[]{"LA JOLLA", "COBASUR"};
        builder.setTitle("Marca")
                .setIcon(R.mipmap.ic_launcher)
                .setItems(items, new DialogInterface.OnClickListener() {
                    //Al seleccionar una Marca
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Mostrar Vista
                        lytVista.setVisibility(RelativeLayout.VISIBLE);

                        //Obtener productos dependiendo de la marca
                        productos = dbd.getProductos(items[which].toString());
                        MARCA = items[which].toString();

                        //Verificar la Existencia de Cambio en esta Visita
                        devolucion = dbd.getDevolucion(visita.getId_visita(), MARCA);
                        if (devolucion != null) {

                            //Si no se ha enviado se muestra para Actualizar
                            if (devolucion.getEnviado() == 0) {
                                //Sacar Detalles
                                detalles = devolucion.getDetalles();

                                //Estamos actualizando
                                actualizando = true;

                                //Poder Reimprimir
                                btnPrint.setVisibility(FloatingActionButton.VISIBLE);

                                //Reestablecer marca al Cambio
                                devolucion.setEmpresa(MARCA);

                                //Mostrar los Cambios
                                lstDevoluciones.setAdapter(new Adapter_Devolucion(context, detalles));

                                //Se realizo Moviemnto
                                F_Visita.movimientos = true;

                            }
                            //Si ya se envio se crea un Cambio Nuevo
                            else if(devolucion.getEnviado() == 1) {
                                //No se esta Actualizando
                                actualizando = false;

                                //Cambio Nuevo
                                devolucion = new Devolucion();
                                devolucion.setEmpresa(MARCA);
                                devolucion.setId_cliente(cliente.getId_cliente());
                                devolucion.setId_visita(visita.getId_visita());
                                devolucion.setEnviado(0);

                                //Detalles Nuevos
                                detalles = new ArrayList<>();
                            }
                        } else {
                            //No se esta Actualizando
                            actualizando = false;

                            //Cambio Nuevo
                            devolucion = new Devolucion();
                            devolucion.setEmpresa(MARCA);
                            devolucion.setId_cliente(cliente.getId_cliente());
                            devolucion.setId_visita(visita.getId_visita());
                            devolucion.setEnviado(0);

                            //Detalles Nuevos
                            detalles = new ArrayList<>();
                        }
                    }
                })
                .create()
                .show();
        super.onViewCreated(view, savedInstanceState);
    }

    public void getViews(View v) {
        lytVista = (RelativeLayout) v.findViewById(R.id.lytVista);

        //Lista
        lstDevoluciones = (ListView) v.findViewById(R.id.lstDevoluciones);
        lstDevoluciones.setOnItemLongClickListener(this);

        //Botones
        btnAdd = (FloatingActionButton) v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnPrint = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);
        btnEnd = (FloatingActionButton) v.findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //boton Agregar
            case R.id.btnAdd : {
                //Abrir Productos
                F_ProductosDevolucion frag = new F_ProductosDevolucion();
                frag.setContext(context);
                frag.setFm(fmMain);
                frag.setAuxiliares(cliente, visita);
                frag.setProductos(productos);

                FragmentTransaction ft = fmMain.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.add(R.id.Container, frag, "Productos").addToBackStack("Devolucion").commit();
            }break;

            //Boton Terminar
            case R.id.btnEnd : {
                if(detalles.size() > 0) {
                    AlertDialog.Builder bul = new AlertDialog.Builder(context);
                    bul.setTitle("Terminar Devolucion")
                            .setIcon(R.mipmap.ic_alert)
                            .setMessage("¿Seguo de terminar el Movimiento?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Finish();
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
                } else {
                    Toast.makeText(context, "Agrega poductos a la Devolucion", Toast.LENGTH_SHORT).show();
                }
            }break;

            //Re-imprimir
            case R.id.btnPrint : {
                P_Devolucion print = new P_Devolucion(context, devolucion, cliente);

                F_Ticket ticket = new F_Ticket();
                ticket.setContext(context);
                ticket.setTb(tb);
                ticket.setTicket(print.imprimir());
                ticket.setTitle("Devolucion");
                ticket.setPrint(print.getBT());

                FragmentTransaction ft = fmMain.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.replace(R.id.ContainerVisita, ticket).addToBackStack("Visita").commit();
            }break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //Mandar abrir dialogo con el detalle seleccionado
        MenuDialog(detalles.get(position), position);

        return false;
    }

    //Abrir Menu
    public void MenuDialog(final Det_Devolucion mod, final int pos) {
        //Crear Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.d_menu, null);
        //Boton cancelar
        FloatingActionButton btnCancel = (FloatingActionButton) view.findViewById(R.id.btnHideMenu);
        //boto eliminar
        FloatingActionButton btnDelete = (FloatingActionButton) view.findViewById(R.id.btnDelete);
        //Boton modificar
        FloatingActionButton btnEdit = (FloatingActionButton) view.findViewById(R.id.btnEdit);

        //Asignar Viusta al dialogo
        final AlertDialog menu = builder.setView(view)
                .create();
        menu.show();

        //Click Canelar
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.cancel();
            }
        });
        //Click Eliminar
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Eliminar de la Lista
                detalles.remove(pos);

                //Actualizar Lista
                lstDevoluciones.setAdapter(new Adapter_Devolucion(context, detalles));

                //Ocultar dialogo
                menu.cancel();
            }
        });
        //Click Modificar
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProducto(mod, pos);

                //Cerrar menu
                menu.cancel();
            }
        });

    }

    //Editar Entrante
    public void EditProducto(final Det_Devolucion detalle, final int pos) {
        //Mostrar Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View d = inflater.inflate(R.layout.d_cambio_entrante,null);

        final TextView txtDescricpion = (TextView) d.findViewById(R.id.txtDesc);
        final EditText editCantidad = (EditText) d.findViewById(R.id.editCantidad);
        final Spinner spMotivos = (Spinner) d.findViewById(R.id.spMotivo);
        final Spinner spUnidadess = (Spinner) d.findViewById(R.id.spUnidad);

        txtDescricpion.setText(detalle.getNombre());
        editCantidad.setText("" + detalle.getCantidad());

        //Llenar Spinner de Unidad
        unidades = dbd.getUnidadesProducto(detalle.getId_producto());
        ArrayAdapter<Unidad> adapter_unidad = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                unidades);
        adapter_unidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadess.setAdapter(adapter_unidad);
        //Marcar el Seleccionado
        for(int i = 0; i < unidades.size(); i ++) {
            if(unidades.get(i).getId_unidad().equals(detalle.getId_unidad()))
                spUnidadess.setSelection(i);
        }

        //llenar Spinner de Motivo
        ArrayAdapter<Motivo> adapter_motivo = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                motivos);
        adapter_motivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivos.setAdapter(adapter_motivo);
        //marcar Seleccion
        for(int i = 0; i < motivos.size(); i ++) {
            if(motivos.get(i).getId_motivo() == detalle.getId_motivo())
                spMotivos.setSelection(i);
        }

        builder.setView(d)
                .setTitle("Producto a Devolver")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Agregar cantidad
                        if (editCantidad.getText().toString().isEmpty())
                            detalle.setCantidad(0);
                        else
                            detalle.setCantidad(Double.parseDouble(editCantidad.getText().toString()));

                        detalle.setId_motivo(motivos.get(spMotivos.getSelectedItemPosition()).getId_motivo());
                        detalle.setMotivo(motivos.get(spMotivos.getSelectedItemPosition()).getDescripcion());
                        detalle.setId_unidad(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
                        detalle.setUnidad(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
                        detalle.setIndexUnidad(spUnidadess.getSelectedItemPosition());
                        //Toast.makeText(context,"" + detalle.getIndex_in(),Toast.LENGTH_LONG).show();
                        detalle.setIndexMotivo(spMotivos.getSelectedItemPosition());

                        //Actualizar el Detalle en la Lista
                        detalles.set(pos, detalle);

                        //Actualizar lista
                        lstDevoluciones.setAdapter(new Adapter_Devolucion(context, detalles));

                        //Dialogo de Saliente
                        dialog.cancel();


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cerrar Dialogo
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    //Finalizar
    public void Finish() {
        if(!actualizando) {
            devolucion.setDetalles(detalles);

            //Guardar Cambio a la DB
            dbd.setDevolucion(devolucion);

            //Imprimir Cambio

            P_Devolucion print = new P_Devolucion(context, devolucion, cliente);

            //Mostrar Ticket
            F_Ticket ticket = new F_Ticket();
            ticket.setContext(context);
            ticket.setTb(tb);
            ticket.setTicket(print.imprimir());
            ticket.setTitle("Devolucion");
            ticket.setPrint(print.getBT());

            //Option 2
            FragmentTransaction ft = fmMain.beginTransaction();
            ft.replace(R.id.ContainerVisita, ticket);

            ft.addToBackStack("Devolucion");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            //Se realizo movimiento
            F_Visita.movimientos = true;

        } else {
            devolucion.setDetalles(detalles);

            //Actualizar Cambio
            dbd.UpdateDevolucion(devolucion);

            //Imprimir Cambio

            P_Devolucion print = new P_Devolucion(context, devolucion, cliente);

            F_Ticket ticket = new F_Ticket();
            ticket.setContext(context);
            ticket.setTb(tb);
            ticket.setTicket(print.imprimir());
            ticket.setTitle("Devolucion");
            ticket.setPrint(print.getBT());

            FragmentTransaction ft = fmMain.beginTransaction();
            ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                    R.animator.enter_up, R.animator.out_up);
            ft.add(R.id.Container, ticket).addToBackStack("Devolucion").commit();

        }
    }

}
