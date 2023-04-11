package com.anggastudio.sample.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.CaraAdapter;
import com.anggastudio.sample.Adapter.CardAdapter;
import com.anggastudio.sample.Adapter.ClienteAdapter;
import com.anggastudio.sample.Adapter.DetalleVentaAdapter;
import com.anggastudio.sample.Adapter.MangueraAdapter;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Card;
import com.anggastudio.sample.WebApiSVEN.Models.Cliente;
import com.anggastudio.sample.WebApiSVEN.Models.Correlativo;
import com.anggastudio.sample.WebApiSVEN.Models.Descuentos;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.Picos;
import com.anggastudio.sample.WebApiSVEN.Models.Placa;
import com.anggastudio.sample.WebApiSVEN.Models.Setting;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTask;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentaFragment extends Fragment{

    private APIService mAPIService;
    AlertDialog.Builder builder;

    /**  Id Cara */
    private String mCara;

    /**  Atributos de la Venta */
    TextView  terminalID;
    Button    btnlibre,btnsoles,btngalones,btnboleta,btnfactura,btnnotadespacho,btnserafin,btnpuntos,automatiStop,imprimirserafin;

    /**  AdapterList - Recycler */
    RecyclerView recyclerCara, recyclerManguera, recyclerDetalleVenta,recyclerCliente;
    ClienteAdapter clienteAdapter;
    List<Cliente> clienteList;
    CaraAdapter caraAdapter;
    MangueraAdapter mangueraAdapter;
    DetalleVentaAdapter detalleVentaAdapter;
    List<DetalleVenta> detalleVentaList;

    /** Time Task */
    boolean mTimerRunning;
    Timer timer;
    TimerTask timerTask;

    /** Datos de la Boleta - Factura */
    Card cards = null;
    RadioGroup radioGroup;
    Spinner dropStatus;
    TextView modopagoefectivo;
    RadioButton cbefectivo,cbtarjeta,cbcredito,radioButton;
    TextInputEditText  textid,txtplaca,textrazsocial,textdni,textruc,textnombre,textdireccion,textkilometraje,textobservacion,textpagoefectivo,textNroOperacio;
    TextInputLayout alertpefectivo,alertoperacion,alertid,alertplaca,alertdni,alertruc, alertnombre,alertrazsocial,textdropStatus;
    Button btnagregar,btncancelar,btngenerar,buscarplaca,buscardni,buscarruc,buscarid;

    /** Buscador por nombres del Cliente */
    SearchView buscadorUser;

    /** Formularios -  Dialog*/
    private Dialog modalBoleta,modalFactura,modalNDespacho,modalCliente,modalClienteRUC,modalSerafin;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_venta, container, false);
        mAPIService  = GlobalInfo.getAPIService();

        /** Nombre de la Terminal ID */
        terminalID      = view.findViewById(R.id.terminalID);
        automatiStop    = view.findViewById(R.id.automatiStop);
        btnlibre        = view.findViewById(R.id.btnlibre);
        btnsoles        = view.findViewById(R.id.btnsoles);
        btngalones      = view.findViewById(R.id.btngalones);
        btnboleta       = view.findViewById(R.id.btnboleta);
        btnfactura      = view.findViewById(R.id.btnfactura);
        btnnotadespacho = view.findViewById(R.id.btnnotadespacho);
        btnserafin      = view.findViewById(R.id.btnserafin);
        btnpuntos       = view.findViewById(R.id.btnpuntos);

        terminalID.setText(GlobalInfo.getterminalID10 + " - "+ "TURNO: " + GlobalInfo.getterminalTurno10.toString() );


        /** Boton Time Task */
        automatiStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTimerRunning) {
                    stoptimertask();
                } else {
                    startTimerGR1();
                }
            }
        });

        /** Boton Bloqueados */
        btnlibre.setEnabled(false);
        btnsoles.setEnabled(false);
        btngalones.setEnabled(false);
        btnboleta.setEnabled(false);
        btnfactura.setEnabled(false);
        btnnotadespacho.setEnabled(false);
        btnserafin.setEnabled(false);
        btnpuntos.setEnabled(false);


        /** Modalidad Libre */
        btnlibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibreFragment libreFragment = new LibreFragment();
                libreFragment.show(getActivity().getSupportFragmentManager(), "Libre");
                libreFragment.setCancelable(false);
            }
        });

        /** Modalidad Soles */
        btnsoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SolesFragment solesFragment = new SolesFragment();
                solesFragment.show(getActivity().getSupportFragmentManager(), "Soles");
                solesFragment.setCancelable(false);
            }
        });

        /** Modalidad Galones */
        btngalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalonesFragment galonesFragment = new GalonesFragment();
                galonesFragment.show(getActivity().getSupportFragmentManager(), "Galones");
                galonesFragment.setCancelable(false);
            }
        });


        /** Mostrar Formulario de Boleta y Realizar la Operacion */
        modalBoleta = new Dialog(getContext());
        modalBoleta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalBoleta.setContentView(R.layout.fragment_boleta);
        modalBoleta.setCancelable(false);

        btnboleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(DetalleVenta detalleVenta : detalleVentaList){

                    String mnCara = detalleVenta.getCara().toString();

                    if(mnCara.equals(mCara)) {

                        modalBoleta.show();

                        alertplaca       = modalBoleta.findViewById(R.id.alertPlaca);
                        alertdni         = modalBoleta.findViewById(R.id.alertDNI);
                        alertnombre      = modalBoleta.findViewById(R.id.alertNombre);
                        alertpefectivo   = modalBoleta.findViewById(R.id.alertPEfectivo);
                        alertoperacion   = modalBoleta.findViewById(R.id.alertOperacion);

                        txtplaca         = modalBoleta.findViewById(R.id.inputPlaca);
                        textdni          = modalBoleta.findViewById(R.id.inputDNI);
                        textnombre       = modalBoleta.findViewById(R.id.inputNombre);
                        textdireccion    = modalBoleta.findViewById(R.id.inputDireccion);
                        textkilometraje  = modalBoleta.findViewById(R.id.inputKilometraje);
                        textobservacion  = modalBoleta.findViewById(R.id.inputObservacion);
                        textNroOperacio  = modalBoleta.findViewById(R.id.inputOperacion);
                        textpagoefectivo = modalBoleta.findViewById(R.id.inputPEfectivo);
                        modopagoefectivo = modalBoleta.findViewById(R.id.modopagoefectivo);

                        radioGroup       = modalBoleta.findViewById(R.id.radioformapago);
                        cbefectivo       = modalBoleta.findViewById(R.id.radioEfectivo);
                        cbtarjeta        = modalBoleta.findViewById(R.id.radioTarjeta);
                        cbcredito        = modalBoleta.findViewById(R.id.radioCredito);

                        dropStatus       = modalBoleta.findViewById(R.id.dropStatus);
                        textdropStatus   = modalBoleta.findViewById(R.id.textdropStatus);

                        btnagregar       = modalBoleta.findViewById(R.id.btnagregarboleta);
                        btncancelar      = modalBoleta.findViewById(R.id.btncancelar);
                        btngenerar       = modalBoleta.findViewById(R.id.btngenerarcliente);
                        buscardni        = modalBoleta.findViewById(R.id.btnrenic);
                        buscarplaca      = modalBoleta.findViewById(R.id.btnplaca);

                        cbcredito.setEnabled(false);

                        textNroOperacio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {

                                if (!hasFocus) {
                                    textpagoefectivo.setText("0");
                                }
                            }
                        });


                        /** Mostrar Formulario de Cliente y Realizar la Operacion */
                        modalCliente = new Dialog(getContext());
                        modalCliente.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalCliente.setContentView(R.layout.fragment_clientes);
                        modalCliente.setCancelable(false);

                        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onDoubleTap(MotionEvent e) {

                                modalCliente.show();

                                btncancelar    = modalCliente.findViewById(R.id.btncancelar);
                                buscadorUser   = modalCliente.findViewById(R.id.searchView);

                                /** API Retrofit - CLIENTE DNI */
                                getClienteDNI();

                                /**  Listado de Cliente */
                                recyclerCliente = modalCliente.findViewById(R.id.recyclercliente);
                                recyclerCliente.setLayoutManager(new LinearLayoutManager(getContext()));

                                /**  Buscador por Nombre - DNI */
                                buscadorUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        clienteAdapter.filtrado(newText);
                                        return false;
                                    }
                                });

                                btncancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        modalCliente.dismiss();

                                        buscadorUser.setQuery("", false);

                                    }
                                });
                                return true;
                            }
                        });

                        textdni.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                if (!gestureDetector.onTouchEvent(event)) {

                                    return false;
                                }
                                return true;
                            }
                        });



                        /** API Retrofit - Tipo de Pago */
                        getCard();

                        dropStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                cards = (Card) dropStatus.getSelectedItem();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        /** Radio Button de Visiluación de campos */
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                radioButton = modalBoleta.findViewById(checkedId);
                                if (checkedId == cbefectivo.getId()){
                                    modopagoefectivo.setVisibility(View.VISIBLE);
                                    textdropStatus.setVisibility(View.GONE);
                                    alertoperacion.setVisibility(View.GONE);
                                    alertpefectivo.setVisibility(View.GONE);
                                } else if (checkedId == cbtarjeta.getId()){
                                    textdropStatus.setVisibility(View.VISIBLE);
                                    alertpefectivo.setVisibility(View.VISIBLE);
                                    alertoperacion.setVisibility(View.VISIBLE);
                                    modopagoefectivo.setVisibility(View.GONE);
                                } else if (checkedId == cbcredito.getId()){
                                    alertpefectivo.setVisibility(View.VISIBLE);
                                    textdropStatus.setVisibility(View.GONE);
                                    alertoperacion.setVisibility(View.GONE);
                                    modopagoefectivo.setVisibility(View.GONE);
                                }
                            }
                        });

                        /** Boton para cancelar operación */
                        btncancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                modalBoleta.dismiss();

                                txtplaca.setText("000-0000");
                                textdni.getText().clear();
                                textnombre.getText().clear();
                                textdireccion.getText().clear();
                                textkilometraje.getText().clear();
                                textobservacion.getText().clear();
                                textNroOperacio.getText().clear();
                                textpagoefectivo.setText("0");

                                alertplaca.setErrorEnabled(false);
                                alertdni.setErrorEnabled(false);
                                alertnombre.setErrorEnabled(false);
                                alertoperacion.setErrorEnabled(false);
                                alertpefectivo.setErrorEnabled(false);
                            }
                        });

                        /** Buscar Placa - Boleta */
                        buscarplaca.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String getPlacaBoleta = txtplaca.getText().toString();

                                if (getPlacaBoleta.isEmpty()) {
                                    alertplaca.setError("* El campo Placa es obligatorio");
                                }else{
                                    alertplaca.setErrorEnabled(false);

                                    findPlaca(getPlacaBoleta,"03");

                                    textdni.getText().clear();
                                    textnombre.getText().clear();
                                    textdireccion.getText().clear();
                                }
                            }
                        });

                        /** Buscar DNI - Boleta */
                        buscardni.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                String getClienteDni = textdni.getText().toString();

                                if (getClienteDni.isEmpty() || getClienteDni == null) {
                                    alertdni.setError("* El campo DNI es obligatorio");
                                }else {

                                    alertdni.setErrorEnabled(false);

                                    findClienteDNI(getClienteDni);

                                    textnombre.getText().clear();
                                    textdireccion.getText().clear();
                                }

                            }
                        });

                        /** Generar Datos Simples - Boleta */
                        btngenerar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                txtplaca.setText(GlobalInfo.getsettingNroPlaca10);
                                textdni.setText(GlobalInfo.getsettingClienteID10);
                                textnombre.setText(GlobalInfo.getsettingClienteRZ10);
                            }
                        });

                        radioGroup.check(cbefectivo.getId());

                        /** Agregar - Boleta */
                        btnagregar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String textnplaca          = txtplaca.getText().toString();
                                String textndni            = textdni.getText().toString();
                                String textnnombre         = textnombre.getText().toString();
                                String textnNroOperacio    = textNroOperacio.getText().toString();
                                String textnpagoefectivo   = textpagoefectivo.getText().toString();

                                int checkedRadioButtonId   = radioGroup.getCheckedRadioButtonId();

                                if (textnplaca.isEmpty()) {
                                    alertplaca.setError("* El campo Placa es obligatorio");
                                    return;
                                } else if (textndni.isEmpty()) {
                                    alertdni.setError("* El campo DNI es obligatorio");
                                    return;
                                } else if (textndni.length() < 8) {
                                    alertdni.setError("* El DNI debe tener 8 dígitos");
                                    return;
                                }else if (textnnombre.isEmpty()) {
                                    alertnombre.setError("* El campo Nombre es obligatorio");
                                    return;
                                } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                                    Toast.makeText(getContext(), "Por favor, seleccione Tipo de Pago", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (checkedRadioButtonId == cbtarjeta.getId()) {
                                    if (textnNroOperacio.isEmpty()) {
                                        alertoperacion.setError("* El campo Nro Operación es obligatorio");
                                        return;
                                    } else if (textnNroOperacio.length() < 4) {
                                        alertoperacion.setError("* El  Nro Operación debe tener 4 dígitos");
                                        return;
                                    } else if (textnpagoefectivo.isEmpty()) {
                                        alertpefectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }
                                } else if (checkedRadioButtonId == cbcredito.getId()) {
                                    if (textnpagoefectivo.isEmpty()) {
                                        alertpefectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }

                                }


                                alertplaca.setErrorEnabled(false);
                                alertdni.setErrorEnabled(false);
                                alertnombre.setErrorEnabled(false);
                                alertoperacion.setErrorEnabled(false);
                                alertpefectivo.setErrorEnabled(false);

                                detalleVenta.setNroPlaca(txtplaca.getText().toString());
                                detalleVenta.setClienteID(textdni.getText().toString());
                                detalleVenta.setClienteRUC("");
                                detalleVenta.setClienteRS(textnombre.getText().toString());
                                detalleVenta.setClienteDR(textdireccion.getText().toString());
                                detalleVenta.setKilometraje(textkilometraje.getText().toString());
                                detalleVenta.setObservacion(textobservacion.getText().toString());

                                detalleVenta.setTipoPago(radioButton.getText().toString().substring(0,1));

                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);

                                String datotipotarjeta = radioButton.getText().toString();

                                if (datotipotarjeta.equals("Tarjeta")){


                                    Double value = Double.valueOf(textnpagoefectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(cards.getCardID())));
                                    detalleVenta.setOperacionREF(textNroOperacio.getText().toString());

                                    if(value != Double.parseDouble(decimalFormat.format(value))){
                                        alertpefectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(textpagoefectivo.getText().toString()));
                                    }

                                }else if (datotipotarjeta.equals("Credito")) {


                                    Double value = Double.valueOf(textnpagoefectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    if(value != Double.parseDouble(decimalFormat.format(value))){
                                        alertpefectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(textpagoefectivo.getText().toString()));
                                    }
                                }

                                recyclerDetalleVenta.setAdapter(detalleVentaAdapter);
                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                modalBoleta.dismiss();

                                txtplaca.setText("000-0000");
                                textdni.getText().clear();
                                textnombre.getText().clear();
                                textdireccion.getText().clear();
                                textkilometraje.getText().clear();
                                textobservacion.getText().clear();
                                textNroOperacio.getText().clear();
                                textpagoefectivo.setText("0");
                                radioGroup.check(cbefectivo.getId());

                            }
                        });
                    }
                }

            }
        });


        /** Mostrar Formulario de Factura y Realizar la Operacion */
        modalFactura = new Dialog(getContext());
        modalFactura.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalFactura.setContentView(R.layout.fragment_factura);
        modalFactura.setCancelable(false);

        btnfactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(DetalleVenta detalleVenta : detalleVentaList){

                    String mnCara = detalleVenta.getCara().toString();

                    if(mnCara.equals(mCara) ) {

                        modalFactura.show();

                        alertplaca       = modalFactura.findViewById(R.id.alertPlaca);
                        alertruc         = modalFactura.findViewById(R.id.alertRUC);
                        alertrazsocial   = modalFactura.findViewById(R.id.alertRazSocial);
                        alertpefectivo   = modalFactura.findViewById(R.id.alertPEfectivo);
                        alertoperacion   = modalFactura.findViewById(R.id.alertOperacion);

                        txtplaca         = modalFactura.findViewById(R.id.inputPlaca);
                        textruc          = modalFactura.findViewById(R.id.inputRUC);
                        textrazsocial    = modalFactura.findViewById(R.id.inputRazSocial);
                        textdireccion    = modalFactura.findViewById(R.id.inputDireccion);
                        textkilometraje  = modalFactura.findViewById(R.id.inputKilometraje);
                        textobservacion  = modalFactura.findViewById(R.id.inputObservacion);
                        textNroOperacio  = modalFactura.findViewById(R.id.inputOperacion);
                        textpagoefectivo = modalFactura.findViewById(R.id.inputPEfectivo);
                        modopagoefectivo = modalFactura.findViewById(R.id.modopagoefectivo);

                        radioGroup       = modalFactura.findViewById(R.id.radioformapago);
                        cbefectivo       = modalFactura.findViewById(R.id.radioEfectivo);
                        cbtarjeta        = modalFactura.findViewById(R.id.radioTarjeta);
                        cbcredito        = modalFactura.findViewById(R.id.radioCredito);

                        dropStatus       = modalFactura.findViewById(R.id.dropStatus);
                        textdropStatus   = modalFactura.findViewById(R.id.textdropStatus);

                        btnagregar       = modalFactura.findViewById(R.id.btnagregarboleta);
                        btncancelar      = modalFactura.findViewById(R.id.btncancelar);
                        buscarruc        = modalFactura.findViewById(R.id.btnsunat);
                        buscarplaca      = modalFactura.findViewById(R.id.btnplaca);



                        cbcredito.setEnabled(false);

                        textNroOperacio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {

                                if (!hasFocus) {
                                    textpagoefectivo.setText("0");
                                }
                            }
                        });


                        /** Mostrar Formulario de Cliente y Realizar la Operacion */
                        modalClienteRUC = new Dialog(getContext());
                        modalClienteRUC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalClienteRUC.setContentView(R.layout.fragment_clientes);
                        modalClienteRUC.setCancelable(false);

                        final GestureDetector gestureDetector2 = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {


                            public boolean onDoubleTap(MotionEvent e) {

                                modalClienteRUC.show();

                                btncancelar    = modalClienteRUC.findViewById(R.id.btncancelar);
                                buscadorUser   = modalClienteRUC.findViewById(R.id.searchView);

                                /** API Retrofit - CLIENTE DNI */
                                getClienteRUC();

                                /**  Listado de Cliente */
                                recyclerCliente = modalClienteRUC.findViewById(R.id.recyclercliente);
                                recyclerCliente.setLayoutManager(new LinearLayoutManager(getContext()));

                                btncancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        modalClienteRUC.dismiss();

                                        buscadorUser.setQuery("", false);

                                    }
                                });

                                /**  Buscador por Nombre - DNI */
                                buscadorUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        clienteAdapter.filtrado(newText);
                                        return false;
                                    }
                                });

                                return true;
                            }
                        });

                        textruc.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event2) {

                                gestureDetector2.onTouchEvent(event2);
                                if (!gestureDetector2.onTouchEvent(event2)) {

                                    return false;
                                }
                                return true;
                            }
                        });


                        /** API Retrofit - Tipo de Pago */
                        getCard();

                        dropStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                cards = (Card) dropStatus.getSelectedItem();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        /** Radio Button de Visiluación de campos */
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                radioButton = modalFactura.findViewById(checkedId);

                                if (checkedId == cbefectivo.getId()){
                                    modopagoefectivo.setVisibility(View.VISIBLE);
                                    textdropStatus.setVisibility(View.GONE);
                                    alertoperacion.setVisibility(View.GONE);
                                    alertpefectivo.setVisibility(View.GONE);
                                } else if (checkedId == cbtarjeta.getId()){
                                    textdropStatus.setVisibility(View.VISIBLE);
                                    alertpefectivo.setVisibility(View.VISIBLE);
                                    alertoperacion.setVisibility(View.VISIBLE);
                                    modopagoefectivo.setVisibility(View.GONE);
                                } else if (checkedId == cbcredito.getId()){
                                    alertpefectivo.setVisibility(View.VISIBLE);
                                    textdropStatus.setVisibility(View.GONE);
                                    alertoperacion.setVisibility(View.GONE);
                                    modopagoefectivo.setVisibility(View.GONE);
                                }
                            }
                        });

                        /** Boton para cancelar operación */
                        btncancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modalFactura.dismiss();

                                txtplaca.setText("000-0000");
                                textruc.getText().clear();
                                textrazsocial.getText().clear();
                                textdireccion.getText().clear();
                                textkilometraje.getText().clear();
                                textobservacion.getText().clear();
                                textNroOperacio.getText().clear();
                                textpagoefectivo.setText("0");

                                alertplaca.setErrorEnabled(false);
                                alertruc.setErrorEnabled(false);
                                alertrazsocial.setErrorEnabled(false);
                                alertoperacion.setErrorEnabled(false);
                                alertpefectivo.setErrorEnabled(false);
                            }
                        });

                        /** Buscar Placa - Factura */
                        buscarplaca.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String getPlacaFactura = txtplaca.getText().toString();

                                if (getPlacaFactura.isEmpty()) {
                                    alertplaca.setError("* El campo Placa es obligatorio");
                                } else {

                                    alertplaca.setErrorEnabled(false);

                                    findPlaca(getPlacaFactura,"01");

                                    textruc.getText().clear();
                                    textrazsocial.getText().clear();
                                    textdireccion.getText().clear();

                                }

                            }
                        });

                        /** Buscar RUC - Factura */
                        buscarruc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String getClienteRuc    = textruc.getText().toString().trim();

                                if(getClienteRuc.isEmpty()){
                                    alertruc.setError("* El campo RUC es obligatorio");
                                }else{

                                    alertruc.setErrorEnabled(false);

                                    findClienteRUC(getClienteRuc);

                                    textrazsocial.getText().clear();
                                    textdireccion.getText().clear();

                                }
                            }
                        });

                        radioGroup.check(cbefectivo.getId());

                        /** Agregar - Factura */
                        btnagregar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String textnplaca         = txtplaca.getText().toString();
                                String textnruc           = textruc.getText().toString();
                                String textnnombre        = textrazsocial.getText().toString();
                                String textnNroOperacio   = textNroOperacio.getText().toString();
                                String textnpagoefectivo  = textpagoefectivo.getText().toString();
                                int checkedRadioButtonId  = radioGroup.getCheckedRadioButtonId();

                                if (textnplaca.isEmpty()) {
                                    alertplaca.setError("* El campo Placa es obligatorio");
                                    return;
                                } else if (textnruc.isEmpty()) {
                                    alertruc.setError("* El campo DNI es obligatorio");
                                    return;
                                } else if (textnruc.length() < 8) {
                                    alertruc.setError("* El RUC debe tener 11 dígitos");
                                    return;
                                }else if (textnnombre.isEmpty()) {
                                    alertrazsocial.setError("* El campo Nombre es obligatorio");
                                    return;
                                }else if (radioGroup.getCheckedRadioButtonId() == -1){
                                    Toast.makeText(getContext(), "Por favor, seleccione Tipo de Pago", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (checkedRadioButtonId == cbtarjeta.getId()) {
                                    if (textnNroOperacio.isEmpty()) {
                                        alertoperacion.setError("* El campo Nro Operación es obligatorio");
                                        return;
                                    } else if(textnNroOperacio.length() < 4){
                                        alertoperacion.setError("* El  Nro Operación debe tener 4 dígitos");
                                        return;
                                    } else if(textnpagoefectivo.isEmpty()) {
                                        alertpefectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }
                                }else if (checkedRadioButtonId == cbcredito.getId()) {
                                    if(textnpagoefectivo.isEmpty()) {
                                        alertpefectivo.setError("* El campo Pago Efectivo es obligatorio");
                                        return;
                                    }
                                }

                                alertplaca.setErrorEnabled(false);
                                alertruc.setErrorEnabled(false);
                                alertrazsocial.setErrorEnabled(false);
                                alertoperacion.setErrorEnabled(false);
                                alertpefectivo.setErrorEnabled(false);

                                detalleVenta.setNroPlaca(txtplaca.getText().toString());
                                detalleVenta.setClienteID("");
                                detalleVenta.setClienteRUC(textruc.getText().toString());
                                detalleVenta.setClienteRS(textrazsocial.getText().toString());
                                detalleVenta.setClienteDR(textdireccion.getText().toString());
                                detalleVenta.setKilometraje(textkilometraje.getText().toString());
                                detalleVenta.setObservacion(textobservacion.getText().toString());

                                detalleVenta.setTipoPago(radioButton.getText().toString().substring(0,1));

                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);

                                String datotipotarjeta =radioButton.getText().toString();

                                if (datotipotarjeta.equals("Tarjeta")){

                                    Double value = Double.parseDouble(textnpagoefectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(cards.getCardID())));
                                    detalleVenta.setOperacionREF(textNroOperacio.getText().toString());

                                    if(value != Double.parseDouble(decimalFormat.format(value))){
                                        alertpefectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(textpagoefectivo.getText().toString()));
                                    }

                                }else if (datotipotarjeta.equals("Credito") ) {

                                    Double value = Double.parseDouble(textnpagoefectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    if(value != Double.parseDouble(decimalFormat.format(value))){
                                        alertpefectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        detalleVenta.setMontoSoles(Double.parseDouble(textpagoefectivo.getText().toString()));
                                    }

                                }

                                recyclerDetalleVenta.setAdapter(detalleVentaAdapter);
                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                modalFactura.dismiss();

                                txtplaca.setText("000-0000");
                                textruc.getText().clear();
                                textrazsocial.getText().clear();
                                textdireccion.getText().clear();
                                textkilometraje.getText().clear();
                                textobservacion.getText().clear();
                                textNroOperacio.getText().clear();
                                textpagoefectivo.setText("0");
                                radioGroup.check(cbefectivo.getId());
                            }
                        });
                    }
                }
            }
        });


        /** Mostrar Formulario de Nota de Despacho y Realizar la Operacion */
        modalNDespacho = new Dialog(getContext());
        modalNDespacho.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalNDespacho.setContentView(R.layout.fragment_nota_despacho);
        modalNDespacho.setCancelable(false);

        btnnotadespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (DetalleVenta detalleVenta : detalleVentaList){
                    String mnCara = detalleVenta.getCara().toString();

                    if(mnCara.equals(mCara) ) {

                        modalNDespacho.show();

                        alertplaca      = modalNDespacho.findViewById(R.id.alertPlacalaca);
                        alertid         = modalNDespacho.findViewById(R.id.alertIDCliente);
                        alertruc        = modalNDespacho.findViewById(R.id.alertRUC);
                        alertrazsocial  = modalNDespacho.findViewById(R.id.alertRazSocial);

                        txtplaca        = modalNDespacho.findViewById(R.id.inputPlaca);
                        textid          = modalNDespacho.findViewById(R.id.inputIDCliente);
                        textruc         = modalNDespacho.findViewById(R.id.inputRUC);
                        textnombre      = modalNDespacho.findViewById(R.id.inputRazSocial);
                        textdireccion   = modalNDespacho.findViewById(R.id.inputDireccion);
                        textkilometraje = modalNDespacho.findViewById(R.id.inputKilometraje);
                        textobservacion = modalNDespacho.findViewById(R.id.inputObservacion);

                        btnagregar      = modalNDespacho.findViewById(R.id.btnagregarboleta);
                        btncancelar     = modalNDespacho.findViewById(R.id.btncancelarboleta);
                        buscarid        = modalNDespacho.findViewById(R.id.btnsunat);
                        buscarplaca     = modalNDespacho.findViewById(R.id.btnplaca);

                        btncancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modalNDespacho.dismiss();
                            }
                        });

                        buscarplaca.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                builder = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.fragment_clientes, null);
                                builder.setView(dialogView);


                                btncancelar    = dialogView.findViewById(R.id.btncancelar);

                                btncancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        modalNDespacho.dismiss();
                                    }
                                });

                            }
                        });

                        buscarid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String getClienteId    = textid.getText().toString().trim();
                                if(getClienteId.isEmpty()){
                                    alertid.setError("* El campo RUC es obligatorio");
                                }else{
                                    alertid.setErrorEnabled(false);
                                    findCliente(getClienteId,"99");

                                    textruc.getText().clear();
                                    textnombre.getText().clear();
                                    textdireccion.getText().clear();

                                }
                            }
                        });

                        btnagregar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String textnplaca     = txtplaca.getText().toString();
                                String textndni       = textid.getText().toString();
                                String textnruc       = textruc.getText().toString();
                                String textnnombre    = textnombre.getText().toString();

                                if (textnplaca.isEmpty()) {
                                    alertplaca.setError("* El campo Placa es obligatorio");
                                } else if (textndni.isEmpty()) {
                                    alertid.setError("* El campo DNI es obligatorio");
                                }else if (textnruc.isEmpty()) {
                                    alertruc.setError("* El campo RUC es obligatorio");
                                } else if (textnnombre.isEmpty()) {
                                    alertrazsocial.setError("* El campo Nombre es obligatorio");
                                } else {
                                    alertplaca.setErrorEnabled(false);
                                    alertid.setErrorEnabled(false);
                                    alertruc.setErrorEnabled(false);
                                    alertrazsocial.setErrorEnabled(false);

                                    detalleVenta.setNroPlaca(txtplaca.getText().toString());
                                    detalleVenta.setClienteID(textid.getText().toString());
                                    detalleVenta.setClienteRUC(textruc.getText().toString());
                                    detalleVenta.setClienteRS(textnombre.getText().toString());
                                    detalleVenta.setClienteDR(textdireccion.getText().toString());
                                    detalleVenta.setKilometraje(textkilometraje.getText().toString());
                                    detalleVenta.setObservacion(textobservacion.getText().toString());
                                    detalleVenta.setTipoPago("C");

                                    recyclerDetalleVenta.setAdapter(detalleVentaAdapter);
                                    Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                    modalNDespacho.dismiss();

                                    txtplaca.getText().clear();
                                    textid.getText().clear();
                                    textruc.getText().clear();
                                    textnombre.getText().clear();
                                    textdireccion.getText().clear();
                                    textkilometraje.getText().clear();
                                    textobservacion.getText().clear();
                                }
                            }
                        });
                    }
                }
            }
        });


        /** Mostrar Formulario Serafin y Realizar la Operacion */
        modalSerafin = new Dialog(getContext());
        modalSerafin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalSerafin.setContentView(R.layout.fragment_serafin);
        modalSerafin.setCancelable(false);

        btnserafin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (DetalleVenta detalleVenta : detalleVentaList){
                    String mnCara = detalleVenta.getCara().toString();

                    if(mnCara.equals(mCara) ) {

                        modalSerafin.show();

                        btncancelar      = modalSerafin.findViewById(R.id.btncancelarserafin);
                        imprimirserafin  = modalSerafin.findViewById(R.id.btnimprimirserafin);

                        btncancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modalSerafin.dismiss();
                            }
                        });

                        imprimirserafin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

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


                                recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                                Toast.makeText(getContext(), "SE GENERO SERAFIN", Toast.LENGTH_SHORT).show();
                                modalSerafin.dismiss();
                            }
                        });
                    }
                }
            }
        });

        /** Operación de Puntos */
        btnpuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PuntosFragment puntosFragment = new PuntosFragment();
                puntosFragment.show(getActivity().getSupportFragmentManager(), "Puntos");

            }
        });

        /** Listado de Dettalles de Venta  */
        recyclerDetalleVenta = view.findViewById(R.id.recyclerdetalleventa);
        recyclerDetalleVenta.setLayoutManager(new LinearLayoutManager(getContext()));

        /** Listado de Mangueras  */
        recyclerManguera = view.findViewById(R.id.recyclerlado);
        recyclerManguera.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        /** Listado de Caras  */
        recyclerCara = view.findViewById(R.id.recycler);
        recyclerCara.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        /** API Retrofit - Consumiendo */
        findLados(GlobalInfo.getterminalImei10);
        findDetalleVenta(GlobalInfo.getterminalImei10);
        findSetting(GlobalInfo.getterminalCompanyID10);

        return view;
    }

    private void startTimerGR1() {

        timer = new Timer();

        mTimerRunning = true;
        automatiStop.setText("Automático");
        automatiStop.setBackgroundColor(Color.parseColor("#001E8A"));

        insertarDespacho();

        timer.schedule(timerTask,2000,2000);

    }

    private void stoptimertask() {
        timer.cancel();
        timer.purge();
        mTimerRunning = false;
        automatiStop.setText("Stop");
        automatiStop.setBackgroundColor(Color.parseColor("#6c757d"));
    }


    private void insertarDespacho() {

        timerTask = new TimerTask() {

            public void run() {

                /** Consultando ventas pendientes por terminal*/
                OptranProcesar(GlobalInfo.getterminalImei10);

            }

        };

    }


    private void registrarVENTA2023() {

        timerTask = new TimerTask() {

            public void run() {

                /** Consultando ventas pendientes por terminal*/
                findOptran(GlobalInfo.getterminalImei10);

                if (GlobalInfo.getpase10 == true) {

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

                    GlobalInfo.getpase10 = false;
                    String NroLadoVali = "";

                    /** Consultando datos de la lista por nrolado GRID*/
                    for (DetalleVenta detalleVenta : detalleVentaList) {

                        String mnCara = detalleVenta.getCara().toString();

                        if (mnCara.equals(GlobalInfo.getoptranNroLado10)) {

                            mnTipoPago = detalleVenta.getTipoPago().toString();
                            mnImpuesto = detalleVenta.getImpuesto();
                            mnNroPlaca = detalleVenta.getNroPlaca();
                            mnTarjetaPuntos = detalleVenta.getTarjetaPuntos();
                            mnClienteID = detalleVenta.getClienteID();
                            mnClienteRUC = detalleVenta.getClienteRUC();
                            mnClienteRS = detalleVenta.getClienteRS().toString();
                            mnCliernteDR = detalleVenta.getClienteDR().toString();
                            mnTarjND = detalleVenta.getTarjetaND().toString();
                            mnTarjetaCredito = detalleVenta.getTarjetaCredito().toString();
                            mnOperacionREF = detalleVenta.getOperacionREF().toString();
                            mnObservacion = detalleVenta.getObservacion().toString();
                            mnKilometraje = detalleVenta.getKilometraje().toString();
                            mnMontoSoles = detalleVenta.getMontoSoles();
                            mnMtoSaldoCredito = detalleVenta.getMtoSaldoCredito();
                            mnPtosDisponibles = detalleVenta.getPtosDisponible();

                            GlobalInfo.getpase11 = true;

                            if (GlobalInfo.getoptranNroLado10 == null){
                                return;
                            }

                            if (GlobalInfo.getoptranNroLado10.isEmpty()){
                                return;
                            }

                            if (GlobalInfo.getoptranArticuloID10 == null){
                                return;
                            }

                            if (GlobalInfo.getoptranArticuloID10.isEmpty()){
                                return;
                            }


                            /** CALCULAR TOTALES IGV-SUBTOTAL-TOTAL VENTA*/

                            if (GlobalInfo.getoptranSoles10 == null){
                                return;
                            }

                            if (GlobalInfo.getoptranSoles10 == 0){
                                return;
                            }

                            /*

                            if (!mnClienteID.equals(GlobalInfo.getsettingClienteID10)) {

                                findDescuentos(mnClienteID);

                            }

                            mnMtoDescuento1 = 0.00;
                            mnMtoCanje = GlobalInfo.getoptranSoles10;

                            if (GlobalInfo.getDescuentoPase == true) {

                                GlobalInfo.getDescuentoPase = false;

                                if(GlobalInfo.getdescuentoArticuloID10.equals(GlobalInfo.getoptranArticuloID10)){

                                    mnMtoDescuentoUnitario = GlobalInfo.getdescuentoDescuento10;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoDescuento2 = String.format("%.2f",mnMtoDescuento1);

                                } else {
                                    mnMtoDescuento1 = 0.00;
                                }

                                mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;
                                mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);


                            } else {

                                GlobalInfo.getDescuentoPase = false;

                                mnMtoDescuento1 = 0.00;
                                mnMtoCanje = 0.00;

                                mnMtoTotal = GlobalInfo.getoptranSoles10;
                                mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                            }

                            */

                            GlobalInfo.getDescuentoPase = false;

                            mnMtoDescuento1 = 0.00;
                            mnMtoCanje = 0.00;

                            mnMtoTotal = GlobalInfo.getoptranSoles10;
                            mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                            mnMtoSubTotal0 = mnMtoTotal / 1.18;
                            mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                            mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                            mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                            mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                            mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                            /** FIN CALCULAR TOTALES IGV-SUBTOTAL-TOTAL VENTA*/

                            /** Consultando datos del DOCUMENTO*/
                            String mnTipoDocumento = "";

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
                                mnClienteID = "";
                                mnClienteRUC = "";
                                mnClienteRS = "";
                                mnCliernteDR = "";
                                mnNroPlaca = "";
                            }

                            if (mnClienteID.length() == 0 && mnTipoDocumento == "03") {
                                mnClienteID = "11111111";
                                mnClienteRUC = "";
                                mnClienteRS = "CLIENTE VARIOS";
                                mnCliernteDR = "";
                                mnNroPlaca = "000-0000";
                            }

                            if (GlobalInfo.getpase11 == true) {

                                NroLadoVali = GlobalInfo.getoptranNroLado10;

                                findCorrelativo(GlobalInfo.getterminalImei10,mnTipoDocumento, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                                        mnMtoDescuento1, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal,
                                        mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                                        mnTarjND, mnTarjetaPuntos, mnPtosGanados, mnPtosDisponibles,
                                        mnMtoCanje, mnItem, mnFise, mnobservacionDet,
                                        mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnmtoPagoUSD, mnobservacionPag);


                                GlobalInfo.getDescuentoPase = false;
                                GlobalInfo.getdescuentoDescuento10 = 0.00;
                                GlobalInfo.getdescuentoClienteID10 = "";
                                GlobalInfo.getdescuentoTipoID10 = "";
                                GlobalInfo.getdescuentoArticuloID10 = "";

                                GlobalInfo.getcorrelativoFecha   = "";
                                GlobalInfo.getcorrelativoSerie   = "";
                                GlobalInfo.getcorrelativoNumero  = "";

                                GlobalInfo.getoptranTranID10     = 0;
                                GlobalInfo.getoptranNroLado10    = "";
                                GlobalInfo.getoptranManguera10   = "";
                                GlobalInfo.getoptranFechaTran10  = "";
                                GlobalInfo.getoptranArticuloID10 = "";
                                GlobalInfo.getoptranProductoDs10 = "";
                                GlobalInfo.getoptranPrecio10     = 0.00;
                                GlobalInfo.getoptranGalones10    = 0.000;
                                GlobalInfo.getoptranSoles10      = 0.00;
                                GlobalInfo.getoptranOperador10   = "";
                                GlobalInfo.getoptranCliente10    = "";
                                GlobalInfo.getoptranUniMed10     = "";

                            }

                            break;

                        }

                    }

                    /** FIN Consultando datos de la lista por nrolado GRID*/

                    NroLadoVali = "";

                }

            }

        };

        mTimerRunning = true;
        automatiStop.setText("Automático");
        automatiStop.setBackgroundColor(Color.parseColor("#001E8A"));

    }

    private void registrarVenta(){

        timerTask = new TimerTask() {

            public void run() {

                /** Consultando ventas pendientes por terminal*/
                findOptran(GlobalInfo.getterminalImei10);

                if (GlobalInfo.getpase10 == true) {

                    GlobalInfo.getpase10 = false;

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

                    mCara = GlobalInfo.getoptranNroLado10;

                    /** FIN Consultando datos de la lista por nrolado GRID*/

                    String mnTipoVenta = "V";
                    String mnReferencia = "";

                    Double mnPtosGanados = 0.00;

                    Double mnMtoDescuento = 0.00;
                    Double mnMtoCanje = 0.00;

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

                    /** Consultando datos de la lista por nrolado GRID*/
                    for (DetalleVenta detalleVenta : detalleVentaList) {

                        String mnCara = detalleVenta.getCara().toString();

                        if (mnCara.equals(mCara)) {

                            mnTipoPago = detalleVenta.getTipoPago().toString();
                            mnImpuesto = detalleVenta.getImpuesto();
                            mnNroPlaca = detalleVenta.getNroPlaca();
                            mnTarjetaPuntos = detalleVenta.getTarjetaPuntos();
                            mnClienteID = detalleVenta.getClienteID();
                            mnClienteRUC = detalleVenta.getClienteRUC();
                            mnClienteRS = detalleVenta.getClienteRS().toString();
                            mnCliernteDR = detalleVenta.getClienteDR().toString();
                            mnTarjND = detalleVenta.getTarjetaND().toString();
                            mnTarjetaCredito = detalleVenta.getTarjetaCredito().toString();
                            mnOperacionREF = detalleVenta.getOperacionREF().toString();
                            mnObservacion = detalleVenta.getObservacion().toString();
                            mnKilometraje = detalleVenta.getKilometraje().toString();
                            mnMontoSoles = detalleVenta.getMontoSoles();
                            mnMtoSaldoCredito = detalleVenta.getMtoSaldoCredito();
                            mnPtosDisponibles = detalleVenta.getPtosDisponible();

                            GlobalInfo.getpase11 = true;



                            if (GlobalInfo.getoptranArticuloID10 == null){
                                return;
                            }

                            if (GlobalInfo.getoptranArticuloID10.isEmpty()){
                                return;
                            }


                            /** CALCULAR TOTALES IGV-SUBTOTAL-TOTAL VENTA*/

                            if (GlobalInfo.getoptranSoles10 == null){
                                return;
                            }

                            if (GlobalInfo.getoptranSoles10 == 0){
                                return;
                            }

                            mnMtoTotal = GlobalInfo.getoptranSoles10;
                            mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                            mnMtoSubTotal0 = mnMtoTotal / 1.18;
                            mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                            mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                            mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                            mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                            mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

                            /** FIN CALCULAR TOTALES IGV-SUBTOTAL-TOTAL VENTA*/

                            /** Consultando datos del DOCUMENTO*/
                            String mnTipoDocumento = "";

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
                                mnClienteID = "";
                                mnClienteRUC = "";
                                mnClienteRS = "";
                                mnCliernteDR = "";
                                mnNroPlaca = "";
                            }

                            if (mnClienteID.length() == 0 && mnTipoDocumento == "03") {
                                mnClienteID = "11111111";
                                mnClienteRUC = "";
                                mnClienteRS = "CLIENTE VARIOS";
                                mnCliernteDR = "";
                                mnNroPlaca = "000-0000";
                            }

                            if (GlobalInfo.getpase11 == true) {

                                findCorrelativo(GlobalInfo.getterminalImei10,mnTipoDocumento, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                                        mnMtoDescuento, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal,
                                        mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                                        mnTarjND, mnTarjetaPuntos, mnPtosGanados, mnPtosDisponibles,
                                        mnMtoCanje, mnItem, mnFise, mnobservacionDet,
                                        mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnmtoPagoUSD, mnobservacionPag);


                            }


                            GlobalInfo.getoptranTranID10     = 0;
                            GlobalInfo.getoptranNroLado10    = "";
                            GlobalInfo.getoptranManguera10   = "";
                            GlobalInfo.getoptranFechaTran10  = "";
                            GlobalInfo.getoptranArticuloID10 = "";
                            GlobalInfo.getoptranProductoDs10 = "";
                            GlobalInfo.getoptranPrecio10     = 0.00;
                            GlobalInfo.getoptranGalones10    = 0.000;
                            GlobalInfo.getoptranSoles10      = 0.00;
                            GlobalInfo.getoptranOperador10   = "";
                            GlobalInfo.getoptranCliente10    = "";
                            GlobalInfo.getoptranUniMed10     = "";

                            GlobalInfo.getcorrelativoFecha   = "";
                            GlobalInfo.getcorrelativoSerie   = "";
                            GlobalInfo.getcorrelativoNumero  = "";



                            break;
                        }

                    }



                }
                /** FIN Consultando datos OPTRAN*/

            }

        };

        mTimerRunning = true;
        automatiStop.setText("Automático");
        automatiStop.setBackgroundColor(Color.parseColor("#001E8A"));

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
                               String _observacionPag
    ){

        String xtranID = _tranID.toString();

        final VentaCA ventaCA = new VentaCA(_companyID, _tipoDocumento, _serieDocumento, _nroDocumento, _terminalID,
                _clienteID, _clienteRUC, _clienteRZ, _clienteDR, _turno,
                _fechaproceso, _fechadocumento, _fechaAtencion,
                _mtoDescuento, _mtoSubTotal, _mtoImpuesto, _mtoTotal,
                _nroPlaca, _odometro, _tipoventa, _observacion, _referencia,
                _nroTarjetaND, _nroTarjetaPuntos, _ptosGanados, _ptosDisponibles,
                _mtoCanjeado, _userID,
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
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
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
            printama.printTextln("Nro. PLaca   : "+ _NroPlaca, Printama.LEFT);

            switch (_TipoDocumento) {
                case "01" :
                    printama.printTextln("RUC          : "+ _ClienteID , Printama.LEFT);
                    printama.printTextln("Razon Social : "+ _ClienteRZ, Printama.LEFT);
                    break;
                case "03" :

                    if (CVarios.equals(_ClienteID)){

                    }else {
                        printama.printTextln("DNI          : "+ _ClienteID , Printama.LEFT);
                        printama.printTextln("Nombres      : "+ _ClienteRZ, Printama.LEFT);
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
                    printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                    break;
                case "99" :
                    printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                    break;
            }

            printama.feedPaper();
            printama.close();

        }, this::showToast);

    }

    /** API SERVICE - Correlativo */
    private void findCorrelativo(String id, String mnTipoDocumento, String mnClienteID, String mnClienteRUC, String mnClienteRS, String mnCliernteDR,
                                 Double mnMtoDescuento, Double mnMtoSubTotal1, Double mnMtoImpuesto1, Double mnMtoTotal,
                                 String mnNroPlaca, String mnKilometraje, String mnTipoVenta, String mnObservacion, String mnReferencia,
                                 String mnTarjND, String mnTarjetaPuntos, Double mnPtosGanados, Double mnPtosDisponibles,
                                 Double mnMtoCanje, Integer mnItem, Double mnFise, String mnobservacionDet,
                                 Integer mnPagoID, Integer mnTarjetaCreditoID, String mnOperacionREF, Double mnmtoPagoUSD, String mnobservacionPag){

        Call<List<Correlativo>> call = mAPIService.findCorrelativo(id,mnTipoDocumento);

        call.enqueue(new Callback<List<Correlativo>>() {
            @Override
            public void onResponse(Call<List<Correlativo>> call, Response<List<Correlativo>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
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
                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoTotal, mnmtoPagoUSD, mnobservacionPag);

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

                    findDetalleVenta(GlobalInfo.getterminalImei10);

                    GlobalInfo.getpase11 = false;

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Correlativo>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Correlativo - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /** Guardar - Descuento */
    private void findDescuentos(String id){

        Call<List<Descuentos>> call = mAPIService.findDescuentos(id);

        call.enqueue(new Callback<List<Descuentos>>() {
            @Override
            public void onResponse(Call<List<Descuentos>> call, Response<List<Descuentos>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Descuentos> descuentosList = response.body();

                    GlobalInfo.getdescuentoDescuento10 = 0.00;
                    GlobalInfo.getdescuentoClienteID10 = "";
                    GlobalInfo.getdescuentoTipoID10 = "";
                    GlobalInfo.getdescuentoArticuloID10 = "";

                    for(Descuentos descuentos: descuentosList) {
                        GlobalInfo.getdescuentoClienteID10     = String.valueOf(descuentos.getClienteID());
                        GlobalInfo.getdescuentoTipoID10        = String.valueOf(descuentos.getTipoID());
                        GlobalInfo.getdescuentoArticuloID10    = String.valueOf(descuentos.getArticuloID());
                        GlobalInfo.getdescuentoDescuento10     = Double.valueOf(descuentos.getDescuento());
                        GlobalInfo.getdescuentoTipoDescuento10 = String.valueOf(descuentos.getTipoDesc());
                        GlobalInfo.getdescuentoTipoRango10     = String.valueOf(descuentos.getTipoRango());
                        GlobalInfo.getdescuentoRango110        = Double.valueOf(descuentos.getRango1());
                        GlobalInfo.getdescuentoRango210        = Double.valueOf(descuentos.getRango2());
                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Descuentos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Descuento - RED - WIFI", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
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

                        for (DetalleVenta detalleVenta : detalleVentaList) {

                            String mnCara = detalleVenta.getCara().toString();

                            if (mnCara.equals(GlobalInfo.getoptranNroLado10)) {

                                mnTipoPago = detalleVenta.getTipoPago().toString();
                                mnImpuesto = detalleVenta.getImpuesto();
                                mnNroPlaca = detalleVenta.getNroPlaca();
                                mnTarjetaPuntos = detalleVenta.getTarjetaPuntos();
                                mnClienteID = detalleVenta.getClienteID();
                                mnClienteRUC = detalleVenta.getClienteRUC();
                                mnClienteRS = detalleVenta.getClienteRS().toString();
                                mnCliernteDR = detalleVenta.getClienteDR().toString();
                                mnTarjND = detalleVenta.getTarjetaND().toString();
                                mnTarjetaCredito = detalleVenta.getTarjetaCredito().toString();
                                mnOperacionREF = detalleVenta.getOperacionREF().toString();
                                mnObservacion = detalleVenta.getObservacion().toString();
                                mnKilometraje = detalleVenta.getKilometraje().toString();
                                mnMontoSoles = detalleVenta.getMontoSoles();
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
                            mnClienteID = "";
                            mnClienteRUC = "";
                            mnClienteRS = "";
                            mnCliernteDR = "";
                            mnNroPlaca = "";
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

                            if (GlobalInfo.getdescuentoArticuloID10.equals(GlobalInfo.getoptranArticuloID10) && GlobalInfo.getoptranSoles10 >= 70.00) {

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

                        /*
                        if (!mnClienteID.equals(GlobalInfo.getsettingClienteID10)) {

                            findDescuentos(mnClienteID);

                            if (GlobalInfo.getdescuentoDescuento10 > 0) {

                                mnMtoCanje = GlobalInfo.getoptranSoles10;

                                if(GlobalInfo.getdescuentoArticuloID10.equals(GlobalInfo.getoptranArticuloID10)){

                                    mnMtoDescuentoUnitario = GlobalInfo.getdescuentoDescuento10;

                                    mnMtoDescuento0 = mnMtoDescuentoUnitario * GlobalInfo.getoptranGalones10;
                                    mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                    mnMtoDescuento2 = String.format("%.2f",mnMtoDescuento1);

                                    mnMtoTotal = GlobalInfo.getoptranSoles10 - mnMtoDescuento1;

                                } else {
                                    mnMtoDescuento1 = 0.00;
                                    mnMtoTotal = GlobalInfo.getoptranSoles10;
                                }

                                mnMtoTotal2 = String.format("%.2f",mnMtoTotal);

                                mnMtoSubTotal0 = mnMtoTotal / 1.18;
                                mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                                mnMtoSubTotal2 = String.format("%.2f",mnMtoSubTotal1);

                                mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                                mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;
                                mnMtoImpuesto2 = String.format("%.2f",mnMtoImpuesto1);

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

                        */

                        findCorrelativo(GlobalInfo.getterminalImei10,mnTipoDocumento, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                                mnMtoDescuento1, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal,
                                mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                                mnTarjND, mnTarjetaPuntos, mnPtosGanados, mnPtosDisponibles,
                                mnMtoCanje, mnItem, mnFise, mnobservacionDet,
                                mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnmtoPagoUSD, mnobservacionPag);

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
    private void findOptran(String id){

        Call<List<Optran>> call = mAPIService.findOptran(id);

        call.enqueue(new Callback<List<Optran>>() {
            @Override
            public void onResponse(Call<List<Optran>> call, Response<List<Optran>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    for(Optran optran: optranList) {
                        GlobalInfo.getpase10 = true;
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

    /** API SERVICE - Setting */
    private void findSetting(Integer id){

        Call<List<Setting>> call = mAPIService.findSetting(id);

        call.enqueue(new Callback<List<Setting>>() {
            @Override
            public void onResponse(Call<List<Setting>> call, Response<List<Setting>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Setting> settingList = response.body();
                    for(Setting setting: settingList) {
                        GlobalInfo.getsettingCompanyId10       = Integer.valueOf(setting.getCompanyID());
                        GlobalInfo.getsettingTituloApp10       = String.valueOf(setting.getTituloApp());
                        GlobalInfo.getsettingFuelName10        = String.valueOf(setting.getFuel_Name());
                        GlobalInfo.getsettingFuelGrupoID10     = String.valueOf(setting.getFuel_GrupoID());
                        GlobalInfo.getsettingFuelLados10       = Integer.valueOf(setting.getFuel_Lados());
                        GlobalInfo.getsettingFuelMontoMinimo10 = Double.valueOf(setting.getFuel_Monto_Minimo());
                        GlobalInfo.getsettingImpuestoID110     = Integer.valueOf(setting.getImpuestoID1());
                        GlobalInfo.getsettingImpuestoValor110  = Integer.valueOf(setting.getImpuesto_Valor1());
                        GlobalInfo.getsettingImpuestoID210     = Integer.valueOf(setting.getImpuestoID2());
                        GlobalInfo.getsettingImpuestoValor210  = Integer.valueOf(setting.getImpuesto_Valor2());
                        GlobalInfo.getsettingMonedaID10        = String.valueOf(setting.getMonedaID());
                        GlobalInfo.getsettingMonedaValor10     = String.valueOf(setting.getMoneda_Valor());
                        GlobalInfo.getsettingClienteID10       = String.valueOf(setting.getClienteID());
                        GlobalInfo.getsettingClienteRZ10       = String.valueOf(setting.getClienteRZ());
                        GlobalInfo.getsettingNroPlaca10        = String.valueOf(setting.getNroplaca());
                        GlobalInfo.getsettingDNIMontoMinimo10  = Double.valueOf(setting.getDnI_Monto_Minimo());
                        GlobalInfo.getsettingtimerAppVenta10   = String.valueOf(setting.getTimerAppVenta());
                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Setting>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Setting - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Buscar Cliente DNI */
    private  void findClienteDNI(String id){

        Call<List<Cliente>> call = mAPIService.findClienteDNI(id);

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                try {

                    if(!response.isSuccessful()){
                        if (clienteList == null || clienteList.isEmpty()) {
                            if (textdni.length() < 8){
                                alertdni.setError("* El DNI debe tener 8 dígitos");
                                return;
                            }else{
                                alertdni.setErrorEnabled(false);
                            }
                            return;
                        }
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }


                    List<Cliente> clienteList = response.body();



                    Cliente cliente = clienteList.get(0);

                    GlobalInfo.getclienteId10  = String.valueOf(cliente.getClienteID());
                    GlobalInfo.getclienteRUC10 = String.valueOf(cliente.getClienteRUC());
                    GlobalInfo.getclienteRZ10  = String.valueOf(cliente.getClienteRZ());
                    GlobalInfo.getclienteDR10  = String.valueOf(cliente.getClienteDR());

                    textnombre.setText(GlobalInfo.getclienteRZ10);
                    textdireccion.setText(GlobalInfo.getclienteDR10);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente DNI - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API SERVICE - Buscar Cliente RUC */
    private  void findClienteRUC(String id){

        Call<List<Cliente>> call = mAPIService.findClienteRUC(id);

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                try {

                    if(!response.isSuccessful()){
                        if (clienteList == null || clienteList.isEmpty()) {
                            if (textruc.length() < 11){
                                alertruc.setError("* El RUC debe tener 11 dígitos");
                            }else{
                                alertruc.setErrorEnabled(false);
                            }
                            return;
                        }
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Cliente> clienteList = response.body();



                    Cliente cliente = clienteList.get(0);

                    GlobalInfo.getclienteId10  = String.valueOf(cliente.getClienteID());
                    GlobalInfo.getclienteRUC10 = String.valueOf(cliente.getClienteRUC());
                    GlobalInfo.getclienteRZ10  = String.valueOf(cliente.getClienteRZ());
                    GlobalInfo.getclienteDR10  = String.valueOf(cliente.getClienteDR());

                    textrazsocial.setText(GlobalInfo.getclienteRZ10);
                    textdireccion.setText(GlobalInfo.getclienteDR10);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente RUC - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API SERVICE - Cliente */
    private  void findCliente(String id,String tipodoc){

        Call<List<Cliente>> call = mAPIService.findCliente(id);

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Cliente> clienteList = response.body();

                    if (clienteList == null || clienteList.isEmpty()) {

                        if (tipodoc == "01"){
                            if (textruc.length() < 11){
                                alertruc.setError("* El RUC debe tener 11 dígitos");
                            }else{
                                alertruc.setError("* No se encontró ningún RUC");
                            }
                        }else if (tipodoc == "03"){
                            if (textdni.length() < 8){
                                alertdni.setError("* El DNI debe tener 8 dígitos");
                            }else{
                                alertdni.setError("* No se encontró ningún DNI");
                            }
                        }else if (tipodoc == "99") {
                            alertid.setError("* No se encontró ningún ID");
                        }

                        return;
                    }

                    Cliente cliente = clienteList.get(0);

                    GlobalInfo.getclienteId10  = String.valueOf(cliente.getClienteID());
                    GlobalInfo.getclienteRUC10 = String.valueOf(cliente.getClienteRUC());
                    GlobalInfo.getclienteRZ10  = String.valueOf(cliente.getClienteRZ());
                    GlobalInfo.getclienteDR10  = String.valueOf(cliente.getClienteDR());

                    if (tipodoc == "01"){
                        textrazsocial.setText(GlobalInfo.getclienteRZ10);
                        textdireccion.setText(GlobalInfo.getclienteDR10);
                    }else if (tipodoc == "03"){
                        textnombre.setText(GlobalInfo.getclienteRZ10);
                        textdireccion.setText(GlobalInfo.getclienteDR10);
                    }else if (tipodoc == "99"){
                        textruc.setText( GlobalInfo.getclienteRUC10);
                        textnombre.setText(GlobalInfo.getclienteRZ10);
                        textdireccion.setText(GlobalInfo.getclienteDR10);
                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API SERVICE - Buscar Placa */
    private void findPlaca(String id, String tipodoc){

        Call<List<Placa>> call = mAPIService.findPlaca(id);

        call.enqueue(new Callback<List<Placa>>() {
            @Override
            public void onResponse(Call<List<Placa>> call, Response<List<Placa>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Placa> placaList = response.body();

                    if (placaList == null || placaList.isEmpty()) {
                        alertplaca.setError("* No se encontró ningúna Placa");
                        return;
                    }

                    Placa placa = placaList.get(0);

                    GlobalInfo.getNroPlaca10 = String.valueOf(placa.getNroPlaca());
                    GlobalInfo.getplacaClienteID10 = String.valueOf(placa.getClienteID());
                    GlobalInfo.getplacaClienteRZ10 = String.valueOf(placa.getClienteRZ());
                    GlobalInfo.getplacaClienteDR10 = String.valueOf(placa.getClienteDR());

                    if (tipodoc == "01"){
                        textruc.setText( GlobalInfo.getplacaClienteID10);
                        textrazsocial.setText(GlobalInfo.getplacaClienteRZ10);
                        textdireccion.setText(GlobalInfo.getplacaClienteDR10);
                    }else if (tipodoc == "03"){
                        textdni.setText(GlobalInfo.getplacaClienteID10);
                        textnombre.setText(GlobalInfo.getplacaClienteRZ10);
                        textdireccion.setText(GlobalInfo.getplacaClienteDR10);
                    }else if (tipodoc == "99"){
                        textid.setText( GlobalInfo.getplacaClienteID10);
                        textnombre.setText(GlobalInfo.getplacaClienteRZ10);
                        textdireccion.setText(GlobalInfo.getplacaClienteDR10);
                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Placa>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Placa - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Lados */
    private void findLados(String id) {

        Call<List<Lados>> call = mAPIService.findLados(id);

        call.enqueue(new Callback<List<Lados>>() {
            @Override
            public void onResponse(Call<List<Lados>> call, Response<List<Lados>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Lados> ladosList = response.body();

                    caraAdapter = new CaraAdapter(ladosList, getContext(), new CaraAdapter.OnItemClickListener() {
                        @Override
                        public int onItemClick(Lados item) {

                            btnlibre.setEnabled(false);
                            btnsoles.setEnabled(false);
                            btngalones.setEnabled(false);
                            btnboleta.setEnabled(false);
                            btnfactura.setEnabled(false);
                            btnnotadespacho.setEnabled(false);
                            btnserafin.setEnabled(false);

                            GlobalInfo.getCara10 = item.getNroLado();
                            mCara = item.getNroLado();
                            findPico(GlobalInfo.getCara10);

                            return 0;

                        }
                    });

                    recyclerCara.setAdapter(caraAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lados>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cara - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Pico o Manguera */
    private void findPico(String id){

        Call<List<Picos>> call = mAPIService.findPico(id);

        call.enqueue(new Callback<List<Picos>>() {
            @Override
            public void onResponse(Call<List<Picos>> call, Response<List<Picos>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Picos> picosList = response.body();
                    mangueraAdapter = new MangueraAdapter(picosList, getContext(), new MangueraAdapter.OnItemClickListener(){
                        @Override
                        public void onItemClick(Picos item) {

                            btnlibre.setEnabled(true);
                            btnsoles.setEnabled(true);
                            btngalones.setEnabled(true);
                            btnboleta.setEnabled(true);
                            btnfactura.setEnabled(true);
                            btnserafin.setEnabled(true);

                            GlobalInfo.getPistola10 = item.getMangueraID();

                        }
                    });
                    recyclerManguera.setAdapter(mangueraAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Picos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Pico - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Card Detalle de Ventas */
    private void findDetalleVenta(String id){

        Call<List<DetalleVenta>> call = mAPIService.findDetalleVenta(id);

        call.enqueue(new Callback<List<DetalleVenta>>() {
            @Override
            public void onResponse(Call<List<DetalleVenta>> call, Response<List<DetalleVenta>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    detalleVentaList = response.body();

                    detalleVentaAdapter = new DetalleVentaAdapter(detalleVentaList, getContext());
                    recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DetalleVenta>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Detalle Venta - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API SERVICE - Setting Task */
    private void findSettingTask(String id){

        Call<List<SettingTask>> call = mAPIService.findSettingTask(id);
        call.enqueue(new Callback<List<SettingTask>>() {
            @Override
            public void onResponse(Call<List<SettingTask>> call, Response<List<SettingTask>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<SettingTask> settingTaskList = response.body();

                    for(SettingTask settingTask: settingTaskList) {

                        GlobalInfo.getsettingtaskID10     = settingTask.getTaskID();
                        GlobalInfo.getsettingtaskName10   = settingTask.getName();
                        GlobalInfo.getsettingtaskIsTask10 = Boolean.valueOf(settingTask.getTask());

                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SettingTask>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Setting Task - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /** Spinner de Tipo de Tarjetas */
    private void getCard(){
        Call<List<Card>> call = mAPIService.getCard();

        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Card> cardlist = response.body();

                    Resources res = getResources();
                    CardAdapter card = new CardAdapter(getContext(), R.layout.item, (ArrayList<Card>) cardlist, res);
                    dropStatus.setAdapter(card);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Listado de Clientes con DNI */
    private void getClienteDNI(){
        Call<List<Cliente>> call = mAPIService.getClienteDNI();

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Cliente> clienteList = response.body();

                    clienteAdapter = new ClienteAdapter(clienteList, getContext(), new ClienteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Cliente item) {
                            String SelectDNI       = item.getClienteID();
                            String SelectNombre    = item.getClienteRZ();
                            String SelectDireccion = item.getClienteDR();

                            textdni.setText(SelectDNI);
                            textnombre.setText(SelectNombre);
                            textdireccion.setText(SelectDireccion);
                            Toast.makeText(getContext(), "Se seleccionó : " + SelectDNI , Toast.LENGTH_SHORT).show();
                            modalCliente.dismiss();

                            buscadorUser.setQuery("", false);
                        }
                    });

                    recyclerCliente.setAdapter(clienteAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Listado de Clientes con DNI */
    private void getClienteRUC(){
        Call<List<Cliente>> call = mAPIService.getClienteRUC();

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Cliente> clienteList = response.body();

                    clienteAdapter = new ClienteAdapter(clienteList, getContext(), new ClienteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Cliente item) {
                            String SelectRUC       = item.getClienteID();
                            String SelectRZSocial    = item.getClienteRZ();
                            String SelectDireccion = item.getClienteDR();

                            textruc.setText(SelectRUC);
                            textrazsocial.setText(SelectRZSocial);
                            textdireccion.setText(SelectDireccion);
                            Toast.makeText(getContext(), "Se seleccionó : " + SelectRUC , Toast.LENGTH_SHORT).show();
                            modalClienteRUC.dismiss();

                            buscadorUser.setQuery("", false);
                        }
                    });

                    recyclerCliente.setAdapter(clienteAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Alerta de Conexión de Bluetooth */
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

}