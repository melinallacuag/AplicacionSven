package com.anggastudio.sample.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ListaComprobanteAdapter;
import com.anggastudio.sample.Menu;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaComprobantesFragment extends Fragment  {

    private APIService mAPIService;

    RecyclerView recyclerLComprobante ;
    ListaComprobanteAdapter listaComprobanteAdapter;
    List<ListaComprobante> listaComprobanteList;

    TextInputEditText usuario, contraseña;
    TextInputLayout alertuser,alertpassword;
    String usuarioUser,contraseñaUser;

    List<Users> usersList;

    Dialog modalReimpresion,modalAnulacion;
    Button btnCancelarRImpresion,btnRImpresion,btnAnular,btnCancelarAnular,btnAceptarIngreso;
    TextView campo_correlativo;

    SearchView BuscarRazonSocial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_comprobantes, container, false);

        mAPIService  = GlobalInfo.getAPIService();

        BuscarRazonSocial   = view.findViewById(R.id.BuscarRazonSocial);

        /** Listado de Comprobantes  */
        recyclerLComprobante = view.findViewById(R.id.recyclerListaComprobante);
        recyclerLComprobante.setLayoutManager(new LinearLayoutManager(getContext()));

        /** Mostrar Modal de Cambio de Turno */
        modalReimpresion = new Dialog(getContext());
        modalReimpresion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalReimpresion.setContentView(R.layout.modal_reimprimir);
        modalReimpresion.setCancelable(false);

        findConsultarVenta(GlobalInfo.getterminalID10);

        /** Buscador por Razon Social */
        BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (listaComprobanteList.isEmpty()) {

                    Toast.makeText(getContext(), "No se encontró el dato", Toast.LENGTH_SHORT).show();

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaComprobanteAdapter.filtrado(newText);
                return false;
            }
        });

        return view;
    }
    /** API SERVICE - Card Consultar Venta */
    private void findConsultarVenta(String id){

        Call<List<ListaComprobante>> call = mAPIService.findConsultarVenta(id);

        call.enqueue(new Callback<List<ListaComprobante>>() {
            @Override
            public void onResponse(Call<List<ListaComprobante>> call, Response<List<ListaComprobante>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaComprobanteList = response.body();

                    ListaComprobante();


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ListaComprobante>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Consulta Venta - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void ListaComprobante(){

        listaComprobanteAdapter = new ListaComprobanteAdapter(listaComprobanteList, getContext(), new ListaComprobanteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListaComprobante item) {

                moveToDescription(item);

                modalReimpresion.show();

                btnCancelarRImpresion    = modalReimpresion.findViewById(R.id.btnCancelarRImpresion);
                btnRImpresion       = modalReimpresion.findViewById(R.id.btnRImpresion);
                btnAnular           = modalReimpresion.findViewById(R.id.btnAnular);
                campo_correlativo   = modalReimpresion.findViewById(R.id.campo_correlativo);

                campo_correlativo.setText("NroDocumento: " + GlobalInfo.getconsultaventaSerieDocumento10 + "-" + GlobalInfo.getconsultaventaNroDocumento10);

                btnCancelarRImpresion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalReimpresion.dismiss();
                    }
                });

                btnRImpresion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Reimpresion(GlobalInfo.getconsultaventaTipoDocumentoID10,GlobalInfo.getconsultaventaSerieDocumento10 ,GlobalInfo.getconsultaventaNroDocumento10);

                    }
                });

                /** Mostrar Modal de Anulación */
                modalAnulacion = new Dialog(getContext());
                modalAnulacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalAnulacion.setContentView(R.layout.modal_anulacion);
                modalAnulacion.setCancelable(false);

                btnAnular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (GlobalInfo.getconsultaventaAnulado10.equals("NO")) {

                            modalAnulacion.show();

                            btnCancelarAnular    = modalAnulacion.findViewById(R.id.btnCancelarAnular);
                            btnAceptarIngreso    = modalAnulacion.findViewById(R.id.btnAceptarIngreso);

                            usuario        = modalAnulacion.findViewById(R.id.inputUserAnulado);
                            contraseña     = modalAnulacion.findViewById(R.id.inputContraseñaAnulado);
                            alertuser      = modalAnulacion.findViewById(R.id.alertUserAnulado);
                            alertpassword  = modalAnulacion.findViewById(R.id.alertContraseñaAnulado);

                            btnCancelarAnular.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    modalAnulacion.dismiss();
                                    alertuser.setErrorEnabled(false);
                                    alertpassword.setErrorEnabled(false);
                                }
                            });

                            btnAceptarIngreso.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    usuarioUser    = usuario.getText().toString();
                                    contraseñaUser = contraseña.getText().toString();

                                    if(usuarioUser.isEmpty()){
                                        alertuser.setError("El campo usuario es obligatorio");
                                        return;
                                    }else if(contraseñaUser.isEmpty()){
                                        alertpassword.setError("El campo contraseña es obligatorio");
                                        return;
                                    }

                                    alertuser.setErrorEnabled(false);
                                    alertpassword.setErrorEnabled(false);

                                    GlobalInfo.getuserID10 = "";
                                    GlobalInfo.getuserName10 = "";
                                    GlobalInfo.getuserPass10 = "";

                                    findUsers(usuario.getText().toString());

                                    modalReimpresion.dismiss();

                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Documento se encuntra anulado", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        recyclerLComprobante.setAdapter(listaComprobanteAdapter);
    }

    public void moveToDescription(ListaComprobante item) {

        GlobalInfo.getconsultaventaTipoDocumentoID10  = item.getTipoDocumento();
        GlobalInfo.getconsultaventaSerieDocumento10   = item.getSerieDocumento();
        GlobalInfo.getconsultaventaNroDocumento10     = item.getNroDocumento();
        GlobalInfo.getconsultaventaAnulado10          = item.getAnulado();

    }

    private void Anular(String tipodoc, String seriedoc, String nrodoc, String anuladoid) {

        Call<Anular> call = mAPIService.postAnular(tipodoc,seriedoc,nrodoc,anuladoid);

        call.enqueue(new Callback<Anular>() {
            @Override
            public void onResponse(Call<Anular> call, Response<Anular> response) {

                if(!response.isSuccessful()){

                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;

                }

            }

            @Override
            public void onFailure(Call<Anular> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Anular", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Reimpresion(String tipodoc, String seriedoc, String nrodoc) {

        Call<List<Reimpresion>> call = mAPIService.findReimpresion(tipodoc, seriedoc, nrodoc);

        call.enqueue(new Callback<List<Reimpresion>>() {
            @Override
            public void onResponse(Call<List<Reimpresion>> call, Response<List<Reimpresion>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Reimpresion> reimpresionList = response.body();

                    for (Reimpresion reimpresion : reimpresionList) {

                        String fechaDocumento1   = String.valueOf(reimpresion.getFechaDocumento());
                        String tipoDocumento1    = String.valueOf(reimpresion.getTipoDocumento());
                        String serieDocumento1   = String.valueOf(reimpresion.getSerieDocumento());
                        String nroDocumento1     = String.valueOf(reimpresion.getNroDocumento());
                        Integer turno1           = Integer.valueOf(reimpresion.getTurno());
                        String clienteID1        = String.valueOf(reimpresion.getClienteID());
                        String clienteRZ1        = String.valueOf(reimpresion.getClienteRZ());
                        String clienteDR1        = String.valueOf(reimpresion.getClienteDR());
                        String nroPlaca1         = String.valueOf(reimpresion.getNroPlaca());
                        String odometro1         = String.valueOf(reimpresion.getOdometro());
                        String userID1           = String.valueOf(reimpresion.getUserID());
                        String anulado1          = String.valueOf(reimpresion.getAnulado());
                        String articuloID1       = String.valueOf(reimpresion.getArticuloID());
                        String articuloDS1       = String.valueOf(reimpresion.getArticuloDS());
                        String uniMed1           = String.valueOf(reimpresion.getUniMed());
                        Double precio111         = Double.valueOf(reimpresion.getPrecio1());
                        Double cantidad1         = Double.valueOf(reimpresion.getCantidad());
                        Double mtoDescuento1     = Double.valueOf(reimpresion.getMtoDescuento());
                        Double mtoSubTotal1      = Double.valueOf(reimpresion.getMtoSubTotal());
                        Double mtoImpuesto1      = Double.valueOf(reimpresion.getMtoImpuesto());
                        Double mtoTotal1         = Double.valueOf(reimpresion.getMtoTotal());
                        Integer pagoID1          = Integer.valueOf(reimpresion.getPagoID());
                        Integer tarjetaID1       = Integer.valueOf(reimpresion.getTarjetaID());
                        String tarjetaDS1        = String.valueOf(reimpresion.getTarjetaDS());
                        Double mtoPagoPEN1       = Double.valueOf(reimpresion.getMtoPagoPEN());
                        Double montoCanjeado1    = Double.valueOf(reimpresion.getMontoCanjeado());
                        String observacion1      = String.valueOf(reimpresion.getObservacion());
                        String fechaQR1          = String.valueOf(reimpresion.getFechaQR());
                        String nroLado1          = String.valueOf(reimpresion.getNroLado());

                        String Cajero1           = GlobalInfo.getuserName10;
                        String NroComprobante    = serieDocumento1 + "-" + nroDocumento1;

                        /**
                         * Iniciar impresión del comprobante
                         */
                        Bitmap logoRobles = Printama.getBitmapFromVector(getContext(), R.drawable.logoprincipal);

                        String TipoDNI = "1";
                        String CVarios = "11111111";

                        String NameCompany = GlobalInfo.getNameCompany10;
                        String RUCCompany = GlobalInfo.getRucCompany10;
                        String AddressCompany = GlobalInfo.getAddressCompany10;
                        String Address1 = AddressCompany.substring(0, 26);
                        String Address2 = AddressCompany.substring(27, 50);
                        String BranchCompany = GlobalInfo.getBranchCompany10;
                        String Branch1 = BranchCompany.substring(0, 32);
                        String Branch2 = BranchCompany.substring(35, 51);

                        switch (tipoDocumento1) {
                            case "01":
                                TipoDNI = "6";
                                break;
                            case "98":
                                TipoDNI = "0";
                                break;
                        }

                        String PrecioFF = String.format("%.2f", precio111);

                        String CantidadFF = String.format("%.3f", cantidad1);

                        String MtoSubTotalFF = String.format("%.2f", mtoSubTotal1);

                        String MtoImpuestoFF = String.format("%.2f", mtoImpuesto1);

                        String MtoTotalFF = String.format("%.2f", mtoTotal1);

                        String MtoCanjeado = String.format("%.2f", montoCanjeado1);

                        String MtoDescuento = String.format("%.2f", mtoDescuento1);

                        /** Convertir número a letras */
                        Numero_Letras NumLetra = new Numero_Letras();
                        String LetraSoles = NumLetra.Convertir(String.valueOf(mtoTotal1), true);

                        /** Generar codigo QR */
                        StringBuilder qrSVEN = new StringBuilder();
                        qrSVEN.append(RUCCompany + "|".toString());
                        qrSVEN.append(tipoDocumento1 + "|".toString());
                        qrSVEN.append(serieDocumento1 + "|".toString());
                        qrSVEN.append(MtoImpuestoFF + "|".toString());
                        qrSVEN.append(MtoTotalFF + "|".toString());
                        qrSVEN.append(fechaQR1 + "|".toString());
                        qrSVEN.append(TipoDNI + "|".toString());
                        qrSVEN.append(clienteID1 + "|".toString());

                        String qrSven = qrSVEN.toString();

                        Printama.with(getContext()).connect(printama -> {

                            printama.printTextln("                 ", Printama.CENTER);
                            printama.printImage(logoRobles, 200);
                            printama.setSmallText();
                            printama.printTextlnBold(NameCompany, Printama.CENTER);
                            printama.printTextlnBold("PRINCIPAL: " + Address1, Printama.CENTER);
                            printama.printTextlnBold(Address2, Printama.CENTER);
                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                            printama.printTextlnBold(Branch2, Printama.CENTER);
                            printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);

                            switch (tipoDocumento1) {
                                case "01":
                                    printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                                    break;
                                case "03":
                                    printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                                    break;
                                case "98":
                                    printama.printTextlnBold("TICKET SERAFIN", Printama.CENTER);
                                    break;
                                case "99":
                                    printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                                    break;
                            }

                            printama.printTextlnBold(NroComprobante, Printama.CENTER);
                            printama.setSmallText();
                            printama.printDoubleDashedLine();
                            printama.addNewLine(1);
                            printama.setSmallText();
                            printama.printTextln("Fecha - Hora : " + fechaDocumento1 + "  Turno: " + turno1, Printama.LEFT);
                            printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);
                            printama.printTextln("Lado         : " + nroLado1, Printama.LEFT);

                            if (!nroPlaca1.isEmpty()) {
                                printama.printTextln("Nro. PLaca   : " + nroPlaca1, Printama.LEFT);
                            }

                            switch (tipoDocumento1) {
                                case "01":
                                    printama.printTextln("RUC          : " + clienteID1, Printama.LEFT);
                                    printama.printTextln("Razon Social : " + clienteRZ1, Printama.LEFT);

                                    if (!clienteDR1.isEmpty()) {
                                        printama.printTextln("Dirección    : " + clienteDR1, Printama.LEFT);
                                    }
                                    break;
                                case "03":

                                    if (CVarios.equals(clienteID1)) {

                                    } else {
                                        printama.printTextln("DNI          : " + clienteID1, Printama.LEFT);
                                        printama.printTextln("Nombres      : " + clienteRZ1, Printama.LEFT);

                                        if (!clienteDR1.isEmpty()) {
                                            printama.printTextln("Dirección    : " + clienteDR1, Printama.LEFT);
                                        }
                                    }
                                    break;
                                case "99":
                                    break;
                            }

                            printama.setSmallText();
                            printama.printDoubleDashedLine();
                            printama.addNewLine(1);
                            printama.setSmallText();
                            printama.printTextlnBold("PRODUCTO      " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                            printama.setSmallText();
                            printama.printTextln(articuloDS1, Printama.LEFT);

                            if (mtoDescuento1 == 0.00) {
                                printama.printTextln(uniMed1 + "    " + PrecioFF + "      " + CantidadFF + "     " + MtoTotalFF, Printama.RIGHT);
                            } else {
                                printama.printTextln(uniMed1 + "    " + PrecioFF + "      " + CantidadFF + "     " + MtoCanjeado, Printama.RIGHT);
                            }

                            printama.setSmallText();
                            printama.printDoubleDashedLine();
                            printama.addNewLine(1);
                            printama.setSmallText();


                            switch (tipoDocumento1) {
                                case "01":

                                    if (mtoDescuento1 > 0) {
                                        printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                    }

                                    printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                    printama.setSmallText();
                                    printama.printDoubleDashedLine();
                                    printama.addNewLine(1);
                                    printama.setSmallText();

                                    switch (pagoID1) {
                                        case 1:
                                            printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                            printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                            break;
                                        case 2:

                                            printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                            switch (tarjetaID1) {
                                                case 1:
                                                    printama.printTextlnBold("VISA: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 2:
                                                    printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 3:
                                                    printama.printTextlnBold("DINERS: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 4:
                                                    printama.printTextlnBold("YAPE: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 5:
                                                    printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 6:
                                                    printama.printTextlnBold("PLIN: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                            }

                                            break;

                                        case 4:
                                            printama.printTextlnBold("CONDICION DE PAGO: 30 DIAS DE", Printama.LEFT);
                                            printama.printTextlnBold("CREDITO: S/ " + MtoTotalFF, Printama.RIGHT);
                                            break;
                                    }

                                    printama.setSmallText();
                                    printama.printTextln("SON: " + LetraSoles, Printama.LEFT);
                                    printama.printTextln("                 ", Printama.CENTER);
                                    QRCodeWriter writer = new QRCodeWriter();
                                    BitMatrix bitMatrix;
                                    try {
                                        bitMatrix = writer.encode(qrSven, BarcodeFormat.QR_CODE, 200, 200);
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
                                    printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

                                    break;
                                case "03":

                                    if (mtoDescuento1 > 0) {
                                        printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                    }

                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                    printama.setSmallText();
                                    printama.printDoubleDashedLine();
                                    printama.addNewLine(1);
                                    printama.setSmallText();

                                    switch (pagoID1) {
                                        case 1:
                                            printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                            printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                            break;
                                        case 2:

                                            printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                            switch (tarjetaID1) {
                                                case 1:
                                                    printama.printTextlnBold("VISA: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 2:
                                                    printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 3:
                                                    printama.printTextlnBold("DINERS: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 4:
                                                    printama.printTextlnBold("YAPE: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 5:
                                                    printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                                case 6:
                                                    printama.printTextlnBold("PLIN: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                    break;
                                            }

                                            break;

                                        case 4:
                                            printama.printTextlnBold("CONDICION DE PAGO: 30 DIAS DE", Printama.LEFT);
                                            printama.printTextlnBold("CREDITO: S/ " + MtoTotalFF, Printama.RIGHT);
                                            break;
                                    }

                                    printama.setSmallText();
                                    printama.printTextln("SON: " + LetraSoles, Printama.LEFT);
                                    printama.printTextln("                 ", Printama.CENTER);
                                    QRCodeWriter writerB = new QRCodeWriter();
                                    BitMatrix bitMatrixB;
                                    try {
                                        bitMatrixB = writerB.encode(qrSven, BarcodeFormat.QR_CODE, 200, 200);
                                        int width = bitMatrixB.getWidth();
                                        int height = bitMatrixB.getHeight();
                                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                        for (int x = 0; x < width; x++) {
                                            for (int y = 0; y < height; y++) {
                                                int color = Color.WHITE;
                                                if (bitMatrixB.get(x, y)) color = Color.BLACK;
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
                                    printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

                                    break;
                                case "98":
                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                    break;
                                case "99":
                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                    break;
                            }

                            printama.feedPaper();
                            printama.close();

                        });

                        modalReimpresion.dismiss();

                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Reimpresion>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Reimpresion - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /** API SERVICE - Usuarios */
    private void findUsers(String id){

        Call<List<Users>> call = mAPIService.findUsers(id);

        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usersList = response.body();

                    for(Users user: usersList) {

                        usuario.setText(user.getUserID());
                        GlobalInfo.getuserID10 = user.getUserID();
                        GlobalInfo.getuserName10 = user.getNames();
                        GlobalInfo.getuserPass10  = user.getPassword();
                        GlobalInfo.getuserCancel10  = user.getCancel();

                    }

                    if (GlobalInfo.getuserCancel10 == false) {

                        Toast.makeText(getActivity(), "El usuario se encuentra bloqueado", Toast.LENGTH_SHORT).show();

                    }else {

                        String getName = usuario.getText().toString();
                        String getPass = checkpassword(contraseña.getText().toString());

                        final FragmentManager fragmentManager = getFragmentManager();

                        if( getName.equals(GlobalInfo.getuserName10)  || getPass.equals(GlobalInfo.getuserPass10)){

                            Anular(GlobalInfo.getconsultaventaTipoDocumentoID10,GlobalInfo.getconsultaventaSerieDocumento10 ,GlobalInfo.getconsultaventaNroDocumento10,GlobalInfo.getuserID10);

                            fragmentManager.popBackStack();

                            modalAnulacion.dismiss();


                        }
                        else {
                            Toast.makeText(getActivity(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    }

                }catch (Exception ex){
                    Toast.makeText( getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText( getContext(), "Error de conexión APICORE Users - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Hash de la Contraseña */
    private String checkpassword(String clave){

        String lResult = "";
        String lasc1 = "";

        int lValor = 0;
        int lTam = 0;
        int lCar = 0;
        int lasc2 = 0;

        lTam = clave.length();

        for(int lcont = 1 ; lcont <= lTam; lcont += 1){

            switch (lcont){
                case 1:
                    lCar = 1;
                    lasc1 = clave.substring(0,1);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 2:
                    lCar = 3;
                    lasc1 = clave.substring(1,2);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 3:
                    lCar = 5;
                    lasc1 = clave.substring(2,3);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 4:
                    lCar = 7;
                    lasc1 = clave.substring(3,4);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 5:
                    lCar = 9;
                    lasc1 = clave.substring(4,5);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 6:
                    lCar = 11;
                    lasc1 = clave.substring(5,6);
                    lasc2 = lasc1.charAt(0);
                    break;
            }

            lValor = lValor + lasc2 * lCar;

        }

        lResult = String.valueOf(lValor);

        return lResult;
    }

}
