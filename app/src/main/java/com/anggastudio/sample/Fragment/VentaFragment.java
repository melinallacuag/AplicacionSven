package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.anggastudio.sample.Adapter.DetalleVentaAdapter;
import com.anggastudio.sample.Adapter.LClienteAdapter;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ManguerasAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class VentaFragment extends Fragment{

    boolean mTimerRunning;

    RecyclerView recyclerLados,recyclerMangueras,recyclerLCliente,recyclerDetalleVenta;

    List<LClientes> clientesList;
    LClienteAdapter lclienteAdapter;

    List<Lados> ladosList;
    LadosAdapter ladosAdapter;

    List<Mangueras> manguerasList;
    ManguerasAdapter manguerasAdapter;

    TipoPago tipoPago;
    List<TipoPago> tipoPagoList;
    TipoPagoAdapter tipoPagoAdapter;

    DetalleVentaAdapter detalleVentaAdapter;
    List<DetalleVenta> detalleVentaList;

    TextView  datos_terminal,textMensajePEfectivo;

    Dialog modalLibre,modalSoles,modalGalones,modalBoleta,modalCliente,modalFactura,modalSerafin;

    Button btnAutomatico,btnListadoComprobante,btnLibre,btnCancelarLibre,btnAceptarLibre,btnSoles,btnCancelarSoles,btnAgregarLibre,btnGalones,btnCancelarGalones,btnAgregarGalones,
           btnBoleta,btnCancelarBoleta,btnAgregarBoleta,btnGenerarBoleta,buscarPlacaBoleta,buscarDNIBoleta,btnCancelarLCliente,
            btnFactura,buscarRUCFactura,buscarPlacaFactura,btnCancelarFactura,btnAgregarFactura,btnSerafin,btnCancelarSerafin,btnAgregarSerafin;

    TextInputLayout alertSoles,alertGalones,alertPlaca,alertDNI,alertRUC,alertNombre,alertRazSocial,alertPEfectivo,alertOperacion,alertSelectTPago;

    TextInputEditText inputMontoSoles,inputCantidadGalones,inputPlaca,inputDNI,inputRUC,inputNombre,inputRazSocial,inputDireccion,inputKilometraje,
            inputObservacion,inputOperacion,inputPEfectivo;

    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioNombreFormaPago;
    Spinner SpinnerTPago;

    SearchView btnBuscadorClienteRZ;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_venta, container, false);

        btnAutomatico   = view.findViewById(R.id.btnAutomatico);
        btnListadoComprobante = view.findViewById(R.id.btnListadoComprobante);

        datos_terminal  = view.findViewById(R.id.datos_terminal);

        btnLibre        = view.findViewById(R.id.btnLibre);
        btnSoles        = view.findViewById(R.id.btnSoles);
        btnGalones      = view.findViewById(R.id.btnGalones);
        btnBoleta       = view.findViewById(R.id.btnBoleta);
        btnFactura      = view.findViewById(R.id.btnFactura);
        btnSerafin      = view.findViewById(R.id.btnSerafin);

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

        /** Boton para Dirigirse al Listado de Comprobantes */
        btnListadoComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManagerComprobante = getActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransactionComprobante = fragmentManagerComprobante.beginTransaction();

                int fragmentContainerVenta = R.id.fragment_container;
                ListaComprobantesFragment listaComprobantesFragment = new ListaComprobantesFragment();
                fragmentTransactionComprobante.replace(fragmentContainerVenta, listaComprobantesFragment);
                fragmentTransactionComprobante.addToBackStack(null);
                fragmentTransactionComprobante.commit();

            }
        });

        /** Datos de Terminal ID y Turno */
        datos_terminal.setText("PUNTO 7 - " +"TURNO: " +02);

        /** Listado de Card - Lado */
        recyclerLados = view.findViewById(R.id.recyclerLado);
        recyclerLados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ladosList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            ladosList.add(new Lados("01"));
            ladosList.add(new Lados("02"));
            ladosList.add(new Lados("03"));
            ladosList.add(new Lados("04"));
            ladosList.add(new Lados("05"));
        }

        ladosAdapter = new LadosAdapter(ladosList, getContext(), new LadosAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Lados item) {
                return 0;
            }
        });

        recyclerLados.setAdapter(ladosAdapter);

        /** Listado de Card - Manguera */
        recyclerMangueras = view.findViewById(R.id.recyclerMangueras);
        recyclerMangueras.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        manguerasList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            manguerasList.add(new Mangueras("G-REGULAR","01"));
            manguerasList.add(new Mangueras("G-PREMIUM","02"));
            manguerasList.add(new Mangueras("GLP","03"));
            manguerasList.add(new Mangueras("DIESEL","04"));
        }

        manguerasAdapter = new ManguerasAdapter(manguerasList, getContext(), new ManguerasAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Mangueras item) {
                return 0;
            }
        });

        recyclerMangueras.setAdapter(manguerasAdapter);

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
                btnAgregarLibre       = modalSoles.findViewById(R.id.btnAgregarLibre);
                inputMontoSoles       = modalSoles.findViewById(R.id.inputMontoSoles);
                alertSoles            = modalSoles.findViewById(R.id.alertSoles);

                btnCancelarSoles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalSoles.dismiss();

                        inputMontoSoles.getText().clear();

                    }
                });

                btnAgregarLibre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String MontoSoles = inputMontoSoles.getText().toString();

                        if (MontoSoles.isEmpty()) {
                            alertSoles.setError("El campo soles es obligatorio");
                            return;
                        }

                        Integer NumIntSoles = Integer.parseInt(MontoSoles);

                        if(NumIntSoles < 5 || NumIntSoles > 9999){
                            alertSoles.setError("El valor debe ser mayor a 5 y menor que 9999");
                            return;
                        }

                        alertSoles.setErrorEnabled(false);
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

                        Integer NumIntGalones = Integer.parseInt(CantidadGalones);

                        if(NumIntGalones < 1 || NumIntGalones > 999){
                            alertGalones.setError("El valor debe ser mayor a 1 y menor que 999");
                            return;
                        }

                        alertGalones.setErrorEnabled(false);

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
                inputKilometraje  = modalBoleta.findViewById(R.id.inputKilometraje);
                inputObservacion  = modalBoleta.findViewById(R.id.inputObservacion);
                inputPEfectivo    = modalBoleta.findViewById(R.id.inputPEfectivo);
                inputOperacion    = modalBoleta.findViewById(R.id.inputOperacion);

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

                /** Mostrar Formulario de Listado de Cliente y Realizar la Operacion */
                modalCliente = new Dialog(getContext());
                modalCliente.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalCliente.setContentView(R.layout.fragment_clientes);
                modalCliente.setCancelable(false);

                final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalCliente.show();

                        btnCancelarLCliente   = modalCliente.findViewById(R.id.btnCancelarLCliente);
                        btnBuscadorClienteRZ  = modalCliente.findViewById(R.id.btnBuscadorClienteRZ);

                        recyclerLCliente = modalCliente.findViewById(R.id.recyclerLCliente);
                        recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));

                        clientesList = new ArrayList<>();

                        for (int i = 0; i < 1; i++){
                            clientesList.add(new LClientes("11111111","","CLIENTE VARIOS","CLEMENTE"));
                            clientesList.add(new LClientes("11111112","","JUAN PEREZ","SAN JUAN"));
                            clientesList.add(new LClientes("11111113","","CLEMENTE MAURICIO","UGARTE"));
                            clientesList.add(new LClientes("11111114","","RAUL CONTRERAS","MIRAFLORES"));
                            clientesList.add(new LClientes("11111115","","PAULO LOPEZ","GRAU"));
                        }

                        lclienteAdapter = new LClienteAdapter(clientesList, getContext(), new LClienteAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(LClientes item) {
                                String SelectDNI       = item.getClienteID();
                                String SelectNombre    = item.getClienteRZ();
                                String SelectDireccion = item.getClienteDR();

                                inputDNI.setText(SelectDNI);
                                inputNombre.setText(SelectNombre);
                                inputDireccion.setText(SelectDireccion);

                                modalCliente.dismiss();

                                return ;
                            }
                        });

                        recyclerLCliente.setAdapter(lclienteAdapter);

                        btnBuscadorClienteRZ.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                lclienteAdapter.filtrado(newText);
                                return false;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                modalCliente.dismiss();

                                btnBuscadorClienteRZ.setQuery("", false);

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
                tipoPagoList = new ArrayList<>();

                for (int i = 0; i < 1; i++){
                    tipoPagoList.add(new TipoPago("VISA"));
                    tipoPagoList.add(new TipoPago("YAPE"));

                }

                Resources res = getResources();
                tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) tipoPagoList, res);
                SpinnerTPago.setAdapter(tipoPagoAdapter);

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

                        if (campoDNI.isEmpty() || campoDNI == null) {
                            alertDNI.setError("* El campo DNI es obligatorio");
                            return;
                        }else if (campoDNI.length() < 8){
                            alertDNI.setError("* El DNI debe tener 8 dígitos");
                            return;
                        }

                            alertDNI.setErrorEnabled(false);

                            inputNombre.getText().clear();
                            inputDireccion.getText().clear();

                    }
                });

                /** Boton Buscador - Placa */
                buscarPlacaBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String campoPlaca = inputPlaca.getText().toString();

                        if (campoPlaca.isEmpty()) {
                            alertPlaca.setError("* El campo Placa es obligatorio");
                            return;
                        }

                            alertPlaca.setErrorEnabled(false);

                            inputDNI.getText().clear();
                            inputNombre.getText().clear();
                            inputDireccion.getText().clear();
                    }
                });

                /** Boton Buscador - Generar Cliente */
                btnGenerarBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputPlaca.setText("000-0000");
                        inputDNI.setText("11111111");
                        inputNombre.setText("CLIENTE VARIOS");
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
                        inputKilometraje.getText().clear();
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

                        String campoPlaca          = inputPlaca.getText().toString();
                        String campoDNI            = inputDNI.getText().toString();
                        String campoNombre         = inputNombre.getText().toString();
                        String campoPEfectivo      = inputPEfectivo.getText().toString();
                        String campoOperacion      = inputOperacion.getText().toString();

                        int checkedRadioButtonId   = radioFormaPago.getCheckedRadioButtonId();

                        Double dosDecimales = Double.valueOf(campoPEfectivo);
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

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
                            } else if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                return;
                            }

                        } else if (checkedRadioButtonId == radioCredito.getId()) {

                            if (campoPEfectivo.isEmpty()) {
                                alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                return;
                            } else if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                return;
                            }

                        }


                        String NombreFormaPago = radioNombreFormaPago.getText().toString();

                        if (NombreFormaPago.equals("Tarjeta")){




                        }else if (NombreFormaPago.equals("Credito")) {


                        }

                        alertPlaca.setErrorEnabled(false);
                        alertDNI.setErrorEnabled(false);
                        alertNombre.setErrorEnabled(false);
                        alertOperacion.setErrorEnabled(false);
                        alertPEfectivo.setErrorEnabled(false);

                        Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();
                        modalBoleta.dismiss();

                        /** Limpiar el Formulario - Boleta*/
                        inputPlaca.setText("000-0000");
                        inputDNI.getText().clear();
                        inputNombre.getText().clear();
                        inputDireccion.getText().clear();
                        inputKilometraje.getText().clear();
                        inputObservacion.getText().clear();
                        inputPEfectivo.setText("0");
                        inputOperacion.getText().clear();
                        radioFormaPago.check(radioEfectivo.getId());

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
                inputKilometraje   = modalFactura.findViewById(R.id.inputKilometraje);
                inputObservacion   = modalFactura.findViewById(R.id.inputObservacion);
                inputOperacion     = modalFactura.findViewById(R.id.inputOperacion);
                inputPEfectivo     = modalFactura.findViewById(R.id.inputPEfectivo);

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

                /** Mostrar Formulario de Listado de Cliente y Realizar la Operacion */
                modalCliente = new Dialog(getContext());
                modalCliente.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalCliente.setContentView(R.layout.fragment_clientes);
                modalCliente.setCancelable(false);

                final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalCliente.show();

                        btnCancelarLCliente   = modalCliente.findViewById(R.id.btnCancelarLCliente);
                        btnBuscadorClienteRZ  = modalCliente.findViewById(R.id.btnBuscadorClienteRZ);

                        recyclerLCliente = modalCliente.findViewById(R.id.recyclerLCliente);
                        recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));

                        clientesList = new ArrayList<>();

                        for (int i = 0; i < 1; i++){
                            clientesList.add(new LClientes("11111111","","CLIENTE VARIOS","CLEMENTE"));
                            clientesList.add(new LClientes("11111112","","JUAN PEREZ","SAN JUAN"));
                            clientesList.add(new LClientes("11111113","","CLEMENTE MAURICIO","UGARTE"));
                            clientesList.add(new LClientes("11111114","","RAUL CONTRERAS","MIRAFLORES"));
                            clientesList.add(new LClientes("11111115","","PAULO LOPEZ","GRAU"));
                        }

                        lclienteAdapter = new LClienteAdapter(clientesList, getContext(), new LClienteAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(LClientes item) {
                                String SelectDNI       = item.getClienteID();
                                String SelectNombre    = item.getClienteRZ();
                                String SelectDireccion = item.getClienteDR();

                                inputRUC.setText(SelectDNI);
                                inputRazSocial.setText(SelectNombre);
                                inputDireccion.setText(SelectDireccion);

                                modalCliente.dismiss();

                                return ;
                            }
                        });

                        recyclerLCliente.setAdapter(lclienteAdapter);

                        btnBuscadorClienteRZ.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                lclienteAdapter.filtrado(newText);
                                return false;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                modalCliente.dismiss();

                                btnBuscadorClienteRZ.setQuery("", false);

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
                tipoPagoList = new ArrayList<>();

                for (int i = 0; i < 1; i++){
                    tipoPagoList.add(new TipoPago("VISA"));
                    tipoPagoList.add(new TipoPago("YAPE"));

                }

                Resources res = getResources();
                tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) tipoPagoList, res);
                SpinnerTPago.setAdapter(tipoPagoAdapter);

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
                        }

                        alertRUC.setErrorEnabled(false);

                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();

                    }
                });

                /** Boton Buscador - Placa */
                buscarPlacaFactura.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String campoPlaca = inputPlaca.getText().toString();

                        if (campoPlaca.isEmpty()) {
                            alertPlaca.setError("* El campo Placa es obligatorio");
                            return;
                        }

                        alertPlaca.setErrorEnabled(false);

                        inputRUC.getText().clear();
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
                        inputKilometraje.getText().clear();
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

                        String campoPlaca          = inputPlaca.getText().toString();
                        String campoRUC            = inputRUC.getText().toString();
                        String campoRazSocial      = inputRazSocial.getText().toString();
                        String campoPEfectivo      = inputPEfectivo.getText().toString();
                        String campoOperacion      = inputOperacion.getText().toString();

                        int checkedRadioButtonId   = radioFormaPago.getCheckedRadioButtonId();

                        Double dosDecimales = Double.valueOf(campoPEfectivo);
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

                        if (campoPlaca.isEmpty()) {

                            alertPlaca.setError("* El campo Placa es obligatorio");
                            return;
                        } else if (campoRUC.isEmpty() ) {

                            alertRUC.setError("* El campo RUC es obligatorio");
                            return;
                        } else if (campoRUC.length() < 11) {

                            alertRUC.setError("* El RUC debe tener 11 dígitos");
                            return;
                        }else if (campoRazSocial.isEmpty()) {

                            alertRazSocial.setError("* El campo Nombre es obligatorio");
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
                            } else if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                return;
                            }

                        } else if (checkedRadioButtonId == radioCredito.getId()) {

                            if (campoPEfectivo.isEmpty()) {
                                alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                return;
                            } else if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                return;
                            }

                        }


                        String NombreFormaPago = radioNombreFormaPago.getText().toString();

                        if (NombreFormaPago.equals("Tarjeta")){




                        }else if (NombreFormaPago.equals("Credito")) {


                        }

                        alertPlaca.setErrorEnabled(false);
                        alertRUC.setErrorEnabled(false);
                        alertRazSocial.setErrorEnabled(false);
                        alertOperacion.setErrorEnabled(false);
                        alertPEfectivo.setErrorEnabled(false);

                        Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();
                        modalBoleta.dismiss();

                        /** Limpiar el Formulario - Boleta*/
                        inputPlaca.setText("000-0000");
                        inputRUC.getText().clear();
                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();
                        inputKilometraje.getText().clear();
                        inputObservacion.getText().clear();
                        inputPEfectivo.setText("0");
                        inputOperacion.getText().clear();
                        radioFormaPago.check(radioEfectivo.getId());

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

                        Toast.makeText(getContext(), "SE GENERO SERAFIN", Toast.LENGTH_SHORT).show();
                        modalSerafin.dismiss();
                    }

                });
            }
        });

        /** Listado de Detalle de Transacciones */
        recyclerDetalleVenta = view.findViewById(R.id.recyclerDetalleVenta);
        recyclerDetalleVenta.setLayoutManager(new LinearLayoutManager(getContext()));

        detalleVentaList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            detalleVentaList.add(new DetalleVenta("01","E",0.18,"FG4-784",""));
            detalleVentaList.add(new DetalleVenta("02","E",0.18,"S54-704",""));
            detalleVentaList.add(new DetalleVenta("03","E",0.18,"R84-145",""));
            detalleVentaList.add(new DetalleVenta("04","E",0.18,"CD4-870",""));
            detalleVentaList.add(new DetalleVenta("05","E",0.18,"C94-555",""));
        }

        detalleVentaAdapter = new DetalleVentaAdapter(detalleVentaList, getContext());

        recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

        return view;
    }
    private void modoAutomatico() {

        mTimerRunning = true;
        btnAutomatico.setText("Automático");
        btnAutomatico.setBackgroundColor(Color.parseColor("#001E8A"));

    }

    private void modoStop() {

        mTimerRunning = false;
        btnAutomatico.setText("Stop");
        btnAutomatico.setBackgroundColor(Color.parseColor("#6c757d"));
    }



}