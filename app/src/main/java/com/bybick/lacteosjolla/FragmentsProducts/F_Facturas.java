package com.bybick.lacteosjolla.FragmentsProducts;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
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

import com.bybick.lacteosjolla.Adapters.Adapter_Factura;
import com.bybick.lacteosjolla.Adapters.Adapter_Pago;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Cobranza;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.FacturaOrden;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
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
    ArrayList<FacturaOrden> orden;
    double importe = 0;
    Det_Pago pago;

    String id_pago;

    String spMethodPago;

    ListView lstFacturas;
    EditText editSearch;

    Boolean isEfectivo;

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
//        this.facturas = facturas;
    }

    public void setId_pago(String id_pago){ this.id_pago = id_pago; }

//    public void isMetodo(Boolean i){
//        this.isMetodo = i;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

        facturas = dbd.getFacturas(cliente.getId_cliente());

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

//        if(seleccion.getSaldo() > isAdd(seleccion)){
//            if (!isPayed(seleccion)) {
                //Abrir Dialogo de Cantidad
        if (position == 0) {
            AddCantidad();
            fm.popBackStack();
        } else{
            Toast.makeText(context, "Debes Saldar la factura anterior", Toast.LENGTH_SHORT).show();
        }
//            } else {
//                Toast.makeText(context, "La Factura anterior no esta cubierta", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(context, "La factura ya esta agregada.", Toast.LENGTH_SHORT).show();
//        }
    }

    //Abrir Dialogo de Cantidad
    public void AddCantidad() {
        final double tempImporte = isAdd(seleccion);

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
        txtSaldo.setText("Saldo: $ " + FormatNumber(seleccion.getSaldo() - tempImporte));

        //Cantidad
        final EditText editCantidad = (EditText) view.findViewById(R.id.editCantidad);
//        editCantidad.setText(FormatNumber(seleccion.getSaldo()));

        //Metodo de Pago
        final Spinner spMetodo = (Spinner) view.findViewById(R.id.spinnerMetodo);

        //Banco
        final Spinner spBanco = (Spinner) view.findViewById(R.id.spinnerBanco);

        //Folio
        final EditText editFolio = (EditText) view.findViewById(R.id.editTxtFolio);

//        if (isMetodo == false){
            //Variable del Precio seleccionado
            ArrayList<String> tipos = new ArrayList<String>();
            tipos.add("Efectivo");
            tipos.add("Cheque");
            tipos.add("Tranferencia");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tipos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMetodo.setAdapter(adapter);

            //Bancos
            ArrayAdapter<String> adapterBancos = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.bancos));
            adapterBancos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spBanco.setAdapter(adapterBancos);
//        } else {
//            spMetodo.setVisibility(View.GONE);
//        }

        spMetodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        spBanco.setEnabled(false);
                        editFolio.setEnabled(false);
                        isEfectivo = true;
                        break;
                    default:
                        spBanco.setEnabled(true);
                        editFolio.setEnabled(true);
                        isEfectivo = false;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        //Listener Cantidad
        editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double saldo1 = seleccion.getSaldo() - isAdd(seleccion);

                if (s.length() > 0 && !s.toString().endsWith(".") && Double.parseDouble(editCantidad.getText().toString()) <= saldo1) {

                    importe = Double.parseDouble(editCantidad.getText().toString());
                    double saldo = seleccion.getSaldo() - importe - tempImporte;

                    //Total
                    txtSaldo.setText("Saldo: $ " + FormatNumber(saldo));

                } else {
//                    txtSaldo.setText("Saldo: $ " + FormatNumber(seleccion.getSaldo() - importe));
                    txtSaldo.setText("Saldo: $ 0.00");
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

                        double editCant;
                        editCant = Double.parseDouble(editCantidad.getText().toString());

                        //Si no se mete cantidad
                        if(editCantidad.getText().toString().isEmpty()){
                            Toast.makeText(context, "No se introdujo Importe", Toast.LENGTH_SHORT).show();
                        } else {

                            if (editCant > seleccion.getSaldo() - isAdd(seleccion)){
                                importe = seleccion.getSaldo()  - isAdd(seleccion);
                            }
                            spMethodPago = spMetodo.getSelectedItem().toString();
                            Det_Pago item = new Det_Pago();


                            item.setId_pago(id_pago);
                            item.setId_det_pago(Main.NEWID());
                            item.setSerie(seleccion.getSerie());
                            item.setFolio(seleccion.getFolio());
                            if (!isEfectivo) {
                                item.setBancos(spBanco.getSelectedItem().toString());
                                item.setReferencia(editFolio.getText().toString());
                            } else {
                                item.setBancos("n/a");
                                item.setReferencia("n/a");
                            }
                            item.setForma_pago(spMethodPago);
                            item.setTotal_factura(seleccion.getTotal());
                            item.setImporte_pago(importe);
                            item.setSaldo_ant(seleccion.getSaldo() - isAdd(seleccion));
                            item.setSaldo(seleccion.getSaldo() - importe - isAdd(seleccion));
                            item.setOrden(seleccion.getOrden());

//                            new F_Cobranza().setFacturasMethod(spMethodPago);
                            F_Cobranza.detalles.add(item);
                            int position = 0;
                            for(int i = 0; i < facturas.size(); i ++) {
                                if(facturas.get(i).getFolio() == seleccion.getFolio() && facturas.get(i).getSerie().equals(seleccion.getSerie()))
                                    position = i;
                            }

//                            for(int i = 0; i < facturas.size(); i ++) {
//                                Factura fc = facturas.get(i);
//                                fc.setPagado(true);
//                                dbd.setPagado(fc.getSerie(), fc.getFolio());
//                            }

//                            seleccion.setSaldo(item.getSaldo());
                            F_Cobranza.facturas.set(position, seleccion);

                            //Actualizar LIsta
                            F_Cobranza.lstPagos.setAdapter(new Adapter_Pago(context, F_Cobranza.detalles));

//                            item.getImporte_pago() > item.getSaldo() ||
                            if (editCant + isAdd(seleccion) >= seleccion.getSaldo()){
                                dbd.setPagado(item.getSerie(), item.getFolio());
                            }
                            dbd.setDetPago(item);

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
    public double isAdd(Factura item) {
        double importe;

        importe = dbd.getImporteTotalFactura(item.getSerie(), item.getFolio());

        return importe;
    }
    
    //Validar orden para el pago de facturas viejas
    public boolean isPayed(Factura item){
        boolean is = false;
        ArrayList<FacturaOrden> facturaP = new ArrayList<>();
//        FacturaOrden facturaOrden = new FacturaOrden();
        facturaP = dbd.getFacturasOrden(cliente.getId_cliente(), seleccion.getFolio());

        if (F_Cobranza.detalles.size() > 0) {
            for (Det_Pago pos : F_Cobranza.detalles) {
                if (item.getOrden() - 1 == pos.getOrden()) {
                    if (pos.getSaldo() >= item.getSaldo()) {
                        is = true;
                    }
                }
            }
        } else if (item.getOrden() == 1){
            return is;
        } else{
            for (FacturaOrden facturaOrden : facturaP) {
                if (facturaOrden.getOrden() == item.getOrden() - 1){
                    is = true;
                }
            }
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
