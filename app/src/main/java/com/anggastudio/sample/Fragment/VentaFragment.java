package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.anggastudio.sample.Adapter.ArticuloAdapter;
import com.anggastudio.sample.Adapter.ArticuloGAdapter;
import com.anggastudio.sample.Adapter.CarritoAdapter;
import com.anggastudio.sample.Adapter.ClienteCreditoAdapter;
import com.anggastudio.sample.Adapter.DetalleVentaAdapter;
import com.anggastudio.sample.Adapter.LClienteAdapter;
import com.anggastudio.sample.Adapter.LRegistroClienteAdapter;
import com.anggastudio.sample.Adapter.LRegistroClientePuntosAdapter;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ManguerasAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.PasswordChecker;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;
import com.anggastudio.sample.WebApiSVEN.Models.ClienteCredito;
import com.anggastudio.sample.WebApiSVEN.Models.ClientePrecio;
import com.anggastudio.sample.WebApiSVEN.Models.Correlativo;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.LClientePuntos;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Models.VentaCA;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentaFragment extends Fragment implements NfcAdapter.ReaderCallback{

    private static final String AUTOMATICO_MODE_KEY = "automatico_mode_key";

    private APIService mAPIService;
    private NFCUtil nfcUtil;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    List<ClientePrecio> clientePrecioList;
    List<LClientePuntos>  clientePuntosList;

    List<Users> usersListNFC;
    LRegistroClienteAdapter lRegistroClienteAdapter;
    LRegistroClientePuntosAdapter lRegistroClientePuntosAdapter;

    boolean mTimerRunning;
    Timer timer;
    TimerTask timerTask;
    boolean mIsTaskScheduled = false;

    RecyclerView recyclerLClientePuntosNFC,recyclerLados,recyclerMangueras,recyclerLCliente,recyclerDetalleVenta,recyclerLClienteCredito,recyclerListaClientesAfiliados;

    ClienteCreditoAdapter clienteCreditoAdapter;

    LadosAdapter ladosAdapter;

    List<Mangueras> filtrarMangueras;
    ManguerasAdapter manguerasAdapter;

    LClienteAdapter lclienteAdapter;

    DetalleVentaAdapter detalleVentaAdapter;

    TipoPago tipoPago;
    TipoPagoAdapter tipoPagoAdapter;

    TextView  datos_terminal,textMensajePEfectivo,textTDescuento,textNumPuntos,textMensajeCanje;

    Dialog modallistNFCPuntos,modal_ErrorWifi,modal_ErrorServidor,modalCalcular,modalNFCLogin,modallistNFC,modalLibre,modalSoles,modalGalones,modalBoleta,modalClienteDNI,modalClienteRUC,modalFactura,modalNotaDespacho,modalSerafin,modalClienteCredito;

    Button btnAceptarErrorWifi,btnAceptarError,btnCancelarNFC,btnAceptarNFC,btnAutomatico,btnListadoComprobante,btnLibre,btnCancelarLibre,btnAceptarLibre,btnSoles,btnCancelarSoles,btnAgregarSoles,btnGalones,btnCancelarGalones,btnAgregarGalones,
            btnBoleta,btnCancelarBoleta,btnAgregarBoleta,btnGenerarBoleta,buscarPlacaBoleta,buscarDNIBoleta,btnCancelarLCliente,
            btnFactura,buscarRUCFactura,buscarPlacaFactura,btnCancelarFactura,btnAgregarFactura,btnNotaDespacho,btnCancelarNotaDespacho,btnAgregarNotaDespacho,btnSerafin,btnCancelarSerafin,btnAgregarSerafin,
            btnAgregarCalcular,btnCancelarCalcular,Activar_Descuento_Punto,buscarListPuntosNFC,buscarListNFC;

    TextInputLayout alertuserNFC,alertpasswordNFC,alertSoles,alertGalones,alertPlaca,alertDNI,alertRUC,alertNombre,alertRazSocial,alertPEfectivo,alertOperacion,alertSelectTPago,
            alertCPlaca,alertCTarjeta,alertCCliente,alertCRazSocial,alertCalcular;

    TextInputEditText usuarioNFC,contraseñaNFC,inputNFC,inputMontoSoles,inputCantidadGalones,inputPlaca,inputDNI,inputRUC,inputNombre,inputRazSocial,inputDireccion,
            inputObservacion,inputOperacion,inputPEfectivo,inputCPlaca,input_CNTarjeta,inputCCliente,inputCRazSocial,inputCDireccion,inputCKilometraje,inputCObservacion,
            inputCalcular,inputCMonto;

    String usuarioUserNFC,contraseñaUserNFC;
    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioNombreFormaPago,radioCanje;
    Spinner SpinnerTPago;

    SearchView btnBuscadorClienteRZ,BuscarRazonSocial;

    Double monto;

    FloatingActionButton btncarritocompra;

    ImageButton btnLimpiarLado,btnCalcular;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcUtil = new NFCUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_venta, container, false);

        mAPIService = GlobalInfo.getAPIService();

        btnAutomatico         = view.findViewById(R.id.btnAutomatico);
        btncarritocompra      = view.findViewById(R.id.btncarritocompra);
        btnListadoComprobante = view.findViewById(R.id.btnListadoComprobante);
        btnLibre              = view.findViewById(R.id.btnLibre);
        btnSoles              = view.findViewById(R.id.btnSoles);
        btnGalones            = view.findViewById(R.id.btnGalones);
        btnBoleta             = view.findViewById(R.id.btnBoleta);
        btnFactura            = view.findViewById(R.id.btnFactura);
        btnNotaDespacho       = view.findViewById(R.id.btnnotadespacho);
        btnSerafin            = view.findViewById(R.id.btnSerafin);
        datos_terminal        = view.findViewById(R.id.datos_terminal);
        btnLimpiarLado        = view.findViewById(R.id.btnLimpiarLado);

        /** Modal de Error al Servidor **/
        modal_ErrorServidor = new Dialog(getContext());
        modal_ErrorServidor.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modal_ErrorServidor.setContentView(R.layout.alerta_servidor);
        modal_ErrorServidor.setCancelable(false);

        /** Modal de Error al Wifi **/
        modal_ErrorWifi = new Dialog(getContext());
        modal_ErrorWifi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modal_ErrorWifi.setContentView(R.layout.alerta_wifi);
        modal_ErrorWifi.setCancelable(false);

        /**
         * @Bloquear:Botones
         */
        btnLibre.setEnabled(false);
        btnSoles.setEnabled(false);
        btnGalones.setEnabled(false);
        btnBoleta.setEnabled(false);
        btnFactura.setEnabled(false);
        btnNotaDespacho.setEnabled(false);
        btnSerafin.setEnabled(false);

        /**
         * @LIMPIAR:Lados
         */

        btnLimpiarLado.setColorFilter(getResources().getColor(R.color.white));
        btnLimpiarLado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ladoSeleccionado = GlobalInfo.getCara10;

                for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10) {
                    if (detalleVenta.getCara().equals(ladoSeleccionado)) {
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
                        detalleVenta.setRfid("1");
                    }
                }

                detalleVentaAdapter.notifyDataSetChanged();

            }
        });

        /**
         * @VISTA:CarritoCompra
         */
        if (GlobalInfo.getterminalVentaTienda10 && GlobalInfo.getterminalVentaPlaya10) {
            btncarritocompra.setVisibility(View.VISIBLE);
        } else {
            btncarritocompra.setVisibility(View.GONE);
        }

        btncarritocompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManagerArticulo = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionArticulo = fragmentManagerArticulo.beginTransaction();

                int fragmentContainerArticulo = R.id.fragment_container;
                ArticuloFragment articuloFragment = new ArticuloFragment();
                fragmentTransactionArticulo.replace(fragmentContainerArticulo, articuloFragment);
                fragmentTransactionArticulo.addToBackStack(null);
                fragmentTransactionArticulo.commit();

            }
        });

        /**
         * @OBTENER:DatosTerminal_IDTurno
         */
        datos_terminal.setText(GlobalInfo.getterminalID10 + " - " +"TURNO: " + GlobalInfo.getterminalTurno10);

        /**
         * @Restaurar:EstadoBotónAutomático
         */
        if (savedInstanceState != null) {
            mTimerRunning = savedInstanceState.getBoolean(AUTOMATICO_MODE_KEY);
        }

        /**
         * @Boton:AutomaticoStop
         */
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

        /**
         * @ESTABLECER:EstadoBotónAutomático
         */
        if(mIsTaskScheduled) {
            modoAutomatico();
        }else{
            modoStop();
        }

        /**
         * @PANTALLA:ListadoComprobantes
         */
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

        /**
         * @OBTENER:ListadoLados
         */
        recyclerLados = view.findViewById(R.id.recyclerLado);
        recyclerLados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        Lados_ByTerminal();

        /**
         * @OBTENER:ListadoMangueras
         */
        recyclerMangueras = view.findViewById(R.id.recyclerMangueras);
        recyclerMangueras.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        /**
         * @MODAL:MostrarFormularioLibre
         */
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

                        Context context = requireContext();

                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {

                            guardar_modoLibre(GlobalInfo.getManguera10);

                            Toast.makeText(getContext(), "SE ACTIVO EL MODO LIBRE", Toast.LENGTH_SHORT).show();
                            modalLibre.dismiss();

                        }else{
                            modal_ErrorWifi.show();
                            btnAceptarErrorWifi   = modal_ErrorWifi.findViewById(R.id.btnAceptarWifi);
                            btnAceptarErrorWifi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    modal_ErrorWifi.dismiss();
                                }
                            });
                        }

                    }
                });

            }
        });

        /**
         * @MODAL:MostrarFormularioSoles
         */
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

                if(GlobalInfo.getsettingFuelName10 == null || GlobalInfo.getsettingFuelName10.isEmpty()) {
                    inputMontoSoles.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }else{
                    inputMontoSoles.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

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

                        boolean isDecimal  = MontoSoles.contains(".");

                        if(GlobalInfo.getsettingFuelName10 == null || GlobalInfo.getsettingFuelName10.isEmpty()) {

                            if (isDecimal) {

                                Double DoubleMontoSoles = Double.parseDouble(MontoSoles);

                                if (DoubleMontoSoles < 2.0 || DoubleMontoSoles > 9999.0) {
                                    alertSoles.setError("El valor debe ser mayor a 2.0 y menor que 9999");
                                    return;
                                }

                            } else {

                                int NumIntSoles = Integer.parseInt(MontoSoles);

                                if (NumIntSoles < 2 || NumIntSoles > 9999) {
                                    alertSoles.setError("El valor debe ser mayor a 2 y menor que 9999");
                                    return;
                                }

                            }

                        }else {

                            int NumIntSoles = Integer.parseInt(MontoSoles);

                            if (NumIntSoles < 5 || NumIntSoles > 9999) {
                                alertSoles.setError("El valor debe ser mayors a 5 y menor que 9999");
                                return;
                            }

                        }

                        Context context = requireContext();

                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {

                            alertSoles.setErrorEnabled(false);

                            guardar_montoSoles(GlobalInfo.getManguera10, Double.parseDouble(MontoSoles));

                            Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                            modalSoles.dismiss();
                            inputMontoSoles.getText().clear();
                        }else{
                            modal_ErrorWifi.show();
                            btnAceptarErrorWifi   = modal_ErrorWifi.findViewById(R.id.btnAceptarWifi);
                            btnAceptarErrorWifi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    modal_ErrorWifi.dismiss();
                                }
                            });
                        }

                    }
                });

            }
        });

        /**
         * @MODAL:MostrarFormularioGalones
         */
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

                if(GlobalInfo.getsettingFuelName10 == null || GlobalInfo.getsettingFuelName10.isEmpty()) {
                    inputCantidadGalones.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }else{
                    inputCantidadGalones.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

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

                        boolean isDecimal  = CantidadGalones.contains(".");

                        if(GlobalInfo.getsettingFuelName10 == null || GlobalInfo.getsettingFuelName10.isEmpty()) {

                            if (isDecimal) {

                                Double DoubleGalonesMonto  = Double.parseDouble(CantidadGalones);

                                if(DoubleGalonesMonto < 1.0 || DoubleGalonesMonto > 999.0){
                                    alertGalones.setError("El valor debe ser mayor a 1.0 y menor que 999.0");
                                    return;
                                }

                            } else {

                                int NumIntGalones = Integer.parseInt(CantidadGalones);

                                if (NumIntGalones < 1 || NumIntGalones > 999) {
                                    alertGalones.setError("El valor debe ser mayor a 1 y menor que 999");
                                    return;
                                }

                            }

                        }else {

                            int NumIntGalones = Integer.parseInt(CantidadGalones);

                            if (NumIntGalones < 1 || NumIntGalones > 999) {
                                alertGalones.setError("El valor debe ser mayor a 1 y menor que 999");
                                return;
                            }

                        }

                        Context context = requireContext();

                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {

                            alertGalones.setErrorEnabled(false);

                            guardar_galones(GlobalInfo.getManguera10, Double.valueOf(CantidadGalones));

                            Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                            modalGalones.dismiss();

                            inputCantidadGalones.getText().clear();
                        }else{
                            modal_ErrorWifi.show();
                            btnAceptarErrorWifi   = modal_ErrorWifi.findViewById(R.id.btnAceptarWifi);
                            btnAceptarErrorWifi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    modal_ErrorWifi.dismiss();
                                }
                            });
                        }

                    }
                });

            }
        });

        /**
         * @MODAL:MostrarFormularioBoleta
         */
        modalBoleta = new Dialog(getContext());
        modalBoleta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalBoleta.setContentView(R.layout.fragment_boleta);
        modalBoleta.setCancelable(false);

        btnBoleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                limpiarCamposBoletaFacturaNDespacho();

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
                textNumPuntos     = modalBoleta.findViewById(R.id.textNumPuntos);
                textMensajeCanje  = modalBoleta.findViewById(R.id.textMensajeCanje);

                radioFormaPago    = modalBoleta.findViewById(R.id.radioFormaPago);
                radioEfectivo     = modalBoleta.findViewById(R.id.radioEfectivo);
                radioTarjeta      = modalBoleta.findViewById(R.id.radioTarjeta);
                radioCredito      = modalBoleta.findViewById(R.id.radioCredito);
                radioCanje        = modalBoleta.findViewById(R.id.radioCanje);

                buscarDNIBoleta   = modalBoleta.findViewById(R.id.buscarDNIBoleta);
                buscarPlacaBoleta = modalBoleta.findViewById(R.id.buscarPlacaBoleta);
                btnGenerarBoleta  = modalBoleta.findViewById(R.id.btnGenerarBoleta);
                btnCancelarBoleta = modalBoleta.findViewById(R.id.btnCancelarBoleta);
                btnAgregarBoleta  = modalBoleta.findViewById(R.id.btnAgregarBoleta);
                buscarListNFC     = modalBoleta.findViewById(R.id.buscarListNFC);
                btnCalcular       = modalBoleta.findViewById(R.id.btnCalcular);
                buscarListPuntosNFC  = modalBoleta.findViewById(R.id.buscarListPuntosNFC);

                inputDNI.setEnabled(true);
                inputNombre.setEnabled(true);
                alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                radioCanje.setVisibility(View.GONE);

                if(GlobalInfo.getTerminalSoloPuntos10){
                    buscarListNFC.setVisibility(View.GONE);
                    buscarListPuntosNFC.setVisibility(View.VISIBLE);
                }else{
                    buscarListNFC.setVisibility(View.VISIBLE);
                    buscarListPuntosNFC.setVisibility(View.GONE);
                }

                /**
                 * @LOGIN_MODAL:ListaDescuentos_Puntos
                 */
                modalNFCLogin = new Dialog(getContext());
                modalNFCLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalNFCLogin.setContentView(R.layout.modal_nfc_login);
                modalNFCLogin.setCancelable(false);

                buscarListNFC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarModalNFCListaDescuentoPunto();
                    }
                });

                buscarListPuntosNFC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarModalNFCListaDescuentoPunto();
                    }
                });

                /**
                 * @LectorNFC:Descuentos_Puntos
                 */
                inputNFC.setKeyListener(null);
                insertNFC();

                if (inputNFC.getTag() != null) {
                    inputNFC.removeTextChangedListener((TextWatcher) inputNFC.getTag());
                }

                inputNFC.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 16) {
                            String nfcCode = s.toString();

                            if(GlobalInfo.getTerminalSoloPuntos10){
                                findClientePrecioConPuntosDNI(nfcCode, GlobalInfo.getterminalCompanyID10);
                            }else{
                                findClientePrecioDNI(nfcCode, String.valueOf(GlobalInfo.getterminalCompanyID10));
                            }

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                /**
                 * @CALCULARDESCUENTOS
                 */
                modalCalcular = new Dialog(getContext());
                modalCalcular.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalCalcular.setContentView(R.layout.modal_calcular);
                modalCalcular.setCancelable(false);

                btnCalcular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modalCalcular.show();

                        inputCalcular       = modalCalcular.findViewById(R.id.inputCalcular);
                        alertCalcular       = modalCalcular.findViewById(R.id.alertCalcular);
                        btnCancelarCalcular = modalCalcular.findViewById(R.id.btnCancelarCalcular);
                        btnAgregarCalcular  = modalCalcular.findViewById(R.id.btnAgregarCalcular);
                        textTDescuento      = modalCalcular.findViewById(R.id.textTDescuento);

                        textTDescuento.setVisibility(View.GONE);

                        btnCancelarCalcular.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modalCalcular.dismiss();
                                inputCalcular.getText().clear();
                                textTDescuento.setText(" ");

                                textTDescuento.setVisibility(View.GONE);
                                alertCalcular.setErrorEnabled(false);
                            }
                        });
                        btnAgregarCalcular.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String calcular = inputCalcular.getText().toString();

                                if (calcular.isEmpty()) {
                                    alertCalcular.setError("* El campo Monto a Calcular es obligatorio");
                                    return;
                                }

                                textTDescuento.setText("El descuento es: " + calcular);
                                textTDescuento.setVisibility(View.VISIBLE);
                                alertCalcular.setErrorEnabled(false);

                            }
                        });

                    }
                });


                /**
                 * @Modal-ListadoClientes-Puntos
                 */
                modallistNFCPuntos = new Dialog(getContext());
                modallistNFCPuntos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modallistNFCPuntos.setContentView(R.layout.modal_list_puntos_nfc);
                modallistNFCPuntos.setCancelable(false);

                recyclerLClientePuntosNFC = modallistNFCPuntos.findViewById(R.id.recyclerLClientePuntosNFC);
                recyclerLClientePuntosNFC.setLayoutManager(new LinearLayoutManager(getContext()));
                findListaClientePuntos(GlobalInfo.getnfcId10, GlobalInfo.getterminalCompanyID10);

                /**
                 * @SELECCIONAR:DobleClick_MostrarListadoClienteDNI
                 */
                GestureDetector gestureDetectorNFC = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modallistNFCPuntos.show();

                        BuscarRazonSocial     = modallistNFCPuntos.findViewById(R.id.btnBuscadorClienteRZ);
                        btnCancelarLCliente   = modallistNFCPuntos.findViewById(R.id.btnCancelarLCliente);

                        BuscarRazonSocial.setIconifiedByDefault(false);

                        /**
                         * @BUSCAR:NombreClienteRZ
                         */

                        /** Buscador por Razon Social */
                        BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                onQueryTextChange(query);
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                String userInput = newText.toLowerCase();
                                if (lRegistroClientePuntosAdapter != null) {
                                    lRegistroClientePuntosAdapter.filtrado(userInput);
                                }
                                return true;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BuscarRazonSocial.setQuery("", false);
                                modallistNFCPuntos.dismiss();
                            }
                        });

                        return true;
                    }
                });

                if(GlobalInfo.getTerminalLecturar10){
                    inputNFC.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            boolean handled = gestureDetectorNFC.onTouchEvent(event);
                            return handled;
                        }
                    });

                }


                /**
                 * @MODAL:MostrarListadoClienteDNI
                 */
                modalClienteDNI = new Dialog(getContext());
                modalClienteDNI.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalClienteDNI.setContentView(R.layout.fragment_clientes);
                modalClienteDNI.setCancelable(false);

                /**
                 * @MODAL:MostrarListadoClienteDNI
                 */
                recyclerLCliente = modalClienteDNI.findViewById(R.id.recyclerLCliente);
                recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));
                ClienteDNI();

                /**
                 * @SELECCIONAR:DobleClick_MostrarListadoClienteDNI
                 */
                GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalClienteDNI.show();

                        btnCancelarLCliente   = modalClienteDNI.findViewById(R.id.btnCancelarLCliente);
                        btnBuscadorClienteRZ  = modalClienteDNI.findViewById(R.id.btnBuscadorClienteRZ);

                        btnBuscadorClienteRZ.setIconifiedByDefault(false);
                        /**
                         * @BUSCAR:NombreClienteRZ
                         */
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
                        boolean handled = gestureDetector.onTouchEvent(event);
                        return handled;
                    }
                });

                /**
                 * @SELECCIONAR:OpciónFormaPago
                 */
                radioFormaPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        radioNombreFormaPago = modalBoleta.findViewById(checkedId);

                        if (checkedId == radioEfectivo.getId()){
                            textMensajePEfectivo.setVisibility(View.VISIBLE);
                            textMensajeCanje.setVisibility(View.GONE);
                            alertSelectTPago.setVisibility(View.GONE);
                            alertOperacion.setVisibility(View.GONE);
                            alertPEfectivo.setVisibility(View.GONE);
                            btnCalcular.setVisibility(View.GONE);
                        } else if (checkedId == radioTarjeta.getId()){
                            textMensajePEfectivo.setVisibility(View.GONE);
                            textMensajeCanje.setVisibility(View.GONE);
                            alertSelectTPago.setVisibility(View.VISIBLE);
                            alertOperacion.setVisibility(View.VISIBLE);
                            alertPEfectivo.setVisibility(View.VISIBLE);
                            btnCalcular.setVisibility(View.GONE);
                        } else if (checkedId == radioCredito.getId()){
                            textMensajePEfectivo.setVisibility(View.GONE);
                            textMensajeCanje.setVisibility(View.GONE);
                            alertSelectTPago.setVisibility(View.GONE);
                            alertOperacion.setVisibility(View.GONE);
                            alertPEfectivo.setVisibility(View.VISIBLE);
                            btnCalcular.setVisibility(View.GONE);
                        }else if (checkedId == radioCanje.getId()){
                            textMensajePEfectivo.setVisibility(View.GONE);
                            textMensajeCanje.setVisibility(View.VISIBLE);
                            alertSelectTPago.setVisibility(View.GONE);
                            alertOperacion.setVisibility(View.GONE);
                            alertPEfectivo.setVisibility(View.GONE);
                            btnCalcular.setVisibility(View.GONE);
                        }
                    }
                });

                radioFormaPago.check(radioEfectivo.getId());

                /**
                 * @SELECCIONAR:OpciónTipoPago
                 */
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

                /**
                 * @BUSCAR:BuscadorDNI
                 */
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

                        inputPlaca.setText("000-0000");
                        inputNFC.getText().clear();
                        inputNombre.getText().clear();
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputDNI.setEnabled(true);
                        inputNombre.setEnabled(true);
                        alertDNI.setErrorEnabled(false);
                        alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);

                    }
                });

                /**
                 * @GENERAR:ClienteSimple
                 */
                btnGenerarBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputPlaca.setText(GlobalInfo.getsettingNroPlaca10);
                        inputNFC.getText().clear();
                        inputDNI.setText(GlobalInfo.getsettingClienteID10);
                        inputNombre.setText(GlobalInfo.getsettingClienteRZ10);
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputDNI.setEnabled(true);
                        inputNombre.setEnabled(true);
                        alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                    }
                });

                /**
                 * @CANCELAR:Boleta
                 */
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
                        textNumPuntos.setText(String.valueOf(0));

                        alertPlaca.setErrorEnabled(false);
                        alertDNI.setErrorEnabled(false);
                        alertNombre.setErrorEnabled(false);
                        alertPEfectivo.setErrorEnabled(false);
                        alertOperacion.setErrorEnabled(false);
                    }
                });

                /**
                 * @AGREGAR:Boleta
                 */
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
                                String nfc                 = inputNFC.getText().toString();

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
                                        alertOperacion.setError("* El  Nro Operación debe tener mayor a 4 dígitos");
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

                                detalleVenta.setMtoSaldoCredito(0.00);
                                detalleVenta.setTarjetaND("");
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);
                                detalleVenta.setPtosDisponible(Double.parseDouble(textNumPuntos.getText().toString()));
                                detalleVenta.setRfid("1");


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

                                if(!nfc.isEmpty() && nfc.equals(GlobalInfo.getRfIdCPrecio10)){
                                    detalleVenta.setRfid(GlobalInfo.getRfIdCPrecio10);
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
                                textNumPuntos.setText(String.valueOf(0));
                                radioFormaPago.check(radioEfectivo.getId());

                            }

                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }

                    }
                });

            }
        });

        /**
         * @MODAL:MostrarFormularioFactura
         */
        modalFactura = new Dialog(getContext());
        modalFactura.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalFactura.setContentView(R.layout.fragment_factura);
        modalFactura.setCancelable(false);

        btnFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                limpiarCamposBoletaFacturaNDespacho();

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
                textNumPuntos      = modalFactura.findViewById(R.id.textNumPuntos);
                textMensajeCanje   = modalFactura.findViewById(R.id.textMensajeCanje);

                radioFormaPago     = modalFactura.findViewById(R.id.radioFormaPago);
                radioEfectivo      = modalFactura.findViewById(R.id.radioEfectivo);
                radioTarjeta       = modalFactura.findViewById(R.id.radioTarjeta);
                radioCredito       = modalFactura.findViewById(R.id.radioCredito);
                radioCanje         = modalFactura.findViewById(R.id.radioCanje);

                buscarRUCFactura   = modalFactura.findViewById(R.id.buscarRUCFactura);
                buscarPlacaFactura = modalFactura.findViewById(R.id.buscarPlacaFactura);
                btnCancelarFactura = modalFactura.findViewById(R.id.btnCancelarFactura);
                btnAgregarFactura  = modalFactura.findViewById(R.id.btnAgregarFactura);
                buscarListNFC      = modalFactura.findViewById(R.id.buscarListNFC);
                btnCalcular        = modalFactura.findViewById(R.id.btnCalcular);
                buscarListPuntosNFC  = modalFactura.findViewById(R.id.buscarListPuntosNFC);

                inputRUC.setEnabled(true);
                inputRazSocial.setEnabled(true);
                alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                radioCanje.setVisibility(View.GONE);

                if(GlobalInfo.getTerminalSoloPuntos10){
                    buscarListNFC.setVisibility(View.GONE);
                    buscarListPuntosNFC.setVisibility(View.VISIBLE);
                }else{
                    buscarListNFC.setVisibility(View.VISIBLE);
                    buscarListPuntosNFC.setVisibility(View.GONE);
                }

                /**
                 * @LOGIN_MODAL:ListaDescuentos_Puntos
                 */
                modalNFCLogin = new Dialog(getContext());
                modalNFCLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalNFCLogin.setContentView(R.layout.modal_nfc_login);
                modalNFCLogin.setCancelable(false);

                buscarListNFC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarModalNFCListaDescuentoPunto();
                    }
                });

                buscarListPuntosNFC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarModalNFCListaDescuentoPunto();
                    }
                });

                /**
                 * @LectorNFC:Descuentos_Puntos
                 */
                inputNFC.setKeyListener(null);
                insertNFC();

                if (inputNFC.getTag() != null) {
                    inputNFC.removeTextChangedListener((TextWatcher) inputNFC.getTag());
                }

                inputNFC.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 16) {
                            String nfcCode = s.toString();

                            if(GlobalInfo.getTerminalSoloPuntos10){
                                findClientePrecioConPuntosRUC(nfcCode,GlobalInfo.getterminalCompanyID10);

                            }else{
                                findClientePrecioRUC(nfcCode, String.valueOf(GlobalInfo.getterminalCompanyID10));

                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                /**
                 * @CALCULARDESCUENTOS
                 */
                modalCalcular = new Dialog(getContext());
                modalCalcular.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalCalcular.setContentView(R.layout.modal_calcular);
                modalCalcular.setCancelable(false);

                btnCalcular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modalCalcular.show();

                        inputCalcular       = modalCalcular.findViewById(R.id.inputCalcular);
                        alertCalcular       = modalCalcular.findViewById(R.id.alertCalcular);
                        btnCancelarCalcular = modalCalcular.findViewById(R.id.btnCancelarCalcular);
                        btnAgregarCalcular  = modalCalcular.findViewById(R.id.btnAgregarCalcular);
                        textTDescuento      = modalCalcular.findViewById(R.id.textTDescuento);

                        textTDescuento.setVisibility(View.GONE);

                        btnCancelarCalcular.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modalCalcular.dismiss();
                                inputCalcular.getText().clear();
                                textTDescuento.setText(" ");

                                textTDescuento.setVisibility(View.GONE);
                                alertCalcular.setErrorEnabled(false);
                            }
                        });
                        btnAgregarCalcular.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String calcular = inputCalcular.getText().toString();

                                if (calcular.isEmpty()) {
                                    alertCalcular.setError("* El campo Monto a Calcular es obligatorio");
                                    return;
                                }

                                textTDescuento.setText("El descuento es: " + calcular);
                                textTDescuento.setVisibility(View.VISIBLE);
                                alertCalcular.setErrorEnabled(false);

                            }
                        });

                    }
                });

                modallistNFCPuntos = new Dialog(getContext());
                modallistNFCPuntos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modallistNFCPuntos.setContentView(R.layout.modal_list_puntos_nfc);
                modallistNFCPuntos.setCancelable(false);

                recyclerLClientePuntosNFC = modallistNFCPuntos.findViewById(R.id.recyclerLClientePuntosNFC);
                recyclerLClientePuntosNFC.setLayoutManager(new LinearLayoutManager(getContext()));
                findListaClientePuntos(GlobalInfo.getnfcId10, GlobalInfo.getterminalCompanyID10);

                /**
                 * @SELECCIONAR:DobleClick_MostrarListadoClienteDNI
                 */
                GestureDetector gestureDetectorNFC = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modallistNFCPuntos.show();

                        BuscarRazonSocial     = modallistNFCPuntos.findViewById(R.id.btnBuscadorClienteRZ);
                        btnCancelarLCliente   = modallistNFCPuntos.findViewById(R.id.btnCancelarLCliente);

                        BuscarRazonSocial.setIconifiedByDefault(false);

                        /**
                         * @BUSCAR:NombreClienteRZ
                         */

                        /** Buscador por Razon Social */
                        BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                onQueryTextChange(query);
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                String userInput = newText.toLowerCase();
                                if (lRegistroClientePuntosAdapter != null) {
                                    lRegistroClientePuntosAdapter.filtrado(userInput);
                                }
                                return true;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BuscarRazonSocial.setQuery("", false);
                                modallistNFCPuntos.dismiss();
                            }
                        });

                        return true;
                    }
                });

                if(GlobalInfo.getTerminalLecturar10){
                    inputNFC.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            boolean handled = gestureDetectorNFC.onTouchEvent(event);
                            return handled;
                        }
                    });

                }

                /**
                 * @MODAL:MostrarListadoClienteRUC
                 */
                modalClienteRUC = new Dialog(getContext());
                modalClienteRUC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalClienteRUC.setContentView(R.layout.fragment_clientes);
                modalClienteRUC.setCancelable(false);

                /**
                 * @MODAL:MostrarListadoClienteRUC
                 */
                recyclerLCliente = modalClienteRUC.findViewById(R.id.recyclerLCliente);
                recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));
                ClienteRUC();

                /**
                 * @SELECCIONAR:DobleClick_MostrarListadoClienteRUC
                 */
                GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalClienteRUC.show();

                        btnCancelarLCliente   = modalClienteRUC.findViewById(R.id.btnCancelarLCliente);
                        btnBuscadorClienteRZ  = modalClienteRUC.findViewById(R.id.btnBuscadorClienteRZ);

                        btnBuscadorClienteRZ.setIconifiedByDefault(false);
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
                        boolean handled = gestureDetector.onTouchEvent(event);
                        return handled;
                    }
                });

                /**
                 * @SELECCIONAR:OpciónFormaPago
                 */
                radioFormaPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        radioNombreFormaPago = modalBoleta.findViewById(checkedId);

                        if (checkedId == radioEfectivo.getId()){
                            textMensajePEfectivo.setVisibility(View.VISIBLE);
                            textMensajeCanje.setVisibility(View.GONE);
                            alertSelectTPago.setVisibility(View.GONE);
                            alertOperacion.setVisibility(View.GONE);
                            alertPEfectivo.setVisibility(View.GONE);
                            btnCalcular.setVisibility(View.GONE);
                        } else if (checkedId == radioTarjeta.getId()){
                            textMensajePEfectivo.setVisibility(View.GONE);
                            textMensajeCanje.setVisibility(View.GONE);
                            alertSelectTPago.setVisibility(View.VISIBLE);
                            alertOperacion.setVisibility(View.VISIBLE);
                            alertPEfectivo.setVisibility(View.VISIBLE);
                            btnCalcular.setVisibility(View.GONE);
                        } else if (checkedId == radioCredito.getId()){
                            textMensajePEfectivo.setVisibility(View.GONE);
                            textMensajeCanje.setVisibility(View.GONE);
                            alertSelectTPago.setVisibility(View.GONE);
                            alertOperacion.setVisibility(View.GONE);
                            alertPEfectivo.setVisibility(View.VISIBLE);
                            btnCalcular.setVisibility(View.GONE);
                        } else if (checkedId == radioCanje.getId()){
                            textMensajePEfectivo.setVisibility(View.GONE);
                            textMensajeCanje.setVisibility(View.VISIBLE);
                            alertSelectTPago.setVisibility(View.GONE);
                            alertOperacion.setVisibility(View.GONE);
                            alertPEfectivo.setVisibility(View.GONE);
                            btnCalcular.setVisibility(View.GONE);
                        }
                    }
                });

                radioFormaPago.check(radioEfectivo.getId());

                /**
                 * @SELECCIONAR:OpciónTipoPago
                 */
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

                /**
                 * @BUSCAR:BuscadorRUC
                 */
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

                        findClienteRUC(campoRUC);

                        alertRUC.setErrorEnabled(false);

                        inputPlaca.setText("000-0000");
                        inputNFC.getText().clear();
                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputRUC.setEnabled(true);
                        inputRazSocial.setEnabled(true);
                        alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);

                    }
                });

                /**
                 * @CANCELAR:Factura
                 */
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
                        textNumPuntos.setText(String.valueOf(0));
                        inputOperacion.getText().clear();

                        alertPlaca.setErrorEnabled(false);
                        alertRUC.setErrorEnabled(false);
                        alertRazSocial.setErrorEnabled(false);
                        alertPEfectivo.setErrorEnabled(false);
                        alertOperacion.setErrorEnabled(false);
                    }
                });

                /**
                 * @AGREGAR:Factura
                 */
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

                                String nfc     = inputNFC.getText().toString();

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
                                }  else if (campoRazSocial.length() < 5 ) {

                                    alertRazSocial.setError("* La Razon Social debe tener mínino 5 dígitos");
                                    return;
                                }else if (checkedRadioButtonId == radioTarjeta.getId()) {

                                    if (campoOperacion.isEmpty()) {

                                        alertOperacion.setError("* El campo Nro Operación es obligatorio");
                                        return;
                                    } else if (campoOperacion.length() < 4) {

                                        alertOperacion.setError("* El  Nro Operación debe tener mayor a 4 dígitos");
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

                                detalleVenta.setMtoSaldoCredito(0.00);
                                detalleVenta.setTarjetaND("");
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);
                                detalleVenta.setPtosDisponible(Double.parseDouble(textNumPuntos.getText().toString()));
                                detalleVenta.setRfid("1");


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

                                if(!nfc.isEmpty() && nfc.equals(GlobalInfo.getRfIdCPrecio10)){
                                    detalleVenta.setRfid(GlobalInfo.getRfIdCPrecio10);
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
                                textNumPuntos.setText(String.valueOf(0));
                                radioFormaPago.check(radioEfectivo.getId());
                            }

                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }

                    }
                });

            }

        });

        /**
         * @MODAL:MostrarFormularioNotaDespacho
         */
        modalNotaDespacho = new Dialog(getContext());
        modalNotaDespacho.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalNotaDespacho.setContentView(R.layout.fragment_notadespacho);
        modalNotaDespacho.setCancelable(false);

        btnNotaDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                limpiarCamposBoletaFacturaNDespacho();
                modalNotaDespacho.show();

                btnCancelarNotaDespacho   = modalNotaDespacho.findViewById(R.id.btnCancelarNotaDespacho);
                btnAgregarNotaDespacho    = modalNotaDespacho.findViewById(R.id.btnAgregarNotaDespacho);
                buscarListNFC             = modalNotaDespacho.findViewById(R.id.buscarListNFC);
                buscarListPuntosNFC       = modalNotaDespacho.findViewById(R.id.buscarListPuntosNFC);

                inputCPlaca               = modalNotaDespacho.findViewById(R.id.inputCPlaca);
                input_CNTarjeta           = modalNotaDespacho.findViewById(R.id.input_CNTarjeta);
                inputCCliente             = modalNotaDespacho.findViewById(R.id.inputCCliente);
                inputCRazSocial           = modalNotaDespacho.findViewById(R.id.inputCRazSocial);
                inputCDireccion           = modalNotaDespacho.findViewById(R.id.inputCDireccion);
                inputCKilometraje         = modalNotaDespacho.findViewById(R.id.inputCKilometraje);
                inputCObservacion         = modalNotaDespacho.findViewById(R.id.inputCObservacion);
                inputCMonto               = modalNotaDespacho.findViewById(R.id.inputCMonto);

                inputNFC                  = modalNotaDespacho.findViewById(R.id.input_EtiquetaNFC);

                alertCPlaca               = modalNotaDespacho.findViewById(R.id.alertPlaca);
                alertCTarjeta             = modalNotaDespacho.findViewById(R.id.alertTarjeta);
                alertCCliente             = modalNotaDespacho.findViewById(R.id.alertCCliente);
                alertCRazSocial           = modalNotaDespacho.findViewById(R.id.alertCRazSocial);

                textNumPuntos             = modalNotaDespacho.findViewById(R.id.textNumPuntos);

                inputCCliente.setEnabled(true);

                if(GlobalInfo.getTerminalSoloPuntos10){
                    buscarListNFC.setVisibility(View.GONE);
                    buscarListPuntosNFC.setVisibility(View.VISIBLE);
                }else{
                    buscarListNFC.setVisibility(View.VISIBLE);
                    buscarListPuntosNFC.setVisibility(View.GONE);
                }

                /**
                 * @LOGIN_MODAL:ListaDescuentos_Puntos
                 */
                modalNFCLogin = new Dialog(getContext());
                modalNFCLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalNFCLogin.setContentView(R.layout.modal_nfc_login);
                modalNFCLogin.setCancelable(false);

                buscarListNFC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarModalNFCListaDescuentoPunto();
                    }
                });

                buscarListPuntosNFC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarModalNFCListaDescuentoPunto();
                    }
                });

                /**
                 * @LectorNFC:Descuentos_Puntos
                 */
                inputNFC.setKeyListener(null);
                insertNFC();

                if (inputNFC.getTag() != null) {
                    inputNFC.removeTextChangedListener((TextWatcher) inputNFC.getTag());
                }

                inputNFC.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 16) {
                            String nfcCode = s.toString();

                            if (GlobalInfo.getTerminalSoloPuntos10) {
                                findClientePrecioConPuntosNotaDespacho(nfcCode,GlobalInfo.getterminalCompanyID10);
                            } else {
                                findClientePrecioNotaDespacho(nfcCode, String.valueOf(GlobalInfo.getterminalCompanyID10));
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                modallistNFCPuntos = new Dialog(getContext());
                modallistNFCPuntos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modallistNFCPuntos.setContentView(R.layout.modal_list_puntos_nfc);
                modallistNFCPuntos.setCancelable(false);

                recyclerLClientePuntosNFC = modallistNFCPuntos.findViewById(R.id.recyclerLClientePuntosNFC);
                recyclerLClientePuntosNFC.setLayoutManager(new LinearLayoutManager(getContext()));
                findListaClientePuntos(GlobalInfo.getnfcId10, GlobalInfo.getterminalCompanyID10);

                /**
                 * @SELECCIONAR:DobleClick_MostrarListadoClienteDNI
                 */
                GestureDetector gestureDetectorNFC = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modallistNFCPuntos.show();

                        BuscarRazonSocial     = modallistNFCPuntos.findViewById(R.id.btnBuscadorClienteRZ);
                        btnCancelarLCliente   = modallistNFCPuntos.findViewById(R.id.btnCancelarLCliente);

                        BuscarRazonSocial.setIconifiedByDefault(false);

                        /**
                         * @BUSCAR:NombreClienteRZ
                         */

                        /** Buscador por Razon Social */
                        BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                onQueryTextChange(query);
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                String userInput = newText.toLowerCase();
                                if (lRegistroClientePuntosAdapter != null) {
                                    lRegistroClientePuntosAdapter.filtrado(userInput);
                                }
                                return true;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BuscarRazonSocial.setQuery("", false);
                                modallistNFCPuntos.dismiss();
                            }
                        });

                        return true;
                    }
                });

                if(GlobalInfo.getTerminalLecturar10){
                    inputNFC.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            boolean handled = gestureDetectorNFC.onTouchEvent(event);
                            return handled;
                        }
                    });

                }

                /**
                 * @MODAL:MostrarListadoClienteNotaDespacho
                 */
                modalClienteCredito = new Dialog(getContext());
                modalClienteCredito.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalClienteCredito.setContentView(R.layout.modal_cliente_credito);
                modalClienteCredito.setCancelable(false);

                /**
                 * @MODAL:MostrarListadoClienteNotaDespacho
                 */
                recyclerLClienteCredito = modalClienteCredito.findViewById(R.id.recyclerLClienteCredito);
                recyclerLClienteCredito.setLayoutManager(new LinearLayoutManager(getContext()));
                ClienteCredito();

                /**
                 * @SELECCIONAR:DobleClick_MostrarListadoClienteCredito
                 */
                GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        modalClienteCredito.show();

                        btnCancelarLCliente   = modalClienteCredito.findViewById(R.id.btnCancelarLClienteCredito);
                        btnBuscadorClienteRZ  = modalClienteCredito.findViewById(R.id.btnBuscadorClienteRZ);

                        btnBuscadorClienteRZ.setIconifiedByDefault(false);
                        /** Buscardor por Cliente Raz. Social */
                        btnBuscadorClienteRZ.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                String userInput = newText.toLowerCase();
                                if (clienteCreditoAdapter != null) {
                                    clienteCreditoAdapter.filtrado(userInput);
                                }
                                return false;
                            }
                        });

                        btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnBuscadorClienteRZ.setQuery("", false);
                                modalClienteCredito.dismiss();
                            }
                        });

                        return true;
                    }
                });

                inputCCliente.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        boolean handled = gestureDetector.onTouchEvent(event);
                        return handled;
                    }
                });

                /**
                 * @CANCELAR:NotaDespacho
                 */
                btnCancelarNotaDespacho.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalNotaDespacho.dismiss();

                        inputCPlaca.setText("000-0000");
                        input_CNTarjeta.getText().clear();
                        inputCCliente.getText().clear();
                        inputCRazSocial.getText().clear();
                        inputCDireccion.getText().clear();
                        inputCKilometraje.getText().clear();
                        inputCObservacion.getText().clear();
                        inputCMonto.setText(String.valueOf(0));
                        textNumPuntos.setText(String.valueOf(0));
                        inputNFC.getText().clear();

                        alertCCliente.setErrorEnabled(false);
                        alertCPlaca.setErrorEnabled(false);
                        alertCRazSocial.setErrorEnabled(false);

                    }
                });

                /**
                 * @AGREGAR:NotaDespacho
                 */
                btnAgregarNotaDespacho.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (DetalleVenta detalleVenta : GlobalInfo.getdetalleVentaList10 ) {

                            if (detalleVenta.getCara().equals(GlobalInfo.getCara10)) {

                                String campoNCliente   = inputCCliente.getText().toString();
                                String campoCRazSocial = inputCRazSocial.getText().toString();
                                String campoPlaca      = inputCPlaca.getText().toString();
                                String nfc             = inputNFC.getText().toString();

                                if (campoNCliente.isEmpty()) {

                                    alertCCliente.setError("* Seleccionar Cliente");
                                    return;
                                } else if (campoCRazSocial.isEmpty()) {

                                    alertCRazSocial.setError("* La Razon Social es obligatorio");
                                    return;
                                } else if (campoPlaca.isEmpty()) {

                                    alertCPlaca.setError("* El campo Placa es obligatorio");
                                    return;
                                }

                                alertCPlaca.setErrorEnabled(false);
                                alertCCliente.setErrorEnabled(false);
                                alertCRazSocial.setErrorEnabled(false);

                                detalleVenta.setNroPlaca(inputCPlaca.getText().toString());
                                detalleVenta.setTipoPago("C");
                                detalleVenta.setClienteID(inputCCliente.getText().toString());
                                detalleVenta.setClienteRUC("");
                                detalleVenta.setClienteRS(inputCRazSocial.getText().toString());
                                detalleVenta.setClienteDR(inputCDireccion.getText().toString());
                                detalleVenta.setObservacion(inputCObservacion.getText().toString());
                                detalleVenta.setKilometraje(inputCKilometraje.getText().toString());
                                detalleVenta.setTarjetaND(input_CNTarjeta.getText().toString());
                                detalleVenta.setMtoSaldoCredito(Double.parseDouble(inputCMonto.getText().toString()));
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setMontoSoles(0.00);
                                detalleVenta.setPtosDisponible(Double.parseDouble(textNumPuntos.getText().toString()) );
                                detalleVenta.setRfid("1");


                                if(!nfc.isEmpty() && nfc.equals(GlobalInfo.getRfIdCPrecio10)){
                                    detalleVenta.setRfid(GlobalInfo.getRfIdCPrecio10);
                                }

                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                modalNotaDespacho.dismiss();

                                /** Limpiar el Formulario - Boleta*/
                                inputCPlaca.setText("000-0000");
                                input_CNTarjeta.getText().clear();
                                inputCCliente.getText().clear();
                                inputCRazSocial.getText().clear();
                                inputCDireccion.getText().clear();
                                inputCKilometraje.getText().clear();
                                inputCObservacion.getText().clear();
                                inputCMonto.setText(String.valueOf(0));
                                textNumPuntos.setText(String.valueOf(0));
                                inputNFC.getText().clear();
                            }

                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }

                    }
                });

            }
        });

        /**
         * @MODAL:MostrarFormularioSerafín
         */
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
                                detalleVenta.setTarjetaND("");
                                detalleVenta.setMtoSaldoCredito(0.00);
                                detalleVenta.setOperacionREF("");
                                detalleVenta.setTarjetaCredito("");
                                detalleVenta.setMontoSoles(0.00);
                                detalleVenta.setPtosDisponible(0.00);
                                detalleVenta.setRfid("1");

                                Toast.makeText(getContext(), "SE GENERO SERAFIN", Toast.LENGTH_SHORT).show();
                                modalSerafin.dismiss();

                            }
                            recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

                        }
                    }

                });
            }
        });

        /**
         * @LISTADO:DetalleVentaTransacciones
         */
        recyclerDetalleVenta = view.findViewById(R.id.recyclerDetalleVenta);
        recyclerDetalleVenta.setLayoutManager(new LinearLayoutManager(getContext()));
        DetalleVenta();

        return view;
    }
    /**
     * @ModaLogin_ParaMostarListaDescuentosPuntosNFC
     */
    private void mostrarModalNFCListaDescuentoPunto() {
        modalNFCLogin.show();

        btnCancelarNFC = modalNFCLogin.findViewById(R.id.btnCancelarAnular);
        btnAceptarNFC = modalNFCLogin.findViewById(R.id.btnAceptarIngreso);
        usuarioNFC = modalNFCLogin.findViewById(R.id.inputUserAnulado);
        contraseñaNFC = modalNFCLogin.findViewById(R.id.inputContraseñaAnulado);
        alertuserNFC = modalNFCLogin.findViewById(R.id.alertUserAnulado);
        alertpasswordNFC = modalNFCLogin.findViewById(R.id.alertContraseñaAnulado);

        btnCancelarNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalNFCLogin.dismiss();
                usuarioNFC.getText().clear();
                contraseñaNFC.getText().clear();
                alertuserNFC.setErrorEnabled(false);
                alertpasswordNFC.setErrorEnabled(false);
            }
        });

        btnAceptarNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuarioUserNFC = usuarioNFC.getText().toString();
                contraseñaUserNFC = contraseñaNFC.getText().toString();

                if (usuarioUserNFC.isEmpty()) {
                    alertuserNFC.setError("El campo usuario es obligatorio");
                    return;
                } else if (contraseñaUserNFC.isEmpty()) {
                    alertpasswordNFC.setError("El campo contraseña es obligatorio");
                    return;
                }

                if(GlobalInfo.getTerminalSoloPuntos10){
                    findUsersPuntos(usuarioUserNFC);
                }else{
                    findUsers(usuarioUserNFC);
                }

                alertuserNFC.setErrorEnabled(false);
                alertpasswordNFC.setErrorEnabled(false);
            }
        });
    }

    /**
     * @APISERVICE:UsuarioAutorizado
     */
    private void findUsers(String id){

        Call<List<Users>> call = mAPIService.findUsers(id);

        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usersListNFC = response.body();

                    for (Users user : usersListNFC) {
                        GlobalInfo.getuserIDAnular10 = user.getUserID();
                        GlobalInfo.getuserNameAnular10 = user.getNames();
                        GlobalInfo.getuserPassAnular10 = user.getPassword();
                        GlobalInfo.getuserCancelAnular10 = user.getCancel();
                    }

                    if (GlobalInfo.getuserCancelAnular10 == true) {

                        String getName = usuarioNFC.getText().toString();
                        String getPass = PasswordChecker.checkpassword(contraseñaNFC.getText().toString());

                        /**
                         * @Modal-ListadoClientes-Descuento
                         */
                        modallistNFC = new Dialog(getContext());
                        modallistNFC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modallistNFC.setContentView(R.layout.modal_list_nfc);
                        modallistNFC.setCancelable(false);

                        if (getName.equals(GlobalInfo.getuserNameAnular10) || getPass.equals(GlobalInfo.getuserPassAnular10)) {

                            recyclerListaClientesAfiliados = modallistNFC.findViewById(R.id.recyclerLClienteNFC);
                            recyclerListaClientesAfiliados.setLayoutManager(new LinearLayoutManager(getContext()));

                            BuscarRazonSocial     = modallistNFC.findViewById(R.id.btnBuscadorClienteRZ);
                            btnCancelarLCliente   = modallistNFC.findViewById(R.id.btnCancelarLCliente);

                            BuscarRazonSocial.setIconifiedByDefault(false);

                            btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BuscarRazonSocial.setQuery("", false);
                                    modallistNFC.dismiss();
                                }
                            });

                            /** Buscador por Razon Social */
                            BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    onQueryTextChange(query);
                                    return true;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    String userInput = newText.toLowerCase();
                                    if (lRegistroClienteAdapter != null) {
                                        lRegistroClienteAdapter.filtrado(userInput);
                                    }
                                    return true;
                                }
                            });

                            modalNFCLogin.dismiss();

                            usuarioNFC.getText().clear();
                            contraseñaNFC.getText().clear();

                            /**
                             * @APISERVICE-ListadoCliente-Descuento
                             */
                            findCliente(GlobalInfo.getnfcId10 , String.valueOf(GlobalInfo.getterminalCompanyID10));

                        } else {
                            Toast.makeText(getContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "El usuario se encuentra bloqueado", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Users - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * @APISERVICE:ListaClienteDescuento
     */
    private void findCliente(String nfcId, String comapyId) {
        Call<List<ClientePrecio>> call = mAPIService.findDescuentos(nfcId,comapyId);

        call.enqueue(new Callback<List<ClientePrecio>>() {
            @Override
            public void onResponse(Call<List<ClientePrecio>> call, Response<List<ClientePrecio>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                GlobalInfo.getclientePrecioList10 = response.body();

                modallistNFC.show();

                lRegistroClienteAdapter = new LRegistroClienteAdapter(GlobalInfo.getclientePrecioList10, getContext(), new LRegistroClienteAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ClientePrecio item) {

                        inputNFC.setText(item.getRfid());

                        BuscarRazonSocial.setQuery("", false);

                        modallistNFC.dismiss();

                    }
                });

                recyclerListaClientesAfiliados.setAdapter(lRegistroClienteAdapter);

            }

            @Override
            public void onFailure(Call<List<ClientePrecio>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Lista Cliente - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:UsuarioAutorizadoPuntos
     */
    private void findUsersPuntos(String id){

        Call<List<Users>> call = mAPIService.findUsers(id);

        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usersListNFC = response.body();

                    for (Users user : usersListNFC) {
                        GlobalInfo.getuserIDAnular10 = user.getUserID();
                        GlobalInfo.getuserNameAnular10 = user.getNames();
                        GlobalInfo.getuserPassAnular10 = user.getPassword();
                        GlobalInfo.getuserCancelAnular10 = user.getCancel();
                    }

                    if (GlobalInfo.getuserCancelAnular10 == true) {

                        String getName = usuarioNFC.getText().toString();
                        String getPass = PasswordChecker.checkpassword(contraseñaNFC.getText().toString());

                        /**
                         * @Modal-ListadoClientes-Puntos
                         */
                        modallistNFCPuntos = new Dialog(getContext());
                        modallistNFCPuntos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modallistNFCPuntos.setContentView(R.layout.modal_list_puntos_nfc);
                        modallistNFCPuntos.setCancelable(false);

                        if (getName.equals(GlobalInfo.getuserNameAnular10) || getPass.equals(GlobalInfo.getuserPassAnular10)) {

                            recyclerLClientePuntosNFC = modallistNFCPuntos.findViewById(R.id.recyclerLClientePuntosNFC);
                            recyclerLClientePuntosNFC.setLayoutManager(new LinearLayoutManager(getContext()));

                            BuscarRazonSocial     = modallistNFCPuntos.findViewById(R.id.btnBuscadorClienteRZ);
                            btnCancelarLCliente   = modallistNFCPuntos.findViewById(R.id.btnCancelarLCliente);

                            BuscarRazonSocial.setIconifiedByDefault(false);

                            btnCancelarLCliente.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BuscarRazonSocial.setQuery("", false);
                                    modallistNFCPuntos.dismiss();
                                }
                            });

                            /** Buscador por Razon Social */
                            BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    onQueryTextChange(query);
                                    return true;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    String userInput = newText.toLowerCase();
                                    if (lRegistroClientePuntosAdapter != null) {
                                        lRegistroClientePuntosAdapter.filtrado(userInput);
                                    }
                                    return true;
                                }
                            });

                            modalNFCLogin.dismiss();

                            usuarioNFC.getText().clear();
                            contraseñaNFC.getText().clear();

                            /**
                             * @APISERVICE-ListadoCliente-Descuento
                             */
                            findClientePuntos(GlobalInfo.getnfcId10, GlobalInfo.getterminalCompanyID10);

                        } else {
                            Toast.makeText(getContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "El usuario se encuentra bloqueado", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Users - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     *
     */
    private void findListaClientePuntos(String nfcId,Integer comapyId){
        Call<List<LClientePuntos>> call = mAPIService.findClienteArticulosPuntos(nfcId,comapyId);

        call.enqueue(new Callback<List<LClientePuntos>>() {
            @Override
            public void onResponse(Call<List<LClientePuntos>> call, Response<List<LClientePuntos>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                GlobalInfo.getclientePuntosList10 = response.body();

                lRegistroClientePuntosAdapter = new LRegistroClientePuntosAdapter(GlobalInfo.getclientePuntosList10, getContext(), new LRegistroClientePuntosAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(LClientePuntos item) {

                        inputNFC.setText(item.getRfid());

                        BuscarRazonSocial.setQuery("", false);

                        modallistNFCPuntos.dismiss();

                    }
                });
                recyclerLClientePuntosNFC.setAdapter(lRegistroClientePuntosAdapter);
            }

            @Override
            public void onFailure(Call<List<LClientePuntos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Lista Cliente - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:ListaClientePuntos
     */
    private void findClientePuntos(String nfcId,Integer comapyId) {
        Call<List<LClientePuntos>> call = mAPIService.findClienteArticulosPuntos(nfcId,comapyId);

        call.enqueue(new Callback<List<LClientePuntos>>() {
            @Override
            public void onResponse(Call<List<LClientePuntos>> call, Response<List<LClientePuntos>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                GlobalInfo.getclientePuntosList10 = response.body();

                modallistNFCPuntos.show();

                lRegistroClientePuntosAdapter = new LRegistroClientePuntosAdapter(GlobalInfo.getclientePuntosList10, getContext(), new LRegistroClientePuntosAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(LClientePuntos item) {

                        inputNFC.setText(item.getRfid());

                        BuscarRazonSocial.setQuery("", false);

                        modallistNFCPuntos.dismiss();

                    }
                });
                recyclerLClientePuntosNFC.setAdapter(lRegistroClientePuntosAdapter);
            }

            @Override
            public void onFailure(Call<List<LClientePuntos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Lista Cliente Puntos - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:Lados
     */
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

    /**
     * @APISERVICE:Manguera
     */
    private void Manguera_ByLados(){

        manguerasAdapter = new ManguerasAdapter(GlobalInfo.getmanguerasList10, getContext(), new ManguerasAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Mangueras item) {

                /** Boton Activados */

                if (GlobalInfo.getsettingFuelName10 == null || GlobalInfo.getsettingFuelName10.isEmpty()) {
                    btnLibre.setEnabled(false);
                } else {
                    btnLibre.setEnabled(true);
                }
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

    /**
     * @APISERVICE:ListadoClienteDNI
     */
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

    /**
     * @APISERVICE:ListadoClienteRUC
     */
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

    /**
     * @APISERVICE:ListadoClienteCredito
     */
    private void ClienteCredito(){

        Call<List<ClienteCredito>> call = mAPIService.getClienteCredito();

        call.enqueue(new Callback<List<ClienteCredito>>() {
            @Override
            public void onResponse(Call<List<ClienteCredito>> call, Response<List<ClienteCredito>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesCreditoList10 = response.body();

                    clienteCreditoAdapter = new ClienteCreditoAdapter(GlobalInfo.getlclientesCreditoList10, getContext(), new ClienteCreditoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ClienteCredito item) {

                            inputCPlaca.setText(item.getNroPLaca());
                            input_CNTarjeta.setText(item.getTarjetaID());
                            inputCCliente.setText(item.getClienteID());
                            inputCRazSocial.setText(item.getClienteRZ());
                            inputCDireccion.setText(item.getClienteDR());
                            inputCMonto.setText(String.valueOf(item.getSaldo()));

                            btnBuscadorClienteRZ.setQuery("", false);
                            modalClienteCredito.dismiss();
                        }
                    });
                    recyclerLClienteCredito.setAdapter(clienteCreditoAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ClienteCredito>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Credito - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:ListadoTipoPago
     */
    private void  TipoPago_Doc(){

        Resources res = getResources();
        tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) GlobalInfo.gettipopagoList10, res);
        SpinnerTPago.setAdapter(tipoPagoAdapter);

    }

    /**
     * @APISERVICE:ListadoDetalleVenta
     */
    private void DetalleVenta(){

        detalleVentaAdapter = new DetalleVentaAdapter(GlobalInfo.getdetalleVentaList10, getContext());
        detalleVentaAdapter.notifyDataSetChanged();
        recyclerDetalleVenta.setAdapter(detalleVentaAdapter);

    }

    /**
     * @OBTENER:LectorEtiquetaNFC
     */
    private void  insertNFC(){
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
    }

    /**
     * @APISERVICE:BuscarClienteDNI
     */
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

    /**
     * @APISERVICE:BuscarClienteRUC
     */
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

    /**
     * @APISERVICE:BuscarClienteConPuntos_DNI
     */
    private String findClientePrecioConPuntosNotaDespacho(String nfcId,Integer comapyId) {

        Call<List<LClientePuntos>> call = mAPIService.findClienteArticulosPuntos(nfcId,comapyId);

        call.enqueue(new Callback<List<LClientePuntos>>() {
            @Override
            public void onResponse(Call<List<LClientePuntos>> call, Response<List<LClientePuntos>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clientePuntosList = response.body();

                    if (clientePuntosList != null && !clientePuntosList.isEmpty()) {
                        LClientePuntos clientePuntos = clientePuntosList.get(0);

                        GlobalInfo.getRfIdCPrecio10      = clientePuntos.getRfid();
                        GlobalInfo.getClienteIDPrecio10  = clientePuntos.getClienteID();
                        GlobalInfo.getClienteRZPrecio10  = clientePuntos.getClienteRZ();
                        GlobalInfo.getNroPlacaPrecio10   = clientePuntos.getNroPlaca();
                        GlobalInfo.getStatusPuntos10     = clientePuntos.getStatus();
                        GlobalInfo.getDisponiblePuntos10 = clientePuntos.getDisponibles();
                        GlobalInfo.getNroTarjetasPuntos10 = "70100";

                        if (GlobalInfo.getClienteIDPrecio10.length() == 8 || GlobalInfo.getClienteIDPrecio10.length() == 11  && GlobalInfo.getStatusPuntos10){
                            inputCPlaca.setText(GlobalInfo.getNroPlacaPrecio10);
                            inputCCliente.setText(GlobalInfo.getClienteIDPrecio10);
                            inputCRazSocial.setText(GlobalInfo.getClienteRZPrecio10);
                            inputCDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(GlobalInfo.getDisponiblePuntos10));
                            input_CNTarjeta.setText(GlobalInfo.getNroTarjetasPuntos10);

                            inputCCliente.setEnabled(false);
                            inputCRazSocial.setEnabled(false);

                            alertCCliente.setBoxBackgroundColorResource(R.color.colornew);
                            alertCRazSocial.setBoxBackgroundColorResource(R.color.colornew);

                        }else{
                            Toast.makeText(getContext(), "El NFC esta bloqueado", Toast.LENGTH_SHORT).show();

                            inputNFC.getText().clear();
                            inputCPlaca.setText("000-0000");
                            inputCCliente.getText().clear();
                            inputCRazSocial.getText().clear();
                            inputCDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));
                            input_CNTarjeta.getText().clear();

                            inputCCliente.setEnabled(true);
                            inputCRazSocial.setEnabled(true);
                            alertCCliente.setBoxBackgroundColorResource(R.color.transparentenew);
                            alertCRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                        }

                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del cliente.", Toast.LENGTH_SHORT).show();
                        inputNFC.getText().clear();
                        inputCPlaca.setText("000-0000");
                        inputCCliente.getText().clear();
                        inputCRazSocial.getText().clear();
                        inputCDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));
                        input_CNTarjeta.getText().clear();

                        inputCCliente.setEnabled(true);
                        inputCRazSocial.setEnabled(true);
                        alertCCliente.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertCRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientePuntos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Precio - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        return nfcId;
    }

    /**
     * @APISERVICE:BuscarClienteRFID_NotaDespacho
     */
    private String findClientePrecioNotaDespacho(String rfid, String companyid) {

        Call<List<ClientePrecio>> call = mAPIService.findDescuentos(rfid, companyid);

        call.enqueue(new Callback<List<ClientePrecio>>() {
            @Override
            public void onResponse(Call<List<ClientePrecio>> call, Response<List<ClientePrecio>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clientePrecioList = response.body();

                    if (clientePrecioList != null && !clientePrecioList.isEmpty()) {
                        ClientePrecio clientePrecio = clientePrecioList.get(0);

                        GlobalInfo.getRfIdCPrecio10      = clientePrecio.getRfid();
                        GlobalInfo.getClienteIDPrecio10  = clientePrecio.getClienteID();
                        GlobalInfo.getClienteRZPrecio10  = clientePrecio.getClienteRZ();
                        GlobalInfo.getNroPlacaPrecio10   = clientePrecio.getNroPlaca();
                        GlobalInfo.getArticuloIdPrecio10     = clientePrecio.getArticuloID();
                        GlobalInfo.getTipClientePrecio10     = clientePrecio.getTipoCliente();
                        GlobalInfo.getTipoDescuentoPrecio10  = clientePrecio.getTipoDescuento();
                        GlobalInfo.getMontoDescuentoPrecio10 = clientePrecio.getMontoDescuento();
                        GlobalInfo.getNroTarjetasPuntos10 = "70100";

                        if (GlobalInfo.getClienteIDPrecio10.length() == 8 || GlobalInfo.getClienteIDPrecio10.length() == 11){

                            inputCPlaca.setText(GlobalInfo.getNroPlacaPrecio10);
                            inputCCliente.setText(GlobalInfo.getClienteIDPrecio10);
                            inputCRazSocial.setText(GlobalInfo.getClienteRZPrecio10);
                            inputCDireccion.getText().clear();
                            input_CNTarjeta.setText(GlobalInfo.getNroTarjetasPuntos10);
                            textNumPuntos.setText(String.valueOf(0));

                            inputCCliente.setEnabled(false);
                            inputCRazSocial.setEnabled(false);

                            alertCCliente.setBoxBackgroundColorResource(R.color.colornew);
                            alertCRazSocial.setBoxBackgroundColorResource(R.color.colornew);


                        }else{
                            Toast.makeText(getContext(), "No se encontraron datos el NFC del cliente.", Toast.LENGTH_SHORT).show();

                            inputNFC.getText().clear();
                            inputCPlaca.setText("000-0000");
                            inputCCliente.getText().clear();
                            inputCRazSocial.getText().clear();
                            inputCDireccion.getText().clear();
                            input_CNTarjeta.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputCCliente.setEnabled(true);
                            inputCRazSocial.setEnabled(true);
                            alertCCliente.setBoxBackgroundColorResource(R.color.transparentenew);
                            alertCRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                        }

                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del cliente.", Toast.LENGTH_SHORT).show();
                        inputNFC.getText().clear();
                        inputCPlaca.setText("000-0000");
                        inputCCliente.getText().clear();
                        inputCRazSocial.getText().clear();
                        inputCDireccion.getText().clear();
                        input_CNTarjeta.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputCCliente.setEnabled(true);
                        inputCRazSocial.setEnabled(true);
                        alertCCliente.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertCRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ClientePrecio>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Precio - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        return rfid;
    }

    /**
     * @APISERVICE:BuscarClienteConPuntos_DNI
     */
    private String findClientePrecioConPuntosDNI(String nfcId,Integer comapyId) {

        Call<List<LClientePuntos>> call = mAPIService.findClienteArticulosPuntos(nfcId,comapyId);

        call.enqueue(new Callback<List<LClientePuntos>>() {
            @Override
            public void onResponse(Call<List<LClientePuntos>> call, Response<List<LClientePuntos>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clientePuntosList = response.body();

                    if (clientePuntosList != null && !clientePuntosList.isEmpty()) {
                        LClientePuntos clientePuntos = clientePuntosList.get(0);

                        GlobalInfo.getRfIdCPrecio10      = clientePuntos.getRfid();
                        GlobalInfo.getClienteIDPrecio10  = clientePuntos.getClienteID();
                        GlobalInfo.getClienteRZPrecio10  = clientePuntos.getClienteRZ();
                        GlobalInfo.getNroPlacaPrecio10   = clientePuntos.getNroPlaca();
                        GlobalInfo.getStatusPuntos10     = clientePuntos.getStatus();
                        GlobalInfo.getDisponiblePuntos10 = clientePuntos.getDisponibles();

                        if (GlobalInfo.getClienteIDPrecio10.length() == 8 && GlobalInfo.getStatusPuntos10){
                            inputPlaca.setText(GlobalInfo.getNroPlacaPrecio10);
                            inputDNI.setText(GlobalInfo.getClienteIDPrecio10);
                            inputNombre.setText(GlobalInfo.getClienteRZPrecio10);
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(GlobalInfo.getDisponiblePuntos10));

                            inputDNI.setEnabled(false);
                            inputNombre.setEnabled(false);

                            alertDNI.setBoxBackgroundColorResource(R.color.colornew);
                            alertNombre.setBoxBackgroundColorResource(R.color.colornew);

                        }else{
                            Toast.makeText(getContext(), "El NFC esta registrado con un RUC o esta bloqueado", Toast.LENGTH_SHORT).show();

                            inputNFC.getText().clear();
                            inputPlaca.setText("000-0000");
                            inputDNI.getText().clear();
                            inputNombre.getText().clear();
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputDNI.setEnabled(true);
                            inputNombre.setEnabled(true);
                            alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                            alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                        }

                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del cliente.", Toast.LENGTH_SHORT).show();
                        inputNFC.getText().clear();
                        inputPlaca.setText("000-0000");
                        inputDNI.getText().clear();
                        inputNombre.getText().clear();
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputDNI.setEnabled(true);
                        inputNombre.setEnabled(true);
                        alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientePuntos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Precio - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        return nfcId;
    }

    /**
     * @APISERVICE:BuscarClienteRFID_DNI
     */
    private String findClientePrecioDNI(String rfid, String companyid) {

        Call<List<ClientePrecio>> call = mAPIService.findDescuentos(rfid, companyid);

        call.enqueue(new Callback<List<ClientePrecio>>() {
            @Override
            public void onResponse(Call<List<ClientePrecio>> call, Response<List<ClientePrecio>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clientePrecioList = response.body();

                    if (clientePrecioList != null && !clientePrecioList.isEmpty()) {
                        ClientePrecio clientePrecio = clientePrecioList.get(0);

                        GlobalInfo.getRfIdCPrecio10      = clientePrecio.getRfid();
                        GlobalInfo.getClienteIDPrecio10  = clientePrecio.getClienteID();
                        GlobalInfo.getClienteRZPrecio10  = clientePrecio.getClienteRZ();
                        GlobalInfo.getNroPlacaPrecio10   = clientePrecio.getNroPlaca();
                        GlobalInfo.getArticuloIdPrecio10     = clientePrecio.getArticuloID();
                        GlobalInfo.getTipClientePrecio10     = clientePrecio.getTipoCliente();
                        GlobalInfo.getTipoDescuentoPrecio10  = clientePrecio.getTipoDescuento();
                        GlobalInfo.getMontoDescuentoPrecio10 = clientePrecio.getMontoDescuento();

                        if (GlobalInfo.getClienteIDPrecio10.length() == 8){
                            inputPlaca.setText(GlobalInfo.getNroPlacaPrecio10);
                            inputDNI.setText(GlobalInfo.getClienteIDPrecio10);
                            inputNombre.setText(GlobalInfo.getClienteRZPrecio10);
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputDNI.setEnabled(false);
                            inputNombre.setEnabled(false);

                            alertDNI.setBoxBackgroundColorResource(R.color.colornew);
                            alertNombre.setBoxBackgroundColorResource(R.color.colornew);

                        }else{
                            Toast.makeText(getContext(), "El NFC esta registrado con un RUC", Toast.LENGTH_SHORT).show();

                            inputNFC.getText().clear();
                            inputPlaca.setText("000-0000");
                            inputDNI.getText().clear();
                            inputNombre.getText().clear();
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputDNI.setEnabled(true);
                            inputNombre.setEnabled(true);
                            alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                            alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                        }

                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del cliente.", Toast.LENGTH_SHORT).show();
                        inputNFC.getText().clear();
                        inputPlaca.setText("000-0000");
                        inputDNI.getText().clear();
                        inputNombre.getText().clear();
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputDNI.setEnabled(true);
                        inputNombre.setEnabled(true);
                        alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ClientePrecio>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Precio - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        return rfid;
    }

    /**
     * @APISERVICE:BuscarClienteRFID_RUC
     */
    private String findClientePrecioConPuntosRUC(String nfcId,Integer comapyId) {

        Call<List<LClientePuntos>> call = mAPIService.findClienteArticulosPuntos(nfcId,comapyId);

        call.enqueue(new Callback<List<LClientePuntos>>() {
            @Override
            public void onResponse(Call<List<LClientePuntos>> call, Response<List<LClientePuntos>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clientePuntosList = response.body();

                    if (clientePuntosList != null && !clientePuntosList.isEmpty()) {
                        LClientePuntos clientePuntos = clientePuntosList.get(0);

                        GlobalInfo.getRfIdCPrecio10      = clientePuntos.getRfid();
                        GlobalInfo.getClienteIDPrecio10  = clientePuntos.getClienteID();
                        GlobalInfo.getClienteRZPrecio10  = clientePuntos.getClienteRZ();
                        GlobalInfo.getNroPlacaPrecio10   = clientePuntos.getNroPlaca();
                        GlobalInfo.getStatusPuntos10     = clientePuntos.getStatus();
                        GlobalInfo.getDisponiblePuntos10 = clientePuntos.getDisponibles();

                        if(GlobalInfo.getClienteIDPrecio10.length() == 11 &&  GlobalInfo.getStatusPuntos10){
                            inputPlaca.setText(GlobalInfo.getNroPlacaPrecio10);
                            inputRUC.setText(GlobalInfo.getClienteIDPrecio10);
                            inputRazSocial.setText(GlobalInfo.getClienteRZPrecio10);
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(GlobalInfo.getDisponiblePuntos10));

                            inputRUC.setEnabled(false);
                            inputRazSocial.setEnabled(false);
                            alertRUC.setBoxBackgroundColorResource(R.color.colornew);
                            alertRazSocial.setBoxBackgroundColorResource(R.color.colornew);
                        }else{
                            Toast.makeText(getContext(), "El NFC esta registrado con un DNI o esta bloqueado", Toast.LENGTH_SHORT).show();

                            inputNFC.getText().clear();
                            inputPlaca.setText("000-0000");
                            inputRUC.getText().clear();
                            inputRazSocial.getText().clear();
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputRUC.setEnabled(true);
                            inputRazSocial.setEnabled(true);
                            alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                            alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                        }

                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del cliente.", Toast.LENGTH_SHORT).show();
                        inputNFC.getText().clear();
                        inputPlaca.setText("000-0000");
                        inputRUC.getText().clear();
                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputRUC.setEnabled(true);
                        inputRazSocial.setEnabled(true);
                        alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientePuntos>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Precio - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        return nfcId;
    }

    /**
     * @APISERVICE:BuscarClienteRFID_RUC
     */
    private String findClientePrecioRUC(String rfid, String companyid) {

        Call<List<ClientePrecio>> call = mAPIService.findDescuentos(rfid, companyid);

        call.enqueue(new Callback<List<ClientePrecio>>() {
            @Override
            public void onResponse(Call<List<ClientePrecio>> call, Response<List<ClientePrecio>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clientePrecioList = response.body();

                    if (clientePrecioList != null && !clientePrecioList.isEmpty()) {
                        ClientePrecio clientePrecio = clientePrecioList.get(0);

                        GlobalInfo.getRfIdCPrecio10      = clientePrecio.getRfid();
                        GlobalInfo.getClienteIDPrecio10  = clientePrecio.getClienteID();
                        GlobalInfo.getClienteRZPrecio10  = clientePrecio.getClienteRZ();
                        GlobalInfo.getNroPlacaPrecio10   = clientePrecio.getNroPlaca();

                        GlobalInfo.getArticuloIdPrecio10     = clientePrecio.getArticuloID();
                        GlobalInfo.getTipClientePrecio10     = clientePrecio.getTipoCliente();
                        GlobalInfo.getTipoDescuentoPrecio10  = clientePrecio.getTipoDescuento();
                        GlobalInfo.getMontoDescuentoPrecio10 = clientePrecio.getMontoDescuento();

                        if(GlobalInfo.getClienteIDPrecio10.length() == 11){
                            inputPlaca.setText(GlobalInfo.getNroPlacaPrecio10);
                            inputRUC.setText(GlobalInfo.getClienteIDPrecio10);
                            inputRazSocial.setText(GlobalInfo.getClienteRZPrecio10);
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputRUC.setEnabled(false);
                            inputRazSocial.setEnabled(false);
                            alertRUC.setBoxBackgroundColorResource(R.color.colornew);
                            alertRazSocial.setBoxBackgroundColorResource(R.color.colornew);
                        }else{
                            Toast.makeText(getContext(), "El NFC esta registrado con un DNI", Toast.LENGTH_SHORT).show();

                            inputNFC.getText().clear();
                            inputPlaca.setText("000-0000");
                            inputRUC.getText().clear();
                            inputRazSocial.getText().clear();
                            inputDireccion.getText().clear();
                            textNumPuntos.setText(String.valueOf(0));

                            inputRUC.setEnabled(true);
                            inputRazSocial.setEnabled(true);
                            alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                            alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                        }

                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del cliente.", Toast.LENGTH_SHORT).show();
                        inputNFC.getText().clear();
                        inputPlaca.setText("000-0000");
                        inputRUC.getText().clear();
                        inputRazSocial.getText().clear();
                        inputDireccion.getText().clear();
                        textNumPuntos.setText(String.valueOf(0));

                        inputRUC.setEnabled(true);
                        inputRazSocial.setEnabled(true);
                        alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ClientePrecio>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Cliente Precio - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        return rfid;
    }

    /**
     * @LIMPIAR:CamposBoletaFactura
     */
    private void limpiarCamposBoletaFacturaNDespacho() {

        TextInputEditText inputNFCBoleta    = modalBoleta.findViewById(R.id.input_EtiquetaNFC);
        TextInputEditText inputPlaca        = modalBoleta.findViewById(R.id.inputPlaca);
        TextInputEditText inputDNI          = modalBoleta.findViewById(R.id.inputDNI);
        TextInputEditText inputNombre       = modalBoleta.findViewById(R.id.inputNombre);
        TextView textNumPuntosB              = modalBoleta.findViewById(R.id.textNumPuntos);

        TextInputEditText inputNFCFactura   = modalFactura.findViewById(R.id.input_EtiquetaNFC);
        TextInputEditText inputPlacaF       = modalFactura.findViewById(R.id.inputPlaca);
        TextInputEditText inputRUC          = modalFactura.findViewById(R.id.inputRUC);
        TextInputEditText inputRZ           = modalFactura.findViewById(R.id.inputRazSocial);
        TextView textNumPuntosF             = modalFactura.findViewById(R.id.textNumPuntos);

        TextInputEditText inputNFCNotaDespacho = modalNotaDespacho.findViewById(R.id.input_EtiquetaNFC);
        TextInputEditText inputPlacaND         = modalNotaDespacho.findViewById(R.id.inputCPlaca);
        TextInputEditText inputIDClienteND     = modalNotaDespacho.findViewById(R.id.inputCCliente);
        TextInputEditText inputRZND            = modalNotaDespacho.findViewById(R.id.inputCRazSocial);
        TextView inputNTarjeta                 = modalNotaDespacho.findViewById(R.id.input_CNTarjeta);
        TextView textNumPuntosND               = modalNotaDespacho.findViewById(R.id.textNumPuntos);

        if (inputNFCBoleta != null) {
            inputNFCBoleta.setText("");
            inputPlaca.setText("000-0000");
            inputDNI.setText("");
            inputNombre.setText("");
            textNumPuntosB.setText("0");
        }

        if (inputNFCFactura != null) {
            inputNFCFactura.setText("");
            inputPlacaF.setText("000-0000");
            inputRUC.setText("");
            inputRZ.setText("");
            textNumPuntosF.setText("0");
        }

        if (inputNFCNotaDespacho != null) {
            inputNFCNotaDespacho.setText("");
            inputPlacaND.setText("000-0000");
            inputIDClienteND.setText("");
            inputRZND.setText("");
            inputNTarjeta.setText("");
            textNumPuntosND.setText("0");
        }
    }

    /**
     * @ESTADO:BotonAutomatico_Timer
     */
    private void modoAutomatico() {

        if (!mIsTaskScheduled) {

            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }

            timer = new Timer();
            insertarDespacho();

            if (timerTask != null) {

                long delay = Long.parseLong(GlobalInfo.getTerminaltimerAppVenta10);
                timer.schedule(timerTask, delay, delay);
                mIsTaskScheduled = true;

                mTimerRunning = true;
                btnAutomatico.setText("Automático");
                btnAutomatico.setBackgroundColor(Color.parseColor("#001E8A"));

                btnListadoComprobante.setEnabled(false);
                btncarritocompra.setEnabled(false);
                btnLimpiarLado.setEnabled(false);

            } else {
                modoStop();
            }
        }

    }

    /**
     * @INSERTAR:OptranVentasPendientes
     */
    private void insertarDespacho() {

        Context context = requireContext();

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            timerTask = new TimerTask() {
                public void run() {

                    Context context = requireContext();

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        OptranProcesar(GlobalInfo.getterminalImei10);
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                modal_ErrorWifi.show();
                                btnAceptarErrorWifi = modal_ErrorWifi.findViewById(R.id.btnAceptarWifi);
                                btnAceptarErrorWifi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        modal_ErrorWifi.dismiss();
                                    }
                                });
                                modoStop();
                            }
                        });
                    }
                }
            };
        }else{
            modal_ErrorWifi.show();
            btnAceptarErrorWifi   = modal_ErrorWifi.findViewById(R.id.btnAceptarWifi);
            btnAceptarErrorWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modal_ErrorWifi.dismiss();
                }
            });
            modoStop();
        }

    }

    /**
     * @ESTADO:BotonStop_Timer
     */
    private void modoStop() {

        if (mIsTaskScheduled) {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            mIsTaskScheduled = false;
        }

        mTimerRunning = false;
        btnAutomatico.setText("Stop");
        btnAutomatico.setBackgroundColor(Color.parseColor("#dc3545"));

        btnListadoComprobante.setEnabled(true);
        btncarritocompra.setEnabled(true);
        btnLimpiarLado.setEnabled(true);

    }

    /**
     * @GUARDAR:Datos_Libre
     */
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

    /**
     * @GUARDAR:Datos_Soles
     */
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
                Toast.makeText(getContext(), "Error de conexión APICORE Soles", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * @GUARDAR:Datos_Galones
     */
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
                Toast.makeText(getContext(), "Error de conexión APICORE Galones", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * @APISERVICE:OptranProcesar  New 07/09/2023
     */
    private void OptranProcesar(String imei){

        Call<List<Optran>> call = mAPIService.findOptran(imei);

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

                        /** Variables de Detalle Venta **/
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
                        String mnRFID     = "1";

                        /** Fin **/

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
                                mnRFID            = detalleVenta.getRfid().toString();

                                break;
                            }

                        }

                        /** Consultando datos del DOCUMENTO **/
                        String mnTipoVenta = "V";
                        Integer mnPagoID = 1;
                        Integer mnTarjetaCreditoID = 0;
                        String mnobservacionPag = "CONTADO";
                        String mnTipoDocumento = "";

                        /** 11-01-2024**/
                        /** 3.- MONTOS MENORES A 0.10 QUE SE GENERE NOTA DE DESPACHO **/

                        if (GlobalInfo.getoptranSoles10 < GlobalInfo.getsettingFuelMontoMinimo10) {
                            mnTipoPago = "C";
                            mnClienteID = "11111111";
                            mnClienteRUC = "";
                            mnClienteRS = "CLIENTE VARIOS";
                            mnCliernteDR = "";
                            mnNroPlaca = "000-0000";
                            mnTarjND = "70100";
                            mnTarjetaCredito = "0";
                            mnOperacionREF = "";
                            mnMontoSoles = 0.00;
                        }

                        if (mnClienteID.length() == 0 && mnClienteRUC.length() == 0   && GlobalInfo.getterminalNDespacho == true && !mnTipoPago.equals("S")) {
                            mnTipoPago = "C";
                            mnClienteID = "11111111";
                            mnClienteRUC = "";
                            mnClienteRS = "CLIENTE VARIOS";
                            mnCliernteDR = "";
                            mnNroPlaca = "000-0000";
                            mnTarjND = "70100";
                            mnTarjetaCredito = "0";
                            mnOperacionREF = "";
                            mnMontoSoles = 0.00;
                        }

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
                                mnTipoDocumento = "99";
                                if(mnTarjND == ""){
                                    if (mnClienteRUC.length() == 11) {
                                        mnClienteID = mnClienteRUC;
                                        mnTipoDocumento = "01";
                                    } else if (mnClienteRUC.length() == 0) {
                                        mnTipoDocumento = "03";
                                    }
                                }
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
                                mnNroPlaca = "";
                                break;
                        }



                        if (mnClienteID.length() == 0 && mnTipoDocumento == "03") {
                            mnClienteID = "11111111";
                            mnClienteRUC = "";
                            mnClienteRS = "CLIENTE VARIOS";
                            mnCliernteDR = "";
                            mnNroPlaca = "000-0000";
                        }

                        /** 4.- VALIDAR BOLETA MAYORES A 700 SOLES **/

                        if (mnClienteID.equals(GlobalInfo.getsettingClienteID10)  && GlobalInfo.getoptranSoles10 >= GlobalInfo.getsettingDNIMontoMinimo10 && mnTipoDocumento.equals("03")) {
                            Toast.makeText(getContext(), "Por montos mayores a 700.00 soles debe ingresar el DNI del cliente", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        /**11-01-2024**/
                        /** 1.- NOTAS DE DESPACHO A CLIENTES VARIOS ====> DEBE SER CONTADO **/
                        if (mnClienteID.equals(GlobalInfo.getsettingClienteID10)  && mnTipoDocumento.equals("99")) {
                            mnobservacionPag = "CONTADO";
                        }

                        /** Generamos correlativo para grabar **/

                        GlobalInfo.getcorrelativoMDescuento = 0.00;

                        if (mnRFID.equals("1")) {

                            /** Cuando es 0 no es necesario leer RFID **/
                            if (GlobalInfo.getsettingDescuentoRFID10 == 0) {

                                findCorrelativoSINRFID(GlobalInfo.getterminalImei10, mnTipoDocumento, mnClienteID,
                                        mnClienteRUC, mnClienteRS, mnCliernteDR,
                                        mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion,
                                        mnTarjND, mnTarjetaPuntos, mnPtosDisponibles,
                                        mnPagoID, mnTarjetaCreditoID, mnOperacionREF,
                                        mnobservacionPag, GlobalInfo.getoptranOperador10,
                                        mnImpuesto, mnMontoSoles, mnMtoSaldoCredito, mnRFID,
                                        GlobalInfo.getoptranSoles10, GlobalInfo.getoptranArticuloID10,
                                        GlobalInfo.getoptranGalones10, String.valueOf(GlobalInfo.getoptranTranID10));

                            } else {

                                findCorrelativoCPE(GlobalInfo.getterminalImei10, mnTipoDocumento, mnClienteID,
                                        mnClienteRUC, mnClienteRS, mnCliernteDR,
                                        mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion,
                                        mnTarjND, mnTarjetaPuntos, mnPtosDisponibles,
                                        mnPagoID, mnTarjetaCreditoID, mnOperacionREF,
                                        mnobservacionPag, GlobalInfo.getoptranOperador10,
                                        mnImpuesto, mnMontoSoles, mnMtoSaldoCredito, mnRFID,
                                        GlobalInfo.getoptranSoles10, GlobalInfo.getoptranArticuloID10,
                                        GlobalInfo.getoptranGalones10, String.valueOf(GlobalInfo.getoptranTranID10));

                            }


                        } else {

                            findCorrelativoCPE(GlobalInfo.getterminalImei10, mnTipoDocumento, mnClienteID,
                                    mnClienteRUC, mnClienteRS, mnCliernteDR,
                                    mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion,
                                    mnTarjND, mnTarjetaPuntos, mnPtosDisponibles,
                                    mnPagoID, mnTarjetaCreditoID, mnOperacionREF,
                                    mnobservacionPag, GlobalInfo.getoptranOperador10,
                                    mnImpuesto, mnMontoSoles, mnMtoSaldoCredito, mnRFID,
                                    GlobalInfo.getoptranSoles10, GlobalInfo.getoptranArticuloID10,
                                    GlobalInfo.getoptranGalones10, String.valueOf(GlobalInfo.getoptranTranID10));

                        }

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
                                detalleVenta.setRfid("1");

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
                modoStop();
            }
        });

    }

    /**
     * @APISERVICE:GenerarCorrelativoComprobante Sin RFID
     */
    private void findCorrelativoSINRFID(String imei, String mnTipoDocumento, String mnClienteID, String mnClienteRUC, String mnClienteRS, String mnCliernteDR,
                                        String mnNroPlaca, String mnKilometraje, String mnTipoVenta, String mnObservacion,
                                        String mnTarjND, String mnTarjetaPuntos, Double mnPtosDisponibles,
                                        Integer mnPagoID, Integer mnTarjetaCreditoID, String mnOperacionREF,
                                        String mnobservacionPag, String mnOperador, Double mnImpuesto,
                                        Double mnMontoSoles, Double mnMtoSaldoCredito, String mnRFID,
                                        Double mnMtoTotal, String mnArticuloID, Double mnCantidad, String mnTranID) {

        Call<List<Correlativo>> call = mAPIService.findCorrelativosinrfid(imei, mnTipoDocumento, mnClienteID, mnArticuloID, mnTranID);

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
                        GlobalInfo.getcorrelativoFecha      = String.valueOf(correlativo.getFechaProceso());
                        GlobalInfo.getcorrelativoSerie      = String.valueOf(correlativo.getSerie());
                        GlobalInfo.getcorrelativoNumero     = String.valueOf(correlativo.getNumero());
                        GlobalInfo.getcorrelativoMDescuento = correlativo.getMontoDescuento();
                        GlobalInfo.getcorrelativoDocumentoVenta = correlativo.getDocumentoVenta();
                        GlobalInfo.getcorrelativoTipoDesc       = correlativo.getTipoDescuento();
                        GlobalInfo.getcorrelativoPuntosGanados  = correlativo.getPuntosGanados();
                        GlobalInfo.getcorrelativoPuntosDisponibles  = correlativo.getPuntosDisponibles();
                    }

                    if(!GlobalInfo.getcorrelativoDocumentoVenta.isEmpty()){
                        return;
                    }

                    /** Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    String GRFecProceso = GlobalInfo.getcorrelativoFecha;
                    String GRNumeroSerie = GlobalInfo.getcorrelativoSerie;
                    String GRNumeroDocumento = GlobalInfo.getcorrelativoNumero;
                    Double GRPuntosGanados =  GlobalInfo.getcorrelativoPuntosGanados;
                    Double GRPuntosDisponibles =  GlobalInfo.getcorrelativoPuntosDisponibles;
                    String mnTarjetaPuntos = mnRFID;

                    String NroComprobante = GlobalInfo.getcorrelativoSerie + "-" + GlobalInfo.getcorrelativoNumero;

                    /** Fecha de Impresión */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());
                    String xFechaDocumento       = xFechaHoraImpresion.substring(6,10) + xFechaHoraImpresion.substring(3,5) + xFechaHoraImpresion.substring(0,2) + " " + xFechaHoraImpresion.substring(11,19);
                    String xFechaDocumentoQR     = xFechaHoraImpresion.substring(6,10) + "-" + xFechaHoraImpresion.substring(3,5) + "-" + xFechaHoraImpresion.substring(0,2);

                    /** FIN Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    /** Calculando los totales **/

                    Double mnPrecioOrig = 0.00;
                    Double mnMtoPagar = 0.00;
                    Double mnMtoCanje = 0.00;
                    Double mnmtoPagoUSD = 0.00;
                    Double mnPtosGanados = 0.00;

                    Double mnMtoDescuento0 = 0.00;
                    Double mnMtoDescuento1 = 0.00;
                    String mnMtoDescuento2 = "";

                    Double mnMtoIncremento0 = 0.00;
                    Double mnMtoIncremento1 = 0.00;
                    String mnMtoIncremento2 = "";

                    Double mnMtoSubTotal0 = 0.00;
                    Double mnMtoSubTotal1 = 0.00;
                    String mnMtoSubTotal2 = "";

                    Double mnMtoImpuesto0 = 0.00;
                    Double mnMtoImpuesto1 = 0.00;
                    String mnMtoImpuesto2 = "";

                    Integer mnItem = 1;
                    Double mnFise = 0.00;
                    String mnobservacionDet = "";
                    String mnReferencia = "";

                    mnPrecioOrig = GlobalInfo.getoptranPrecio10;

                    mnMtoCanje = mnMtoTotal;
                    mnMtoPagar = mnMtoTotal;

                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                    Double mnMtoDescuentoUnitario = GlobalInfo.getcorrelativoMDescuento;

                    if (mnMtoDescuentoUnitario != 0 && mnCantidad >= 1) {

                        /* MONTO DESCUENTO POR GALON (DES) */
                        if (GlobalInfo.getcorrelativoTipoDesc.equals("DES")) {
                            mnMtoDescuento0 = mnMtoDescuentoUnitario * mnCantidad;
                            mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                            mnMtoPagar = mnMtoTotal - mnMtoDescuento1;
                        } else {
                            /* PRECIO FIJO POR GALON (PRE) */

                            if (mnMtoDescuentoUnitario > mnPrecioOrig) {
                                /* PRECIO CON INCREMENTO */
                                GlobalInfo.getoptranPrecio10 = mnMtoDescuentoUnitario;
                                mnMtoDescuento0 = mnMtoDescuentoUnitario * mnCantidad;
                                mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                mnMtoPagar = mnMtoDescuento1;
                                mnMtoDescuento1 = 0.00;

                                mnMtoIncremento0 = mnMtoPagar - mnMtoTotal;
                                mnMtoIncremento1 = Math.round(mnMtoIncremento0*100.0)/100.0;

                            } else if (mnMtoDescuentoUnitario < mnPrecioOrig) {
                                /* PRECIO CON DESCUENTO */
                                mnMtoDescuentoUnitario = mnPrecioOrig - mnMtoDescuentoUnitario;
                                mnMtoDescuento0 = mnMtoDescuentoUnitario * mnCantidad;
                                mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                mnMtoPagar = mnMtoTotal - mnMtoDescuento1;
                            }

                        }

                        mnMtoSubTotal0 = mnMtoPagar / 1.18;
                        mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                        mnMtoImpuesto0 = mnMtoPagar - mnMtoSubTotal1;
                        mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                    }

                    /** FIN Calculando los totales **/

                    if(!GlobalInfo.getTerminalSoloPuntos10){
                        GRPuntosGanados = 0.00;
                        GRPuntosDisponibles = 0.00;
                    }

                    /** GRABAR VENTA EN BASE DE DATOS **/

                    grabarVentaCA(GlobalInfo.getterminalCompanyID10, mnTipoDocumento, GRNumeroSerie, GRNumeroDocumento,
                            GlobalInfo.getterminalID10, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                            GlobalInfo.getterminalTurno10, GlobalInfo.getcorrelativoFecha, xFechaDocumento,
                            GlobalInfo.getoptranFechaTran10,
                            mnMtoDescuento1, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoPagar,
                            mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                            mnTarjND, mnTarjetaPuntos, GRPuntosGanados, GRPuntosDisponibles,
                            mnMtoCanje, GlobalInfo.getuserID10,
                            mnItem, mnArticuloID, GlobalInfo.getoptranProductoDs10, GlobalInfo.getoptranUniMed10, GlobalInfo.getterminalAlmacenID10,
                            GlobalInfo.getsettingImpuestoID110, GlobalInfo.getsettingImpuestoValor110, GlobalInfo.getoptranPrecio10, mnPrecioOrig, mnCantidad,
                            mnFise, GlobalInfo.getoptranTranID10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranManguera10,
                            mnobservacionDet,
                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoPagar, mnMontoSoles, mnobservacionPag,
                            mnOperador, mnMtoIncremento1);

                    /** FIN GRABAR VENTA EN BASE DE DATOS **/

                    /** IMPRESION DEL COMPROBANTE **/

                    /** 11-01-2024**/
                    /** 2.- PARAMETRO PARA IMPRIMIR COMPROBANTES A CLIENTES VARIOS (TRUE O FALSE) **/

                    boolean flagprinter = true;

                    if (mnClienteID.equals(GlobalInfo.getsettingClienteID10) && GlobalInfo.getterminalCvariosPrinter10 == false) {
                        flagprinter = false;
                    }

                    if (flagprinter == true) {

                        if (mnPagoID == 2) {

                            /** @IMPRESION01 */
                            imprimirGR10(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                    GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10,
                                    GlobalInfo.getoptranUniMed10, GlobalInfo.getoptranPrecio10, mnCantidad,
                                    mnMtoPagar, mnMtoSubTotal1, mnMtoImpuesto1,
                                    mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                    mnKilometraje, mnObservacion, mnTarjND, xFechaDocumentoQR,
                                    mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoCanje, mnMtoDescuento1, mnMontoSoles,mnTarjetaPuntos,GRPuntosGanados,GRPuntosDisponibles);


                            Double finalMnMtoPagar1 = mnMtoPagar;
                            Double finalMnMtoSubTotal1 = mnMtoSubTotal1;
                            Double finalMnMtoImpuesto1 = mnMtoImpuesto1;
                            Double finalMnMtoCanje1 = mnMtoCanje;
                            Double finalMnMtoDescuento1 = mnMtoDescuento1;

                            long delay = 3000;

                            if( GlobalInfo.getTipoPapel10.equals("58mm")){
                                delay = 6000;
                            }

                            Timer timerS = new Timer();

                            Double finalGRPuntosGanados = GRPuntosGanados;
                            Double finalGRPuntosDisponibles = GRPuntosDisponibles;
                            /** @IMPRESION02 */
                            timerS.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    imprimirGR10(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                            GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10,
                                            GlobalInfo.getoptranUniMed10, GlobalInfo.getoptranPrecio10, mnCantidad,
                                            finalMnMtoPagar1, finalMnMtoSubTotal1, finalMnMtoImpuesto1,
                                            mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                            mnKilometraje, mnObservacion, mnTarjND, xFechaDocumentoQR,
                                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, finalMnMtoCanje1, finalMnMtoDescuento1, mnMontoSoles,mnTarjetaPuntos,finalGRPuntosGanados,finalGRPuntosDisponibles);

                                    timerS.cancel();
                                }
                            }, delay);

                        } else {

                            imprimirGR10(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                    GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10,
                                    GlobalInfo.getoptranUniMed10, GlobalInfo.getoptranPrecio10, mnCantidad,
                                    mnMtoPagar, mnMtoSubTotal1, mnMtoImpuesto1,
                                    mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                    mnKilometraje, mnObservacion, mnTarjND, xFechaDocumentoQR,
                                    mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoCanje, mnMtoDescuento1, mnMontoSoles,mnTarjetaPuntos,GRPuntosGanados,GRPuntosDisponibles);

                        }

                    }

                    /** FIN IMPRESION DEL COMPROBANTE*/

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

    /**
     * @APISERVICE:GenerarCorrelativoComprobante
     */
    private void findCorrelativoCPE(String imei, String mnTipoDocumento, String mnClienteID, String mnClienteRUC, String mnClienteRS, String mnCliernteDR,
                                    String mnNroPlaca, String mnKilometraje, String mnTipoVenta, String mnObservacion,
                                    String mnTarjND, String mnTarjetaPuntos, Double mnPtosDisponibles,
                                    Integer mnPagoID, Integer mnTarjetaCreditoID, String mnOperacionREF,
                                    String mnobservacionPag, String mnOperador, Double mnImpuesto,
                                    Double mnMontoSoles, Double mnMtoSaldoCredito, String mnRFID,
                                    Double mnMtoTotal, String mnArticuloID, Double mnCantidad, String mnTranID) {

        Call<List<Correlativo>> call = mAPIService.findCorrelativo(imei, mnTipoDocumento, mnRFID, mnArticuloID, mnTranID);

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
                        GlobalInfo.getcorrelativoFecha      = String.valueOf(correlativo.getFechaProceso());
                        GlobalInfo.getcorrelativoSerie      = String.valueOf(correlativo.getSerie());
                        GlobalInfo.getcorrelativoNumero     = String.valueOf(correlativo.getNumero());
                        GlobalInfo.getcorrelativoMDescuento = correlativo.getMontoDescuento();
                        GlobalInfo.getcorrelativoDocumentoVenta = correlativo.getDocumentoVenta();
                        GlobalInfo.getcorrelativoTipoDesc       = correlativo.getTipoDescuento();
                        GlobalInfo.getcorrelativoPuntosGanados  = correlativo.getPuntosGanados();
                        GlobalInfo.getcorrelativoPuntosDisponibles  = correlativo.getPuntosDisponibles();
                    }

                    if(!GlobalInfo.getcorrelativoDocumentoVenta.isEmpty()){
                        return;
                    }

                    /** Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    String GRFecProceso = GlobalInfo.getcorrelativoFecha;
                    String GRNumeroSerie = GlobalInfo.getcorrelativoSerie;
                    String GRNumeroDocumento = GlobalInfo.getcorrelativoNumero;
                    Double GRPuntosGanados =  GlobalInfo.getcorrelativoPuntosGanados;
                    Double GRPuntosDisponibles =  GlobalInfo.getcorrelativoPuntosDisponibles;
                    String mnTarjetaPuntos = mnRFID;

                    String NroComprobante = GlobalInfo.getcorrelativoSerie + "-" + GlobalInfo.getcorrelativoNumero;

                    /** Fecha de Impresión - Documento */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());
                    String xFechaDocumento       = xFechaHoraImpresion.substring(6,10) + xFechaHoraImpresion.substring(3,5) + xFechaHoraImpresion.substring(0,2) + " " + xFechaHoraImpresion.substring(11,19);
                    String xFechaDocumentoQR     = xFechaHoraImpresion.substring(6,10) + "-" + xFechaHoraImpresion.substring(3,5) + "-" + xFechaHoraImpresion.substring(0,2);

                    /** FIN Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    /** Calculando los totales **/

                    Double mnPrecioOrig = 0.00;
                    Double mnMtoPagar = 0.00;
                    Double mnMtoCanje = 0.00;
                    Double mnmtoPagoUSD = 0.00;
                    Double mnPtosGanados = 0.00;

                    Double mnMtoDescuento0 = 0.00;
                    Double mnMtoDescuento1 = 0.00;
                    String mnMtoDescuento2 = "";

                    Double mnMtoIncremento0 = 0.00;
                    Double mnMtoIncremento1 = 0.00;
                    String mnMtoIncremento2 = "";

                    Double mnMtoSubTotal0 = 0.00;
                    Double mnMtoSubTotal1 = 0.00;
                    String mnMtoSubTotal2 = "";

                    Double mnMtoImpuesto0 = 0.00;
                    Double mnMtoImpuesto1 = 0.00;
                    String mnMtoImpuesto2 = "";

                    Integer mnItem = 1;
                    Double mnFise = 0.00;
                    String mnobservacionDet = "";
                    String mnReferencia = "";

                    mnPrecioOrig = GlobalInfo.getoptranPrecio10;

                    mnMtoCanje = mnMtoTotal;
                    mnMtoPagar = mnMtoTotal;

                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                    Double mnMtoDescuentoUnitario = GlobalInfo.getcorrelativoMDescuento;

                    if (mnMtoDescuentoUnitario != 0 && mnCantidad >= 1) {

                        /* MONTO DESCUENTO POR GALON (DES) */
                        if (GlobalInfo.getcorrelativoTipoDesc.equals("DES")) {
                            mnMtoDescuento0 = mnMtoDescuentoUnitario * mnCantidad;
                            mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                            mnMtoPagar = mnMtoTotal - mnMtoDescuento1;
                        } else {
                            /* PRECIO FIJO POR GALON (PRE) */

                            if (mnMtoDescuentoUnitario > mnPrecioOrig) {
                                /* PRECIO CON INCREMENTO */
                                GlobalInfo.getoptranPrecio10 = mnMtoDescuentoUnitario;
                                mnMtoDescuento0 = mnMtoDescuentoUnitario * mnCantidad;
                                mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                mnMtoPagar = mnMtoDescuento1;
                                mnMtoDescuento1 = 0.00;

                                mnMtoIncremento0 = mnMtoPagar - mnMtoTotal;
                                mnMtoIncremento1 = Math.round(mnMtoIncremento0*100.0)/100.0;

                            } else if (mnMtoDescuentoUnitario < mnPrecioOrig) {
                                /* PRECIO CON DESCUENTO */
                                mnMtoDescuentoUnitario = mnPrecioOrig - mnMtoDescuentoUnitario;
                                mnMtoDescuento0 = mnMtoDescuentoUnitario * mnCantidad;
                                mnMtoDescuento1 = Math.round(mnMtoDescuento0*100.0)/100.0;
                                mnMtoPagar = mnMtoTotal - mnMtoDescuento1;
                            }

                        }

                        mnMtoSubTotal0 = mnMtoPagar / 1.18;
                        mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;
                        mnMtoImpuesto0 = mnMtoPagar - mnMtoSubTotal1;
                        mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                    }

                    /** FIN Calculando los totales **/

                    /** GRABAR VENTA EN BASE DE DATOS **/

                    if(!GlobalInfo.getTerminalSoloPuntos10){
                        GRPuntosGanados = 0.00;
                        GRPuntosDisponibles = 0.00;
                    }

                    grabarVentaCA(GlobalInfo.getterminalCompanyID10, mnTipoDocumento, GRNumeroSerie, GRNumeroDocumento,
                            GlobalInfo.getterminalID10, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                            GlobalInfo.getterminalTurno10, GlobalInfo.getcorrelativoFecha, xFechaDocumento,
                            GlobalInfo.getoptranFechaTran10,
                            mnMtoDescuento1, mnMtoSubTotal1, mnMtoImpuesto1, mnMtoPagar,
                            mnNroPlaca, mnKilometraje, mnTipoVenta, mnObservacion, mnReferencia,
                            mnTarjND, mnTarjetaPuntos, GRPuntosGanados, GRPuntosDisponibles,
                            mnMtoCanje, GlobalInfo.getuserID10,
                            mnItem, mnArticuloID, GlobalInfo.getoptranProductoDs10, GlobalInfo.getoptranUniMed10, GlobalInfo.getterminalAlmacenID10,
                            GlobalInfo.getsettingImpuestoID110, GlobalInfo.getsettingImpuestoValor110, GlobalInfo.getoptranPrecio10, mnPrecioOrig, mnCantidad,
                            mnFise, GlobalInfo.getoptranTranID10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranManguera10,
                            mnobservacionDet,
                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoPagar, mnMontoSoles, mnobservacionPag,
                            mnOperador, mnMtoIncremento1);

                    /** FIN GRABAR VENTA EN BASE DE DATOS **/

                    /** IMPRESION DEL COMPROBANTE **/

                    /** 11-01-2024**/
                    /** 2.- PARAMETRO PARA IMPRIMIR COMPROBANTES A CLIENTES VARIOS (TRUE O FALSE) **/

                    boolean flagprinter = true;

                    if (mnClienteID.equals(GlobalInfo.getsettingClienteID10) && GlobalInfo.getterminalCvariosPrinter10 == false) {
                        flagprinter = false;
                    }

                    if (flagprinter == true) {

                        if (mnPagoID == 2) {

                            /** @IMPRESION01 */
                            imprimirGR10(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                    GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10,
                                    GlobalInfo.getoptranUniMed10, GlobalInfo.getoptranPrecio10, mnCantidad,
                                    mnMtoPagar, mnMtoSubTotal1, mnMtoImpuesto1,
                                    mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                    mnKilometraje, mnObservacion, mnTarjND, xFechaDocumentoQR,
                                    mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoCanje, mnMtoDescuento1, mnMontoSoles,mnTarjetaPuntos,GRPuntosGanados,GRPuntosDisponibles);

                            Double finalMnMtoPagar1 = mnMtoPagar;
                            Double finalMnMtoSubTotal1 = mnMtoSubTotal1;
                            Double finalMnMtoImpuesto1 = mnMtoImpuesto1;
                            Double finalMnMtoCanje1 = mnMtoCanje;
                            Double finalMnMtoDescuento1 = mnMtoDescuento1;

                            long delay = 3000;

                            if( GlobalInfo.getTipoPapel10.equals("58mm")){
                                delay = 6000;
                            }

                            Timer timerS = new Timer();
                            /** @IMPRESION02 */

                            Double finalGRPuntosGanados = GRPuntosGanados;
                            Double finalGRPuntosDisponibles = GRPuntosDisponibles;

                            timerS.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    imprimirGR10(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                            GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10,
                                            GlobalInfo.getoptranUniMed10, GlobalInfo.getoptranPrecio10, mnCantidad,
                                            finalMnMtoPagar1, finalMnMtoSubTotal1, finalMnMtoImpuesto1,
                                            mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                            mnKilometraje, mnObservacion, mnTarjND, xFechaDocumentoQR,
                                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, finalMnMtoCanje1, finalMnMtoDescuento1, mnMontoSoles,mnTarjetaPuntos, finalGRPuntosGanados, finalGRPuntosDisponibles);

                                    timerS.cancel();
                                }
                            }, delay);

                        } else {

                            imprimirGR10(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                    GlobalInfo.getuserName10, GlobalInfo.getoptranNroLado10, GlobalInfo.getoptranProductoDs10,
                                    GlobalInfo.getoptranUniMed10, GlobalInfo.getoptranPrecio10, mnCantidad,
                                    mnMtoPagar, mnMtoSubTotal1, mnMtoImpuesto1,
                                    mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                    mnKilometraje, mnObservacion, mnTarjND, xFechaDocumentoQR,
                                    mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoCanje, mnMtoDescuento1, mnMontoSoles,mnTarjetaPuntos,GRPuntosGanados,GRPuntosDisponibles);

                        }

                    }

                    /** FIN IMPRESION DEL COMPROBANTE*/

                    /**GlobalInfo.getpase11 = false;*/

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

    /**
     * @APISERVICE:GrabaraVenta CA
     */
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
                               String _observacionPag, String _Operador, Double _mtoIncremento
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
                _observacionPag, _mtoIncremento);

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
                Toast.makeText(getContext(), "Error de conexión APICORE Grabar Venta", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * @IMPRIMIR:ComprobanteVenta
     */
    private void imprimirGR10(String tipopapel,String _TipoDocumento, String _NroDocumento, String _FechaDocumento, Integer _Turno,
                              String _Cajero, String _nroLado, String _ArticuloDS, String _ArticuloUMED,
                              Double _Precio, Double _Cantidad, Double _MtoTotal, Double _MtoSubTotal, Double _MtoImpuesto,
                              String _ClienteID, String _ClienteRUC, String _ClienteRZ, String _ClienteDR, String _NroPlaca,
                              String _Kilometraje, String _Obervacion,String _NTarjND,
                              String _FechaQR, Integer _PagoID, Integer _TarjetaCreditoID, String _OperacionREF,
                              Double _mtoCanjeado, Double _mtoDescuento, Double _mtoSoles, String mnTarjetaPuntos,Double GRPuntosGanados, Double GRPuntosDisponibles){

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
        String RUCCompany  = GlobalInfo.getRucCompany10;

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

        String MtoSoles      = String.format("%.2f",_mtoSoles);

        String MtoTotalPagoFF = String.format("%.2f",_MtoTotal - _mtoSoles);

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

        String QRGenerado = qrSVEN.toString();

        int logoSize = (tipopapel.equals("80mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("58mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("65mm") ? GlobalInfo.getTerminalImageW10 : 400);

        Printama.with(getContext()).connect(printama -> {

            switch (tipopapel) {

                case "58mm":

                    switch (_TipoDocumento) {

                        case "01" :
                        case "03" :
                            printama.printTextln("                 ", Printama.CENTER);
                            printama.printImage(logoRobles, logoSize);
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
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha-Hora : " + _FechaDocumento, Printama.LEFT);
                    printama.printTextln("Turno        : " + _Turno, Printama.LEFT);
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
                            if (!_Obervacion.isEmpty()) {
                                printama.printTextln("Observación  : "+ _Obervacion, Printama.LEFT);
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

                                if (!_Obervacion.isEmpty()) {
                                    printama.printTextln("Observación  : "+ _Obervacion, Printama.LEFT);
                                }

                            }
                            break;
                        case "99" :
                            if (!_Kilometraje.isEmpty()) {
                                printama.printTextln("Kilometraje  : " + _Kilometraje, Printama.LEFT);
                            }

                            if (!_Obervacion.isEmpty()) {
                                printama.printTextln("Observación  : " + _Obervacion, Printama.LEFT);
                            }
                            printama.printTextln("RUC/DNI      : " + _ClienteID, Printama.LEFT);
                            printama.printTextln("Cliente      : " + _ClienteRZ, Printama.LEFT);
                            printama.printTextln("#Contrato    : " + _NTarjND , Printama.LEFT);

                            break;
                    }

                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("PROD. " + "U/MED " + "PRE.  " + "CANT.  " + "IMPORTE", Printama.LEFT);
                    printama.setSmallText();
                    printama.printTextln(_ArticuloDS,Printama.LEFT);
                    if (_mtoDescuento == 0.00) {
                        printama.printTextln(_ArticuloUMED+" " + PrecioFF + "  " + CantidadFF +"    "+ MtoTotalFF,Printama.RIGHT);
                    } else {
                        printama.printTextln(_ArticuloUMED+" " + PrecioFF + "  " + CantidadFF +"    "+ MtoCanjeado,Printama.RIGHT);
                    }
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
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
                            printSeparatorLine(printama, tipopapel);
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
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 2 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 3 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 4 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 5 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 6 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
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
                            printSeparatorLine(printama, tipopapel);
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
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 2 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 3 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 4 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 5 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 6 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");
                            break;
                        case "98" :
                            printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                            break;
                        case "99" :
                            printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                            printama.setSmallText();
                            printSeparatorLine(printama, tipopapel);
                            printama.addNewLine(1);
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }else{
                                printama.setSmallText();
                                printama.addNewLine(1);
                            }
                            printama.setSmallText();
                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                            break;
                    }

                    break;

                case "80mm":

                    switch (_TipoDocumento) {

                        case "01" :
                        case "03" :
                            printama.printTextln("                 ", Printama.CENTER);
                            printama.printImage(logoRobles, logoSize);
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
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha - Hora : " + _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
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
                            if (!_Obervacion.isEmpty()) {
                                printama.printTextln("Observación  : "+ _Obervacion, Printama.LEFT);
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

                                if (!_Obervacion.isEmpty()) {
                                    printama.printTextln("Observación  : "+ _Obervacion, Printama.LEFT);
                                }

                            }
                            break;
                        case "99" :
                            if (!_Kilometraje.isEmpty()) {
                                printama.printTextln("Kilometraje  : " + _Kilometraje, Printama.LEFT);
                            }

                            if (!_Obervacion.isEmpty()) {
                                printama.printTextln("Observación  : " + _Obervacion, Printama.LEFT);
                            }
                            printama.printTextln("RUC/DNI      : " + _ClienteID, Printama.LEFT);
                            printama.printTextln("Cliente      : " + _ClienteRZ, Printama.LEFT);
                            printama.printTextln("#Contrato    : " + _NTarjND , Printama.LEFT);

                            break;
                    }

                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("PRODUCTO     " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                    printama.setSmallText();
                    printama.printTextln(_ArticuloDS,Printama.LEFT);
                    if (_mtoDescuento == 0.00) {
                        printama.printTextln(_ArticuloUMED+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoTotalFF,Printama.RIGHT);
                    } else {
                        printama.printTextln(_ArticuloUMED+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoCanjeado,Printama.RIGHT);
                    }
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
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
                            printSeparatorLine(printama, tipopapel);
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
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 2 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 3 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 4 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 5 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 6 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                                bitMatrix = writer.encode(QRGenerado, BarcodeFormat.QR_CODE, 200, 200);
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
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
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
                            printSeparatorLine(printama, tipopapel);
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
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 2 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 3 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 4 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 5 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 6 :
                                            if (_mtoSoles > 0){
                                                printama.printTextlnBold("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextlnBold("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
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
                                bitMatrixB = writerB.encode(QRGenerado, BarcodeFormat.QR_CODE, 200, 200);
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
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");
                            break;
                        case "98" :
                            printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                            break;
                        case "99" :
                            printama.printTextlnBold("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                            printama.setSmallText();
                            printSeparatorLine(printama, tipopapel);
                            printama.addNewLine(1);
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }else{
                                printama.setSmallText();
                                printama.addNewLine(1);
                            }
                            printama.setSmallText();
                            printama.printTextlnBold("NOMBRE :" , Printama.LEFT);
                            printama.printTextlnBold("DNI    :" , Printama.LEFT);
                            printama.printTextlnBold("FIRMA  :" , Printama.LEFT);
                            break;
                    }

                    break;

                case "65mm":

                    switch (_TipoDocumento) {

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

                    printama.printTextln(_NroDocumento,Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha - Hora : " + _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
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
                            if (!_Obervacion.isEmpty()) {
                                printama.printTextln("Observación  : "+ _Obervacion, Printama.LEFT);
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

                                if (!_Obervacion.isEmpty()) {
                                    printama.printTextln("Observación  : "+ _Obervacion, Printama.LEFT);
                                }

                            }
                            break;
                        case "99" :
                            if (!_Kilometraje.isEmpty()) {
                                printama.printTextln("Kilometraje  : " + _Kilometraje, Printama.LEFT);
                            }

                            if (!_Obervacion.isEmpty()) {
                                printama.printTextln("Observación  : " + _Obervacion, Printama.LEFT);
                            }
                            printama.printTextln("RUC/DNI      : " + _ClienteID, Printama.LEFT);
                            printama.printTextln("Cliente      : " + _ClienteRZ, Printama.LEFT);
                            printama.printTextln("#Contrato    : " + _NTarjND , Printama.LEFT);

                            break;
                    }

                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("PRODUCTO     " + "U/MED   " + "PRECIO   " + "CANTIDAD  " + "IMPORTE", Printama.RIGHT);
                    printama.setSmallText();
                    printama.printTextln(_ArticuloDS,Printama.LEFT);
                    if (_mtoDescuento == 0.00) {
                        printama.printTextln(_ArticuloUMED+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoTotalFF,Printama.RIGHT);
                    } else {
                        printama.printTextln(_ArticuloUMED+"    " + PrecioFF + "      " + CantidadFF +"     "+ MtoCanjeado,Printama.RIGHT);
                    }
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();

                    switch (_TipoDocumento) {
                        case "01" :

                            if (_mtoDescuento > 0) {
                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                            }

                            printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                            printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                            printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);

                            printama.setSmallText();
                            printSeparatorLine(printama, tipopapel);
                            printama.addNewLine(1);
                            printama.setSmallText();

                            switch (_PagoID) {
                                case 1 :
                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                    printama.printTextln("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                    break;
                                case 2 :

                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                    switch (_TarjetaCreditoID) {
                                        case 1 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 2 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 3 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 4 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 5 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 6 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                    }

                                    break;

                                case 4 :
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
                                bitMatrix = writer.encode(QRGenerado, BarcodeFormat.QR_CODE, 200, 200);
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
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");
                            break;
                        case "03" :

                            if (_mtoDescuento > 0) {
                                printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                            }

                            printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF , Printama.RIGHT);

                            printama.setSmallText();
                            printSeparatorLine(printama, tipopapel);
                            printama.addNewLine(1);
                            printama.setSmallText();

                            switch (_PagoID) {
                                case 1 :
                                    printama.printTextlnBold("CONDICION DE PAGO:", Printama.LEFT);
                                    printama.printTextln("CONTADO: S/ " + MtoTotalFF, Printama.RIGHT);
                                    break;
                                case 2 :

                                    printama.printTextlnBold("CONDICION DE PAGO: CONTADO", Printama.LEFT);

                                    switch (_TarjetaCreditoID) {
                                        case 1 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("VISA: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 2 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("MASTERCARD: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 3 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("DINERS: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 4 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("YAPE: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 5 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("AMERICAN EXPRES: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                        case 6 :
                                            if (_mtoSoles > 0){
                                                printama.printTextln("EFECTIVO: S/ " + MtoSoles, Printama.RIGHT);
                                            }
                                            printama.printTextln("PLIN: S/ " + MtoTotalPagoFF, Printama.RIGHT);
                                            printama.setSmallText();
                                            printama.printTextln("NRO.OPERACION:" + _OperacionREF, Printama.LEFT);
                                            break;
                                    }

                                    break;

                                case 4 :
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
                                bitMatrixB = writerB.encode(QRGenerado, BarcodeFormat.QR_CODE, 200, 200);
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
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");
                            break;
                        case "98" :
                            printama.printTextln("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                            break;
                        case "99" :
                            printama.printTextln("TOTAL VENTA: S/ "+ MtoTotalFF , Printama.RIGHT);
                            printama.setSmallText();
                            printSeparatorLine(printama, tipopapel);
                            printama.addNewLine(1);
                            if(GlobalInfo.getTerminalSoloPuntos10){
                                if(!mnTarjetaPuntos.isEmpty() && !mnTarjetaPuntos.equals("1")){
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                    printama.setSmallText();
                                    printama.printTextlnBold("NRO. TARJETA PUNTOS : " + mnTarjetaPuntos , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS GANADOS     : " + GRPuntosGanados , Printama.LEFT);
                                    printama.printTextlnBold("PUNTOS DISPONIBLES : " + GRPuntosDisponibles, Printama.LEFT);
                                    printama.setSmallText();
                                    printama.addNewLine(1);
                                }
                            }else{
                                printama.setSmallText();
                                printama.addNewLine(1);
                            }
                            printama.setSmallText();
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

    private void printSeparatorLine(Printama printama, String tipopapel) {
        if ("80mm".equals(tipopapel) || "65mm".equals(tipopapel)) {
            printama.printDoubleDashedLine();
        } else if ("58mm".equals(tipopapel)) {
            printama.printDoubleDashedLines();
        }
    }

    /**
     * @ALERTA:Conexión de Bluetooth
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

    /**
     * @METODOS:CicloVida
     */
    @Override
    public void onResume() {
        super.onResume();
        nfcUtil.onResume();

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
        nfcUtil.onPause();

        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(getActivity());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTOMATICO_MODE_KEY, mIsTaskScheduled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * @OBTENER:NFC
     */
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

}