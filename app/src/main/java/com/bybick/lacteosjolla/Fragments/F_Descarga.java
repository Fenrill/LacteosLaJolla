package com.bybick.lacteosjolla.Fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Actualizar;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Carga;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Equivalencia;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.Formas;
import com.bybick.lacteosjolla.ObjectIN.Lista;
import com.bybick.lacteosjolla.ObjectIN.Listas_Precios;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
//import com.bybick.lacteosjolla.ObjectIN.Producto_Jabas;
import com.bybick.lacteosjolla.ObjectIN.Producto_unidad;
import com.bybick.lacteosjolla.ObjectIN.Promociones;
import com.bybick.lacteosjolla.ObjectIN.Secuencia;
import com.bybick.lacteosjolla.ObjectIN.Serie;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Jornada;
import com.bybick.lacteosjolla.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bicktor on 25/05/2016.
 */
public class F_Descarga extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    Context context;
    ActionBar tb;

    FragmentManager fm;
    DBConfig dbc;
    DBData dbd;

    ArrayList<Lista> data;

    //Vistas
    ListView lstItems;
    FloatingActionButton btnNext;

    //Validar Actualizacione
    boolean Uclientes = false;
    boolean Uproductos = false;
    boolean Ufacturas = false;
    boolean Ugrales = false;

    //Datos a Descargar Clientes
    ArrayList<Secuencia> secuencias;
    ArrayList<Formas> formas;
    ArrayList<Cliente> clientes;
    ArrayList<Listas_Precios> listas;

    //Datos a Descargar Productos
    ArrayList<Producto> productos;
    ArrayList<Carga> carga;
    ArrayList<Unidad> unis;
    ArrayList<Producto_unidad> prounis;
    ArrayList<Promociones> promociones;
//    ArrayList<Producto_Jabas> producto_jabas;
    ArrayList<Equivalencia> equivalencias;

    //Datos a Descargar Facturas
    ArrayList<Factura> facturas;

    //Datos a Descargar Conceptos Generales
    ArrayList<Serie> series;
    ArrayList<Motivo> no_venta;
    ArrayList<Motivo> devoluciones;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        //Inicializar
        secuencias = new ArrayList<>();
        formas = new ArrayList<>();
        clientes = new ArrayList<>();
        listas = new ArrayList<>();

        productos = new ArrayList<>();
        carga = new ArrayList<>();
        unis = new ArrayList<>();
        prounis = new ArrayList<>();
        equivalencias = new ArrayList<>();

        facturas = new ArrayList<>();

        series = new ArrayList<>();
        no_venta = new ArrayList<>();
        devoluciones = new ArrayList<>();

        tb.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_descarga, container, false);
        getViews(v);

        data = new ArrayList<>();

//        data.add(new Lista(R.mipmap.logo_cliente, "Descargar Clientes", "Descarga los datos relacionados al cliente."));
//        data.add(new Lista(R.mipmap.logo_producto, "Descargar Productos", "Descarga los productos a vender."));
//        data.add(new Lista(R.mipmap.logo_factura, "Descargar Facturas", "Descarga las cuentas por pagar de los clientes."));
//        data.add(new Lista(R.mipmap.logo_general, "Descargar Conceptos Grals.", "Descarga Motivos Series y Folios."));
        data.add(new Lista(R.mipmap.logo_cliente, "Cargar Datos", "Realizar la Carga de Datos de Clientes, Productos, Facturas y Datos Generales"));

        lstItems.setAdapter(new Adapter_Actualizar(context, data));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tb.setTitle("Actualizar Listas");
        super.onViewCreated(view, savedInstanceState);
    }

    public void getViews(View v) {
        tb.setTitle("Actualizar Listas");

        lstItems = (ListView) v.findViewById(R.id.lstOptions);
        lstItems.setOnItemClickListener(this);

        btnNext = (FloatingActionButton) v.findViewById(R.id.btnCarga);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
//            Descargar Datos del Cliente
//            case 0 : {
//                new getClientes().execute();
//            }break;
//
//            case 1 : {
//                new getProductos().execute();
//            }break;
//
//            case 2 : {
//                new getFacturas().execute();
//            }break;
//
//            case 3 : {
//                new getGenerales().execute();
//            }break;
            case 0 : {
                new getClientes().execute();
                new getProductos().execute();
                new getFacturas().execute();
                new getGenerales().execute();
            }break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCarga : {
                if(Uclientes &&
                        Uproductos &&
                        Ufacturas &&
                        Ugrales) {

                    Continuar();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.mipmap.ic_error)
                            .setTitle("Atencion")
                            .setMessage("No se han actualizado todas las listas. ¿Deseas Continuar?")
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Continuar();
                                }
                            })
                            .setNegativeButton("Actualizar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();

                }
            }break;
        }
    }

    //Guardar Y mostrar siguiente Fragment
    public void Continuar() {
        AsyncTask Save =new AsyncTask() {
            ProgressDialog pd;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Mostrar Dialogo
                pd = new ProgressDialog(context);
                pd.setIcon(R.mipmap.ic_progress);
                pd.setTitle("Guardando");
                pd.setMessage("Espera un Momento. Se está Guardando la información.");
                pd.setCancelable(false);
                pd.show();

            }

            @Override
            protected Object doInBackground(Object[] params) {
                //Iniciar Jornada
                Jornada inicio = new Jornada();
                inicio.setId_usuario(dbc.getlogin().getId_usuario());
                inicio.setFecha(Main.getFecha());
                inicio.setHora_inicio(Main.getHora());
                //Validar GPS
                if (Main.mylocation != null)
                    inicio.setGps_inicio(Main.mylocation.getLatitude()
                            + "," + Main.mylocation.getLongitude());
                else
                    inicio.setGps_inicio("0.0,0.0");

                //Mandar Inicio de Jornada a la DB
                boolean ini = dbd.setInitJornada(inicio);

                //Guardar Información Descargada
                //Si se inicio Jornada Correctamente
                if (ini) {
                    //Guardar Datos de Clientes
                    //Secuencia
                    dbd.setSecuencia(secuencias);
                    //Formas de Venta
                    dbd.setFormasVenta(formas);
                    //Clientes
                    dbd.setClientes(clientes);
                    //Listas de Precio
                    dbd.setListasPrecios(listas);

                    //Guardar datos de los Productos
                    //Productos
                    dbd.setProductos(productos);
                    //Carga
                    dbd.setCarga(carga);
                    //Unidades
                    dbd.setUnidades(unis);
                    //Producto Unidad
                    dbd.setProductoUnidad(prounis);
                    //Productos Promociones
                    dbd.setProductoPromociones(promociones);
                    //Producto Jabas
//                    dbd.setProductoJabas(producto_jabas);
                    //Equivalencias
                    dbd.setEquivalencias(equivalencias);

                    //Guardar datos de las Facturas
                    //Facturas
                    dbd.setFacturas(facturas);

                    //Guardar datos de Conceptos Generales
                    //Series y Folios
                    dbd.setSeriesFolios(series);

                    //Motivos No Venta
                    dbd.setMotivosNoVenta(no_venta);

                    //Motivos devolucion
                    dbd.setMotivosDevolucion(devoluciones);

                    //Cerrar Dialogo
                    pd.cancel();

                    //Abrir Fragment de Carga
                    F_Carga frag = new F_Carga();
                    frag.setContext(context);
                    frag.setTb(tb);

                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                            R.animator.enter_anim, R.animator.out_anim);
                    ft.replace(R.id.Container, frag).commit();
                } else {
                    Toast.makeText(context, "Error. Intentalo mas tarde.", Toast.LENGTH_SHORT).show();
                    pd.cancel();
                }
                return null;
            }
        }.execute();
    }


    //Convertir lo descargado a JSON
    private String readStream(InputStream in) throws IOException{
        BufferedReader r = null;
        r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        if(r != null){
            r.close();
        }
        in.close();
        Log.e("Result", total.toString());
        return total.toString();
    }

    /***** CLASES PARA DESCARGAR INFORMACION *******/
    //Clase para descargar Clientes y sus datos
    public class getClientes extends AsyncTask {

        ProgressDialog dialog;
        boolean ERROR;
        String ERROR_MSG;

        //datos a enviar
        JSONObject data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = new JSONObject();

            secuencias = new ArrayList<>();
            formas = new ArrayList<>();
            clientes = new ArrayList<>();
            listas = new ArrayList<>();

            dialog = ProgressDialog.show(context, "Descarga de clientes", "Descargando datos. Espera por favor.", true, false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                data.put("fecha", Main.getFecha());
                data.put("id_usuario", dbc.getlogin().getId_usuario());
                String toSend = data.toString();

                URL url = new URL(dbc.getServer() + "Clientes");
                Log.e("Connect", url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setFixedLengthStreamingMode(toSend.getBytes().length);

                //Hacer cabecera HTTP
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Request-With", "XMLHttpRequest");

                //Abrir
                conn.connect();

                //ocnfigurar envio
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(toSend.getBytes());
                //Limpiar
                os.flush();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                String data = readStream(in);

                //Pasar Datos a JSon a Objetos
                JSONArray jsonArray = new JSONArray(data);
                //validar que no se reciba mensaje de Error

                if(jsonArray.length() == 1){
                    Log.e("getClientes", jsonArray.getJSONObject(0).getString("Message"));
                    if(jsonArray.getJSONObject(0).getString("Tipo").equals("SR"))
                        ERROR_MSG = jsonArray.getJSONObject(0).getString("Message");
                    else
                        ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
                    ERROR = true;
                }else {
                    JSONArray jsSecuencia = jsonArray.getJSONArray(0);
                    JSONArray jsFormas = jsonArray.getJSONArray(1);
                    JSONArray jsClientes = jsonArray.getJSONArray(2);
                    JSONArray jsListas = jsonArray.getJSONArray(3);

                    //Recorrer Secuencia de Clientes
                    for (int i = 0; i < jsSecuencia.length(); i++) {
                        JSONObject object = jsSecuencia.getJSONObject(i);

                        //Crear Objeto Secuencia
                        Secuencia item = new Secuencia();
                        item.setId_usuario(object.getString("id_usuario"));
                        item.setFecha(object.getString("fecha"));
                        item.setSecuencia(object.getInt("secuencia"));
                        item.setId_cliente(object.getString("id_cliente"));

                        //Enviar Objeto al ArrayList
                        secuencias.add(item);

                    }

                    //Recorrer Formas de Venta
                    for (int i = 0; i < jsFormas.length(); i++) {
                        JSONObject object = jsFormas.getJSONObject(i);

                        //crear Forma de Venta
                        Formas item = new Formas();
                        item.setId_forma(object.getInt("id_forma"));
                        item.setForma(object.getString("forma"));
                        item.setId_cliente(object.getString("id_cliente"));

                        //Enviar al ArrayList
                        formas.add(item);

                    }

                    //Recorrer Clientes
                    for (int i = 0; i < jsClientes.length(); i++) {
                        JSONObject object = jsClientes.getJSONObject(i);

                        //Crera Cliente
                        Cliente item = new Cliente();
                        item.setId_cliente(object.getString("id_cliente"));
                        item.setNombre(object.getString("nombre"));
                        item.setRfc(object.getString("rfc"));
                        item.setRazon_social(object.getString("razon_social"));
                        item.setForma_venta(object.getString("forma_venta"));
                        item.setId_forma(object.getInt("id_forma_venta"));

                        //Enviar al ArrayList
                        clientes.add(item);
                    }

                    //Recorrer Listas de Precios
                    for (int i = 0; i < jsListas.length(); i++) {
                        JSONObject object = jsListas.getJSONObject(i);

                        //Crear lista
                        Listas_Precios item = new Listas_Precios();
                        item.setTipo_lista(object.getString("tipo_lista"));
                        item.setLista(object.getString("lista"));
                        item.setId_cliente(object.getString("id_cliente"));
                        item.setId_producto(object.getString("id_producto"));
                        item.setSub_precio(object.getDouble("sub_precio"));
                        item.setImpuestos(object.getDouble("impuestos"));
                        item.setPrecio(object.getDouble("precio"));
                        item.setId_unidad(object.getString("id_unidad"));

                        //ArrayList
                        listas.add(item);
                    }
                }

                //Cerrar Conexion
                conn.disconnect();
            } catch (MalformedURLException e) {
                Log.e("getClientes", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
            } catch (IOException e) {
                Log.e("getClientes", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al conectar con el servidor. Verifica tu conexión.";
            } catch (JSONException e) {
                Log.e("getClientes", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al descargar los datos";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(ERROR) {
                dialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_error)
                        .setTitle("Error")
                        .setMessage(ERROR_MSG)
                        .create()
                        .show();
                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                    ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
                }

            }else {
                Uclientes = true;
                dialog.cancel();
/*
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        dialog.cancel();
                    }
                }).start();
*/
            }
        }
    }

    //Clase para descargar productos y mas
    public class getProductos extends AsyncTask {
        ProgressDialog dialog;
        boolean ERROR;
        String ERROR_MSG;

        //datos a enviar
        JSONObject data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = new JSONObject();

            productos = new ArrayList<>();
            carga = new ArrayList<>();
            unis = new ArrayList<>();
            prounis = new ArrayList<>();
            promociones = new ArrayList<>();
//            producto_jabas = new ArrayList<>();
            equivalencias = new ArrayList<>();

            dialog = ProgressDialog.show(context, "Descarga de Productos", "Descargando datos. Espera por favor.", true, false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                data.put("fecha", Main.getFecha());
                data.put("id_usuario", dbc.getlogin().getId_usuario());
                String toSend = data.toString();

                URL url = new URL(dbc.getServer() + "Productos");
                Log.e("Connect", url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setFixedLengthStreamingMode(toSend.getBytes().length);

                //Hacer cabecera HTTP
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Request-With", "XMLHttpRequest");

                //Abrir
                conn.connect();

                //ocnfigurar envio
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(toSend.getBytes());
                //Limpiar
                os.flush();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                String data = readStream(in);

                //Pasar Datos a JSon a Objetos
                JSONArray jsonArray = new JSONArray(data);
                //validar que no se reciba mensaje de Error

                if(jsonArray.length() == 1){
                    Log.e("getProductos", jsonArray.getJSONObject(0).getString("Message"));
                    if(jsonArray.getJSONObject(0).getString("Tipo").equals("SR"))
                        ERROR_MSG = jsonArray.getJSONObject(0).getString("Message");
                    else
                        ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
                    ERROR = true;
                }else {
                    JSONArray jsProductos = jsonArray.getJSONArray(0);
                    JSONArray jsCarga = jsonArray.getJSONArray(1);
                    JSONArray jsUnidades = jsonArray.getJSONArray(2);
                    JSONArray jsProU = jsonArray.getJSONArray(3);
                    JSONArray jsPromociones = jsonArray.getJSONArray(4);
//                    JSONArray jsProducto_jabas = jsonArray.getJSONArray(5);
                    JSONArray jsEq = jsonArray.getJSONArray(5);

                    //Recorrer Secuencia de Productos
                    for (int i = 0; i < jsProductos.length(); i++) {
                        JSONObject object = jsProductos.getJSONObject(i);

                        //Crear Objeto Secuencia
                        Producto item = new Producto();
                        item.setId_producto(object.getString("id_producto"));
                        item.setDescripcion(object.getString("descripcion"));
                        item.setMarca(object.getString("marca"));
                        item.setFamilia(object.getString("familia"));
                        item.setDecimales(object.getBoolean("decimales") ? 1 : 0);
                        item.setOrden(object.getInt("orden"));

                        //Enviar Objeto al ArrayList
                        productos.add(item);

                    }

                    //Recorrer Cargas
                    for (int i = 0; i < jsCarga.length(); i++) {
                        JSONObject object = jsCarga.getJSONObject(i);

                        //crear Forma de Venta
                        Carga item = new Carga();
                        item.setId_usuario(object.getString("id_usuario"));
                        item.setId_producto(object.getString("id_producto"));
                        item.setFecha(object.getString("fecha"));
                        item.setMarca(object.getString("marca"));
                        item.setCantidad(object.getDouble("cantidad"));
                        item.setId_unidad(object.getString("id_unidad"));
                        item.setPiezas(object.getInt("cantidadpza"));

                        //Enviar al ArrayList
                        carga.add(item);

                    }

                    //Recorrer Unidadaes
                    for (int i = 0; i < jsUnidades.length(); i++) {
                        JSONObject object = jsUnidades.getJSONObject(i);

                        //Crera Cliente
                        Unidad item = new Unidad();
                        item.setId_unidad(object.getString("id_unidad"));
                        item.setUnidad(object.getString("unidad"));

                        //Enviar al ArrayList
                        unis.add(item);
                    }

                    //Recorrer Productos Unidad
                    for (int i = 0; i < jsProU.length(); i++) {
                        JSONObject object = jsProU.getJSONObject(i);

                        //Crear lista
                        Producto_unidad item = new Producto_unidad();
                        item.setId_producto(object.getString("id_producto"));
                        item.setId_unidad(object.getString("id_unidad"));
                        item.setConversion(object.getDouble("conversion"));
                        item.setMinima(object.getInt("minima"));

                        //ArrayList
                        prounis.add(item);
                    }

                    //Recorrer Promociones
                    for (int i = 0; i < jsPromociones.length(); i++){
                        JSONObject object = jsPromociones.getJSONObject(i);

                        //Crear lista
                        Promociones item = new Promociones();
                        item.setId_cliente(object.getString("id_cliente"));
                        item.setNombre(object.getString("nombre_promo"));
                        item.setProducto_venta(object.getString("producto_venta"));
                        item.setProducto_entrega(object.getString("producto_entrega"));
                        item.setCantidad_venta(object.getDouble("cantidad_venta"));
                        item.setCantidad_entrega(object.getDouble("cantidad_entrega"));

                        //ArrayList
                        promociones.add(item);
                    }

                    //Recorrer Productos Jabas
//                    for (int i = 0; i < jsProducto_jabas.length(); i++){
//                        JSONObject object = jsProducto_jabas.getJSONObject(i);
//
//                        //Crear lista
//                        Producto_Jabas item = new Producto_Jabas();
//                        item.setId_producto_jabas(object.getString("id_producto_jabas"));
//                        item.setDescripcion(object.getString("descripcion"));
//                        item.setOrden(object.getInt("orden"));
//
//                        //ArrayList
//                        producto_jabas.add(item);
//                    }
//
                    //Recorrer Equivalencias
                    for (int i = 0; i < jsEq.length(); i++) {
                        JSONObject object = jsEq.getJSONObject(i);

                        //Crear lista
                        Equivalencia item = new Equivalencia();
                        item.setId_producto(object.getString("id_producto"));
                        item.setId_producto_equivalente(object.getString("id_producto_equivalente"));

                        //ArrayList
                        equivalencias.add(item);
                    }

                }

                //Cerrar Conexion
                conn.disconnect();
            } catch (MalformedURLException e) {
                Log.e("getProductos", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
            } catch (IOException e) {
                Log.e("getProductos", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al conectar con el servidor. Verifica tu conexión.";
            } catch (JSONException e) {
                Log.e("getProductos", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al descargar los datos";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(ERROR) {
                dialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_error)
                        .setTitle("Error")
                        .setMessage(ERROR_MSG)
                        .create()
                        .show();
            }else {
                Uproductos = true;
                dialog.cancel();
            }
        }
    }

    //Clas epara descargar Facturas
    public class getFacturas extends AsyncTask {
        ProgressDialog dialog;
        boolean ERROR;
        String ERROR_MSG;

        //datos a enviar
        JSONObject data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = new JSONObject();

            facturas = new ArrayList<>();

            dialog = ProgressDialog.show(context, "Descarga de Facturas", "Descargando datos. Espera por favor.", true, false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                data.put("id_usuario", dbc.getlogin().getId_usuario());
                String toSend = data.toString();

                URL url = new URL(dbc.getServer() + "Facturas");
                Log.e("Connect", url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setFixedLengthStreamingMode(toSend.getBytes().length);

                //Hacer cabecera HTTP
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Request-With", "XMLHttpRequest");

                //Abrir
                conn.connect();

                //ocnfigurar envio
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(toSend.getBytes());
                //Limpiar
                os.flush();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                String data = readStream(in);

                //Pasar Datos a JSon a Objetos
                JSONArray jsonArray = new JSONArray(data);
                //validar que no se reciba mensaje de Error

                if(jsonArray.length() == 1){
                    Log.e("getFacturas", jsonArray.getJSONObject(0).getString("Message"));
                    if(jsonArray.getJSONObject(0).getString("Tipo").equals("SR"))
                        ERROR_MSG = jsonArray.getJSONObject(0).getString("Message");
                    else
                        ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
                    ERROR = true;
                }else {
                    JSONArray jsFacturas = jsonArray.getJSONArray(0);

                    //Recorrer Secuencia de Clientes
                    for (int i = 0; i < jsFacturas.length(); i++) {
                        JSONObject object = jsFacturas.getJSONObject(i);

                        //Crear Objeto Secuencia
                        Factura item = new Factura();
                        item.setId_cliente(object.getString("id_cliente"));
                        item.setId_usuario(object.getString("id_usuario"));
                        item.setFecha(object.getString("fecha"));
                        item.setSerie(object.getString("serie"));
                        item.setFolio(object.getInt("folio"));
                        item.setTotal(object.getDouble("total"));
                        item.setSaldo(object.getDouble("saldo"));

                        //Enviar Objeto al ArrayList
                        facturas.add(item);

                    }

                }

                //Cerrar Conexion
                conn.disconnect();
            } catch (MalformedURLException e) {
                Log.e("getFacturas", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
            } catch (IOException e) {
                Log.e("getFacturas", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al conectar con el servidor. Verifica tu conexión.";
            } catch (JSONException e) {
                Log.e("getFacturas", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al descargar los datos";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(ERROR) {
                dialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_error)
                        .setTitle("Error")
                        .setMessage(ERROR_MSG)
                        .create()
                        .show();
            }else {
                Ufacturas = true;
                dialog.cancel();
            }
        }

    }

    //Clas epara descargar Conceptos Generales
    public class getGenerales extends AsyncTask {

        ProgressDialog dialog;
        boolean ERROR;
        String ERROR_MSG;

        //datos a enviar
        JSONObject data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = new JSONObject();

            series = new ArrayList<>();
            no_venta = new ArrayList<>();
            devoluciones = new ArrayList<>();

            dialog = ProgressDialog.show(context, "Descarga de Conceptos", "Descargando datos. Espera por favor.", true, false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                data.put("id_usuario", dbc.getlogin().getId_usuario());
                String toSend = data.toString();

                URL url = new URL(dbc.getServer() + "Conceptos");
                Log.e("Connect", url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setFixedLengthStreamingMode(toSend.getBytes().length);

                //Hacer cabecera HTTP
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Request-With", "XMLHttpRequest");

                //Abrir
                conn.connect();

                //ocnfigurar envio
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(toSend.getBytes());
                //Limpiar
                os.flush();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                String data = readStream(in);

                //Pasar Datos a JSon a Objetos
                JSONArray jsonArray = new JSONArray(data);
                //validar que no se reciba mensaje de Error

                if(jsonArray.length() == 1){
                    Log.e("getGenerales", jsonArray.getJSONObject(0).getString("Message"));
                    if(jsonArray.getJSONObject(0).getString("Tipo").equals("SR"))
                        ERROR_MSG = jsonArray.getJSONObject(0).getString("Message");
                    else
                        ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
                    ERROR = true;
                }else {
                    JSONArray jsSeries = jsonArray.getJSONArray(0);
                    JSONArray jsNoVentas = jsonArray.getJSONArray(1);
                    JSONArray jsDevoluciones = jsonArray.getJSONArray(2);

                    //Recorrer Series
                    for (int i = 0; i < jsSeries.length(); i++) {
                        JSONObject object = jsSeries.getJSONObject(i);

                        //Crear Objeto Seerie
                        Serie item = new Serie();
                        item.setId_usuario(object.getString("id_usuario"));
                        item.setSerie(object.getString("serie"));
                        item.setTipo(object.getString("tipo"));
                        item.setFolio(object.getInt("folio"));
                        item.setEmpresa(object.getString("empresa"));

                        //Enviar Objeto al ArrayList
                        series.add(item);

                    }

                    //Recorrer Motivos No Venta
                    for (int i = 0; i < jsNoVentas.length(); i++) {
                        JSONObject object = jsNoVentas.getJSONObject(i);

                        //crear Motivo No Venta
                        Motivo item = new Motivo();
                        item.setId_motivo(object.getInt("id_motivo"));
                        item.setDescripcion(object.getString("descripcion"));

                        //Enviar al ArrayList
                        no_venta.add(item);

                    }

                    //Recorrer Motivos Devolucion
                    for (int i = 0; i < jsDevoluciones.length(); i++) {
                        JSONObject object = jsDevoluciones.getJSONObject(i);

                        //Crera Cliente
                        Motivo item = new Motivo();
                        item.setId_motivo(object.getInt("id_motivo"));
                        item.setDescripcion(object.getString("descripcion"));

                        //Enviar al ArrayList
                        devoluciones.add(item);
                    }

                }

                //Cerrar Conexion
                conn.disconnect();
            } catch (MalformedURLException e) {
                Log.e("getGenerales", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
            } catch (IOException e) {
                Log.e("getGenerales", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al conectar con el servidor. Verifica tu conexión.";
            } catch (JSONException e) {
                Log.e("getGenerales", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al descargar los datos";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(ERROR) {
                dialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_error)
                        .setTitle("Error")
                        .setMessage(ERROR_MSG)
                        .create()
                        .show();
            }else {
                Ugrales = true;
                dialog.cancel();
            }
        }
    }

}
