package com.bybick.lacteosjolla.DataBases;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Contraseña;
import com.bybick.lacteosjolla.ObjectIN.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by bicktor on 25/05/2016.
 */
public class DBConfig extends SQLiteOpenHelper {
    Context context;
    Main m;
    SQLiteDatabase db;
    private static String DB_NAME = "Jolla_dbC";
    private static String DB_PATH = "/data/data/com.bybick.lacteosjolla/databases/";
    private SQLiteDatabase myDataBase;

    public DBConfig(Context context){
        super(context,DB_NAME,null,1);
        this.context = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist=checkDataBase();
        if(dbExist){
            //la base de datos existe y no hacemos nada
        }else{
            //llamando a este metodo se crea la base de datos vacia en la ruta por defecto del sistema
            //de nuestra aplicacion por lo que podremos sobreescribirla con nuestra base de datos
            this.getReadableDatabase();
            try{
                copyDataBase();
            }catch (IOException e){
                throw new Error("Error copiando Base de Datos");
            }
        }
    }

    /*
    * Compueba si la base de datos esxiste para evitar copiar siempre el fichero cade vez que se abra la aplicacion.
    * @return true si ya existe, false si no existe*/
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
        }catch (SQLiteException e){
            //Si llegamos aqui es porque la base de datos no existe todavia
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    /*
    * Copia nuestra base de datos desde la carpeta aasets a la recien creada
    * base de datos en la carpeta del sistema, desde donde podremos accede a ella.
    * esto de hace con ByteStream.
    * */

    public void copyDataBase() throws IOException{
        //Abrimos el fichero de la base de datos como entrada
        InputStream myInput = context.getAssets().open(DB_NAME);

        //Ruta a la base de datos vacia recien creada
        String outFileName = DB_PATH + DB_NAME;

        //Abrimos la base de datos vacia como salida
        OutputStream myOutput = new FileOutputStream(outFileName);

        //TRansferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;
        while((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer,0,length);
        }

        //Liberamos los stream
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void open(){
        //Abre la BAse de Datos
        try{
            createDataBase();
        }catch (IOException e){
            throw new Error("Ha sido imposible crear la Base de datos");
        }

        String myPath= DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Metodos

    //Obtener lista de CEDIS
    public String[] GET_Cedis(){
        String[] cedis = new String[0];
        ArrayList<String> data = new ArrayList<>();
        db = this.getReadableDatabase();

        String sql = "SELECT nombre FROM cedis";

        Cursor rs = db.rawQuery(sql, null);
        //Obtener Cedis de la DB
        if(rs.moveToFirst()){
            do{
                data.add(rs.getString(0));
            }while(rs.moveToNext());
        }

        //Pasar a String[]
        cedis = new String[data.size()];
        for(int i = 0; i < data.size(); i ++) {
            cedis[i] = data.get(i);
        }

        return cedis;
    }

    //Guardar el Servidor Seleccionadp
    public void setServer(String nombre) {
        db = this.getWritableDatabase();
        String sql = "SELECT ip, puerto FROM cedis WHERE nombre = '" + nombre + "'";
        Cursor rs = db.rawQuery(sql, null);
        ContentValues values = new ContentValues();
        String server = "";
        if(rs.moveToFirst()) {
            server = "http://" + rs.getString(0) + ":" + rs.getInt(1) + "/api/";
        }

        values.put("server", server);
        db.update("config", values, null, null);
    }

    //Obtener la Direccion del server
    public String getServer() {
        String server = "";
        db = this.getReadableDatabase();
        String sql = "SELECT server FROM config";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst())
            server = rs.getString(0);

        return server;
    }

    //Guardar Impresora seleccionada
    public void setPrinter(BluetoothDevice item) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name_printer", item.getName());
        values.put("mac_printer", item.getAddress());

        db.update("config", values, null, null);
    }

    //Guardar usuarios descargados
    public void setUsuarios(ArrayList<Usuario> data) {
        db = this.getWritableDatabase();
        String sql = "";

        for(int i = 0; i < data.size(); i ++){
            Usuario item = data.get(i);
            sql = "SELECT id_usuario FROM usuario WHERE id_usuario = '" + item.getId_usuario() +"'";
            Cursor c = db.rawQuery(sql, null);
            if(!c.moveToFirst()) {
                //Valores a insertar
                ContentValues values = new ContentValues();
                values.put("id_usuario", item.getId_usuario());
                values.put("contraseña", item.getContraseña());
                values.put("login", 0);

                //insertar en la DB
                db.insert("usuario", null, values);
            }
        }
    }

    //Ingresar Contraseñas
    public void setContraseñas(ArrayList<Contraseña> data) {
        db = this.getWritableDatabase();
        String sql = "";

        for(int i = 0; i < data.size(); i ++){
            Contraseña item = data.get(i);
            sql = "SELECT id_modulo FROM passwords WHERE id_modulo = '" + item.getId_modulo() +"'";
            Cursor c = db.rawQuery(sql, null);
            if(!c.moveToFirst()) {
                //Valores a insertar
                ContentValues values = new ContentValues();
                values.put("id_modulo", item.getId_modulo());
                values.put("contraseña", item.getContraseña());

                //insertar en la DB
                db.insert("passwords", null, values);
            }
        }

    }

    //Verificar el Login
    public boolean setLogin(String user, String pass) {
        db = this.getWritableDatabase();
        String sql = "SELECT id_usuario, contraseña FROM usuario WHERE id_usuario = '" + user +"' AND contraseña = '" + pass + "'";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put("login", 0);

            //Todos sin Login
            db.update("usuario", values, null, null);
            values.clear();

            values.put("login", 1);
            //Login User
            db.update("usuario", values, "id_usuario = '" + user + "'", null);

            return true;
        }else{
            return false;
        }
    }

    //Obteer Usuario Logueado
    public Usuario getlogin() {
        Usuario user = new Usuario();
        db = getReadableDatabase();
        String sql = "SELECT id_usuario, contraseña FROM usuario WHERE login = 1";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            user.setId_usuario(rs.getString(0));
            user.setContraseña(rs.getString(1));
        }

        return user;
    }

    //Obtener Impresora
    public JSONObject getPrinter() {
        db = this.getReadableDatabase();
        JSONObject printer = new JSONObject();
        String sql = "SELECT name_printer, mac_printer FROM config";
        Cursor rs = db.rawQuery(sql, null);
        rs.moveToFirst();
        try {
            printer.put("mac", rs.getString(1));
            printer.put("nombre", rs.getString(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return printer;
    }

    /******* VALIDAR CONFIG ***********/
    public boolean isModule(String pass, String modulo) {
        boolean result = false;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "COUNT('id_modulo') AS 'RESULTADO' " +
                "FROM passwords " +
                "WHERE id_modulo = '" + modulo + "' AND contraseña = '" + pass + "'";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            if(rs.getInt(0) == 0)
                result = false;
            else
                result = true;
        }

        return result;
    }



}
