package com.bybick.lacteosjolla.FragmentsProducts;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Detalle;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Venta;
import com.bybick.lacteosjolla.ObjectIN.Carga;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Cliente_Promos;
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Promociones;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.R;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.sql.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bybick.lacteosjolla.Fragments.F_Venta.detalles;

/**
 * Created by bicktor on 11/07/2016.
 */
public class F_ProductosVenta extends Fragment implements AdapterView.OnItemClickListener,
        TextWatcher{
    Context context;
    DBData dbd;

    Cliente cliente;
    Visita visita;
    FragmentManager fm;
    Det_Venta det_venta = new Det_Venta();
    Det_Cambio det_cambio = new Det_Cambio();
//    Carga carga  = new Carga();

    //Productos
    ArrayList<Producto> productos;
    ArrayList<Producto> busqueda;
    ArrayList<Precios> precios;
    ArrayList<Precios> conversionItem2;
//    double carga;
//    ArrayList<Unidad> inv;




    Inventario piezasInv;

//    BigDecimal inv;
//    BigDecimal vent;
//    BigDecimal camb;
    BigDecimal total;
//    BigDecimal cantDetalle = new BigDecimal(0);

    double piezascont;

    //if promo
    ArrayList<Cliente_Promos> cliente_promos;
    boolean enPromocion;
    Promociones promociones;
    double cantidadVenta;
    double cantidadEntrega;
    String promoProductoVenta;
    String promoProductoEntrega;

    boolean inv_detalle_zero = false;
    Producto seleccion;

    ListView lstProductos;
    EditText editSearch;

    //two digits
    Pattern mPattern;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    public void setAuxiliares(Cliente cliente, Visita visita, boolean promociones) {
        this.cliente = cliente;
        this.visita = visita;
        this.enPromocion = promociones;
    }

    public void setPromocion(ArrayList<Cliente_Promos> cliente_promos){
        this.cliente_promos = cliente_promos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_productos, container, false);
        getViews(view);

        return view;
    }

    //Obtener Vistas
    public void getViews(View v) {
        editSearch = (EditText) v.findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(this);

        //obtener lista
        lstProductos = (ListView) v.findViewById(R.id.lstProductos);

        //llenar lista
        lstProductos.setAdapter(new Adapter_Producto(context, productos));

        //listener de la lista
        lstProductos.setOnItemClickListener(this);
    }

    //Seleccionar
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtener producto
        if(editSearch.getText().toString().isEmpty()) {
            seleccion = productos.get(position);
        } else {
            seleccion = busqueda.get(position);
        }

        if(!isAdd(seleccion)){
            //Abrir Dialogo de Cantidad
            AddCantidad();
            fm.popBackStack();
        } else {
            Toast.makeText(context, "Agrega el producto en una nueva visita.", Toast.LENGTH_SHORT).show();
        }
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

    //Determinar si aun hay producto de promocion
    public double isProductoPromocion(String idProducto){
        final Inventario inv_cargaUnidad = dbd.getCargaUnidad(idProducto);
        final Inventario inv_VentaUnidad = dbd.getVentaUnidad(idProducto);
        final Inventario inv_cambioUnidad = dbd.getCambioUnidad(idProducto);

        final BigDecimal inventario = new BigDecimal(inv_cargaUnidad.getCantidad());
        final BigDecimal venta = new BigDecimal(inv_VentaUnidad.getVentas_inventario());
        final BigDecimal cambio = new BigDecimal(inv_cambioUnidad.getCambios_inventario());



        total =  inventario.subtract(venta).subtract(cambio);

        return total.doubleValue();

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
            if (det_venta.getId_producto().equals(idProducto)) {
                detCantidad += det_venta.getCantidad();
            }
        }

        inv_total = dbd.getCargaUnidad(idProducto);
        inv_ventas = dbd.getVentaUnidad(idProducto);
        inv_cambios = dbd.getCambioUnidad(idProducto);

        invCarga = (double) inv_total.getCantidad();
        invVenta = (double) inv_ventas.getVentas_inventario();
        invCamb = (double) inv_cambios.getCambios_inventario();
        invTotal = invCarga - invVenta - invCamb - detCantidad;

        return invTotal;
    }

    //Abrir Dialogo de Cantidad
    public void AddCantidad() {
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
        final TextView txtPiezas = (TextView) view.findViewById(R.id.txtPieza);
        final TextView txtNumPieza = (TextView) view.findViewById(R.id.txtNumPiezas);
        if(seleccion.getDecimales() == 1) {
            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editCantidad2.setVisibility(View.VISIBLE);
            txtPiezas.setVisibility(View.VISIBLE);
            txtNumPieza.setVisibility(View.VISIBLE);
        }

        //Promociones Section
        final Cliente_Promos cpromos = new Cliente_Promos();
        if (enPromocion) {
            for (int i = 0; i < cliente_promos.size(); i++) {
                if (seleccion.getId_producto().equals(cliente_promos.get(i).getPromoProductoVenta())) {
                    cpromos.setPromoCliente(cliente_promos.get(i).getPromoCliente());
                    cpromos.setCantidadVenta(cliente_promos.get(i).getCantidadVenta());
                    cpromos.setCantidadEntrega(cliente_promos.get(i).getCantidadEntrega());
                    cpromos.setPromoProductoVenta(cliente_promos.get(i).getPromoProductoVenta());
                    cpromos.setPromoProductoEntrega(cliente_promos.get(i).getPromoProductoEntrega());
                }
//                else {
//                    cpromos.setPromoProductoVenta(cliente_promos.get(i).getPromoProductoVenta());
//                }
            }
        }

        //Unidad
        final Spinner spUnidad = (Spinner) view.findViewById(R.id.spUnidad);
        //VAriable del Precio seleccionado
        precios = dbd.getPrecios(seleccion.getId_producto(), cliente.getId_cliente());
        ArrayAdapter<Precios> adapter = new ArrayAdapter<Precios>(context, android.R.layout.simple_spinner_item, precios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adapter);

//        inv_total = dbd.getCargaUnidad(seleccion.getId_producto());
//        inv_ventas = dbd.getVentaUnidad(seleccion.getId_producto());
//        inv_cambios = dbd.getCambioUnidad(seleccion.getId_producto());
//
//        inv = new BigDecimal(inv_total.getCantidad());
//        vent = new BigDecimal(inv_ventas.getVentas_inventario());
//        camb = new BigDecimal(inv_cambios.getCambios_inventario());

//        total =  inv.subtract(vent).subtract(camb);
//        total =  inv.subtract(camb);

        //piezas inventario

        piezasInv = dbd.getCargaUnidadPieza(seleccion.getId_producto());
        final Inventario piezas_total = dbd.getVentaPiezas(seleccion.getId_producto());

        piezascont = piezasInv.getPiezas();
        final double piezasVenta = piezas_total.getVentas_piezas();
        final double piezasTotal = piezascont - piezasVenta;

        //Precio
        final TextView txtPrecio = (TextView) view.findViewById(R.id.txtPrecio);

        //Importe
        final TextView txtImporte = (TextView) view.findViewById(R.id.txtImporte);

        //Inventario
        final TextView txtInventario = (TextView) view.findViewById(R.id.txtNumCant);

        txtInventario.setText("" + FormatNumber(totalInventario(seleccion.getId_producto())));

        //Piezas
        txtNumPieza.setText("" + piezasTotal);


        //Lister de Spinner Unidad y del Edit Cantidad
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
                    String c = s.toString();
                    if (c.equals(".")){
                        c = "0.";
                    }
                    double cantidad = Double.parseDouble(c);

                    //cantidad_res
                    double num1 = cantidad/cpromos.getCantidadVenta();
                    int num2 = (int)num1;

                    //if de promociones para los precios, el else es la opcion de no promocion
                    if (seleccion.getId_producto().equals(cpromos.getPromoProductoVenta()) && isCargaPromocion(cpromos.getPromoProductoEntrega())) {
                        if (cantidad >= cpromos.getCantidadVenta()) {
                            if (cpromos.getPromoProductoEntrega().equals(cpromos.getPromoProductoVenta())) {
                                //Total
                                String totalString = FormatNumber(price.getPrecio() * (cantidad - (cpromos.getCantidadEntrega() * num2)));
                                F_Venta.total = DecodeStringDecimal(totalString);

                                //Impuesto
                                String impuestoString = FormatNumber(price.getImpuestos() * (cantidad - (cpromos.getCantidadEntrega() * num2)));
                                F_Venta.impuestos = DecodeStringDecimal(impuestoString);

                                //Subtotal
                                String subString = FormatNumber(price.getSubprecio() * (cantidad - (cpromos.getCantidadEntrega() * num2)));
                                F_Venta.subtotal = DecodeStringDecimal(subString);

                                txtImporte.setText("$ " + totalString);
                            } else { //else por si los productos son diferentes entre si mismos en la promocion
                                //Total
                                String totalString = FormatNumber(price.getPrecio() * cantidad);
                                F_Venta.total = DecodeStringDecimal(totalString);

                                //Impuesto
                                String impuestoString = FormatNumber(price.getImpuestos() * cantidad);
                                F_Venta.impuestos = DecodeStringDecimal(impuestoString);

                                //Subtotal
                                String subString = FormatNumber(price.getSubprecio() * cantidad);
                                F_Venta.subtotal = DecodeStringDecimal(subString);

                                txtImporte.setText("$ " + totalString);
                            }
                        } else { //else de si la cantidad no alcanza para el producto en promocion
                            //Total
                            String totalString = FormatNumber(price.getPrecio() * cantidad);
                            F_Venta.total = DecodeStringDecimal(totalString);

                            //Impuesto
                            String impuestoString = FormatNumber(price.getImpuestos() * cantidad);
                            F_Venta.impuestos = DecodeStringDecimal(impuestoString);

                            //Subtotal
                            String subString = FormatNumber(price.getSubprecio() * cantidad);
                            F_Venta.subtotal = DecodeStringDecimal(subString);

                            txtImporte.setText("$ " + totalString);
                        }
                    } else { //else de si el producto no esta en promocion
                        //Total
                        String totalString = FormatNumber(price.getPrecio() * cantidad);
                        F_Venta.total = DecodeStringDecimal(totalString);

                        //Impuesto
                        String impuestoString = FormatNumber(price.getImpuestos() * cantidad);
                        F_Venta.impuestos = DecodeStringDecimal(impuestoString);

                        //Subtotal
                        String subString = FormatNumber(price.getSubprecio() * cantidad);
                        F_Venta.subtotal = DecodeStringDecimal(subString);

                        txtImporte.setText("$ " + totalString);
                    }


                } else {
                    txtImporte.setText("$ 00.00");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Mostrar Dialogo
        alert.setView(view)
                .setTitle(seleccion.getDescripcion())
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Si no mete cantidad

                        final BigDecimal editcantidad;
                        

                        final String edcnt = editCantidad.getText().toString();
                        if (edcnt.isEmpty()){
                            editcantidad = new BigDecimal(0);
                        } else {
                            editcantidad = new BigDecimal(edcnt);
                        }

                        double promocionInventario = totalInventario(cpromos.getPromoProductoEntrega());

                        //cantidad_res
                        double num1 = Double.parseDouble(editcantidad.toString())/cpromos.getCantidadVenta();
                        int num2 = (int)num1;
                        conversionItem2 = dbd.getPrecios(cpromos.getPromoProductoEntrega(), cliente.getId_cliente());

                        if (editCantidad.getText().toString().isEmpty()
                                || Double.valueOf(editCantidad.getText().toString()) <= 0) {
                            Toast.makeText(context, "No se introdujo cantidad", Toast.LENGTH_SHORT).show();
                        }else if(editcantidad.compareTo(new BigDecimal(totalInventario(seleccion.getId_producto()))) == 1){
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        } else if (seleccion.getDecimales() == 1){

                            //justificacion para las piezas arreglar luego por codigo duplicado-
                            if (editCantidad2.getText().toString().isEmpty()
                                    || Double.valueOf(editCantidad2.getText().toString())<0) {
                                Toast.makeText(context, "No se introdujeron piezas", Toast.LENGTH_SHORT).show();
                            } else{
                                Det_Venta item = new Det_Venta();
                                Precios precio = (Precios) spUnidad.getSelectedItem();

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
                        } else {
                            if (enPromocion) {
                               if (seleccion.getId_producto().equals(cpromos.getPromoProductoVenta())) {
                                    Det_Venta item = new Det_Venta();
                                    Precios precio = (Precios) spUnidad.getSelectedItem();

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

                                    if (item.getCantidadResult() != 0 && isCargaPromocion(cpromos.getPromoProductoEntrega()) && isProductoPromocion(cpromos.getPromoProductoEntrega()) >= cpromos.getCantidadEntrega()) {
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
                                            item.setPiezaB(Double.parseDouble(editCantidad.getText().toString()) - (cpromos.getCantidadEntrega()*num2));
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
                                        if (item2.getCantidad() >= isProductoPromocion(cpromos.getPromoProductoEntrega())){
                                            item2.setCantidad(isProductoPromocion(cpromos.getPromoProductoEntrega()));
                                        }


                                        //promociones

                                        item2.setPiezaB(cpromos.getCantidadEntrega()*num2);
                                        if (item2.getCantidad() >= isProductoPromocion(cpromos.getPromoProductoEntrega())){
                                            item2.setPiezaB(isProductoPromocion(cpromos.getPromoProductoEntrega()));
                                        }
                                        item2.setUnidad(precio.getUnidad());
                                        item2.setId_unidad(conversionItem2.get(0).getUnidad());
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
                                } else {
                                    Det_Venta item = new Det_Venta();
                                    Precios precio = (Precios) spUnidad.getSelectedItem();

                                    item.setId_producto(seleccion.getId_producto());
                                    item.setDescripcion(seleccion.getDescripcion());
                                    item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                    item.setCantidadResult(0);

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

                                    //Actualizar LIsta
                                    F_Venta.lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                                    //Actualizar Totales
                                    UpdateImportes();
                                }
                                //IF de Promociones
                            } else {
                                Det_Venta item = new Det_Venta();
                                Precios precio = (Precios) spUnidad.getSelectedItem();

                                item.setId_producto(seleccion.getId_producto());
                                item.setDescripcion(seleccion.getDescripcion());
                                item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                item.setCantidadResult(0);

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

                                detalles.add(item);

                                //Actualizar LIsta
                                F_Venta.lstVentas.setAdapter(new Adapter_Detalle(context, detalles));

                                //Actualizar Totales
                                UpdateImportes();
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

    //Busqueda
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            busqueda = new ArrayList<>();
            for (Producto item : productos) {
                if (item.getId_producto().toLowerCase().indexOf(s.toString().toLowerCase()) != -1
                        || item.getDescripcion().toLowerCase().indexOf(s.toString().toLowerCase()) != -1
                        || s.toString().equalsIgnoreCase("")) {

                    busqueda.add(item);
                }
            }
            lstProductos.setAdapter(new Adapter_Producto(context, busqueda));
        } else {
            lstProductos.setAdapter(new Adapter_Producto(context, productos));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //Validar que el producto no este agregado
    public boolean isAdd(Producto item) {
        boolean is = false;

        for(Det_Venta pos : detalles) {
            if(pos.getId_producto().equals(item.getId_producto()) && !pos.isPromocion())
                is = true;
        }
        return is;
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
        F_Venta.txtSubTotal.setText(FormatNumber(subtotal));
        F_Venta.txtImpuestos.setText(FormatNumber(impuestos));
        F_Venta.txtTotal.setText(FormatNumber(total));

        //Actualizar Totales en la Venta
        F_Venta.venta.setSubtotal(subtotal);
        F_Venta.venta.setImpuestos(impuestos);
        F_Venta.venta.setTotal(total);
    }

    public double TwoDigits ( double n) {
        DecimalFormat number = new DecimalFormat("###,##0.00");
        number.format(n);

        return n;
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
