package com.anggastudio.sample.Fragment;

import android.graphics.Bitmap;
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
import com.anggastudio.sample.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class PrintNotaDespachoFragment extends DialogFragment {

   Button cerrar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_print_nota_despacho, container, false);

        TextView producto     = view.findViewById(R.id.textproducto);
        TextView lado         = view.findViewById(R.id.textlado);
        TextView importe      = view.findViewById(R.id.textimporte);

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
        view.findViewById(R.id.btnimpirmirnotadespacho).setOnClickListener(v -> notadespacho(turno,cajero,kilometraje,placa,dni,clientes,tarjeta,umed));
        return view;
    }
    int turno           = 1;
    String cajero       = "RUBEN ESCOBAR";
    long kilometraje    = Long.parseLong("00000000000");
    String placa        = "BKC-926";
    long dni            = Long.parseLong("20653232");
    long ruc            = Long.parseLong("20600064062");
    String clientes     = "CONNEXA DISTRIBUICIONES SAC";
    long tarjeta        = Long.parseLong("7020130000000309");
    String direccion    = "AV FERROCARRIL N 590 HUANCAYO";
    String umed         = "GLL";
    private  void notadespacho(int turno ,String cajero,long kilometraje,String placa,long dni,String clientes,long tarjeta,String umed) {
        Bundle bundle         = this.getArguments();
        //LOGO
        Bitmap logo = Printama.getBitmapFromVector(getContext(), R.drawable.logoprincipal);

        //FECHAHORA
        Calendar cal          = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat sdf  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
        String productos = producto;
        double totalprecio = Double.parseDouble(finalPrecio);

        //IMPORTE
        String importe    = bundle.getString("importe");
        double totalimporte   = Double.parseDouble(importe);

        //OPERACION DE VALOR DE CANTIDAD POR GALONES
        double resultados      = totalimporte/totalprecio ;
        double decimal = Math.round(resultados*1000.0)/1000.0;
        String cantidadgalones = String.valueOf(decimal);
        String galon           = cantidadgalones.replace(",",".");

        String exoneradas = " 0.00";

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
            printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
            printama.printTextlnBold("015-0017680",Printama.CENTER);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextln("Fecha - Hora : "+ FechaHora + "   Turno: "+turno,Printama.LEFT);
            printama.printTextln("Cajero : "+cajero, Printama.LEFT);
            printama.printTextln("Lado   : "+lado + "         Kilometraje: "+kilometraje, Printama.LEFT);
            printama.printTextln("Placa  : "+placa, Printama.LEFT);
            printama.printTextln("RUC / DNI : "+dni, Printama.LEFT);
            printama.printTextln("Cliente   : "+clientes, Printama.LEFT);
            printama.printTextln("#Tarjeta  : "+tarjeta + "  Chofer : ",Printama.LEFT);
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
            printama.setSmallText();
            printama.addNewLine(1);
            printama.printTextlnBold("TOTAL VENTA: S/ "+ importe, Printama.RIGHT);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextln("NOMBRE:", Printama.LEFT);
            printama.printTextln("DNI:", Printama.LEFT);
            printama.printTextln("FIRMA:", Printama.LEFT);
            printama.feedPaper();
            printama.close();
            dismiss();
        }, this::showToast);
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }
}