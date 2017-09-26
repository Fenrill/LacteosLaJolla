package com.bybick.lacteosjolla.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.Adapter_Actualizar;
import com.bybick.lacteosjolla.Adapters.Adapter_Result;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Devoluciones;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Efectividad;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Ventas;
import com.bybick.lacteosjolla.FragmentsReportes.FR_Visitas;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.Lista;
import com.bybick.lacteosjolla.ObjectOUT.Jornada;
import com.bybick.lacteosjolla.ObjectView.Result;
import com.bybick.lacteosjolla.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by bicktor on 19/07/2016.
 */
public class F_Enviar extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    Context context;
    ActionBar tb;

    FragmentManager fm;
    FragmentManager fmChild;
    DBConfig dbc;
    DBData dbd;

    ArrayList<Lista> opciones;

    //Vistas
    ListView lstItems;
    FloatingActionButton btnNext;

    //Informacion a Enviar
    JSONObject data_send;
    boolean finalizar = false;


    public void setContext(Context context) {
        this.context = context;
    }


    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fmChild = getChildFragmentManager();

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();

        tb.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_descarga, container, false);
        getViews(v);

        opciones = new ArrayList<>();

        opciones.add(new Lista(R.mipmap.ic_enviar, "Enviar Información", "Envia la información del dispositivo al Servidor."));
        opciones.add(new Lista(R.mipmap.ic_enviar, "Respaldar Base de Datos", "Realiza un respaldo de la información para efectos de seguridad."));
        lstItems.setAdapter(new Adapter_Actualizar(context, opciones));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tb.setTitle("Información");
    }


    public void getViews(View v) {

        lstItems = (ListView) v.findViewById(R.id.lstOptions);
        lstItems.setOnItemClickListener(this);

        btnNext = (FloatingActionButton) v.findViewById(R.id.btnCarga);
        btnNext.setVisibility(FloatingActionButton.GONE);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            //Descargar Datos del Cliente
            case 0 : {
                AlertDialog.Builder bul = new AlertDialog.Builder(context);
                bul.setTitle("Enviar Datos")
                        .setIcon(R.mipmap.ic_alert)
                        .setMessage("¿Terminar la Jornada?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Jornada fin = new Jornada();
                                fin.setId_jornada(dbd.getJornada(dbc.getlogin().getId_usuario()).getId_jornada());
                                fin.setHora_fin(Main.getHora());
                                fin.setGps_fin(Main.getGPS());
                                fin.setBateria_fin(Main.batteryLevel(context));

                                //Marcar Fin de Jornada
                                //dbd.setFinJornada(fin);

                                //Obtener Datos de la DB
                                finalizar = true;
                                data_send = dbd.getData(true, dbc.getlogin().getId_usuario());
                                //Enviar
                                new setInfo().execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Obtener Datos de la DB
                                finalizar = false;
                                data_send = dbd.getData(false, dbc.getlogin().getId_usuario());
                                //Enviar Info
                                new setInfo().execute();
                            }
                        })
                        .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }break;

            case 1 : {
                boolean sdDisponible = false;
                boolean sdAcceso = false;

                //Comprobamos estado de la memoria externa
                String estado = Environment.getExternalStorageState();

                if(estado.equals(Environment.MEDIA_MOUNTED)){
                    try {
                        File file = Environment.getExternalStorageDirectory();
                        Calendar c = Calendar.getInstance(TimeZone.getDefault());
                        File f = new File(file, "JollaDB"
                                + (c.getTime().getYear() + 1900)
                                + c.getTime().getDate()
                                + "_"
                                + c.getTime().getHours()
                                + c.getTime().getMinutes());

                        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));

                        //Abrimos el fichero de base de datos como entrada
                        InputStream myInput = new FileInputStream("/data/data/com.bybick.lacteosjolla/databases/Jolla_dbD");

                        //Ruta a la base de datos vacía recién creada
                        //String outFileName =  + DB_NAME;

                        //Abrimos la base de datos vacía como salida
                        OutputStream myOutput = new FileOutputStream(f);

                        //Transferimos los bytes desde el fichero de entrada al de salida
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = myInput.read(buffer))>0){
                            myOutput.write(buffer, 0, length);
                        }

                        //Liberamos los streams
                        myOutput.flush();
                        myOutput.close();
                        myInput.close();

                        Toast.makeText(context,"Base de datos Respaldada", Toast.LENGTH_SHORT).show();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("FileNotFond", e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("IO", e.getMessage());
                    }

                }else if(estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
                    Toast.makeText(context,"SD no disponible",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"SD no disponible",Toast.LENGTH_SHORT).show();
                }
            }break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCarga : {

            }break;
        }
    }


    public class setInfo extends AsyncTask {
        ProgressDialog dialog;
        boolean ERROR;
        String ERROR_MSG;

        //datos a enviar
        JSONObject info;
        JSONArray result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            info = new JSONObject();

            dialog = ProgressDialog.show(context, "Enviando", "Enviando información, espera un momento.", true, false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{
                info.put("Data", data_send);
                String toSend = info.toString();

                URL url = new URL(dbc.getServer() + "Carga");
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
                result = new JSONArray(data);

                //Cerrar Conexion
                conn.disconnect();
        } catch (MalformedURLException e) {
            Log.e("setInfo", e.getMessage());
            ERROR = true;
            ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
        } catch (IOException e) {
            Log.e("setInfo", e.getMessage());
            ERROR = true;
            ERROR_MSG = "Error al conectar con el servidor. Verifica tu conexión.";
        } catch (JSONException e) {
            Log.e("setInfo", e.getMessage());
            ERROR = true;
            ERROR_MSG = "Error al respuesta del servidor.";
        }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dialog.cancel();
            if(ERROR) {
                AlertDialog.Builder bul = new AlertDialog.Builder(context);
                bul.setIcon(R.mipmap.ic_error)
                        .setTitle("Error")
                        .setMessage(ERROR_MSG)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            } else {

                final ArrayList<Result> infodata = new ArrayList<>();
                //Recorrer Respuesta
                for(int i = 0; i < result.length(); i ++) {
                    try {
                        JSONObject object = result.getJSONObject(i);
                        Result item = new Result();

                        item.setNombre(object.getString("Name"));
                        item.setCode(object.getInt("Code"));
                        item.setMsg(object.getString("Message"));
                        item.setOk(object.getInt("Ok"));
                        item.setFail(object.getInt("Fail"));
                        item.setAll(item.getOk() + item.getFail());
                        item.setCorrectos(object.getJSONArray("Items"));

                        infodata.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                AlertDialog.Builder bul = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.d_productos, null);

                EditText edit = (EditText) view.findViewById(R.id.editSearch);
                edit.setVisibility(EditText.GONE);

                ListView lst = (ListView) view.findViewById(R.id.lstProductos);
                lst.setAdapter(new Adapter_Result(context, infodata));

                bul.setView(view)
                        .setTitle("Resultado")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(finalizar) {
                                    dbd.clearDB(infodata);

                                    for(int i = 0; i < fm.getBackStackEntryCount(); i ++) {
                                        fm.popBackStack();
                                    }

                                    tb.hide();
                                    F_Login frag = new F_Login();
                                    frag.setContext(context);
                                    frag.setTb(tb);

                                    fm.beginTransaction().replace(R.id.Container, frag, "Login").commit();
                                }else {
                                    dbd.clearDB(infodata);
                                }
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
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

}
