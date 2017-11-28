package com.bybick.lacteosjolla.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.Forma_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Visita;
import com.bybick.lacteosjolla.R;

import java.util.ArrayList;

/**
 * Created by Clowny on 10/28/2017.
 */

public class F_NoCredito extends Fragment {
    Context context;
    FragmentManager fmMain;
    FragmentManager fm;
    ActionBar tb;
    Integer id;
    ArrayList<Forma_Venta> formas;

    //DATA
    DBData dbd;
    Visita visita;
    Cliente cliente;


    public void setAuxiliares (Visita visita, Cliente cliente){
        this.visita = visita;
        this.cliente = cliente;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        formas = dbd.getFormas(cliente.getId_cliente());
        id = formas.get(0).getId_forma();

        View v;

        if (id == 3){
            v = inflater.inflate(R.layout.f_credito, container, false);
        } else {
            v = inflater.inflate(R.layout.f_credito2, container, false);
        }

//        switch (id){
//            case 3: {
//                v = inflater.inflate(R.layout.f_credito, container, false);
//            } break;
//            case 4: {
//                v = inflater.inflate(R.layout.f_credito2, container, false);
//            } break;
//        }
        return v;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setFmMain(FragmentManager fmMain) {
        this.fmMain = fmMain;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
