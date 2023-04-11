package com.anggastudio.sample.Fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class PrintBoletaFragment extends DialogFragment {

    Button cerrar;
    TextView producto,lado,importe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_print_boleta, container, false);

        producto     = view.findViewById(R.id.textproducto);
        lado         = view.findViewById(R.id.textlado);
        importe      = view.findViewById(R.id.textimporte);

        //Detalle de la Operación
        Bundle bundle         = this.getArguments();
        String datoproducto   = bundle.getString("producto");
        String datolado       = bundle.getString("lado");
        String datoimporte    = bundle.getString("importe");

        producto.setText(datoproducto);
        lado.setText(datolado);
        importe.setText(datoimporte);

        cerrar   = view.findViewById(R.id.btncerrar);

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btnimpirmirboleta).setOnClickListener(v -> boletas(turno,cajero,umed));
        return view;
    }

    String turno    = String.valueOf(GlobalInfo.getterminalTurno10);
    String cajero   = GlobalInfo.getuserName10;
    String umed     = "GLL";

    private  void boletas(String turno ,String cajero, String umed) {

        Bundle bundle         = this.getArguments();
        //LOGO DE LA EMPRESA
        Bitmap logo = Printama.getBitmapFromVector(getContext(), R.drawable.logoprincipal);

        //FECHA-HORA
        Calendar cal          = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
        String FechaHora      = sdf.format(cal.getTime());

        //LADO
        String lado       = bundle.getString("lado");

        //PRODUCTO
        String manguera        = bundle.getString("producto");
        String precio         = null;
        String producto       = null;

        switch (manguera) {
            case "DB5":
                precio = "18.89";
                producto = "DIESEL B5 S50";
                break;
            case "G90":
                precio = "16.69";
                producto = "GASOHOL 90";
                break;
            case "G95":
                precio = "18.99";
                producto = "GASOHOL 95";
                break;
            case "G97":
                precio = "19.99";
                producto = "GASOHOL 97";
                break;
            case "GLP":
                precio = "7.44";
                producto = "GLP";
                break;
            default:
                Log.d("Error", "NULL");
        }
        String finalPrecio = precio;
        String productos   = producto;
        double totalprecio = Double.parseDouble(finalPrecio);

        //IMPORTE
        String importe    = bundle.getString("importe");
        double totalimporte   = Double.parseDouble(importe);

        //OP. GRAVADAS
        double subtotal     = (totalimporte/1.18);
        double roundOff     = Math.round(subtotal*100.0)/100.0;
        String valorventa   = String.valueOf(roundOff);

        //IGV
        double impuesto     = (totalimporte-roundOff);
        double impuestoOff  = Math.round(impuesto*100.0)/100.0;
        String igv          = String.valueOf(impuestoOff);

        //Convertir decimal y Operacion de Cantidad por GALONES
        double resultados       = totalimporte/totalprecio ;
        double decimal          = Math.round(resultados*1000.0)/1000.0;
        String cantidadgalones  = String.valueOf(decimal);
        String galon            = cantidadgalones.replace(",",".");

        //Convertir numero a letras
        Numero_Letras numToWord = new Numero_Letras();
        String letraimporte     = numToWord.Convertir(importe,true);

        String exoneradas = " 0.00";

        //Generar Codigo QR
        String rucempresa       ="20602130259";
        String tipodocumento    ="03";
        String boleta           ="B006-0142546";
        String fecha            = FechaHora.substring(6,10) + "-" + FechaHora.substring(3,5) + "-" + FechaHora.substring(0,2);
        String tipodni          ="01";
        String dni              ="11111111";

        /* DNI: 01
           RUC: 06
        */
      /*  01 factura
          03 boleta
          99 nota de desoacho
          98 serafin
      */
        //GENERAR CODIGO QR BOLETA
        StringBuilder QRBoleta = new StringBuilder();
        QRBoleta.append(rucempresa + "|".toString());
        QRBoleta.append(tipodocumento+ "|".toString());
        QRBoleta.append(boleta+ "|".toString());
        QRBoleta.append(igv+ "|".toString());
        QRBoleta.append(importe+ "|".toString());
        QRBoleta.append(fecha+ "|".toString());
        QRBoleta.append(tipodni+ "|".toString());
        QRBoleta.append(dni+ "|".toString());

        String Qrboleta = QRBoleta.toString();

        Printama.with(getContext()).connect(printama -> {
            printama.setNormalText();
            printama.printTextln("                 ", Printama.CENTER);
            printama.printImage(logo, 200);
            printama.setSmallText();
            printama.printTextlnBold("GRIFO ROBLES S.A.C", Printama.CENTER);
            printama.printTextlnBold("PRINCIPAL: AV.SAN BORJA SUR NRO.810", Printama.CENTER);
            printama.printTextlnBold("LIMA-LIMA-SAN BORJA", Printama.CENTER);
            printama.printTextlnBold("SUCURSAL: CAR. CENTRAL MARGEN NRO.S/N", Printama.CENTER);
            printama.printTextlnBold("JUNIN - HUANCAYO - PILCOMAYO", Printama.CENTER);
            printama.printTextlnBold("RUC: " + rucempresa, Printama.CENTER);
            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
            printama.printTextlnBold(boleta,Printama.CENTER);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextln("Fecha - Hora : "+ FechaHora + "   Turno:"+turno,Printama.LEFT);
            printama.printTextln("Cajero : "+cajero, Printama.LEFT);
            printama.printTextln("Lado   : "+lado, Printama.LEFT);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextlnBold("PRODUCTO      "+"U/MED   "+"PRECIO   "+"CANTIDAD  "+"IMPORTE",Printama.RIGHT);
            printama.setSmallText();
            printama.printTextln(productos,Printama.LEFT);
            printama.printTextln(umed+"    " + finalPrecio+"     " + galon +"    "+ importe,Printama.RIGHT);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextln("OP. GRAVADAS: S/ "+ valorventa, Printama.RIGHT);
            printama.printTextln("OP. EXONERADAS: S/   "+ exoneradas , Printama.RIGHT);
            printama.printTextln("I.G.V. 18%: S/  "+ igv, Printama.RIGHT);
            printama.printTextlnBold("TOTAL VENTA: S/ "+ importe, Printama.RIGHT);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
            printama.printTextlnBold("CONTADO: S/ " + importe, Printama.RIGHT);
            printama.setSmallText();
            printama.printTextln("SON: " + letraimporte, Printama.LEFT);
            printama.setSmallText();
            printama.printTextln("                 ", Printama.CENTER);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix;
            try {
                bitMatrix = writer.encode(Qrboleta, BarcodeFormat.QR_CODE, 200, 200);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = Color.WHITE;
                        if (bitMatrix.get(x, y)) color = Color.BLACK;
                        bitmap.setPixel(x, y, color);
                    }
                }
                if (bitmap != null) {
                    printama.printImage(bitmap);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
            printama.setSmallText();
            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");
            printama.addNewLine();
            printama.feedPaper();
            printama.close();
            dismiss();
        }, this::showToast);
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }
}