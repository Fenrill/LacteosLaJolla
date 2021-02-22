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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Jabas;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Jabas;
import com.bybick.lacteosjolla.Fragments.F_Venta;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectOUT.Det_Jabas;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Jabas;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class F_ProductosJabas extends Fragment implements AdapterView.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    Context context;
    DBData dbd;

    Cliente cliente;
    Visita visita;
    FragmentManager fm;
    ArrayList<Producto> jabas;
    ArrayList<Producto> busqueda;
    ArrayList<Precios> precios;

    Producto seleccion;

    //Views
    ListView lstProductos;
    EditText editSearch;

    public void setContext(Context context) { this.context = context; }

    public void setAuxiliares(Cliente cliente, Visita visita){
        this.cliente = cliente;
        this.visita = visita;
    }

    public void setFm(FragmentManager fm){
        this.fm = fm;
    }

    public void setProductos(ArrayList<Producto> jabas){
        this.jabas = jabas;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_productos, container, false);
        getViews(view);

        return view;
    }

    //Obtener Views
    public void getViews(View v){
        editSearch = (EditText) v.findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(this);

        //obtener lista
        lstProductos = (ListView) v.findViewById(R.id.lstProductos);

        //llenar lista
        lstProductos.setAdapter(new Adapter_Producto(context, jabas));

        //listener de la lista
        lstProductos.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //obtener productos
        if (editSearch.getText().toString().isEmpty()){
            seleccion = jabas.get(position);
        } else {
            seleccion = busqueda.get(position);
        }

//        if (!isAddd(seleccion)) {
            //Abrir dialogo de cantidad
            AddCantidad();
            fm.popBackStack();
//        }
    }

    public void AddCantidad() {

        //Crear Dialogo
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //Obtener el inflate para inflar la vista
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Crear la vista del Dialogo
        final View view = inflater.inflate(R.layout.d_cantidad, null);
        //Obtener Vista
        //Cantidad
        final EditText editCantidad = (EditText) view.findViewById(R.id.editCantidad);

//        editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        //Unidad
        final Spinner spUnidad = (Spinner) view.findViewById(R.id.spUnidad);

        //hide labels
        final TextView lbPrecio = (TextView) view.findViewById(R.id.lbPrecio);
        final TextView txtInventario = (TextView) view.findViewById(R.id.txtInventario);
        final TextView txtCant = (TextView) view.findViewById(R.id.txtCant);
        final TextView txtPieza = (TextView) view.findViewById(R.id.txtPieza);
        final TextView txtNumCant = (TextView) view.findViewById(R.id.txtNumCant);
        final TextView txtNumPiezas = (TextView) view.findViewById(R.id.txtNumPiezas);
        final TextView txtImporte = (TextView) view.findViewById(R.id.txtImporte);

        lbPrecio.setVisibility(view.GONE);
        txtInventario.setVisibility(view.GONE);
        txtCant.setVisibility(view.GONE);
        txtPieza.setVisibility(view.GONE);
        txtNumCant.setVisibility(view.GONE);
        txtNumPiezas.setVisibility(view.GONE);
        txtImporte.setVisibility(view.GONE);

        //obtener switches entrada/salida
        final RadioGroup rgrpEntSal = (RadioGroup) view.findViewById(R.id.rgrpEntSal);

        rgrpEntSal.setVisibility(View.VISIBLE);

        //variable de precios seleccionado
        precios = dbd.getPrecios(seleccion.getId_producto(), cliente.getId_cliente());
        ArrayAdapter<Precios> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, precios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adapter);


        //mostrar dialogo
        alert.setView(view)
                .setTitle(seleccion.getDescripcion())
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final int rgrpID = rgrpEntSal.getCheckedRadioButtonId();

                        if (editCantidad.getText().toString().isEmpty()
                                || Double.valueOf(editCantidad.getText().toString()) < 0){
                            Toast.makeText(context, "No se Introdujo Cantidad", Toast.LENGTH_SHORT).show();
                        }else if (rgrpID == -1){
                            Toast.makeText(context, "No se selecciono Entrada/Salida", Toast.LENGTH_SHORT).show();
                        }else {
                            Det_Jabas item = new Det_Jabas();

                            final RadioButton rButton = (RadioButton) view.findViewById(rgrpID);

                            item.setId_producto(seleccion.getId_producto());
                            item.setCantidad(Double.parseDouble(editCantidad.getText().toString()));
                            item.setDescripcion(seleccion.getDescripcion());
                            item.setEnt_sal(rButton.getText().toString());
                            F_Jabas.detalles.add(item);

                            //actualizar lista
                            F_Jabas.lstJabas.setAdapter(new Adapter_Jabas(context, F_Jabas.detalles));

//                            if (swEntrada.isChecked()){
//                                item.setEnt_sal("E");
//                                F_Jabas.detalles.add(item);
//
//                                //actualizar lista
//                                F_Jabas.lstJabas.setAdapter(new Adapter_Jabas(context, F_Jabas.detalles));
//                            } else if (swSalida.isChecked()){
//                                item.setEnt_sal("S");
//                                F_Jabas.detalles.add(item);
//
//                                //actualizar lista
//                                F_Jabas.lstJabas.setAdapter(new Adapter_Jabas(context, F_Jabas.detalles));
//                            } else {
//                                Toast.makeText(context,"No seleccionaste Entrada o Salida de la Jaba", Toast.LENGTH_SHORT).show();
//                            }
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //Validar que el producto no este agregado
    public boolean isAddd(Producto item){
        boolean is = false;

        for (Det_Jabas pos : F_Jabas.detalles) {
            if (pos.getId_producto().equals(item.getId_producto())){
                is = true;
            }
        }

        return is;
    }
}
