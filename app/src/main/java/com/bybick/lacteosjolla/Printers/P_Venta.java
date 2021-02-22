package com.bybick.lacteosjolla.Printers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import com.bybick.lacteosjolla.Adapters.PrinterCommands;
import com.bybick.lacteosjolla.Adapters.Utils;
import com.bybick.lacteosjolla.DataBases.DBConfig;
import com.bybick.lacteosjolla.DataBases.DBData;
import com.bybick.lacteosjolla.Fragments.F_Venta;
import com.bybick.lacteosjolla.Main;
import com.bybick.lacteosjolla.ObjectIN.Cliente;
import com.bybick.lacteosjolla.ObjectOUT.Det_Venta;
import com.bybick.lacteosjolla.ObjectOUT.Recomendacion;
import com.bybick.lacteosjolla.ObjectOUT.Venta;
import com.bybick.lacteosjolla.R;
//import com.google.android.things.pio.UartDevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 * Created by bicktor on 20/06/2016.
 */
public class P_Venta {
    Context context;
    DBData dbd;
    DBConfig dbc;

    Venta venta;
    Cliente cliente;

    ConnectionBT bt;
    OutputStream ons;

    int marca;

    //    private UartDevice mDevice;
    private final byte ESC_CHAR = 0x1B;
    private final byte[] PRINTER_SET_LINE_SPACE_24 = new byte[]{ESC_CHAR, 0x33, 24};
    private final byte BYTE_LF = 0xA;
    private final byte[] PRINTER_PRINT_AND_FEED = {0x1B, 0x64};


    public P_Venta(Context context, Venta venta, Cliente cliente) {
        this.context = context;
        this.venta = venta;
        this.cliente = cliente;
    }

    public void Marca(int marca){
        this.marca = marca;
    }

    public String imprimir() {
        dbc = new DBConfig(context);
        dbd = new DBData(context);
        //main = new Main();
        String txt = "";
        String title = "== VENTA DE " + venta.getEmpresa() + " ==";
        title = center(title);
        int total = 0;
        ArrayList<Recomendacion> list = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final boolean x = false;
        try{
//            do {
            OutputStreamWriter ticket = new OutputStreamWriter(context.openFileOutput("ticket.txt", Context.MODE_PRIVATE));
            //Escribir en el ticket * CABECERA GENERAL *
//            ticket.write(center(title) + "\r\n\r\n");
            ticket.write(center("== CARACOL =="));
            ticket.write("Usuario: " + dbc.getlogin().getId_usuario() + "\r\n");
            ticket.write("No Cliente: " + cliente.getId_cliente() + "\r\n");
            ticket.write("Cliente: " + cliente.getNombre() + "\r\n");
            ticket.write("Fecha: " + Main.getFecha() + "\r\n");
            ticket.write("Hora: " + Main.getHora() + "\r\n\r\n");


            ticket.write("Serie y Folio: " + venta.getSerie() + "  -  " + venta.getFolio());
            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");

            ticket.write("--------------------------------\r\n");
            ticket.write(center("Venta a " + venta.getForma_venta()));
            ticket.write(center("Metodo de Pago: " + venta.getMetodo_pago()));


            // * CABECERA DE LOS PRODUCTOS *
            ticket.write("--------------------------------\r\n");
//            ticket.write(center("Descripcion") + "\r\n");
//            ticket.write("Cant.                Precio    \r\n");
//            ticket.write("Pzas.                Precio    \r\n");
//            ticket.write("Unidad               Importe\r\n");
            ticket.write("--------------------------------\r\n");
            ArrayList<Det_Venta> det_venta = venta.getDet_venta();

            list = dbd.randomRec();


            for (int i = 0; i < det_venta.size(); i++) {
//                ticket.write("--------------------------------\r\n");
                ticket.write(center(det_venta.get(i).getDescripcion()) + "\r\n");
//                ticket.write(center("Descripcion") + "\r\n");
//                ticket.write("Cant.                Precio    \r\n");
                ticket.write("Cant: " + det_venta.get(i).getCantidad() + "   x   $" + FormatNumber(det_venta.get(i).getPrecio()) + "\r\n");
//                ticket.write(" " + det_venta.get(i).getCantidad() + "             $" + det_venta.get(i).getPrecio() + "\r\n");
//                ticket.write("             $" + det_venta.get(i).getPrecio() + "\r\n");
//                ticket.write("Pzas.                          \r\n");
                ticket.write(" " + det_venta.get(i).getUnidad() + "      Importe: $" + FormatNumber(det_venta.get(i).getTotal()) + "\r\n");
                if (det_venta.get(i).getUnidad().equals("Kg")) {
                    ticket.write("Piezas: " + det_venta.get(i).getPiezaB() + "\r\n");
                }
//                ticket.write(" " + det_venta.get(i).getPiezaB() + "\r\n");
//                ticket.write(" " + det_venta.get(i).getUnidad() + "             $" + det_venta.get(i).getTotal() + "\r\n");
//                ticket.write("             $" + det_venta.get(i).getTotal() + "\r\n");
                ticket.write("--------------------------------\r\n");
                
                if (det_venta.get(i).getUnidad().equals("Kg")){
                    total += det_venta.get(i).getPiezaB();
                }else {
                    total += det_venta.get(i).getCantidad();
                }
            }
            String line;
            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
            ticket.write("Total de Productos: " + total + "\r\n");
            ticket.write("--------------------------------\r\n");

            line = "SUBTOTAL:  $ " + FormatNumber(venta.getSubtotal());
            line = align(line);
            ticket.write(line);
                ticket.write("                                \r\n");
//            ticket.write("                                \r\n");
//
            line = "IMPUESTOS: $ " + FormatNumber(venta.getImpuestos());
            line = align(line);
            ticket.write(line);
                          ticket.write("                                \r\n");
//            ticket.write("                                \r\n");

            line = "TOTAL:     $ " + FormatNumber(venta.getTotal());
            line = align(line);
            ticket.write(line);
            ticket.write("                                \r\n");
//                ticket.write("                                \r\n");
//                ticket.write("                                \r\n");
//                ticket.write("                                \r\n");

//            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
//            ticket.write("                                \r\n");
            ticket.write("Se le recomienda tambien:       \r\n");
            ticket.write("                                \r\n");
            for (int i = 0; i < list.size(); i++){
                ticket.write(" "  + list.get(i).getNombre() + "\r\n");
            }
            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
            if (marca == 1){
                ticket.write("Puede Descargar la Factura de:  \n");
                ticket.write("www.caracolmexico.com/facturas  \n");
            }
            ticket.write("                                \r\n");
            ticket.write(center("!Gracias por su Compra!"));
            ticket.write(center("Tel. Atencion al Cliente"));
            ticket.write(center("800-849-8820"));
            ticket.write(center("www.caracolmexico.com"));
            ticket.write("                                \r\n");
            ticket.write("--------------------------------\r\n");
            ticket.write("                                \r\n");

            ticket.close();

            //Escribir Ticket
            File file = new File("/data/data/com.bybick.lacteosjolla/files/ticket.txt");
            final byte[] bites = new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bites);
            txt = new String(bites);

            //Crear Conexion
            bt = new ConnectionBT(context);
            ons = bt.connect();

            if(ons != null)
//                ons.write(printPhoto(R.drawable.logo_naranja));
                ons.write(bites);



            AlertDialog.Builder buil = new AlertDialog.Builder(context);
            buil.setIcon(R.mipmap.ic_printer).setTitle("Reimprimir").setMessage("Imprimir el Ticket de Nuevo?").setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        ons.write(bites);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setCancelable(false).create().show();


            //Borrar
            file.delete();

        } catch (FileNotFoundException es) {
            Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Create Failed ", es.toString());
        } catch (IOException es) {
            Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Write Failed ",es.toString());
        } catch (Exception es){
            Toast.makeText(context, "Error : " + es.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Otra Excepcion ", es.toString());
        }

        return txt;
    }

    //Cerrar BT
    public void Close() {
        bt.close();
    }

    //Retornar BT
    public ConnectionBT getBT() {
        return bt;
    }

    //tirnar OnsWri
    public OutputStream getONS() {
        return ons;
    }



    public String align(String line){
        int spaces=11;
        for(int i=0;i<spaces;i++){
            line=" "+line;
        }
        return line;
    }

    public String center(String line){
        if(line.length()<32){
            int x=(32-line.length())/2;
            for(int i=0;i<x;i++){
                line=" "+line;
                line=line+" ";
            }
        }
        return line;

    }

    //Formatear Numero de double a String
    public String FormatNumber(double number) {
        String result = "";

        DecimalFormatSymbols sim = new DecimalFormatSymbols();
        sim.setDecimalSeparator('.');
        sim.setGroupingSeparator(',');
        DecimalFormat fd = new DecimalFormat("###,##0.00", sim);

        result = fd.format(number);

        return result;
    }

    public Bitmap printPhoto(int img) {

        Resources res = context.getResources();
        int id = img;
        Bitmap bmp = BitmapFactory.decodeResource(res, id);
//        byte[] byteArray;
//        int size = bmp.getRowBytes() * bmp.getHeight();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
//        bmp.copyPixelsToBuffer(byteBuffer);
//        byteArray = byteBuffer.array();
        return bmp;

    }

    void printImage(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        final byte[] controlByte = {(byte) (0x00ff & width), (byte) ((0x00ff & width) >> 8)};
        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        final int BAND_HEIGHT = 24;

        // Bands of pixels are sent that are 8 pixels high.  Iterate through bitmap
        // 24 rows of pixels at a time, capturing bytes representing vertical slices 1 pixel wide.
        // Each bit indicates if the pixel at that position in the slice should be dark or not.

        for (int row = 0; row < height; row += BAND_HEIGHT){
            ByteArrayOutputStream imageData = getOutputStream();

            writeToPrinterBuffer(imageData, PRINTER_SET_LINE_SPACE_24);

            // Need to send these two sets of bytes at the beginning of each row.
            for (int col = 0; col < width; col++){

                byte[] bandBytes = {0x0, 0x0, 0x0};

                // Ugh, the nesting of forloops.  For each starting row/col position, evaluate
                // each pixel in a column, or "band", 24 pixels high.  Convert into 3 bytes.
                for (int rowOffset = 0; rowOffset < 8; rowOffset++){
                    // Because the printer only maintains correct height/width ratio
                    // at the highest density, where it takes 24 bit-deep slices, process
                    // a 24-bit-deep slice as 3 bytes.
                    int[] pixelSlice = new int[3];
                    int pixel2Row = row + rowOffset + 8;
                    int pixel3Row = row + rowOffset + 16;

                    // If we go past the bottom of the image, just send white pixels so the printer
                    // doesn't do anything.  Everything still needs to be sent in sets of 3 rows.
                    pixelSlice[0] = bitmap.getPixel(col, row + rowOffset);
                    pixelSlice[1] = (pixel2Row >= bitmap.getHeight()) ? Color.WHITE : bitmap.getPixel(col, pixel2Row);
                    pixelSlice[2] = (pixel3Row >= bitmap.getHeight()) ? Color.WHITE : bitmap.getPixel(col, pixel3Row);

                    boolean[] isDark = {pixelSlice[0] == Color.BLACK, pixelSlice[1] == Color.BLACK, pixelSlice[2] == Color.BLACK, };

                    // Towing that fine line between "should I forloop or not".  This will only
                    // ever be 3 elements deep.
                    if (isDark[0]) bandBytes[0] |= 1 << (7 - rowOffset);
                    if (isDark[1]) bandBytes[1] |= 1 << (7 - rowOffset);
                    if (isDark[2]) bandBytes[2] |= 1 << (7 - rowOffset);
                }
                writeToPrinterBuffer(imageData, bandBytes);
            }
            addLineFeed(imageData, 1);
//            print(imageData);

        }
    }

//    private synchronized void writeUartData(byte[] data) throws IOException {
//        // In the case of writing images, let's assume we shouldn't send more than 400 bytes
//        // at a time to avoid buffer overrun - At which point the thermal printer tends to
//        // either lock up or print garbage.
//        final int DEFAULT_CHUNK_SIZE = 400;
//
//        byte[] chunk = new byte[DEFAULT_CHUNK_SIZE];
//        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//        while (byteBuffer.remaining() > DEFAULT_CHUNK_SIZE) {
//            byteBuffer.get(chunk);
//            mDevice.write(chunk, chunk.length);
//            try {
//                this.wait(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        if (byteBuffer.hasRemaining()) {
//            byte[] lastChunk = new byte[byteBuffer.remaining()];
//            byteBuffer.get(lastChunk);
//            mDevice.write(lastChunk, lastChunk.length);
//        }
//    }

//    private void print(ByteArrayOutputStream output) {
//        try {
//            writeUartData(output.toByteArray());
//        } catch (IOException e) {
//            Log.d("ThermalPrinter", "IO Exception while printing.", e);
//        }
//    }

    private void addLineFeed(ByteArrayOutputStream printerBuffer, int numLines) {
        try {
            if (numLines <= 1) {
                printerBuffer.write(BYTE_LF);
            } else {
                printerBuffer.write(PRINTER_PRINT_AND_FEED);
                printerBuffer.write(numLines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//        try {
//            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
//                    img);
//            if(bmp!=null){
//                byte[] command = Utils.decodeBitmap(bmp);
//                ons.write(PrinterCommands.ESC_ALIGN_CENTER);
//                printText(command);
//            }else{
//                Log.e("Print Photo error", "the file isn't exists");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("PrintTools", "the file isn't exists");
//        }
//    }

    private ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }

    private void writeToPrinterBuffer(ByteArrayOutputStream printerBuffer, byte[] command) {
        try {
            printerBuffer.write(command);
        } catch (IOException e) {
            Log.d("ThermalPrinter", "IO Exception while writing printer data to buffer.", e);
        }
    }

}
