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
import android.text.InputType;
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
import com.bybick.lacteosjolla.Adapters.Adapter_Detalle;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsProducts.F_CambioEntrante;
import com.bybick.lacteosjolla.FragmentsProducts.F_CambioEquivalente;
import com.bybick.lacteosjolla.ObjectIN.Carga;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Producto_unidad;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.Printers.P_Cambio;
import com.bybick.lacteosjolla.Printers.P_Venta;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;

/**
 * Created by bicktor on 05/07/2016.
 */
public class F_Cambio extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener{
    Context context;
    Fragment fragmento;
    ActionBar tb;
    FragmentManager fmMain;
    FragmentManager fmChild;

    //Act
    boolean actualizando;
    boolean flag;

    //DATA
    Visita visita;
    Cliente cliente;

    //DataBases
    DBData dbd;
    DBConfig dbc;

    //Vistas
    RelativeLayout lytVista;
    public static ListView lstCambios;

    FloatingActionButton btnAdd;
    FloatingActionButton btnEnd;
    FloatingActionButton btnPrint;

    //Productos
    ArrayList<Producto> productos;
    ArrayList<Producto> equivalentes;
    ArrayList<Producto> busqueda;

    //Inventario Total
    Inventario inv_total;
    Inventario inv_ventas;
    Inventario inv_cambios;
    Inventario inventario;

    double inv;
    double vent;
    double camb;
    double total;

    Producto producto;

    String MARCA;

    //Motivo y Unidades
    ArrayList<Motivo> motivos;
    ArrayList<Unidad> unidades;

    //Generada
    Cambio cambio;
    public static ArrayList<Det_Cambio> detalles;

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
        equivalentes = new ArrayList<>();
        busqueda = new ArrayList<>();

        //Data DB
        dbd = new DBData(context);
        dbd.open();

        //Config DB
        dbc = new DBConfig(context);
        dbc.open();

        fragmento = this;

        //Arrays
        motivos = dbd.getMotivosCambio();
        unidades = dbd.getUnidades();

        detalles = new ArrayList<>();
        cambio = new Cambio();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.f_cambio, container, false);
        getViews(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tb.setTitle("Cambios");
        //Mostrar Dialogo de Seleccion de Empresa
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = new String[]{"LA JOLLA", "COBASUR"};
//        builder.setTitle("Marca")
//                .setIcon(R.mipmap.ic_launcher)
//                .setItems(items, new DialogInterface.OnClickListener() {
//                    //Al seleccionar una Marca
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
        //Mostrar Vista
        lytVista.setVisibility(RelativeLayout.VISIBLE);

        //Obtener productos dependiendo de la marca
        MARCA = "LA JOLLA";
        productos = dbd.getProductos(MARCA);

        //Verificar la Existencia de Cambio en esta Visita
        cambio = dbd.getCambio(visita.getId_visita(), MARCA);
        if (cambio != null) {

            //Si no se ha enviado se muestra para Actualizar
            if (cambio.getEnviado() == 0) {
                //Sacar Detalles
                detalles = cambio.getDetalles();

                //Estamos actualizando
                actualizando = true;

//                                //Poder Reimprimir
//                                btnPrint.setVisibility(FloatingActionButton.VISIBLE);

                //Reestablecer marca al Cambio
                cambio.setEmpresa(MARCA);

                //Mostrar los Cambios
                lstCambios.setAdapter(new Adapter_Cambio(context, detalles));

                //Se realizo Moviemnto
                F_Visita.movimientos = true;

            }
            //Si ya se envio se crea un Cambio Nuevo
            else if(cambio.getEnviado() == 1) {
                //No se esta Actualizando
                actualizando = false;

                //Cambio Nuevo
                cambio = new Cambio();
                cambio.setEmpresa(MARCA);
                cambio.setId_cliente(cliente.getId_cliente());
                cambio.setId_visita(visita.getId_visita());
                cambio.setEnviado(0);

                //Detalles Nuevos
                detalles = new ArrayList<>();
            }
        } else {
            //No se esta Actualizando
            actualizando = false;

            //Cambio Nuevo
            cambio = new Cambio();
            cambio.setEmpresa(MARCA);
            cambio.setId_cliente(cliente.getId_cliente());
            cambio.setId_visita(visita.getId_visita());
            cambio.setEnviado(0);

            //Detalles Nuevos
            detalles = new ArrayList<>();
        }
    }
//                })
//                .create()
//                .show();
//        super.onViewCreated(view, savedInstanceState);
//    }

    public void getViews(View v) {
        lytVista = (RelativeLayout) v.findViewById(R.id.lytVista);

        //Lista
        lstCambios = (ListView) v.findViewById(R.id.lstCambios);
        lstCambios.setOnItemLongClickListener(this);

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
                F_CambioEntrante frag = new F_CambioEntrante();
                frag.setContext(context);
                frag.setFm(fmMain);
                frag.setAuxiliares(cliente, visita);
                frag.setProductos(productos);

                FragmentTransaction ft = fmMain.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.add(R.id.Container, frag, "Productos").addToBackStack("Cambio").commit();
            }break;

            //Boton Terminar
            case R.id.btnEnd : {
                if(detalles.size() > 0) {
                    AlertDialog.Builder bul = new AlertDialog.Builder(context);
                    bul.setTitle("Terminar Cambio")
                            .setIcon(R.mipmap.ic_alert)
                            .setMessage("¿Seguro de terminar el Movimiento?")
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
                    if(!flag){
                        btnAdd.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(context, "Agrega poductos al Cambio", Toast.LENGTH_SHORT).show();
                }

            }break;

//            //Boton Re-Imprimir
//            case R.id.btnPrint : {
//                P_Cambio print = new P_Cambio(context, cambio, cliente);

//                F_Ticket ticket = new F_Ticket();
//                ticket.setContext(context);
//                ticket.setTb(tb);
//                ticket.setTicket(print.imprimir());
//                ticket.setTitle("Cambio");
//                ticket.setPrint(print.getBT());

//                FragmentTransaction ft = fmMain.beginTransaction();
//                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
//                        R.animator.enter_up, R.animator.out_up);
//                ft.replace(R.id.ContainerVisita, ticket).addToBackStack("Visita").commit();
//            }break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (!flag) {
            //Mandar abrir dialogo con el detalle seleccionado
            MenuDialog(detalles.get(position), position);
        }

        return false;
    }

    //Abrir Menu
    public void MenuDialog(final Det_Cambio mod, final int pos) {
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
                lstCambios.setAdapter(new Adapter_Cambio(context, detalles));

                //Ocultar dialogo
                menu.cancel();
            }
        });
        //Click Modificar
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEntrante(mod, pos);

                //Cerrar menu
                menu.cancel();
            }
        });

    }

    //Editar Entrante
    public void EditEntrante(final Det_Cambio detalle, final int pos) {
        //Mostrar Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View d = inflater.inflate(R.layout.d_cambio_entrante,null);

        //Llenar textView de Inventario
        inv_total = dbd.getCargaUnidad(detalle.getId_producto_entregado());
        inv_ventas = dbd.getVentaUnidad(detalle.getId_producto_entregado());
        inv_cambios = dbd.getCambioUnidad(detalle.getId_producto_entregado());

        inv = inv_total.getCantidad();
        vent = inv_ventas.getVentas_inventario();
        camb = inv_cambios.getCambios_inventario();

        total =  inv - vent - camb;

        final TextView txtDescricpion = (TextView) d.findViewById(R.id.txtDesc);
        final EditText editCantidad = (EditText) d.findViewById(R.id.editCantidad);
        final Spinner spMotivos = (Spinner) d.findViewById(R.id.spMotivo);
        final Spinner spUnidadess = (Spinner) d.findViewById(R.id.spUnidad);
        final TextView tInventario = (TextView) d.findViewById(R.id.totalInv);

        txtDescricpion.setText(detalle.getNombre_recibido());

        editCantidad.setText("" + (int) detalle.getCantidad_in());

        //Inventario
        tInventario.setText("Inventario: " + total);

        //Llenar Spinner de Unidad
        unidades = dbd.getUnidadesProducto(detalle.getId_producto_recibido());
        ArrayAdapter<Unidad> adapter_unidad = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                unidades);
        adapter_unidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadess.setAdapter(adapter_unidad);

        //Marcar el Seleccionado
        for(int i = 0; i < unidades.size(); i ++) {
            if(unidades.get(i).getId_unidad().equals(detalle.getId_unidad_in()))
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
            Log.e("ID-ACtual", "" + motivos.get(i).getId_motivo());
            Log.e("ID-Save", "" + detalle.getId_motivo());
            if(motivos.get(i).getId_motivo() == detalle.getId_motivo())
                spMotivos.setSelection(i);
        }

        builder.setView(d)
                .setTitle("Producto Entrante")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Agregar cantidad
                        if (editCantidad.getText().toString().isEmpty()) {
                            detalle.setCantidad_in(0);
                        } else if(Double.valueOf(editCantidad.getText().toString())>total) {
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        } else {
                            detalle.setCantidad_in(Double.parseDouble(editCantidad.getText().toString()));
                            detalle.setCantidad_out(Double.parseDouble(editCantidad.getText().toString()));
                        }

                        detalle.setId_motivo(motivos.get(spMotivos.getSelectedItemPosition()).getId_motivo());
                        detalle.setMotivo(motivos.get(spMotivos.getSelectedItemPosition()).getDescripcion());
                        detalle.setId_unidad_in(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
                        detalle.setUnidad_in(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
                        detalle.setIndex_in(spUnidadess.getSelectedItemPosition());
                        //Toast.makeText(context,"" + detalle.getIndex_in(),Toast.LENGTH_LONG).show();
                        detalle.setIdex_motivo(spMotivos.getSelectedItemPosition());

                        //Actualizar el Detalle en la Lista
                        detalles.set(pos, detalle);

                        //Actualizar lista
                        lstCambios.setAdapter(new Adapter_Cambio(context, detalles));

                        //Dialogo de Saliente
//                        EditSaliente(detalle, pos);



                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //cerrar dialogo
                        dialog.cancel();
                    }
                })
//                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                //                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Agregar cantidad
//                        if (editCantidad.getText().toString().isEmpty())
//                            detalle.setCantidad_in(0);
//                        else
//                            detalle.setCantidad_in(Double.parseDouble(editCantidad.getText().toString()));
//
//                        detalle.setId_motivo(motivos.get(spMotivos.getSelectedItemPosition()).getId_motivo());
//                        detalle.setMotivo(motivos.get(spMotivos.getSelectedItemPosition()).getDescripcion());
//                        detalle.setId_unidad_in(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
//                        detalle.setUnidad_in(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
//                        detalle.setIndex_in(spUnidadess.getSelectedItemPosition());
//                        detalle.setIdex_motivo(spMotivos.getSelectedItemPosition());
//
//                        //Actualizar el Detalle en la Lista
//                        detalles.set(pos, detalle);
//
//                        //Actualizar lista
//                        lstCambios.setAdapter(new Adapter_Cambio(context, detalles));
//
//                        //Cerrar Dialogo
//                        dialog.cancel();
//
//                        if (editCantidad.getText().toString().isEmpty())
//                            detalle.setCantidad_out(0);
//                        else
//                            detalle.setCantidad_out(Double.parseDouble(editCantidad.getText().toString()));
//
//                        detalle.setId_unidad_out(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
//                        detalle.setUnidad_out(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
//                        detalle.setIndex_out(spUnidadess.getSelectedItemPosition());
//                        //Toast.makeText(context,"" + detalle.getIndex_in(),Toast.LENGTH_LONG).show();
//
//                        //Actualizar Lista
//                        detalles.set(pos, detalle);
//                        lstCambios.setAdapter(new Adapter_Cambio(context, detalles));
//
//                    }

//                })
                .create()
                .show();
    }

//    //Editar Saliente
//    //Dialogo de la Cantidad y Motivo
//    public void EditSaliente(final Det_Cambio detalle, final int pos) {
//        //Mostrar Dialogo
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View d = inflater.inflate(R.layout.d_cambio_entrante, null);
//        final TextView txtDescricpion = (TextView) d.findViewById(R.id.txtDesc);
//        final EditText editCantidad = (EditText) d.findViewById(R.id.editCantidad);
//        final Spinner spMotivos = (Spinner) d.findViewById(R.id.spMotivo);
//        final Spinner spUnidadess = (Spinner) d.findViewById(R.id.spUnidad);
//
//        spMotivos.setVisibility(Spinner.INVISIBLE);
//
//        txtDescricpion.setText(detalle.getNombre_entregado());
//        editCantidad.setText("" + detalle.getCantidad_out());
//
//        //Llenar Spinner de Unidad
//        unidades = dbd.getUnidadesProducto(detalle.getId_producto_entregado());
//        ArrayAdapter<Unidad> adapter_unidad = new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_spinner_item,
//                unidades);
//        adapter_unidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spUnidadess.setAdapter(adapter_unidad);
//        //Marcar el Seleccionado
//        for(int i = 0; i < unidades.size(); i ++) {
//            if(unidades.get(i).getId_unidad().equals(detalle.getId_unidad_out()))
//                spUnidadess.setSelection(i);
//        }
//
///*
//        //llenar Spinner de Motivo
//        ArrayAdapter<Motivo> adapter_motivo=new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_spinner_item,
//                motivos);
//        adapter_motivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spMotivos.setAdapter(adapter_motivo);
//*/
//        builder.setView(d)
//                .setTitle("Producto Saliente")
//                .setIcon(R.mipmap.ic_launcher)
//                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //detalle = new Det_Cambio();
//                        //Agregar cantidad
//                        if (editCantidad.getText().toString().isEmpty())
//                            detalle.setCantidad_out(0);
//                        else
//                            detalle.setCantidad_out(Double.parseDouble(editCantidad.getText().toString()));
//
//                        detalle.setId_unidad_out(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
//                        detalle.setUnidad_out(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
//                        detalle.setIndex_out(spUnidadess.getSelectedItemPosition());
//                        //Toast.makeText(context,"" + detalle.getIndex_in(),Toast.LENGTH_LONG).show();
//
//                        //Actualizar Lista
//                        detalles.set(pos, detalle);
//                        lstCambios.setAdapter(new Adapter_Cambio(context, detalles));
//
//                    }
//                })
//                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
//                .create()
//                .show();
//    }

    //Finalizar
    public void Finish() {
        if(!actualizando) {
            cambio.setDetalles(detalles);

            if (flag){

//                cambio.setDetalles(detalles);

                //Actualizar Cambio
                dbd.UpdateCambio(cambio);

            }
            else{

                //Guardar Cambio a la DB
                dbd.setCambio(cambio);
                //Imprimir Cambio
                P_Cambio print = new P_Cambio(context, cambio, cliente);

                //Mostrar Ticket
                F_Ticket ticket = new F_Ticket();
                ticket.setContext(context);
                ticket.setTb(tb);
                ticket.setTicket(print.imprimir());
                ticket.setTitle("Cambio");
                ticket.setPrint(print.getBT());

                //Option 2
                FragmentTransaction ft = fmMain.beginTransaction();
                ft.replace(R.id.ContainerVisita, ticket);

                ft.addToBackStack("Cambio");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                //Se realizo movimiento
                F_Visita.movimientos = true;

                flag = true;

            }

        } else {
            cambio.setDetalles(detalles);

            //Actualizar Cambio
            dbd.UpdateCambio(cambio);

            //Imprimir Cambio
            P_Cambio print = new P_Cambio(context, cambio, cliente);

            F_Ticket ticket = new F_Ticket();
            ticket.setContext(context);
            ticket.setTb(tb);
            ticket.setTicket(print.imprimir());
            ticket.setTitle("Cambio");
            ticket.setPrint(print.getBT());

            FragmentTransaction ft = fmMain.beginTransaction();
            ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                    R.animator.enter_up, R.animator.out_up);
            ft.add(R.id.Container, ticket).addToBackStack("Cambio").commit();
        }
    }
}
