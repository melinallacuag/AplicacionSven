package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.anggastudio.sample.Adapter.DetalleVentaAdapter;
import com.anggastudio.sample.Adapter.LClienteAdapter;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ManguerasAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Correlativo;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.VentaCA;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class VentaFragment extends Fragment implements NfcAdapter.ReaderCallback{

    private static final String AUTOMATICO_MODE_KEY = "automatico_mode_key";

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    boolean mTimerRunning;
    Timer timer;
    TimerTask timerTask;
    boolean mIsTaskScheduled = false;

    RecyclerView recyclerLados,recyclerMangueras,recyclerLCliente,recyclerDetalleVenta;

    LadosAdapter ladosAdapter;

    List<Mangueras> filtrarMangueras;
    ManguerasAdapter manguerasAdapter;

    LClienteAdapter lclienteAdapter;

    DetalleVentaAdapter detalleVentaAdapter;

    TipoPago tipoPago;
    TipoPagoAdapter tipoPagoAdapter;

    TextView  datos_terminal,textMensajePEfectivo;

    Dialog modalLibre,modalSoles,modalGalones,modalBoleta,modalClienteDNI,modalClienteRUC,modalFactura,modalNotaDespacho,modalSerafin;

    Button btnAutomatico,btnListadoComprobante,btnLibre,btnCancelarLibre,btnAceptarLibre,btnSoles,btnCancelarSoles,btnAgregarSoles,btnGalones,btnCancelarGalones,btnAgregarGalones,
           btnBoleta,btnCancelarBoleta,btnAgregarBoleta,btnGenerarBoleta,buscarPlacaBoleta,buscarDNIBoleta,btnCancelarLCliente,
            btnFactura,buscarRUCFactura,buscarPlacaFactura,btnCancelarFactura,btnAgregarFactura,btnNotaDespacho,btnCancelarNotaDespacho,btnSerafin,btnCancelarSerafin,btnAgregarSerafin;

    TextInputLayout alertSoles,alertGalones,alertPlaca,alertDNI,alertRUC,alertNombre,alertRazSocial,alertPEfectivo,alertOperacion,alertSelectTPago;

    TextInputEditText inputNFC,inputMontoSoles,inputCantidadGalones,inputPlaca,inputDNI,inputRUC,inputNombre,inputRazSocial,inputDireccion,
            inputObservacion,inputOperacion,inputPEfectivo;

    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioNombreFormaPago;
    Spinner SpinnerTPago;

    SearchView btnBuscadorClienteRZ;

    private APIService mAPIService;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_venta, container, false);

        mAPIService = GlobalInfo.getAPIService();

        btnAutomatico   = view.findViewById(R.id.btnAutomatico);
        btnListadoComprobante = view.findViewById(R.id.btnListadoComprobante);

        datos_terminal  = view.findViewById(R.id.datos_terminal);

        btnLibre        = view.findViewById(R.id.btnLibre);
        btnSoles        = view.findViewById(R.id.btnSoles);
        btnGalones      = view.findViewById(R.id.btnGalones);
        btnBoleta       = view.findViewById(R.id.btnBoleta);
        btnFactura      = view.findViewById(R.id.btnFactura);
        btnNotaDespacho = view.findViewById(R.id.btnnotadespacho);
        btnSerafin      = view.findViewById(R.id.btnSerafin);

        /** Boton Bloqueados */
        btnLibre.setEnabled(false);
        btnSoles.setEnabled(false);
        btnGalones.setEnabled(false);
        btnBoleta.setEnabled(false);
        btnFactura.setEnabled(false);
        btnNotaDespacho.setEnabled(false);
        btnSerafin.setEnabled(false);

        /** Restaurar estado del botón automático */
        if (savedInstanceState != null) {
            mTimerRunning = savedInstanceState.getBoolean(AUTOMATICO_MODE_KEY);
        }

        /** Boton Automatico o Stop */
        btnAutomatico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTimerRunning) {
                    modoStop();
                } else {
                    modoAutomatico();
                }

            }
        });

        /** Establecer el botón automático en el estado correspondiente */
        if(mIsTaskScheduled) {
            modoAutomatico();
        }else{
            modoStop();
        }

        /** Boton para Dirigirse al Listado de Comprobantes */
        btnListadoComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                ListaComprobantesFragment listaComprobantesFragment = (ListaComprobantesFragment) fragmentManager.findFragmentByTag(ListaComprobantesFragment.TAG);
                if (listaComprobantesFragment == null) {
                    listaComprobantesFragment = new ListaComprobantesFragment();
                }

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, listaComprobantesFragment, ListaComprobantesFragment.TAG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        /** Datos de Terminal ID y Turno */
        datos_terminal.setText(GlobalInfo.getterminalID10 + " - " +"TURNO: " + String.valueOf(GlobalInfo.getterminalTurno10));

        /** Listado de Card - Lado */
        recyclerLados = view.findViewById(R.id.recyclerLado);
        recyclerLados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Lados_ByTerminal();

        /** Listado de Card - Manguera */
        recyclerMangueras = view.findViewById(R.id.recyclerMangueras);
        recyclerMangueras.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        /** Mostrar Formulario Libre y Realizar la Operacion */
        modalLibre = new Dialog(getContext());
        modalLibre.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalLibre.setContentView(R.layout.fragment_libre);
        modalLibre.setCancelable(false);

        btnLibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalLibre.show();

                btnCancelarLibre      = modalLibre.findViewById(R.id.btnCancelarLibre);
                btnAceptarLibre       = modalLibre.findViewById(R.id.btnAceptarLibre);

                btnCancelarLibre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalLibre.dismiss();

                    }
                });

                btnAceptarLibre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        guardar_modoLibre(GlobalInfo.getManguera10);

                        Toast.makeText(getContext(), "SE ACTIVO EL MODO LIBRE", Toast.LENGTH_SHORT).show();
                        modalLibre.dismiss();

                    }
                });

            }
        });

        /** Mostrar Formulario Soles y Realizar la Operacion */
        modalSoles = new Dialog(getContext());
        modalSoles.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalSoles.setContentView(R.layout.fragment_soles);
        modalSoles.setCancelable(false);

        btnSoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalSoles.show();

                btnCancelarSoles      = modalSoles.findViewById(R.id.btnCancelarSoles);
                btnAgregarSoles       = modalSoles.findViewById(R.id.btnAgregarSoles);
                inputMontoSoles       = modalSoles.findViewById(R.id.inputMontoSoles);
                alertSoles            = modalSoles.findViewById(R.id.alertSoles);

                btnCancelarSoles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalSoles.dismiss();

                        inputMontoSoles.getText().clear();

                        alertSoles.setErrorEnabled(false);

                    }
                });

                btnAgregarSoles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String MontoSoles = inputMontoSoles.getText().toString();

                        if (MontoSoles.isEmpty()) {
                            alertSoles.setError("El campo soles es obligatorio");
                            return;
                        }

                        Double DoubleMontoSoles = Double.parseDouble(MontoSoles);

                        Integer NumIntSoles = Integer.parseInt(MontoSoles);

                        if(NumIntSoles < 5 || NumIntSoles > 9999){
                            alertSoles.setError("El valor debe ser mayor a 5 y menor que 9999");
                            return;
                        }

                        alertSoles.setErrorEnabled(false);

                        guardar_montoSoles(GlobalInfo.getManguera10,DoubleMontoSoles);

                        Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        modalSoles.dismiss();

                        inputMontoSoles.getText().clear();

                    }
                });

            }
        });

        /** Mostrar Formulario Galones y Realizar la Operacion */
        modalGalones = new Dialog(getContext());
        modalGalones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalGalones.setContentView(R.layout.fragment_galones);
        modalGalones.setCancelable(false);

        btnGalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalGalones.show();

                btnCancelarGalones      = modalGalones.findViewById(R.id.btnCancelarGalones);
                btnAgregarGalones       = modalGalones.findViewById(R.id.btnAgregarGalones);
                inputCantidadGalones    = modalGalones.findViewById(R.id.inputCantidadGalones);
                alertGalones            = modalGalones.findViewById(R.id.alertGalones);

                btnCancelarGalones.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalGalones.dismiss();

                        inputCantidadGalones.getText().clear();

                        alertGalones.setErrorEnabled(false);

                    }
                });

                btnAgregarGalones.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String CantidadGalones = inputCantidadGalones.getText().toString();

                        if (CantidadGalones.isEmpty()) {
                            alertGalones.setError("El campo galones es obligatorio");
                            return;
                        }

                        Double DoubleGalonesMonto  = Double.parseDouble(CantidadGalones);

                        Integer NumIntGalones = Integer.parseInt(CantidadGalones);

                        if(NumIntGalones < 1 || NumIntGalones > 999){
                            alertGalones.setError("El valor debe ser mayor a 1 y menor que 999");
                            return;
                        }

                        alertGalones.setErrorEnabled(false);

                        guardar_galones(GlobalInfo.getManguera10,DoubleGalonesMonto);

                        Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        modalGalones.dismiss();

                        inputCantidadGalones.getText().clear();

                    }
                });

            }
        });

        /** Mostrar Formulario Boleta y Realizar la Operacion */
        modalBoleta = new Dialog(getContext());
        modalBoleta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalBoleta.setContentView(R.layout.fragment_boleta);
        modalBoleta.setCancelable(false);

        btnBoleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalBoleta.show();

                alertPlaca        = modalBoleta.findViewById(R.id.alertPlaca);
                alertDNI          = modalBoleta.findViewById(R.id.alertDNI);
                alertNombre       = modalBoleta.findViewById(R.id.alertNombre);
                alertPEfectivo    = modalBoleta.findViewById(R.id.alertPEfectivo);
                alertOperacion    = modalBoleta.findViewById(R.id.alertOperacion);

                inputPlaca        = modalBoleta.findViewById(R.id.inputPlaca);
                inputDNI          = modalBoleta.findViewById(R.id.inputDNI);
                inputNombre       = modalBoleta.findViewById(R.id.inputNombre);
                inputDireccion    = modalBoleta.findViewById(R.id.inputDireccion);
                inputObservacion  = modalBoleta.findViewById(R.id.inputObservacion);
                inputPEfectivo    = modalBoleta.findViewById(R.id.inputPEfectivo);
                inputOperacion    = modalBoleta.findViewById(R.id.inputOperacion);

                inputNFC          = modalBoleta.findViewById(R.id.input_EtiquetaNFC);

                SpinnerTPago      = modalBoleta.findViewById(R.id.SpinnerTPago);
                alertSelectTPago  = modalBoleta.findViewById(R.id.inputSelectTPago);

                textMensajePEfectivo = modalBoleta.findViewById(R.id.textMensajePEfectivo);

                radioFormaPago    = modalBoleta.findViewById(R.id.radioFormaPago);
                radioEfectivo     = modalBoleta.findViewById(R.id.radioEfectivo);
                radioTarjeta      = modalBoleta.findViewById(R.id.radioTarjeta);
                radioCredito      = modalBoleta.findViewById(R.id.radioCredito);

                buscarDNIBoleta   = modalBoleta.findViewById(R.id.buscarDNIBoleta);
                buscarPlacaBoleta = modalBoleta.findViewById(R.id.buscarPlacaBoleta);
                btnGenerarBoleta  = modalBoleta.findViewById(R.id.btnGenerarBoleta);
                btnCancelarBoleta = modalBoleta.findViewById(R.id.btnCancelarBoleta);
                btnAgregarBoleta  = modalBoleta.findViewById(R.id.btnAgregarBoleta);

                /**
                 * Inicio de Detector NFC
                 * **/
                inputNFC.setKeyListener(null);

                nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());

                Intent intent = new Intent(getContext(), getActivity().getClass());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                intentFilters = new IntentFilter[]{tagIntentFilter};
                techLists = new String[][]{new String[]{NfcA.class.getName(), NfcB.class.getName(),
                        NfcF.class.getName(), NfcV.class.getName(), IsoDep.class.getName(),
                        MifareClassic.class.getName(), MifareUltralight.class.getName(),
                        Ndef.class.getName()}};

                /**
                 * Final de Detector NFC
                 * **/

                /**
                 * Agrega un Listener a tu campo de texto NFC
                 * */
                inputNFC.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 16) {
                            String nfcCode = s.toString();
                            String correspondingName = findCorrespondingName(nfcCode);
                            inputNombre.setText(correspondingName);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // No se necesita hacer nada después de que cambie el texto
                    }
                });

                /**
                 * Mostrar Listado de Cliente para Boleta y Seleccionar - DNI y Nombre
                 * */
                modalClienteDNI = new Dialog(getContext());
                modalClienteDNI.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalClienteDNI.setContentView(R.layout.fragment_clientes);
                modalClienteDNI.setCancelable(false);

                /* Listado de Card - Cliente DNI */
                recyclerLCliente = modalClienteDNI.findViewById(R.id.recyclerLCliente);
                recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));
                ClienteDNI();

                /* Inicio Doble click para abrir modal - Cliente DNI */
                GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalClienteDNI.show();

                        btnCancelarLCliente   = modalClienteDNI.findViewById(R.id.btnCancelarLCliente);
                        btnBuscadorClienteRZ  = modalClienteDNI.findViewById(R.id.btnBuscadorClienteRZ);

                        /* Buscardor por Nombre del Cliente */
                        btnBuscadorClienteRZ.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                String userInput = newText.toLowerCase();
                                if (lclienteAdapter != null) {
                                    lclienteAdapter.filtrado(userInput);
                                }
                                return false;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnBuscadorClienteRZ.setQuery("", false);
                                modalClienteDNI.dismiss();
                            }
                        });

                        return true;
                    }
                });

                inputDNI.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        if (!gestureDetector.onTouchEvent(event)) {

                            return false;
                        }
                        return true;
                    }
                });

                /* Fin Doble click para abrir modal - Cliente DNI */

                /** Seleccionar Opción - Forma de Pago */
                radioFormaPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        radioNombreFormaPago = modalBoleta.findViewById(checkedId);

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

                /** Seleccionar Opción - Tipo de Pago */
                TipoPago_Doc();

                SpinnerTPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        tipoPago = (TipoPago) SpinnerTPago.getSelectedItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                /** Boton Buscador - DNI */
                buscarDNIBoleta.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String campoDNI = inputDNI.getText().toString();

                        if (campoDNI.isEmpty() || campoDNI == null){
                            alertDNI.setError("* El campo DNI es obligatorio");
                            return;
                        }else if (campoDNI.length() < 8){
                            alertDNI.setError("* El DNI debe tener 8 dígitos");
                            return;
                        }

                        findClienteDNI(campoDNI);

                            alertDNI.setErrorEnabled(false);

                            inputNombre.getText().clear();
                            inputDireccion.getText().clear();

                    }
                });

                /** Boton Buscador - Generar Cliente */
                btnGenerarBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputPlaca.setText(GlobalInfo.getsettingNroPlaca10);
                        inputDNI.setText(GlobalInfo.getsettingClienteID10);
                        inputNombre.setText(GlobalInfo.getsettingClienteRZ10);
                    }
                });

                /** Boton Buscador - Cancelar Operacion */
                btnCancelarBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalBoleta.dismiss();

                        /** Limpiar el Formulario - Boleta*/
                        inputPlaca.setText("000-0000");
                        inputDNI.getText().clear();
                        inputNombre.getText().clear();
                        inputDireccion.getText().clear();
                        inputNFC.getText().clear();
                        inputObservacion.getText().clear();
                        radioFormaPago.check(radioEfectivo.getId());
                        inputPEfectivo.setText("0");
                        inputOperacion.getText().clear();

                        alertPlaca.setErrorEnabled(false);
                        alertDNI.setErrorEnabled(false);
                        alertNombre.setErrorEnabled(false);
                        alertPEfectivo.setErrorEnabled(false);
                        alertOperacion.setErrorEnabled(false);
                    }
                });

                /** Boton Buscador - Agregar Operacion */
                btnAgregarBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10 ){

                            if(detalleVenta.getCara().equals(GlobalInfo.getCara10)){

                                String campoPlaca          = inputPlaca.getText().toString();
                                String campoDNI            = inputDNI.getText().toString();
                                String campoNombre         = inputNombre.getText().toString();
                                String campoPEfectivo      = inputPEfectivo.getText().toString();
                                String campoOperacion      = inputOperacion.getText().toString();

                                int checkedRadioButtonId   = radioFormaPago.getCheckedRadioButtonId();

                                if (campoPlaca.isEmpty()) {

                                    alertPlaca.setError("* El campo Placa es obligatorio");
                                    return;
                                } else if (campoDNI.isEmpty() ) {

                                    alertDNI.setError("* El campo DNI es obligatorio");
                                    return;
                                } else if (campoDNI.length() < 8) {

                                    alertDNI.setError("* El DNI debe tener 8 dígitos");
                                    return;
                                }else if (campoNombre.isEmpty()) {

                                    alertNombre.setError("* El campo Nombre es obligatorio");
                                    return;
                                } else if (campoNombre.length() < 8 ) {

                                    alertNombre.setError("* El Nombre debe tener mínino 8 dígitos");
                                    return;
                                }else if (checkedRadioButtonId == radioTarjeta.getId()) {

                                    if (campoOperacion.isEmpty()) {
                                        alertOperacion.setError("* El campo Nro Operación es obligatorio");
                                        return;
                                    } else if (campoOperacion.length() < 4) {
                                        alertOperacion.setError("* El  Nro Operación debe tener 4 dígitos");
                                        return;
                                    } else if (campoPEfectivo.isEmpty()) {
                                        alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }

                                } else if (checkedRadioButtonId == radioCredito.getId()) {

                                    if (campoPEfectivo.isEmpty()) {
                                        alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }

                                }

                                alertPlaca.setErrorEnabled(false);
                                alertDNI.setErrorEnabled(false);
                                alertNombre.setErrorEnabled(false);
                                alertOperacion.setErrorEnabled(false);
                                alertPEfectivo.setErrorEnabled(false);

                                detalleVenta.setNroPlaca(inputPlaca.getText().toString());
                                detalleVenta.setClienteID(inputDNI.getText().toString());
                                detalleVenta.setClienteRUC("");
                                detalleVenta.setClienteRS(inputNombre.getText().toString());
                                detalleVenta.setClienteDR(inputDireccion.getText().toString());

                                detalleVenta.setObservacion(inputObservacion.getText().toString());

                                detalleVenta.setTipoPago(radioNombreFormaPago.getText().toString().substring(0,1));

                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);

                                String NombreFormaPago = radioNombreFormaPago.getText().toString();

                                if (NombreFormaPago.equals("Tarjeta")){

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                    detalleVenta.setOperacionREF(inputOperacion.getText().toString());

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                    }

                                }else if (NombreFormaPago.equals("Credito")) {

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                    }

                                }

                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                modalBoleta.dismiss();

                                /** Limpiar el Formulario - Boleta*/
                                inputPlaca.setText("000-0000");
                                inputDNI.getText().clear();
                                inputNombre.getText().clear();
                                inputDireccion.getText().clear();
                                inputNFC.getText().clear();
                                inputObservacion.getText().clear();
                                inputPEfectivo.setText("0");
                                inputOperacion.getText().clear();
                                radioFormaPago.check(radioEfectivo.getId());

                            }

                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }

                    }
                });

            }
        });

        /** Mostrar Formulario Factura y Realizar la Operacion */
        modalFactura = new Dialog(getContext());
        modalFactura.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalFactura.setContentView(R.layout.fragment_factura);
        modalFactura.setCancelable(false);

        btnFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalFactura.show();

                alertPlaca        = modalFactura.findViewById(R.id.alertPlaca);
                alertRUC          = modalFactura.findViewById(R.id.alertRUC);
                alertRazSocial    = modalFactura.findViewById(R.id.alertRazSocial);
                alertPEfectivo    = modalFactura.findViewById(R.id.alertPEfectivo);
                alertOperacion    = modalFactura.findViewById(R.id.alertOperacion);

                inputPlaca         = modalFactura.findViewById(R.id.inputPlaca);
                inputRUC           = modalFactura.findViewById(R.id.inputRUC);
                inputRazSocial     = modalFactura.findViewById(R.id.inputRazSocial);
                inputDireccion     = modalFactura.findViewById(R.id.inputDireccion);
                inputObservacion   = modalFactura.findViewById(R.id.inputObservacion);
                inputOperacion     = modalFactura.findViewById(R.id.inputOperacion);
                inputPEfectivo     = modalFactura.findViewById(R.id.inputPEfectivo);
                inputNFC           = modalFactura.findViewById(R.id.input_EtiquetaNFC);

                SpinnerTPago       = modalFactura.findViewById(R.id.SpinnerTPago);
                alertSelectTPago   = modalFactura.findViewById(R.id.inputSelectTPago);

                textMensajePEfectivo = modalFactura.findViewById(R.id.textMensajePEfectivo);

                radioFormaPago     = modalFactura.findViewById(R.id.radioFormaPago);
                radioEfectivo      = modalFactura.findViewById(R.id.radioEfectivo);
                radioTarjeta       = modalFactura.findViewById(R.id.radioTarjeta);
                radioCredito       = modalFactura.findViewById(R.id.radioCredito);

                buscarRUCFactura   = modalFactura.findViewById(R.id.buscarRUCFactura);
                buscarPlacaFactura = modalFactura.findViewById(R.id.buscarPlacaFactura);
                btnCancelarFactura = modalFactura.findViewById(R.id.btnCancelarFactura);
                btnAgregarFactura  = modalFactura.findViewById(R.id.btnAgregarFactura);

                /**
                 * Inicio de Detector NFC
                 * **/
                inputNFC.setKeyListener(null);

                nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());

                Intent intent = new Intent(getContext(), getActivity().getClass());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                intentFilters = new IntentFilter[]{tagIntentFilter};
                techLists = new String[][]{new String[]{NfcA.class.getName(), NfcB.class.getName(),
                        NfcF.class.getName(), NfcV.class.getName(), IsoDep.class.getName(),
                        MifareClassic.class.getName(), MifareUltralight.class.getName(),
                        Ndef.class.getName()}};

                /**
                 * Mostrar Listado de Cliente para Factura y Seleccionar RUC - Raz. Social
                 */
                modalClienteRUC = new Dialog(getContext());
                modalClienteRUC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalClienteRUC.setContentView(R.layout.fragment_clientes);
                modalClienteRUC.setCancelable(false);

                /* Listado de Card - Cliente RUC */
                recyclerLCliente = modalClienteRUC.findViewById(R.id.recyclerLCliente);
                recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));
                ClienteRUC();

                /* Inicio Doble click para abrir modal - Cliente RUC */
                final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalClienteRUC.show();

                        btnCancelarLCliente   = modalClienteRUC.findViewById(R.id.btnCancelarLCliente);
                        btnBuscadorClienteRZ  = modalClienteRUC.findViewById(R.id.btnBuscadorClienteRZ);

                        /** Buscardor por Cliente Raz. Social */
                        btnBuscadorClienteRZ.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                String userInput = newText.toLowerCase();
                                if (lclienteAdapter != null) {
                                    lclienteAdapter.filtrado(userInput);
                                }
                                return false;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnBuscadorClienteRZ.setQuery("", false);
                                modalClienteRUC.dismiss();
                            }
                        });

                        return true;
                    }
                });

                inputRUC.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        if (!gestureDetector.onTouchEvent(event)) {

                            return false;
                        }
                        return true;
                    }
                });
                /* Fin Doble click para abrir modal - Cliente RUC */

                /** Seleccionar Opción - Forma de Pago */
                radioFormaPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        radioNombreFormaPago = modalBoleta.findViewById(checkedId);

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

                /** Seleccionar Opción - Tipo de Pago */
                TipoPago_Doc();

                SpinnerTPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        tipoPago = (TipoPago) SpinnerTPago.getSelectedItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                /** Boton Buscador - RUC */
                buscarRUCFactura.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String campoRUC = inputRUC.getText().toString();

                        if (campoRUC.isEmpty() || campoRUC == null) {
                            alertRUC.setError("* El campo RUC es obligatorio");
                            return;
                        }else if (campoRUC.length() < 11){
                            alertRUC.setError("* El RUC debe tener 11 dígitos");
                            return;
                        }/*else if(campoRUC.equals(GlobalInfo.getclienteRUC10)){
                            alertRUC.setError("* No se encontro RUC");
                            return;
                        }*/

                        findClienteRUC(campoRUC);

                        alertRUC.setErrorEnabled(false);

                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();

                    }
                });

                /** Boton Buscador - Cancelar Operacion */
                btnCancelarFactura.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalFactura.dismiss();

                        /** Limpiar el Formulario - Boleta*/
                        inputPlaca.setText("000-0000");
                        inputRUC.getText().clear();
                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();
                        inputNFC.getText().clear();
                        inputObservacion.getText().clear();
                        radioFormaPago.check(radioEfectivo.getId());
                        inputPEfectivo.setText("0");
                        inputOperacion.getText().clear();

                        alertPlaca.setErrorEnabled(false);
                        alertRUC.setErrorEnabled(false);
                        alertRazSocial.setErrorEnabled(false);
                        alertPEfectivo.setErrorEnabled(false);
                        alertOperacion.setErrorEnabled(false);
                    }
                });

                /** Boton Buscador - Agregar Operacion */
                btnAgregarFactura.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10 ) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getCara10)) {

                                String campoPlaca     = inputPlaca.getText().toString();
                                String campoRUC       = inputRUC.getText().toString();
                                String campoRazSocial = inputRazSocial.getText().toString();
                                String campoPEfectivo = inputPEfectivo.getText().toString();
                                String campoOperacion = inputOperacion.getText().toString();

                                int checkedRadioButtonId = radioFormaPago.getCheckedRadioButtonId();

                                if (campoPlaca.isEmpty()) {

                                    alertPlaca.setError("* El campo Placa es obligatorio");
                                    return;
                                } else if (campoRUC.isEmpty()) {

                                    alertRUC.setError("* El campo RUC es obligatorio");
                                    return;
                                } else if (campoRUC.length() < 11) {

                                    alertRUC.setError("* El RUC debe tener 11 dígitos");
                                    return;
                                } else if (campoRazSocial.isEmpty()) {

                                    alertRazSocial.setError("* La Razon Social es obligatorio");
                                    return;
                                }  else if (campoRazSocial.length() < 4 ) {

                                    alertRazSocial.setError("* La Razon Social debe tener mínino 4 dígitos");
                                    return;
                                }else if (checkedRadioButtonId == radioTarjeta.getId()) {

                                    if (campoOperacion.isEmpty()) {

                                        alertOperacion.setError("* El campo Nro Operación es obligatorio");
                                        return;
                                    } else if (campoOperacion.length() < 4) {

                                        alertOperacion.setError("* El  Nro Operación debe tener 4 dígitos");
                                        return;
                                    } else if (campoPEfectivo.isEmpty()) {

                                        alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }

                                } else if (checkedRadioButtonId == radioCredito.getId()) {

                                    if (campoPEfectivo.isEmpty()) {

                                        alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }

                                }

                                alertPlaca.setErrorEnabled(false);
                                alertRUC.setErrorEnabled(false);
                                alertRazSocial.setErrorEnabled(false);
                                alertOperacion.setErrorEnabled(false);
                                alertPEfectivo.setErrorEnabled(false);

                                detalleVenta.setNroPlaca(inputPlaca.getText().toString());
                                detalleVenta.setClienteID("");
                                detalleVenta.setClienteRUC(inputRUC.getText().toString());
                                detalleVenta.setClienteRS(inputRazSocial.getText().toString());
                                detalleVenta.setClienteDR(inputDireccion.getText().toString());
                                detalleVenta.setObservacion(inputObservacion.getText().toString());

                                detalleVenta.setTipoPago(radioNombreFormaPago.getText().toString().substring(0,1));

                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);

                                String NombreFormaPago = radioNombreFormaPago.getText().toString();

                                if (NombreFormaPago.equals("Tarjeta")){

                                    detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                    detalleVenta.setOperacionREF(inputOperacion.getText().toString());

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                    detalleVenta.setOperacionREF(inputOperacion.getText().toString());

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                    }


                                }else if (NombreFormaPago.equals("Credito")) {

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                    }

                                }

                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                modalFactura.dismiss();

                                /** Limpiar el Formulario - Boleta*/
                                inputPlaca.setText("000-0000");
                                inputRUC.getText().clear();
                                inputRazSocial.getText().clear();
                                inputDireccion.getText().clear();
                                inputNFC.getText().clear();
                                inputObservacion.getText().clear();
                                inputPEfectivo.setText("0");
                                inputOperacion.getText().clear();
                                radioFormaPago.check(radioEfectivo.getId());
                            }

                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }

                    }
                });

            }
        });

        /** Mostrar Formulario Nota de Despacho y Realizar la Operacion */
        modalNotaDespacho = new Dialog(getContext());
        modalNotaDespacho.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalNotaDespacho.setContentView(R.layout.fragment_notadespacho);
        modalNotaDespacho.setCancelable(false);

        btnNotaDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalNotaDespacho.show();

                btnCancelarNotaDespacho   = modalNotaDespacho.findViewById(R.id.btnCancelarNotaDespacho);

                /** Boton Buscador - Cancelar Operacion */
                btnCancelarNotaDespacho.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalNotaDespacho.dismiss();

                    }
                });

            }
        });

        /** Mostrar Modal Serafin y Realizar la Operacion */
        modalSerafin = new Dialog(getContext());
        modalSerafin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalSerafin.setContentView(R.layout.fragment_serafin);
        modalSerafin.setCancelable(false);

        btnSerafin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalSerafin.show();

                btnCancelarSerafin = modalSerafin.findViewById(R.id.btnCancelarSerafin);
                btnAgregarSerafin  = modalSerafin.findViewById(R.id.btnAgregarSerafin);

                btnCancelarSerafin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                modalSerafin.dismiss();
                            }
                });

                btnAgregarSerafin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getCara10)) {

                                detalleVenta.setNroPlaca("");
                                detalleVenta.setTipoPago("S");
                                detalleVenta.setClienteID("");
                                detalleVenta.setClienteRUC("");
                                detalleVenta.setClienteRS("");
                                detalleVenta.setClienteDR("");
                                detalleVenta.setKilometraje("");
                                detalleVenta.setObservacion("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setMontoSoles(0.00);

                                Toast.makeText(getContext(), "SE GENERO SERAFIN", Toast.LENGTH_SHORT).show();
                                modalSerafin.dismiss();

                            }
                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }
                    }

                });
            }
        });

        /** Listado de Detalle de Transacciones */
        recyclerDetalleVenta = view.findViewById(R.id.recyclerDetalleVenta);
        recyclerDetalleVenta.setLayoutManager(new LinearLayoutManager(getContext()));
        DetalleVenta();

        return view;
    }

    /** Listado - LADOS */
    private void Lados_ByTerminal() {

        ladosAdapter = new LadosAdapter(GlobalInfo.getladosList10, getContext(), new LadosAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Lados item) {

                /** Boton Desactivado */
                btnLibre.setEnabled(false);
                btnSoles.setEnabled(false);
                btnGalones.setEnabled(false);
                btnBoleta.setEnabled(false);
                btnFactura.setEnabled(false);
                btnNotaDespacho.setEnabled(false);
                btnSerafin.setEnabled(false);

                Manguera_ByLados();

                GlobalInfo.getCara10 = item.getNroLado();

                filtrarMangueras = new ArrayList<>();

                for (Mangueras mangueras : GlobalInfo.getmanguerasList10){
                    if (mangueras.getNroLado().equals(GlobalInfo.getCara10)){
                        filtrarMangueras.add(mangueras);
                    }
                }

                manguerasAdapter.setMangueraList(filtrarMangueras);
                manguerasAdapter.notifyDataSetChanged();

                return 0;
            }
        });
        recyclerLados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerLados.setAdapter(ladosAdapter);

    }

    /** Listado - MANGUERA */
    private void Manguera_ByLados(){

        manguerasAdapter = new ManguerasAdapter(GlobalInfo.getmanguerasList10, getContext(), new ManguerasAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Mangueras item) {

                /** Boton Activados */
                btnLibre.setEnabled(true);
                btnSoles.setEnabled(true);
                btnGalones.setEnabled(true);
                btnBoleta.setEnabled(true);
                btnFactura.setEnabled(true);
                btnNotaDespacho.setEnabled(true);
                btnSerafin.setEnabled(true);

                GlobalInfo.getManguera10 = item.getMangueraID();

                return 0;
            }
        });

        recyclerMangueras.setAdapter(manguerasAdapter);

    }

    /** Listado - CLIENTE CON DNI */
    private void ClienteDNI(){

        Call<List<LClientes>> call = mAPIService.getClienteDNI();

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesList10 = response.body();

                    lclienteAdapter = new LClienteAdapter(GlobalInfo.getlclientesList10, getContext(), new LClienteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(LClientes item) {

                            inputDNI.setText(item.getClienteID());
                            inputNombre.setText(item.getClienteRZ());
                            inputDireccion.setText(item.getClienteDR());

                            btnBuscadorClienteRZ.setQuery("", false);
                            modalClienteDNI.dismiss();
                        }
                    });
                    recyclerLCliente.setAdapter(lclienteAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientes>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente DNI - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Listado - CLIENTE CON RUC */
    private void ClienteRUC(){

        Call<List<LClientes>> call = mAPIService.getClienteRUC();

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesList10 = response.body();

                    lclienteAdapter = new LClienteAdapter(GlobalInfo.getlclientesList10, getContext(), new LClienteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(LClientes item) {

                            inputRUC.setText(item.getClienteRUC());
                            inputRazSocial.setText(item.getClienteRZ());
                            inputDireccion.setText(item.getClienteDR());

                            btnBuscadorClienteRZ.setQuery("", false);
                            modalClienteRUC.dismiss();
                        }
                    });
                    recyclerLCliente.setAdapter(lclienteAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientes>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente DNI - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /** Listado - TIPO PAGO */
    private void  TipoPago_Doc(){

        Resources res = getResources();
        tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) GlobalInfo.gettipopagoList10, res);
        SpinnerTPago.setAdapter(tipoPagoAdapter);

    }

    /** Listado - DETALLE VENTA */
    private void DetalleVenta(){

        detalleVentaAdapter = new DetalleVentaAdapter(GlobalInfo.getdetalleVentaList10, getContext());
        detalleVentaAdapter.notifyDataSetChanged();
        recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

    }

    /** API SERVICE - Buscar Cliente DNI */
    private  void findClienteDNI(String id){

        Call<List<LClientes>> call = mAPIService.findClienteDNI(id);

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesList10 = response.body();

                    LClientes lClientes = GlobalInfo.getlclientesList10.get(0);

                    GlobalInfo.getclienteId10  = String.valueOf(lClientes.getClienteID());
                    GlobalInfo.getclienteRZ10  = String.valueOf(lClientes.getClienteRZ());
                    GlobalInfo.getclienteDR10  = String.valueOf(lClientes.getClienteDR());

                    inputNombre.setText(GlobalInfo.getclienteRZ10 );
                    inputDireccion.setText(GlobalInfo.getclienteDR10);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientes>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente DNI - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API SERVICE - Buscar Cliente RUC */
    private  void findClienteRUC(String id){

        Call<List<LClientes>> call = mAPIService.findClienteRUC(id);

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesList10 = response.body();

                    LClientes lClientes = GlobalInfo.getlclientesList10.get(0);

                    GlobalInfo.getclienteId10  = String.valueOf(lClientes.getClienteID());
                    GlobalInfo.getclienteRUC10 = String.valueOf(lClientes.getClienteRUC());
                    GlobalInfo.getclienteRZ10  = String.valueOf(lClientes.getClienteRZ());
                    GlobalInfo.getclienteDR10  = String.valueOf(lClientes.getClienteDR());

                    inputRazSocial.setText(GlobalInfo.getclienteRZ10);
                    inputDireccion.setText(GlobalInfo.getclienteDR10);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientes>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente RUC - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTOMATICO_MODE_KEY, mIsTaskScheduled);
    }

    /** Se mantenga en el estado actual */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /** Boton Modo - AUTOMATICO */
    private void modoAutomatico() {

        if (!mIsTaskScheduled) {

            timer = new Timer();

            insertarDespacho();

            timer.schedule(timerTask,3000,3000);
            mIsTaskScheduled = true;
        }

        mTimerRunning = true;
        btnAutomatico.setText("Automático");
        btnAutomatico.setBackgroundColor(Color.parseColor("#001E8A"));

        btnListadoComprobante.setEnabled(false);

    }

    private void insertarDespacho() {

        timerTask = new TimerTask() {

            public void run() {

                /** Consultando ventas pendientes por terminal*/
                OptranProcesarNew(GlobalInfo.getterminalImei10);

            }

        };

    }

    /** Boton Modo - STOP */
    private void modoStop() {

        if (mIsTaskScheduled) {
            timer.cancel();
            timer.purge();
            mIsTaskScheduled = false;
        }

        mTimerRunning = false;
        btnAutomatico.setText("Stop");
        btnAutomatico.setBackgroundColor(Color.parseColor("#dc3545"));

        btnListadoComprobante.setEnabled(true);

    }

    /** Guardar - MODO LIBRE */
    private void guardar_modoLibre(String manguera){

        final Mangueras mangueras = new Mangueras(manguera,"01","1","05","DB5","G",999.00);

        Call<Mangueras> call = mAPIService.postMangueras(mangueras);

        call.enqueue(new Callback<Mangueras>() {
            @Override
            public void onResponse(Call<Mangueras> call, Response<Mangueras> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error : " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<Mangueras> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE - Modo Libre", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Guardar - MONTO SOLES */
    private void guardar_montoSoles(String manguera, Double valor){

        final Mangueras mangueras = new Mangueras(manguera,"01","1","05","DB5","S",valor);

        Call<Mangueras> call = mAPIService.postMangueras(mangueras);

        call.enqueue(new Callback<Mangueras>() {
            @Override
            public void onResponse(Call<Mangueras> call, Response<Mangueras> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<Mangueras> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Guardar - GALONES */
    private void guardar_galones(String manguera, Double valor){

        final Mangueras mangueras = new Mangueras(manguera,"01","1","05","DB5","G",valor);

        Call<Mangueras> call = mAPIService.postMangueras(mangueras);

        call.enqueue(new Callback<Mangueras>() {
            @Override
            public void onResponse(Call<Mangueras> call, Response<Mangueras> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<Mangueras> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Correlativo */
    private void findCorrelativo(String id, String mnTipoDocumento, String mnClienteID, String mnClienteRUC, String mnClienteRS, String mnCliernteDR,
                                 Double mnMtoDescuento, Double mnMtoSubTotal1, Double mnMtoImpuesto1, Double mnMtoTotal,
                                 String mnNroPlaca, String mnKilometraje, String mnTipoVenta, String mnObservacion, String mnReferencia,
                                 String mnTarjND, String mnTarjetaPuntos, Double mnPtosGanados, Double mnPtosDisponibles,
                                 Double mnMtoCanje, Integer mnItem, Double mnFise, String mnobservacionDet,
                                 Integer mnPagoID, Integer mnTarjetaCreditoID, String mnOperacionREF, Double mnmtoPagoUSD,
                                 String mnobservacionPag, String mnOperador){

        Call<List<Correlativo>> call = mAPIService.findCorrelativo(id,mnTipoDocumento);

        call.enqueue(new Callback<List<Correlativo>>() {
            @Override
            public void onResponse(Call<List<Correlativo>> call, Response<List<Correlativo>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Correlativo: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Correlativo> correlativoList = response.body();

                    for(Correlativo correlativo: correlativoList) {

                        GlobalInfo.getcorrelativoFecha  = String.valueOf(correlativo.getFechaProceso());
                        GlobalInfo.getcorrelativoSerie  = String.valueOf(correlativo.getSerie());
                        GlobalInfo.getcorrelativoNumero = String.valueOf(correlativo.getNumero());

                    }

                    if (GlobalInfo.getcorrelativoSerie == null){
                        return;
                    }

                    if (GlobalInfo.getcorrelativoSerie.isEmpty()){
                        return;
                    }

                    if (GlobalInfo.getcorrelativoNumero == null){
                        return;
                    }

                    if (GlobalInfo.getcorrelativoNumero.isEmpty()){
                        return;
                    }

                    if (GlobalInfo.getoptranArticuloID10 == null){
                        return;
                    }

                    if (GlobalInfo.getoptranArticuloID10.isEmpty()){
                        return;
                    }

                    if (GlobalInfo.getoptranSoles10 == null){
                        return;
                    }

                    if (GlobalInfo.getoptranNroLado10 == null){
                        return;
                    }

                    if (GlobalInfo.getoptranManguera10 == null){
                        return;
                    }


                    /** Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    String GRFecProceso = GlobalInfo.getcorrelativoFecha;
                    String GRNumeroSerie = GlobalInfo.getcorrelativoSerie;
                    String GRNumeroDocumento = GlobalInfo.getcorrelativoNumero;

                    String NroComprobante = GlobalInfo.getcorrelativoSerie + "-" + GlobalInfo.getcorrelativoNumero;

                    /** Fecha de Impresión */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());
                    String xFechaDocumento       = xFechaHoraImpresion.substring(6,10) + xFechaHoraImpresion.substring(3,5) + xFechaHoraImpresion.substring(0,2) + " " + xFechaHoraImpresion.substring(11,19);
                    String xFechaDocumentoQR     = xFechaHoraImpresion.substring(6,10) + "-" + xFechaHoraImpresion.substring(3,5) + "-" + xFechaHoraImpresion.substring(0,2);

                    /** FIN Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    /** GRABAR VENTA EN BASE DE DATOS*/

                    grabarVentaCA(GlobalInfo.getterminalCompanyID10, mnTipoDocumento, GlobalInfo.getcorrelativoSerie, GlobalInfo.getcorrelativoNumero,
                            GlobalInfo.getterminalID10, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                            GlobalInfo.getterminalTurno10, GlobalInfo.getcorrelativoFecha, xFechaDocumento, GlobalInfo.getoptranFechaTran10,
                            mnMtoDescuento, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal,
                            mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                            mnTarjND, mnTarjetaPuntos, mnPtosGanados, mnPtosDisponibles,
                            mnMtoCanje, GlobalInfo.getuserID10,
                            mnItem, GlobalInfo.getoptranArticuloID10, GlobalInfo.getoptranProductoDs10, GlobalInfo.getoptranUniMed10, GlobalInfo.getterminalAlmacenID10,
                            GlobalInfo.getsettingImpuestoID110, GlobalInfo.getsettingImpuestoValor110, GlobalInfo.getoptranPrecio10, GlobalInfo.getoptranPrecio10, GlobalInfo.getoptranGalones10,
                            mnFise, GlobalInfo.getoptranTranID10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranManguera10,
                            mnobservacionDet,
                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoTotal, mnmtoPagoUSD, mnobservacionPag,
                            mnOperador);

                    /** FIN GRABAR VENTA EN BASE DE DATOS*/

                    /** IMPRESION DEL COMPROBANTE*/

                    if (mnPagoID == 2) {

                        for(int i = 0; i <= 1 ; i += 1){

                            imprimirGR10(mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                    GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10, GlobalInfo.getoptranUniMed10,
                                    GlobalInfo.getoptranPrecio10, GlobalInfo.getoptranGalones10, mnMtoTotal, mnMtoSubTotal1, mnMtoImpuesto1,
                                    mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca, xFechaDocumentoQR,
                                    mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoCanje, mnMtoDescuento);

                        }

                    } else {

                        imprimirGR10(mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10, GlobalInfo.getoptranUniMed10,
                                GlobalInfo.getoptranPrecio10, GlobalInfo.getoptranGalones10, mnMtoTotal, mnMtoSubTotal1, mnMtoImpuesto1,
                                mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca, xFechaDocumentoQR,
                                mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoCanje, mnMtoDescuento);

                    }

                    /** FIN IMPRESION DEL COMPROBANTE*/

                    GlobalInfo.getpase11 = false;

                }catch (Exception ex){
                    Toast.makeText(getActivity(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Correlativo>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Correlativo - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void grabarVentaCA(Integer _companyID, String _tipoDocumento, String _serieDocumento, String _nroDocumento, String _terminalID,
                               String _clienteID, String _clienteRUC, String _clienteRZ, String _clienteDR, Integer _turno,
                               String _fechaproceso, String _fechadocumento, String _fechaAtencion,
                               Double _mtoDescuento, Double _mtoSubTotal, Double _mtoImpuesto, Double _mtoTotal,
                               String _nroPlaca, String _odometro, String _tipoventa, String _observacion, String _referencia,
                               String _nroTarjetaND, String _nroTarjetaPuntos, Double _ptosGanados, Double _ptosDisponibles,
                               Double _mtoCanjeado, String _userID,
                               Integer _nroItem, String _articuloID, String _productoDs, String _uniMed, Integer _almacenID,
                               Integer impuestoID, Integer impuestoValor, Double _precio1, Double _precio2, Double _cantidad,
                               Double _fise, Integer _tranID, String _nroLado, String _manguera,
                               String _observacionDet,
                               Integer _pagoID, Integer _tarjetaID, String _TarjetaDS, Double _mtoPagoPEN, Double _mtoPagoUSD,
                               String _observacionPag, String _Operador
    ){

        String xtranID = _tranID.toString();

        final VentaCA ventaCA = new VentaCA(_companyID, _tipoDocumento, _serieDocumento, _nroDocumento, _terminalID,
                _clienteID, _clienteRUC, _clienteRZ, _clienteDR, _turno,
                _fechaproceso, _fechadocumento, _fechaAtencion,
                _mtoDescuento, _mtoSubTotal, _mtoImpuesto, _mtoTotal,
                _nroPlaca, _odometro, _tipoventa, _observacion, _referencia,
                _nroTarjetaND, _nroTarjetaPuntos, _ptosGanados, _ptosDisponibles,
                _mtoCanjeado, _userID, _Operador,
                _nroItem, _articuloID, _productoDs, _uniMed, _almacenID,
                impuestoID, impuestoValor, _precio1, _precio2, _cantidad,
                _fise, xtranID, _nroLado, _manguera,
                _observacionDet,
                _pagoID, _tarjetaID, _TarjetaDS, _mtoPagoPEN, _mtoPagoUSD,
                _observacionPag);

        Call<VentaCA> call = mAPIService.postVentaCA(ventaCA);

        call.enqueue(new Callback<VentaCA>() {
            @Override
            public void onResponse(Call<VentaCA> call, Response<VentaCA> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error Venta CA: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<VentaCA> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Impresión del Comprobante */

    private void imprimirGR10(String _TipoDocumento, String _NroDocumento, String _FechaDocumento, Integer _Turno,
                              String _Cajero, String _nroLado, String _ArticuloDS, String _ArticuloUMED,
                              Double _Precio, Double _Cantidad, Double _MtoTotal, Double _MtoSubTotal, Double _MtoImpuesto,
                              String _ClienteID, String _ClienteRUC, String _ClienteRZ, String _ClienteDR, String _NroPlaca,
                              String _FechaQR, Integer _PagoID, Integer _TarjetaCreditoID, String _OperacionREF,
                              Double _mtoCanjeado, Double _mtoDescuento){


        Bitmap logoRobles = Printama.getBitmapFromVector(getContext(), R.drawable.logoprincipal);

        String TipoDNI = "1";
        String CVarios = "11111111";

        String NameCompany = GlobalInfo.getNameCompany10;
        String RUCCompany = GlobalInfo.getRucCompany10;
        String AddressCompany = GlobalInfo.getAddressCompany10;
        String Address1 = AddressCompany.substring(0,26);
        String Address2 = AddressCompany.substring(27,50);
        String BranchCompany = GlobalInfo.getBranchCompany10;
        String Branch1 = BranchCompany.substring(0,32);
        String Branch2 = BranchCompany.substring(35,51);

        switch (_TipoDocumento) {
            case "01" :
                TipoDNI = "6";
                break;
            case "98" :
                TipoDNI = "0";
                break;
        }

        String PrecioFF      = String.format("%.2f",_Precio);

        String CantidadFF    = String.format("%.3f",_Cantidad);

        String MtoSubTotalFF = String.format("%.2f",_MtoSubTotal);

        String MtoImpuestoFF = String.format("%.2f",_MtoImpuesto);

        String MtoTotalFF    = String.format("%.2f",_MtoTotal);

        String MtoCanjeado   = String.format("%.2f",_mtoCanjeado);

        String MtoDescuento  = String.format("%.2f",_mtoDescuento);

        /** Convertir número a letras */
        Numero_Letras NumLetra = new Numero_Letras();
        String LetraSoles      = NumLetra.Convertir(String.valueOf(_MtoTotal),true);

        /** Generar codigo QR */
        StringBuilder qrSVEN = new StringBuilder();
        qrSVEN.append(RUCCompany + "|".toString());
        qrSVEN.append(_TipoDocumento+ "|".toString());
        qrSVEN.append(_NroDocumento+ "|".toString());
        qrSVEN.append(MtoImpuestoFF+ "|".toString());
        qrSVEN.append(MtoTotalFF+ "|".toString());
        qrSVEN.append(_FechaQR+ "|".toString());
        qrSVEN.append(TipoDNI+ "|".toString());
        qrSVEN.append(_ClienteID+ "|".toString());

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

            switch (_TipoDocumento) {
                case "01" :
                    printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                    break;
                case "03" :
                    printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                    break;
                case "98" :
                    printama.printTextlnBold("TICKET SERAFIN", Printama.CENTER);
                    break;
                case "99" :
                    printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                    break;
            }

            printama.printTextlnBold(_NroDocumento,Printama.CENTER);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextln("Fecha - Hora : "+ _FechaDocumento + "  Turno: "+ _Turno,Printama.LEFT);
            printama.printTextln("Cajero       : "+ _Cajero , Printama.LEFT);
            printama.printTextln("Lado         : "+ _nroLado, Printama.LEFT);

            if (!_NroPlaca.isEmpty()) {
                printama.printTextln("Nro. PLaca   : "+ _NroPlaca, Printama.LEFT);
            }

            switch (_TipoDocumento) {
                case "01" :
                    printama.printTextln("RUC          : "+ _ClienteID , Printama.LEFT);
                    printama.printTextln("Razon Social : "+ _ClienteRZ, Printama.LEFT);

                    if (!_ClienteDR.isEmpty()) {
                        printama.printTextln("Dirección    : "+ _ClienteDR, Printama.LEFT);
                    }

                    break;
                case "03" :

                    if (CVarios.equals(_ClienteID)){

                    }else {
                        printama.printTextln("DNI          : "+ _ClienteID , Printama.LEFT);
                        printama.printTextln("Nombres      : "+ _ClienteRZ, Printama.LEFT);

                        if (!_ClienteDR.isEmpty()) {
                            printama.printTextln("Dirección    : "+ _ClienteDR, Printama.LEFT);
                        }

                    }
                    break;
                case "99" :
                    break;
            }

            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextlnBold("PRODUCTO      "+"U/MED   "+"PRECIO   "+"CANTIDAD  "+"IMPORTE",Printama.RIGHT);
            printama.setSmallText();
            printama.printTextln(_ArticuloDS,Printama.LEFT);

            if (_mtoDescuento == 0.00) {
                printama.printTextln(_ArticuloUMED+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoTotalFF,Printama.RIGHT);
            } else {
                printama.printTextln(_ArticuloUMED+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoCanjeado,Printama.RIGHT);
            }

            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();


            switch (_TipoDocumento) {
                case "01" :

                    if (_mtoDescuento > 0) {
                        printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                    }

                    printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                    printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);

                    printama.setSmallText();
                    printama.printDoubleDashedLine();
                    printama.addNewLine(1);
                    printama.setSmallText();

                    switch (_PagoID) {
                        case 1 :
                            printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                            printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                            break;
                        case 2 :

                            printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                            switch (_TarjetaCreditoID) {
                                case 1 :
                                    printama.printTextlnBold("VISA: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 2 :
                                    printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 3 :
                                    printama.printTextlnBold("DINERS: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 4 :
                                    printama.printTextlnBold("YAPE: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 5 :
                                    printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 6 :
                                    printama.printTextlnBold("PLIN: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                            }

                            break;

                        case 4 :
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
                    printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");

                    break;
                case "03" :

                    if (_mtoDescuento > 0) {
                        printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                    }

                    printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);

                    printama.setSmallText();
                    printama.printDoubleDashedLine();
                    printama.addNewLine(1);
                    printama.setSmallText();

                    switch (_PagoID) {
                        case 1 :
                            printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                            printama.printTextlnBold("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                            break;
                        case 2 :

                            printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                            switch (_TarjetaCreditoID) {
                                case 1 :
                                    printama.printTextlnBold("VISA: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 2 :
                                    printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 3 :
                                    printama.printTextlnBold("DINERS: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 4 :
                                    printama.printTextlnBold("YAPE: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 5 :
                                    printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                                case 6 :
                                    printama.printTextlnBold("PLIN: S/ " + MtoTotalFF, Printama.RIGHT);
                                    printama.setSmallText();
                                    printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                    break;
                            }

                            break;

                        case 4 :
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
                    printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");

                    break;
                case "98" :
                case "99" :
                    printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                    break;
            }

            printama.feedPaper();
            printama.close();

        }, this::showToast);

    }

    /** API SERVICE - Optran New 2023 */
    private void OptranProcesarNew(String id){

        Call<List<Optran>> call = mAPIService.findOptran(id);

        call.enqueue(new Callback<List<Optran>>() {
            @Override
            public void onResponse(Call<List<Optran>> call, Response<List<Optran>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Optran: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    for(Optran optran: optranList) {

                        GlobalInfo.getoptranTranID10     = Integer.valueOf(optran.getTranID());
                        GlobalInfo.getoptranNroLado10    = String.valueOf(optran.getNroLado());
                        GlobalInfo.getoptranManguera10   = String.valueOf(optran.getManguera());
                        GlobalInfo.getoptranFechaTran10  = String.valueOf(optran.getFechaTran());
                        GlobalInfo.getoptranArticuloID10 = String.valueOf(optran.getArticuloID());
                        GlobalInfo.getoptranProductoDs10 = String.valueOf(optran.getProductoDs());
                        GlobalInfo.getoptranPrecio10     = Double.valueOf(optran.getPrecio());
                        GlobalInfo.getoptranGalones10    = Double.valueOf(optran.getGalones());
                        GlobalInfo.getoptranSoles10      = Double.valueOf(optran.getSoles());
                        GlobalInfo.getoptranOperador10   = String.valueOf(optran.getOperador());
                        GlobalInfo.getoptranCliente10    = String.valueOf(optran.getCliente());
                        GlobalInfo.getoptranUniMed10     = String.valueOf(optran.getUniMed());

                        /** Consultando datos de la lista por nrolado GRID*/

                        if (GlobalInfo.getoptranOperador10.equals("FFFFFFFFFFFFFFFF")) {
                            GlobalInfo.getoptranOperador10 = GlobalInfo.getuseridentFID10;
                        } else if (GlobalInfo.getoptranOperador10.equals("0000000000000000")) {
                            GlobalInfo.getoptranOperador10 = GlobalInfo.getuseridentFID10;
                        }

                        String mnTipoPago = "";
                        Double mnImpuesto = 0.00;
                        String mnNroPlaca = "";
                        String mnTarjetaPuntos = "";
                        String mnClienteID = "";
                        String mnClienteRUC = "";
                        String mnClienteRS = "";
                        String mnCliernteDR = "";
                        String mnTarjND = "";
                        String mnTarjetaCredito = "";
                        String mnOperacionREF = "";
                        String mnObservacion = "";
                        String mnKilometraje = "";
                        Double mnMontoSoles = 0.00;
                        Double mnMtoSaldoCredito = 0.00;
                        Double mnPtosDisponibles = 0.00;

                        String mnTipoVenta = "V";
                        String mnReferencia = "";

                        Double mnPtosGanados = 0.00;

                        Double mnMtoDescuentoUnitario = 0.00;
                        Double mnMtoCanje = 0.00;

                        Double mnMtoDescuento0 = 0.00;
                        Double mnMtoDescuento1 = 0.00;
                        String mnMtoDescuento2 = "";

                        Double mnMtoSubTotal0 = 0.00;
                        Double mnMtoSubTotal1 = 0.00;
                        String mnMtoSubTotal2 = "";

                        Double mnMtoImpuesto0 = 0.00;
                        Double mnMtoImpuesto1 = 0.00;
                        String mnMtoImpuesto2 = "";

                        Double mnMtoTotal = 0.00;
                        String mnMtoTotal2 = "";

                        Integer mnItem = 1;
                        Double mnFise = 0.00;
                        String mnobservacionDet = "";

                        Integer mnPagoID = 1;
                        Integer mnTarjetaCreditoID = 0;
                        String mnobservacionPag = "CONTADO";

                        Double mnmtoPagoUSD = 0.00;

                        String mnTipoDocumento = "";

                        mnMtoDescuento1 = 0.00;
                        mnMtoCanje = GlobalInfo.getoptranSoles10;

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getoptranNroLado10)) {

                                mnTipoPago        = detalleVenta.getTipoPago().toString();
                                mnImpuesto        = detalleVenta.getImpuesto();
                                mnNroPlaca        = detalleVenta.getNroPlaca();
                                mnTarjetaPuntos   = detalleVenta.getTarjetaPuntos();
                                mnClienteID       = detalleVenta.getClienteID();
                                mnClienteRUC      = detalleVenta.getClienteRUC();
                                mnClienteRS       = detalleVenta.getClienteRS().toString();
                                mnCliernteDR      = detalleVenta.getClienteDR().toString();
                                mnTarjND          = detalleVenta.getTarjetaND().toString();
                                mnTarjetaCredito  = detalleVenta.getTarjetaCredito().toString();
                                mnOperacionREF    = detalleVenta.getOperacionREF().toString();
                                mnObservacion     = detalleVenta.getObservacion().toString();
                                mnKilometraje     = detalleVenta.getKilometraje().toString();
                                mnMontoSoles      = detalleVenta.getMontoSoles();
                                mnMtoSaldoCredito = detalleVenta.getMtoSaldoCredito();
                                mnPtosDisponibles = detalleVenta.getPtosDisponible();

                                break;
                            }

                        }

                        /** Consultando datos del DOCUMENTO*/

                        switch (mnTipoPago) {
                            case "E" :
                                if (mnClienteRUC.length() == 11) {
                                    mnClienteID = mnClienteRUC;
                                    mnTipoDocumento = "01";
                                } else if (mnClienteRUC.length() == 0) {
                                    mnTipoDocumento = "03";
                                }
                                break;
                            case "T" :
                                if (mnClienteRUC.length() == 11) {
                                    mnClienteID = mnClienteRUC;
                                    mnTipoDocumento = "01";
                                } else if (mnClienteRUC.length() == 0) {
                                    mnTipoDocumento = "03";
                                }
                                mnPagoID = 2;
                                mnTarjetaCreditoID = Integer.valueOf(mnTarjetaCredito);
                                mnobservacionPag = "TARJETA";
                                break;
                            case "C" :
                                mnPagoID = 4;
                                mnobservacionPag = "CREDITO";
                                break;
                            case "G" :
                                mnTipoVenta = "G";
                                mnobservacionPag = "GRATUITA";
                                break;
                            case "S" :
                                mnTipoDocumento = "98";
                                mnobservacionPag = "SERAFIN";
                                mnClienteID = "11111111";
                                mnClienteRUC = "";
                                mnClienteRS = "CLIENTE VARIOS";
                                mnCliernteDR = "";
                                mnNroPlaca = "000-0000";
                                break;
                        }

                        if (mnClienteID.length() == 0 && mnTipoDocumento == "03") {
                            mnClienteID = "11111111";
                            mnClienteRUC = "";
                            mnClienteRS = "CLIENTE VARIOS";
                            mnCliernteDR = "";
                            mnNroPlaca = "000-0000";
                        }

                        mnMtoDescuento1 = 0.00;
                        mnMtoCanje = GlobalInfo.getoptranSoles10;

                        /** Calculando los totales **/

                        mnMtoTotal = GlobalInfo.getoptranSoles10;

                        mnMtoSubTotal0 = mnMtoTotal / 1.18;
                        mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                        mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                        mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                        if (!mnTipoDocumento.equals("98")) {

                            if (!mnClienteID.equals(GlobalInfo.getsettingClienteID10)) {

                                if (GlobalInfo.getoptranArticuloID10.equals("05") && GlobalInfo.getoptranSoles10 >= 70.00) {

                                    mnMtoDescuentoUnitario = 0.20;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                                } else if (GlobalInfo.getoptranArticuloID10.equals("92") && GlobalInfo.getoptranSoles10 >= 50.00) {

                                    mnMtoDescuentoUnitario = 0.20;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                                } else if (GlobalInfo.getoptranArticuloID10.equals("93") && GlobalInfo.getoptranSoles10 >= 9999.00) {

                                    mnMtoDescuentoUnitario = 0.20;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                                } else if (GlobalInfo.getoptranArticuloID10.equals("07") && GlobalInfo.getoptranSoles10 >= 30.00) {

                                    mnMtoDescuentoUnitario = 0.10;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                                }

                            }

                        }

                        /** Generamos correlativo para grabar **/

                        findCorrelativo(GlobalInfo.getterminalImei10,mnTipoDocumento, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                                mnMtoDescuento1, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal,
                                mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                                mnTarjND, mnTarjetaPuntos, mnPtosGanados, mnPtosDisponibles,
                                mnMtoCanje, mnItem, mnFise, mnobservacionDet,
                                mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnmtoPagoUSD, mnobservacionPag,
                                GlobalInfo.getoptranOperador10);

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getoptranNroLado10)) {

                                detalleVenta.setTipoPago("E");
                                detalleVenta.setImpuesto(18.00);
                                detalleVenta.setNroPlaca("");
                                detalleVenta.setTarjetaPuntos("");
                                detalleVenta.setClienteID("");
                                detalleVenta.setClienteRUC("");
                                detalleVenta.setClienteRS("");
                                detalleVenta.setClienteDR("");
                                detalleVenta.setTarjetaND("");
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setObservacion("");
                                detalleVenta.setKilometraje("");
                                detalleVenta.setMontoSoles(0.00);
                                detalleVenta.setMtoSaldoCredito(0.00);
                                detalleVenta.setPtosDisponible(0.00);

                                recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                                break;
                            }

                        }

                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optran>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Optran */
    private void OptranProcesar(String id){

        Call<List<Optran>> call = mAPIService.findOptran(id);

        call.enqueue(new Callback<List<Optran>>() {
            @Override
            public void onResponse(Call<List<Optran>> call, Response<List<Optran>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Optran: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    for(Optran optran: optranList) {

                        GlobalInfo.getoptranTranID10     = Integer.valueOf(optran.getTranID());
                        GlobalInfo.getoptranNroLado10    = String.valueOf(optran.getNroLado());
                        GlobalInfo.getoptranManguera10   = String.valueOf(optran.getManguera());
                        GlobalInfo.getoptranFechaTran10  = String.valueOf(optran.getFechaTran());
                        GlobalInfo.getoptranArticuloID10 = String.valueOf(optran.getArticuloID());
                        GlobalInfo.getoptranProductoDs10 = String.valueOf(optran.getProductoDs());
                        GlobalInfo.getoptranPrecio10     = Double.valueOf(optran.getPrecio());
                        GlobalInfo.getoptranGalones10    = Double.valueOf(optran.getGalones());
                        GlobalInfo.getoptranSoles10      = Double.valueOf(optran.getSoles());
                        GlobalInfo.getoptranOperador10   = String.valueOf(optran.getOperador());
                        GlobalInfo.getoptranCliente10    = String.valueOf(optran.getCliente());
                        GlobalInfo.getoptranUniMed10     = String.valueOf(optran.getUniMed());

                        /** Consultando datos de la lista por nrolado GRID*/

                        String mnTipoPago = "";
                        Double mnImpuesto = 0.00;
                        String mnNroPlaca = "";
                        String mnTarjetaPuntos = "";
                        String mnClienteID = "";
                        String mnClienteRUC = "";
                        String mnClienteRS = "";
                        String mnCliernteDR = "";
                        String mnTarjND = "";
                        String mnTarjetaCredito = "";
                        String mnOperacionREF = "";
                        String mnObservacion = "";
                        String mnKilometraje = "";
                        Double mnMontoSoles = 0.00;
                        Double mnMtoSaldoCredito = 0.00;
                        Double mnPtosDisponibles = 0.00;

                        String mnTipoVenta = "V";
                        String mnReferencia = "";

                        Double mnPtosGanados = 0.00;

                        Double mnMtoDescuentoUnitario = 0.00;
                        Double mnMtoCanje = 0.00;

                        Double mnMtoDescuento0 = 0.00;
                        Double mnMtoDescuento1 = 0.00;
                        String mnMtoDescuento2 = "";

                        Double mnMtoSubTotal0 = 0.00;
                        Double mnMtoSubTotal1 = 0.00;
                        String mnMtoSubTotal2 = "";

                        Double mnMtoImpuesto0 = 0.00;
                        Double mnMtoImpuesto1 = 0.00;
                        String mnMtoImpuesto2 = "";

                        Double mnMtoTotal = 0.00;
                        String mnMtoTotal2 = "";

                        Integer mnItem = 1;
                        Double mnFise = 0.00;
                        String mnobservacionDet = "";

                        Integer mnPagoID = 1;
                        Integer mnTarjetaCreditoID = 0;
                        Double mnmtoPagoUSD = 0.00;
                        String mnobservacionPag = "";

                        String mnTipoDocumento = "";

                        mnMtoDescuento1 = 0.00;
                        mnMtoCanje = GlobalInfo.getoptranSoles10;

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getoptranNroLado10)) {

                                mnTipoPago        = detalleVenta.getTipoPago().toString();
                                mnImpuesto        = detalleVenta.getImpuesto();
                                mnNroPlaca        = detalleVenta.getNroPlaca();
                                mnTarjetaPuntos   = detalleVenta.getTarjetaPuntos();
                                mnClienteID       = detalleVenta.getClienteID();
                                mnClienteRUC      = detalleVenta.getClienteRUC();
                                mnClienteRS       = detalleVenta.getClienteRS().toString();
                                mnCliernteDR      = detalleVenta.getClienteDR().toString();
                                mnTarjND          = detalleVenta.getTarjetaND().toString();
                                mnTarjetaCredito  = detalleVenta.getTarjetaCredito().toString();
                                mnOperacionREF    = detalleVenta.getOperacionREF().toString();
                                mnObservacion     = detalleVenta.getObservacion().toString();
                                mnKilometraje     = detalleVenta.getKilometraje().toString();
                                mnMontoSoles      = detalleVenta.getMontoSoles();
                                mnMtoSaldoCredito = detalleVenta.getMtoSaldoCredito();
                                mnPtosDisponibles = detalleVenta.getPtosDisponible();

                                break;
                            }

                        }

                        /** Consultando datos del DOCUMENTO*/

                        switch (mnTipoPago) {
                            case "E" :
                                if (mnClienteRUC.length() == 11) {
                                    mnClienteID = mnClienteRUC;
                                    mnTipoDocumento = "01";
                                } else if (mnClienteRUC.length() == 0) {
                                    mnTipoDocumento = "03";
                                }
                                mnPagoID = 1;
                                mnTarjetaCreditoID = 0;
                                mnobservacionPag = "CONTADO";
                                break;
                            case "T" :
                                if (mnClienteRUC.length() == 11) {
                                    mnClienteID = mnClienteRUC;
                                    mnTipoDocumento = "01";
                                } else if (mnClienteRUC.length() == 0) {
                                    mnTipoDocumento = "03";
                                }
                                mnPagoID = 2;
                                mnTarjetaCreditoID = Integer.valueOf(mnTarjetaCredito);
                                mnobservacionPag = "TARJETA";
                                break;
                            case "C" :
                                mnPagoID = 4;
                                mnTarjetaCreditoID = 0;
                                mnobservacionPag = "CREDITO";
                                break;
                            case "G" :
                                mnTipoVenta = "G";
                                mnPagoID = 1;
                                mnTarjetaCreditoID = 0;
                                mnobservacionPag = "GRATUITA";
                                break;
                            case "S" :
                                mnTipoDocumento = "98";
                                mnPagoID = 1;
                                mnTarjetaCreditoID = 0;
                                mnClienteID = "";
                                mnClienteRUC = "";
                                mnClienteRS = "";
                                mnCliernteDR = "";
                                mnNroPlaca = "";
                                break;
                        }

                        if(mnTipoPago == "S"){
                            mnobservacionPag = "SERAFIN";
                            mnTipoDocumento = "98";
                            mnClienteID = "11111111";
                            mnClienteRUC = "";
                            mnClienteRS = "CLIENTE VARIOS";
                            mnCliernteDR = "";
                            mnNroPlaca = "000-0000";
                        }

                        if (mnClienteID.length() == 0 && mnTipoDocumento == "03") {
                            mnClienteID = "11111111";
                            mnClienteRUC = "";
                            mnClienteRS = "CLIENTE VARIOS";
                            mnCliernteDR = "";
                            mnNroPlaca = "000-0000";
                        }

                        mnMtoDescuento1 = 0.00;
                        mnMtoCanje = GlobalInfo.getoptranSoles10;

                        GlobalInfo.getdescuentoArticuloID10 = "05";

                        if (!mnClienteID.equals(GlobalInfo.getsettingClienteID10)) {

                            if (GlobalInfo.getoptranArticuloID10.equals("05") && GlobalInfo.getoptranSoles10 >= 70.00) {

                                if (mnTipoDocumento == "98") {

                                    mnMtoTotal = GlobalInfo.getoptranSoles10;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                } else {

                                    mnMtoDescuentoUnitario = 0.20;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoDescuento2 = String.format("%.2f",mnMtoDescuento1);

                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                }

                            } else if (GlobalInfo.getoptranArticuloID10.equals("92") && GlobalInfo.getoptranSoles10 >= 50.00) {

                                if (mnTipoDocumento == "98") {

                                    mnMtoTotal = GlobalInfo.getoptranSoles10;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                } else {

                                    mnMtoDescuentoUnitario = 0.20;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoDescuento2 = String.format("%.2f",mnMtoDescuento1);

                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                }

                            } else if (GlobalInfo.getoptranArticuloID10.equals("93") && GlobalInfo.getoptranSoles10 >= 9999.00) {

                                if (mnTipoDocumento == "98") {

                                    mnMtoTotal = GlobalInfo.getoptranSoles10;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                } else {

                                    mnMtoDescuentoUnitario = 0.20;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoDescuento2 = String.format("%.2f",mnMtoDescuento1);

                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                }

                            } else if (GlobalInfo.getoptranArticuloID10.equals("07") && GlobalInfo.getoptranSoles10 >= 30.00) {

                                if (mnTipoDocumento == "98") {

                                    mnMtoTotal = GlobalInfo.getoptranSoles10;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                } else {

                                    mnMtoDescuentoUnitario = 0.10;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoDescuento2 = String.format("%.2f",mnMtoDescuento1);

                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;
                                    mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                    mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                    mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                                }

                            }else {

                                mnMtoTotal = GlobalInfo.getoptranSoles10;
                                mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                            }

                        } else {

                            mnMtoTotal = GlobalInfo.getoptranSoles10;
                            mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                            mnMtoSubTotal0 = mnMtoTotal / 1.18;
                            mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                            mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                            mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                            mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                            mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                        }

                        findCorrelativo(GlobalInfo.getterminalImei10,mnTipoDocumento, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                                mnMtoDescuento1, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal,
                                mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                                mnTarjND, mnTarjetaPuntos, mnPtosGanados, mnPtosDisponibles,
                                mnMtoCanje, mnItem, mnFise, mnobservacionDet,
                                mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnmtoPagoUSD, mnobservacionPag,
                                GlobalInfo.getoptranOperador10);

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getoptranNroLado10)) {

                                detalleVenta.setTipoPago("E");
                                detalleVenta.setImpuesto(18.00);
                                detalleVenta.setNroPlaca("");
                                detalleVenta.setTarjetaPuntos("");
                                detalleVenta.setClienteID("");
                                detalleVenta.setClienteRUC("");
                                detalleVenta.setClienteRS("");
                                detalleVenta.setClienteDR("");
                                detalleVenta.setTarjetaND("");
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setObservacion("");
                                detalleVenta.setKilometraje("");
                                detalleVenta.setMontoSoles(0.00);
                                detalleVenta.setMtoSaldoCredito(0.00);
                                detalleVenta.setPtosDisponible(0.00);

                                recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                                break;
                            }

                        }

                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optran>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Alerta de Conexión de Bluetooth */
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(getActivity(), this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V, null);
            return;
        }

        if (mTimerRunning && !mIsTaskScheduled) {
            modoStop();
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(getActivity());
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        String nfcTag = ByteArrayToHexString(tag.getId());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputNFC.setText(nfcTag);
            }
        });
    }

    private String ByteArrayToHexString(byte[] byteArray) {

        StringBuilder sb = new StringBuilder(byteArray.length * 2);

        for (byte b : byteArray) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    private String findCorrespondingName(String nfcCode) {

        List<String> nfcCodes = Arrays.asList("0D746CE5500104E0", "3C746CE5500104E0");
        List<String> names = Arrays.asList("Nombre 1", "Nombre 2");

        int index = nfcCodes.indexOf(nfcCode);
        if (index != -1) {
            return names.get(index);
        }

        // Si no se encuentra un nombre correspondiente, puedes devolver una cadena vacía o un valor predeterminado.
        return "";
    }

}