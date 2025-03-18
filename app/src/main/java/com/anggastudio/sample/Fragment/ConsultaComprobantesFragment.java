package com.anggastudio.sample.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.ListaComprobanteAdapter;
import com.anggastudio.sample.Adapter.TipoDocumentoAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Models.TipoDocumento;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultaComprobantesFragment extends Fragment {

    TipoDocumento tipoDocumento;
    Spinner SpinnerTDocumento;
    TipoDocumentoAdapter tipoDocumentoAdapter;

    TipoPago tipoPago;
    Spinner SpinnerTPago;
    TipoPagoAdapter tipoPagoAdapter;

    TextInputLayout alertNroLado,alertNroPlaca,alertIdCliente,alertNroTPuntos,alertRazSocial,alertDireccion,
                    alertObservacion,alertPEfectivo,alertOperacion,alertSelectTPago,alertNroSerie,alertNroDocumento;

    TextInputEditText inputNroLado,inputNroPlaca,inputIdCliente,inputNroTPuntos,inputRazSocial,inputDireccion,
                      inputObservacion,inputPEfectivo,inputOperacion,inputNroSerie,inputNroDocumento;

    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioNombreFormaPago;

    Button btnConsultar,btnGrabaImprimir;

    TextView textMensajePEfectivo,textCTotal,textCAnulado;

    private APIService mAPIService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAPIService  = GlobalInfo.getAPIService();

        View view = inflater.inflate(R.layout.fragment_consulta_comprobantes, container, false);

        inputNroSerie     = view.findViewById(R.id.inputNroSerie);
        inputNroDocumento = view.findViewById(R.id.inputNroDocumento);
        inputNroLado      = view.findViewById(R.id.inputNroLado);
        inputNroPlaca     = view.findViewById(R.id.inputNroPlaca);
        inputIdCliente    = view.findViewById(R.id.inputIdCliente);
        inputNroTPuntos   = view.findViewById(R.id.inputNroTPuntos);
        inputRazSocial    = view.findViewById(R.id.inputRazSocial);
        inputDireccion    = view.findViewById(R.id.inputDireccion);
        inputObservacion  = view.findViewById(R.id.inputObservacion);
        inputOperacion    = view.findViewById(R.id.inputOperacion);
        inputPEfectivo    = view.findViewById(R.id.inputPEfectivo);

        alertNroSerie     = view.findViewById(R.id.alertNroSerie);
        alertNroDocumento = view.findViewById(R.id.alertNroDocumento);
        alertNroLado      = view.findViewById(R.id.alertNroLado);
        alertNroPlaca     = view.findViewById(R.id.alertNroPlaca);
        alertIdCliente    = view.findViewById(R.id.alertIdCliente);
        alertNroTPuntos   = view.findViewById(R.id.alertNroTPuntos);
        alertRazSocial    = view.findViewById(R.id.alertRazSocial);
        alertDireccion    = view.findViewById(R.id.alertDireccion);
        alertObservacion  = view.findViewById(R.id.alertObservacion);
        alertPEfectivo    = view.findViewById(R.id.alertPEfectivo);
        alertOperacion    = view.findViewById(R.id.alertOperacion);
        alertSelectTPago  = view.findViewById(R.id.inputSelectTPago);

        radioFormaPago    = view.findViewById(R.id.radioFormaPago);
        radioEfectivo     = view.findViewById(R.id.radioEfectivo);
        radioTarjeta      = view.findViewById(R.id.radioTarjeta);
        radioCredito      = view.findViewById(R.id.radioCredito);

        SpinnerTDocumento = view.findViewById(R.id.SpinnerTDocumento);
        SpinnerTPago      = view.findViewById(R.id.SpinnerTPago);

        textMensajePEfectivo = view.findViewById(R.id.textMensajePEfectivo);
        textCTotal        = view.findViewById(R.id.textCTotal);
        textCAnulado      = view.findViewById(R.id.textCAnulado);

        btnConsultar      = view.findViewById(R.id.btnConsultar);
        btnGrabaImprimir  = view.findViewById(R.id.btnGrabaImprimir);
      //  btnReimprimir     = view.findViewById(R.id.btnReimprimir);


     //   btnReimprimir.setEnabled(false);
        /**
         *
         */
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalInfo.getConsultaComprobanteNroSerie      = inputNroSerie.getText().toString();
                GlobalInfo.getConsultaComprobanteNroDocumento  = inputNroDocumento.getText().toString();
                GlobalInfo.getConsultaComprobanteTipoDocumento = tipoDocumento.getCardID();

                if (GlobalInfo.getConsultaComprobanteNroSerie.isEmpty()) {
                    alertNroSerie.setError("* El campo Nro. Serie es obligatorio");
                    return;
                }else if(GlobalInfo.getConsultaComprobanteNroDocumento.isEmpty()){
                    alertNroDocumento.setError("* El campo Nro. Documento es obligatorio");
                    return;
                }

                if(GlobalInfo.getConsultaComprobanteTipoDocumento == 3 &&  GlobalInfo.getConsultaComprobanteNroSerie.equals("B002") && GlobalInfo.getConsultaComprobanteNroDocumento.equals("0049139") ){

                    textCTotal.setText("20.00");
                    textCAnulado.setText("NO");
                    inputNroLado.setText("05");
                    inputNroPlaca.setText("000-0000");
                    inputIdCliente.setText("11111111");
                    inputNroTPuntos.setText("");
                    inputRazSocial.setText("CLIENTE VARIOS");
                    inputDireccion.setText("");
                    inputObservacion.setText("");

                    int cardID = 4;
                    switch (cardID) {
                        case 1:
                            int indexVisa = obtenerIndiceTipoPago("VISA");
                            SpinnerTPago.setSelection(indexVisa);
                            break;
                        case 2:
                            int indexMastercard = obtenerIndiceTipoPago("MASTERCARD");
                            SpinnerTPago.setSelection(indexMastercard);
                            break;
                        case 3:
                            int indexDiners = obtenerIndiceTipoPago("DINERS");
                            SpinnerTPago.setSelection(indexDiners);
                            break;
                        case 4:
                            int indexYape = obtenerIndiceTipoPago("YAPE");
                            SpinnerTPago.setSelection(indexYape);
                            break;
                        case 5:
                            int indexAmex = obtenerIndiceTipoPago("AMERICAN EXPRESS");
                            SpinnerTPago.setSelection(indexAmex);
                            break;
                        case 6:
                            int indexPlin = obtenerIndiceTipoPago("PLIN");
                            SpinnerTPago.setSelection(indexPlin);
                            break;
                        default:
                            break;
                    }



                    tipoPagoAdapter.notifyDataSetChanged();

                    String formaPago    = "T";

                    if (formaPago.equals("T")) {
                        radioFormaPago.check(radioTarjeta.getId());
                    } else if (formaPago.equals("E")) {
                        radioFormaPago.check(radioEfectivo.getId());
                    } else if (formaPago.equals("C")){
                        radioFormaPago.check(radioCredito.getId());
                    }

                    inputOperacion.setText("2545");
                    inputPEfectivo.setText("0");

                  //  btnReimprimir.setEnabled(true);

                }else{
                    Toast.makeText(getContext(), "No se encontro comprobante", Toast.LENGTH_SHORT).show();

                    textCTotal.setText(" ");
                    textCAnulado.setText(" ");
                    inputNroLado.getText().clear();
                    inputNroPlaca.getText().clear();
                    inputIdCliente.getText().clear();
                    inputNroTPuntos.getText().clear();
                    inputRazSocial.getText().clear();
                    inputDireccion.getText().clear();
                    inputObservacion.getText().clear();
                    inputPEfectivo.setText("0");
                    inputOperacion.getText().clear();
                    radioFormaPago.check(radioEfectivo.getId());

                   // btnReimprimir.setEnabled(false);
                }
            }
        });

       /** btnReimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reimpresion(GlobalInfo.getTipoPapel10, String.valueOf(String.format("%02d", GlobalInfo.getConsultaComprobanteTipoDocumento)),GlobalInfo.getConsultaComprobanteNroSerie, GlobalInfo.getConsultaComprobanteNroDocumento);
                Toast.makeText(getContext(), String.valueOf(GlobalInfo.getConsultaComprobanteTipoDocumento) + GlobalInfo.getConsultaComprobanteNroSerie + GlobalInfo.getConsultaComprobanteNroDocumento, Toast.LENGTH_SHORT).show();
                btnReimprimir.setEnabled(false);
            }
        });**/

        /**
         * @SELECCIONAR:OpciónFormaPago
         */
        radioFormaPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                radioNombreFormaPago = view.findViewById(checkedId);

                if (checkedId == radioEfectivo.getId()){
                    textMensajePEfectivo.setVisibility(View.VISIBLE);
                    alertSelectTPago.setVisibility(View.GONE);
                    alertOperacion.setVisibility(View.GONE);
                    alertPEfectivo.setVisibility(View.GONE);
                } else if (checkedId == radioTarjeta.getId()){
                    textMensajePEfectivo.setVisibility(View.GONE);
                    alertSelectTPago.setVisibility(View.VISIBLE);
                    alertOperacion.setVisibility(View.VISIBLE);
                    alertPEfectivo.setVisibility(View.VISIBLE);
                } else if (checkedId == radioCredito.getId()){
                    textMensajePEfectivo.setVisibility(View.GONE);
                    alertSelectTPago.setVisibility(View.GONE);
                    alertOperacion.setVisibility(View.GONE);
                    alertPEfectivo.setVisibility(View.VISIBLE);
                }
            }
        });

        radioFormaPago.check(radioEfectivo.getId());

        /**
         * @SELECCIONAR:OpciónTipoDocumento
         */
        TipoDocumento();
        SpinnerTDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoDocumento = (TipoDocumento) SpinnerTDocumento.getSelectedItem();
                GlobalInfo.getConsultaComprobanteTipoDocumento = tipoDocumento.getCardID();

                switch (GlobalInfo.getConsultaComprobanteTipoDocumento) {
                    case 3:
                        inputNroSerie.setText("B");
                        break;
                    case 1:
                        inputNroSerie.setText("F");
                        break;
                    default:
                        inputNroSerie.getText().clear();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /**
         * @SELECCIONAR:OpciónTipoPago
         */
        TipoPago();
        SpinnerTPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoPago = (TipoPago) SpinnerTPago.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private int obtenerIndiceTipoPago(String nombreTipoPago) {
        int index = 0;
        for (int i = 0; i < tipoPagoAdapter.getCount(); i++) {
            TipoPago tipoPago = tipoPagoAdapter.getItem(i);
            if (tipoPago != null && tipoPago.getNames().equals(nombreTipoPago)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * @APISERVICE:TipoDocumento
     */
    private void  TipoDocumento(){

        List<TipoDocumento> cardTipoDocumento = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            cardTipoDocumento.add(new TipoDocumento(03,"BOLETA"));
            cardTipoDocumento.add(new TipoDocumento(01,"FACTURA"));
        }

        Resources res = getResources();
        tipoDocumentoAdapter = new TipoDocumentoAdapter(getContext(), R.layout.item_tipo_doc, (ArrayList<TipoDocumento>) cardTipoDocumento, res);
        SpinnerTDocumento.setAdapter(tipoDocumentoAdapter);

    }

    /**
     * @APISERVICE:TipoPago
     */
    private void  TipoPago(){

        Resources res = getResources();
        tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) GlobalInfo.gettipopagoList10, res);
        SpinnerTPago.setAdapter(tipoPagoAdapter);

    }


    /** API SERVICE - Card Consultar Venta */
    private void findConsultarVenta(String id){

        Call<List<ListaComprobante>> call = mAPIService.findConsultarVenta(id);

        call.enqueue(new Callback<List<ListaComprobante>>() {
            @Override
            public void onResponse(Call<List<ListaComprobante>> call, Response<List<ListaComprobante>> response) {

                try {

                    if (!response.isSuccessful()) {

                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }


                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ListaComprobante>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Consulta Venta - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /** API SERVICE - Reimprimir Comprobante */
    private void Reimpresion(String tipopapel,String tipodoc, String seriedoc, String nrodoc) {

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
                        Double mtoTotalEfectivo  = Double.valueOf(reimpresion.getMtoTotalEfectivo());
                        String nroTarjetaNotaD   = String.valueOf(reimpresion.getNroTarjetaNotaD());

                        String Cajero1           = GlobalInfo.getuserName10;
                        String NroComprobante    = serieDocumento1 + "-" + nroDocumento1;

                        /**
                         * Iniciar impresión del comprobante
                         */

                        String rutaImagen = "/storage/emulated/0/appSven/";

                        if (!TextUtils.isEmpty(GlobalInfo.getsettingRutaLogo210)) {
                            rutaImagen += GlobalInfo.getsettingRutaLogo210;
                            File file = new File(rutaImagen);
                            if (!file.exists()) {
                                rutaImagen = "/storage/emulated/0/appSven/sinlogo.jpg";
                            }
                        } else {
                            rutaImagen += "sinlogo.jpg";
                        }

                        Bitmap logoRobles = BitmapFactory.decodeFile(rutaImagen);

                        String TipoDNI = "1";
                        String CVarios = "11111111";

                        String NameCompany = GlobalInfo.getNameCompany10;
                        String RUCCompany = GlobalInfo.getRucCompany10;

                        String AddressCompany = GlobalInfo.getAddressCompany10;
                        String[] partesAddress = AddressCompany.split(" - " , 2);
                        String Address1 = partesAddress[0];
                        String Address2 = partesAddress[1];

                        String BranchCompany = GlobalInfo.getBranchCompany10;
                        String[] partesBranch = BranchCompany.split(" - " , 2);
                        String Branch1 = partesBranch[0];
                        String Branch2 = partesBranch[1];

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

                        String MTotalEfectivo = String.format("%.2f",mtoTotalEfectivo);

                        String MtoTotalPagoFF = String.format("%.2f",mtoTotal1 - mtoTotalEfectivo);

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

                        int logoSize = (tipopapel.equals("80mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("65mm") ? GlobalInfo.getTerminalImageW10 : 400);

                        Printama.with(getContext()).connect(printama -> {

                            switch (tipopapel) {

                                case "58mm":

                                    switch (tipoDocumento1) {

                                        case "01" :
                                        case "03" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage( logoRobles,logoSize);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            printama.printTextlnBold("PRINCIPAL: " + Address1, Printama.CENTER);
                                            printama.printTextlnBold(Address2, Printama.CENTER);
                                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                                            printama.printTextlnBold(Branch2, Printama.CENTER);
                                            printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);

                                            break;

                                        case "98" :
                                        case "99" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage(logoRobles, logoSize);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                                            printama.printTextlnBold(Branch2, Printama.CENTER);

                                            break;

                                    }

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
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextln("Fecha-Hora : " + fechaDocumento1, Printama.LEFT);
                                    printama.printTextln("Turno        : " + turno1, Printama.LEFT);
                                    printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);
                                    if (!nroLado1.isEmpty() && !nroLado1.equals("99")) {
                                        printama.printTextln("Lado         : " + nroLado1, Printama.LEFT);
                                    }
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

                                            if (!observacion1.isEmpty()) {
                                                printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
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

                                                if (!observacion1.isEmpty()) {
                                                    printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
                                                }

                                            }

                                            break;

                                        case "99":

                                            if (!odometro1.isEmpty()) {
                                                printama.printTextln("Kilometraje  : " + odometro1, Printama.LEFT);
                                            }

                                            if (!observacion1.isEmpty()) {
                                                printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
                                            }

                                            printama.printTextln("RUC/DNI      : " + clienteID1, Printama.LEFT);
                                            printama.printTextln("Cliente      : " + clienteRZ1, Printama.LEFT);
                                            printama.printTextln("#Contrato    : " + nroTarjetaNotaD , Printama.LEFT);
                                            break;

                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("PROD. " + "U/MED " + "PRE.  " + "CANT.  " + "IMPORTE", Printama.LEFT);
                                    printama.setSmallText();
                                    printama.printTextln(articuloDS1,Printama.LEFT);
                                    if (mtoDescuento1 == 0.00) {
                                        printama.printTextln(uniMed1+" " + PrecioFF + "  " + CantidadFF +"    "+ MtoTotalFF,Printama.RIGHT);
                                    } else {
                                        printama.printTextln(uniMed1+" " + PrecioFF + "  " + CantidadFF +"    "+ MtoCanjeado,Printama.RIGHT);
                                    }
                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
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
                                            printSeparatorLine(printama, tipopapel);
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

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 2:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "03":

                                            if (mtoDescuento1 > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
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

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;
                                                        case 2:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "98":

                                            printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                            break;

                                        case "99":

                                            printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();
                                            printama.addNewLine(1);
                                            printama.setSmallText();
                                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                                            printama.addNewLine(1);

                                            break;
                                    }

                                    break;

                                case "80mm":

                                    switch (tipoDocumento1) {

                                        case "01" :
                                        case "03" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage( logoRobles,logoSize);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            printama.printTextlnBold("PRINCIPAL: " + Address1, Printama.CENTER);
                                            printama.printTextlnBold(Address2, Printama.CENTER);
                                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                                            printama.printTextlnBold(Branch2, Printama.CENTER);
                                            printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);

                                            break;

                                        case "98" :
                                        case "99" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage(logoRobles, logoSize);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                                            printama.printTextlnBold(Branch2, Printama.CENTER);

                                            break;

                                    }

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
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextln("Fecha - Hora : " + fechaDocumento1 + "  Turno: " + turno1, Printama.LEFT);
                                    printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);

                                    if (!nroLado1.isEmpty() && !nroLado1.equals("99")) {
                                        printama.printTextln("Lado         : " + nroLado1, Printama.LEFT);
                                    }

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

                                            if (!observacion1.isEmpty()) {
                                                printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
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

                                                if (!observacion1.isEmpty()) {
                                                    printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
                                                }

                                            }

                                            break;

                                        case "99":

                                            if (!odometro1.isEmpty()) {
                                                printama.printTextln("Kilometraje  : " + odometro1, Printama.LEFT);
                                            }

                                            if (!observacion1.isEmpty()) {
                                                printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
                                            }

                                            printama.printTextln("RUC/DNI      : " + clienteID1, Printama.LEFT);
                                            printama.printTextln("Cliente      : " + clienteRZ1, Printama.LEFT);
                                            printama.printTextln("#Contrato    : " + nroTarjetaNotaD , Printama.LEFT);
                                            break;

                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("PRODUCTO     " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln(articuloDS1, Printama.LEFT);
                                    if (mtoDescuento1 == 0.00) {
                                        printama.printTextln(uniMed1+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoTotalFF,Printama.RIGHT);
                                    } else {
                                        printama.printTextln(uniMed1+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoCanjeado,Printama.RIGHT);
                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
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
                                            printSeparatorLine(printama, tipopapel);
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

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 2:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "03":

                                            if (mtoDescuento1 > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
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

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;
                                                        case 2:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "98":

                                            printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                            break;

                                        case "99":

                                            printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();
                                            printama.addNewLine(1);
                                            printama.setSmallText();
                                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                                            printama.addNewLine(1);

                                            break;
                                    }

                                    break;

                                case "65mm":

                                    switch (tipoDocumento1) {

                                        case "01" :
                                        case "03" :
                                            printama.printImage(Printama.RIGHT,logoRobles, logoSize);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            printama.printTextlnBold("PRINCIPAL: " + Address1, Printama.CENTER);
                                            printama.printTextlnBold(Address2, Printama.CENTER);
                                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                                            printama.printTextlnBold(Branch2, Printama.CENTER);
                                            printama.printTextln("RUC: " + RUCCompany, Printama.CENTER);

                                            break;

                                        case "98" :
                                        case "99" :
                                            printama.printImage(Printama.RIGHT,logoRobles, logoSize);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
                                            printama.printTextlnBold(Branch2, Printama.CENTER);

                                            break;

                                    }

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
                                    printama.printTextln(NroComprobante, Printama.CENTER);

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextln("Fecha - Hora : " + fechaDocumento1 + "  Turno: " + turno1, Printama.LEFT);
                                    printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);
                                    if (!nroLado1.isEmpty() && !nroLado1.equals("99")) {
                                        printama.printTextln("Lado         : " + nroLado1, Printama.LEFT);
                                    }
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

                                            if (!observacion1.isEmpty()) {
                                                printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
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

                                                if (!observacion1.isEmpty()) {
                                                    printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
                                                }

                                            }

                                            break;

                                        case "99":

                                            if (!odometro1.isEmpty()) {
                                                printama.printTextln("Kilometraje  : " + odometro1, Printama.LEFT);
                                            }

                                            if (!observacion1.isEmpty()) {
                                                printama.printTextln("Observación  : " + observacion1, Printama.LEFT);
                                            }

                                            printama.printTextln("RUC/DNI      : " + clienteID1, Printama.LEFT);
                                            printama.printTextln("Cliente      : " + clienteRZ1, Printama.LEFT);
                                            printama.printTextln("#Contrato    : " + nroTarjetaNotaD , Printama.LEFT);
                                            break;

                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("PRODUCTO     " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln(articuloDS1, Printama.LEFT);
                                    if (mtoDescuento1 == 0.00) {
                                        printama.printTextln(uniMed1+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoTotalFF,Printama.RIGHT);
                                    } else {
                                        printama.printTextln(uniMed1+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoCanjeado,Printama.RIGHT);
                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    switch (tipoDocumento1) {

                                        case "01":

                                            if (mtoDescuento1 > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                            }
                                            printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                            printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                            printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (pagoID1) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextln("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                                    switch (tarjetaID1) {

                                                        case 1:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 2:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                    }

                                                    break;

                                                case 4:

                                                    printama.printTextlnBold("CONDICION DE PAGO: 30 DIAS DE", Printama.LEFT);
                                                    printama.printTextln("CREDITO: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                    printama.printImage(Printama.RIGHT,bitmap,200);
                                                }

                                            } catch (WriterException e) {

                                                e.printStackTrace();
                                            }

                                            printama.setSmallText();
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "03":

                                            if (mtoDescuento1 > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                            }
                                            printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (pagoID1) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextln("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);
                                                    switch (tarjetaID1) {

                                                        case 1:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;
                                                        case 2:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (mtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + tarjetaDS1, Printama.LEFT);
                                                            break;
                                                    }

                                                    break;

                                                case 4:
                                                    printama.printTextlnBold("CONDICION DE PAGO: 30 DIAS DE", Printama.LEFT);
                                                    printama.printTextln("CREDITO: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                    printama.printImage(Printama.RIGHT,bitmap,200);
                                                }
                                            } catch (WriterException e) {
                                                e.printStackTrace();
                                            }
                                            printama.setSmallText();
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "98":

                                            printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                            break;

                                        case "99":

                                            printama.printTextln("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();
                                            printama.addNewLine(1);
                                            printama.setSmallText();
                                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                                            printama.addNewLine(1);

                                            break;
                                    }

                                    break;

                            }

                            printama.feedPaper();
                            printama.cutPaper();
                            printama.close();

                        }, this::showToast);

                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            private void printSeparatorLine(Printama printama, String tipopapel) {
                if ("80mm".equals(tipopapel) || "65mm".equals(tipopapel)) {
                    printama.printDoubleDashedLine();
                } else if ("58mm".equals(tipopapel)) {
                    printama.printDoubleDashedLines();
                }
            }

            /** Alerta de Conexión de Bluetooth */
            private void showToast(String message) {
                Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Reimpresion>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Reimpresion - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });
    }

}