package com.bybick.lacteosjolla.Fragments;

import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.ObjectIN.Efectivo;
import com.bybick.lacteosjolla.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class F_PLiquid extends Fragment implements View.OnClickListener {
    Context context;
    FragmentManager fmMain;
    FragmentManager fm;

    RelativeLayout vista;

    ActionBar tb;
    DBData dbd;
    DBConfig dbc;

    FloatingActionButton btnSave;

    Efectivo efectivo;

    boolean flag;

    //Views
    TextView txtBilletes1;
    TextView txtBilletes2;
    TextView txtBilletes3;
    TextView txtBilletes4;
    TextView txtBilletes5;
    TextView txtBilletes6;

    TextView txtMonedas1;
    TextView txtMonedas2;
    TextView txtMonedas3;
    TextView txtMonedas4;
    TextView txtMonedas5;
    TextView txtMonedas6;
    TextView txtMonedas7;
    TextView txtMonedas8;
    TextView txtMonedas9;
    TextView txtMonedas10;

    EditText cntBilletes1;
    EditText cntBilletes2;
    EditText cntBilletes3;
    EditText cntBilletes4;
    EditText cntBilletes5;
    EditText cntBilletes6;
    EditText cntMonedas1;
    EditText cntMonedas2;
    EditText cntMonedas3;
    EditText cntMonedas4;
    EditText cntMonedas5;
    EditText cntMonedas6;
    EditText cntMonedas7;
    EditText cntMonedas8;
    EditText cntMonedas9;
    EditText cntMonedas10;
    EditText cntPracticaja;

    Integer billetes1 = 0;
    Integer billetes2 = 0;
    Integer billetes3 = 0;
    Integer billetes4 = 0;
    Integer billetes5 = 0;
    Integer billetes6 = 0;
    Integer monedas1 = 0;
    Integer monedas2 = 0;
    Integer monedas3 = 0;
    Integer monedas4 = 0;
    Integer monedas5 = 0;
    Integer monedas6 = 0;
    Integer monedas7 = 0;
    Integer monedas8 = 0;
    Integer monedas9 = 0;
    Integer monedas10 = 0;
    Double practicaja = 0.00;

    public void setContext(Context context){ this.context = context;}

//    public void setEfectivo(boolean efectivo){ this.flag = efectivo;}

    public void setTb(ActionBar tb) { this.tb = tb; }

    public void setFmMain (FragmentManager fmMain) { this.fmMain = fmMain; }

    public void setFm (FragmentManager fmChild) { this.fm = fmChild; }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_pliquid, container, false);
        getViews(v);

        cntBilletes1 = (EditText) v.findViewById(R.id.cntBilletes1);
        cntBilletes2 = (EditText) v.findViewById(R.id.cntBilletes2);
        cntBilletes3 = (EditText) v.findViewById(R.id.cntBilletes3);
        cntBilletes4 = (EditText) v.findViewById(R.id.cntBilletes4);
        cntBilletes5 = (EditText) v.findViewById(R.id.cntBilletes5);
        cntBilletes6 = (EditText) v.findViewById(R.id.cntBilletes6);

        cntMonedas1 = (EditText) v.findViewById(R.id.cntMonedas1);
        cntMonedas2 = (EditText) v.findViewById(R.id.cntMonedas2);
        cntMonedas3 = (EditText) v.findViewById(R.id.cntMonedas3);
        cntMonedas4 = (EditText) v.findViewById(R.id.cntMonedas4);
        cntMonedas5 = (EditText) v.findViewById(R.id.cntMonedas5);
        cntMonedas6 = (EditText) v.findViewById(R.id.cntMonedas6);
        cntMonedas7 = (EditText) v.findViewById(R.id.cntMonedas7);
        cntMonedas8 = (EditText) v.findViewById(R.id.cntMonedas8);
        cntMonedas9 = (EditText) v.findViewById(R.id.cntMonedas9);
        cntMonedas10 = (EditText) v.findViewById(R.id.cntMonedas10);

        cntPracticaja = (EditText) v.findViewById(R.id.cntPracticaja);
        cntPracticaja.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        cntBilletes1.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntBilletes2.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntBilletes3.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntBilletes4.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntBilletes5.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntBilletes6.setFilters(new InputFilter[]{ new FormatNumber(4)});

        cntMonedas1.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas2.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas3.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas4.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas5.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas6.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas7.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas8.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas9.setFilters(new InputFilter[]{ new FormatNumber(4)});
        cntMonedas10.setFilters(new InputFilter[]{ new FormatNumber(4)});


        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbd = new DBData(context);
        dbd.open();

        dbc = new DBConfig(context);
        dbc.open();
    }

    public void getViews(View v){
        tb.setTitle("Pre-Liquidacion");

        btnSave = (FloatingActionButton) v.findViewById(R.id.addPLiquid);
        btnSave.setOnClickListener(this);

        txtBilletes1 = (TextView) v.findViewById(R.id.txtBilletes1);
        txtBilletes2 = (TextView) v.findViewById(R.id.txtBilletes2);
        txtBilletes3 = (TextView) v.findViewById(R.id.txtBilletes3);
        txtBilletes4 = (TextView) v.findViewById(R.id.txtBilletes4);
        txtBilletes5 = (TextView) v.findViewById(R.id.txtBilletes5);
        txtBilletes6 = (TextView) v.findViewById(R.id.txtBilletes6);

        txtMonedas1 = (TextView) v.findViewById(R.id.txtMonedas1);
        txtMonedas2 = (TextView) v.findViewById(R.id.txtMonedas2);
        txtMonedas3 = (TextView) v.findViewById(R.id.txtMonedas3);
        txtMonedas4 = (TextView) v.findViewById(R.id.txtMonedas4);
        txtMonedas5 = (TextView) v.findViewById(R.id.txtMonedas5);
        txtMonedas6 = (TextView) v.findViewById(R.id.txtMonedas6);
        txtMonedas7 = (TextView) v.findViewById(R.id.txtMonedas7);
        txtMonedas8 = (TextView) v.findViewById(R.id.txtMonedas8);
        txtMonedas9 = (TextView) v.findViewById(R.id.txtMonedas9);
        txtMonedas10 = (TextView) v.findViewById(R.id.txtMonedas10);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addPLiquid : {
//                flag = clientes.efectivo;

//                try {
                    practicaja = Double.parseDouble(cntPracticaja.getText().toString());
                    billetes1 = zeroNumber(cntBilletes1.getText().toString());
                    billetes2 = zeroNumber(cntBilletes2.getText().toString());
                    billetes3 = zeroNumber(cntBilletes3.getText().toString());
                    billetes4 = zeroNumber(cntBilletes4.getText().toString());
                    billetes5 = zeroNumber(cntBilletes5.getText().toString());
                    billetes6 = zeroNumber(cntBilletes6.getText().toString());
                    monedas1 = zeroNumber(cntMonedas1.getText().toString());
                    monedas2 = zeroNumber(cntMonedas2.getText().toString());
                    monedas3 = zeroNumber(cntMonedas3.getText().toString());
                    monedas4 = zeroNumber(cntMonedas4.getText().toString());
                    monedas5 = zeroNumber(cntMonedas5.getText().toString());
                    monedas6 = zeroNumber(cntMonedas6.getText().toString());
                    monedas7 = zeroNumber(cntMonedas7.getText().toString());
                    monedas8 = zeroNumber(cntMonedas8.getText().toString());
                    monedas9 = zeroNumber(cntMonedas9.getText().toString());
                    monedas10 = zeroNumber(cntMonedas10.getText().toString());
//                } catch (NumberFormatException e){
//                    practicaja = 0;
//                    billetes1 = 0;
//                    billetes2 = 0;
//                    billetes3 = 0;
//                    billetes4 = 0;
//                    billetes5 = 0;
//                    billetes6 = 0;
//                    monedas1 = 0;
//                    monedas2 = 0;
//                    monedas3 = 0;
//                    monedas4 = 0;
//                    monedas5 = 0;
//                    monedas6 = 0;
//                    monedas7 = 0;
//                    monedas8 = 0;
//                    monedas9 = 0;
//                    monedas10 = 0;
//                }

                efectivo = new Efectivo();
                efectivo = dbd.getEfectivo();

                efectivo.setPracticaja(practicaja);
                efectivo.setBillete_1000(billetes1);
                efectivo.setBillete_500(billetes2);
                efectivo.setBillete_200(billetes3);
                efectivo.setBillete_100(billetes4);
                efectivo.setBillete_50(billetes5);
                efectivo.setBillete_20(billetes6);
                efectivo.setMoneda_100(monedas1);
                efectivo.setMoneda_50(monedas2);
                efectivo.setMoneda_20(monedas3);
                efectivo.setMoneda_10(monedas4);
                efectivo.setMoneda_5(monedas5);
                efectivo.setMoneda_2(monedas6);
                efectivo.setMoneda_1(monedas7);
                efectivo.setMoneda_05(monedas8);
                efectivo.setMoneda_02(monedas9);
                efectivo.setMoneda_01(monedas10);

                if (efectivo.isFlag() == true){
                    dbd.updateEfectivo(efectivo);

                    Toast.makeText(context, "Actualizado Exitosamente", Toast.LENGTH_SHORT).show();
                } else {

                    dbd.setEfectivo(efectivo);

                    Toast.makeText(context, "Agregado Exitosamente", Toast.LENGTH_SHORT).show();

                    flag = true;

                }

            }
        }
    }

    public int zeroNumber(String data){
        final int a = !data.equals("")?Integer.parseInt(data) : 0;
        return a;
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

    public class FormatNumber implements InputFilter{
        Pattern mPattern;

        public FormatNumber(int digits){
            mPattern = Pattern.compile("[0-9]{0," + (digits-1) + "}?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches()){
                return "";
            }

            return null;
        }
    }

}
