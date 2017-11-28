package com.bybick.lacteosjolla.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Carga;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Equivalencia;
import com.bybick.lacteosjolla.ObjectIN.Factura;
import com.bybick.lacteosjolla.ObjectIN.Formas;
import com.bybick.lacteosjolla.ObjectIN.Inventario;
import com.bybick.lacteosjolla.ObjectIN.Listas_Precios;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectIN.Producto;
import com.bybick.lacteosjolla.ObjectIN.Producto_unidad;
import com.bybick.lacteosjolla.ObjectIN.Secuencia;
import com.bybick.lacteosjolla.ObjectIN.Serie;
import com.bybick.lacteosjolla.ObjectIN.Unidad;
import com.bybick.lacteosjolla.ObjectOUT.Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Cambio;
import com.bybick.lacteosjolla.ObjectOUT.Det_Devolucion;
import com.bybick.lacteosjolla.ObjectOUT.Det_Pago;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Devolucion;
import com.bybick.lacteosjolla.ObjectOUT.Forma_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Jornada;
import com.bybick.lacteosjolla.ObjectOUT.NoVenta;
import com.bybick.lacteosjolla.ObjectOUT.Pago;
import com.bybick.lacteosjolla.ObjectOUT.Precios;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.ObjectOUT.VentaResumen;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.ObjectView.CargaView;
import com.bybick.lacteosjolla.ObjectView.Efectividad;
import com.bybick.lacteosjolla.ObjectView.Result;
import com.bybick.lacteosjolla.ObjectView.Sobrantes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by bicktor on 25/05/2016.
 */
public class DBData extends SQLiteOpenHelper {
    Context context;
    DBConfig dbc;
    SQLiteDatabase db;
    private static String DB_NAME="Jolla_dbD";
    private static String DB_PATH="/data/data/com.bybick.lacteosjolla/databases/";
    private SQLiteDatabase myDataBase;

    public DBData(Context context){
        super(context, DB_NAME, null, 1);
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
            dbc = new DBConfig(context);
            dbc.open();
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
    //Verificar si hay Jornada Iniciada
    public boolean ValidarInicio(String id_usuario){
        boolean cargar = false;
        db = this.getReadableDatabase();
        String sql = "SELECT COUNT(id_jornada) FROM jornada WHERE id_usuario = '" + id_usuario + "' AND hora_fin IS NULL";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            if(rs.getInt(0) == 1) {
                cargar = false;
            }else {
                cargar = true;
            }
        }

        return cargar;
    }

    //Registrar el Inicio de Jornada Laboral
    public boolean setInitJornada(Jornada inicio) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_jornada", Main.NEWID());
        values.put("id_usuario", inicio.getId_usuario());
        values.put("fecha", inicio.getFecha());
        values.put("hora_inicio", inicio.getHora_inicio());
        values.put("gps_inicio", inicio.getGps_inicio());
        values.put("bateria_inicio", inicio.getBateria_inicio());
        values.put("terminada", false);

        if(db.insert("jornada", null, values) == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Registrar Fin de Jornada
    public void setFinJornada(Jornada fin) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hora_fin", fin.getHora_fin());
        values.put("gps_fin", fin.getGps_fin());
        values.put("bateria_fin", fin.getBateria_fin());
        values.put("terminada", true);

        db.update("jornada", values, "id_jornada = '" + fin.getId_jornada() +"'", null);
    }

    //Obtener Jornada
    public Jornada getJornada(String id_usuario) {
        Jornada item = new Jornada();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_jornada," +
                "id_usuario," +
                "fecha," +
                "hora_inicio," +
                "hora_fin," +
                "gps_inicio," +
                "gps_fin," +
                "bateria_inicio," +
                "bateria_fin " +
                "FROM jornada " +
                "WHERE terminada = 0 AND id_usuario = '" + id_usuario + "'";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            //Llenar Jornada
            item.setId_jornada(rs.getString(0));
            item.setId_usuario(rs.getString(1));
            item.setFecha(rs.getString(2));
            item.setHora_inicio(rs.getString(3));
            item.setHora_fin(rs.getString(4));
            item.setGps_inicio(rs.getString(5));
            item.setGps_fin(rs.getString(6));
            item.setBateria_inicio(rs.getInt(7));
            item.setBateria_fin(rs.getInt(8));

            return item;
        } else {
            return null;
        }

    }

    //Guardar Secuencias
    public void setSecuencia(ArrayList<Secuencia> data) {
        db = this.getWritableDatabase();

        //Borrar datos del usuario Registrado
        db.delete("secuencia", "id_usuario = '" + dbc.getlogin().getId_usuario() + "'", null);
        //Crear e insertar la secuencias
        ContentValues val = new ContentValues();
        for(Secuencia item : data) {

            val.put("fecha", item.getFecha());
            val.put("id_usuario", item.getId_usuario());
            val.put("secuencia", item.getSecuencia());
            val.put("id_cliente", item.getId_cliente());

            //Insertar registro en la DB
            db.insert("secuencia",null,val);
            val.clear();
        }
    }

    //Guardar Fromas de Ventas
    public void setFormasVenta(ArrayList<Formas> data) {
        db = this.getWritableDatabase();
        //Eliminar Registros
        db.delete("formas_venta", null, null);
        //Crear Registro
        ContentValues val = new ContentValues();
        for(Formas item : data) {
            val.put("id_forma", item.getId_forma());
            val.put("forma", item.getForma());
            val.put("id_cliente", item.getId_cliente());

            //Insertar Registro
            db.insert("formas_venta",null,val);
            val.clear();
        }

    }

    //Guaradr Clientes
    public void setClientes(ArrayList<Cliente> data) {
        db = this.getWritableDatabase();

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Cliente item : data) {
            values.put("id_cliente", item.getId_cliente().trim());
            values.put("nombre", item.getNombre());
            values.put("rfc", item.getRfc());
            values.put("razon_social", item.getRazon_social());
            values.put("id_forma_venta", item.getId_forma());
            values.put("forma_venta", item.getForma_venta());

            //Insertar Registro
            db.insert("cliente", null, values);
            values.clear();
        }
    }

    //Guardar Listas de Precios
    public void setListasPrecios(ArrayList<Listas_Precios> data) {
        db = this.getWritableDatabase();

        //Eliminamos registro anteriores
        db.delete("listas_precios", null, null);
        //CRear Registro
        ContentValues values = new ContentValues();
        for(Listas_Precios item : data) {
            values.put("tipo_lista", item.getTipo_lista());
            values.put("lista", item.getLista());
            values.put("id_cliente", item.getId_cliente());
            values.put("id_producto", item.getId_producto());
            values.put("sub_precio", item.getSub_precio());
            values.put("impuestos", item.getImpuestos());
            values.put("precio", item.getPrecio());
            values.put("id_unidad", item.getId_unidad());

            //Insertar Registro
            db.insert("listas_precios", null, values);
            values.clear();

        }
    }

    //Guardar Productos
    public void setProductos(ArrayList<Producto> data) {
        db = this.getWritableDatabase();

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Producto item : data) {
            values.put("id_producto", item.getId_producto());
            values.put("descripcion", item.getDescripcion());
            values.put("marca", item.getMarca());
            values.put("familia", item.getFamilia());
            values.put("decimales", item.getDecimales());
            values.put("orden", item.getOrden());

            //Insertar Registro
            db.insert("producto", null, values);
            values.clear();
        }
    }

    //Guardar Carga de Trabajo
    public void setCarga(ArrayList<Carga> data) {
        db = this.getWritableDatabase();

        //Elimiar Carga
        db.delete("carga", "id_usuario = '" + dbc.getlogin().getId_usuario() + "'", null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Carga item : data) {
            values.put("id_usuario", item.getId_usuario());
            values.put("id_producto", item.getId_producto());
            values.put("fecha", item.getFecha());
            values.put("marca", item.getMarca());
            values.put("cantidad", item.getCantidad());
            values.put("id_unidad", item.getId_unidad());

            db.insert("carga", null, values);
            values.clear();
        }
    }

    //Guardar Unidades
    public void setUnidades(ArrayList<Unidad> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("unidades", null, null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Unidad item : data) {
            values.put("id_unidad", item.getId_unidad());
            values.put("unidad", item.getUnidad());

            db.insert("unidades", null, values);
            values.clear();

        }
    }

    //Guadar Producto Unidad
    public void setProductoUnidad(ArrayList<Producto_unidad> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("conversion", null, null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Producto_unidad item : data) {
            values.put("id_producto", item.getId_producto());
            values.put("id_unidad", item.getId_unidad());
            values.put("conversion", item.getConversion());
            values.put("minima", item.getMinima());

            //Insertar Registro
            db.insert("conversion", null, values);
            values.clear();
        }
        //Queda en duda el Inventario
    }

    //Guardar Equivalencias
    public void setEquivalencias(ArrayList<Equivalencia> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("equivalencia", null, null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Equivalencia item : data) {
            values.put("id_producto", item.getId_producto());
            values.put("id_producto_equivalente", item.getId_producto_equivalente());

            //Insertar Registro
            db.insert("equivalencia", null, values);
            values.clear();
        }
    }

    //Guardar Facturas
    public  void setFacturas(ArrayList<Factura> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("facturas", "id_usuario = '" + dbc.getlogin().getId_usuario() + "'", null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Factura item : data) {
            values.put("id_cliente", item.getId_cliente().trim());
            values.put("id_usuario", item.getId_usuario().trim());
            values.put("fecha", item.getFecha());
            values.put("serie", item.getSerie());
            values.put("folio", item.getFolio());
            values.put("total", item.getTotal());
            values.put("saldo", item.getSaldo());

            db.insert("facturas", null, values);
            values.clear();
        }
    }

    //Guaradr Series y Folios
    public void setSeriesFolios(ArrayList<Serie> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("folio_serie", "id_usuario = '" + dbc.getlogin().getId_usuario() + "'", null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Serie item : data) {
            values.put("id_usuario", item.getId_usuario().trim());
            values.put("serie", item.getSerie());
            values.put("tipo", item.getTipo());
            values.put("folio", item.getFolio());
            values.put("empresa", item.getEmpresa());

            //Insertar Registro
            db.insert("folio_serie", null, values);
            values.clear();

        }
    }

    //Guardar Motivos de No Venta
    public void setMotivosNoVenta(ArrayList<Motivo> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("motivo_no_venta", null, null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Motivo item : data) {
            values.put("id_motivo", item.getId_motivo());
            values.put("descripcion", item.getDescripcion());

            db.insert("motivo_no_venta", null, values);
            values.clear();

        }
    }

    //Guardar Motivos Devolucion
    public void setMotivosDevolucion(ArrayList<Motivo> data) {
        db = this.getWritableDatabase();

        //Eliminar Registros
        db.delete("motivo_devolucion", null, null);

        //Generar Registro
        ContentValues values = new ContentValues();
        for(Motivo item : data) {
            values.put("id_motivo", item.getId_motivo());
            values.put("descripcion", item.getDescripcion());

            db.insert("motivo_devolucion", null, values);
            values.clear();
        }

    }

    //Obtener cantidad del producto seleccionado
    public Inventario getUnidad(String id_producto){
        Inventario data = new Inventario();
        String sql = "SELECT " +
                "id_unidad " +
                "FROM " +
                "carga " +
                "WHERE id_producto = '" + id_producto + "'";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do{
                Inventario item = new Inventario();
                item.setUnidad(rs.getString(0));
                data = item;
            }while(rs.moveToNext());
        }
        return data;
    }

    //Update cantidad FROM det_ventas
//    public Det_Venta updateCantidad (double cantidad, String id_det_venta, String id_producto){
//        Det_Venta data = new Det_Venta();
//        db = this.getReadableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("cantidad",cantidad);
//        db.update("det_venta", values, "id_det_venta = '" + id_det_venta +
//                "' AND id_producto = '" + id_producto + "'", null);
//        return null;
//    }

    //Consulta id_det_venta
    public Det_Venta idVenta (String id_producto, String id_venta){
        Det_Venta data = new Det_Venta();
        String sql = "SELECT " +
                "id_det_venta " +
                "FROM det_venta " +
                "WHERE id_producto = '" + id_producto + "'" +
                " AND id_venta = '" + id_venta + "'";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do{
                Det_Venta item = new Det_Venta();
                item.setId_det_venta(rs.getString(0));
                data = item;
            } while (rs.moveToNext());
        }
        return data;
    }

    //    //Consulta id_det_venta
//    public Det_Venta idVenta (String id_producto, String id_venta){
//        Det_Venta data = new Det_Venta();
//        String sql = "SELECT " +
//                "id_det_venta " +
//                "FROM det_venta " +
//                "WHERE id_producto = '" + id_producto + "'" +
//                " AND id_venta = '" + id_venta + "'";
//        db = this.getReadableDatabase();
//        Cursor rs = db.rawQuery(sql, null);
//        if (rs.moveToFirst()){
//            do{
//                Det_Venta item = new Det_Venta();
//                item.setId_det_venta(rs.getString(0));
//                data = item;
//            } while (rs.moveToNext());
//        }
//        return data;
//    }
//
    //Comparacion Palomita
    public Inventario getBtnConsulta (String id_producto, String id_det_venta){
        Inventario data = new Inventario();
        String sql = "SELECT " +
                "id_producto " +
                "FROM det_venta " +
                "WHERE id_producto = '" + id_producto +
                "' AND id_det_venta = '" + id_det_venta + "'";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do{
                Inventario item = new Inventario();
                item.setConsultBtn(rs.getString(0));
                data = item;
            } while (rs.moveToNext());
        }
        return data;
    }

    //Obtener cantidad del producto seleccionado
    public Inventario getCargaUnidad(String id_producto){
        Inventario data = new Inventario();
        String sql = "SELECT " +
                "cantidad " +
                "FROM " +
                "carga " +
                "WHERE id_producto = '" + id_producto +
                "' ORDER BY fecha DESC";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do{
                Inventario item = new Inventario();
                item.setCantidad(rs.getDouble(0));
                data = item;
            }while(rs.moveToNext());
        }
        return data;
    }

    //Obtener cantidad del producto seleccionado
    public Inventario getVentaUnidad(String id_producto){
        Inventario data = new Inventario();
        String sql = "SELECT " +
                "SUM(cantidad) AS cantidad  " +
                "FROM " +
                "det_venta " +
                "WHERE id_producto = '" + id_producto + "'";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do{
                Inventario item = new Inventario();
                item.setVentas_inventario(rs.getDouble(0));
                data = item;
            }while(rs.moveToNext());
        }
        return data;
    }

    //Obtener cantidad del producto seleccionado
    public Inventario getCambioUnidad(String id_producto){
        Inventario data = new Inventario();
        String sql = "SELECT " +
                "SUM(cantidad_out) AS cantidad_out " +
                "FROM " +
                "det_cambio " +
                "WHERE id_producto_entregado = '" + id_producto + "'";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do{
                Inventario item = new Inventario();
                item.setCambios_inventario(rs.getDouble(0));
                data = item;
            }while(rs.moveToNext());
        }
        return data;
    }

    public Det_Venta getDet_Venta (){
        Det_Venta data = new Det_Venta();
        String sql = "SELECT " +
                "id_producto, id_venta, id_det_venta, cantidad FROM det_venta";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if (rs.moveToFirst()){
            do {
                Det_Venta item = new Det_Venta();
                item.setId_producto(rs.getString(0));
                item.setId_venta(rs.getString(1));
                item.setId_det_venta(rs.getString(2));
                item.setCantidad(rs.getDouble(2));
                data = item;
            }while (rs.moveToNext());
        }
        return data;
    }

//    //Obtener cantidad del producto seleccionado
//    public ArrayList<Inventario> getCargaUnidad(String id_producto){
//        ArrayList<Inventario> data = new ArrayList<>();
//        String sql = "SELECT carga.cantidad, det_cambio.cantidad_out, det_venta.cantidad AS cantidad_venta " +
//                     "FROM carga CROSS JOIN " +
//                     "det_cambio CROSS JOIN " +
//                     "det_venta " +
//                     "WHERE carga.id_producto = '" + id_producto +
//                     "' ORDER BY carga.fecha DESC";
//
//        db = this.getReadableDatabase();
//        Cursor rs = db.rawQuery(sql, null);
//        if (rs.moveToFirst()){
//            do{
//                Inventario item = new Inventario();
//
//                item.setCantidad(rs.getInt(0));
//                item.setVentas_inventario(rs.getInt(1));
//                item.setCambios_inventario(rs.getInt(2));
//
//                data.add(item);
//            }while(rs.moveToNext());
//        }
//        return data;
//    }

    //Obtener carga ara mostrarla al Usuario
    public  ArrayList<CargaView> getCarga() {
        ArrayList<CargaView> data = new ArrayList<>();
        String sql = "SELECT " +
                "p.descripcion," +
                "p.marca," +
                "c.id_producto," +
                "c.cantidad," +
                "u.unidad " +
                "FROM carga c " +
                "INNER JOIN producto p " +
                "ON p.id_producto = c.id_producto " +
                "INNER JOIN unidades u " +
                "ON u.id_unidad = c.id_unidad " +
                "ORDER BY p.orden";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                CargaView item = new CargaView();

                item.setDescripcion(rs.getString(0));
                item.setMarca(rs.getString(1));
                item.setClave(rs.getString(2));
                item.setCantidad(rs.getDouble(3));
                item.setUnidad(rs.getString(4));

                data.add(item);

            }while(rs.moveToNext());
        }

        return data;
    }

    //Obtener Cliente
    public ArrayList<Cliente> getClientes( String filtro) {
        ArrayList<Cliente> data = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "";
        //Consulta dependiendo del Filtro
        if(filtro.equals("SE")) {
            sql = "SELECT " +
                    "DISTINCT(c.id_cliente)," +
                    "c.nombre," +
                    "c.rfc," +
                    "c.razon_social," +
                    "c.visitado," +
                    "c.bloqueado, " +
                    "cf.forma, " +
                    "s.secuencia " +
                    "FROM cliente c " +
                    "INNER JOIN secuencia s " +
                    "ON s.id_cliente = c.id_cliente " +
                    "INNER JOIN formas_venta cf " +
                    "ON cf.id_cliente = c.id_cliente " +
                    "WHERE visitado = 'false' " +
                    "ORDER BY s.secuencia";

        } else if(filtro.equals("ALL")) {
            sql = "SELECT " +
                    "DISTINCT(c.id_cliente)," +
                    "c.nombre," +
                    "c.rfc," +
                    "c.razon_social," +
                    "c.visitado," +
                    "c.bloqueado, " +
                    "cf.forma, " +
                    "s.secuencia " +
                    "FROM cliente c " +
                    "INNER JOIN secuencia s " +
                    "ON s.id_cliente = c.id_cliente " +
                    "INNER JOIN formas_venta cf " +
                    "ON cf.id_cliente = c.id_cliente " +
                    "ORDER BY s.secuencia";
        }

        //Ejecutar Consulta
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                //Crear Cliente
                Cliente item = new Cliente();

                item.setId_cliente(rs.getString(0));
                item.setNombre(rs.getString(1));
                item.setRfc(rs.getString(2));
                item.setRazon_social(rs.getString(3));
                item.setVisitado(rs.getInt(4) > 0);
                item.setBloqueado(rs.getInt(5) > 0);
                item.setForma_venta(rs.getString(6));


                data.add(item);
            }while(rs.moveToNext());
        }

        return data;
    }

    //Registrar Visita
    public void setVisita( Visita item) {
        db = this.getReadableDatabase();
        //Valores
        ContentValues values = new ContentValues();
        values.put("id_visita", item.getId_visita());
        values.put("id_jornada", item.getId_jornada());
        values.put("id_usuario", item.getId_usuario());
        values.put("id_cliente", item.getId_cliente());
        values.put("fecha", item.getFecha());
        values.put("hora_inicio", item.getHora_inicio());
        values.put("gps_inicio", item.getGps_inicio());
        values.put("bateria_inicio", item.getBateria_inicio());
        values.put("hora_fin", item.getHora_fin());
        values.put("gps_fin", item.getGps_fin());
        values.put("bateria_fin", item.getBateria_fin());
        values.put("ult_modificacion", "n/a");

        //Insertar Visita
        db.insert("visita", null, values);
        Log.e("Visita Insert", "ID : " + item.getId_visita());

        values.clear();
        values.put("visitado", true);

        //marcar Cliente como Visitado
        db.update("cliente", values, "id_cliente = '" + item.getId_cliente() + "'", null);
    }

    //Finalizar Visita
    public void endVisita(Visita item) {
        db = this.getReadableDatabase();
        //Valores
        ContentValues values = new ContentValues();
        values.put("hora_fin", item.getHora_fin());
        values.put("gps_fin", item.getGps_fin());
        values.put("bateria_fin", item.getBateria_fin());

        //Insertar Visita
        db.update("visita", values, "id_visita = '" + item.getId_visita() + "'", null);
        Log.e("Visita Finalizada", "ID : " + item.getId_visita());

        values.clear();
        values.put("visitado", true);
        //marcar Cliente como Visitado
        db.update("cliente", values, "id_cliente = '" + item.getId_cliente() + "' AND hora_fin = '---'", null);

    }

    //Eliminar Visita si no hay Movimiento
    public void deleteVisita(String id_visita, String id_cliente) {
        db = this.getWritableDatabase();

        //Eliminar Visita
        db.delete("visita", "id_visita = '" + id_visita + "'", null);

        //marcar Cliente como no visitado
        ContentValues values = new ContentValues();
        values.put("visitado", false);

        //Marcar que no esta visitado
        db.update("cliente", values, "id_cliente = '" + id_cliente + "'", null);
    }

    //ACtualizar Hora de Modificacion
    public void setModificacion(Visita item) {
        db = this.getWritableDatabase();
        //Valores
        ContentValues values = new ContentValues();
        values.put("ult_modificacion", item.getUlt_modificacion());

        //Actualizar
        db.update("visita", values, "id_visita = '" + item.getId_visita() + "'", null);
    }

    //Obtener Visitas x Clientes
    public ArrayList<Visita> getVisitaCliente(String id_cliente) {
        ArrayList<Visita> data = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_visita," +
                "id_jornada," +
                "id_usuario," +
                "id_cliente," +
                "fecha," +
                "hora_inicio," +
                "hora_fin," +
                "gps_inicio," +
                "gps_fin," +
                "bateria_inicio," +
                "bateria_fin," +
                "ult_modificacion, " +
                "terminado " +
                "FROM visita WHERE id_cliente = '" + id_cliente + "'";
        //Obtener Resultados
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                //Llear cada Visita
                Visita item = new Visita();
                item.setId_visita(rs.getString(0));
                item.setId_jornada(rs.getString(1));
                item.setId_usuario(rs.getString(2));
                item.setId_cliente(rs.getString(3));
                item.setFecha(rs.getString(4));
                item.setHora_inicio(rs.getString(5));
                item.setHora_fin(rs.getString(6));
                item.setGps_inicio(rs.getString(7));
                item.setGps_fin(rs.getString(8));
                item.setBateria_inicio(rs.getInt(9));
                item.setBateria_fin(rs.getInt(10));
                item.setUlt_modificacion(rs.getString(11));
                item.setTerminado(rs.getInt(12) > 0);

                if(item.getHora_fin() != null)
                    data.add(item);
                else
                    db.delete("visita", "id_visita = '" + item.getId_visita() + "'", null);
            }while(rs.moveToNext());

        }
        //Retornar Visitas
        return data;
    }

    //Obtener los productos
    public ArrayList<Producto> getProductos(String marca) {
        ArrayList<Producto> data = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "carga.id_producto," +
                "descripcion," +
                "producto.marca," +
                "decimales," +
                "familia " +
                "FROM producto, carga " +
                "WHERE producto.marca = '" + marca + "' AND producto.id_producto = carga.id_producto " +
                "ORDER BY orden";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                Producto item = new Producto();

                item.setId_producto(rs.getString(0));
                item.setDescripcion(rs.getString(1));
                item.setMarca(rs.getString(2));
                item.setDecimales(rs.getInt(3));
                item.setFamilia(rs.getString(4));

                data.add(item);
            }while(rs.moveToNext());
        }

        return data;
    }

    /******* VENTA ********/

    //Insertar Nueva Venta
    public void setVenta(Venta venta) {
        String id_venta = Main.NEWID();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //INSERTAR VENTA
        values.put("id_venta", id_venta);
        values.put("id_cliente", venta.getId_cliente());

        values.put("id_visita", venta.getId_visita());
        values.put("subtotal", venta.getSubtotal());
        values.put("impuestos", venta.getImpuestos());
        values.put("total", venta.getTotal());
        values.put("empresa", venta.getEmpresa());
        values.put("forma_venta", venta.getForma_venta());
        values.put("id_forma_venta", venta.getId_forma_venta());
        values.put("serie", venta.getSerie());
        values.put("folio", venta.getFolio());
        values.put("enviado", 0);

        if(db.insert("venta", null, values) == -1)
            Toast.makeText(context, "Error insertando Venta " + id_venta, Toast.LENGTH_SHORT).show();

        //INSERTAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Venta> detalles = venta.getDet_venta();

        //Reset
        values =new ContentValues();
        for(int i = 0; i < detalles.size(); i++){
            Det_Venta item = detalles.get(i);

            values.put("id_det_venta", Main.NEWID());
            values.put("id_venta", id_venta);
            values.put("id_producto", item.getId_producto());
            values.put("id_unidad", item.getId_unidad());
            values.put("cantidad", item.getCantidad());
            values.put("subtotal", item.getSubtotal());
            values.put("impuestos", item.getImpuestos());
            values.put("precio", item.getPrecio());
            values.put("total", item.getTotal());
            values.put("partida",(i + 1));
            values.put("cantidad_minima", (item.getCantidad() * item.getConversion()));

            if(db.insert("det_venta", null, values) == -1)
                Toast.makeText(context, "Error insertando " + item.getDescripcion(), Toast.LENGTH_SHORT).show();
            values.clear();
/*
            //Actualizar saldo en la tabla facturas
            String sql="";
            sql="UPDATE inventario SET cantidad=cantidad-("+(item.getCantidad() * item.getConversion())+") WHERE id_producto='"+item.getId_producto()+"'";
            db.execSQL(sql);
*/
        }
    }

    //Actualizar venta
    public void UpdateVenta(Venta venta) {
        String id_venta = venta.getId_venta();
        db = this.getWritableDatabase();
        String sql = "";
        ContentValues values = new ContentValues();

        //Generar VENTA
        values.put("subtotal", venta.getSubtotal());
        values.put("impuestos", venta.getImpuestos());
        values.put("total", venta.getTotal());

        if(db.update("venta", values, "id_venta = '" + id_venta + "'", null) == -1)
            Toast.makeText(context, "Error actualizando Venta " + id_venta, Toast.LENGTH_SHORT).show();

        //INSERTAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Venta> detalles = venta.getDet_venta();

        //Eliminar Detalles
        db.delete("det_venta", "id_venta = '" + id_venta + "'", null);

        //Reset y Preparar detalles a Insertar
        values =new ContentValues();
        for(int i = 0; i < detalles.size(); i++){
            Det_Venta item = detalles.get(i);

            values.put("id_det_venta", Main.NEWID());
            values.put("id_venta", id_venta);
            values.put("id_producto", item.getId_producto());
            values.put("id_unidad", item.getId_unidad());
            values.put("cantidad", item.getCantidad());
            values.put("subtotal", item.getSubtotal());
            values.put("impuestos", item.getImpuestos());
            values.put("precio", item.getPrecio());
            values.put("total", item.getTotal());
            values.put("partida",(i + 1));
            values.put("cantidad_minima", (item.getCantidad() * item.getConversion()));

            if(db.insert("det_venta", null, values) == -1)
                Toast.makeText(context, "Imprimiendo " + item.getDescripcion(), Toast.LENGTH_SHORT).show();
            values.clear();
/*
            //Actualizar saldo en la tabla facturas
            String sql="";
            sql="UPDATE inventario SET cantidad=cantidad-("+(item.getCantidad() * item.getConversion())+") WHERE id_producto='"+item.getId_producto()+"'";
            db.execSQL(sql);
*/
        }

    }

    //Obtener Venta si existe
    public Venta getVenta(String id_visita, String marca) {
        Venta venta = new Venta();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_venta," +
                "id_cliente," +
                "id_visita," +
                "subtotal," +
                "impuestos," +
                "total," +
                "enviado," +
                "forma_venta," +
                "serie," +
                "folio " +
                "FROM venta WHERE id_visita = '" + id_visita + "' " +
                "AND empresa = '" + marca + "'";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {

            //Rellenar Venta
            venta.setId_venta(rs.getString(0));
            venta.setId_cliente(rs.getString(1));
            venta.setId_visita(rs.getString(2));
            venta.setSubtotal(rs.getDouble(3));
            venta.setImpuestos(rs.getDouble(4));
            venta.setTotal(rs.getDouble(5));
            venta.setEnviado(rs.getInt(6));
            venta.setForma_venta(rs.getString(7));
            venta.setSerie(rs.getString(8));
            venta.setFolio(rs.getInt(9));

            //Obtener Los Detalles de la Venta
            sql = "SELECT " +
                    "p.descripcion," +
                    "d.cantidad," +
                    "u.unidad," +
                    "d.subtotal," +
                    "d.total," +
                    "d.impuestos," +
                    "d.id_unidad," +
                    "d.id_producto," +
                    "c.conversion," +
                    "d.precio " +
                    "FROM det_venta d " +
                    "INNER JOIN producto p " +
                    "ON p.id_producto = d.id_producto " +
                    "INNER JOIN unidades u " +
                    "ON u.id_unidad = d.id_unidad " +
                    "INNER JOIN conversion c " +
                    "ON c.id_producto = d.id_producto " +
                    "AND c.id_unidad = d.id_unidad " +
                    "WHERE d.id_venta = '" + venta.getId_venta() + "'";
            Cursor rs2 = db.rawQuery(sql, null);
            ArrayList<Det_Venta> detalles = new ArrayList<>();
            if(rs2.moveToFirst()) {
                do {
                    //Llenamos el detalles de la venta
                    Det_Venta item = new Det_Venta();
                    item.setDescripcion(rs2.getString(0));
                    item.setCantidad(rs2.getDouble(1));
                    item.setUnidad(rs2.getString(2));
                    item.setSubtotal(rs2.getDouble(3));
                    item.setTotal(rs2.getDouble(4));
                    item.setImpuestos(rs2.getDouble(5));
                    item.setId_unidad(rs2.getString(6));
                    item.setId_producto(rs2.getString(7));
                    item.setConversion(rs2.getDouble(8));
                    item.setPrecio(rs2.getDouble(9));

                    detalles.add(item);
                }while(rs2.moveToNext());
            }
            //Agregar Detalles a la Venta
            venta.setDet_venta(detalles);

            //Regresar venta en casode que existe
            return venta;
        } else {
            //Regresar NULLsi no existe veta para el cliente en esa visita
            return null;
        }
    }

    //Obtenet Lista de Precios
    public ArrayList<Precios> getPrecios(String id_producto, String id_cliente) {
        ArrayList<Precios> data = new ArrayList<>();
        db = this.getReadableDatabase();

        //Intenentar traer Precios por Cliente
        String sql = "SELECT " +
                "l.sub_precio," +
                "l.impuestos," +
                "l.precio," +
                "l.id_unidad," +
                "l.lista," +
                "u.unidad," +
                "l.tipo_lista," +
                "c.conversion " +
                "FROM listas_precios l " +
                "INNER JOIN unidades u " +
                "ON u.id_unidad = l.id_unidad " +
                "INNER JOIN conversion c " +
                "ON c.id_producto = l.id_producto " +
                "AND c.id_unidad = l.id_unidad " +
                "WHERE l.id_cliente = '" + id_cliente + "' " +
                "AND l.id_producto = '" + id_producto + "'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                Precios item = new Precios();

                item.setSubprecio(rs.getDouble(0));
                item.setImpuestos(rs.getDouble(1));
                item.setPrecio(rs.getDouble(2));
                item.setId_unidad(rs.getString(3));
                item.setLista(rs.getString(4));
                item.setUnidad(rs.getString(5));
                item.setTipo_lista(rs.getString(6));
                item.setConversion(rs.getDouble(7));

                data.add(item);
            } while(rs.moveToNext());
        } else {
            //Trarse Lista de precio General
            sql = "SELECT " +
                    "l.sub_precio," +
                    "l.impuestos," +
                    "l.precio," +
                    "l.id_unidad," +
                    "l.lista," +
                    "u.unidad," +
                    "l.tipo_lista," +
                    "c.conversion " +
                    "FROM listas_precios l " +
                    "INNER JOIN unidades u " +
                    "ON u.id_unidad = l.id_unidad " +
                    "INNER JOIN conversion c " +
                    "ON c.id_producto = l.id_producto " +
                    "AND c.id_unidad = l.id_unidad " +
                    "WHERE l.id_cliente = 'NULL' " +
                    "AND l.id_producto = '" + id_producto + "'";

            rs = db.rawQuery(sql, null);
            if(rs.moveToFirst()) {
                do {
                    Precios item = new Precios();

                    item.setSubprecio(rs.getDouble(0));
                    item.setImpuestos(rs.getDouble(1));
                    item.setPrecio(rs.getDouble(2));
                    item.setId_unidad(rs.getString(3));
                    item.setLista(rs.getString(4));
                    item.setUnidad(rs.getString(5));
                    item.setTipo_lista(rs.getString(6));
                    item.setConversion(rs.getDouble(7));

                    data.add(item);
                } while(rs.moveToNext());
            }
        }

        return data;
    }

    //Obtener las Forma de Venta por cliente
    public ArrayList<Forma_Venta> getFormas(String id_cliente) {
        ArrayList<Forma_Venta> data = new ArrayList<>();

        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_forma," +
                "forma," +
                "id_cliente " +
                "bloqueado " +
                "FROM formas_venta " +
                "WHERE id_cliente = '"+id_cliente+"'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            do{
                Forma_Venta item = new Forma_Venta();
                item.setId_forma(rs.getInt(0));
                item.setForma(rs.getString(1));
                item.setId_cliente(rs.getString(2));
                if (item.getId_forma() == 1 || item.getId_forma() == 2) {
                    sql = "UPDATE " +
                            "cliente " +
                            "SET bloqueado = 0 " +
                            "WHERE id_cliente = '"+id_cliente+"'";
                    db.execSQL(sql);
                    item.setBloqueado(false);
                } else{
                    sql = "UPDATE " +
                            "cliente " +
                            "SET bloqueado = 1 " +
                            "WHERE id_cliente = '"+id_cliente+"'";
                    db.execSQL(sql);
                    item.setBloqueado(true);
                }

                data.add(item);

            }while(rs.moveToNext());
        }

        return data;
    }

    //Obtnere el Folio para la Venta
    public Serie getFolio(boolean factura, String marca) {
        Serie folio = new Serie();
        db = this.getReadableDatabase();

        String sql = "";
        if(factura) {
            sql = "SELECT " +
                    "serie," +
                    "folio " +
                    "FROM folio_serie " +
                    "WHERE empresa = '" + marca + "'";
        }else{
            sql = "SELECT " +
                    "serie," +
                    "folio " +
                    "FROM folio_serie " +
                    "WHERE empresa = 'NULL'";
        }

        Cursor rs=db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            folio.setSerie( rs.getString(0) );
            folio.setFolio( (rs.getInt(1)) + 1 );

            sql = "UPDATE " +
                    "folio_serie " +
                    "SET folio = folio+1 " +
                    "WHERE serie = '" + rs.getString(0) + "'";
            db.execSQL(sql);
        }

        return folio;
    }

    /****** CAMBIOS ********/

    //Insertar un Nuevo Cambio
    public void setCambio(Cambio cambio) {
        String id_cambio = Main.NEWID();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //INSERTAR VENTA
        values.put("id_cambio", id_cambio);
        values.put("id_cliente", cambio.getId_cliente());
        values.put("id_visita", cambio.getId_visita());
        values.put("empresa", cambio.getEmpresa());
        values.put("enviado", 0);

        if(db.insert("cambio", null, values) == -1)
            Toast.makeText(context, "Error insertando Cambio " + id_cambio, Toast.LENGTH_SHORT).show();

        //INSERTAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Cambio> detalles = cambio.getDetalles();

        //Reset
        values =new ContentValues();
        for(int i = 0; i < detalles.size(); i++) {
            Det_Cambio item = detalles.get(i);

            values.put("id_det_cambio", Main.NEWID());
            values.put("id_cambio", id_cambio);
            values.put("id_motivo", item.getId_motivo());
            values.put("id_producto_recibido", item.getId_producto_recibido());
            values.put("id_unidad_in", item.getId_unidad_in());
            values.put("cantidad_in", item.getCantidad_in());
            values.put("id_producto_entregado", item.getId_producto_entregado());
            values.put("id_unidad_out", item.getUnidad_out());
            values.put("cantidad_out", item.getCantidad_out());
            values.put("partida", (i + 1));

            //Obtener Unidad Minima
            String minima = "SELECT " +
                    "" + item.getCantidad_in() +
                    "*(SELECT conversion " +
                    "FROM conversion " +
                    "WHERE id_producto = '" + item.getId_producto_recibido() + "' " +
                    "AND id_unidad = '" + item.getId_unidad_in() + "')";
            Cursor cant = db.rawQuery(minima, null);
            if (cant.moveToFirst()) {
                values.put("cantidad_minima_in", cant.getDouble(0));
            }

            //Insertar el detalle
            if (db.insert("det_cambio", null, values) == -1)
                Toast.makeText(context, "Error insertando " + item.getId_motivo(), Toast.LENGTH_SHORT).show();
            values.clear();
        }
    }

    //Actualizar Cambio
    public void UpdateCambio(Cambio cambio) {
        String id_cambio = cambio.getId_cambio();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //INSERTAR LOS DETALLES DE cada CAMBIO
        ArrayList<Det_Cambio> detalles = cambio.getDetalles();

        //Eliminar Detalles
        db.delete("det_cambio", "id_cambio = '" + id_cambio + "'", null);

        //Reset
        values =new ContentValues();
        for(int i = 0; i < detalles.size(); i++) {
            Det_Cambio item = detalles.get(i);
            Log.e("ID-MOTIVO", item.getId_motivo() + "");

            values.put("id_det_cambio", Main.NEWID());
            values.put("id_cambio", id_cambio);
            values.put("id_motivo", item.getId_motivo());
            values.put("id_producto_recibido", item.getId_producto_recibido());
            values.put("id_unidad_in", item.getId_unidad_in());
            values.put("cantidad_in", item.getCantidad_in());
            values.put("id_producto_entregado", item.getId_producto_entregado());
            values.put("id_unidad_out", item.getUnidad_out());
            values.put("cantidad_out", item.getCantidad_out());
            values.put("partida", (i + 1));

            //Obtener Unidad Minima
            String minima = "SELECT " +
                    "" + item.getCantidad_in() +
                    "*(SELECT conversion " +
                    "FROM conversion " +
                    "WHERE id_producto = '" + item.getId_producto_recibido() + "' " +
                    "AND id_unidad = '" + item.getId_unidad_in() + "')";
            Cursor cant = db.rawQuery(minima, null);
            if (cant.moveToFirst()) {
                values.put("cantidad_minima_in", cant.getDouble(0));
            }

            //Insertar el detalle
            if (db.insert("det_cambio", null, values) == -1)
                Toast.makeText(context, "Error insertando " + item.getId_motivo(), Toast.LENGTH_SHORT).show();
            values.clear();
        }
    }

    //Obtener Cambio si Existe
    public Cambio getCambio(String id_visita, String marca) {
        Cambio cambio = new Cambio();
        ArrayList<Det_Cambio> detalles = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_cambio," +
                "id_cliente," +
                "id_visita," +
                "enviado," +
                "empresa " +
                "FROM cambio WHERE id_visita = '" + id_visita + "' " +
                "AND empresa = '" + marca + "'";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {

            //Rellenar Venta
            cambio.setId_cambio(rs.getString(0));
            cambio.setId_cliente(rs.getString(1));
            cambio.setId_visita(rs.getString(2));
            cambio.setEnviado(rs.getInt(3));
            cambio.setEmpresa(rs.getString(4));

            //Obtener Los Detalles del Cambio Entregado
            sql = "SELECT " +
                    "p.descripcion," +
                    "d.cantidad_out," +
                    "u.unidad," +
                    "d.id_unidad_out," +
                    "d.id_producto_entregado," +
                    "d.id_det_cambio," +
                    "m.descripcion," +
                    "d.id_motivo " +
                    "FROM det_cambio d "+
                    "INNER JOIN producto p " +
                    "ON p.id_producto = d.id_producto_entregado " +
                    "INNER JOIN unidades u " +
                    "ON u.id_unidad = d.id_unidad_out " +
                    "INNER JOIN motivo_devolucion m " +
                    "ON m.id_motivo = d.id_motivo "+
                    "WHERE d.id_cambio = '" + cambio.getId_cambio() + "'";

            Cursor rs2 = db.rawQuery(sql, null);
            if(rs2.moveToFirst()) {
                do {
                    //Llenamos el detalles de Salida
                    Det_Cambio item = new Det_Cambio();
                    item.setNombre_entregado(rs2.getString(0));
                    item.setCantidad_out(rs2.getDouble(1));
                    item.setUnidad_out(rs2.getString(2));
                    item.setId_unidad_out(rs2.getString(3));
                    item.setId_producto_entregado(rs2.getString(4));
                    item.setId_det_cambio(rs2.getString(5));
                    item.setMotivo(rs2.getString(6));
                    item.setId_motivo(rs2.getInt(7));

                    //Llenamos los detalles de Ingreso
                    sql="SELECT " +
                            "p.descripcion," +
                            "d.cantidad_in," +
                            "u.unidad," +
                            "d.id_unidad_in," +
                            "d.id_producto_recibido " +
                            "FROM det_cambio d " +
                            "INNER JOIN producto p " +
                            "ON p.id_producto = d.id_producto_recibido " +
                            "INNER JOIN unidades u " +
                            "ON u.id_unidad = d.id_unidad_in " +
                            "WHERE id_det_cambio = '" + item.getId_det_cambio() + "'";

                    Cursor rs3 = db.rawQuery(sql,null);
                    if(rs3.moveToFirst()){
                        item.setNombre_recibido(rs3.getString(0));
                        item.setCantidad_in(rs3.getDouble(1));
                        item.setUnidad_in(rs3.getString(2));
                        item.setId_unidad_in(rs3.getString(3));
                        item.setId_producto_recibido(rs3.getString(4));
                    }

                    detalles.add(item);
                }while(rs2.moveToNext());
            }

            //Agregar Detalles al Cambio
            cambio.setDetalles(detalles);
            //Regresar venta en casode que existe
            return cambio;
        } else {
            //Regresar NULLsi no existe veta para el cliente en esa visita
            return null;
        }
    }

    //btener Motivos de Cambio
    public ArrayList<Motivo> getMotivosCambio(){
        ArrayList<Motivo> data = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql="SELECT * FROM motivo_devolucion";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            do{
                Motivo motivo=new Motivo();

                motivo.setId_motivo(rs.getInt(0));
                motivo.setDescripcion(rs.getString(1));

                data.add(motivo);
            }while(rs.moveToNext());
        }

        return data;
    }

    //Obtener las Unidades
    public ArrayList<Unidad> getUnidades() {
        ArrayList<Unidad> data = new ArrayList<>();
        String sql="SELECT * FROM unidades";
        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql,null);
        if(rs.moveToFirst()){
            do{
                Unidad unidad=new Unidad();

                unidad.setId_unidad(rs.getString(0));
                unidad.setUnidad(rs.getString(1));

                data.add(unidad);
            }while (rs.moveToNext());
        }

        return data;
    }

    //Obtener Unidades por Producto
    public ArrayList<Unidad> getUnidadesProducto(String id_producto) {
        ArrayList<Unidad> data = new ArrayList<>();
        String sql="SELECT " +
                "u.id_unidad," +
                "u.unidad " +
                "FROM unidades u " +
                "INNER JOIN conversion pu " +
                "ON pu.id_unidad=u.id_unidad " +
                "WHERE pu.id_producto = '"+id_producto+"'";

        db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            do{
                Unidad unidad=new Unidad();
                unidad.setId_unidad(rs.getString(0));
                unidad.setUnidad(rs.getString(1));

                data.add(unidad);

            }while(rs.moveToNext());
        }
        return data;
    }

    //Ovbtener productos Qeuivalentes
    public ArrayList<Producto> getEquivalentes(String id_producto) {
        ArrayList<Producto> data = new ArrayList<>();
        String sql="SELECT " +
                "p.id_producto," +
                "p.descripcion," +
                "p.marca," +
                "p.decimales," +
                "p.familia " +
                "FROM producto p " +
                "INNER JOIN equivalencia e " +
                "ON e.id_producto_equivalente = p.id_producto " +
                "WHERE e.id_producto = '"+id_producto+"'";

        db = this.getReadableDatabase();

        Cursor rs =db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            do{
                Producto producto=new Producto();

                producto.setId_producto(rs.getString(0));
                producto.setDescripcion(rs.getString(1));
                producto.setMarca(rs.getString(2));
                producto.setDecimales(rs.getInt(3));
                producto.setFamilia(rs.getString(4));

                data.add(producto);
            }while(rs.moveToNext());
        }
        return data;
    }

    /******* DEVOLUCIONES ********/

    //Guardar Devolucion
    public void setDevolucion(Devolucion item) {
        String id_devolucion = Main.NEWID();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //INSERTAR VENTA
        values.put("id_devolucion", id_devolucion);
        values.put("id_cliente", item.getId_cliente());
        values.put("id_visita", item.getId_visita());
        values.put("empresa", item.getEmpresa());
        values.put("enviado", 0);

        if(db.insert("devolucion", null, values) == -1)
            Toast.makeText(context, "Error insertando Devolucion " + id_devolucion, Toast.LENGTH_SHORT).show();

        //INSERTAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Devolucion> detalles = item.getDetalles();

        //Reset
        values =new ContentValues();
        for(int i = 0; i < detalles.size(); i++){
            Det_Devolucion det = detalles.get(i);

            values.put("id_det_devolucion", Main.NEWID());
            values.put("id_devolucion", id_devolucion);
            values.put("id_producto", det.getId_producto());
            values.put("id_unidad", det.getId_unidad());
            values.put("cantidad", det.getCantidad());
            values.put("id_motivo", det.getId_motivo());
            values.put("partida",(i + 1));
            values.put("importe", det.getImporte());
            //Sacar la unidad minima
            String minima = "SELECT "
                    + det.getCantidad() + "*(SELECT conversion " +
                    "FROM conversion " +
                    "WHERE id_producto = '" + det.getId_producto() + "' " +
                    "AND id_unidad='" + det.getId_unidad() + "')";
            Cursor cant = db.rawQuery(minima,null);

            if(cant.moveToFirst()){
                values.put("cantidad_minima",cant.getDouble(0));
            }

            if(db.insert("det_devolucion", null, values) == -1)
                Toast.makeText(context, "Error insertando " + det.getNombre(), Toast.LENGTH_SHORT).show();
            values.clear();
        }
    }

    //Actualizar Devolcuion
    public void UpdateDevolucion(Devolucion item) {
        String id_devolucion = item.getId_devolucion();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //ACTUALIZAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Devolucion> detalles = item.getDetalles();

        //Eliminar Detalles
        db.delete("det_devolucion", "id_devolucion = '" + id_devolucion + "'", null);

        //Reset
        values = new ContentValues();
        for(int i = 0; i < detalles.size(); i++){
            Det_Devolucion det = detalles.get(i);

            values.put("id_det_devolucion", Main.NEWID());
            values.put("id_devolucion", id_devolucion);
            values.put("id_producto", det.getId_producto());
            values.put("id_unidad", det.getId_unidad());
            values.put("cantidad", det.getCantidad());
            values.put("id_motivo", det.getId_motivo());
            values.put("partida",(i + 1));
            //Sacar la unidad minima
            String minima = "SELECT "
                    + det.getCantidad() + "*(SELECT conversion " +
                    "FROM conversion " +
                    "WHERE id_producto = '" + det.getId_producto() + "' " +
                    "AND id_unidad='" + det.getId_unidad() + "')";
            Cursor cant = db.rawQuery(minima,null);

            if(cant.moveToFirst()){
                values.put("cantidad_minima",cant.getDouble(0));
            }

            if(db.insert("det_devolucion", null, values) == -1)
                Toast.makeText(context, "Error insertando " + det.getNombre(), Toast.LENGTH_SHORT).show();
            values.clear();
        }
    }

    //Obtener Devvolucion si Existe
    public Devolucion getDevolucion(String id_visita, String marca) {
        Devolucion item = new Devolucion();
        ArrayList<Det_Devolucion> detalles = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_devolucion," +
                "id_cliente," +
                "id_visita," +
                "enviado," +
                "empresa " +
                "FROM devolucion WHERE id_visita = '" + id_visita + "' " +
                "AND empresa = '" + marca + "'";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {

            //Rellenar Venta
            item.setId_devolucion(rs.getString(0));
            item.setId_cliente(rs.getString(1));
            item.setId_visita(rs.getString(2));
            item.setEnviado(rs.getInt(3));
            item.setEmpresa(rs.getString(4));

            //Obtener Detalles
            sql="SELECT " +
                    "p.descripcion," +
                    "d.cantidad," +
                    "u.unidad," +
                    "d.id_unidad," +
                    "d.id_producto," +
                    "d.id_motivo," +
                    "m.descripcion " +
                    "FROM det_devolucion d "+
                    "INNER JOIN producto p " +
                    "ON p.id_producto = d.id_producto " +
                    "INNER JOIN unidades u " +
                    "ON u.id_unidad = d.id_unidad " +
                    "INNER JOIN motivo_devolucion m " +
                    "ON m.id_motivo = d.id_motivo " +
                    "WHERE d.id_devolucion = '" + item.getId_devolucion() + "'";
            //Ejecutar Consulta
            Cursor c = db.rawQuery(sql,null);
            if(c.moveToFirst()){
                do{
                    Det_Devolucion detalle = new Det_Devolucion();
                    detalle.setNombre(c.getString(0));
                    detalle.setCantidad(c.getDouble(1));
                    detalle.setUnidad(c.getString(2));
                    detalle.setId_unidad(c.getString(3));
                    detalle.setId_producto(c.getString(4));
                    detalle.setId_motivo(c.getInt(5));
                    detalle.setMotivo(c.getString(6));

                    detalles.add(detalle);
                }while(c.moveToNext());
            }

            //Agregar Detalles al Cambio
            item.setDetalles(detalles);
            //Regresar venta en casode que existe
            return item;
        } else {
            return null;
        }
    }


    /******* COBRANZA ********/

    //Obtener Facturas del Cliente
    public ArrayList<Factura> getFacturas(String id_cliente) {
        ArrayList<Factura> data = new ArrayList<>();
        String sql="SELECT " +
                "id_cliente," +
                "fecha," +
                "serie," +
                "folio," +
                "total," +
                "saldo " +
                "FROM facturas " +
                "WHERE id_cliente = '"+id_cliente+"'" +
                " AND Fecha < date('now')";
        db=this.getReadableDatabase();
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()){
            do{
                Factura factura=new Factura();

                factura.setId_cliente(rs.getString(0));
                factura.setFecha(rs.getString(1));
                factura.setSerie(rs.getString(2));
                factura.setFolio(rs.getInt(3));
                factura.setTotal(rs.getDouble(4));
                factura.setSaldo(rs.getDouble(5));

                data.add(factura);
            }while(rs.moveToNext());
        }

        return data;
    }

    //Registrar Pago
    public void setPago(Pago item) {
        String id_pago = Main.NEWID();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //INSERTAR LA CABECERA DE LOS PAGOS
        values.put("id_pago", id_pago);
        values.put("id_visita", item.getId_visita());
        values.put("id_cliente", item.getId_cliente());
        values.put("fecha", item.getFecha());
        values.put("enviado", 0);

        if(db.insert("pago", null, values) == -1)
            Toast.makeText(context, "Error insertando Pago", Toast.LENGTH_SHORT).show();

        values = new ContentValues();
        //INSERTAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Pago> detalles = item.getDetalles();
        for(int i = 0; i < detalles.size() ; i++){
            Det_Pago det = detalles.get(i);

            values.put("id_det_pago", Main.NEWID());
            values.put("id_pago", id_pago);
            values.put("serie", det.getSerie());
            values.put("folio", det.getFolio());
            values.put("importe_pago", det.getImporte_pago());
            values.put("forma_pago", det.getForma_pago());
            values.put("comentarios", det.getComentarios());
            values.put("saldo_anterior", det.getSaldo_ant());

            db.insert("det_pago",null,values);
            values.clear();

            //Actualizar saldo en la tabla facturas
            String sql="";
            sql="UPDATE facturas SET saldo = saldo - " +
                    detalles.get(i).getImporte_pago() +
                    " WHERE serie = '" + detalles.get(i).getSerie() + "' AND folio = "+detalles.get(i).getFolio();
            db.execSQL(sql);
        }
    }

    //Actualizar Pago
    public void UpdatePago(Pago item) {
        String id_pago = item.getId_pago();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //INSERTAR LOS DETALLES DE CADA PAGO
        ArrayList<Det_Pago> detalles = item.getDetalles();
        //Eliminar Detalles
        String sql = "SELECT " +
                "serie," +
                "folio," +
                "importe_pago " +
                "FROM det_pago " +
                "WHERE id_pago = '" + id_pago + "'";
        Cursor rs = db.rawQuery(sql,null);
        if(rs.moveToFirst()){
            do{
                sql = "UPDATE facturas " +
                        "SET saldo = saldo + " + rs.getDouble(2) + " " +
                        "WHERE serie = '"+rs.getString(0)+"' " +
                        "AND folio ='" + rs.getString(1) + "'";
                db.execSQL(sql);
            }while(rs.moveToNext());
        }

        //Eliminar Detalles
        db.delete("det_pago", "id_pago = '" + id_pago + "'", null);

        for(int i = 0; i < detalles.size() ; i++){
            Det_Pago det = detalles.get(i);

            values.put("id_det_pago", Main.NEWID());
            values.put("id_pago", id_pago);
            values.put("serie", det.getSerie());
            values.put("folio", det.getFolio());
            values.put("importe_pago", det.getImporte_pago());
            values.put("forma_pago", det.getForma_pago());
            values.put("comentarios", det.getComentarios());
            values.put("saldo_anterior", det.getSaldo_ant());

            db.insert("det_pago", null, values);
            values.clear();

            //Actualizar saldo en la tabla facturas
            sql = "UPDATE facturas SET saldo = saldo - " +
                    detalles.get(i).getImporte_pago() +
                    " WHERE serie = '" + detalles.get(i).getSerie() + "' AND folio = " + detalles.get(i).getFolio();
            db.execSQL(sql);
        }
    }

    //Obtener Cobranza si Existe
    public Pago getPago(String id_visita) {
        Pago item = new Pago();
        ArrayList<Det_Pago> detalles = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "id_pago," +
                "id_cliente," +
                "id_visita," +
                "fecha," +
                "enviado " +
                "FROM pago " +
                "WHERE id_visita = '" + id_visita + "'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            //Rellenar Pago
            item.setId_pago(rs.getString(0));
            item.setId_cliente(rs.getString(1));
            item.setId_visita(rs.getString(2));
            item.setFecha(rs.getString(3));
            item.setEnviado(rs.getInt(4));

            //Obtener Detalles
            sql = "SELECT " +
                    "id_pago," +
                    "folio," +
                    "serie," +
                    "importe_pago," +
                    "forma_pago," +
                    "comentarios," +
                    "saldo_anterior " +
                    "FROM det_pago " +
                    "WHERE id_pago = '"+item.getId_pago()+"'";

            //Ejecutar Consulta
            Cursor c = db.rawQuery(sql,null);
            if(c.moveToFirst()){
                do{
                    Det_Pago detalle = new Det_Pago();

                    detalle.setId_pago(c.getString(0));
                    detalle.setFolio(c.getInt(1));
                    detalle.setSerie(c.getString(2));
                    detalle.setImporte_pago(c.getDouble(3));
                    detalle.setForma_pago(c.getString(4));
                    detalle.setComentarios(c.getString(5));
                    detalle.setSaldo_ant(c.getDouble(6));
                    detalle.setSaldo(c.getDouble(6)-c.getDouble(3));

                    detalles.add(detalle);
                }while(c.moveToNext());
            }

            //Agregar Detalles al pago
            item.setDetalles(detalles);

            //Regresar pago en caso de que existe
            return item;
        } else {
            return null;
        }

    }

    /******* NO VENTA ********/

    //Guaradr NoVenta a un Cliente
    public void setNoVenta(NoVenta item) {
        db = this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_noventa", Main.NEWID());
        values.put("id_visita", item.getId_visita());
        values.put("id_cliente", item.getId_cliente());
        values.put("id_motivo", item.getId_motivo());
        values.put("comentario", item.getComentario());
        values.put("uuid_imagen", item.getImagen());
        values.put("enviado", 0);

        if(db.insert("noventa", null, values) != -1)
            Toast.makeText(context, "No Venta Registrada Correctamente", Toast.LENGTH_SHORT).show();
    }

    //Obtener los Motivos de No Venta
    public ArrayList<Motivo> getMotivosNoVenta(){
        ArrayList<Motivo> data = new ArrayList<Motivo>();
        db = this.getReadableDatabase();
        String sql = "SELECT * FROM motivo_no_venta";
        Cursor rs = db.rawQuery(sql,null);
        if(rs.moveToFirst()){
            do{
                Motivo motivo = new Motivo();

                motivo.setId_motivo(rs.getInt(0));
                motivo.setDescripcion(rs.getString(1));

                data.add(motivo);

            }while(rs.moveToNext());
        }

        return data;
    }


    /********* REPORTES ************/

    //REPORTE DEVOLUCIONES
    public ArrayList<Sobrantes> getSobrantes() {
        ArrayList<Sobrantes> data = new ArrayList<>();
        db = this.getReadableDatabase();

        //Obtener La cantidad Inicial del producto
        String sql = "SELECT " +
                "c.id_producto," +
                "d.descripcion," +
                "c.marca," +
                "(c.cantidad * p.conversion) " +
                "FROM carga c " +
                "INNER JOIN conversion p " +
                "ON p.id_producto = c.id_producto " +
                "INNER JOIN producto d " +
                "ON d.id_producto = c.id_producto " +
                "WHERE c.id_unidad = p.id_unidad " +
                "ORDER BY d.orden";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                Sobrantes item = new Sobrantes();
                item.setId_producto(rs.getString(0));
                item.setDescripcion(rs.getString(1));
                item.setMarca(rs.getString(2));
                item.setCarga(rs.getDouble(3));

                //Ventas Totales
                String ventas = "SELECT SUM(cantidad_minima) FROM det_venta WHERE id_producto = '" + rs.getString(0) + "'";
                Cursor det_venta = db.rawQuery(ventas,null);
                if(det_venta.moveToFirst())
                    item.setVentas(det_venta.getDouble(0));

                //Cambios Totales
                String cambios = "SELECT SUM(cantidad_minima_in) FROM det_cambio WHERE id_producto_recibido = '" + rs.getString(0) + "'";
                Cursor det_cambio = db.rawQuery(cambios,null);
                if(det_cambio.moveToFirst())
                    item.setCambios(det_cambio.getDouble(0));

                //Devoluciones Totales
                String devol = "SELECT SUM(cantidad_minima) FROM det_devolucion WHERE id_producto = '" + rs.getString(0) + "'";
                Cursor det_dev = db.rawQuery(devol,null);
                if(det_dev.moveToFirst())
                    item.setDevoluciones(det_dev.getDouble(0));

                //Calcular Finales:
                item.setInv_final( (item.getCarga() - item.getVentas() + item.getDevoluciones()) );
                item.setInv_mal( (item.getCambios() + item.getDevoluciones()) );
                item.setInv_buen( (item.getCarga() - item.getVentas() - item.getCambios()) );

                data.add(item);
            }while(rs.moveToNext());
        }

        return data;
    }

    //REPORTE VISITAS
    public ArrayList<String> getPorcentajes() {
        ArrayList<String> data = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql = "SELECT COUNT(id_cliente) FROM secuencia";
        Cursor rs = db.rawQuery(sql, null);

        if(rs.moveToFirst()){
            double total = rs.getInt(0);
            double visitas = 0;
            Log.e("Total Empleados", "" + total);

            double visitados = 0;
            double sinvisita = 0;
            sql = "SELECT " +
                    "DISTINCT(c.id_cliente)," +
                    "c.nombre," +
                    "c.rfc," +
                    "c.razon_social," +
                    "c.visitado," +
                    "s.secuencia " +
                    "FROM cliente c " +
                    "INNER JOIN secuencia s " +
                    "ON s.id_cliente = c.id_cliente " +
                    "WHERE c.visitado = 1";

            Cursor v = db.rawQuery(sql, null);
            if(v.moveToFirst()){
                Log.e("Result", v.getCount() + "");
                visitas = v.getCount();
            }

            visitados = (visitas * 100);// / total;

            visitados = visitados / total;

            sinvisita = 100 - visitados;

            data.add(FormatNumber(visitados) + " % (" + (int) visitas + ")");
            data.add(FormatNumber(sinvisita) + " % (" + (int) (total-visitas) + ")");

        }

        return data;
    }

    public ArrayList<Cliente> getReporteVisitas(){
        db = this.getReadableDatabase();
        ArrayList<Cliente> data = new ArrayList<>();
        String sql;
        sql = "SELECT " +
                "DISTINCT(c.id_cliente)," +
                "c.nombre," +
                "c.rfc," +
                "c.razon_social," +
                "c.visitado," +
                "s.secuencia " +
                "FROM cliente c " +
                "INNER JOIN secuencia s " +
                "ON s.id_cliente = c.id_cliente " +
                "WHERE c.visitado = 1 " +
                "ORDER BY s.secuencia";

        Cursor rs = db.rawQuery(sql,null);
        if(rs.moveToFirst()){
            do{
                Cliente cliente=new Cliente();

                cliente.setId_cliente(rs.getString(0));
                cliente.setNombre(rs.getString(1));
                cliente.setRfc(rs.getString(2));
                cliente.setRazon_social(rs.getString(3));
                if(rs.getString(4).equalsIgnoreCase("true"))
                    cliente.setVisitado(true);
                else
                    cliente.setVisitado(false);

                data.add(cliente);
            }while(rs.moveToNext());
        }

        return data;
    }

    //REPORTE DE EFECTIVIDAD
    public  ArrayList<Efectividad> getEfectividad() {
        db = this.getReadableDatabase();
        ArrayList<Efectividad> data=new ArrayList<>();
        String sql;
        sql = "SELECT " +
                "DISTINCT(c.id_cliente)," +
                "c.nombre," +
                "c.rfc," +
                "c.razon_social," +
                "c.visitado," +
                "s.secuencia " +
                "FROM cliente c " +
                "INNER JOIN secuencia s " +
                "ON s.id_cliente = c.id_cliente " +
                "WHERE c.visitado = 1 " +
                "ORDER BY s.secuencia";

        Cursor rs = db.rawQuery(sql,null);
        if(rs.moveToFirst()){
            do{
                Efectividad cliente = new Efectividad();

                cliente.setId_cliente(rs.getString(0));
                cliente.setNombre(rs.getString(1));
                cliente.setRfc(rs.getString(2));
                cliente.setRazon_social(rs.getString(3));

                //Saber si se le vendio
                String is = "SELECT " +
                        "COUNT(*) " +
                        "FROM venta " +
                        "WHERE id_cliente = '" + rs.getString(0) + "'";
                Cursor gV = db.rawQuery(is, null);
                if(gV.moveToFirst()) {
                    if (gV.getInt(0) > 0)
                        cliente.setVentas(true);
                    else
                        cliente.setVentas(false);
                }

                gV.close();

                //Saber si se le Cambio
                is = "SELECT " +
                        "COUNT(*) " +
                        "FROM cambio " +
                        "WHERE id_cliente = '" + rs.getString(0) + "'";
                Cursor gC = db.rawQuery(is, null);
                if(gC.moveToFirst()){
                    if(gC.getInt(0) > 0)
                        cliente.setCambios(true);
                    else
                        cliente.setCambios(false);
                }

                //Saber si Devolvio
                is = "SELECT " +
                        "COUNT(*) " +
                        "FROM devolucion " +
                        "WHERE id_cliente = '" + rs.getString(0) + "'";
                Cursor gD = db.rawQuery(is, null);
                if(gD.moveToFirst()){
                    if(gD.getInt(0) > 0)
                        cliente.setDevoluciones(true);
                    else
                        cliente.setDevoluciones(false);
                }

                data.add(cliente);
            }while(rs.moveToNext());
        }

        return data;
    }

    //REPORTE VENTAS
    public String getVentasxProd(String id_producto){
        db = this.getReadableDatabase();
        double total_c_venta = 0;
        double total_x_producto = 0;

        //
        String sqlCount = "SELECT COUNT(DISTINCT id_cliente) FROM venta";

        Cursor t_c_v = db.rawQuery(sqlCount, null);
        if(t_c_v.moveToFirst())
            total_c_venta = t_c_v.getInt(0);

        String sql = "SELECT " +
                "COUNT(DISTINCT v.id_cliente) " +
                "FROM venta v " +
                "INNER JOIN det_venta d " +
                "ON d.id_venta = v.id_venta " +
                "WHERE d.id_producto = '" + id_producto + "'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst())
            total_x_producto = rs.getInt(0);


        double percent = total_x_producto * 100;
        if(percent > 0)
            percent = percent / total_c_venta;
        else
            percent = 0;

        return percent +"% (" + (int) total_x_producto + " de " + (int) total_c_venta +")";
    }

    /********* ENVIO DE INFORMACION **********/
    public JSONObject getData(boolean fin, String id_usuario) {
        JSONObject data = new JSONObject();
        db = this.getReadableDatabase();
        String sql = "";
        //Obtener Data
        try {
            //Obtener la Jornada si se solicita
            JSONObject jornada = new JSONObject();
            if(fin) {
                sql = "SELECT " +
                        "id_jornada," +
                        "id_usuario," +
                        "fecha," +
                        "hora_inicio," +
                        "gps_inicio," +
                        "bateria_inicio," +
                        "hora_fin," +
                        "gps_fin," +
                        "bateria_fin " +
                        "FROM jornada " +
                        "WHERE id_usuario = '" + id_usuario + "'";
                Cursor j = db.rawQuery(sql, null);
                if(j.moveToFirst())  {
                    jornada.put("id_jornada", j.getString(0));
                    jornada.put("id_usuario", j.getString(1));
                    jornada.put("fecha", j.getString(2));
                    jornada.put("hora_inicio", j.getString(3));
                    jornada.put("gps_inicio", j.getString(4));
                    jornada.put("bateria_inicio", j.getInt(5));
                    if(j.getString(6) != null)
                        jornada.put("hora_fin", j.getString(6));
                    else
                        jornada.put("hora_fin", "00:00");

                    if(j.getString(7) != null)
                        jornada.put("gps_fin", j.getString(7));
                    else
                        jornada.put("gps_fin", "0.0");

                    if(j.getString(8) != null)
                        jornada.put("bateria_fin", j.getInt(8));
                    else
                        jornada.put("bateria_fin", 0);
                }

                //Agregar Jornada a los Datos a Enviar
                data.put("Jornada", jornada);
            }

            /*********** OBTENER VISITAS *********/

            JSONArray visitas = new JSONArray();
            JSONObject item = new JSONObject();
            sql="SELECT " +
                    "id_visita," +
                    "id_jornada," +
                    "id_usuario," +
                    "id_cliente," +
                    "fecha," +
                    "hora_inicio," +
                    "gps_inicio," +
                    "bateria_inicio," +
                    "hora_fin," +
                    "gps_fin," +
                    "bateria_fin," +
                    "ult_modificacion " +
                    "FROM visita " +
                    "WHERE id_usuario = '" + id_usuario + "'";
            Cursor vst = db.rawQuery(sql,null);
            if(vst.moveToFirst()) {
                do {
                    //Reiniciar Item
                    item = new JSONObject();
                    item.put("id_visita", vst.getString(0));
                    item.put("id_jornada", vst.getString(1));
                    item.put("id_usuario", vst.getString(2));
                    item.put("id_cliente", vst.getString(3));
                    item.put("fecha", vst.getString(4));
                    item.put("hora_inicio", vst.getString(5));
                    item.put("gps_inicio", vst.getString(6));
                    item.put("bateria_inicio", vst.getInt(7));

                    //** Verificar HoraFin
                    if (vst.getString(8) != null)
                        item.put("hora_fin", vst.getString(8));
                    else
                        item.put("hora_fin", "0:00:00");

                    //**Verificar GPSFin
                    if (vst.getString(9) != null)
                        item.put("gps_fin", vst.getString(9));
                    else
                        item.put("gps_fin", "0.0,0.0");

                    //**Verificar BateriaFin
                    item.put("bateria_fin", vst.getInt(10));

                    if (vst.getString(11) != null)
                        item.put("ult_modificacion", vst.getString(11));
                    else
                        item.put("ult_modificacion", vst.getString(11));


                    //Add to Array
                    visitas.put(item);
                } while (vst.moveToNext());
            }

            /******* OBETNER VENTAS **********/

            JSONArray ventas = new JSONArray();
            sql = "SELECT " +
                    "id_venta," +
                    "id_visita," +
                    "id_cliente," +
                    "subtotal," +
                    "impuestos," +
                    "total," +
                    "empresa," +
                    "serie," +
                    "folio," +
                    "forma_venta " +
                    "FROM venta " +
                    "WHERE enviado = 0";
            Cursor vts = db.rawQuery(sql,null);
            if(vts.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_venta", vts.getString(0));
                    item.put("id_visita", vts.getString(1));
                    item.put("id_cliente", vts.getString(2));
                    item.put("subtotal", vts.getDouble(3));
                    item.put("impuestos", vts.getDouble(4));
                    item.put("total", vts.getDouble(5));
                    item.put("empresa", vts.getString(6));
                    item.put("serie", vts.getString(7));
                    item.put("folio", vts.getInt(8));
                    item.put("forma_venta", vts.getString(9));

                    //Add to Array
                    ventas.put(item);

                } while (vts.moveToNext());
            }

            /*****OBTENER DETALLES DE LA VENTA*/

            JSONArray det_ventas = new JSONArray();
            sql="SELECT " +
                    "d.id_det_venta," +
                    "d.id_venta," +
                    "d.id_producto," +
                    "d.id_unidad," +
                    "d.cantidad," +
                    "d.subtotal," +
                    "d.impuestos," +
                    "d.total," +
                    "d.partida," +
                    "d.cantidad_minima " +
                    "FROM det_venta d " +
                    "INNER JOIN venta v " +
                    "ON d.id_venta = v.id_venta " +
                    "WHERE v.enviado = 0";
            Cursor d_vts = db.rawQuery(sql,null);
            if(d_vts.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_det_venta", d_vts.getString(0));
                    item.put("id_venta", d_vts.getString(1));
                    item.put("id_producto", d_vts.getString(2));
                    item.put("id_unidad", d_vts.getString(3));
                    item.put("cantidad", d_vts.getDouble(4));
                    item.put("subtotal", d_vts.getDouble(5));
                    item.put("impuestos", d_vts.getDouble(6));
                    item.put("total", d_vts.getDouble(7));
                    item.put("partida", d_vts.getInt(8));
                    item.put("cantidad_minima", d_vts.getDouble(9));

                    //Add to Array
                    det_ventas.put(item);

                } while (d_vts.moveToNext());
            }

            /***** OBTENER CAMBIOS *******/
            JSONArray cambios = new JSONArray();
            sql="SELECT " +
                    "id_cambio," +
                    "id_cliente," +
                    "id_visita," +
                    "empresa " +
                    "FROM cambio " +
                    "WHERE enviado = 0";
            Cursor cbs = db.rawQuery(sql,null);
            if(cbs.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_cambio", cbs.getString(0));
                    item.put("id_cliente", cbs.getString(1));
                    item.put("id_visita", cbs.getString(2));
                    item.put("empresa", cbs.getString(3));

                    //Add to Array
                    cambios.put(item);

                } while (cbs.moveToNext());
            }

            /******* OBTENER DETALLES DE CAMBIOS */
            JSONArray det_cambios = new JSONArray();
            sql="SELECT " +
                    "c.id_det_cambio," +
                    "c.id_cambio," +
                    "c.id_motivo," +
                    "c.cantidad_in," +
                    "c.cantidad_out," +
                    "c.id_producto_recibido," +
                    "c.id_producto_entregado," +
                    "c.id_unidad_in," +
                    "c.id_unidad_out," +
                    "c.partida," +
                    "c.cantidad_minima_in " +
                    "FROM det_cambio c " +
                    "INNER JOIN cambio d " +
                    "ON c.id_cambio = d.id_cambio " +
                    "WHERE d.enviado = 0";
            Cursor d_cbs = db.rawQuery(sql,null);
            if(d_cbs.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_det_cambio", d_cbs.getString(0));
                    item.put("id_cambio", d_cbs.getString(1));
                    item.put("id_motivo", d_cbs.getInt(2));
                    item.put("cantidad_in", d_cbs.getDouble(3));
                    item.put("cantidad_out", d_cbs.getDouble(4));
                    item.put("id_producto_recibido", d_cbs.getString(5));
                    item.put("id_producto_entregado", d_cbs.getString(6));
                    item.put("id_unidad_in", d_cbs.getString(7));
                    item.put("id_unidad_out", d_cbs.getString(8));
                    item.put("partida", d_cbs.getInt(9));
                    item.put("cantidad_minima_in", d_cbs.getDouble(10));

                    //Add to Array
                    det_cambios.put(item);

                } while (d_cbs.moveToNext());
            }

            /********OBTENER DEVOLUCIONES ******/
            JSONArray devoluciones = new JSONArray();
            sql = "SELECT " +
                    "id_devolucion," +
                    "id_cliente," +
                    "id_visita," +
                    "empresa " +
                    "FROM devolucion " +
                    "WHERE enviado=0";
            Cursor devs = db.rawQuery(sql,null);
            if(devs.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_devolucion", devs.getString(0));
                    item.put("id_cliente", devs.getString(1));
                    item.put("id_visita", devs.getString(2));
                    item.put("empresa", devs.getString(3));

                    //Add to Array
                    devoluciones.put(item);

                } while (devs.moveToNext());
            }

            /*********** OBTENER DETALLES DE LAS DEVOLUCIONES **********/
            JSONArray det_devoluciones = new JSONArray();
            sql="SELECT " +
                    "v.id_det_devolucion," +
                    "v.id_devolucion," +
                    "v.id_producto," +
                    "v.id_motivo," +
                    "v.id_unidad," +
                    "v.cantidad," +
                    "v.partida," +
                    "v.cantidad_minima " +
                    "FROM det_devolucion v " +
                    "INNER JOIN devolucion d " +
                    "ON v.id_devolucion = d.id_devolucion " +
                    "WHERE d.enviado=0";
            Cursor d_devs = db.rawQuery(sql,null);
            if(d_devs.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_det_devolucion", d_devs.getString(0));
                    item.put("id_devolucion", d_devs.getString(1));
                    item.put("id_producto", d_devs.getString(2));
                    item.put("id_motivo", d_devs.getInt(3));
                    item.put("id_unidad", d_devs.getString(4));
                    item.put("cantidad", d_devs.getDouble(5));
                    item.put("partida", d_devs.getInt(6));
                    item.put("cantidad_minima", d_devs.getDouble(7));

                    //Add to Array
                    det_devoluciones.put(item);

                } while (d_devs.moveToNext());
            }

            /****** OBTENER PAGOS **********/
            JSONArray pagos = new JSONArray();
            sql = "SELECT " +
                    "id_pago," +
                    "id_visita," +
                    "id_cliente," +
                    "fecha " +
                    "FROM pago " +
                    "WHERE enviado = 0";
            Cursor pgs = db.rawQuery(sql,null);
            if(pgs.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_pago", pgs.getString(0));
                    item.put("id_visita", pgs.getString(1));
                    item.put("id_cliente", pgs.getString(2));
                    item.put("fecha", pgs.getString(3));

                    //Add to Array
                    pagos.put(item);

                } while (pgs.moveToNext());
            }

            /****** OBTENER DETALLES DE LOS PAGOS ********/
            JSONArray det_pagos = new JSONArray();
            sql = "SELECT " +
                    "d.id_det_pago," +
                    "d.id_pago," +
                    "d.importe_pago," +
                    "d.serie," +
                    "d.folio," +
                    "d.forma_pago," +
                    "d.comentarios " +
                    "FROM det_pago d " +
                    "INNER JOIN pago p " +
                    "ON d.id_pago = p.id_pago " +
                    "WHERE p.enviado = 0";
            Cursor d_pgs = db.rawQuery(sql,null);
            if(d_pgs.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_det_pago",d_pgs.getString(0));
                    item.put("id_pago",d_pgs.getString(1));
                    item.put("importe_pago",d_pgs.getDouble(2));
                    item.put("serie",d_pgs.getString(3));
                    item.put("folio",d_pgs.getInt(4));
                    item.put("forma_pago",d_pgs.getString(5));
                    if(d_pgs.getString(6)!=null)
                        item.put("comentarios",d_pgs.getString(6));
                    else
                        item.put("comentarios","** Sin comentarios **");

                    //Add to Array
                    det_pagos.put(item);

                } while (d_pgs.moveToNext());
            }

            /***** OBTENER NO VENTAS*/
            JSONArray n_ventas = new JSONArray();
            sql = "SELECT " +
                    "id_noventa," +
                    "id_visita," +
                    "id_cliente," +
                    "id_motivo," +
                    "comentario," +
                    "uuid_imagen " +
                    "FROM noventa " +
                    "WHERE enviado = 0";
            Cursor nv = db.rawQuery(sql, null);
            if (nv.moveToFirst()) {
                do {
                    item = new JSONObject();
                    item.put("id_noventa", nv.getString(0));
                    item.put("id_visita", nv.getString(1));
                    item.put("id_cliente", nv.getString(2));
                    item.put("id_motivo", nv.getInt(3));
                    if(nv.getString(4) != null)
                        item.put("comentario", nv.getString(4));
                    else
                        item.put("comentario", "Sin comentarios");
                    if(nv.getString(5) != null)
                        item.put("uuid_imagen", nv.getString(5));
                        //item.put("uuid_imagen", "No Photo");
                    else
                        item.put("uuid_imagen", "No Photo");

                    //Add to Array
                    n_ventas.put(item);

                } while (nv.moveToNext());
            }

            //Agregar Todos los Array al Objeto Final

            if(fin)
                data.put("Jornada",jornada);
            data.put("Visitas", visitas);
            data.put("Ventas", ventas);
            data.put("Det_Ventas", det_ventas);
            data.put("Cambios", cambios);
            data.put("Det_Cambios", det_cambios);
            data.put("Devoluciones", devoluciones);
            data.put("Det_Devoluciones", det_devoluciones);
            data.put("Pagos", pagos);
            data.put("Det_Pagos", det_pagos);
            data.put("No_Ventas", n_ventas);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Data --> ", data.toString());
        return data;
    }

    /* METODOS EXTRAS*/

    //Resumen de Ventas
    public ArrayList<VentaResumen> getResumenVentas(){
        ArrayList<VentaResumen> data = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " +
                "empresa," +
                "forma_venta," +
                "total," +
                "id_forma_venta " +
                "FROM venta";

        Cursor c = db.rawQuery(sql,null);
        if(c.moveToFirst()){
            do{
                VentaResumen item = new VentaResumen();

                item.setEmpresa(c.getString(0));
                item.setForma_venta(c.getString(1));
                item.setTotal(c.getDouble(2));
                item.setId_forma_venta(c.getInt(3));

                data.add(item);
            }while(c.moveToNext());
        }else{
            VentaResumen item = new VentaResumen();
            item.setEmpresa("LA JOLLA");
        }
        return data;
    }

    //Formater Nuemros a 2 Decimales
    public String FormatNumber(double n){
        NumberFormat nf = new DecimalFormat("#0.00");

        return nf.format(n);
    }

    //Eliminar Datos de Visita en caso de Omitir
    public void OmitirVisita(String id_visita) {
        db = this.getWritableDatabase();
        String sql = "";

        //Eliminar Ventas
        sql = "SELECT " +
                "id_venta " +
                "FROM venta " +
                "WHERE id_visita = '" + id_visita + "'";
        Cursor ventas = db.rawQuery(sql, null);
        if(ventas.moveToFirst()) {
            String id_venta = ventas.getString(0);
            db.delete("det_venta", "id_venta = '" + id_venta + "'", null);
            db.delete("venta", "id_venta = '" + id_venta + "'", null);
        }

        //Eliminar Cambios
        sql = "SELECT " +
                "id_cambio " +
                "FROM cambio " +
                "WHERE id_visita = '" + id_visita + "'";
        Cursor cambios = db.rawQuery(sql, null);
        if(cambios.moveToFirst()) {
            String id_cambio = cambios.getString(0);
            db.delete("det_cambio", "id_cambio = '" + id_cambio + "'", null);
            db.delete("cambio", "id_cambio = '" + id_cambio + "'", null);
        }

        //Eliminar Devoluciones
        sql = "SELECT " +
                "id_devolucion " +
                "FROM devolucion " +
                "WHERE id_visita = '" + id_visita + "'";
        Cursor dev = db.rawQuery(sql, null);
        if(dev.moveToFirst()) {
            String id_dev = dev.getString(0);
            db.delete("det_devolucion", "id_devolucion = '" + id_dev + "'", null);
            db.delete("devolucion", "id_devolucion = '" + id_dev + "'", null);
        }

        //Eliminar Pagos
        sql = "SELECT " +
                "id_venta " +
                "FROM venta " +
                "WHERE id_visita = '" + id_visita + "'";
        Cursor pagos = db.rawQuery(sql, null);
        if(pagos.moveToFirst()) {
            String id_pago = pagos.getString(0);
            db.delete("det_pago", "id_pago = '" + id_pago + "'", null);
            db.delete("pago", "id_pago = '" + id_pago + "'", null);
        }

        //Eliminar No Ventas
        db.delete("noventa", "id_visita = '" + id_visita + "'", null);
    }

    //Eliminar lo Enviado

    //Resetear toda la DB
    public void resetDB() {
        db = this.getWritableDatabase();
        db.delete("carga", null, null);
        db.delete("cliente", null, null);
        db.delete("conversion", null, null);
        db.delete("equivalencia", null, null);
        db.delete("facturas", null, null);
        db.delete("folio_serie", null, null);
        db.delete("inventario", null, null);
        db.delete("listas_precios", null, null);
        db.delete("motivo_devolucion", null, null);
        db.delete("motivo_no_venta", null, null);
        db.delete("producto", null, null);
        db.delete("secuencia", null, null);
        db.delete("unidades", null, null);

    }

    //Limpiar Base de Datos
    public void clearDB(ArrayList<Result> data) {
        db = this.getWritableDatabase();

        try {
            for (Result item : data) {
                //Jornada
                if (item.getNombre().equals("Jornada")) {
                    db.delete("jornada", "id_usuario = '" + dbc.getlogin().getId_usuario() + "'", null);
                    resetDB();
                }

                //Visitas
                else if (item.getNombre().equals("Visitas")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Marcar Clientes como no Visitados
                        ContentValues v = new ContentValues();
                        v.put("visitado", "false");
                        String sql = "SELECT id_cliente FROM visita WHERE id_visita = '" + id + "'";
                        Cursor rs = db.rawQuery(sql, null);
                        if(rs.moveToFirst())
                            db.update("cliente", v, "id_cliente = '" + rs.getString(0) + "'", null);
                        //Eliminar Registro Correcto
                        db.delete("visita", "id_visita = '" + id + "'", null);
                    }
                }

                //Ventas
                else if (item.getNombre().equals("Ventas")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("venta", "id_venta = '" + id + "'", null);
                    }

                }

                //Detales Venta
                else if (item.getNombre().equals("Detalles Ventas")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("det_venta", "id_det_venta = '" + id + "'", null);
                    }

                }

                //Cambios
                else if (item.getNombre().equals("Cambios")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("cambio", "id_cambio = '" + id + "'", null);
                    }

                }

                //Detalles Cambios
                else if (item.getNombre().equals("Detalles Cambios")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("det_cambio", "id_det_cambio = '" + id + "'", null);
                    }

                }

                //Devoluciones
                else if (item.getNombre().equals("Devoluciones")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("devolucion", "id_devolucion = '" + id + "'", null);
                    }

                }

                //Detalles Devoluciones
                else if (item.getNombre().equals("Detalles Devoluciones")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("det_devolucion", "id_det_devolucion = '" + id + "'", null);
                    }

                }

                //pagos
                else if (item.getNombre().equals("Pagos")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("pago", "id_pago = '" + id + "'", null);
                    }

                }

                //detalles Pagos
                else if (item.getNombre().equals("Detalles Pagos")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("det_pago", "id_det_pago = '" + id + "'", null);
                    }

                }

                //No Ventas
                else if (item.getNombre().equals("No Venta")) {
                    for (int i = 0; i < item.getCorrectos().length(); i++) {
                        String id = item.getCorrectos().getString(i);

                        //Eliminar Registro Correcto
                        db.delete("noventa", "id_noventa = '" + id + "'", null);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setTerminado(){
        db = this.getReadableDatabase();
        String sql = "UPDATE visita " +
                "SET terminado = 1";
        db.execSQL(sql);
    }


    //Obtener el Total de Cobranza
    public double getTotalCobranza() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago " +
                "FROM det_pago";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCobranzaLP() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'L%'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCobranzaCS() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'C%'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCRLPEfectivo() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'L%' AND forma_pago='Efectivo'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCRCSEfectivo() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'C%' AND forma_pago='Efectivo'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCRLPCheques() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'L%' AND forma_pago='Cheque'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCRCSCheques() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'C%' AND forma_pago='Cheque'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCRLPTranferencia() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'L%' AND forma_pago='Tranferencia'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Obtener el Total de Cobranza
    public double getTotalCRCSTranferencia() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe_pago," +
                "serie " +
                "FROM det_pago " +
                "WHERE serie LIKE 'C%' AND forma_pago='Tranferencia'";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return total;
    }

    //Get Ultimo Folio
    public double getLastFolio() {
        double folio = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "folio " +
                "FROM venta " +
                "ORDER BY folio DESC";

        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do {
                folio = rs.getDouble(0);
            }while(rs.moveToNext());
        }


        return folio;
    }


    //Obtener Total deDevoluciones
    public double getTotalDevolucion() {
        double total = 0;
        db = this.getReadableDatabase();
        String sql = "SELECT " +
                "importe " +
                "FROM det_devolucion";
        Cursor rs = db.rawQuery(sql, null);
        if(rs.moveToFirst()) {
            do{
                total += rs.getDouble(0);
            }while(rs.moveToNext());
        }

        return total;
    }
}
