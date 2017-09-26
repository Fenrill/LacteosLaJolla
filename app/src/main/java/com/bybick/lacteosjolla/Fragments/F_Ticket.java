package com.bybick.lacteosjolla.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bybick.lacteosjolla.Printers.ConnectionBT;
import com.bybick.lacteosjolla.Printers.P_Carga;
import com.bybick.lacteosjolla.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by bicktor on 04/06/2016.
 */
public class F_Ticket extends Fragment {
    Context context;
    ActionBar tb;
    String ticket;
    String title;
    OutputStream ons;
    ConnectionBT print;

    FragmentManager fm;

    TextView txtTicket;
    FloatingActionButton btnCerrar;
    FloatingActionButton btnRePrint;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPrint(ConnectionBT print) {
        this.print = print;
    }

    public void setTb(ActionBar tb) {
        this.tb = tb;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public void setOns(OutputStream ons) {
        this.ons = ons;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_ticket, container, false);
        getViews(v);

        return v;
    }

    public void getViews(View v) {
        tb.setTitle("Ticket");
        Display display = getActivity().getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        RelativeLayout papel = (RelativeLayout) v.findViewById(R.id.paper);
        int tamaño = size.x / 3;
        tamaño = tamaño * 2;
        papel.getLayoutParams().width = tamaño;

        txtTicket = (TextView) v.findViewById(R.id.txtTicket);
        txtTicket.setText(ticket);

        btnCerrar = (FloatingActionButton) v.findViewById(R.id.btnClose);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print.close();
                fm.popBackStack();
            }
        });

        btnRePrint = (FloatingActionButton) v.findViewById(R.id.btnRePrint);
        btnRePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OutputStreamWriter ticket_file = new OutputStreamWriter(context.openFileOutput("ticket.txt", Context.MODE_PRIVATE));

                    //Add
                    ticket_file.write(ticket);
                    F_Ticket ticket = new F_Ticket();
//                ticket.setContext(context);
//                ticket.setTb(tb);
//                ticket.setTicket(print.imprimir());
//                ticket.setTitle("Venta");
//                ticket.setPrint(print.getBT());

                    //Escribir Ticket
                    File file = new File("/data/data/com.bybick.lacteosjolla/files/ticket.txt");
                    byte[] bites = new byte[(int)file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(bites);

                    //Escribir en BT
                    if(ons == null)
                        ons = print.connect();
                    if(ons != null)
                        ons.write(bites);

                    //Delete temporal file
                    file.delete();

                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Create Failed ", e.toString());
                } catch (IOException e) {
                    Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Write Failed ",e.toString());
                } catch (Exception es){
                    Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Otra Excepcion ", es.toString());
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tb.setTitle(title);
    }
}
