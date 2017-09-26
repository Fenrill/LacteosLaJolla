package com.bybick.lacteosjolla.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectIN.Motivo;
import com.bybick.lacteosjolla.ObjectOUT.NoVenta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by bicktor on 25/06/2016.
 */
public class F_noVenta extends Fragment implements View.OnClickListener{
    Context context;
    FragmentManager fmMain;
    ActionBar tb;

    //DATA
    Visita visita;
    Cliente cliente;

    NoVenta no_venta;
    ArrayList<Motivo> motivos;
    F_noVenta fragmento;

    //Vistas
    Spinner spMotivos;
    EditText editComentarios;
    ImageView imgFoto;
    TextView txtNueva;
    FloatingActionButton btnEnd;


    //DbataBases
    DBData dbd;

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

        dbd = new DBData(context);
        dbd.open();

        fragmento = this;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.f_noventa, container, false);
        getViews(v);
        motivos = dbd.getMotivosNoVenta();

        ArrayAdapter<Motivo> adapter = new ArrayAdapter<Motivo>(getActivity(), android.R.layout.simple_spinner_item, motivos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivos.setAdapter(adapter);
        spMotivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }


    public void getViews(View v) {
        spMotivos = (Spinner) v.findViewById(R.id.spMotivo);
        editComentarios = (EditText) v.findViewById(R.id.editComentarios);
        imgFoto = (ImageView) v.findViewById(R.id.img_Camera);
        imgFoto.setOnClickListener(this);
        btnEnd = (FloatingActionButton) v.findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Camera : {
                try {
                    Intent galeryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(galeryIntent, 1888);
                }catch(Exception ex){
                    Toast.makeText(context, "Error en la captura", Toast.LENGTH_SHORT).show();
                }
            }break;

            case R.id.btnEnd : {
                NoVenta nv = new NoVenta();

                nv.setId_cliente(cliente.getId_cliente());
                nv.setId_visita(visita.getId_visita());

                if(!motivos.isEmpty())
                    nv.setId_motivo(motivos.get(spMotivos.getSelectedItemPosition()).getId_motivo());
                else
                    nv.setId_motivo(0);

                if(!editComentarios.getText().toString().isEmpty())
                    nv.setComentario(editComentarios.getText().toString());
                else
                    nv.setComentario("Sin Comentarios");

                //Imagen
                imgFoto.buildDrawingCache();
                Bitmap bitmap = imgFoto.getDrawingCache();

                ByteArrayOutputStream st = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, st);
                byte[] image = st.toByteArray();

                String temp = Base64.encodeToString(image, Base64.DEFAULT);
                nv.setImagen(temp);

                //Guardar en la DB
                dbd.setNoVenta(nv);

                //Se relizo movimiento
                F_Visita.movimientos = true;

                //Remover
                FragmentTransaction ft = fmMain.beginTransaction();
                ft.remove(fragmento).commit();

            }break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1888) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    imgFoto.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
                }
            }
        }
    }
}
