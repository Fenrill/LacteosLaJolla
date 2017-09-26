package com.bybick.lacteosjolla.FragmentsProducts;

import android.app.AlertDialog;
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
import com.bybick.lacteosjolla.Adapters.Adapter_Detalle;
import com.bybick.lacteosjolla.Adapters.Adapter_Devolucion;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Cambio;
import com.bybick.lacteosjolla.Fragments.F_Devolucion;
import com.bybick.lacteosjolla.Fragments.F_Venta;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Devolucion;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class F_ProductosDevolucion extends Fragment implements AdapterView.OnItemClickListener,
        TextWatcher {
    Context context;
    DBData dbd;

    Cliente cliente;
    Visita visita;
    FragmentManager fm;

    //Productos
    ArrayList<Producto> productos;
    ArrayList<Producto> busqueda;
    ArrayList<Precios> precios;

    Producto seleccion;
    Det_Devolucion detalle;

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

        motivos = dbd.getMotivosCambio();
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
        } else {
            Toast.makeText(context, "El producto ya está agregado.", Toast.LENGTH_SHORT).show();
        }
    }

    //Abrir Dialogo de Cantidad
    public void AddCantidad() {
        //Mostrar Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View d = inflater.inflate(R.layout.d_cambio_entrante,null);

        final TextView txtDescricpion = (TextView) d.findViewById(R.id.txtDesc);
        final EditText editCantidad = (EditText) d.findViewById(R.id.editCantidad);
        final Spinner spMotivos = (Spinner) d.findViewById(R.id.spMotivo);
        final Spinner spUnidadess = (Spinner) d.findViewById(R.id.spUnidad);

        txtDescricpion.setText(seleccion.getDescripcion());
        editCantidad.setText("1");

        //Llenar Spinner de Unidad
        precios = dbd.getPrecios(seleccion.getId_producto(), cliente.getId_cliente());
        ArrayAdapter<Precios> adapter_unidad = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                precios);
        adapter_unidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadess.setAdapter(adapter_unidad);

        //llenar Spinner de Motivo
        ArrayAdapter<Motivo> adapter_motivo=new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                motivos);
        adapter_motivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivos.setAdapter(adapter_motivo);

        builder.setView(d)
                .setTitle("Producto a Devolver")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        detalle = new Det_Devolucion();
                        //Agregar cantidad
                        if (editCantidad.getText().toString().isEmpty())
                            detalle.setCantidad(0);
                        else
                            detalle.setCantidad(Double.parseDouble(editCantidad.getText().toString()));

                        detalle.setId_producto(seleccion.getId_producto());
                        detalle.setNombre(seleccion.getDescripcion());
                        detalle.setId_motivo(motivos.get(spMotivos.getSelectedItemPosition()).getId_motivo());
                        detalle.setMotivo(motivos.get(spMotivos.getSelectedItemPosition()).getDescripcion());
                        detalle.setUnidad(precios.get(spUnidadess.getSelectedItemPosition()).getUnidad());
                        detalle.setId_unidad(precios.get(spUnidadess.getSelectedItemPosition()).getId_unidad());
                        detalle.setIndexUnidad(spUnidadess.getSelectedItemPosition());
                        detalle.setIndexMotivo(spMotivos.getSelectedItemPosition());

                        //Obtener Importe
                        double precio = precios.get(spUnidadess.getSelectedItemPosition()).getPrecio();
                        double importe = precio * detalle.getCantidad();
                        //Guardar Importe
                        detalle.setImporte(importe);

                        //Abrir Equivalentes
                        fm.popBackStack();

                        //Actualizar Lista
                        F_Devolucion.detalles.add(detalle);
                        F_Devolucion.lstDevoluciones.setAdapter(new Adapter_Devolucion(context, F_Devolucion.detalles));

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();    }

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

        for(Det_Devolucion pos : F_Devolucion.detalles) {
            if(pos.getId_producto().equals(item.getId_producto()))
                is = true;
        }
        return is;
    }

}
