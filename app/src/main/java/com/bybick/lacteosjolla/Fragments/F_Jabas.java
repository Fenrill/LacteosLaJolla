package com.bybick.lacteosjolla.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.bybick.lacteosjolla.Adapters.Adapter_Detalle;
//import com.bybick.lacteosjolla.Adapters.Adapter_Detalle_Jabas;
import com.bybick.lacteosjolla.Adapters.Adapter_Jabas;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsProducts.F_ProductosJabas;
import com.bybick.lacteosjolla.FragmentsProducts.F_ProductosVenta;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectOUT.Det_Jabas;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.ObjectOUT.Jabas;
import com.bybick.lacteosjolla.ObjectOUT.Pago;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class F_Jabas extends Fragment implements AdapterView.OnItemLongClickListener, View.OnClickListener {
    Context context;
    Fragment fragmento;
    ActionBar tb;
    FragmentManager fmMain;
    Visita visita;
    Cliente cliente;
    String location;

    public static Jabas jabas;
    public static ArrayList<Det_Jabas> detalles;

    boolean actualizando;
    public boolean flag;

    //Producto Seleccion
    Producto seleccion;
    Pago tipoPago;
    Det_Pago tipoDetPago;

    FragmentManager fmChild;
    DBConfig dbc;
    DBData dbd;

    //Botones
    FloatingActionButton btnPrint;
    FloatingActionButton btnAdd;
    FloatingActionButton btnEnd;


    //Lista
    RelativeLayout vista;
    public static ListView lstJabas;
    ListView lstProductos;
    ArrayList<Producto> productos;
    ArrayList<Producto> busqueda;
    String MARCA;


    public void setContext(Context context){ this.context = context; }

    public void setTb(ActionBar tb){ this.tb = tb; }

    public void setFmMain(FragmentManager fmMain){ this.fmMain = fmMain; }

    public void setAuxiliares(Visita visita, Cliente cliente){
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_jabas, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tb.setTitle("Jabas");
        Log.e("TES ID VISITA", "ID: " + visita.getId_visita());

        //Mostrar Visita
        vista.setVisibility(RelativeLayout.VISIBLE);

        //Obtener produtos
        productos = dbd.getProductosJabas();

        jabas = dbd.getJabas(visita.getId_visita());

        if (jabas != null){

            //si no se ha enviado se muestra para actualizar
            if (jabas.getEnviado() == 0){

                //sacar detalles
                detalles = jabas.getDet_jabas();

                //sabemos que actualizamos
                actualizando = true;

                //para poder reimprimir (desactivado por ahorita)
//                btnPrint.setVisibility(FloatingActionButton.VISIBLE);

                //actualizamos la lista
                lstJabas.setAdapter(new Adapter_Jabas(context, detalles));

                //actualizar totales
                F_Visita.movimientos = true;

            }
            //si ya se envio se crea uno nuevo
            else{
                actualizando = false;

                //nuevo jabas()
                jabas = new Jabas();

                //detalles nuevos
                detalles = new ArrayList<>();
            }
        }
        else{
            actualizando = false;

            //nuevo jabas()
            jabas = new Jabas();

            //detalles nuevos
            detalles = new ArrayList<>();
        }
     }

    public void getViews(View v){
        //Vistas
        vista = (RelativeLayout) v.findViewById(R.id.Views);

        //Lista
        lstJabas = (ListView) v.findViewById(R.id.lstJabas);
        lstJabas.setOnItemLongClickListener(this);

        //Botones
        btnAdd = (FloatingActionButton) v.findViewById(R.id.addButton);
        btnAdd.setOnClickListener(this);
        btnEnd = (FloatingActionButton) v.findViewById(R.id.endButton);
        btnEnd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addButton : {
                if (!flag){
//                    addjaba()
                    F_ProductosJabas frag = new F_ProductosJabas();
                    frag.setContext(context);
                    frag.setFm(fmMain);
                    frag.setProductos(productos);
                    frag.setAuxiliares(cliente, visita);

                    FragmentTransaction ft = fmMain.beginTransaction();
                    ft.setCustomAnimations(R.animator.enter_up, R.animator.out_up,
                            R.animator.enter_up, R.animator.out_up);
                    ft.add(R.id.Container, frag, "Productos").addToBackStack("Jabas").commit();
                }
            } break;

            case R.id.endButton : {
                FinishDialog();
                if (!flag){
                    btnAdd.setVisibility(View.GONE);
                }
            } break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (actualizando){
            //Mandar abrir dialogo
            MenuDialog(detalles.get(position), position);
        }

        return false;
    }

    public void MenuDialog(final Det_Jabas mod, final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.d_menu, null);
        //boton cancelar
        FloatingActionButton btnCancel = (FloatingActionButton) view.findViewById(R.id.btnHideMenu);
        //boton eliminar
        FloatingActionButton btnDelete = (FloatingActionButton) view.findViewById(R.id.btnDelete);
        //boton modificar
        FloatingActionButton btnEdit = (FloatingActionButton) view.findViewById(R.id.btnEdit);

        //asignar vista al dialogo
        final AlertDialog menu = builder.setView(view).create();
        menu.show();

        ///click cancelar
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.cancel();
            }
        });
        //click eliminar
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detalles.remove(pos);

                //actualizar lista
                lstJabas.setAdapter(new Adapter_Jabas(context, detalles));

                //ocultar dialogo
                menu.cancel();
            }
        });
        btnEdit.setVisibility(View.GONE);
    }

    //agregar cantidad
    public void EditDialog(final Det_Jabas item, final int position){
        //marcarseleccion
        for (Producto prod : productos){
            if (item.getId_producto().equals(prod.getId_producto())){
                seleccion = prod;
            }
        }
        //crear dialogo
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //obtener el infalte para la vista
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //crear la vista del dialogo
        View view = inflater.inflate(R.layout.f_jabas, null);
        //cantidad
        final EditText editCantidad = (EditText) view.findViewById(R.id.editCantidad);
        //two digits
        editCantidad.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(6, 2)});
        if (seleccion.getDecimales() == 1){
            editCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        final Spinner spUnidad = (Spinner) view.findViewById(R.id.spUnidad);
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

    //Terminar Jabas
    public void FinishDialog(){
        //Mostrar dialogo de opciones
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View d = getActivity().getLayoutInflater().inflate(R.layout.d_finventa, null);

        final Switch fact = (Switch) d.findViewById(R.id.swFacturar);
        final Spinner formas = (Spinner) d.findViewById(R.id.spFormas);
        final Spinner pago = (Spinner) d.findViewById(R.id.spFormaPago);
        formas.setVisibility(View.GONE);
        pago.setVisibility(View.GONE);
        fact.setVisibility(View.GONE);

        builder.setView(d)
                .setTitle("Terminar Ingreso de jabas?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            location = Main.getGPS();

                            jabas.setId_visita(visita.getId_visita());
                            jabas.setId_cliente(visita.getId_cliente());
                            jabas.setDet_jabas(detalles);
                            jabas.setLocation(location);

                            if (actualizando){
                                jabas.setDet_jabas(detalles);
                                dbd.UpdateJabas(jabas);
                            } else{
                                dbd.setJabas(jabas);
                            }

                            F_Visita.movimientos = true;
                            flag = true;

                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
    }

}
