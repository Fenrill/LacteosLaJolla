package com.bybick.lacteosjolla;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.Fragments.F_Login;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Main extends AppCompatActivity implements LocationListener {

    Context context;
    FragmentManager fm;

    Toolbar tb;
    ActionBar bar;

    boolean press = false;

    //Ubicaccion
    public static Location mylocation;
    public static LocationManager location;
    private static final int REQUEST_ENABLE_BT = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Contexto de la Aplicacion
        context = this;

        //Fragmanet Managar para el manejo de los Fragmentos
        fm = getFragmentManager();

        //Obtener la Barra de titulo
        tb = (Toolbar) findViewById(R.id.my_ActionBar);
        setSupportActionBar(tb);
        bar = getSupportActionBar();
        bar.hide();

        //Cargar el Login
        F_Login frag = new F_Login();
        frag.setContext(context);
        frag.setTb(bar);

        fm.beginTransaction().replace(R.id.Container, frag, "Login").commit();

        //Location Listener
        location = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        //Activar GPS
        if(!location.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        //Activar BT
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Timer time = new Timer();
        if(press){
            //Finalizar la aplicación
            System.exit(0);
            //cancelar el contador ya que se salio de la Aplicación
            time.cancel();
        }else {
            if (fm.getBackStackEntryCount() > 0) {
                //Hacer un regresa a atras
                if(fm.findFragmentByTag("Visita") != null){
                    if(fm.findFragmentByTag("Productos") == null)
                        Toast.makeText(context, "Finaliza la Visita para Salir.", Toast.LENGTH_SHORT).show();
                    else
                        fm.popBackStack();
                } else {
                    fm.popBackStack();
                }
            } else {
                if (fm.findFragmentByTag("Login") != null) {
                    //Finalizar aplicación
                    System.exit(0);
                } else {
                    //Mostrar mensaje para confirmar Salida
                    Toast.makeText(context, "Presione una vez más para Salir.", Toast.LENGTH_SHORT).show();
                    //POneren espera el segundo toque a atras
                    press = true;
                    //Contador de 3 seg. para regresar la variable a false
                    time.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            press = false;
                        }
                    }, 3000);
                }
            }
        }
    }

    //Obtener Craga de Bateria
    public static int batteryLevel(Context context){

        Intent intent  = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int    level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int    scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int    percent = (level*100)/scale;
        return level;
        //return String.valueOf(percent) + "%";
    }

    public static String getFecha(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        return df.format(c.getTime());
    }

    public static String getGPS(){
        if(mylocation != null)
            return mylocation.getLatitude() + "," + mylocation.getLongitude();
        else
            return "0.0,0.0";
    }

    public static String getHora(){
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        return c.getTime().getHours()+":"+c.getTime().getMinutes()+":"+c.getTime().getSeconds();
    }

    public static String NEWID(){
        String result= UUID.randomUUID().toString();
        result.replaceAll("-","");
        result.substring(0,32);

        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if(mylocation!=null) {
            //Log.e("GPS Test",""+location.getLongitude()+","+location.getLatitude());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
