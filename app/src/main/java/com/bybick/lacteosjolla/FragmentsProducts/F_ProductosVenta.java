package com.bybick.lacteosjolla.FragmentsProducts;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
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
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.R;

import java.sql.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;

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
//    double carga;
//    ArrayList<Unidad> inv;

    Inventario inv_total;
    Inventario inv_ventas;
    Inventario inv_cambios;
    Inventario inventario;

    double inv;
    double vent;
    double camb;
    double total;

    Producto seleccion;

    ListView lstProductos;
    EditText editSearch;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    public void setAuxiliares(Cliente cliente, Visita visita) {
        this.cliente = cliente;
        this.visita = visita;
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
            Toast.makeText(context, "El producto ya está agregado.", Toast.LENGTH_SHORT).show();
        }
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
        if(seleccion.getDecimales() == 1)
            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //Unidad
        final Spinner spUnidad = (Spinner) view.findViewById(R.id.spUnidad);
        //VAriable del Precio seleccionado
        precios = dbd.getPrecios(seleccion.getId_producto(), cliente.getId_cliente());
        ArrayAdapter<Precios> adapter = new ArrayAdapter<Precios>(context, android.R.layout.simple_spinner_item, precios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adapter);
        //variable de inventario seleccionado

        inv_total = dbd.getCargaUnidad(seleccion.getId_producto());
        inv_ventas = dbd.getVentaUnidad(seleccion.getId_producto());
        inv_cambios = dbd.getCambioUnidad(seleccion.getId_producto());

        inv = inv_total.getCantidad();
        vent = inv_ventas.getVentas_inventario();
        camb = inv_cambios.getCambios_inventario();

        total =  inv - vent - camb;

        //Precio
        final TextView txtPrecio = (TextView) view.findViewById(R.id.txtPrecio);

        //Importe
        final TextView txtImporte = (TextView) view.findViewById(R.id.txtImporte);

        //Inventario
        final TextView txtInventario = (TextView) view.findViewById(R.id.txtInventario);

        txtInventario.setText("Inventario: " + total);


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
                        //Si no me mete cantidad
                        if(editCantidad.getText().toString().isEmpty() || Double.valueOf(editCantidad.getText().toString())<=0){
                            Toast.makeText(context, "No se introdujo cantidad", Toast.LENGTH_SHORT).show();
                        } else if(Double.valueOf(editCantidad.getText().toString())>total){
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        } else {
                                Det_Venta item = new Det_Venta();
                                Precios precio = (Precios) spUnidad.getSelectedItem();


                                item.setId_producto(seleccion.getId_producto());
                                item.setDescripcion(seleccion.getDescripcion());
                                item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                                item.setUnidad(precio.getUnidad());
                                item.setId_unidad(precio.getId_unidad());
                                item.setSubtotal(F_Venta.subtotal);
                                item.setImpuestos(F_Venta.impuestos);
                                item.setTotal(F_Venta.total);
                                item.setConversion(precio.getConversion());
                                item.setPrecio(precio.getPrecio());

                                F_Venta.detalles.add(item);

                                //Actualizar LIsta
                                F_Venta.lstVentas.setAdapter(new Adapter_Detalle(context, F_Venta.detalles));

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

        for(Det_Venta pos : F_Venta.detalles) {
            if(pos.getId_producto().equals(item.getId_producto()))
                is = true;
        }
        return is;
    }

    //Actualizar Importes Visuales y en la venta
    public void UpdateImportes() {
        double subtotal = 0;
        double impuestos = 0;
        double total = 0;

        for(int i = 0; i < F_Venta.detalles.size(); i ++) {
            Det_Venta item = F_Venta.detalles.get(i);
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
