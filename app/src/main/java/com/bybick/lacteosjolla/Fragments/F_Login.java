package com.bybick.lacteosjolla.Fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Contraseña;
import com.bybick.lacteosjolla.ObjectIN.Usuario;
import com.bybick.lacteosjolla.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bicktor on 24/05/2016.
 */
public class F_Login extends Fragment implements View.OnClickListener {
    Context context;
    ActionBar tb;

    FragmentManager fm;
    DBConfig dbc;
    DBData dbd;

    //Vistas Login
    EditText editUsuario;
    EditText editContraseña;
    Button btnLogin;

    FloatingActionButton btnConfig;
    FloatingActionButton btnUpdate;
    FloatingActionButton btnDeleteData;

    //Array de Usuarios
    ArrayList<Usuario> usuarios;

    //Bluetooth
    BluetoothAdapter adapter;

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

        dbc = new DBConfig(context);
        dbc.open();

        dbd = new DBData(context);
        dbd.open();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_login, container, false);
        getViews(v);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(tb.isShowing()) {
            tb.hide();
            tb.setTitle("");
        }
    }

    public void getViews(View v){
        //Login
        editUsuario = (EditText) v.findViewById(R.id.editUser);
        editContraseña = (EditText) v.findViewById(R.id.editPass);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

        //Config Updtae
        btnConfig = (FloatingActionButton) v.findViewById(R.id.btnConfigurar);
        btnConfig.setOnClickListener(this);
        btnUpdate = (FloatingActionButton) v.findViewById(R.id.btnActualizar);
        btnUpdate.setOnClickListener(this);
        btnDeleteData = (FloatingActionButton) v.findViewById(R.id.btnDeleteDatos);
        btnDeleteData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Si se presiona el boton de configurar
            case R.id.btnConfigurar : {
                onCedisDialog();
            }break;
            //Si se presiona el boton de Actualizar
            case R.id.btnActualizar : {
                new getUsuarios().execute();
            }break;
            //Si se presiona el boton de login
            case R.id.btnLogin : {
                if(dbc.setLogin(editUsuario.getText().toString(), editContraseña.getText().toString())){
                    //Limpiar DB de ventas fantasma
                    dbd.deleteNoVisitas();

                    //Si no hay Jornada
                    if(dbd.ValidarInicio(editUsuario.getText().toString())){
                        F_Descarga frag = new F_Descarga();
                        frag.setContext(context);
                        frag.setTb(tb);

                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                                R.animator.enter_anim, R.animator.out_anim);
                        ft.replace(R.id.Container, frag).commit();

                        //Si hay Jornada
                    }else{
                        //Abrir Listado de Clientes
                        F_Clientes frag = new F_Clientes();
                        frag.setContext(context);
                        frag.setTb(tb);

                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim,
                                R.animator.enter_anim, R.animator.out_anim);
                        ft.replace(R.id.Container, frag).commit();

                    }

                }else{
                    Toast.makeText(context, "Usuario y/o Contraseña Incorrecto", Toast.LENGTH_SHORT).show();
                    editContraseña.setText("");
                }
            }break;
            case R.id.btnDeleteDatos : {
                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.d_password, null);
                    final EditText editPass = (EditText) v.findViewById(R.id.editPass);
                    builder.setTitle("Password")
                            .setIcon(R.mipmap.ic_alert)
                            .setView(v)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Comprobar COntraseña para abrir Config
                                    String pass = editPass.getText().toString();
                                    if(dbc.isModule(pass, "herramienta")) {
                                        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
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
            }
        }
    }

    //Metodo paraAbrir el Dialogo de los CEDIS
    public void onCedisDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = dbc.GET_Cedis();
        builder.setTitle("Elige un CEDIS")
                .setIcon(R.mipmap.ic_launcher)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbc.setServer(items[which]);
                        onBTDialog();
                    }
                })
                .create()
                .show();
    }

    //Metodo para Abrir el Dialogo de Los Dispositivos BT
    public void onBTDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final ArrayList<BluetoothDevice> btDevices = readDevices();
        final String[] items = new String[btDevices.size()];
        //Pasar a String[] para Mostrar
        for(int i = 0; i < btDevices.size(); i ++) {
            items[i] = btDevices.get(i).getName();
        }
        builder.setTitle("Elige un Dispositivo")
                .setIcon(R.mipmap.ic_launcher)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbc.setPrinter(btDevices.get(which));
                    }
                })
                .setPositiveButton("Saltar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    //Obtener Dispositivos BT
    public ArrayList<BluetoothDevice> readDevices(){
        ArrayList<BluetoothDevice> data = new ArrayList<>();
        adapter = BluetoothAdapter.getDefaultAdapter();

        for(BluetoothDevice disp : adapter.getBondedDevices()){
            data.add(disp);
        }
        return data;
    }

    //Clase interna para la Descarga de los Usuario desde el Server
    public class getUsuarios extends AsyncTask {

        ArrayList<Usuario> users;
        ArrayList<Contraseña> pass;

        boolean ERROR;
        String ERROR_MSG;


        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "Conectando..", "Descargando Información", true, false);

            users = new ArrayList<>();
            pass = new ArrayList<>();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                URL url = new URL(dbc.getServer() + "Usuarios");
                Log.e("Connect", url.getProtocol() + "://" + url.getHost() + ":" + url.getPort()  + url.getPath());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);

                //Hacer cabecera HTTP
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Request-With", "XMLHttpRequest");

                //Abrir
                conn.connect();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                String data = readStream(in);

                //Pasar Datos a JSon a Objetos
                JSONArray jsonArray = new JSONArray(data);

                JSONArray jsonUsuarios = jsonArray.getJSONArray(0);
                JSONArray jsonContraseñas = jsonArray.getJSONArray(1);

                //Recorrer usuarios
                for (int i = 0; i < jsonUsuarios.length(); i++) {
                    JSONObject object = jsonUsuarios.getJSONObject(i);

                    Usuario item = new Usuario();
                    item.setId_usuario(object.getString("id_usuario"));
                    item.setContraseña(object.getString("contraseña"));
                    item.setLogin(0);

                    users.add(item);
                }

                //Recorrer Contraseñas
                for (int i = 0; i < jsonContraseñas.length(); i++) {
                    JSONObject object = jsonContraseñas.getJSONObject(i);

                    Contraseña item = new Contraseña();
                    item.setId_modulo(object.getString("id_modulo"));
                    item.setContraseña(object.getString("contraseña"));

                    pass.add(item);
                }
                //Cerrar Conexion
                conn.disconnect();
            } catch (MalformedURLException e) {
                Log.e("getUsuarios", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error interno de la aplicación. Favor de reportar con el Desarrollador.";
            } catch (IOException e) {
                Log.e("getUsuarios", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al conectar con el servidor. Verifica tu conexión.";
            } catch (JSONException e) {
                Log.e("getUsuarios", e.getMessage());
                ERROR = true;
                ERROR_MSG = "Error al descargar los datos";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            dialog.cancel();
            super.onPostExecute(o);
            if(ERROR){
                Toast.makeText(context, ERROR_MSG, Toast.LENGTH_SHORT).show();
            }else{
                usuarios = users;
                Toast.makeText(context, "Descarga Exitosa!", Toast.LENGTH_SHORT).show();
                dbc.setUsuarios(usuarios);
                dbc.setContraseñas(pass);
            }
        }
    }

    //Metodo para convertir lo descargado a String
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
