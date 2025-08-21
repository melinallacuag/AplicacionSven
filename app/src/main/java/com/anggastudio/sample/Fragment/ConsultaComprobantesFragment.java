package com.anggastudio.sample.Fragment;

import static com.anggastudio.printama.Printama.CENTER;

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
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Button btnConsultar,btnReimpresion,btnGrabaImprimir;

    TextView textMensajePEfectivo,textCTotal,textCAnulado;

    private APIService mAPIService;

    private List<Reimpresion> datosReimpresion;

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
        btnReimpresion    = view.findViewById(R.id.btnReimpresion);
        btnGrabaImprimir  = view.findViewById(R.id.btnGrabaImprimir);

        btnReimpresion.setVisibility(View.GONE);
        btnGrabaImprimir.setVisibility(View.GONE);

        /**
         * @OPCION:ConsultarDatos
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

                ConsultarDatos(GlobalInfo.getConsultaComprobanteTipoDocumento,GlobalInfo.getConsultaComprobanteNroSerie,GlobalInfo.getConsultaComprobanteNroDocumento);
            }
        });

        /**
         * @OPCION:Reimpresiones
         */
        btnReimpresion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reimpresiones(GlobalInfo.getTipoPapel10,GlobalInfo.getConsultaComprobanteTipoDocumento,GlobalInfo.getConsultaComprobanteNroSerie,GlobalInfo.getConsultaComprobanteNroDocumento);
            }
        });

        /**
         * @OPCION:Grabar
         */
        btnGrabaImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
                    alertPEfectivo.setVisibility(View.GONE);
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
                    case "03":
                        inputNroSerie.setText("B");
                        break;
                    case "01":
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

    /**
     * @APISERVICE:ConsultarDatos
     */
    private void ConsultarDatos(String tipodoc, String seriedoc, String nrodoc) {

        Call<List<Reimpresion>> call = mAPIService.findReimpresion(tipodoc, seriedoc, nrodoc);

        call.enqueue(new Callback<List<Reimpresion>>() {
            @Override
            public void onResponse(Call<List<Reimpresion>> call, Response<List<Reimpresion>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Código de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Reimpresion> reimpresionList = response.body();

                if (reimpresionList == null || reimpresionList.isEmpty()) {
                    Toast.makeText(getContext(), "No se encontró el comprobante", Toast.LENGTH_SHORT).show();
                    btnReimpresion.setVisibility(View.GONE);
                    btnGrabaImprimir.setVisibility(View.GONE);
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
                    return;
                }

                Reimpresion r = reimpresionList.get(0);

                textCTotal.setText(String.valueOf(r.getMtoTotal()));
                textCAnulado.setText(String.valueOf(r.getAnulado()));
                inputNroLado.setText(String.valueOf(r.getNroLado()));
                inputNroPlaca.setText(String.valueOf(r.getNroPlaca()));
                inputIdCliente.setText(String.valueOf(r.getClienteID()));
                inputNroTPuntos.setText(String.valueOf(r.getNroTarjetaPuntos()));
                inputRazSocial.setText(String.valueOf(r.getClienteRZ()));
                inputDireccion.setText(String.valueOf(r.getClienteDR()));
                inputObservacion.setText(String.valueOf(r.getObservacion()));

                int cardID = r.getTarjetaID();
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
                    case 7:
                        int indexTransferencia= obtenerIndiceTipoPago("TRANSFERENCIA");
                        SpinnerTPago.setSelection(indexTransferencia);
                        break;

                    default:
                        break;
                }

                tipoPagoAdapter.notifyDataSetChanged();

                String formaPago    = String.valueOf(r.getPagoID());

                if (formaPago.equals("2")) {
                    radioFormaPago.check(radioTarjeta.getId());
                } else if (formaPago.equals("1")) {
                    radioFormaPago.check(radioEfectivo.getId());
                } else if (formaPago.equals("4")){
                    radioFormaPago.check(radioCredito.getId());
                }

                inputOperacion.setText(String.valueOf(r.getTarjetaDS()));
                inputPEfectivo.setText(String.valueOf(r.getMtoTotalEfectivo()));

                btnReimpresion.setVisibility(View.VISIBLE);
                btnGrabaImprimir.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<Reimpresion>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:Reimpresiones
     */
    private void Reimpresiones(String tipopapel,String tipodoc, String seriedoc, String nrodoc) {

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

                    if (reimpresionList == null || reimpresionList.isEmpty()) {
                        Toast.makeText(getContext(), "No se encontró el comprobante", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, List<Reimpresion>> mapComprobantes = new HashMap<>();

                    String fechaDocumento1   = "";
                    String tipoDocumento1    = "";
                    String serieDocumento1   = "";
                    String nroDocumento1     = "";
                    Integer turno1           = 0;
                    String clienteID1        = "";
                    String clienteRZ1        = "";
                    String clienteDR1        = "";
                    String nroPlaca1         = "";
                    String odometro1         = "";
                    String userID1           = "";
                    String anulado1          = "";
                    String articuloID1       = "";
                    String articuloDS1       = "";
                    String uniMed1           = "";
                    Double precio111         = 0.00;
                    Double cantidad1         = 0.00;
                    Double mtoDescuento1     = 0.00;
                    Double mtoSubTotal1      = 0.00;
                    Double mtoImpuesto1      = 0.00;
                    Double mtoTotal1         = 0.00;
                    Integer pagoID1          = 0;
                    Integer tarjetaID1       = 0;
                    String tarjetaDS1        = "";
                    Double mtoPagoPEN1       = 0.00;
                    Double montoCanjeado1    = 0.00;
                    String observacion1      = "";
                    String fechaQR1          = "";
                    String nroLado1          = "";
                    Double mtoTotalEfectivo  = 0.00;
                    String nroTarjetaNotaD   = "";
                    String nroTarjetaPuntos  = "";
                    Double puntosGanados     = 0.00;
                    Double puntosDisponibles = 0.00;


                    String Cajero1           = GlobalInfo.getuserName10;
                    String NroComprobante    = "";

                    for (Reimpresion reimpresion : reimpresionList) {

                        fechaDocumento1   = String.valueOf(reimpresion.getFechaDocumento());
                        tipoDocumento1    = String.valueOf(reimpresion.getTipoDocumento());
                        serieDocumento1   = String.valueOf(reimpresion.getSerieDocumento());
                        nroDocumento1     = String.valueOf(reimpresion.getNroDocumento());
                        turno1           = Integer.valueOf(reimpresion.getTurno());
                        clienteID1        = String.valueOf(reimpresion.getClienteID());
                        clienteRZ1        = String.valueOf(reimpresion.getClienteRZ());
                        clienteDR1        = String.valueOf(reimpresion.getClienteDR());
                        nroPlaca1         = String.valueOf(reimpresion.getNroPlaca());
                        odometro1         = String.valueOf(reimpresion.getOdometro());
                        userID1           = String.valueOf(reimpresion.getUserID());
                        anulado1          = String.valueOf(reimpresion.getAnulado());
                        articuloID1       = String.valueOf(reimpresion.getArticuloID());
                        articuloDS1       = String.valueOf(reimpresion.getArticuloDS());
                        uniMed1           = String.valueOf(reimpresion.getUniMed());
                        precio111         = Double.valueOf(reimpresion.getPrecio1());
                        cantidad1         = Double.valueOf(reimpresion.getCantidad());
                        mtoDescuento1     = Double.valueOf(reimpresion.getMtoDescuento());
                        mtoSubTotal1     += Double.valueOf(reimpresion.getMtoSubTotal());
                        mtoImpuesto1     += Double.valueOf(reimpresion.getMtoImpuesto());
                        mtoTotal1        += Double.valueOf(reimpresion.getMtoTotal());
                        pagoID1           = Integer.valueOf(reimpresion.getPagoID());
                        tarjetaID1        = Integer.valueOf(reimpresion.getTarjetaID());
                        tarjetaDS1        = String.valueOf(reimpresion.getTarjetaDS());
                        mtoPagoPEN1       = Double.valueOf(reimpresion.getMtoPagoPEN());
                        montoCanjeado1    = Double.valueOf(reimpresion.getMontoCanjeado());
                        observacion1      = String.valueOf(reimpresion.getObservacion());
                        fechaQR1          = String.valueOf(reimpresion.getFechaQR());
                        nroLado1          = String.valueOf(reimpresion.getNroLado());
                        mtoTotalEfectivo  = Double.valueOf(reimpresion.getMtoTotalEfectivo());
                        nroTarjetaNotaD   = String.valueOf(reimpresion.getNroTarjetaNotaD());
                        nroTarjetaPuntos  = String.valueOf(reimpresion.getNroTarjetaPuntos());
                        puntosGanados     = Double.valueOf(reimpresion.getPuntosGanados());
                        puntosDisponibles = Double.valueOf(reimpresion.getPuntosDisponibles());

                        NroComprobante    = serieDocumento1 + "-" + nroDocumento1;

                        /**  Verificar si ya existe un comprobante con esta NroComprobante **/
                        if (mapComprobantes.containsKey(NroComprobante)) {
                            /**  Obtiene la lista de productos asociados **/
                            List<Reimpresion> productos = mapComprobantes.get(NroComprobante);
                            productos.add(reimpresion);
                        } else {
                            List<Reimpresion> productos = new ArrayList<>();
                            productos.add(reimpresion);
                            mapComprobantes.put(NroComprobante, productos);
                        }

                    }

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

                    /** Address Company **/

                    String AddressCompany = (GlobalInfo.getAddressCompany10 != null) ? GlobalInfo.getAddressCompany10 : "";
                    String finalAddress = "";
                    String finalAddress1 = "";

                    if (!AddressCompany.isEmpty()) {
                        String[] partesAddress = AddressCompany.split(" - " , 2);
                        finalAddress = partesAddress[0];
                        finalAddress1 = (partesAddress.length > 1) ? partesAddress[1] : "";
                    }
                    String Address1 = finalAddress;
                    String Address2 = finalAddress1;

                    String Address1Part1 = Address1.substring(0, Math.min(Address1.length(), 36));
                    String Address1Part2 = "";

                    if (!Address1Part1.isEmpty()) {
                        if (Address1.length() > 36) {
                            Address1Part2 = Address1.substring(36);
                        }
                    }
                    String finalAddress1Part = Address1Part2;

                    /** Branch Company **/

                    String BranchCompany = (GlobalInfo.getBranchCompany10 != null) ? GlobalInfo.getBranchCompany10 : "";
                    String finalBranch = "";
                    String finalBranch1 = "";

                    if (!BranchCompany.isEmpty()) {
                        String[] partesBranch = BranchCompany.split(" - ", 2);
                        finalBranch = partesBranch[0];
                        finalBranch1 = (partesBranch.length > 1) ? partesBranch[1] : "";
                    }
                    String Branch1 = finalBranch;
                    String Branch2 = finalBranch1;

                    String Branch1Part1 = Branch1.substring(0, Math.min(Branch1.length(), 37));
                    String Branch1Part2 = "";

                    if (!Branch1Part1.isEmpty()) {
                        if (Branch1.length() > 37) {
                            Branch1Part2 = Branch1.substring(37);
                        }
                    }
                    String finalBranch1Part = Branch1Part2;

                    /** Tipo de Documento **/

                    switch (tipoDocumento1) {
                        case "01":
                            TipoDNI = "6";
                            break;
                        case "98":
                            TipoDNI = "0";
                            break;
                    }

                    String MtoSubTotalFF = String.format("%.2f", mtoSubTotal1);

                    String MtoImpuestoFF = String.format("%.2f", mtoImpuesto1);

                    String MtoTotalFF = String.format("%.2f", mtoTotal1);

                    String MtoDescuento = String.format("%.2f", mtoDescuento1);

                    String MTotalEfectivo = String.format("%.2f",mtoTotalEfectivo);

                    String MtoTotalPagoFF = String.format("%.2f",mtoTotal1 - mtoTotalEfectivo);

                    /** Convertir número a letras */
                    Numero_Letras NumLetra = new Numero_Letras();
                    String LetraSoles = NumLetra.Convertir(String.valueOf(mtoTotal1), true);

                    String OpGravadoF =  String.format("%.2f",0.00);

                    /** Generar codigo QR */
                    StringBuilder qrSVEN = new StringBuilder();
                    qrSVEN.append(RUCCompany + "|".toString());
                    qrSVEN.append(tipoDocumento1 + "|".toString());
                    qrSVEN.append(NroComprobante+ "|".toString());
                    qrSVEN.append(MtoImpuestoFF + "|".toString());
                    qrSVEN.append(MtoTotalFF + "|".toString());
                    qrSVEN.append(fechaQR1 + "|".toString());
                    qrSVEN.append(TipoDNI + "|".toString());
                    qrSVEN.append(clienteID1 + "|".toString());

                    String qrSven = qrSVEN.toString();

                    int logoSize = (tipopapel.equals("80mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("58mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("65mm") ? GlobalInfo.getTerminalImageW10 : 400);

                    String finalTipoDocumento  = tipoDocumento1;
                    String finalFechaDocumento = fechaDocumento1;
                    Integer finalTurno         = turno1;
                    String finalNroLado        = nroLado1;
                    String finalNroPlaca       = nroPlaca1;
                    String finalClienteID      = clienteID1;
                    String finalClienteRZ      = clienteRZ1;
                    String finalClienteDR      = clienteDR1;
                    String finalObservacion    = observacion1;
                    String finalOdometro       = odometro1;
                    String finalNroTarjetaNotaD = nroTarjetaNotaD;
                    Double finalMtoDescuento   = mtoDescuento1;
                    Integer finalPagoID        = pagoID1;
                    Integer finalTarjetaID     = tarjetaID1;
                    Double finalMtoTotalEfectivo = mtoTotalEfectivo;
                    String finalTarjetaDS      = tarjetaDS1;
                    String finalNroComprobante = NroComprobante;
                    String finalMontoCanjeado  = String.format("%.2f", montoCanjeado1);
                    Double finalMtoTotal = mtoTotal1;
                    Double finalMontoCanjeado1 = montoCanjeado1;
                    String finalNroTarjetaPuntos = nroTarjetaPuntos;
                    Double finalPuntosGanados = puntosGanados;
                    Double finalPuntosDisponibles = puntosDisponibles;
                    String finalAnulado  = anulado1;


                    textCTotal.setText(String.valueOf(finalMtoTotalEfectivo));
                    textCAnulado.setText(finalAnulado);
                    inputNroLado.setText(finalNroLado);
                    inputNroPlaca.setText(finalNroPlaca);
                    inputIdCliente.setText(finalClienteID);
                    inputNroTPuntos.setText("");
                    inputRazSocial.setText(finalClienteRZ);
                    inputDireccion.setText(finalClienteDR);
                    inputObservacion.setText(finalObservacion);

                    for (Map.Entry<String, List<Reimpresion>> entry : mapComprobantes.entrySet()) {

                        List<Reimpresion> productos = entry.getValue();

                        Printama.with(getContext()).connect(printama -> {

                            switch (tipopapel) {

                                case "58mm":

                                    switch (finalTipoDocumento) {

                                        case "01" :
                                        case "03" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage( logoRobles,logoSize);
                                            printama.addNewLine(GlobalInfo.getterminalFCabecera);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }
                                            if (!Address1.isEmpty()) {
                                                if (!Address1Part1.isEmpty() && !Address2.isEmpty()) {
                                                    printama.printTextlnBold("PRINCIPAL: " + Address1Part1, Printama.CENTER);
                                                    if (!finalAddress1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalAddress1Part + " - " + Address2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Address2, Printama.CENTER);
                                                    }
                                                }
                                            }

                                            if (!Branch1.isEmpty()) {
                                                if (!Branch1Part1.isEmpty() && !Branch2.isEmpty()) {
                                                    printama.printTextlnBold("SUCURSAL: " + Branch1Part1, Printama.CENTER);
                                                    if (!finalBranch1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalBranch1Part + " - " + Branch2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Branch2, Printama.CENTER);
                                                    }
                                                }
                                            }
                                            printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);

                                            break;

                                        case "98" :
                                        case "99" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage(logoRobles, logoSize);
                                            printama.addNewLine(GlobalInfo.getterminalFCabecera);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }

                                            if (!Branch1.isEmpty()) {
                                                if (!Branch1Part1.isEmpty() && !Branch2.isEmpty()) {
                                                    printama.printTextlnBold("SUCURSAL: " + Branch1Part1, Printama.CENTER);
                                                    if (!finalBranch1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalBranch1Part + " - " + Branch2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Branch2, Printama.CENTER);
                                                    }
                                                }
                                            }else{
                                                if (!Address1.isEmpty()) {
                                                    if (!Address1Part1.isEmpty() && !Address2.isEmpty()) {
                                                        printama.printTextlnBold("PRINCIPAL: " + Address1Part1, Printama.CENTER);
                                                        if (!finalAddress1Part.isEmpty()) {
                                                            printama.printTextlnBold(finalAddress1Part + " - " + Address2, Printama.CENTER);
                                                        } else {
                                                            printama.printTextlnBold(Address2, Printama.CENTER);
                                                        }
                                                    }
                                                }
                                            }

                                            break;

                                    }

                                    switch (finalTipoDocumento) {

                                        case "01":
                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                                            }
                                            printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                                            if (GlobalInfo.getVistaQR) {
                                                try {
                                                    String qrContenido = qrSven;
                                                    int qrTamanio = 180;

                                                    Map<EncodeHintType, Object> hints = new HashMap<>();
                                                    hints.put(EncodeHintType.MARGIN, 0);

                                                    BitMatrix bitMatrix = new MultiFormatWriter().encode(
                                                            qrContenido,
                                                            BarcodeFormat.QR_CODE,
                                                            qrTamanio,
                                                            qrTamanio,
                                                            hints
                                                    );

                                                    int width = bitMatrix.getWidth();
                                                    int height = bitMatrix.getHeight();
                                                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                                                    for (int x = 0; x < width; x++) {
                                                        for (int y = 0; y < height; y++) {
                                                            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                                        }
                                                    }

                                                    printama.printImage(bitmap);

                                                } catch (WriterException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            break;

                                        case "03":
                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                                            }
                                            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                                            if (GlobalInfo.getVistaQR) {
                                                try {
                                                    String qrContenido = qrSven;
                                                    int qrTamanio = 180;

                                                    Map<EncodeHintType, Object> hints = new HashMap<>();
                                                    hints.put(EncodeHintType.MARGIN, 0);

                                                    BitMatrix bitMatrix = new MultiFormatWriter().encode(
                                                            qrContenido,
                                                            BarcodeFormat.QR_CODE,
                                                            qrTamanio,
                                                            qrTamanio,
                                                            hints
                                                    );

                                                    int width = bitMatrix.getWidth();
                                                    int height = bitMatrix.getHeight();
                                                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                                                    for (int x = 0; x < width; x++) {
                                                        for (int y = 0; y < height; y++) {
                                                            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                                        }
                                                    }

                                                    printama.printImage(bitmap);

                                                } catch (WriterException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            break;

                                        case "98":
                                            printama.printTextlnBold("TICKET SERAFIN", Printama.CENTER);
                                            break;

                                        case "99":
                                            printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                                            break;

                                    }
                                    printama.printTextlnBold(finalNroComprobante, Printama.CENTER);

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextln("Fecha-Hora : " + finalFechaDocumento, Printama.LEFT);
                                    printama.printTextln("Turno        : " + finalTurno, Printama.LEFT);
                                    printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);
                                    if (!finalNroLado.isEmpty() && !finalNroLado.equals("99")) {
                                        printama.printTextln("Lado         : " + finalNroLado, Printama.LEFT);
                                    }
                                    if (!finalNroPlaca.isEmpty()) {
                                        printama.printTextln("Nro. PLaca   : " + finalNroPlaca, Printama.LEFT);
                                    }
                                    switch (finalTipoDocumento) {

                                        case "01":

                                            printama.printTextln("RUC          : " + finalClienteID, Printama.LEFT);
                                            printama.printTextln("Razon Social : " + finalClienteRZ, Printama.LEFT);

                                            if (!finalClienteDR.isEmpty()) {
                                                printama.printTextln("Dirección    : " + finalClienteDR, Printama.LEFT);
                                            }

                                            if (!finalObservacion.isEmpty()) {
                                                printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                            }

                                            break;

                                        case "03":

                                            if (CVarios.equals(finalClienteID)) {

                                            } else {

                                                printama.printTextln("DNI          : " + finalClienteID, Printama.LEFT);
                                                printama.printTextln("Nombres      : " + finalClienteRZ, Printama.LEFT);

                                                if (!finalClienteDR.isEmpty()) {
                                                    printama.printTextln("Dirección    : " + finalClienteDR, Printama.LEFT);
                                                }

                                                if (!finalObservacion.isEmpty()) {
                                                    printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                                }

                                            }

                                            break;

                                        case "99":

                                            if (!finalOdometro.isEmpty()) {
                                                printama.printTextln("Kilometraje  : " + finalOdometro, Printama.LEFT);
                                            }

                                            if (!finalObservacion.isEmpty()) {
                                                printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                            }

                                            printama.printTextln("RUC/DNI      : " + finalClienteID, Printama.LEFT);
                                            printama.printTextln("Cliente      : " + finalClienteRZ, Printama.LEFT);
                                            printama.printTextln("#Contrato    : " + finalNroTarjetaNotaD, Printama.LEFT);
                                            break;

                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("PROD. " + "U/MED " + "PRE.  " + "CANT.  " + "IMPORTE", Printama.LEFT);
                                    for (Reimpresion producto : productos) {

                                        String articulo       = producto.getArticuloDS();
                                        String UniMed         = producto.getUniMed();
                                        String PrecioPro      = String.format("%.2f", producto.getPrecio1());
                                        String CantidadPro    = String.format("%.3f", producto.getCantidad());
                                        String MtoTotalPro    = String.format("%.2f", producto.getMtoTotal());
                                        String MtoCanjeadoPro = String.format("%.2f", producto.getMontoCanjeado());

                                        printama.printTextln(articulo, Printama.LEFT);

                                        if (finalMtoDescuento == 0.00) {
                                            printama.printTextln(UniMed + " " + PrecioPro + "  " + CantidadPro + "   " + MtoTotalPro, Printama.RIGHT);
                                        } else {
                                            printama.printTextln(UniMed + " " + PrecioPro + "  " + CantidadPro+ "    " + MtoCanjeadoPro, Printama.RIGHT);
                                        }
                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    switch (finalTipoDocumento) {

                                        case "01":

                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                            }

                                            if (GlobalInfo.getsettingImpuestoID110 == 20) {
                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                    printama.printTextln("OP. GRAVADAS: S/  " + OpGravadoF, Printama.RIGHT);
                                                    printama.printTextln("OP. EXONERADAS: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL A PAGAR: S/ "+ MtoTotalFF , Printama.RIGHT);
                                                }else{
                                                    printama.printTextln("OP. GRAVADAS: S/ " + OpGravadoF, Printama.RIGHT);
                                                    printama.printTextln("OP. EXONERADAS: S/ " + MtoTotalFF , Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);
                                                }
                                            }else{
                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                    printama.printTextln("OP. GRAVADAS: S/  " + MtoSubTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextln("TOTAL A PAGAR: S/ "+ MtoTotalFF , Printama.RIGHT);
                                                }else{
                                                    printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);
                                                }
                                            }

                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (finalPagoID) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                                    switch (finalTarjetaID) {

                                                        case 1:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 2:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 7:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("TRANSFERENCIA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
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
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos, Printama.LEFT);
                                                    if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                        printama.printTextlnBold("PUNTOS CANJEADOS     : " + finalPuntosGanados, Printama.LEFT);
                                                    }else{
                                                        printama.printTextlnBold("PUNTOS GANADOS     : " + finalPuntosGanados, Printama.LEFT);
                                                    }
                                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }
                                            printama.setSmallText();
                                            if (GlobalInfo.getsettingImpuestoID110 == 20) {
                                                printSeparatorLine(printama, tipopapel);
                                                printama.addNewLine(1);
                                                printama.setSmallText();
                                                printama.printTextln("Bienes transferidos en la\n" + "Amazonia para ser consumidos en la misma.", CENTER);
                                                printama.setSmallText();
                                                printSeparatorLine(printama, tipopapel);
                                                printama.addNewLine(1);
                                                printama.setSmallText();
                                            }
                                            printama.printTextln("Autorizado mediante resolucion\n" + "de Superintendencia Nro.203-2015\n"+"SUNAT. Representacion impresa de\n"+"la boleta de venta electronica. Consulte desde", CENTER);
                                            printama.printTextln("https://cpesven.apisven.com", CENTER);
                                            break;

                                        case "03":

                                            if (finalMtoDescuento > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                printama.printTextlnBold("TOTAL A PAGAR: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }else{
                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }

                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                            }

                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (finalPagoID) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);
                                                    switch (finalTarjetaID) {

                                                        case 1:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;
                                                        case 2:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
                                                            break;
                                                        case 7:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("TRANSFERENCIA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
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
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos, Printama.LEFT);
                                                    if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                        printama.printTextlnBold("PUNTOS CANJEADOS   : " + finalPuntosGanados, Printama.LEFT);
                                                    }else{
                                                        printama.printTextlnBold("PUNTOS GANADOS     : " + finalPuntosGanados, Printama.LEFT);
                                                    }
                                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }
                                            printama.setSmallText();
                                            printama.printTextln("Autorizado mediante resolucion\n" + "de Superintendencia Nro.203-2015\n"+"SUNAT. Representacion impresa de\n"+"la boleta de venta electronica. Consulte desde", CENTER);
                                            printama.printTextln("https://cpesven.apisven.com", CENTER);
                                            break;

                                        case "98":

                                            printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                            break;

                                        case "99":

                                            if (finalMtoDescuento > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                printama.printTextlnBold("TOTAL A PAGAR: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }else{
                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                    printama.setSmallText();
                                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos , Printama.LEFT);
                                                    printama.printTextlnBold("PUNTOS GANADOS     : " + finalPuntosGanados , Printama.LEFT);
                                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }else{
                                                printama.setSmallText();
                                                printama.addNewLine(1);
                                            }
                                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                                            break;
                                    }

                                    break;

                                case "80mm":

                                    switch (finalTipoDocumento) {

                                        case "01" :
                                        case "03" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage( logoRobles,logoSize);
                                            printama.addNewLine(GlobalInfo.getterminalFCabecera);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }

                                            if (!Address1.isEmpty()) {
                                                if (!Address1Part1.isEmpty() && !Address2.isEmpty()) {
                                                    printama.printTextlnBold("PRINCIPAL: " + Address1Part1, Printama.CENTER);
                                                    if (!finalAddress1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalAddress1Part + " - " + Address2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Address2, Printama.CENTER);
                                                    }
                                                }
                                            }

                                            if (!Branch1.isEmpty()) {
                                                if (!Branch1Part1.isEmpty() && !Branch2.isEmpty()) {
                                                    printama.printTextlnBold("SUCURSAL: " + Branch1Part1, Printama.CENTER);
                                                    if (!finalBranch1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalBranch1Part + " - " + Branch2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Branch2, Printama.CENTER);
                                                    }
                                                }
                                            }

                                            printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);

                                            break;

                                        case "98" :
                                        case "99" :
                                            printama.printTextln("                 ", Printama.CENTER);
                                            printama.printImage(logoRobles, logoSize);
                                            printama.addNewLine(GlobalInfo.getterminalFCabecera);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }

                                            if (!Branch1.isEmpty()) {
                                                if (!Branch1Part1.isEmpty() && !Branch2.isEmpty()) {
                                                    printama.printTextlnBold("SUCURSAL: " + Branch1Part1, Printama.CENTER);
                                                    if (!finalBranch1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalBranch1Part + " - " + Branch2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Branch2, Printama.CENTER);
                                                    }
                                                }
                                            }else{
                                                if (!Address1.isEmpty()) {
                                                    if (!Address1Part1.isEmpty() && !Address2.isEmpty()) {
                                                        printama.printTextlnBold("PRINCIPAL: " + Address1Part1, Printama.CENTER);
                                                        if (!finalAddress1Part.isEmpty()) {
                                                            printama.printTextlnBold(finalAddress1Part + " - " + Address2, Printama.CENTER);
                                                        } else {
                                                            printama.printTextlnBold(Address2, Printama.CENTER);
                                                        }
                                                    }
                                                }
                                            }

                                            break;

                                    }

                                    switch (finalTipoDocumento) {

                                        case "01":
                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                                            }
                                            printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                                            break;

                                        case "03":
                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                                            }
                                            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                                            break;

                                        case "98":
                                            printama.printTextlnBold("TICKET SERAFIN", Printama.CENTER);
                                            break;

                                        case "99":
                                            printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                                            break;

                                    }
                                    printama.printTextlnBold(finalNroComprobante, Printama.CENTER);

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextln("Fecha - Hora : " + finalFechaDocumento + "  Turno: " + finalTurno, Printama.LEFT);
                                    printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);

                                    if (!finalNroLado.isEmpty() && !finalNroLado.equals("99")) {
                                        printama.printTextln("Lado         : " + finalNroLado, Printama.LEFT);
                                    }

                                    if (!finalNroPlaca.isEmpty()) {
                                        printama.printTextln("Nro. PLaca   : " + finalNroPlaca, Printama.LEFT);
                                    }
                                    switch (finalTipoDocumento) {

                                        case "01":

                                            printama.printTextln("RUC          : " + finalClienteID, Printama.LEFT);
                                            printama.printTextln("Razon Social : " + finalClienteRZ, Printama.LEFT);

                                            if (!finalClienteDR.isEmpty()) {
                                                printama.printTextln("Dirección    : " + finalClienteDR, Printama.LEFT);
                                            }

                                            if (!finalObservacion.isEmpty()) {
                                                printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                            }

                                            break;

                                        case "03":

                                            if (CVarios.equals(finalClienteID)) {

                                            } else {

                                                printama.printTextln("DNI          : " + finalClienteID, Printama.LEFT);
                                                printama.printTextln("Nombres      : " + finalClienteRZ, Printama.LEFT);

                                                if (!finalClienteDR.isEmpty()) {
                                                    printama.printTextln("Dirección    : " + finalClienteDR, Printama.LEFT);
                                                }

                                                if (!finalObservacion.isEmpty()) {
                                                    printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                                }

                                            }

                                            break;

                                        case "99":

                                            if (!finalOdometro.isEmpty()) {
                                                printama.printTextln("Kilometraje  : " + finalOdometro, Printama.LEFT);
                                            }

                                            if (!finalObservacion.isEmpty()) {
                                                printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                            }

                                            printama.printTextln("RUC/DNI      : " + finalClienteID, Printama.LEFT);
                                            printama.printTextln("Cliente      : " + finalClienteRZ, Printama.LEFT);
                                            printama.printTextln("#Contrato    : " + finalNroTarjetaNotaD, Printama.LEFT);
                                            break;

                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("PRODUCTO     " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                                    printama.setSmallText();
                                    for (Reimpresion producto : productos) {

                                        String articulo       = producto.getArticuloDS();
                                        String UniMed         = producto.getUniMed();
                                        String PrecioPro      = String.format("%.2f", producto.getPrecio1());
                                        String CantidadPro    = String.format("%.3f", producto.getCantidad());
                                        String MtoTotalPro    = String.format("%.2f", producto.getMtoTotal());
                                        String MtoCanjeadoPro = String.format("%.2f", producto.getMontoCanjeado());

                                        printama.printTextln(articulo, Printama.LEFT);

                                        if (finalMtoDescuento == 0.00) {
                                            printama.printTextln(UniMed + "    " + PrecioPro + "      " + CantidadPro + "     " + MtoTotalPro, Printama.RIGHT);
                                        } else {
                                            printama.printTextln(UniMed + "    " + PrecioPro + "      " + CantidadPro+ "     " + MtoCanjeadoPro, Printama.RIGHT);
                                        }
                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    switch (finalTipoDocumento) {

                                        case "01":

                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                            }

                                            if (GlobalInfo.getsettingImpuestoID110 == 20) {
                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                    printama.printTextln("OP. GRAVADAS: S/  " + OpGravadoF, Printama.RIGHT);
                                                    printama.printTextln("OP. EXONERADAS: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL A PAGAR: S/ "+ MtoTotalFF , Printama.RIGHT);
                                                }else{
                                                    printama.printTextln("OP. GRAVADAS: S/  " + OpGravadoF, Printama.RIGHT);
                                                    printama.printTextln("OP. EXONERADAS: S/ " + MtoTotalFF , Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);
                                                }
                                            }else{
                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                    printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL A PAGAR: S/ "+ MtoTotalFF , Printama.RIGHT);
                                                }else{
                                                    printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);
                                                }
                                            }

                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (finalPagoID) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                                    switch (finalTarjetaID) {

                                                        case 1:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 2:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;
                                                        case 7:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("TRANSFERENCIA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
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
                                            if(GlobalInfo.getVistaQR){
                                                QRCodeWriter writer = new QRCodeWriter();
                                                BitMatrix bitMatrix;
                                                try {

                                                    bitMatrix = writer.encode(qrSven, BarcodeFormat.QR_CODE, 150, 150);
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
                                            }
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos, Printama.LEFT);
                                                    if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                        printama.printTextlnBold("PUNTOS CANJEADOS   : " + finalPuntosGanados, Printama.LEFT);
                                                    }else{
                                                        printama.printTextlnBold("PUNTOS GANADOS     : " + finalPuntosGanados, Printama.LEFT);
                                                    }
                                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }
                                            printama.setSmallText();
                                            if (GlobalInfo.getsettingImpuestoID110 == 20) {
                                                printama.printTextln("Bienes transferidos en la Amazonia para ser\n"+"consumidos en la misma.");
                                                printama.setSmallText();
                                                printSeparatorLine(printama, tipopapel);
                                                printama.addNewLine(1);
                                                printama.setSmallText();
                                            }
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "03":

                                            if (finalMtoDescuento > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                printama.printTextlnBold("TOTAL A PAGAR: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }else{
                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }

                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                            }
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (finalPagoID) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);
                                                    switch (finalTarjetaID) {

                                                        case 1:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;
                                                        case 2:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
                                                            break;

                                                        case 7:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextlnBold("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextlnBold("TRANSFERENCIA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
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
                                            if(GlobalInfo.getVistaQR){
                                                QRCodeWriter writerB = new QRCodeWriter();
                                                BitMatrix bitMatrixB;
                                                try {
                                                    bitMatrixB = writerB.encode(qrSven, BarcodeFormat.QR_CODE, 150, 150);
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
                                            }
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos, Printama.LEFT);
                                                    if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                        printama.printTextlnBold("PUNTOS CANJEADOS   : " + finalPuntosGanados, Printama.LEFT);

                                                    }else{
                                                        printama.printTextlnBold("PUNTOS GANADOS     : " + finalPuntosGanados, Printama.LEFT);

                                                    }
                                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }
                                            printama.setSmallText();
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "98":

                                            printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                            break;

                                        case "99":

                                            if (finalMtoDescuento > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                printama.printTextlnBold("TOTAL A PAGAR: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }else{
                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }
                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                    printama.setSmallText();
                                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos , Printama.LEFT);
                                                    printama.printTextlnBold("PUNTOS GANADOS     : " + finalPuntosGanados , Printama.LEFT);
                                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }else{
                                                printama.setSmallText();
                                                printama.addNewLine(1);
                                            }
                                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                                            break;
                                    }

                                    break;

                                case "65mm":

                                    switch (finalTipoDocumento) {

                                        case "01" :
                                        case "03" :
                                            printama.printImage(Printama.RIGHT,logoRobles, logoSize);
                                            printama.addNewLine(GlobalInfo.getterminalFCabecera);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }
                                            if (!Address1.isEmpty()) {
                                                if (!Address1Part1.isEmpty() && !Address2.isEmpty()) {
                                                    printama.printTextlnBold("PRINCIPAL: " + Address1Part1, Printama.CENTER);
                                                    if (!finalAddress1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalAddress1Part + " - " + Address2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Address2, Printama.CENTER);
                                                    }
                                                }
                                            }

                                            if (!Branch1.isEmpty()) {
                                                if (!Branch1Part1.isEmpty() && !Branch2.isEmpty()) {
                                                    printama.printTextlnBold("SUCURSAL: " + Branch1Part1, Printama.CENTER);
                                                    if (!finalBranch1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalBranch1Part + " - " + Branch2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Branch2, Printama.CENTER);
                                                    }
                                                }
                                            }
                                            printama.printTextln("RUC: " + RUCCompany, Printama.CENTER);

                                            break;

                                        case "98" :
                                        case "99" :
                                            printama.printImage(Printama.RIGHT,logoRobles, logoSize);
                                            printama.addNewLine(GlobalInfo.getterminalFCabecera);
                                            printama.setSmallText();
                                            if(GlobalInfo.getTerminalNameCompany10){
                                                printama.printTextlnBold(NameCompany, Printama.CENTER);
                                            }else {
                                                printama.printTextlnBold(" ");
                                            }

                                            if (!Branch1.isEmpty()) {
                                                if (!Branch1Part1.isEmpty() && !Branch2.isEmpty()) {
                                                    printama.printTextlnBold("SUCURSAL: " + Branch1Part1, Printama.CENTER);
                                                    if (!finalBranch1Part.isEmpty()) {
                                                        printama.printTextlnBold(finalBranch1Part + " - " + Branch2, Printama.CENTER);
                                                    } else {
                                                        printama.printTextlnBold(Branch2, Printama.CENTER);
                                                    }
                                                }
                                            }else{
                                                if (!Address1.isEmpty()) {
                                                    if (!Address1Part1.isEmpty() && !Address2.isEmpty()) {
                                                        printama.printTextlnBold("PRINCIPAL: " + Address1Part1, Printama.CENTER);
                                                        if (!finalAddress1Part.isEmpty()) {
                                                            printama.printTextlnBold(finalAddress1Part + " - " + Address2, Printama.CENTER);
                                                        } else {
                                                            printama.printTextlnBold(Address2, Printama.CENTER);
                                                        }
                                                    }
                                                }
                                            }

                                            break;

                                    }

                                    switch (finalTipoDocumento) {

                                        case "01":
                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                                            }
                                            printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                                            break;

                                        case "03":
                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                                            }
                                            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                                            break;

                                        case "98":
                                            printama.printTextlnBold("TICKET SERAFIN", Printama.CENTER);
                                            break;

                                        case "99":
                                            printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                                            break;

                                    }
                                    printama.printTextln(finalNroComprobante, Printama.CENTER);

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextln("Fecha - Hora : " + finalFechaDocumento + "  Turno: " + finalTurno, Printama.LEFT);
                                    printama.printTextln("Cajero       : " + Cajero1, Printama.LEFT);
                                    if (!finalNroLado.isEmpty() && !finalNroLado.equals("99")) {
                                        printama.printTextln("Lado         : " + finalNroLado, Printama.LEFT);
                                    }
                                    if (!finalNroPlaca.isEmpty()) {
                                        printama.printTextln("Nro. PLaca   : " + finalNroPlaca, Printama.LEFT);
                                    }
                                    switch (finalTipoDocumento) {

                                        case "01":

                                            printama.printTextln("RUC          : " + finalClienteID, Printama.LEFT);
                                            printama.printTextln("Razon Social : " + finalClienteRZ, Printama.LEFT);

                                            if (!finalClienteDR.isEmpty()) {
                                                printama.printTextln("Dirección    : " + finalClienteDR, Printama.LEFT);
                                            }

                                            if (!finalObservacion.isEmpty()) {
                                                printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                            }

                                            break;

                                        case "03":

                                            if (CVarios.equals(finalClienteID)) {

                                            } else {

                                                printama.printTextln("DNI          : " + finalClienteID, Printama.LEFT);
                                                printama.printTextln("Nombres      : " + finalClienteRZ, Printama.LEFT);

                                                if (!finalClienteDR.isEmpty()) {
                                                    printama.printTextln("Dirección    : " + finalClienteDR, Printama.LEFT);
                                                }

                                                if (!finalObservacion.isEmpty()) {
                                                    printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                                }

                                            }

                                            break;

                                        case "99":

                                            if (!finalOdometro.isEmpty()) {
                                                printama.printTextln("Kilometraje  : " + finalOdometro, Printama.LEFT);
                                            }

                                            if (!finalObservacion.isEmpty()) {
                                                printama.printTextln("Observación  : " + finalObservacion, Printama.LEFT);
                                            }

                                            printama.printTextln("RUC/DNI      : " + finalClienteID, Printama.LEFT);
                                            printama.printTextln("Cliente      : " + finalClienteRZ, Printama.LEFT);
                                            printama.printTextln("#Contrato    : " + finalNroTarjetaNotaD, Printama.LEFT);
                                            break;

                                    }

                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("PRODUCTO     " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                                    printama.setSmallText();

                                    for (Reimpresion producto : productos) {
                                        String articulo       = producto.getArticuloDS();
                                        String UniMed         = producto.getUniMed();
                                        String PrecioPro      = String.format("%.2f", producto.getPrecio1());
                                        String CantidadPro    = String.format("%.3f", producto.getCantidad());
                                        String MtoTotalPro    = String.format("%.2f", producto.getMtoTotal());
                                        String MtoCanjeadoPro = String.format("%.2f", producto.getMontoCanjeado());

                                        printama.printTextln(articulo, Printama.LEFT);

                                        if (finalMtoDescuento == 0.00) {
                                            printama.printTextln(UniMed + "    " + PrecioPro + "      " + CantidadPro + "     " + MtoTotalPro, Printama.RIGHT);
                                        } else {
                                            printama.printTextln(UniMed + "    " + PrecioPro + "      " + CantidadPro+ "     " + MtoCanjeadoPro, Printama.RIGHT);
                                        }
                                    }


                                    printama.setSmallText();
                                    printSeparatorLine(printama, tipopapel);
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    switch (finalTipoDocumento) {

                                        case "01":

                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                            }

                                            if (GlobalInfo.getsettingImpuestoID110 == 20) {
                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                    printama.printTextln("OP. GRAVADAS: S/  " + OpGravadoF, Printama.RIGHT);
                                                    printama.printTextln("OP. EXONERADAS: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextln("TOTAL A PAGAR: S/ "+ MtoTotalFF , Printama.RIGHT);
                                                }else{
                                                    printama.printTextln("OP. GRAVADAS: S/ " + OpGravadoF, Printama.RIGHT);
                                                    printama.printTextln("OP. EXONERADAS: S/ " + MtoTotalFF , Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);
                                                }
                                            }else{
                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                    printama.printTextln("OP. GRAVADAS: S/  " + MtoSubTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextln("TOTAL A PAGAR: S/ "+ MtoTotalFF , Printama.RIGHT);
                                                }else{
                                                    printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                    printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);
                                                }
                                            }

                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (finalPagoID) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextln("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                                    switch (finalTarjetaID) {

                                                        case 1:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 2:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;
                                                        case 7:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("TRANSFERENCIA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
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
                                            if(GlobalInfo.getVistaQR){
                                                QRCodeWriter writer = new QRCodeWriter();
                                                BitMatrix bitMatrix;
                                                try {

                                                    bitMatrix = writer.encode(qrSven, BarcodeFormat.QR_CODE, 150, 150);
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
                                                        printama.printImage(Printama.RIGHT,bitmap,150);
                                                    }

                                                } catch (WriterException e) {

                                                    e.printStackTrace();
                                                }
                                            }
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos, Printama.LEFT);
                                                    if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                        printama.printTextln("PUNTOS CANJEADOS   : " + finalPuntosGanados, Printama.LEFT);

                                                    }else{
                                                        printama.printTextln("PUNTOS GANADOS     : " + finalPuntosGanados, Printama.LEFT);
                                                    }
                                                    printama.printTextln("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }
                                            printama.setSmallText();
                                            if (GlobalInfo.getsettingImpuestoID110 == 20) {
                                                printama.printTextln("Bienes transferidos en la Amazonia para ser\n"+"consumidos en la misma.");
                                                printama.setSmallText();
                                                printSeparatorLine(printama, tipopapel);
                                                printama.addNewLine(1);
                                                printama.setSmallText();
                                            }
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "03":

                                            if (finalMtoDescuento > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                printama.printTextln("TOTAL A PAGAR: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }else{
                                                printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }

                                            if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                            }

                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            printama.setSmallText();

                                            switch (finalPagoID) {

                                                case 1:

                                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                                    printama.printTextln("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                                    break;

                                                case 2:

                                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);
                                                    switch (finalTarjetaID) {

                                                        case 1:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;
                                                        case 2:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 3:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 4:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 5:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);

                                                            break;

                                                        case 6:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
                                                            break;
                                                        case 7:

                                                            if (finalMtoTotalEfectivo > 0){
                                                                printama.printTextln("EFECTIVO: S/ " + MTotalEfectivo , Printama.RIGHT);
                                                            }
                                                            printama.printTextln("TRANSFERENCIA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                                            printama.setSmallText();
                                                            printama.printTextln("NRO.OPERACION:" + finalTarjetaDS, Printama.LEFT);
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
                                            if(GlobalInfo.getVistaQR){
                                                QRCodeWriter writerB = new QRCodeWriter();
                                                BitMatrix bitMatrixB;
                                                try {
                                                    bitMatrixB = writerB.encode(qrSven, BarcodeFormat.QR_CODE, 150, 150);
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
                                                        printama.printImage(Printama.RIGHT,bitmap,150);
                                                    }
                                                } catch (WriterException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos, Printama.LEFT);
                                                    if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                        printama.printTextln("PUNTOS CANJEADOS   : " + finalPuntosGanados, Printama.LEFT);
                                                    }else{
                                                        printama.printTextln("PUNTOS GANADOS     : " + finalPuntosGanados, Printama.LEFT);
                                                    }
                                                    printama.printTextln("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }
                                            printama.setSmallText();
                                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "https://cpesven.apisven.com");

                                            break;

                                        case "98":

                                            printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);

                                            break;

                                        case "99":

                                            if (finalMtoDescuento > 0) {
                                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                printama.printTextln("TOTAL A PAGAR: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }else{
                                                printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
                                            }

                                            printama.setSmallText();
                                            printSeparatorLine(printama, tipopapel);
                                            printama.addNewLine(1);
                                            if(GlobalInfo.getTerminalSoloPuntos10){
                                                if(!finalNroTarjetaPuntos.isEmpty() && !finalNroTarjetaPuntos.equals("1")){
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                    printama.setSmallText();
                                                    printama.printTextln("NRO. TARJETA PUNTOS : " + finalNroTarjetaPuntos , Printama.LEFT);
                                                    printama.printTextln("PUNTOS GANADOS     : " + finalPuntosGanados , Printama.LEFT);
                                                    printama.printTextln("PUNTOS DISPONIBLES : " + finalPuntosDisponibles, Printama.LEFT);
                                                    printama.setSmallText();
                                                    printama.addNewLine(1);
                                                }
                                            }else{
                                                printama.setSmallText();
                                                printama.addNewLine(1);
                                            }
                                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                                            break;
                                    }

                                    break;

                            }
                            printama.feedPaper();
                            printama.cutPaper();
                            printama.close();

                        }, this::showToast);

                    }

                    btnReimpresion.setVisibility(View.GONE);
                    btnGrabaImprimir.setVisibility(View.GONE);
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

    /**
     * @APISERVICE:TipoDocumento
     */
    private void  TipoDocumento(){

        List<TipoDocumento> cardTipoDocumento = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            cardTipoDocumento.add(new TipoDocumento("03","BOLETA"));
            cardTipoDocumento.add(new TipoDocumento("01","FACTURA"));
            cardTipoDocumento.add(new TipoDocumento("99","NOTA DE DESPACHO"));
            cardTipoDocumento.add(new TipoDocumento("98","SERAFÍN"));
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

}