package com.bybick.lacteosjolla.FragmentsProducts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.bybick.lacteosjolla.Adapters.Adapter_Cambio;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Cambio;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by bicktor on 12/07/2016.
 */
public class F_CambioEntrante extends Fragment implements AdapterView.OnItemClickListener,
        TextWatcher{
    Context context;
    DBData dbd;

    //Data
    Cliente cliente;
    Visita visita;
    FragmentManager fm;

    //Productos
    ArrayList<Producto> productos;
    ArrayList<Producto> equivalentes;
    ArrayList<Producto> busqueda;
    Producto seleccion;
    Det_Cambio detalle;

    //Inventario Total
    Inventario inv_total;
    Inventario inv_ventas;
    Inventario inv_cambios;
    Inventario inventario;

    double inv;
    double vent;
    double camb;
    double total;

    //Unidades Motiovs
    ArrayList<Unidad> unidades;
    ArrayList<Motivo> motivos;

    ListView lstProductos;
    EditText editSearch;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public void setAuxiliares(Cliente cliente, Visita visita) {
        this.cliente = cliente;
        this.visita = visita;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

        //Motivos del Cambio
        motivos = dbd.getMotivosCambio();

        //Nuevo Detalle
        detalle = new Det_Cambio();
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
        //Obtener caja de busqueda
        editSearch = (EditText) v.findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(this);

        //obtener lista
        lstProductos = (ListView) v.findViewById(R.id.lstProductos);
        lstProductos.setOnItemClickListener(this);

        //llenar lista
        lstProductos.setAdapter(new Adapter_Producto(context, productos));

    }

    //Seleccion de productos
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtener producto
        if(editSearch.getText().toString().isEmpty()) {
            seleccion = productos.get(position);
        } else {
            seleccion = busqueda.get(position);
        }

        //Unidades del Producto
        unidades = dbd.getUnidadesProducto(seleccion.getId_producto());

        //Abrir dialogo de Cantidad
        DialogCantidad();
    }

    //Dialogo de la Cantidad y Motivo
    public void DialogCantidad() {
        //Mostrar Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View d = inflater.inflate(R.layout.d_cambio_entrante,null);
        final TextView txtDescricpion = (TextView) d.findViewById(R.id.txtDesc);
        final EditText editCantidad = (EditText) d.findViewById(R.id.editCantidad);
        final Spinner spMotivos = (Spinner) d.findViewById(R.id.spMotivo);
        final Spinner spUnidadess = (Spinner) d.findViewById(R.id.spUnidad);
        final TextView tInventario = (TextView) d.findViewById(R.id.totalInv);

        txtDescricpion.setText(seleccion.getDescripcion());
        editCantidad.setText("1");
        if(seleccion.getDecimales() == 1)
            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        //Llenar textView de Inventario
        inv_total = dbd.getCargaUnidad(seleccion.getId_producto());
        inv_ventas = dbd.getVentaUnidad(seleccion.getId_producto());
        inv_cambios = dbd.getCambioUnidad(seleccion.getId_producto());

        inv = inv_total.getCantidad();
        vent = inv_ventas.getVentas_inventario();
        camb = inv_cambios.getCambios_inventario();

        total =  inv - vent - camb;

        //Inventario
        tInventario.setText("Inventario: " + total);


        //Llenar Spinner de Unidad
        ArrayAdapter<Unidad> adapter_unidad = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                unidades);
        adapter_unidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadess.setAdapter(adapter_unidad);

        //llenar Spinner de Motivo
        ArrayAdapter<Motivo> adapter_motivo=new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                motivos);
        adapter_motivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivos.setAdapter(adapter_motivo);

        builder.setView(d)
                .setTitle("Producto Entrante")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        detalle = new Det_Cambio();
                        //Agregar cantidad

                        detalle.setId_producto_recibido(seleccion.getId_producto());
                        detalle.setNombre_recibido(seleccion.getDescripcion());
                        detalle.setId_motivo(motivos.get(spMotivos.getSelectedItemPosition()).getId_motivo());
                        detalle.setMotivo(motivos.get(spMotivos.getSelectedItemPosition()).getDescripcion());
                        detalle.setId_unidad_in(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
                        detalle.setUnidad_in(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
                        detalle.setIndex_in(spUnidadess.getSelectedItemPosition());
                        //Toast.makeText(context,"" + detalle.getIndex_in(),Toast.LENGTH_LONG).show();
                        detalle.setIdex_motivo(spMotivos.getSelectedItemPosition());

                        //Obtener Productos Equivalentes
                        equivalentes = dbd.getEquivalentes(seleccion.getId_producto());

                        //Abrir Equivalentes
                        fm.popBackStack();

//                        F_CambioEquivalente frag = new F_CambioEquivalente();
//                        frag.setContext(context);
//                        frag.setDetalle(detalle);
//                        frag.setProductos(equivalentes);
//                        frag.setAuxiliares(cliente, visita);
//                        frag.setFm(fm);

//                        FragmentTransaction ft = fm.beginTransaction();
//                        ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
//                                R.animator.enter_up, R.animator.out_up);
//                        ft.add(frag, "Productos").addToBackStack("Cambio").commit();

                        if(editCantidad.getText().toString().isEmpty())
                            detalle.setCantidad_out(0);
                        else if(Double.valueOf(editCantidad.getText().toString())>total)
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        else
                            detalle.setCantidad_out(Double.parseDouble(editCantidad.getText().toString()));

                        detalle.setId_producto_entregado(seleccion.getId_producto());
                        detalle.setNombre_entregado(seleccion.getDescripcion());
                        detalle.setId_unidad_out(unidades.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
                        detalle.setUnidad_out(unidades.get(spUnidadess.getSelectedItemPosition()).getUnidad());
                        detalle.setIndex_out(spUnidadess.getSelectedItemPosition());
                        //Toast.makeText(context,"" + detalle.getIndex_in(),Toast.LENGTH_LONG).show();


                        if (editCantidad.getText().toString().isEmpty()) {
                            detalle.setCantidad_in(0);
                        } else if(Double.valueOf(editCantidad.getText().toString())>total) {
                            Toast.makeText(context, "No hay Suficiente Producto", Toast.LENGTH_SHORT).show();
                        } else {
                            detalle.setCantidad_in(Double.parseDouble(editCantidad.getText().toString()));
                            //Actualizar Lista
                            F_Cambio.detalles.add(detalle);
                            F_Cambio.lstCambios.setAdapter(new Adapter_Cambio(context, F_Cambio.detalles));
                            detalle.setCantidad_out(Double.parseDouble(editCantidad.getText().toString()));
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
    }

    //Cambio de Texto en Cantidad
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

}
