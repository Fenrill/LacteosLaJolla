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
import com.bybick.lacteosjolla.Adapters.Adapter_Pago;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsProducts.F_Facturas;
import com.bybick.lacteosjolla.FragmentsProducts.F_ProductosVenta;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Serie;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Forma_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Pago;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.Printers.P_Devolucion;
import com.bybick.lacteosjolla.Printers.P_Pago;
import com.bybick.lacteosjolla.Printers.P_Venta;
import com.bybick.lacteosjolla.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bicktor on 13/07/2016.
 */
public class F_Cobranza extends Fragment implements View.OnClickListener,
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

    public static Pago pago;
    public static ArrayList<Det_Pago> detalles;

    //Data Auxiliar
    //Totales Globales

    //Totales x Detalle
    public static double total;

    boolean actualizando;

    //Producto Seleccion
    Det_Pago seleccion;
    double importe = 0;

    //Vistas
    //Botones
    FloatingActionButton btnAdd;
    FloatingActionButton btnEnd;
    FloatingActionButton btnPrint;

    //Lista
    RelativeLayout vista;
    public static ListView lstPagos;

    public static ArrayList<Factura> facturas;

    //Textos
    public static TextView txtTotal;

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

        dbc = new DBConfig(context);
        dbc.open();

        dbd = new DBData(context);
        dbd.open();

        facturas = new ArrayList<>();
        facturas = dbd.getFacturas(cliente.getId_cliente());

        fragmento = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_cobranza, container, false);
        getViews(v);

        return v;
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        //Enviar Titulo a la Barra de Titulo
        tb.setTitle("Cobranza");

        //Validar Existencia de Pagos
        pago = dbd.getPago(visita.getId_visita());
        if (pago != null) {

            //Si no se ha enviado se muestra para Actualizar
            if (pago.getEnviado() == 0) {
                //Sacar Detalles
                detalles = pago.getDetalles();

                //Estamos actualizando
                actualizando = true;

                //POder Reimprimir
                btnPrint.setVisibility(FloatingActionButton.VISIBLE);

                //Actualizar Lista

                lstPagos.setAdapter(new Adapter_Pago(context, detalles));

                //Actualizar Totales
                UpdateImportes();

                //Se realizo Moviemnto
                F_Visita.movimientos = true;


            }
            //Si ya se envio se crea una venta
            else {
                actualizando = false;

                //Venta Nueva
                pago = new Pago();

                //Detalles Nuevos
                detalles = new ArrayList<>();
            }

        } else {
            actualizando = false;

            //Venta Nueva
            pago = new Pago();

            //Detalles Nuevos
            detalles = new ArrayList<>();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void getViews(View v) {
        //Vista
        vista = (RelativeLayout) v.findViewById(R.id.Views);

        //Lista
        lstPagos = (ListView) v.findViewById(R.id.lstPagos);
        lstPagos.setOnItemLongClickListener(this);

        //Botones
        btnAdd = (FloatingActionButton) v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnPrint = (FloatingActionButton) v.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);
        btnEnd = (FloatingActionButton) v.findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(this);

        //Textos
        txtTotal = (TextView) v.findViewById(R.id.txtTotal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd : {
                //AddProduct();
                F_Facturas frag = new F_Facturas();
                frag.setContext(context);
                frag.setFm(fmMain);
                frag.setFacturas(facturas);
                frag.setAuxiliares(cliente, visita);

                FragmentTransaction ft = fmMain.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                        R.animator.enter_up, R.animator.out_up);
                ft.add(R.id.Container, frag, "Productos").addToBackStack("Pago").commit();

            }break;

            case R.id.btnEnd : {
                facturas = dbd.getFacturas(cliente.getId_cliente());

                if(detalles.size() > 0) {
                    AlertDialog.Builder bul = new AlertDialog.Builder(context);
                    bul.setTitle("Terminar Cambio")
                            .setIcon(R.mipmap.ic_alert)
                            .setMessage("¿Seguro de terminar el Movimiento?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FinshDialog();
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
                } else {
                    Toast.makeText(context, "Agrgea pagos.", Toast.LENGTH_SHORT).show();
                }
            }break;

            case R.id.btnPrint : {
/*
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
                ft.replace(R.id.ContainerVisita, ticket).addToBackStack("Visita").commit();
*/
            }break;
        }
    }

    //Para abrir el Menu
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        //Mandar abrir dialogo con el detalle seleccionado
        MenuDialog(detalles.get(position), position);

        return true;
    }


    //Editar Cantidad
    public void EditDialog(final Det_Pago item, final int position) {
        //Marcar la seleccion
        for(Det_Pago prod : detalles) {
            if(item.getSerie().equals(prod.getSerie()) && item.getFolio() == prod.getFolio())
                seleccion = prod;
        }
        //Crear Dialogo
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //Obtener el inflate para inflar la vista
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Crear la vista del dialogo
        View view = inflater.inflate(R.layout.d_pago, null);
        //Obtener Vistas

        //Total
        final TextView txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        for(int i = 0; i < facturas.size(); i ++) {
            Factura fc = facturas.get(i);
            if(fc.getFolio() == seleccion.getFolio() && fc.getSerie().equals(seleccion.getSerie()))
                txtTotal.setText("Total: $ " + FormatNumber(fc.getTotal()));
        }

        //Saldo
        final TextView txtSaldo = (TextView) view.findViewById(R.id.txtSaldo);
        txtSaldo.setText("Saldo: $ " + FormatNumber(seleccion.getSaldo()));

        //Cantidad
        final EditText editCantidad = (EditText) view.findViewById(R.id.editCantidad);

        //Comentarios
        final EditText editComentario = (EditText) view.findViewById(R.id.editComentario);
        editComentario.setText(seleccion.getComentarios());

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

        //Marcar Tipo
        for(int i = 0; i < tipos.size(); i ++) {
            if(tipos.get(i).equals(seleccion.getForma_pago()))
                spTipo.setSelection(i);
        }

        //Listener Cantidad
        editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !s.toString().endsWith(".")) {

                    importe = Double.parseDouble(editCantidad.getText().toString());
                    double saldo = seleccion.getSaldo_ant() - importe;

                    //Total
                    txtSaldo.setText("Saldo: $ " + FormatNumber(saldo));

                } else {
                    txtSaldo.setText("Saldo: $ " + seleccion.getSaldo_ant());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editCantidad.setText("" + seleccion.getImporte_pago());

        //Mostrar Dialogo
        alert.setView(view)
                .setTitle(seleccion.getSerie() + " - " + seleccion.getFolio())
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Si no me mete cantidad
                        if(editCantidad.getText().toString().isEmpty()){
                            Toast.makeText(context, "No se introdujo Importe", Toast.LENGTH_SHORT).show();
                        } else {

                            item.setComentarios(editComentario.getText().toString());
                            item.setForma_pago(spTipo.getSelectedItem().toString());
                            item.setImporte_pago(importe);
                            item.setSaldo(seleccion.getSaldo() - importe);

                            detalles.set(position, item);

                            //Actualizar LIsta
                            lstPagos.setAdapter(new Adapter_Pago(context, detalles));

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

    //Abrir Menu
    public void MenuDialog(final Det_Pago mod, final int pos) {
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
                lstPagos.setAdapter(new Adapter_Pago(context, detalles));

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
        if(!actualizando) {
            pago.setFecha(Main.getFecha());
            pago.setId_visita(visita.getId_visita());
            pago.setEnviado(0);
            pago.setId_cliente(cliente.getId_cliente());
            pago.setDetalles(detalles);

            //Guardar Cambio a la DB
            dbd.setPago(pago);

            //Imprimir Pago
            P_Pago print = new P_Pago(context, pago, cliente);

            //Mostrar Ticket
            F_Ticket ticket = new F_Ticket();
            ticket.setContext(context);
            ticket.setTb(tb);
            ticket.setTicket(print.imprimir());
            ticket.setTitle("Cobranza");
            ticket.setPrint(print.getBT());

            //Option 2
            FragmentTransaction ft = fmMain.beginTransaction();
            ft.replace(R.id.ContainerVisita, ticket);

            ft.addToBackStack("Cobranza");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            //Se realizo movimiento
            F_Visita.movimientos = true;

        } else {
            pago.setDetalles(detalles);

            //Actualizar Pago
            dbd.UpdatePago(pago);

            //Imprimir Pago

            P_Pago print = new P_Pago(context, pago, cliente);

            //Mostrar Ticket
            F_Ticket ticket = new F_Ticket();
            ticket.setContext(context);
            ticket.setTb(tb);
            ticket.setTicket(print.imprimir());
            ticket.setTitle("Cobranza");
            ticket.setPrint(print.getBT());

            //Option 2
            FragmentTransaction ft = fmMain.beginTransaction();
            ft.replace(R.id.ContainerVisita, ticket);

            ft.addToBackStack("Cobranza");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();


        }

    }

    //Actualizar Importes Visuales y en la venta
    public void UpdateImportes() {
        double total = 0;

        for(int i = 0; i < detalles.size(); i ++) {
            Det_Pago item = detalles.get(i);

            total += item.getImporte_pago();
        }

        //Actualizar Totales visuales
        txtTotal.setText(FormatNumber(total));
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
