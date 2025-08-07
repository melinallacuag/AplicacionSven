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

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.EgresosAdapter;
import com.anggastudio.sample.Adapter.IngresosAdapter;
import com.anggastudio.sample.Adapter.MonedaAdapter;
import com.anggastudio.sample.Adapter.TEgresoAdapter;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.Egreso;
import com.anggastudio.sample.WebApiSVEN.Models.Ingresos;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.MontoEfectivo;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Models.SettingMoneda;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTEgreso;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BovedasFragment extends Fragment {

    private APIService mAPIService;
    private NFCUtil nfcUtil;

    Spinner SpinnerMoneda,SpinnerTipoEgreso;
    TextInputLayout alertImportes,alertObservacion,alertSelectMoneda,alertTipoEgreso,alertImporteDeposito,alertEfectivoDisponible;
    TextInputEditText inputImportes,inputEfectivoDisponible,inputImporteDeposito,inputObservacion;
    Button btnCancelarBoveda,btnAgregarBoveda,btnCancelarRImpresion,btnRImpresion,btnAnular,btnAnularI,btnCerrarCaja,btnAgregarIngresoBoveda;

    TEgresoAdapter tEgresoAdapter;
    SettingTEgreso settingTEgreso;
    MonedaAdapter monedaAdapter;
    SettingMoneda settingMoneda;

    List<MontoEfectivo> montoEfectivoList;
    List<Egreso> getlistegresoList10;
    List<Ingresos>  getlistingresosList10;
    RecyclerView recyclerListaEgresos,recyclerListaIngresos;

    IngresosAdapter ingresosAdapter;
    EgresosAdapter egresosAdapter;
    TextView MTotalEgreso,MTotalIngreso,AperturaCaja,VentasEfectivo,SalidaEfectivo,DineroCaja;
    Dialog modalOpciones, modalCaja,modalConfirmacion,modalAnularE,modalConfirmacionIngreso,modalAnularIngreso;

    TextView campo_correlativo;

    Double importeD,efectivoD,importeDIngreso,efectivoDIngreso;
    ImageButton caja;

    LinearLayout listaEgreso,listaIngreso,textEgreso,textIngreso,btnGuardarEgresos,btnCancelarEgresos,btnAnularEgresos,btnCancelarAEgresos,btnGuardarIngreso,btnCancelarIngreso,btnCancelarAIngreso,btnAnularIngreso;

    RadioGroup radioTipoEI;
    RadioButton radioEgresos,radioIngresos,radioTipoEgresoIngresa;
    CardView cardengreso,cardingreso;

    private double sumaTotal = 0.0;
    private double sumaTotalIngresos = 0.0;
    private double efectivoDisponible = 0.0;

    private int idGenerado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcUtil =  new NFCUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bovedas, container, false);

        mAPIService = GlobalInfo.getAPIService();

        alertSelectMoneda       = view.findViewById(R.id.alertSelectMoneda);
        SpinnerMoneda           = view.findViewById(R.id.SpinnerMoneda);
        alertTipoEgreso         = view.findViewById(R.id.alertTipoEgreso);
        SpinnerTipoEgreso       = view.findViewById(R.id.SpinnerTipoEgreso);
        inputEfectivoDisponible = view.findViewById(R.id.inputEfectivoDisponible);
        inputImporteDeposito    = view.findViewById(R.id.inputImporteDeposito);
        inputObservacion        = view.findViewById(R.id.inputObservacion);
        alertImporteDeposito    = view.findViewById(R.id.alertImporteDeposito);
        alertEfectivoDisponible = view.findViewById(R.id.alertEfectivoDisponible);
        btnCancelarBoveda       = view.findViewById(R.id.btnCancelarBoveda);
        btnAgregarBoveda        = view.findViewById(R.id.btnAgregarBoveda);
        btnAgregarIngresoBoveda = view.findViewById(R.id.btnAgregarIngresoBoveda);
        MTotalEgreso            = view.findViewById(R.id.MTotalEgreso);
        MTotalIngreso           = view.findViewById(R.id.MTotalIngreso);
        caja                    = view.findViewById(R.id.caja);
        radioTipoEI             = view.findViewById(R.id.radioTipoEI);
        radioEgresos            = view.findViewById(R.id.radioEgresos);
        radioIngresos           = view.findViewById(R.id.radioIngresos);
        textEgreso              = view.findViewById(R.id.textEgreso);
        textIngreso             = view.findViewById(R.id.textIngreso);
        cardengreso             = view.findViewById(R.id.cardengreso);
        cardingreso             = view.findViewById(R.id.cardingreso);
        listaEgreso             = view.findViewById(R.id.listaEgreso);
        listaIngreso            = view.findViewById(R.id.listaIngreso);
        alertObservacion        = view.findViewById(R.id.alertObservacion);
        alertImportes           = view.findViewById(R.id.alertImportes);
        inputImportes           = view.findViewById(R.id.inputImportes);

        alertImportes.setVisibility(View.GONE);

        /**
         * @TipoEgresoIngreso
         */
        radioTipoEI.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                radioTipoEgresoIngresa  = view.findViewById(checkedId);

                if (checkedId == radioEgresos.getId()){
                    alertTipoEgreso.setVisibility(View.VISIBLE);
                    btnAgregarBoveda.setVisibility(View.VISIBLE);
                    btnAgregarIngresoBoveda.setVisibility(View.GONE);
                    alertEfectivoDisponible.setVisibility(View.VISIBLE);
                    alertImporteDeposito.setVisibility(View.VISIBLE);
                    alertImportes.setVisibility(View.GONE);
                    textEgreso.setVisibility(View.VISIBLE);
                    textIngreso.setVisibility(View.GONE);
                    cardengreso.setVisibility(View.VISIBLE);
                    cardingreso.setVisibility(View.GONE);
                    listaEgreso.setVisibility(View.VISIBLE);
                    listaIngreso.setVisibility(View.GONE);

                    inputImporteDeposito.getText().clear();
                    inputObservacion.getText().clear();

                    alertImporteDeposito.setErrorEnabled(false);
                } else if (checkedId == radioIngresos.getId()){
                    alertTipoEgreso.setVisibility(View.GONE);
                    btnAgregarBoveda.setVisibility(View.GONE);
                    btnAgregarIngresoBoveda.setVisibility(View.VISIBLE);
                    alertEfectivoDisponible.setVisibility(View.GONE);
                    alertImporteDeposito.setVisibility(View.GONE);
                    alertImportes.setVisibility(View.VISIBLE);
                    textEgreso.setVisibility(View.GONE);
                    textIngreso.setVisibility(View.VISIBLE);
                    cardengreso.setVisibility(View.GONE);
                    cardingreso.setVisibility(View.VISIBLE);
                    listaEgreso.setVisibility(View.GONE);
                    listaIngreso.setVisibility(View.VISIBLE);

                    inputImporteDeposito.getText().clear();
                    inputObservacion.getText().clear();

                    alertImporteDeposito.setErrorEnabled(false);
                }
            }
        });

        /**
         * @Modal:VistaDetalledaEfectivo
         */
        modalCaja = new Dialog(getContext());
        modalCaja.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalCaja.setContentView(R.layout.modalcaja);
        modalCaja.setCancelable(false);

        caja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalCaja.show();

                btnCerrarCaja   = modalCaja.findViewById(R.id.btnCerrarCaja);
                AperturaCaja    = modalCaja.findViewById(R.id.AperturaCaja);
                VentasEfectivo  = modalCaja.findViewById(R.id.VentasEfectivo);
                SalidaEfectivo  = modalCaja.findViewById(R.id.SalidaEfectivo);
                DineroCaja      = modalCaja.findViewById(R.id.DineroCaja);

                AperturaCaja.setText(String.format("%.2f", sumaTotalIngresos));
                VentasEfectivo.setText(String.format("%.2f", efectivoDisponible));
                SalidaEfectivo.setText("-" + String.format("%.2f", sumaTotal));

                double DineroEnCaja = sumaTotalIngresos + efectivoDisponible - sumaTotal;

                DineroCaja.setText(String.format("%.2f", DineroEnCaja));

                btnCerrarCaja.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalCaja.dismiss();
                    }
                });
            }
        });

        /**
         * @Seleccionar:TipoEgreso
         */
        ArrayList<SettingTEgreso> listaTEgresoFiltrada = new ArrayList<>();
        for (SettingTEgreso item : GlobalInfo.getegresoList10) {
            if (item.getStatus() != null && item.getStatus()) {
                listaTEgresoFiltrada.add(item);
            }
        }

        Resources resT = getResources();
        tEgresoAdapter = new TEgresoAdapter(getContext(), R.layout.item_tegreso, listaTEgresoFiltrada, resT);

        SpinnerTipoEgreso.setAdapter(tEgresoAdapter);
        SpinnerTipoEgreso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settingTEgreso = (SettingTEgreso) SpinnerTipoEgreso.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /**
         * @Seleccionar:OpciónMoneda
         */
        ArrayList<SettingMoneda> listaMonedaFiltrada = new ArrayList<>();
        for (SettingMoneda item : GlobalInfo.getmonedaList10) {
            if (item.getStatus() != null && item.getStatus()) {
                listaMonedaFiltrada.add(item);
            }
        }

        Resources res = getResources();
        monedaAdapter = new MonedaAdapter(getContext(), R.layout.item_moneda, listaMonedaFiltrada, res);

        SpinnerMoneda.setAdapter(monedaAdapter);
        SpinnerMoneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        settingMoneda = (SettingMoneda) SpinnerMoneda.getSelectedItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        /**
         * @Agregar_Ingreso
         */

        inputImporteDeposito.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ImporteDepositoIngreso = s.toString();
                String ImporteDeposito =  inputEfectivoDisponible.getText().toString();

                if (ImporteDepositoIngreso.isEmpty()) {
                    alertImporteDeposito.setError("* Ingresar Importe a Depositar");
                    return;
                } else {
                    alertImporteDeposito.setError(null);
                    alertImporteDeposito.setErrorEnabled(false);
                }

                try {
                    Double importeDIngreso = Double.valueOf(ImporteDepositoIngreso);
                    Double efectivoD = Double.valueOf(ImporteDeposito);

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    if(importeDIngreso > efectivoD){
                        alertImporteDeposito.setError("* El importe no puede ser mayor al efectivo disponible");
                        return;
                    }else {
                        alertImporteDeposito.setError(null);
                        alertImporteDeposito.setErrorEnabled(false);
                    }

                    if(importeDIngreso != Double.parseDouble(decimalFormat.format(importeDIngreso))){
                        alertImporteDeposito.setError("* Por favor ingrese un valor con dos decimales solamente");
                        return;
                    }else {
                        alertImporteDeposito.setError(null);
                        alertImporteDeposito.setErrorEnabled(false);
                    }

                } catch (NumberFormatException e) {
                    alertImporteDeposito.setError("* Formato numérico no válido");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        inputObservacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ObservacionIngreso = s.toString();

                if (!ObservacionIngreso.isEmpty() && !ObservacionIngreso.matches("^[A-Za-z0-9 ]+$"))  {
                    alertObservacion.setError("* Solo se permiten letras y números");
                    return;
                } else {
                    alertObservacion.setError(null);
                    alertObservacion.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        inputImportes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ImportesIngreso = s.toString();

                if (ImportesIngreso.isEmpty()) {
                    alertImportes.setError("* Ingresar Importe a Depositar");
                    return;
                } else {
                    alertImportes.setError(null);
                    alertImportes.setErrorEnabled(false);
                }

                try {
                    Double importeDIngreso = Double.valueOf(ImportesIngreso);

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    if(importeDIngreso != Double.parseDouble(decimalFormat.format(importeDIngreso))){
                        alertImportes.setError("* Por favor ingrese un valor con dos decimales solamente");
                        return;
                    }else {
                        alertImportes.setError(null);
                        alertImportes.setErrorEnabled(false);
                    }



                } catch (NumberFormatException e) {
                    alertImportes.setError("* Formato numérico no válido");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAgregarIngresoBoveda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ImportesIngreso =  inputImportes.getText().toString();
                String ObservacionIngreso =  inputObservacion.getText().toString();


                if (ImportesIngreso.isEmpty()) {
                    alertImportes.setError("* Ingresar Importe a Depositar");
                    return;
                } else {
                    alertImportes.setError(null);
                    alertImportes.setErrorEnabled(false);
                }

                importeDIngreso = Double.valueOf(ImportesIngreso);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                if(importeDIngreso != Double.parseDouble(decimalFormat.format(importeDIngreso))){
                    alertImportes.setError("* Por favor ingrese un valor con dos decimales solamente");
                    return;
                }

                modalConfirmacionIngreso = new Dialog(getContext());
                modalConfirmacionIngreso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalConfirmacionIngreso.setContentView(R.layout.modal_confirmacion_ingreso);
                modalConfirmacionIngreso.setCancelable(false);

                btnGuardarIngreso  = modalConfirmacionIngreso.findViewById(R.id.btnGuardarIngreso);
                btnCancelarIngreso = modalConfirmacionIngreso.findViewById(R.id.btnCancelarIngreso);

                modalConfirmacionIngreso.show();

                btnCancelarIngreso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalConfirmacionIngreso.dismiss();
                    }
                });

                btnGuardarIngreso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        String fechaFormateada = "";

                        try {
                            Date fecha = formatoEntrada.parse(GlobalInfo.getterminalFecha10);
                            fechaFormateada = formatoSalida.format(fecha);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        guardarIngresos(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10,fechaFormateada,
                                settingTEgreso.getEgresoID(),importeDIngreso,ObservacionIngreso,
                                GlobalInfo.getuserID10,GlobalInfo.getterminalImei10);

                        modalConfirmacionIngreso.dismiss();

                        inputImportes.getText().clear();
                        inputObservacion.getText().clear();
                        alertImportes.setError(null);
                        alertImportes.setErrorEnabled(false);
                    }
                });
            }
        });

        /**
         * @Caneclar_Ingreso
         */
        btnCancelarBoveda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        inputImporteDeposito.getText().clear();
                        inputObservacion.getText().clear();

                        alertImporteDeposito.setErrorEnabled(false);
                    }
                });

        /**
         * @Agregar_Egreso
         */
        btnAgregarBoveda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String EfectivoDisponible = inputEfectivoDisponible.getText().toString();
                String ImporteDeposito =  inputImporteDeposito.getText().toString();
                String Observacion =  inputObservacion.getText().toString();


                if (ImporteDeposito.isEmpty()) {
                    alertImporteDeposito.setError("* Ingresar Importe a Depositar");
                    return;
                } else {
                    alertImporteDeposito.setError(null);
                    alertImporteDeposito.setErrorEnabled(false);
                }

                efectivoD = Double.valueOf(EfectivoDisponible);
                importeD = Double.valueOf(ImporteDeposito);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                if(importeD != Double.parseDouble(decimalFormat.format(importeD))){
                    alertImporteDeposito.setError("* Por favor ingrese un valor con dos decimales solamente");
                    return;
                }
                if(importeD > efectivoD){
                    alertImporteDeposito.setError("* El importe no puede ser mayor al efectivo disponible");
                    return;
                }

                modalConfirmacion = new Dialog(getContext());
                modalConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalConfirmacion.setContentView(R.layout.modal_confirmacion);
                modalConfirmacion.setCancelable(false);

                btnGuardarEgresos  = modalConfirmacion.findViewById(R.id.btnGuardarEgresos);
                btnCancelarEgresos = modalConfirmacion.findViewById(R.id.btnCancelarEgresos);

                modalConfirmacion.show();

                btnCancelarEgresos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalConfirmacion.dismiss();
                    }
                });

                alertImporteDeposito.setError(null);
                alertImporteDeposito.setErrorEnabled(false);

                btnGuardarEgresos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        String fechaFormateada = "";

                        try {
                            Date fecha = formatoEntrada.parse(GlobalInfo.getterminalFecha10);
                            fechaFormateada = formatoSalida.format(fecha);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        guardarEgresos(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10,fechaFormateada,
                                settingMoneda.getMonedaID(),settingTEgreso.getEgresoID(),importeD,Observacion,
                                GlobalInfo.getuserID10,GlobalInfo.getterminalImei10);

                        modalConfirmacion.dismiss();

                        inputImporteDeposito.getText().clear();
                        inputObservacion.getText().clear();
                        alertImporteDeposito.setError(null);
                        alertImporteDeposito.setErrorEnabled(false);
                    }
                });
            }
        });

        /**
         * @MontoEfectivo
         */
        findMontoEfectivo(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        /**
         * @Listado:Egreso
         */
        findListadoEgresos(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);
        recyclerListaEgresos = view.findViewById(R.id.recyclerListaEgresos);
        recyclerListaEgresos.setLayoutManager(new LinearLayoutManager(getContext()));

        /**
         * @Listado:Ingreso
         */
        findListadoIngresos(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);
        recyclerListaIngresos = view.findViewById(R.id.recyclerListaIngresos);
        recyclerListaIngresos.setLayoutManager(new LinearLayoutManager(getContext()));

        /**
         * @Mostrar:Opciones
         */
        modalOpciones = new Dialog(getContext());
        modalOpciones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalOpciones.setContentView(R.layout.modal_reimprimir);
        modalOpciones.setCancelable(false);

        return view;
    }

    /**
     * @MONTO_EFECTIVO
     */
    private void findMontoEfectivo(String id,Integer turno){

        Call<List<MontoEfectivo>> call = mAPIService.findMontoEfectivo(id,turno);

        call.enqueue(new Callback<List<MontoEfectivo>>() {
            @Override
            public void onResponse(Call<List<MontoEfectivo>> call, Response<List<MontoEfectivo>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error MontoEfectivo: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    montoEfectivoList = response.body();

                    if (montoEfectivoList != null && !montoEfectivoList.isEmpty()) {

                        efectivoDisponible = montoEfectivoList.get(0).getSoles();
                        inputEfectivoDisponible.setText(String.format("%.2f", efectivoDisponible));
                        calcularEfectivoRestante();
                    } else {
                        efectivoDisponible = 0.0;
                        inputEfectivoDisponible.setText("0.00");
                        calcularEfectivoRestante(); //
                    }

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MontoEfectivo>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE MontoEfectivo - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * @LISTADO:Egresos
     */
    private void findListadoEgresos(String id,Integer turno){

        Call<List<Egreso>> call = mAPIService.findListadoEgresos(id,turno);

        call.enqueue(new Callback<List<Egreso>>() {
            @Override
            public void onResponse(Call<List<Egreso>> call, Response<List<Egreso>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Egresos: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getlistegresoList10 = response.body();

                    sumaTotal = 0;

                    for (Egreso egreso : getlistegresoList10) {
                        if ("NO".equalsIgnoreCase(egreso.getAnulado())) {
                            sumaTotal += egreso.getMtoTotal();
                        }
                    }

                    MTotalEgreso.setText(String.format("%.2f", sumaTotal));

                    calcularEfectivoRestante();

                    egresosAdapter = new EgresosAdapter(getlistegresoList10, getContext(), new EgresosAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Egreso item) {

                            moveToDescriptionE(item);

                            if (GlobalInfo.getegresoAnulado10 .equals("NO")) {

                                modalOpciones.show();

                                btnCancelarRImpresion = modalOpciones.findViewById(R.id.btnCancelarRImpresion);
                                btnRImpresion         = modalOpciones.findViewById(R.id.btnRImpresion);
                                btnAnular             = modalOpciones.findViewById(R.id.btnAnular);
                                campo_correlativo     = modalOpciones.findViewById(R.id.campo_correlativo);

                                campo_correlativo.setText("Nro. Despósito: " + GlobalInfo.getegresoID10);


                                btnCancelarRImpresion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        modalOpciones.dismiss();
                                    }
                                });

                                btnRImpresion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ReimpresionEgreso(GlobalInfo.getTipoPapel10,GlobalInfo.getegresoID10);
                                        modalOpciones.dismiss();
                                    }
                                });

                                btnAnular.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        modalAnularE = new Dialog(getContext());
                                        modalAnularE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        modalAnularE.setContentView(R.layout.modal_anulacion_e);
                                        modalAnularE.setCancelable(false);

                                        modalAnularE.show();

                                        btnAnularEgresos    = modalAnularE.findViewById(R.id.btnAnularEgresos);
                                        btnCancelarAEgresos = modalAnularE.findViewById(R.id.btnCancelarAEgresos);

                                        btnAnularEgresos.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AnularEgreso(GlobalInfo.getegresoID10);
                                                modalAnularE.dismiss();
                                                modalOpciones.dismiss();
                                            }
                                        });

                                        btnCancelarAEgresos.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                modalAnularE.dismiss();
                                                modalOpciones.dismiss();
                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "Egreso se encuntra anulado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    egresosAdapter.notifyDataSetChanged();

                    recyclerListaEgresos.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerListaEgresos.setAdapter(egresosAdapter);


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Egreso>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Egresos - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * @LISTADO:Ingresos
     */
    private void findListadoIngresos(String id,Integer turno){

        Call<List<Ingresos>> call = mAPIService.findListadoIngresos(id,turno);

        call.enqueue(new Callback<List<Ingresos>>() {
            @Override
            public void onResponse(Call<List<Ingresos>> call, Response<List<Ingresos>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Egresos: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getlistingresosList10 = response.body();

                    sumaTotalIngresos = 0;

                    for (Ingresos ingresos : getlistingresosList10) {
                        if ("NO".equalsIgnoreCase(ingresos.getAnulado())) {
                            sumaTotalIngresos += ingresos.getMtoTotal();
                        }
                    }

                    MTotalIngreso.setText(String.format("%.2f", sumaTotalIngresos));

                    ingresosAdapter = new IngresosAdapter(getlistingresosList10, getContext(), new IngresosAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Ingresos item) {

                            moveToDescriptionI(item);

                            if (GlobalInfo.getegresoAnulado10 .equals("NO")) {

                                modalOpciones.show();

                                btnCancelarRImpresion = modalOpciones.findViewById(R.id.btnCancelarRImpresion);
                                btnRImpresion         = modalOpciones.findViewById(R.id.btnRImpresion);
                                btnAnularI            = modalOpciones.findViewById(R.id.btnAnular);
                                campo_correlativo     = modalOpciones.findViewById(R.id.campo_correlativo);

                                campo_correlativo.setText("Nro. Ingreso: " + GlobalInfo.getegresoID10);

                                btnCancelarRImpresion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        modalOpciones.dismiss();
                                    }
                                });

                                btnRImpresion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ReimpresionIngreso(GlobalInfo.getTipoPapel10,GlobalInfo.getegresoID10);
                                        modalOpciones.dismiss();
                                    }
                                });

                                btnAnularI.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        modalAnularIngreso = new Dialog(getContext());
                                        modalAnularIngreso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        modalAnularIngreso.setContentView(R.layout.modal_anulacion_ingreso);
                                        modalAnularIngreso.setCancelable(false);

                                        modalAnularIngreso.show();

                                        btnAnularIngreso    = modalAnularIngreso.findViewById(R.id.btnAnularIngreso);
                                        btnCancelarAIngreso = modalAnularIngreso.findViewById(R.id.btnCancelarIngreso);

                                        btnAnularIngreso.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AnularIngresos(GlobalInfo.getegresoID10);
                                                modalAnularIngreso.dismiss();
                                                modalOpciones.dismiss();
                                            }
                                        });

                                        btnCancelarAIngreso.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                modalAnularIngreso.dismiss();
                                                modalOpciones.dismiss();
                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "Ingreso se encuntra anulado", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    ingresosAdapter.notifyDataSetChanged();

                    recyclerListaIngresos.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerListaIngresos.setAdapter(ingresosAdapter);


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ingresos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Egresos - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * @MODAL:Egresos
     */
    public void moveToDescriptionE(Egreso item) {
        GlobalInfo.getegresoID10        = item.getId();
        GlobalInfo.getegresoAnulado10   = item.getAnulado();
    }

    /**
     * @MODAL:Ingresos
     */
    public void moveToDescriptionI(Ingresos item) {
        GlobalInfo.getegresoID10        = item.getId();
        GlobalInfo.getegresoAnulado10   = item.getAnulado();
    }

    /**
     * @MODAL:CalculoEfectivo
     */
    private void calcularEfectivoRestante() {
        double restante = efectivoDisponible - sumaTotal;
        inputEfectivoDisponible.setText(String.format("%.2f", restante));
    }

    /**
     * @GUARDAR:Egresos
     */
    private void guardarEgresos(String terminalID, Integer turno, String fechaProceso, Integer monedaID, Integer egresoID, Double mtoTotal,
                                String observacion,String userID,String identFID) {
        Egreso egreso = new Egreso(terminalID,turno,fechaProceso,monedaID,egresoID,mtoTotal,observacion,userID,identFID);

        Call<Integer> call = mAPIService.post(egreso);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {

                    idGenerado = response.body();

                    Toast.makeText(getContext(), "Guardado correctamente Egresos" , Toast.LENGTH_SHORT).show();

                    findListadoEgresos(GlobalInfo.getterminalID10, GlobalInfo.getterminalTurno10);
                    findMontoEfectivo(GlobalInfo.getterminalID10, GlobalInfo.getterminalTurno10);

                    /** Fecha de Impresión - Documento */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());

                    imprimirEgreso(GlobalInfo.getTipoPapel10,GlobalInfo.getterminalTurno10,GlobalInfo.getterminalFecha10,
                            GlobalInfo.getuserName10, importeD, xFechaHoraImpresion, String.valueOf(idGenerado));

                } else {
                    Toast.makeText(getContext(), "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión Egresos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * @GUARDAR:Ingresos
     */
    private void guardarIngresos(String terminalID, Integer turno, String fechaProceso, Integer monedaID, Double mtoTotal,
                                String observacion,String userID,String identFID) {
        Ingresos ingresos = new Ingresos(terminalID,turno,fechaProceso,monedaID,mtoTotal,observacion,userID,identFID);

        Call<Integer> call = mAPIService.post(ingresos);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {

                    idGenerado = response.body();

                    Toast.makeText(getContext(), "Guardado correctamente Ingresos" , Toast.LENGTH_SHORT).show();

                    findListadoIngresos(GlobalInfo.getterminalID10, GlobalInfo.getterminalTurno10);

                    /** Fecha de Impresión - Documento */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());

                    imprimirIngreso(GlobalInfo.getTipoPapel10,GlobalInfo.getterminalTurno10,GlobalInfo.getterminalFecha10,
                            GlobalInfo.getuserName10, importeDIngreso, xFechaHoraImpresion, String.valueOf(idGenerado));

                } else {
                    Toast.makeText(getContext(), "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión Egresos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @Anular:Egreso
     */
    private void AnularEgreso(Integer id) {

        Call<List<Egreso>> call = mAPIService.findEgresoAnular(id);
        call.enqueue(new Callback<List<Egreso>>() {
            @Override
            public void onResponse(Call<List<Egreso>> call, Response<List<Egreso>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error Anular Egreso: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    findListadoEgresos(GlobalInfo.getterminalID10, GlobalInfo.getterminalTurno10);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Egreso>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Anular Egreso - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * @Anular:Ingresos
     */
    private void AnularIngresos(Integer id) {

        Call<List<Ingresos>> call = mAPIService.findIngresosAnular(id);
        call.enqueue(new Callback<List<Ingresos>>() {
            @Override
            public void onResponse(Call<List<Ingresos>> call, Response<List<Ingresos>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error Anular Ingreso: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    findListadoIngresos(GlobalInfo.getterminalID10, GlobalInfo.getterminalTurno10);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Ingresos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Anular Ingreso - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * @IMPRIMIR:Egreso
     */
    private void imprimirEgreso(String tipopapel, Integer _Turno,String _FechaDocumento,
                              String _Cajero,Double _importe,String xFechaDocumento, String ultimo){


        String NameCompany = GlobalInfo.getNameCompany10;
        String RUCCompany  = GlobalInfo.getRucCompany10;

        String MtoTotalImporte    = String.format("%.2f",_importe);

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

        Printama.with(getContext()).connect(printama -> {

            switch (tipopapel) {

                case "58mm":

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
                    printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                    printama.printTextlnBold("NRO. DEPÓSITO " + ultimo, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha Trabajo: " + _FechaDocumento, Printama.LEFT);
                    printama.printTextln("Turno      : " + _Turno, Printama.LEFT);
                    printama.printTextln("Fecha-Hora : " + xFechaDocumento, Printama.LEFT);
                    printama.printTextln("Cajero     : " + _Cajero, Printama.LEFT);
                    printama.printTextln("Monto Total Depositado S/: ", Printama.LEFT);
                    printama.printTextlnBold("                        "+ MtoTotalImporte , Printama.RIGHT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("DNI    :" , Printama.LEFT);
                    printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                    break;

                case "80mm":

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
                    printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                    printama.printTextlnBold("NRO. DEPÓSITO " + ultimo, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha Trabajo: "+ _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
                    printama.printTextln("Fecha - Hora : " + xFechaDocumento,  Printama.LEFT);
                    printama.printTextln("Cajero       : "+ _Cajero , Printama.LEFT);
                    printama.printTextln("Monto Total Depositado S/: " , Printama.LEFT);
                    printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("DNI    :" , Printama.LEFT);
                    printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                    break;

                case "65mm":


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
                    printama.printTextlnBold("NRO. DEPÓSITO "+ ultimo, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha Trabajo: "+ _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
                    printama.printTextln("Fecha - Hora : " + xFechaDocumento,  Printama.LEFT);
                    printama.printTextln("Cajero       : "+ _Cajero , Printama.LEFT);
                    printama.printTextln("Monto Total Depositado S/: ", Printama.LEFT);
                    printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("DNI    :" , Printama.LEFT);
                    printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                    break;

            }
            printama.feedPaper();
            printama.cutPaper();
            printama.close();

        }, this::showToast);

    }

    /**
     * @IMPRIMIR:Ingreso
     */
    private void imprimirIngreso(String tipopapel, Integer _Turno,String _FechaDocumento,
                              String _Cajero,Double _importe,String xFechaDocumento, String ultimo){


        String NameCompany = GlobalInfo.getNameCompany10;
        String RUCCompany  = GlobalInfo.getRucCompany10;

        String MtoTotalImporte    = String.format("%.2f",_importe);

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

        Printama.with(getContext()).connect(printama -> {

            switch (tipopapel) {

                case "58mm":

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
                    printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                    printama.printTextlnBold("NRO. INGRESO " + ultimo, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha Trabajo: " + _FechaDocumento, Printama.LEFT);
                    printama.printTextln("Turno      : " + _Turno, Printama.LEFT);
                    printama.printTextln("Fecha-Hora : " + xFechaDocumento, Printama.LEFT);
                    printama.printTextln("Cajero     : " + _Cajero, Printama.LEFT);
                    printama.printTextln("Monto Total Ingresado S/: ", Printama.LEFT);
                    printama.printTextlnBold("                        "+ MtoTotalImporte , Printama.RIGHT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("DNI    :" , Printama.LEFT);
                    printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                    break;

                case "80mm":

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
                    printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                    printama.printTextlnBold("NRO. INGRESO " + ultimo, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha Trabajo: "+ _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
                    printama.printTextln("Fecha - Hora : " + xFechaDocumento,  Printama.LEFT);
                    printama.printTextln("Cajero       : "+ _Cajero , Printama.LEFT);
                    printama.printTextln("Monto Total Ingresado S/: " , Printama.LEFT);
                    printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("DNI    :" , Printama.LEFT);
                    printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                    break;

                case "65mm":


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
                    printama.printTextlnBold("NRO. INGRESO "+ ultimo, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha Trabajo: "+ _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
                    printama.printTextln("Fecha - Hora : " + xFechaDocumento,  Printama.LEFT);
                    printama.printTextln("Cajero       : "+ _Cajero , Printama.LEFT);
                    printama.printTextln("Monto Total Ingresado S/: ", Printama.LEFT);
                    printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("DNI    :" , Printama.LEFT);
                    printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                    break;

            }
            printama.feedPaper();
            printama.cutPaper();
            printama.close();

        }, this::showToast);

    }

    /**
     * @REIMPRIMIR:Egreso
     */
    private void ReimpresionEgreso(String tipopapel,Integer id) {

        Call<List<Egreso>> call = mAPIService.findListadoReimprimir(id);

        call.enqueue(new Callback<List<Egreso>>() {
            @Override
            public void onResponse(Call<List<Egreso>> call, Response<List<Egreso>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Egreso> egresoList = response.body();

                    Integer id            = 0;
                    String terminalID     = "";
                    Integer turno         = 0;
                    String fechaProceso   = "";
                    Integer monedaID      = 0;
                    Integer egresoID      = 0;
                    Double mtoTotal       = 0.00;
                    String observacion    = "";
                    String userID         = "";
                    String identFID       = "";
                    String anulado        = "";

                    String finalId  = "";

                    for (Egreso egreso : egresoList) {

                        id            = Integer.valueOf(egreso.getId());
                        terminalID    = String.valueOf(egreso.getTerminalID());
                        turno         = Integer.valueOf(egreso.getTurno());
                        fechaProceso  = String.valueOf(egreso.getFechaProceso());
                        monedaID      = Integer.valueOf(egreso.getMonedaID());
                        egresoID      = Integer.valueOf(egreso.getEgresoID());
                        mtoTotal      = Double.valueOf(egreso.getMtoTotal());
                        observacion   = String.valueOf(egreso.getObservacion());
                        userID        = String.valueOf(egreso.getUserID());
                        identFID      = String.valueOf(egreso.getIdentFID());
                        anulado       = String.valueOf(egreso.getAnulado());

                        finalId = String.valueOf(id);

                    }

                    String NameCompany = GlobalInfo.getNameCompany10;
                    String RUCCompany  = GlobalInfo.getRucCompany10;
                    String Cajero1           = GlobalInfo.getuserName10;

                    String MtoTotalImporte    = String.format("%.2f",mtoTotal);

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

                    Integer finalTurno = turno;
                    String finalFechaProceso = fechaProceso.split(" ")[0];


                    /** Fecha de Impresión - Documento */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());


                    String finalId1 = finalId;
                    Printama.with(getContext()).connect(printama -> {

                        switch (tipopapel) {

                            case "58mm":

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
                                printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                                printama.printTextlnBold("NRO. DEPÓSITO " + finalId1, Printama.CENTER);
                                printama.printTextlnBold("COPIA", Printama.CENTER);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextln("Fecha Trabajo: " + finalFechaProceso, Printama.LEFT);
                                printama.printTextln("Turno      : " + finalTurno, Printama.LEFT);
                                printama.printTextln("Fecha-Hora : " + xFechaHoraImpresion, Printama.LEFT);
                                printama.printTextln("Cajero     : " + Cajero1, Printama.LEFT);
                                printama.printTextln("Monto Total Depositado S/: ", Printama.LEFT);
                                printama.printTextlnBold("                      "+ MtoTotalImporte , Printama.RIGHT);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                                break;

                            case "80mm":

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
                                printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                                printama.printTextlnBold("NRO. DEPÓSITO "+ finalId1, Printama.CENTER);
                                printama.printTextlnBold("COPIA", Printama.CENTER);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextln("Fecha Trabajo: "+ finalFechaProceso + "  Turno: " + finalTurno, Printama.LEFT);
                                printama.printTextln("Fecha - Hora : " + xFechaHoraImpresion,  Printama.LEFT);
                                printama.printTextln("Cajero       : "+ Cajero1, Printama.LEFT);
                                printama.printTextln("Monto Total Depositado S/: " , Printama.LEFT);
                                printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                                break;

                            case "65mm":

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
                                printama.printTextlnBold("NRO. DEPÓSITO "+ finalId1, Printama.CENTER);
                                printama.printTextlnBold("COPIA", Printama.CENTER);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextln("Fecha Trabajo: "+ finalFechaProceso + "  Turno: " + finalTurno, Printama.LEFT);
                                printama.printTextln("Fecha - Hora : " + xFechaHoraImpresion,  Printama.LEFT);
                                printama.printTextln("Cajero       : "+ Cajero1, Printama.LEFT);
                                printama.printTextln("Monto Total Depositado S/: ", Printama.LEFT);
                                printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                                break;

                        }
                        printama.feedPaper();
                        printama.cutPaper();
                        printama.close();
                    }, this::showToast);

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
            public void onFailure(Call<List<Egreso>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Reimpresion - RED - WIFI", Toast.LENGTH_SHORT).show();
            }


        });
    }

    /**
     * @REIMPRIMIR:Ingreso
     */
    private void ReimpresionIngreso(String tipopapel,Integer id) {

        Call<List<Ingresos>> call = mAPIService.findListadoReimprimirIngresos(id);

        call.enqueue(new Callback<List<Ingresos>>() {
            @Override
            public void onResponse(Call<List<Ingresos>> call, Response<List<Ingresos>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Ingresos> ingresosList = response.body();

                    Integer id            = 0;
                    String terminalID     = "";
                    Integer turno         = 0;
                    String fechaProceso   = "";
                    Integer monedaID      = 0;
                    Integer egresoID      = 0;
                    Double mtoTotal       = 0.00;
                    String observacion    = "";
                    String userID         = "";
                    String identFID       = "";
                    String anulado        = "";

                    String finalId  = "";

                    for (Ingresos ingresos : ingresosList) {

                        id            = Integer.valueOf(ingresos.getId());
                        terminalID    = String.valueOf(ingresos.getTerminalID());
                        turno         = Integer.valueOf(ingresos.getTurno());
                        fechaProceso  = String.valueOf(ingresos.getFechaProceso());
                        monedaID      = Integer.valueOf(ingresos.getMonedaID());
                        mtoTotal      = Double.valueOf(ingresos.getMtoTotal());
                        observacion   = String.valueOf(ingresos.getObservacion());
                        userID        = String.valueOf(ingresos.getUserID());
                        identFID      = String.valueOf(ingresos.getIdentFID());
                        anulado       = String.valueOf(ingresos.getAnulado());

                        finalId = String.valueOf(id);

                    }

                    String NameCompany = GlobalInfo.getNameCompany10;
                    String RUCCompany  = GlobalInfo.getRucCompany10;
                    String Cajero1     = GlobalInfo.getuserName10;

                    String MtoTotalImporte    = String.format("%.2f",mtoTotal);

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

                    Integer finalTurno = turno;
                    String finalFechaProceso = fechaProceso.split(" ")[0];


                    /** Fecha de Impresión - Documento */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());


                    String finalId1 = finalId;
                    Printama.with(getContext()).connect(printama -> {

                        switch (tipopapel) {

                            case "58mm":

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
                                printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                                printama.printTextlnBold("NRO. INGRESO " + finalId1, Printama.CENTER);
                                printama.printTextlnBold("COPIA", Printama.CENTER);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextln("Fecha Trabajo: " + finalFechaProceso, Printama.LEFT);
                                printama.printTextln("Turno      : " + finalTurno, Printama.LEFT);
                                printama.printTextln("Fecha-Hora : " + xFechaHoraImpresion, Printama.LEFT);
                                printama.printTextln("Cajero     : " + Cajero1, Printama.LEFT);
                                printama.printTextln("Monto Total Ingresado S/: ", Printama.LEFT);
                                printama.printTextlnBold("                      "+ MtoTotalImporte , Printama.RIGHT);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                                break;

                            case "80mm":

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
                                printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);
                                printama.printTextlnBold("NRO. INGRESO "+ finalId1, Printama.CENTER);
                                printama.printTextlnBold("COPIA", Printama.CENTER);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextln("Fecha Trabajo: "+ finalFechaProceso + "  Turno: " + finalTurno, Printama.LEFT);
                                printama.printTextln("Fecha - Hora : " + xFechaHoraImpresion,  Printama.LEFT);
                                printama.printTextln("Cajero       : "+ Cajero1, Printama.LEFT);
                                printama.printTextln("Monto Total Ingresado S/: " , Printama.LEFT);
                                printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                                break;

                            case "65mm":

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
                                printama.printTextlnBold("NRO. INGRESO "+ finalId1, Printama.CENTER);
                                printama.printTextlnBold("COPIA", Printama.CENTER);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextln("Fecha Trabajo: "+ finalFechaProceso + "  Turno: " + finalTurno, Printama.LEFT);
                                printama.printTextln("Fecha - Hora : " + xFechaHoraImpresion,  Printama.LEFT);
                                printama.printTextln("Cajero       : "+ Cajero1, Printama.LEFT);
                                printama.printTextln("Monto Total Ingresado S/: ", Printama.LEFT);
                                printama.printTextlnBold("                           "+ MtoTotalImporte , Printama.LEFT);
                                printama.setSmallText();
                                printSeparatorLine(printama, tipopapel);
                                printama.addNewLine(1);
                                printama.setSmallText();
                                printama.printTextlnBold("DNI    :" , Printama.LEFT);
                                printama.printTextlnBold("FIRMA  :" , Printama.LEFT);

                                break;

                        }
                        printama.feedPaper();
                        printama.cutPaper();
                        printama.close();
                    }, this::showToast);

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
            public void onFailure(Call<List<Ingresos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Reimpresion - RED - WIFI", Toast.LENGTH_SHORT).show();
            }


        });
    }

    /**
     * @ALERTA:Conexión de Bluetooth
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

    private void printSeparatorLine(Printama printama, String tipopapel) {
        if ("80mm".equals(tipopapel) || "65mm".equals(tipopapel)) {
            printama.printDoubleDashedLine();
        } else if ("58mm".equals(tipopapel)) {
            printama.printDoubleDashedLines();
        }
    }
}
