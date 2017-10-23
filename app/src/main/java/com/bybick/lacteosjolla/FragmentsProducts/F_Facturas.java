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
import com.bybick.lacteosjolla.Adapters.Adapter_Factura;
import com.bybick.lacteosjolla.Adapters.Adapter_Pago;
import com.bybick.lacteosjolla.Adapters.Adapter_Producto;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Cobranza;
import com.bybick.lacteosjolla.Fragments.F_Venta;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bicktor on 14/07/2016.
 */
public class F_Facturas extends Fragment implements AdapterView.OnItemClickListener{
    Context context;
    DBData dbd;

    Cliente cliente;
    Visita visita;
    FragmentManager fm;

    //Productos
    ArrayList<Factura> facturas;

    Factura seleccion;
    double importe = 0;
    Det_Pago pago;

    ListView lstFacturas;
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

    public void setFacturas(ArrayList<Factura> facturas) {
        this.facturas = facturas;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

        pago = new Det_Pago();
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
        editSearch.setVisibility(EditText.GONE);

        //obtener lista
        lstFacturas = (ListView) v.findViewById(R.id.lstProductos);

        //llenar lista
        lstFacturas.setAdapter(new Adapter_Factura(context, facturas));

        //listener de la lista
        lstFacturas.setOnItemClickListener(this);
    }

    //Seleccionar
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtener producto
        seleccion = facturas.get(position);

        if(!isAdd(seleccion)){
            //Abrir Dialogo de Cantidad
            AddCantidad();
            fm.popBackStack();
        } else {
            Toast.makeText(context, "La factura ya esta agregada.", Toast.LENGTH_SHORT).show();
        }
    }

    //Abrir Dialogo de Cantidad
    public void AddCantidad() {
        //Crear Dialogo
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //Obtener el inflate para inflar la vista
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Crear la vista del dialogo
        View view = inflater.inflate(R.layout.d_pago, null);
        //Obtener Vistas

        //Total
        final TextView txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        txtTotal.setText("Total: $ " + FormatNumber(seleccion.getTotal()));

        //Saldo
        final TextView txtSaldo = (TextView) view.findViewById(R.id.txtSaldo);
        txtSaldo.setText("Saldo: $ " + FormatNumber(seleccion.getSaldo()));

        //Cantidad
        final EditText editCantidad = (EditText) view.findViewById(R.id.editCantidad);

        //Comentarios
        final EditText editComentario = (EditText) view.findViewById(R.id.editComentario);

        //Tipo de Pago
        final Spinner spTipo = (Spinner) view.findViewById(R.id.spTipoPago);
        //Variable del Precio seleccionado
        ArrayList<String> tipos = new ArrayList<String>();
        tipos.add("Efectivo");
        tipos.add("Cheque");
        tipos.add("Tranferencia");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        //Listener Cantidad
        editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !s.toString().endsWith(".")) {

                    importe = Double.parseDouble(editCantidad.getText().toString());
                    double saldo = seleccion.getTotal() - importe;

                    //Total
                    txtSaldo.setText("Saldo: $ " + FormatNumber(saldo));

                } else {
                    txtSaldo.setText("Saldo: $ " + FormatNumber(seleccion.getTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Mostrar Dialogo
        alert.setView(view)
                .setTitle(seleccion.getSerie() + " - " + seleccion.getFolio())
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Si no me mete cantidad
                        if(editCantidad.getText().toString().isEmpty()){
                            Toast.makeText(context, "No se introdujo Importe", Toast.LENGTH_SHORT).show();
                        } else {
                            Det_Pago item = new Det_Pago();

                            item.setId_det_pago(Main.NEWID());
                            item.setSerie(seleccion.getSerie());
                            item.setFolio(seleccion.getFolio());
                            item.setComentarios(editComentario.getText().toString());
                            item.setForma_pago(spTipo.getSelectedItem().toString());
                            item.setTotal_factura(seleccion.getTotal());
                            item.setImporte_pago(importe);
                            item.setSaldo_ant(seleccion.getSaldo());
                            item.setSaldo(seleccion.getSaldo() - importe);

                            F_Cobranza.detalles.add(item);
                            int position = 0;
                            for(int i = 0; i < facturas.size(); i ++) {
                                if(facturas.get(i).getFolio() == seleccion.getFolio() && facturas.get(i).getSerie().equals(seleccion.getSerie()))
                                    position = i;
                            }

                            seleccion.setSaldo(item.getSaldo());
                            F_Cobranza.facturas.set(position, seleccion);

                            //Actualizar LIsta
                            F_Cobranza.lstPagos.setAdapter(new Adapter_Pago(context, F_Cobranza.detalles));

                            //Actualizar Totales
                            UpdateImportes();
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

    //Validar que el producto no este agregado
    public boolean isAdd(Factura item) {
        boolean is = false;

        for(Det_Pago pos : F_Cobranza.detalles) {
            if(pos.getSerie().equals(item.getSerie()) && pos.getFolio() == item.getFolio())
                is = true;
        }
        return is;
    }

    //Actualizar Importes Visuales y en la venta
    public void UpdateImportes() {
        double total = 0;

        for(int i = 0; i < F_Cobranza.detalles.size(); i ++) {
            Det_Pago item = F_Cobranza.detalles.get(i);

            total += item.getImporte_pago();
        }

        //Actualizar Totales visuales
        F_Cobranza.txtTotal.setText(FormatNumber(total));
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
