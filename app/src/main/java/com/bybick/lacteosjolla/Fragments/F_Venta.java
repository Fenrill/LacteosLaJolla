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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Detalle;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsProducts.F_ProductosVenta;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Serie;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Forma_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.Printers.P_Carga;
import com.bybick.lacteosjolla.Printers.P_Venta;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bicktor on 09/06/2016.
 */
public class F_Venta extends Fragment implements View.OnClickListener,
        AdapterView.OnItemLongClickListener{

    Context context;
    Fragment fragmento;
    ActionBar tb;
    FragmentManager fmMain;

    FragmentManager fmChild;
    DBConfig dbc;
    DBData dbd;

    //DATA
    Visita visita;
    Cliente cliente;

    public static Venta venta;
    public static ArrayList<Det_Venta> detalles;
    ArrayList<Precios> precios;

    //Data Auxiliar
    //Totales Globales

    //Totales x Detalle
    public static double total;
    public static double subtotal;
    public static double impuestos;
    public static double inv_actualizado;

    boolean actualizando;

    public boolean flag;

    //Producto Seleccion
    Producto seleccion;

    //Vistas
    //Botones
    FloatingActionButton btnAdd;
    FloatingActionButton btnEnd;
    FloatingActionButton btnPrint;

    //Lista
    RelativeLayout vista;
    public static ListView lstVentas;
    ListView lstProductos;
    ArrayList<Producto> productos;
    ArrayList<Producto> busqueda;
    String MARCA;

    //Textos
    public static TextView txtSubTotal;
    public static TextView txtImpuestos;
    public static TextView txtTotal;

    Inventario inv_total;
    Inventario inv_ventas;
    Inventario inv_cambios;
    Inventario inventario;

    double inv;
    double vent;
    double camb;
    double total_inv;

    Det_Venta det_venta;


    //Dialogo
    EditText editSearch;
    AlertDialog alertDialog;

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

        productos = new ArrayList<>();
        busqueda = new ArrayList<>();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        fragmento = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_venta, container, false);
        getViews(v);

        return v;
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        //Enviar Titulo a la Barra de Titulo
        tb.setTitle("Venta");
        Log.e("TES ID VISITA","ID: " + visita.getId_visita());

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
                        vista.setVisibility(RelativeLayout.VISIBLE);

                        //Obtener productos dependiendo de la marca
                        productos = dbd.getProductos(items[which].toString());
                        MARCA = items[which].toString();

                        //Verificar la Existencia de Venta
                        venta = dbd.getVenta(visita.getId_visita(), MARCA);
                        if (venta != null) {

                            //Si no se ha enviado se muestra para Actualizar
                            if (venta.getEnviado() == 0) {
                                //Sacar Detalles
                                detalles = venta.getDet_venta();

                                //Estamos actualizando
                                actualizando = true;

                                //POder Reimprimir
//                                btnPrint.setVisibility(FloatingActionButton.VISIBLE);

                                //Reestablecer marca a la Venta
                                venta.setEmpresa(MARCA);

                                //Actualizar Lista
                                lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                                //Actualizar Totales
                                UpdateImportes();

                                //Se realizo Moviemnto
                                F_Visita.movimientos = true;


                            }
                            //Si ya se envio se crea una venta
                            else {
                                actualizando = false;

                                //Venta Nueva
                                venta = new Venta();

                                //Detalles Nuevos
                                detalles = new ArrayList<>();
                            }

                        } else {
                            actualizando = false;

                            //Venta Nueva
                            venta = new Venta();

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
        //Vista
        vista = (RelativeLayout) v.findViewById(R.id.Views);

        //Lista
        lstVentas = (ListView) v.findViewById(R.id.lstVentas);
        lstVentas.setOnItemLongClickListener(this);

        //Botones
        btnAdd = (FloatingActionButton) v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnPrint = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);
        btnEnd = (FloatingActionButton) v.findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(this);

        //Textos
        txtSubTotal = (TextView) v.findViewById(R.id.txtSubTotal);
        txtImpuestos = (TextView) v.findViewById(R.id.txtImpuestos);
        txtTotal = (TextView) v.findViewById(R.id.txtTotal);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd : {
                if (!flag){
                    //AddProduct();
                    F_ProductosVenta frag = new F_ProductosVenta();
                    frag.setContext(context);
                    frag.setFm(fmMain);
                    frag.setProductos(productos);
                    frag.setAuxiliares(cliente, visita);

                    FragmentTransaction ft = fmMain.beginTransaction();
                    ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                            R.animator.enter_up, R.animator.out_up);
                    ft.add(R.id.Container, frag, "Productos").addToBackStack("Venta").commit();
                }

            }break;

            case R.id.btnEnd : {
//                if(detalles.size() > 0) {
                    FinshDialog();
                    if(!flag){
                        btnAdd.setVisibility(View.GONE);
                    }

//                } else {
//                    Toast.makeText(context, "Agrega poductos a la venta", Toast.LENGTH_SHORT).show();
//                }
            }break;

//            case R.id.btnPrint : {
//                P_Venta print = new P_Venta(context, venta, cliente);
//
//                F_Ticket ticket = new F_Ticket();
//                ticket.setContext(context);
//                ticket.setTb(tb);
//                ticket.setTicket(print.imprimir());
//                ticket.setTitle("Venta");
//                ticket.setPrint(print.getBT());
//
//                FragmentTransaction ft = fmMain.beginTransaction();
//                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
//                        R.animator.enter_up, R.animator.out_up);
//                ft.replace(R.id.ContainerVisita, ticket).addToBackStack("Visita").commit();
//            }
//            break;
        }
    }

    //Para abrir el Menu
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (actualizando) {
            //Mandar abrir dialogo con el detalle seleccionado
            MenuDialog(detalles.get(position), position);
        }

        return false;
    }


    //Editar Cantidad
    public void EditDialog(final Det_Venta item, final int position) {
        //Marcar la seleccion
        for(Producto prod : productos) {
            if(item.getId_producto().equals(prod.getId_producto()))
                seleccion = prod;
        }
        //Crear Dialogo
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //Obtener el inflate para inflar la vista
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Crear la vista del dialogo
        View view = inflater.inflate(R.layout.d_cantidad, null);
        //Obtener Vistas
        //Cantidad
        final EditText editCantidad = (EditText) view.findViewById(R.id.editCantidad);
        if(seleccion.getDecimales() == 1)
            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //Unidad
        final Spinner spUnidad = (Spinner) view.findViewById(R.id.spUnidad);
        //VAriable del Precio seleccionado
        precios = dbd.getPrecios(seleccion.getId_producto(), cliente.getId_cliente());
        ArrayAdapter<Precios> adapter = new ArrayAdapter<Precios>(context, android.R.layout.simple_spinner_item, precios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adapter);

        //Precio
        final TextView txtPrecio = (TextView) view.findViewById(R.id.txtPrecio);

        //Importe
        final TextView txtImporte = (TextView) view.findViewById(R.id.txtImporte);


        //ÑLister de Spinner Unidad y del Edit Cantidad
        //agregar listener para seleccion de Unidades
        spUnidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Precios item = precios.get(position);
                txtPrecio.setText("" + item.getPrecio());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Listener Cantidad
        editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Precios price = (Precios) spUnidad.getSelectedItem();
                    double cantidad = Double.parseDouble(s.toString());

                    //Inventario
                    String inv_total = FormatNumber(total_inv - cantidad);
                    inv_actualizado = DecodeStringDecimal(inv_total);

                    //Total
                    String totalString = FormatNumber(price.getPrecio() * cantidad);
                    total = DecodeStringDecimal(totalString);

                    //Impuesto
                    String impuestoString = FormatNumber(price.getImpuestos() * cantidad);
                    impuestos = DecodeStringDecimal(impuestoString);

                    //Subtotal
                    String subString = FormatNumber(price.getSubprecio() * cantidad);
                    subtotal = DecodeStringDecimal(subString);

                    txtImporte.setText("$ " + totalString);

                } else {
                    txtImporte.setText("$ 00.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //variable de inventario seleccionado and hide button

        inv_total = dbd.getCargaUnidad(seleccion.getId_producto());
        inv_ventas = dbd.getVentaUnidad(seleccion.getId_producto());
        inv_cambios = dbd.getCambioUnidad(seleccion.getId_producto());

        inv = inv_total.getCantidad();
        vent = inv_ventas.getVentas_inventario();
        camb = inv_cambios.getCambios_inventario();

        total_inv =  inv - vent - camb;

        //Inventario
        final TextView txtInventario = (TextView) view.findViewById(R.id.txtInventario);

        txtInventario.setText("Inventario: " + total_inv);


        //Asignar Unidad
        for(int i = 0; i < precios.size(); i ++) {
            Precios precio = precios.get(i);

            if(precio.getId_unidad().equals(item.getId_unidad())){
                spUnidad.setSelection(i, true);
                break;
            }
        }

        //Asignar Cantidad
        editCantidad.setText((int) item.getCantidad() + "");
        editCantidad.selectAll();

        //Mostrar Dialogo
        alert.setView(view)
                .setTitle(seleccion.getDescripcion())
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Precios precio = (Precios) spUnidad.getSelectedItem();
                        if(editCantidad.getText().toString().isEmpty() || Double.valueOf(editCantidad.getText().toString())<=0){
                            Toast.makeText(context, "No se introdujo cantidad.", Toast.LENGTH_SHORT).show();
                        } else if(Double.valueOf(editCantidad.getText().toString())>total_inv){
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        } else if(seleccion.getDecimales() == 1) {
                            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        }else {

                            item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                            item.setUnidad(precio.getUnidad());
                            item.setId_unidad(precio.getId_unidad());

                            item.setSubtotal(subtotal);
                            item.setImpuestos(impuestos);
                            item.setTotal(total);


                            item.setConversion(precio.getConversion());
                            item.setPrecio(precio.getPrecio());

                            detalles.set(position, item);

                            //Actualizar LIsta
                            lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                            //Actualizar Totales
                            UpdateImportes();
                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

    }

    //Abrir Menu
    public void MenuDialog(final Det_Venta mod, final int pos) {
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
        final AlertDialog menu =builder.setView(view)
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
                lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                //Actualizar Totales
                UpdateImportes();

                //Ocultar dialogo
                menu.cancel();
            }
        });
        //Click Modificar
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDialog(mod, pos);

                //Cerrar menu
                menu.cancel();
            }
        });

    }

    //Terminar Venta
    public void FinshDialog() {
        //Mostrar Dialogo de opciones
        if(!actualizando){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View d = getActivity().getLayoutInflater().inflate(R.layout.d_finventa, null);

            //Controles
            final Spinner spFormas = (Spinner) d.findViewById(R.id.spFormas);
            final ArrayList<Forma_Venta> formas_venta = dbd.getFormas(cliente.getId_cliente());
            final Switch facturar = (Switch) d.findViewById(R.id.swFacturar);

            //Formas de Venta
            ArrayAdapter<Forma_Venta> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, formas_venta);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFormas.setAdapter(adapter);

            //Mostrar Dialogo
            builder.setView(d)
                    .setTitle("Forma de Venta")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("Terminar Venta", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String forma = formas_venta.get(spFormas.getSelectedItemPosition()).getForma();
                            int id_forma = formas_venta.get(spFormas.getSelectedItemPosition()).getId_forma();


                            Serie folios;
                            if (facturar.isChecked()) {
                                folios = dbd.getFolio(true, MARCA);
                            } else {
                                folios = dbd.getFolio(false, "");
                            }


//                            det_venta = dbd.getDet_Venta();

//                            inventario = dbd.getBtnConsulta(det_venta.getId_producto(), det_venta.getId_det_venta());

                            venta.setId_visita(visita.getId_visita());
                            venta.setId_cliente(cliente.getId_cliente());
                            venta.setEmpresa(MARCA);
                            venta.setForma_venta(forma);
                            venta.setId_forma_venta(id_forma);
                            venta.setSerie(folios.getSerie());
                            venta.setFolio(folios.getFolio());
                            venta.setDet_venta(detalles);
//                            if (inventario.getConsultBtn()!=null && !inventario.getConsultBtn().isEmpty()){
//                                //Enviar Venta

//
//                            } else{
//                                det_venta = dbd.updateCantidad(det_venta.getCantidad(), det_venta.getId_det_venta(), det_venta.getId_producto());
//                            }


                            if (flag){
                                venta.setDet_venta(detalles);
                                dbd.UpdateVenta(venta);

                            }else{
                                dbd.setVenta(venta);
                            }


//                            if (venta2 != null){
//                            } else{
//                                venta.setDet_venta(detalles);
//                                dbd.UpdateVenta(venta);
//                            }


//                            ArrayList<Det_Venta> det_venta2 = venta.getDet_venta();
//                            Det_Venta dventa;
//                            Venta venta2;
//                            venta2 = dbd.getVenta(visita.getId_cliente(), MARCA);
//                            String idDet;

//                            for (int i=0; i < det_venta2.size(); i++){
//                                dventa = det_venta2.get(i);
//                                det_venta = dbd.idVenta(dventa.getId_producto(), venta2.getId_venta());
//                                idDet = det_venta.getId_det_venta();
////                                dventa.setId_det_venta(idDet);
//                                if (idDet==null || idDet.isEmpty()){
//                                    dbd.setVenta(venta);
//                                } else{
//                                    venta.setDet_venta(detalles);
//                                    dbd.UpdateVenta(venta);
//                                }
//                            }
////                            if (venta.getEnviado() == 0){

//                            if(dventa.getId_det_venta()) {
//                                dbd.setVenta(venta);
//                            }

//                            venta.setDet_venta(detalles);
//                            dbd.UpdateVenta(venta);

                            if (detalles.size()>0){
                                P_Venta print = new P_Venta(context, venta, cliente);

                                //Mostrar Ticket
                                F_Ticket ticket = new F_Ticket();
                                ticket.setContext(context);
                                ticket.setTb(tb);
                                ticket.setTicket(print.imprimir());
                                ticket.setTitle("Venta");
                                ticket.setPrint(print.getBT());

                                //Option 2
                                FragmentTransaction ft = fmMain.beginTransaction();
                                ft.replace(R.id.ContainerVisita, ticket);

                                ft.addToBackStack("Venta");
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                ft.commit();
                            }

                            //Se realizo Moviemnto
                            F_Visita.movimientos = true;
                            flag = true;


                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            //Editando Venta
        }else{
            //Actualoizar Venta
            venta.setDet_venta(detalles);
            dbd.UpdateVenta(venta);

            //Imprimir Venta
            P_Venta print = new P_Venta(context, venta, cliente);

            F_Ticket ticket = new F_Ticket();
            ticket.setContext(context);
            ticket.setTb(tb);
            ticket.setTicket(print.imprimir());
            ticket.setTitle("Venta");
            ticket.setPrint(print.getBT());

            FragmentTransaction ft = fmMain.beginTransaction();
            ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                    R.animator.enter_up, R.animator.out_up);
            ft.add(R.id.Container, ticket).addToBackStack("Venta").commit();
        }

    }

    //Actualizar Importes Visuales y en la venta
    public void UpdateImportes() {
        double subtotal = 0;
        double impuestos = 0;
        double total = 0;

        for(int i = 0; i < detalles.size(); i ++) {
            Det_Venta item = detalles.get(i);
            subtotal += item.getSubtotal();

            impuestos += item.getImpuestos();

            total += item.getTotal();
        }

        //Actualizar Totales visuales
        txtSubTotal.setText(FormatNumber(subtotal));
        txtImpuestos.setText(FormatNumber(impuestos));
        txtTotal.setText(FormatNumber(total));

        //Actualizar Totales en la Venta
        venta.setSubtotal(subtotal);
        venta.setImpuestos(impuestos);
        venta.setTotal(total);
    }

    //Formatear Numero de double a String
    public String FormatNumber(double number) {
        String result = "";

        DecimalFormatSymbols sim = new DecimalFormatSymbols();
        sim.setDecimalSeparator('.');
        sim.setGroupingSeparator(',');
        DecimalFormat fd = new DecimalFormat("###,##0.00", sim);

        result = fd.format(number);

        return result;
    }

    //Formatear numero de String a double
    public double DecodeStringDecimal(String text) {
        Number n = null;
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        df.setDecimalFormatSymbols(symbols);

        try {
            n = df.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return n.doubleValue();
    }

}
