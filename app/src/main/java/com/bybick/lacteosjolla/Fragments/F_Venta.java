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
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
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
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Cliente_Promos;
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Promociones;
import com.bybick.lacteosjolla.ObjectIN.Serie;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Forma_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Pago;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.Printers.P_Carga;
import com.bybick.lacteosjolla.Printers.P_Venta;
import com.bybick.lacteosjolla.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    boolean promo2 = false;

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

    boolean enPromocion;

    public boolean flag;

    //Producto Seleccion
    Producto seleccion;
    Pago tipoPago;
    Det_Pago tipoDetPago;

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
    ArrayList<Precios> conversionItem2;
    String MARCA;

    //Textos
    public static TextView txtSubTotal;
    public static TextView txtImpuestos;
    public static TextView txtTotal;

    Inventario inv_total;
    Inventario inv_ventas;
    Inventario inv_cambios;
    Inventario inventario;
    Inventario piezasInv;

    int piezascont;
    double inv;
    double vent;
    double camb;
    double invDetalles;
    double total_inv;

    Det_Venta det_venta;

    //promociones
    ArrayList<Promociones> promociones;
    ArrayList<Cliente_Promos> clientes_promos;
    String promoCliente;
    double cantidadVenta;
    double cantidadEntrega;
    String promoProductoVenta;
    String promoProductoEntrega;

    //GPS
    String location;


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
//        builder.setTitle("Marca")
//                .setIcon(R.mipmap.ic_launcher)
//                .setItems(items, new DialogInterface.OnClickListener() {
//                    //Al seleccionar una Marca
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
        //Mostrar Vista
        vista.setVisibility(RelativeLayout.VISIBLE);

        //Obtener productos dependiendo de la marca
        MARCA = "LA JOLLA";
        productos = dbd.getProductos(MARCA);

        //obtener las promociones y comprar si esta el cliente
        promociones = dbd.getPromociones();
        clientes_promos = new ArrayList<>();

        Cliente_Promos item =  new Cliente_Promos();
//        int i=0;
//        if (promociones.size()>0){
//            do{
//                enPromocion = true;
//
//                item.setPromoCliente(promociones.get(i).getId_cliente());
//                item.setCantidadVenta(promociones.get(i).getCantidad_venta());
//                item.setCantidadEntrega(promociones.get(i).getCantidad_entrega());
//                item.setPromoProductoVenta(promociones.get(i).getProducto_venta());
//                item.setPromoProductoEntrega(promociones.get(i).getProducto_entrega());
//
//                clientes_promos.add(item);
//                i++;
//            } while (i<=promociones.size());
//        }

        for (Promociones promo : promociones){
            if(promo.getId_cliente().equals(visita.getId_cliente())){
                item =  new Cliente_Promos();

                enPromocion = true;

                item.setPromoCliente(promo.getId_cliente());
                item.setCantidadVenta(promo.getCantidad_venta());
                item.setCantidadEntrega(promo.getCantidad_entrega());
                item.setPromoProductoVenta(promo.getProducto_venta());
                item.setPromoProductoEntrega(promo.getProducto_entrega());

                clientes_promos.add(item);
//                promoCliente = promo.getId_cliente();
//                cantidadVenta = promo.getCantidad_venta();
//                cantidadEntrega = promo.getCantidad_entrega();
//                promoProductoVenta = promo.getProducto_venta();
//                promoProductoEntrega = promo.getProducto_entrega();
            } else{}
        }

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

            if (enPromocion) {
                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
//
//                for (:
//                     ) {
//
//                }
                for (Cliente_Promos i: clientes_promos
                     ) {
                    build.setIcon(R.mipmap.ic_alert).setTitle("Promocion")
                            .setMessage("" + dbd.getProductoPromo(i.getPromoProductoVenta()) + " esta en oferta, al comprar  " + (int) i.getCantidadVenta() + " " + dbd.getProductoPromo(i.getPromoProductoVenta()) + " se dan " + (int) i.getCantidadEntrega() + " " + dbd.getProductoPromo(i.getPromoProductoEntrega()))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
    }
//                }
//                .create()
//                .show();
//        super.onViewCreated(view, savedInstanceState);
//    }

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
                    frag.setAuxiliares(cliente, visita, enPromocion);
                    if (enPromocion){
                        frag.setPromocion(clientes_promos);
                    }

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
        //two digits
        editCantidad.setFilters(new InputFilter[]{ new DecimalDigitsInputFilter(6,2) });
        final EditText editCantidad2 = (EditText) view.findViewById(R.id.editCantidad2);
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

//                    //Inventario
//                    String inv_total = FormatNumber(total_inv - cantidad);
//                    inv_actualizado = DecodeStringDecimal(inv_total);
//
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

//        inv_total = dbd.getCargaUnidad(seleccion.getId_producto());
//        inv_ventas = dbd.getVentaUnidad(seleccion.getId_producto());
//        inv_cambios = dbd.getCambioUnidad(seleccion.getId_producto());
//
//        inv = inv_total.getCantidad();
//        vent = inv_ventas.getVentas_inventario();
//        camb = inv_cambios.getCambios_inventario();
//
//        total_inv =  inv - vent - camb;

        //Inventario
        final TextView txtInventario = (TextView) view.findViewById(R.id.txtNumCant);

        txtInventario.setText("" + totalInventario(seleccion.getId_producto()));

        //variables piezas

        piezasInv = dbd.getCargaUnidadPieza(seleccion.getId_producto());
        final Inventario piezas_total = dbd.getVentaPiezas(seleccion.getId_producto());

        piezascont = piezasInv.getPiezas();
        final int piezasVenta = piezas_total.getVentas_piezas();
        final int piezasTotal = piezascont - piezasVenta;

        //Inventario
        final TextView txtPiezas = (TextView) view.findViewById(R.id.txtNumPiezas);

        txtPiezas.setText("" + totalInventario(seleccion.getId_producto()));

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
                        //Promociones Section
                        final Cliente_Promos cpromos = new Cliente_Promos();
                        for (int i = 0;i < clientes_promos.size(); i++){
                            if (seleccion.getId_producto().equals(clientes_promos.get(i).getPromoProductoVenta())) {
                                cpromos.setPromoCliente(clientes_promos.get(i).getPromoCliente());
                                cpromos.setCantidadVenta(clientes_promos.get(i).getCantidadVenta());
                                cpromos.setCantidadEntrega(clientes_promos.get(i).getCantidadEntrega());
                                cpromos.setPromoProductoVenta(clientes_promos.get(i).getPromoProductoVenta());
                                cpromos.setPromoProductoEntrega(clientes_promos.get(i).getPromoProductoEntrega());
                            }
                        }

                        //cantidad_res
                        double num1 = Double.parseDouble(editCantidad.getText().toString())/cpromos.getCantidadVenta();
                        int num2 = (int)num1;
                        conversionItem2 = dbd.getPrecios(cpromos.getPromoProductoEntrega(), cliente.getId_cliente());

                        double promocionInventario = totalInventario(cpromos.getPromoProductoEntrega());


                        //promociones
                        if (enPromocion){
                            if (item.getId_producto().equals(cpromos.getPromoProductoVenta())){
                                if (cpromos.getCantidadVenta() <= item.getCantidad()){
                                    int residue = (int)Double.parseDouble(editCantidad.getText().toString()) / (int)cpromos.getCantidadVenta();
                                    item.setCantidadResult(residue);
                                }
                            }
                        } else { item.setCantidadResult(0); }

                        if(editCantidad.getText().toString().isEmpty()
                                || Double.valueOf(editCantidad.getText().toString())<0){
                            Toast.makeText(context, "No se introdujo cantidad.", Toast.LENGTH_SHORT).show();
                        } else if (seleccion.getDecimales() == 1){
                            //justificacion para las piezas arreglar luego por codigo duplicado-
                            if (editCantidad2.getText().toString().isEmpty()
                                    || Double.valueOf(editCantidad2.getText().toString())<0) {
                                Toast.makeText(context, "No se introdujeron piezas", Toast.LENGTH_SHORT).show();
                            } else{
                                Det_Venta item = new Det_Venta();
                                precio = (Precios) spUnidad.getSelectedItem();

                                //IF para chequeo de cantidadResult de promociones
                                if (seleccion.getId_producto().equals(cpromos.getPromoProductoVenta())) {
                                    if (Double.parseDouble(editCantidad.getText().toString()) >= cpromos.getCantidadVenta()) {
                                        if (cpromos.getPromoProductoVenta().equals(cpromos.getPromoProductoEntrega())) {
                                            int residue = Integer.parseInt(editCantidad.getText().toString()) / (int) cpromos.getCantidadVenta();
                                            item.setCantidadResult(residue);
                                        } else {
                                            item.setCantidadResult((int) cpromos.getCantidadEntrega());
                                        }
                                    }
                                } else {
                                    item.setCantidadResult(0);
                                }

                                if (item.getCantidadResult() != 0 && isCargaPromocion(cpromos.getPromoProductoEntrega()) && isProductoPromocion(cpromos.getPromoProductoEntrega()) >= cpromos.getCantidadEntrega() && promocionInventario != 0) {
                                    Det_Venta item2 = new Det_Venta();

                                    //Producto #1
                                    item.setId_producto(seleccion.getId_producto());
                                    item.setDescripcion(seleccion.getDescripcion());
                                    item.setPromocion(false);

                                    //if para reduccion de promo si es el mismo producto
                                    if (cpromos.getPromoProductoVenta().equals(item.getId_producto())) {
                                        if (cpromos.getPromoProductoEntrega().equals(cpromos.getPromoProductoVenta())) {
                                            item.setCantidad(Double.parseDouble(editCantidad.getText().toString()) - (cpromos.getCantidadEntrega()*num2));
                                        } else {
                                            item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                        }
                                    } else {
                                        item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                    }

//                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));

                                    //promociones

                                    if (seleccion.getDecimales() == 1) {
                                        item.setPiezaB(Double.parseDouble(editCantidad2.getText().toString()));
                                    } else {
                                        if (seleccion.getId_producto().equals(cpromos.getPromoProductoEntrega())) {
                                            item.setPiezaB(Double.parseDouble(editCantidad.getText().toString()) - (cpromos.getCantidadEntrega() * num2));
                                        } else {
                                            item.setPiezaB(item.getCantidad());
                                        }
                                    }
                                    item.setUnidad(precio.getUnidad());
                                    item.setId_unidad(precio.getId_unidad());
                                    if (seleccion.getId_producto().equals(cpromos.getPromoProductoEntrega())){
//                                            if (item.getCantidad() == 1){
                                        item.setSubtotal(F_Venta.subtotal);
                                        item.setTotal(F_Venta.total);
//                                            } else {
//                                                item.setSubtotal(F_Venta.subtotal - (precio.getPrecio() * num2));
//                                                item.setTotal(F_Venta.total - (precio.getPrecio() * num2));
//                                            }
                                    } else {
                                        item.setSubtotal(F_Venta.subtotal);
                                        item.setTotal(F_Venta.total);
                                    }
                                    item.setImpuestos(F_Venta.impuestos);
                                    item.setConversion(precio.getConversion());
                                    item.setPrecio(precio.getPrecio());

                                    //Producto #2 de promociones

                                    Producto itemPromo = dbd.getProductodePromo(cpromos.getPromoProductoEntrega());

                                    item2.setId_producto(itemPromo.getId_producto());
                                    item2.setDescripcion(itemPromo.getDescripcion());
                                    item2.setPromocion(true);
                                    item2.setCantidad(cpromos.getCantidadEntrega()*num2);
                                    if (item2.getCantidad() >= promocionInventario){
                                        item2.setCantidad(promocionInventario);
                                    }


                                    //promociones

                                    item2.setPiezaB(cpromos.getCantidadEntrega()*num2);
                                    if (item2.getCantidad() >= promocionInventario){
                                        item2.setPiezaB(promocionInventario);
                                    }
                                    item2.setUnidad(conversionItem2.get(0).getUnidad());
                                    item2.setId_unidad(conversionItem2.get(0).getId_unidad());
                                    item2.setSubtotal(0);
                                    item2.setImpuestos(0);
                                    item2.setTotal(0);
                                    item2.setConversion(conversionItem2.get(0).getConversion());
                                    item2.setPrecio(0);

                                    detalles.add(item);

                                    detalles.add(item2);
                                } else {
                                    item.setId_producto(seleccion.getId_producto());
                                    item.setDescripcion(seleccion.getDescripcion());
                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));

                                    //promociones

                                    if (seleccion.getDecimales() == 1) {
                                        item.setPiezaB(Double.parseDouble(editCantidad2.getText().toString()));
                                    } else {
                                        item.setPiezaB(Double.parseDouble(editCantidad.getText().toString()));
                                    }
                                    item.setUnidad(precio.getUnidad());
                                    item.setId_unidad(precio.getId_unidad());
                                    item.setSubtotal(F_Venta.subtotal);
                                    item.setImpuestos(F_Venta.impuestos);
                                    item.setTotal(F_Venta.total);
                                    item.setConversion(precio.getConversion());
                                    item.setPrecio(precio.getPrecio());

                                    detalles.add(item);
                                }


                                //Actualizar LIsta
                                F_Venta.lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                                //Actualizar Totales
                                UpdateImportes();
                            }
                        }
                        else if(Double.valueOf(editCantidad.getText().toString())>totalInventario(seleccion.getId_producto())){
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        } else if(seleccion.getDecimales() == 1) {
                            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        }else {
//                            //IF para chequeo de cantidadResult de promociones
//                            if (promoProductoVenta.equals(seleccion.getId_producto())){
//                                if ( Double.parseDouble(editCantidad.getText().toString()) >= cantidadVenta ){
//                                    if (promoProductoVenta.equals(promoProductoEntrega)){
//                                        int residue = (int)item.getCantidad() / (int)cantidadVenta;
//                                        item.setCantidadResult(residue);
//                                    } else{
//                                        item.setCantidadResult((int) cantidadEntrega);
//                                    }
//                                }
//                            } else { item.setCantidadResult(0); }

                            if (item.getCantidadResult() != 0){

                                Det_Venta item2 = new Det_Venta();

                                //Promociones Section
//                                final Cliente_Promos cpromos = new Cliente_Promos();
//                                for (int i = 0;i < clientes_promos.size(); i++){
//                                    if (seleccion.getId_producto().equals(clientes_promos.get(i).getPromoProductoVenta())) {
//                                        cpromos.setPromoCliente(clientes_promos.get(i).getPromoCliente());
//                                        cpromos.setCantidadVenta(clientes_promos.get(i).getCantidadVenta());
//                                        cpromos.setCantidadEntrega(clientes_promos.get(i).getCantidadEntrega());
//                                        cpromos.setPromoProductoVenta(clientes_promos.get(i).getPromoProductoVenta());
//                                        cpromos.setPromoProductoEntrega(clientes_promos.get(i).getPromoProductoEntrega());
//                                    }
//                                }

                                //if para reduccion de promo si es el mismo producto
                                if (cpromos.getPromoProductoVenta().equals(item.getId_producto())){
                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()) - cpromos.getCantidadEntrega());
                                } else {
                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                }

                                if (seleccion.getDecimales() == 1) {
                                    item.setPiezaB(Double.parseDouble(editCantidad2.getText().toString()));
                                } else {
                                    item.setPiezaB(Double.parseDouble(editCantidad.getText().toString()));
                                }
                                item.setUnidad(precio.getUnidad());
                                item.setId_unidad(precio.getId_unidad());

                                item.setSubtotal(subtotal);
                                item.setImpuestos(impuestos);
                                item.setTotal(total);


                                item.setConversion(precio.getConversion());
                                item.setPrecio(precio.getPrecio());

                                //Producto #2 de promociones

                                Producto itemPromo  = dbd.getProductodePromo(cpromos.getPromoProductoEntrega());

                                item2.setId_producto(itemPromo.getId_producto());
                                item2.setDescripcion(itemPromo.getDescripcion());
                                item2.setPromocion(true);
                                item2.setCantidad(item.getCantidadResult()*cpromos.getCantidadEntrega());

                                //promociones

                                if(seleccion.getDecimales() == 1) {
                                    item2.setPiezaB(Double.parseDouble(editCantidad2.getText().toString()));
                                } else{
                                    item2.setPiezaB(item.getCantidadResult()*cpromos.getCantidadEntrega());
                                }
                                item2.setUnidad(conversionItem2.get(0).getUnidad());
                                item2.setId_unidad(conversionItem2.get(0).getId_unidad());
                                item2.setSubtotal(0);
                                item2.setImpuestos(0);
                                item2.setTotal(0);
                                item2.setConversion(conversionItem2.get(0).getConversion());
                                item2.setPrecio(0);

                                detalles.set(position, item);

                                if (item.getCantidadResult() > 0) {
                                    detalles.set(position + 1, item2);
                                } else {
                                    detalles.remove(position+1);
                                }

                                //Actualizar LIsta
                                lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                                //Actualizar Totales
                                UpdateImportes();
                            } else {

                                //Promociones Section
//                                final Cliente_Promos cpromos = new Cliente_Promos();
//                                for (int i = 0;i < clientes_promos.size(); i++){
//                                    if (seleccion.getId_producto().equals(clientes_promos.get(i).getPromoProductoVenta())) {
//                                        cpromos.setPromoCliente(clientes_promos.get(i).getPromoCliente());
//                                        cpromos.setCantidadVenta(clientes_promos.get(i).getCantidadVenta());
//                                        cpromos.setCantidadEntrega(clientes_promos.get(i).getCantidadEntrega());
//                                        cpromos.setPromoProductoVenta(clientes_promos.get(i).getPromoProductoVenta());
//                                        cpromos.setPromoProductoEntrega(clientes_promos.get(i).getPromoProductoEntrega());
//                                    }
//                                }

                                if (item.getId_producto().equals(cpromos.getPromoProductoVenta())){
                                    //En caso que la modificacion si alcance la promo

                                    //promociones
                                    if (enPromocion){
                                        if (cpromos.getPromoProductoVenta().equals(item.getId_producto())){
//                                            if (cantidadVenta <= item.getCantidad()){
                                                int residue = (int)Double.parseDouble(editCantidad.getText().toString()) / (int)cpromos.getCantidadVenta();
                                                item.setCantidadResult(residue);
//                                            }
                                        }
                                    } else { item.setCantidadResult(0); }


                                    Det_Venta item2 = new Det_Venta();

                                    //Producto #1
                                    item.setId_producto(seleccion.getId_producto());
                                    item.setDescripcion(seleccion.getDescripcion());
                                    item.setPromocion(false);

                                    //if para reduccion de promo si es el mismo producto
                                    if (cpromos.getPromoProductoVenta().equals(item.getId_producto())){
                                        item.setCantidad(Double.parseDouble(editCantidad.getText().toString()) - num2);
                                    } else {
                                        item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                    }

//                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));

                                    //promociones

                                    if(seleccion.getDecimales() == 1) {
                                        item.setPiezaB(Double.parseDouble(editCantidad2.getText().toString()));
                                    } else{
                                        item.setPiezaB(Double.parseDouble(editCantidad.getText().toString()));
                                    }
                                    item.setUnidad(precio.getUnidad());
                                    item.setId_unidad(precio.getId_unidad());
                                    item.setSubtotal(F_Venta.subtotal);
                                    item.setImpuestos(F_Venta.impuestos);
                                    item.setTotal(F_Venta.total);
                                    item.setConversion(precio.getConversion());
                                    item.setPrecio(precio.getPrecio());

                                    //Producto #2 de promociones

                                    Producto itemPromo = dbd.getProductodePromo(cpromos.getPromoProductoEntrega());

                                    item2.setId_producto(itemPromo.getId_producto());
                                    item2.setDescripcion(itemPromo.getDescripcion());
                                    item2.setPromocion(true);
                                    item2.setCantidad(item.getCantidadResult()*cpromos.getCantidadEntrega());

                                    //promociones

                                    if(seleccion.getDecimales() == 1) {
                                        item2.setPiezaB(item.getCantidadResult()*cpromos.getCantidadEntrega());
                                    } else{
                                        item2.setPiezaB(item.getCantidadResult()*cpromos.getCantidadEntrega());
                                    }
                                    item2.setUnidad(precio.getUnidad());
                                    item2.setId_unidad(precio.getId_unidad());
                                    item2.setSubtotal(0);
                                    item2.setImpuestos(0);
                                    item2.setTotal(0);
                                    item2.setConversion(0);
                                    item2.setPrecio(0);

//                                    F_Venta.detalles.add(item);
                                    if (item.getCantidadResult() > 0) {
                                        F_Venta.detalles.add(item2);
//                                        detalles.set(position, item);
                                    } else {
                                        if (detalles.size() > 1) {
                                            detalles.remove(position + 1);
                                        }
                                    }


//                                    detalles.set(position, item);

//                                    if (item.getCantidadResult() > 0) {
//                                        detalles.set(position, item);
//                                    } else {
//                                        detalles.remove(position+1);
//                                    }



                                    //Actualizar LIsta
                                    lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                                    //Actualizar Totales
                                    UpdateImportes();

                                } else {


                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                    if (seleccion.getDecimales() == 1) {
                                        item.setPiezaB(Double.parseDouble(editCantidad2.getText().toString()));
                                    } else {
                                        item.setPiezaB(Double.parseDouble(editCantidad.getText().toString()));
                                    }
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
                boolean deletePromo = false;
                if (!mod.isPromocion()) {
                    if (detalles.size()>1 & pos+1 < detalles.size()) {
                        if (detalles.get(pos + 1).isPromocion()) {
                            deletePromo = true;
                        }
                    }

                    //Eliminar de la Lista
                    detalles.remove(pos);
                    for (Cliente_Promos promo: clientes_promos
                         ) {
                        if (mod.getId_producto().equals(promo.getPromoProductoVenta()) & deletePromo){
                            promo2 = false;
                            detalles.remove(pos);
                        }
                    }



                    //Actualizar Lista
                    lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                    //Actualizar Totales
                    UpdateImportes();

                }
                //Ocultar dialogo
                menu.cancel();
            }
        });
        //Click Modificar
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Cliente_Promos promo: clientes_promos
                ) {
                    if (cliente.getId_cliente().equals(promo.getPromoCliente()) & mod.getId_producto().equals(promo.getPromoProductoVenta())){
                        promo2 = true;
                    }
                }
                if (!mod.isPromocion() & !promo2) {


                    EditDialog(mod, pos);

                }
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
            final Spinner spFormaPago = (Spinner) d.findViewById(R.id.spFormaPago);
            final ArrayList<Forma_Venta> formas_venta = dbd.getFormas(cliente.getId_cliente());
            final Switch facturar = (Switch) d.findViewById(R.id.swFacturar);

            //Formas de Venta
            ArrayAdapter<Forma_Venta> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, formas_venta);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFormas.setAdapter(adapter);

            //cliente contado o credito
            int spText = 0;


            //text forma
            for (int i = 0; i < formas_venta.size(); i++){
                if (formas_venta.get(i).getId_forma() == 1){
                    spText = 1;
                } else{
                    spText = 2;
                }
            }

            //Formas Metodo Pago
            if (spText == 1) {

                List<String> tipos = Arrays.asList(getResources().getStringArray(R.array.tiposList));
                tipoPago = dbd.getPago(visita.getId_visita());


                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.tiposList));
                spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spFormaPago.setAdapter(spinnerArrayAdapter);

            } else{
                ArrayList<String> tipos = new ArrayList<String>();
                tipos.add("Por Definir");
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tipos);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFormaPago.setAdapter(adapter2);
            }



            if (!flag) {
                //Mostrar Dialogo
                builder.setView(d)
                        .setTitle("Forma de Venta")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("Terminar Venta", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String forma = formas_venta.get(spFormas.getSelectedItemPosition()).getForma();
                                int id_forma = formas_venta.get(spFormas.getSelectedItemPosition()).getId_forma();


                                //GPS
                                location = Main.getGPS();


                                String switchCase = spFormaPago.getSelectedItem().toString();
                                int spMetodoPago = 0;
                                switch (switchCase) {
                                    case "Efectivo":
                                        spMetodoPago = 01;
                                        break;

                                    case "Cheque":
                                        spMetodoPago = 02;
                                        break;

                                    case "Transferencia":
                                        spMetodoPago = 03;
                                        break;

                                    case "Tarjeta":
                                        spMetodoPago = 04;
                                        break;
                                }


                                Serie folios;
                                if (facturar.isChecked()) {
                                    folios = dbd.getFolio(true, MARCA, flag);
                                } else {
                                    folios = dbd.getFolio(false, "", flag);
                                }

                                venta.setId_visita(visita.getId_visita());
                                venta.setId_cliente(cliente.getId_cliente());
                                venta.setEmpresa(MARCA);
                                venta.setForma_venta(forma);
                                venta.setId_forma_venta(id_forma);
                                venta.setSerie(folios.getSerie());
                                venta.setFolio(folios.getFolio());
                                venta.setFecha(Main.getFecha() + "-" + Main.getHora());
                                venta.setDet_venta(detalles);
                                venta.setMetodo_pago(switchCase);
                                venta.setId_metodo_pago(spMetodoPago);
                                venta.setGpsVenta(location);

                                if (flag) {
                                    venta.setDet_venta(detalles);
                                    dbd.UpdateVenta(venta);

                                } else {
                                    dbd.setVenta(venta);
                                }


                                if (detalles.size() > 0) {
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
            } else{
                edit_P_Venta();
            }
            //Editando Venta
        }else{
            edit_P_Venta();
        }

    }

    //Funcion de Inventario
    public double totalInventario(String idProducto){
        final Inventario inv_ventas;
        final Inventario inv_cambios;
        final Inventario inv_total;

        final double invCarga;
        final double invVenta;
        final double invCamb;
        final double invTotal;

        double detCantidad = 0;

        for (Det_Venta det_venta : detalles) {
            if (det_venta.getId_producto().equals(idProducto) && !det_venta.isPromocion()) {
                detCantidad += det_venta.getCantidad();
            }
        }

        inv_total = dbd.getCargaUnidad(idProducto);
        inv_ventas = dbd.getVentaUnidad(idProducto);
        inv_cambios = dbd.getCambioUnidad(idProducto);

        invCarga = (double) inv_total.getCantidad();
        invVenta = (double) inv_ventas.getVentas_inventario();
        invCamb = (double) inv_cambios.getCambios_inventario();
        invTotal = invCarga - invVenta - invCamb + detCantidad;

        return invTotal;
    }

    //Determinar si aun hay producto de promocion
    public double isProductoPromocion(String idProducto){
        final Inventario inv_cargaUnidad = dbd.getCargaUnidad(idProducto);
        final Inventario inv_VentaUnidad = dbd.getVentaUnidad(idProducto);
        final Inventario inv_cambioUnidad = dbd.getCambioUnidad(idProducto);

        final double inventario = inv_cargaUnidad.getCantidad();
        final double venta = inv_VentaUnidad.getVentas_inventario();
        final double cambio = inv_cambioUnidad.getCambios_inventario();
        final double total;

        total =  inventario -venta - cambio;

        return total;

    }

    //Determinar si hay producto de carga
    public boolean isCargaPromocion(String productoPromo){
        boolean isCarga = false;
        final Inventario cargaProducto;
        final double carga;

        cargaProducto = dbd.getCargaUnidad(productoPromo);
        carga = (double) cargaProducto.getCantidad();

        if (carga > 0){
            isCarga = true;
        }
        return isCarga;

    }

    public void edit_P_Venta(){
        //Actualizar Venta
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

    //Two Digits
    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

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
