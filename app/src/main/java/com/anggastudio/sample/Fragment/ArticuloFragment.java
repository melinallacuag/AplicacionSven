package com.anggastudio.sample.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
import com.anggastudio.sample.Adapter.CarritoAdapter;
import com.anggastudio.sample.Adapter.ClienteCreditoAdapter;
import com.anggastudio.sample.Adapter.FamiliaAdapter;
import com.anggastudio.sample.Adapter.LClienteAdapter;
import com.anggastudio.sample.Adapter.LRegistroClienteAdapter;
import com.anggastudio.sample.Adapter.LRegistroClientePuntosAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.CaptureActivityPortrait;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.PasswordChecker;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;
import com.anggastudio.sample.WebApiSVEN.Models.ClienteCredito;
import com.anggastudio.sample.WebApiSVEN.Models.ClientePrecio;
import com.anggastudio.sample.WebApiSVEN.Models.Correlativo;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Familia;
import com.anggastudio.sample.WebApiSVEN.Models.LClientePuntos;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Models.VentaCA;
import com.anggastudio.sample.WebApiSVEN.Models.VentaMarketCA;
import com.anggastudio.sample.WebApiSVEN.Models.VentaMarketDA;
import com.anggastudio.sample.WebApiSVEN.Models.WifiSpeedChecker;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticuloFragment extends Fragment implements NfcAdapter.ReaderCallback{

    private APIService mAPIService;
    private NFCUtil nfcUtil;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    boolean isTodoProductoSelected = false;

    List<Familia> familiaList;
    FamiliaAdapter familiaAdapter;

    List<Articulo> articuloList;

    ArticuloAdapter articuloAdapter;

    List<Articulo> articuloFiltrados;

    List<Articulo> articuloSeleccionados = new ArrayList<Articulo>();

    List<Users> usersListNFC;
    LRegistroClienteAdapter lRegistroClienteAdapter;
    LRegistroClientePuntosAdapter lRegistroClientePuntosAdapter;

    List<LClientePuntos>  clientePuntosList;

    RecyclerView recyclerLClientePuntosNFC,recyclerListaClientesAfiliados,recyclerFamilia, recyclerArticulo, recyclerCarrito, recyclerLCliente,recyclerLClienteCredito;
    Button btnCancelarNFC,btnAceptarNFC,
            btnAceptarErrorWifi,btnAceptarError,
            btnTodoArticulo,
            btnBoleta,btnCancelarBoleta,btnAgregarBoleta,btnGenerarBoleta,buscarPlacaBoleta,buscarDNIBoleta,btnCancelarLCliente,
            btnFactura,buscarRUCFactura,buscarPlacaFactura,btnCancelarFactura,btnAgregarFactura,
            btnNotaDespacho,btnCancelarNotaDespacho,btnAgregarNotaDespacho,buscarListNFC,buscarListPuntosNFC;

    CardView btncarritocompra;

    LinearLayout linearLayoutRecyclerArticulo,btnVolverCompra,btnconfirmarventa,btnSeleccionCliente,btnGuardarVenta,btnCancelarVenta,btnImprimirVenta,btnNuevaVenta;

    Map<String, Integer> cantidadesSeleccionadas = new HashMap<>();
    Map<String, Double> nuevosPrecios = new HashMap<>();

    TextView totalmontoCar,textMensajePEfectivo,textMensajeGratuito,text_guardarC,text_inprimirC,titleconfirmar,titleimprimir;
    Dialog modallistNFCPuntos,modallistNFC,modalNFCLogin,modal_ErrorWifi,modal_ErrorServidor,modal_CV_Articulo,modalCarrito, modalBoleta, modalClienteDNI,modalFactura,modalClienteRUC,modalNotaDespacho,modalClienteCredito;

    CarritoAdapter carritoAdapter;
    SearchView BuscarProducto,btnBuscadorClienteRZ,BuscarRazonSocial;

    TextInputLayout alertuserNFC,alertpasswordNFC,textNFC,alertPlaca,alertDNI,alertNombre,alertPEfectivo,alertOperacion,alertSelectTPago,
            alertRUC,alertRazSocial,alertObservacion,alertTarjeta,alertCliente,alertKilometraje;

    TextInputEditText usuarioNFC,contraseñaNFC,inputNFC,inputPlaca,inputDNI,inputNombre,inputDireccion,inputObservacion,inputOperacion,inputPEfectivo,
            inputRUC,inputRazSocial,
            inputTarjeta,inputCliente,inputKilometraje;

    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioGratuito,radioNombreFormaPago,radioCanje;

    Spinner SpinnerTPago;
    LClienteAdapter lclienteAdapter;
    TipoPagoAdapter tipoPagoAdapter;

    TipoPago tipoPago;


    ImageButton btnfiltrar,btnscanear,btnpromociones;

    TextView nombreCliente,textCliente,textNumPuntos,textMensajeCanje;

    private String usuarioUserNFC,contraseñaUserNFC,mnTipoDocumento,mnTipoPago,mnNroPlaca,mnClienteID,mnClienteRUC,mnClienteRS,mnCliernteDR,mnTarjetaCredito,mnOperacionREF,mnobservacionPag,
            detArticuloId,detArticuloDs,detUmedida,opGratruitas,mnTarjND,mnobservacion;

    private Double mnMontoSoles,mnMtoSaldoCredito,mnPtosDisponibles,detPrecio,detCantidad,detTotal,detTotal1,detSubtotal,detSubtotal1,detImpuesto,detImpuesto1,
            mnMtoSubTotal1, mnMtoImpuesto1;

    private Integer mnPagoID,mnTarjetaCreditoID;

    private Boolean mnRespuestaCorre;

    ClienteCreditoAdapter clienteCreditoAdapter;
    Double monto;

    List<ClientePrecio> clientePrecioList;

    private boolean isVentaGuardadaCorrectamente = false;

    /**
     * @CONFIGURACIÓN:ObtenerResultado
     */
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(getContext(), "CANCELADO", Toast.LENGTH_SHORT).show();
        } else {
            BuscarProducto.setQuery(result.getContents(), true);
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcUtil = new NFCUtil(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
                limpiarDatos();
                return true;
            }
            return false;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_articulo, container, false);

        mAPIService = GlobalInfo.getAPIService();

        btnfiltrar       = view.findViewById(R.id.btnfiltrar);
        BuscarProducto   = view.findViewById(R.id.btnBuscadorProducto);
        btnscanear       = view.findViewById(R.id.btnscanear);
        btnTodoArticulo  = view.findViewById(R.id.btnTodoArticulo);
        btncarritocompra = view.findViewById(R.id.btncarritocompra);
        linearLayoutRecyclerArticulo = view.findViewById(R.id.linearLayoutRecyclerArticulo);
        btnpromociones = view.findViewById(R.id.btnpromociones);
        recyclerArticulo = view.findViewById(R.id.recyclerProducto);

        btnpromociones.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
        btnpromociones.setColorFilter(getResources().getColor(R.color.white));

        btnpromociones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnpromociones.setSelected(!btnpromociones.isSelected());

                if (btnpromociones.isSelected()) {

                    recyclerArticulo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    limpiarDatos();
                    getArticuloG();

                    btnpromociones.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                    btnpromociones.setColorFilter(getResources().getColor(R.color.white));

                    btnfiltrar.setEnabled(false);
                    btnTodoArticulo.setEnabled(false);
                    recyclerFamilia.setEnabled(false);
                    familiaAdapter.setTodosBotonesHabilitados(false);

                    /**
                     * @BOTON:DesactivarEstado
                     */
                    isTodoProductoSelected = false;
                    btnTodoArticulo.setBackgroundColor(Color.parseColor("#999999"));
                    btnTodoArticulo.setTextColor(Color.parseColor("#FFFFFF"));

                    /**
                     * @BOTON:ActivarFamiliaSeleccionado
                     */
                    familiaAdapter.setTodoSelected(true);

                    /**
                     * @BOTON:DesactivarEstado
                     */
                    isTodoProductoSelected = false;

                    btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                    btnfiltrar.setColorFilter(getResources().getColor(R.color.white));

                    /** Limpiar articulo Seleccionado **/
                    for (Articulo articulo : articuloSeleccionados) {
                        articulo.setSeleccionado(false);
                    }

                    articuloSeleccionados.clear();
                    cantidadesSeleccionadas.clear();

                    articuloAdapter.notifyDataSetChanged();
                    carritoAdapter.notifyDataSetChanged();
                    btncarritocompra.setVisibility(View.GONE);

                }else {

                    btnpromociones.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                    btnpromociones.setColorFilter(getResources().getColor(R.color.white));

                    btnfiltrar.setEnabled(true);
                    btnTodoArticulo.setEnabled(true);
                    recyclerFamilia.setEnabled(true);
                    familiaAdapter.setTodosBotonesHabilitados(true);

                    limpiarDatos();
                    getArticulo();
                    actualizarBoton();

                    /** Limpiar articulo Seleccionado **/
                    for (Articulo articulo : articuloSeleccionados) {
                        articulo.setSeleccionado(false);
                    }

                    articuloSeleccionados.clear();
                    cantidadesSeleccionadas.clear();

                    articuloAdapter.notifyDataSetChanged();
                    carritoAdapter.notifyDataSetChanged();
                    btncarritocompra.setVisibility(View.GONE);
                }
            }
        });

        /**
         * @MOSTRARSCANNER
         */
        btnscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanner();
            }
        });

        /**
         * @MOSTRARBOTON:DesactivadoOculto
         */
        btncarritocompra.setVisibility(View.GONE);
        btnTodoArticulo.setBackgroundColor(Color.parseColor("#FFC107"));
        btnTodoArticulo.setTextColor(Color.parseColor("#000000"));

        /**
         * @BUSCADOR:ProductosNombres
         */
        BuscarProducto.setIconifiedByDefault(false);
        BuscarProducto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                String userInput = newText.toLowerCase();
                if (articuloAdapter != null) {
                    // Buscar por nombre
                    articuloAdapter.filtrado(userInput, false);

                    // Buscar código de barras
                    if (articuloList.isEmpty()) {
                        articuloAdapter.filtrado(userInput, true);

                        if (articuloList.isEmpty()) {
                            Toast.makeText(getContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                return true;
            }
        });

        /**
         * @FILTRAR:ArticuloTop
         */
        btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
        btnfiltrar.setColorFilter(Color.parseColor("#FFFFFF"));

        btnfiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTodoProductoSelected) {
                    btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                    btnfiltrar.setColorFilter(getResources().getColor(R.color.white));
                }else {
                    btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                    btnfiltrar.setColorFilter(getResources().getColor(R.color.white));
                    articuloFiltrados = new ArrayList<>();

                    for (Articulo articulo : articuloList) {
                        if (articulo.getSalidas() >= 0.00) {
                            articuloFiltrados.add(articulo);
                        }
                    }

                    Collections.sort(articuloFiltrados, new Comparator<Articulo>() {
                        @Override
                        public int compare(Articulo articulo1, Articulo articulo2) {
                            return Double.compare(articulo2.getSalidas(), articulo1.getSalidas());
                        }
                    });
                }

                articuloAdapter.setProductos(articuloFiltrados);
                articuloAdapter.notifyDataSetChanged();

                /**
                 * @BOTON:DesactivarEstado
                 */
                isTodoProductoSelected = false;
                btnTodoArticulo.setBackgroundColor(Color.parseColor("#999999"));
                btnTodoArticulo.setTextColor(Color.parseColor("#FFFFFF"));

                /**
                 * @BOTON:ActivarFamiliaSeleccionado
                 */
                familiaAdapter.setTodoSelected(true);

            }
        });

        /**
         * @FILTRAR:BotonTodosArticulos
         */
        btnTodoArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * @BOTON:ActivarDesactivarEstado
                 */
                if (isTodoProductoSelected) {
                    btnTodoArticulo.setBackgroundColor(Color.parseColor("#999999"));
                    btnTodoArticulo.setTextColor(Color.parseColor("#FFFFFF"));

                }else {
                    btnTodoArticulo.setBackgroundColor(Color.parseColor("#FFC107"));
                    btnTodoArticulo.setTextColor(Color.parseColor("#000000"));
                }

                articuloAdapter.setProductos(articuloList);
                articuloAdapter.notifyDataSetChanged();

                /**
                 * @BOTON:ActivarFamiliaSeleccionado
                 */
                familiaAdapter.setTodoSelected(true);

                /**
                 * @BOTON:DesactivarEstado
                 */
                isTodoProductoSelected = false;
                btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                btnfiltrar.setColorFilter(getResources().getColor(R.color.white));

            }
        });

        /**
         * @LISTADO:Productos
         */
        recyclerArticulo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getArticulo();

        /**
         * @LISTADO:Familia
         */
        recyclerFamilia = view.findViewById(R.id.recyclerFamilia);
        recyclerFamilia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        getFamilia();

        /**
         * @Modal:CarritoCompra
         */
        modalCarrito = new Dialog(getContext());
        modalCarrito.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalCarrito.setContentView(R.layout.fragment_carrito);
        modalCarrito.setCancelable(false);

        totalmontoCar    = modalCarrito.findViewById(R.id.totalmontoCar);

        btncarritocompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalCarrito.show();

                btnBoleta           = modalCarrito.findViewById(R.id.btnBoleta);
                btnFactura          = modalCarrito.findViewById(R.id.btnFactura);
                btnNotaDespacho     = modalCarrito.findViewById(R.id.btnnotadespacho);
                btnSeleccionCliente = modalCarrito.findViewById(R.id.btnSeleccionCliente);
                btnconfirmarventa   = modalCarrito.findViewById(R.id.btnconfirmarventa);
                btnVolverCompra     = modalCarrito.findViewById(R.id.btnvolvercompra);
                textCliente         = modalCarrito.findViewById(R.id.textCliente);
                nombreCliente       = modalCarrito.findViewById(R.id.nombreCliente);

                if (GlobalInfo.getMarketClienteRZ.equals("CLIENTE VARIOS")) {
                    textCliente.setVisibility(View.VISIBLE);
                    nombreCliente.setVisibility(View.GONE);
                    nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                }

                btnBoleta.setVisibility(View.GONE);
                btnFactura.setVisibility(View.GONE);
                btnNotaDespacho.setVisibility(View.GONE);
                btnSeleccionCliente.setVisibility(View.VISIBLE);

                btnVolverCompra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalCarrito.dismiss();
                        if (!GlobalInfo.getMarketClienteRZ.equals("CLIENTE VARIOS")) {
                            textCliente.setVisibility(View.GONE);
                            nombreCliente.setVisibility(View.VISIBLE);
                            nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                        }
                    }
                });

                btnSeleccionCliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnBoleta.setVisibility(View.VISIBLE);
                        btnFactura.setVisibility(View.VISIBLE);
                        btnNotaDespacho.setVisibility(View.VISIBLE);
                        btnSeleccionCliente.setVisibility(View.GONE);
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
                        alertObservacion  = modalBoleta.findViewById(R.id.alertObservacion);
                        textNFC           = modalBoleta.findViewById(R.id.textNFC);

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
                        textMensajeGratuito = modalBoleta.findViewById(R.id.textMensajeGratuito);
                        textMensajeCanje   = modalBoleta.findViewById(R.id.textMensajeCanje);

                        radioFormaPago    = modalBoleta.findViewById(R.id.radioFormaPago);
                        radioEfectivo     = modalBoleta.findViewById(R.id.radioEfectivo);
                        radioTarjeta      = modalBoleta.findViewById(R.id.radioTarjeta);
                        radioCredito      = modalBoleta.findViewById(R.id.radioCredito);
                        radioGratuito     = modalBoleta.findViewById(R.id.radioGratuito);
                        radioCanje        = modalBoleta.findViewById(R.id.radioCanje);

                        buscarDNIBoleta   = modalBoleta.findViewById(R.id.buscarDNIBoleta);
                        buscarPlacaBoleta = modalBoleta.findViewById(R.id.buscarPlacaBoleta);
                        btnGenerarBoleta  = modalBoleta.findViewById(R.id.btnGenerarBoleta);
                        btnCancelarBoleta = modalBoleta.findViewById(R.id.btnCancelarBoleta);
                        btnAgregarBoleta  = modalBoleta.findViewById(R.id.btnAgregarBoleta);
                        buscarListNFC     = modalBoleta.findViewById(R.id.buscarListNFC);
                        buscarListPuntosNFC  = modalBoleta.findViewById(R.id.buscarListPuntosNFC);
                        textNumPuntos     = modalBoleta.findViewById(R.id.textNumPuntos);

                        inputDNI.setEnabled(true);
                        inputNombre.setEnabled(true);
                        alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);
                        radioCredito.setVisibility(View.GONE);
                        radioGratuito.setVisibility(View.VISIBLE);
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
                                gestureDetector.onTouchEvent(event);
                                if (!gestureDetector.onTouchEvent(event)) {

                                    return false;
                                }
                                return true;
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
                                    textMensajeGratuito.setVisibility(View.GONE);
                                    alertSelectTPago.setVisibility(View.GONE);
                                    alertOperacion.setVisibility(View.GONE);
                                    alertPEfectivo.setVisibility(View.GONE);
                                } else if (checkedId == radioTarjeta.getId()){
                                    textMensajePEfectivo.setVisibility(View.GONE);
                                    textMensajeCanje.setVisibility(View.GONE);
                                    textMensajeGratuito.setVisibility(View.GONE);
                                    alertSelectTPago.setVisibility(View.VISIBLE);
                                    alertOperacion.setVisibility(View.VISIBLE);
                                    alertPEfectivo.setVisibility(View.VISIBLE);
                                } else if (checkedId == radioCredito.getId()){
                                    textMensajePEfectivo.setVisibility(View.GONE);
                                    textMensajeCanje.setVisibility(View.GONE);
                                    textMensajeGratuito.setVisibility(View.GONE);
                                    alertSelectTPago.setVisibility(View.GONE);
                                    alertOperacion.setVisibility(View.GONE);
                                    alertPEfectivo.setVisibility(View.VISIBLE);
                                } else if (checkedId == radioGratuito.getId()){
                                    textMensajeGratuito.setVisibility(View.VISIBLE);
                                    textMensajeCanje.setVisibility(View.GONE);
                                    textMensajePEfectivo.setVisibility(View.GONE);
                                    alertSelectTPago.setVisibility(View.GONE);
                                    alertOperacion.setVisibility(View.GONE);
                                    alertPEfectivo.setVisibility(View.GONE);
                                }else if (checkedId == radioCanje.getId()){
                                    textMensajeGratuito.setVisibility(View.GONE);
                                    textMensajePEfectivo.setVisibility(View.GONE);
                                    textMensajeCanje.setVisibility(View.VISIBLE);
                                    alertSelectTPago.setVisibility(View.GONE);
                                    alertOperacion.setVisibility(View.GONE);
                                    alertPEfectivo.setVisibility(View.GONE);
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
                                radioFormaPago.check(radioEfectivo.getId());
                                radioCanje.setVisibility(View.GONE);


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
                                radioFormaPago.check(radioEfectivo.getId());
                                radioCanje.setVisibility(View.GONE);
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
                                radioFormaPago.check(radioEfectivo.getId());
                                inputPEfectivo.setText("0");
                                inputNFC.getText().clear();
                                inputOperacion.getText().clear();
                                textNumPuntos.setText(String.valueOf(0));

                                alertPlaca.setErrorEnabled(false);
                                alertDNI.setErrorEnabled(false);
                                alertNombre.setErrorEnabled(false);
                                alertPEfectivo.setErrorEnabled(false);
                                alertOperacion.setErrorEnabled(false);

                                btnBoleta.setVisibility(View.GONE);
                                btnFactura.setVisibility(View.GONE);
                                btnNotaDespacho.setVisibility(View.GONE);
                                btnSeleccionCliente.setVisibility(View.VISIBLE);
                            }
                        });

                        /**
                         * @AGREGAR:Boleta
                         */
                        btnAgregarBoleta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                GlobalInfo.getMarketPlaca           = inputPlaca.getText().toString();
                                GlobalInfo.getMarketClienteID       = inputDNI.getText().toString();
                                GlobalInfo.getMarketClienteRZ       = inputNombre.getText().toString();
                                GlobalInfo.getMarketClienteDR       = inputDireccion.getText().toString();
                                GlobalInfo.getMarketOperacion       = inputOperacion.getText().toString();
                                GlobalInfo.getMarketPEfectivo       = inputPEfectivo.getText().toString();

                                int checkedRadioButtonId = radioFormaPago.getCheckedRadioButtonId();

                                    if (GlobalInfo.getMarketPlaca.isEmpty()) {

                                        alertPlaca.setError("* El campo Placa es obligatorio");
                                        return;
                                    } else if (GlobalInfo.getMarketClienteID.isEmpty()) {

                                        alertDNI.setError("* El campo DNI es obligatorio");
                                        return;
                                    } else if (GlobalInfo.getMarketClienteID.length() < 8) {

                                        alertDNI.setError("* El DNI debe tener 8 dígitos");
                                        return;
                                    } else if (GlobalInfo.getMarketClienteRZ.isEmpty()) {

                                        alertNombre.setError("* El campo Nombre es obligatorio");
                                        return;
                                    } else if (GlobalInfo.getMarketClienteRZ.length() < 8) {

                                        alertNombre.setError("* El Nombre debe tener mínino 8 dígitos");
                                        return;
                                    } else if (checkedRadioButtonId == radioTarjeta.getId()) {

                                        if (GlobalInfo.getMarketOperacion.isEmpty()) {
                                            alertOperacion.setError("* El campo Nro Operación es obligatorio");
                                            return;
                                        } else if (GlobalInfo.getMarketOperacion.length() < 4) {
                                            alertOperacion.setError("* El  Nro Operación debe tener mayor a 4 dígitos");
                                            return;
                                        } else if (GlobalInfo.getMarketPEfectivo.isEmpty()) {
                                            alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                            return;
                                        }

                                    } else if (checkedRadioButtonId == radioCredito.getId()) {

                                        if (GlobalInfo.getMarketPEfectivo.isEmpty()) {
                                            alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                            return;
                                        }

                                    }

                                    GlobalInfo.getMarketTarjetaCredito = "";
                                    GlobalInfo.getMarketOperacion = "";
                                    GlobalInfo.getMarketPEfectivo = "0.00";
                                    GlobalInfo.getMarketnroTarjetaNotaD = "";
                                    GlobalInfo.getMarketObservacion     = "";


                                    alertPlaca.setErrorEnabled(false);
                                    alertDNI.setErrorEnabled(false);
                                    alertNombre.setErrorEnabled(false);
                                    alertOperacion.setErrorEnabled(false);
                                    alertPEfectivo.setErrorEnabled(false);

                                GlobalInfo.getMarketFormaPago = radioNombreFormaPago.getText().toString().substring(0, 1);

                                if (GlobalInfo.getMarketFormaPago.equals("T")) {

                                        Double dosDecimales = Double.valueOf(GlobalInfo.getMarketPEfectivo);
                                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                        GlobalInfo.getMarketTarjetaCredito  = String.valueOf(Integer.valueOf(tipoPago.getCardID()));
                                        GlobalInfo.getMarketOperacion       = inputOperacion.getText().toString();

                                        if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))) {
                                            alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                            return;
                                        } else {
                                            GlobalInfo.getMarketPEfectivo  = inputPEfectivo.getText().toString();
                                        }


                                    } else if (GlobalInfo.getMarketFormaPago.equals("C")) {

                                        Double dosDecimales = Double.valueOf(GlobalInfo.getMarketPEfectivo );
                                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                        if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))) {
                                            alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                            return;
                                        } else {
                                            GlobalInfo.getMarketPEfectivo       = inputPEfectivo.getText().toString();
                                        }
                                    }

                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                    modalBoleta.dismiss();

                                    if (!GlobalInfo.getMarketClienteRZ.equals("CLIENTE VARIOS")) {
                                        textCliente.setVisibility(View.GONE);
                                        nombreCliente.setVisibility(View.VISIBLE);
                                        nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                                    }else{
                                        textCliente.setVisibility(View.VISIBLE);
                                        nombreCliente.setVisibility(View.GONE);
                                        nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                                    }

                                    /** Limpiar el Formulario - Boleta*/
                                    inputPlaca.setText("000-0000");
                                    inputDNI.getText().clear();
                                    inputNombre.getText().clear();
                                    inputDireccion.getText().clear();
                                    inputPEfectivo.setText("0");
                                    inputOperacion.getText().clear();
                                    inputNFC.getText().clear();
                                    radioFormaPago.check(radioEfectivo.getId());
                                    textNumPuntos.setText(String.valueOf(0));

                                    btnBoleta.setVisibility(View.GONE);
                                    btnFactura.setVisibility(View.GONE);
                                    btnNotaDespacho.setVisibility(View.GONE);
                                    btnSeleccionCliente.setVisibility(View.VISIBLE);
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
                        alertObservacion  = modalFactura.findViewById(R.id.alertObservacion);
                        textNFC           = modalFactura.findViewById(R.id.textNFC);

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
                        buscarListNFC      = modalFactura.findViewById(R.id.buscarListNFC);
                        buscarListPuntosNFC  = modalFactura.findViewById(R.id.buscarListPuntosNFC);
                        textNumPuntos       = modalFactura.findViewById(R.id.textNumPuntos);

                        textNumPuntos.setVisibility(View.GONE);
                        textNFC.setVisibility(View.GONE);
                        buscarListPuntosNFC.setVisibility(View.GONE);
                        buscarListNFC.setVisibility(View.GONE);
                        alertObservacion.setVisibility(View.GONE);
                        radioCredito.setVisibility(View.GONE);

                        inputRUC.setEnabled(true);
                        inputRazSocial.setEnabled(true);
                        alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);

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
                                gestureDetector.onTouchEvent(event);
                                if (!gestureDetector.onTouchEvent(event)) {

                                    return false;
                                }
                                return true;
                            }
                        });

                        /**
                         * @SELECCIONAR:OpciónFormaPago
                         */
                        radioFormaPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                radioNombreFormaPago = modalFactura.findViewById(checkedId);

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
                                radioFormaPago.check(radioEfectivo.getId());
                                inputPEfectivo.setText("0");
                                inputOperacion.getText().clear();

                                alertPlaca.setErrorEnabled(false);
                                alertRUC.setErrorEnabled(false);
                                alertRazSocial.setErrorEnabled(false);
                                alertPEfectivo.setErrorEnabled(false);
                                alertOperacion.setErrorEnabled(false);

                                btnBoleta.setVisibility(View.GONE);
                                btnFactura.setVisibility(View.GONE);
                                btnNotaDespacho.setVisibility(View.GONE);
                                btnSeleccionCliente.setVisibility(View.VISIBLE);
                            }
                        });

                        /**
                         * @AGREGAR:Factura
                         */
                        btnAgregarFactura.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                GlobalInfo.getMarketPlaca           = inputPlaca.getText().toString();
                                GlobalInfo.getMarketClienteID       = inputRUC.getText().toString();
                                GlobalInfo.getMarketClienteRZ       = inputRazSocial.getText().toString();
                                GlobalInfo.getMarketClienteDR       = inputDireccion.getText().toString();
                                GlobalInfo.getMarketOperacion       = inputOperacion.getText().toString();
                                GlobalInfo.getMarketPEfectivo       = inputPEfectivo.getText().toString();

                                        int checkedRadioButtonId = radioFormaPago.getCheckedRadioButtonId();

                                        if (GlobalInfo.getMarketPlaca.isEmpty()) {

                                            alertPlaca.setError("* El campo Placa es obligatorio");
                                            return;
                                        } else if (GlobalInfo.getMarketClienteID   .isEmpty()) {

                                            alertRUC.setError("* El campo RUC es obligatorio");
                                            return;
                                        } else if (GlobalInfo.getMarketClienteID   .length() < 11) {

                                            alertRUC.setError("* El RUC debe tener 11 dígitos");
                                            return;
                                        } else if (GlobalInfo.getMarketClienteRZ.isEmpty()) {

                                            alertRazSocial.setError("* La Razon Social es obligatorio");
                                            return;
                                        }  else if (GlobalInfo.getMarketClienteRZ.length() < 11 ) {

                                            alertRazSocial.setError("* La Razon Social debe tener mínino 11 dígitos");
                                            return;
                                        }else if (checkedRadioButtonId == radioTarjeta.getId()) {

                                            if (GlobalInfo.getMarketOperacion.isEmpty()) {

                                                alertOperacion.setError("* El campo Nro Operación es obligatorio");
                                                return;
                                            } else if (GlobalInfo.getMarketOperacion.length() < 4) {

                                                alertOperacion.setError("* El  Nro Operación debe tener mayor a 4 dígitos");
                                                return;
                                            } else if (GlobalInfo.getMarketPEfectivo.isEmpty()) {

                                                alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                                return;
                                            }

                                        } else if (checkedRadioButtonId == radioCredito.getId()) {

                                            if (GlobalInfo.getMarketPEfectivo.isEmpty()) {

                                                alertPEfectivo.setError("* El campo Pago Efectivo es obligatorio");
                                                return;
                                            }

                                        }

                                        alertPlaca.setErrorEnabled(false);
                                        alertRUC.setErrorEnabled(false);
                                        alertRazSocial.setErrorEnabled(false);
                                        alertOperacion.setErrorEnabled(false);
                                        alertPEfectivo.setErrorEnabled(false);


                                        GlobalInfo.getMarketTarjetaCredito = "";
                                        GlobalInfo.getMarketOperacion = "";
                                        GlobalInfo.getMarketPEfectivo = "0.00";
                                        GlobalInfo.getMarketnroTarjetaNotaD = "";
                                        GlobalInfo.getMarketObservacion     = "";

                                        GlobalInfo.getMarketFormaPago = radioNombreFormaPago.getText().toString().substring(0, 1);

                                        if (GlobalInfo.getMarketFormaPago.equals("T")) {

                                            Double dosDecimales = Double.valueOf(GlobalInfo.getMarketPEfectivo);
                                            DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                            GlobalInfo.getMarketTarjetaCredito  = String.valueOf(Integer.valueOf(tipoPago.getCardID()));
                                            GlobalInfo.getMarketOperacion       = inputOperacion.getText().toString();

                                            if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))) {
                                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                                return;
                                            } else {
                                                GlobalInfo.getMarketPEfectivo  = inputPEfectivo.getText().toString();
                                            }


                                        } else if (GlobalInfo.getMarketFormaPago.equals("C")) {

                                            Double dosDecimales = Double.valueOf(GlobalInfo.getMarketPEfectivo );
                                            DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                            if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))) {
                                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                                return;
                                            } else {
                                                GlobalInfo.getMarketPEfectivo       = inputPEfectivo.getText().toString();
                                            }
                                        }

                                        Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                        modalFactura.dismiss();

                                        if (!GlobalInfo.getMarketClienteRZ.equals("CLIENTE VARIOS")) {
                                            textCliente.setVisibility(View.GONE);
                                            nombreCliente.setVisibility(View.VISIBLE);
                                            nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                                        }else{
                                            textCliente.setVisibility(View.VISIBLE);
                                            nombreCliente.setVisibility(View.GONE);
                                            nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                                        }

                                        /** Limpiar el Formulario - Boleta*/
                                        inputPlaca.setText("000-0000");
                                        inputRUC.getText().clear();
                                        inputRazSocial.getText().clear();
                                        inputDireccion.getText().clear();
                                        inputPEfectivo.setText("0");
                                        inputOperacion.getText().clear();
                                        radioFormaPago.check(radioEfectivo.getId());

                                        btnBoleta.setVisibility(View.GONE);
                                        btnFactura.setVisibility(View.GONE);
                                        btnNotaDespacho.setVisibility(View.GONE);
                                        btnSeleccionCliente.setVisibility(View.VISIBLE);

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

                        inputPlaca               = modalNotaDespacho.findViewById(R.id.inputCPlaca);
                        inputTarjeta             = modalNotaDespacho.findViewById(R.id.input_CNTarjeta);
                        inputCliente             = modalNotaDespacho.findViewById(R.id.inputCCliente);
                        inputRazSocial           = modalNotaDespacho.findViewById(R.id.inputCRazSocial);
                        inputDireccion           = modalNotaDespacho.findViewById(R.id.inputCDireccion);
                        inputKilometraje         = modalNotaDespacho.findViewById(R.id.inputCKilometraje);
                        inputObservacion         = modalNotaDespacho.findViewById(R.id.inputCObservacion);

                        alertPlaca               = modalNotaDespacho.findViewById(R.id.alertPlaca);
                        alertTarjeta             = modalNotaDespacho.findViewById(R.id.alertTarjeta);
                        alertCliente             = modalNotaDespacho.findViewById(R.id.alertCCliente);
                        alertRazSocial           = modalNotaDespacho.findViewById(R.id.alertCRazSocial);
                        alertKilometraje         = modalNotaDespacho.findViewById(R.id.alertKilometraje);

                        textNumPuntos            = modalNotaDespacho.findViewById(R.id.textNumPuntos);
                        buscarListNFC            = modalNotaDespacho.findViewById(R.id.buscarListNFC);
                        buscarListPuntosNFC      = modalNotaDespacho.findViewById(R.id.buscarListPuntosNFC);
                        textNFC                  = modalNotaDespacho.findViewById(R.id.textNFC);

                        textNumPuntos.setVisibility(View.GONE);
                        buscarListNFC.setVisibility(View.GONE);
                        buscarListPuntosNFC.setVisibility(View.GONE);
                        textNFC.setVisibility(View.GONE);
                        alertKilometraje.setVisibility(View.GONE);

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

                        inputCliente.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                if (!gestureDetector.onTouchEvent(event)) {

                                    return false;
                                }
                                return true;
                            }
                        });

                        /**
                         * @CANCELAR:NotaDespacho
                         */
                        btnCancelarNotaDespacho.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                modalNotaDespacho.dismiss();

                                inputPlaca.setText("000-0000");
                                inputTarjeta.getText().clear();
                                inputCliente.getText().clear();
                                inputRazSocial.getText().clear();
                                inputDireccion.getText().clear();
                                inputKilometraje.getText().clear();
                                inputObservacion.getText().clear();

                                alertCliente.setErrorEnabled(false);
                                alertPlaca.setErrorEnabled(false);
                                alertRazSocial.setErrorEnabled(false);

                                btnBoleta.setVisibility(View.GONE);
                                btnFactura.setVisibility(View.GONE);
                                btnNotaDespacho.setVisibility(View.GONE);
                                btnSeleccionCliente.setVisibility(View.VISIBLE);

                            }
                        });

                        /**
                         * @AGREGAR:NotaDespacho
                         */
                        btnAgregarNotaDespacho.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                GlobalInfo.getMarketClienteID       = inputCliente.getText().toString();
                                GlobalInfo.getMarketClienteRZ       = inputRazSocial.getText().toString();
                                GlobalInfo.getMarketPlaca           = inputPlaca.getText().toString();
                                GlobalInfo.getMarketClienteDR       = inputDireccion.getText().toString();
                                GlobalInfo.getMarketObservacion     = inputObservacion.getText().toString();
                                GlobalInfo.getMarketnroTarjetaNotaD = inputTarjeta.getText().toString();

                                    if(GlobalInfo.getMarketClienteID.isEmpty()){
                                        alertCliente.setError("* Seleccionar Cliente");
                                        return;
                                    }else if(GlobalInfo.getMarketClienteRZ.isEmpty()){
                                        alertRazSocial.setError("* La Razon Social es obligatorio");
                                        return;
                                    }else if(GlobalInfo.getMarketPlaca.isEmpty()){
                                        alertPlaca.setError("* El campo Placa es obligatorio");
                                        return;
                                    }

                                GlobalInfo.getMarketTarjetaCredito = "";
                                GlobalInfo.getMarketOperacion = "";
                                GlobalInfo.getMarketPEfectivo = "0.00";
                                GlobalInfo.getMarketFormaPago = "C";

                                    alertPlaca.setErrorEnabled(false);
                                    alertCliente.setErrorEnabled(false);
                                    alertRazSocial.setErrorEnabled(false);

                                Toast.makeText(getContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show();

                                modalNotaDespacho.dismiss();

                                if (!GlobalInfo.getMarketClienteRZ.equals("CLIENTE VARIOS")) {
                                    textCliente.setVisibility(View.GONE);
                                    nombreCliente.setVisibility(View.VISIBLE);
                                    nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                                }else{
                                    textCliente.setVisibility(View.VISIBLE);
                                    nombreCliente.setVisibility(View.GONE);
                                    nombreCliente.setText("Venta a: " + GlobalInfo.getMarketClienteRZ);
                                }

                                /** Limpiar el Formulario - Nota de Despacho*/
                                inputPlaca.setText("000-0000");
                                inputTarjeta.getText().clear();
                                inputCliente.getText().clear();
                                inputRazSocial.getText().clear();
                                inputDireccion.getText().clear();
                                inputKilometraje.getText().clear();
                                inputObservacion.getText().clear();

                                btnBoleta.setVisibility(View.GONE);
                                btnFactura.setVisibility(View.GONE);
                                btnNotaDespacho.setVisibility(View.GONE);
                                btnSeleccionCliente.setVisibility(View.VISIBLE);

                            }
                        });

                    }
                });

                /**
                 * @IMPRIMIR:BotonVenta
                 */
                modal_CV_Articulo = new Dialog(getContext());
                modal_CV_Articulo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modal_CV_Articulo.setContentView(R.layout.modal_confirmarventa_articulo);
                modal_CV_Articulo.setCancelable(false);

                btnconfirmarventa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modal_CV_Articulo.show();

                        btnGuardarVenta   = modal_CV_Articulo.findViewById(R.id.btnGuardarVenta);
                        btnCancelarVenta  = modal_CV_Articulo.findViewById(R.id.btnCancelarVenta);
                        btnImprimirVenta  = modal_CV_Articulo.findViewById(R.id.btnImprimirVenta);
                        btnNuevaVenta     = modal_CV_Articulo.findViewById(R.id.btnNuevaVenta);
                        text_guardarC     = modal_CV_Articulo.findViewById(R.id.text_guardarC);
                        text_inprimirC    = modal_CV_Articulo.findViewById(R.id.text_inprimirC);
                        titleconfirmar    = modal_CV_Articulo.findViewById(R.id.titleconfirmar);
                        titleimprimir     = modal_CV_Articulo.findViewById(R.id.titleimprimir);

                        btnGuardarVenta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Context context = requireContext();

                                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {

                                        /** Variables de Detalle Venta **/

                                        mnTipoPago        = GlobalInfo.getMarketFormaPago;
                                        mnNroPlaca        = GlobalInfo.getMarketPlaca;
                                        mnClienteID       = GlobalInfo.getMarketClienteID;
                                        mnClienteRUC      = "";
                                        mnClienteRS       = GlobalInfo.getMarketClienteRZ;
                                        mnCliernteDR      = GlobalInfo.getMarketClienteDR;
                                        mnTarjetaCredito  = GlobalInfo.getMarketTarjetaCredito;
                                        mnOperacionREF    = GlobalInfo.getMarketOperacion;
                                        mnMontoSoles      = Double.valueOf(GlobalInfo.getMarketPEfectivo);
                                        mnTarjND          = GlobalInfo.getMarketnroTarjetaNotaD;
                                        mnobservacion     = GlobalInfo.getMarketObservacion;
                                        mnMtoSaldoCredito = 0.00;
                                        mnPtosDisponibles = 0.00;

                                        /** Consultando datos del DOCUMENTO **/
                                        mnPagoID = 1;
                                        mnTarjetaCreditoID = 0;
                                        mnobservacionPag = "CONTADO";
                                        mnTipoDocumento = "03";

                                        switch (mnTipoPago) {
                                            case "E" : //Efectivo
                                                if (mnClienteID != null && mnClienteID.length() == 11) {
                                                    mnTipoDocumento = "01";
                                                    mnClienteRUC = mnClienteID;
                                                } else if (mnClienteID == null || mnClienteID.isEmpty()){
                                                    mnTipoDocumento = "03";
                                                }
                                                break;
                                            case "T" : //Tarjeta
                                                if (mnClienteID != null && mnClienteID.length() == 11) {
                                                    mnTipoDocumento = "01";
                                                    mnClienteRUC = mnClienteID;
                                                } else if (mnClienteID == null || mnClienteID.isEmpty()) {
                                                    mnTipoDocumento = "03";
                                                }
                                                mnPagoID = 2;
                                                mnTarjetaCreditoID = Integer.valueOf(mnTarjetaCredito);
                                                mnobservacionPag = "TARJETA";
                                                break;

                                            case "C" : //credito
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

                                            case "G" : //Transferencia Gratuita
                                                mnobservacionPag = "GRATUITA";
                                                break;
                                        }

                                        if (mnClienteID != null && mnClienteID.isEmpty() && mnTipoDocumento == "03") {
                                            mnTipoPago       = "E";
                                            mnNroPlaca       = "000-0000";
                                            mnClienteID      = "11111111";
                                            mnClienteRUC     = "";
                                            mnClienteRS      = "CLIENTE VARIOS";
                                            mnCliernteDR     = "";
                                            mnTarjetaCredito = "";
                                            mnOperacionREF   = "";
                                            mnMontoSoles     = Double.valueOf(" 0.00");
                                        }

                                        /** 4.- VALIDAR BOLETA MAYORES A 700 SOLES **/

                                        if (mnClienteID != null && mnClienteID.equals(GlobalInfo.getsettingClienteID10)  && GlobalInfo.getMarketMontoTotal >= GlobalInfo.getsettingDNIMontoMinimo10 && mnTipoDocumento.equals("03")) {
                                            Toast.makeText(getContext(), "Por montos mayores a 700.00 soles debe ingresar el DNI del cliente", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        findCorrelativo(GlobalInfo.getterminalImei10, mnTipoDocumento, mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR,
                                            mnNroPlaca, mnPagoID, mnTarjetaCreditoID, mnOperacionREF,
                                            mnobservacionPag, mnMontoSoles, GlobalInfo.getMarketMontoTotal,mnTarjND,mnobservacion);

                                } else {

                                    modal_ErrorWifi.show();
                                    btnAceptarErrorWifi   = modal_ErrorWifi.findViewById(R.id.btnAceptarWifi);
                                    btnAceptarErrorWifi.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            modal_ErrorWifi.dismiss();
                                        }
                                    });
                                    modal_CV_Articulo.dismiss();
                                }


                            }

                        });

                        btnCancelarVenta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modal_CV_Articulo.dismiss();

                            }
                        });

                        btnImprimirVenta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!isVentaGuardadaCorrectamente) {
                                    Toast.makeText(getContext(), "La venta no se ha guardado correctamente. No se puede imprimir el comprobante.", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                String NroComprobante = GlobalInfo.getCorrelativoMarketSerie + "-" + GlobalInfo.getCorrelativoMarketNumero;

                                /** Fecha de Impresión */
                                Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                                SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());
                                String xFechaDocumentoQR     = xFechaHoraImpresion.substring(6,10) + "-" + xFechaHoraImpresion.substring(3,5) + "-" + xFechaHoraImpresion.substring(0,2);


                                /** Market-DA */
                                StringBuilder MarketDABuilder = new StringBuilder();
                                for (Articulo articulo : articuloSeleccionados) {

                                    detArticuloDs = articulo.getArticuloDS1();
                                    detUmedida    = articulo.getUniMed();
                                    detPrecio     = articulo.getPrecio_Venta();
                                    detCantidad   = Double.valueOf(cantidadesSeleccionadas.get(articulo.getArticuloID()));
                                    detTotal   = detPrecio * detCantidad;

                                    if (mnobservacionPag.equals("GRATUITA")){
                                        detTotal = 0.00;
                                    }

                                    String linnesS = String.format(Locale.getDefault(), "%-48s\n%-9s %4s %8s %7s %10s", detArticuloDs, "", detUmedida, String.format("%.2f", detPrecio), detCantidad, String.format("%.2f", detTotal));
                                    MarketDABuilder.append(linnesS).append("\n");

                                }

                                    if (mnPagoID == 2) {

                                        /** @IMPRESION01 */
                                        imprimirMarket(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                                GlobalInfo.getuserName10,GlobalInfo.getMarketMontoTotal,mnMtoSubTotal1, mnMtoImpuesto1,mnClienteID, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                                xFechaDocumentoQR,mnPagoID,mnTarjetaCreditoID,mnOperacionREF, mnMontoSoles,MarketDABuilder,mnobservacion,mnTarjND);

                                        /** @IMPRESION02 */
                                        Timer timerS = new Timer();
                                        timerS.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                imprimirMarket(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                                        GlobalInfo.getuserName10,GlobalInfo.getMarketMontoTotal,mnMtoSubTotal1, mnMtoImpuesto1,mnClienteID, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                                        xFechaDocumentoQR,mnPagoID,mnTarjetaCreditoID,mnOperacionREF, mnMontoSoles,MarketDABuilder,mnobservacion,mnTarjND);

                                                timerS.cancel();
                                            }
                                        }, 3000);

                                    } else {

                                        imprimirMarket(GlobalInfo.getTipoPapel10, mnTipoDocumento, NroComprobante, xFechaHoraImpresion, GlobalInfo.getterminalTurno10,
                                                GlobalInfo.getuserName10,GlobalInfo.getMarketMontoTotal,mnMtoSubTotal1, mnMtoImpuesto1,mnClienteID, mnClienteRS, mnCliernteDR, mnNroPlaca,
                                                xFechaDocumentoQR,mnPagoID,mnTarjetaCreditoID,mnOperacionREF, mnMontoSoles,MarketDABuilder,mnobservacion,mnTarjND);
                                    }

                                  ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                                  layoutParams.bottomMargin = 0;
                                  linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);

                                limpiarDatos();
                                getArticulo();
                                actualizarBoton();

                                btnpromociones.setSelected(!btnpromociones.isSelected());

                                btnpromociones.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                                btnpromociones.setColorFilter(getResources().getColor(R.color.white));

                                btnfiltrar.setEnabled(true);
                                btnTodoArticulo.setEnabled(true);
                                recyclerFamilia.setEnabled(true);
                                familiaAdapter.setTodosBotonesHabilitados(true);

                            }
                        });

                        btnNuevaVenta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (Articulo articulo : articuloSeleccionados) {
                                    articulo.setSeleccionado(false);
                                }

                                articuloSeleccionados.clear();
                                cantidadesSeleccionadas.clear();

                                articuloAdapter.notifyDataSetChanged();
                                carritoAdapter.notifyDataSetChanged();

                                modalCarrito.dismiss();
                                modal_CV_Articulo.dismiss();
                                btncarritocompra.setVisibility(View.GONE);

                                limpiarDatos();
                                getArticulo();
                                actualizarBoton();

                                btnpromociones.setSelected(!btnpromociones.isSelected());

                                btnpromociones.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                                btnpromociones.setColorFilter(getResources().getColor(R.color.white));

                                btnfiltrar.setEnabled(true);
                                btnTodoArticulo.setEnabled(true);
                                recyclerFamilia.setEnabled(true);
                                familiaAdapter.setTodosBotonesHabilitados(true);
                            }
                        });


                    }
                });
            }
        });

        /**
         * @LISTADO:CarritoCompra
         */
        recyclerCarrito = modalCarrito.findViewById(R.id.recyclerCarrito);
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        carritoAdapter = new CarritoAdapter(articuloSeleccionados,cantidadesSeleccionadas,nuevosPrecios,totalmontoCar,modalCarrito,btncarritocompra,linearLayoutRecyclerArticulo);

        carritoAdapter.setOnProductoEliminadoListener(new CarritoAdapter.OnArticuloEliminadoListener() {
            @Override
            public void onArticuloEliminado(List<Articulo> articuloEnCarrito) {
                articuloAdapter.notifyDataSetChanged();
            }

        });

        recyclerCarrito.setAdapter(carritoAdapter);
        carritoAdapter.notifyDataSetChanged();


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

                            Double numPuntos = Double.parseDouble(String.valueOf(textNumPuntos.getText().toString()));

                            if(numPuntos >= 10){
                                radioCanje.setVisibility(View.VISIBLE);
                            }else{
                                radioCanje.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Canje no disponible: el número de puntos debe ser mayor a 50.", Toast.LENGTH_SHORT).show();
                            }

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
                            radioFormaPago.check(radioEfectivo.getId());
                            radioCanje.setVisibility(View.GONE);
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
                        radioFormaPago.check(radioEfectivo.getId());
                        radioCanje.setVisibility(View.GONE);
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
     * @LISTADOCLIENTESCONPUNTOS
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

    private void limpiarDatos(){
        GlobalInfo.getMarketFormaPago    = "E";
        GlobalInfo.getMarketPlaca        = "000-000";
        GlobalInfo.getMarketClienteID    = "11111111";
        mnClienteRUC                     = "";
        GlobalInfo.getMarketClienteRZ    = "CLIENTE VARIOS";
        GlobalInfo.getMarketClienteDR    = "";
        GlobalInfo.getMarketTarjetaCredito  = "";
        GlobalInfo.getMarketOperacion    = "";
        GlobalInfo.getMarketPEfectivo    = "0.00";
        GlobalInfo.getMarketnroTarjetaNotaD = "";
        GlobalInfo.getMarketObservacion  = "";
        mnMtoSaldoCredito                = 0.00;
        mnPtosDisponibles                = 0.00;

    }

    private void actualizarBoton(){
        if (isTodoProductoSelected) {
            btnTodoArticulo.setBackgroundColor(Color.parseColor("#999999"));
            btnTodoArticulo.setTextColor(Color.parseColor("#FFFFFF"));

        }else {
            btnTodoArticulo.setBackgroundColor(Color.parseColor("#FFC107"));
            btnTodoArticulo.setTextColor(Color.parseColor("#000000"));
        }

        /**
         * @BOTON:ActivarFamiliaSeleccionado
         */
        familiaAdapter.setTodoSelected(true);

        /**
         * @BOTON:DesactivarEstado
         */
        isTodoProductoSelected = false;
        btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
        btnfiltrar.setColorFilter(getResources().getColor(R.color.white));
    }

    /**
     * @APISERVICE:GenerarCorrelativoComprobanteSinRFID
     */
    private void findCorrelativo(String imei, String mnTipoDocumento, String mnClienteID, String mnClienteRUC, String mnClienteRS, String mnCliernteDR,
                                 String mnNroPlaca, Integer mnPagoID, Integer mnTarjetaCreditoID, String mnOperacionREF,
                                 String mnobservacionPag, Double mnMontoSoles, Double mnMtoTotal,String mnTarjND,String mnobservacion) {


        Call<List<Correlativo>> call = mAPIService.findCorrelativomarket(imei, mnTipoDocumento);

        call.enqueue(new Callback<List<Correlativo>>() {
            @Override
            public void onResponse(Call<List<Correlativo>> call, Response<List<Correlativo>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Correlativo: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Correlativo> correlativoList = response.body();

                    if (correlativoList == null || correlativoList.isEmpty()) {
                        Toast.makeText(getContext(), "No se encontró correlativo.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for(Correlativo correlativo: correlativoList) {
                        GlobalInfo.getCorrelativoMarketFecha      = String.valueOf(correlativo.getFechaProceso());
                        GlobalInfo.getCorrelativoMarketSerie      = String.valueOf(correlativo.getSerie());
                        GlobalInfo.getCorrelativoMarketNumero     = String.valueOf(correlativo.getNumero());
                        GlobalInfo.getCorrelativoMarketMDescuento = correlativo.getMontoDescuento();
                        GlobalInfo.getCorrelativoMarketDocumentoVenta = correlativo.getDocumentoVenta();
                        GlobalInfo.getCorrelativoMarketTipoDesc       = correlativo.getTipoDescuento();
                    }

                    if(!GlobalInfo.getCorrelativoMarketDocumentoVenta.isEmpty()){
                        return;
                    }

                    /** Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    String GRNumeroSerie     = GlobalInfo.getCorrelativoMarketSerie;
                    String GRNumeroDocumento = GlobalInfo.getCorrelativoMarketNumero;

                    /** Fecha de Impresión */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String xFechaHoraImpresion   = formatdate.format(calendarprint.getTime());
                    String xFechaDocumento       = xFechaHoraImpresion.substring(6,10) + xFechaHoraImpresion.substring(3,5) + xFechaHoraImpresion.substring(0,2) + " " + xFechaHoraImpresion.substring(11,19);

                    /** FIN Consultando datos del DOCUMENTO-SERIE-CORRELATIVO*/

                    /** Calculando los totales **/

                    Double mnPrecioOrig = 0.00;
                    Double mnMtoPagar = 0.00;
                    Double mnMtoCanje = 0.00;

                    Double mnMtoSubTotal0 = 0.00;
                    mnMtoSubTotal1 = 0.00;
                    String mnMtoSubTotal2 = "";

                    Double mnMtoImpuesto0 = 0.00;
                    mnMtoImpuesto1 = 0.00;
                    String mnMtoImpuesto2 = "";

                    Integer mnItem = 0;

                    mnPrecioOrig = GlobalInfo.getMarketPrecio;

                    mnMtoCanje = mnMtoTotal;
                    mnMtoPagar = mnMtoTotal;

                    mnMtoSubTotal0 = mnMtoTotal / 1.18;
                    mnMtoSubTotal1 = Math.round(mnMtoSubTotal0*100.0)/100.0;

                    mnMtoImpuesto0 = mnMtoTotal - mnMtoSubTotal1;
                    mnMtoImpuesto1 = Math.round(mnMtoImpuesto0*100.0)/100.0;

                    /** GRABAR VENTA DETALLE EN BASE DE DATOS **/

                    for (Articulo articulo : articuloSeleccionados) {

                        detArticuloId = articulo.getArticuloID();
                        detArticuloDs = articulo.getArticuloDS1();
                        detUmedida    = articulo.getUniMed();
                        detPrecio     = articulo.getPrecio_Venta();
                        detCantidad   = Double.valueOf(cantidadesSeleccionadas.get(articulo.getArticuloID()));

                        detTotal = detPrecio * detCantidad;
                        detTotal1 = Math.round(detTotal*100.0)/100.0;

                        detSubtotal = detTotal1 / 1.18;
                        detSubtotal1 = Math.round(detSubtotal*100.0)/100.0;

                        if (mnobservacionPag.equals("GRATUITA")){
                            detImpuesto1 = 0.00;
                            detTotal1 = 0.00;
                        } else {
                            detImpuesto = detTotal1 - detSubtotal1;
                            detImpuesto1 = Math.round(detImpuesto*100.0)/100.0;
                        }

                        mnItem += 1;

                        grabarVentaMarketDA(mnTipoDocumento, GRNumeroSerie, GRNumeroDocumento, mnItem,
                                            detArticuloId, detArticuloDs, detUmedida, GlobalInfo.getterminalID10, xFechaDocumento,
                                            detPrecio, detCantidad, detSubtotal1, detImpuesto1, detTotal1);

                    }

                    /** GRABAR VENTA CAB. EN BASE DE DATOS **/

                        grabarVentaMarketCA(mnTipoDocumento, GRNumeroSerie, GRNumeroDocumento, GlobalInfo.getterminalID10,
                                            mnClienteID, mnClienteRUC, mnClienteRS, mnCliernteDR, xFechaDocumento,
                                            mnMtoSubTotal1, mnMtoImpuesto1, mnMtoTotal, mnNroPlaca, GlobalInfo.getuserID10,
                                            mnPagoID, mnTarjetaCreditoID, mnOperacionREF, mnMtoTotal, mnMontoSoles,
                                            mnobservacionPag, mnTarjND, mnobservacion, mnItem);

                            /** FIN GRABAR VENTA EN BASE DE DATOS **/

                            isVentaGuardadaCorrectamente = true;

                            Toast.makeText(getContext(), "Se Guardo Venta Correctamente", Toast.LENGTH_SHORT).show();

                            btnGuardarVenta.setVisibility(View.GONE);
                            btnCancelarVenta.setVisibility(View.GONE);
                            text_guardarC.setVisibility(View.GONE);
                            titleconfirmar.setVisibility(View.GONE);
                            btnImprimirVenta.setVisibility(View.VISIBLE);
                            btnNuevaVenta.setVisibility(View.VISIBLE);
                            text_inprimirC.setVisibility(View.VISIBLE);
                            titleimprimir.setVisibility(View.VISIBLE);

                }catch (Exception ex){
                    Toast.makeText(getActivity(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Correlativo>> call, Throwable t) {

                modal_ErrorServidor.show();
                btnAceptarError   = modal_ErrorServidor.findViewById(R.id.btnAceptarError);
                btnAceptarError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modal_ErrorServidor.dismiss();
                    }
                });
                modal_CV_Articulo.dismiss();

            }
        });

    }

    /**
     * @APISERVICE:ListaArticuloPromocion
     */
    private void getArticuloG(){

        Call<List<Articulo>> call = mAPIService.getArticuloG();

        call.enqueue(new Callback<List<Articulo>>() {
            @Override
            public void onResponse(Call<List<Articulo>> call, Response<List<Articulo>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Articulo Gratuito: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    articuloList = response.body();

                    articuloAdapter = new ArticuloAdapter(articuloList, new ArticuloAdapter.OnItemClickListener() {
                        @Override
                        public int onItemClick(Articulo item, boolean isSelected) {

                            /**
                             * @PRODUCTOSELECCIONADO:Agregar_Remover
                             */
                            item.setSeleccionado(isSelected);

                            if (isSelected) {
                                articuloSeleccionados.add(item);
                            } else {
                                articuloSeleccionados.remove(item);
                            }

                            /**
                             * @VISUALIZAR:BotonIrCarritoCompra
                             */
                            if (!articuloSeleccionados.isEmpty()) {
                                btncarritocompra.setVisibility(View.VISIBLE);
                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                                layoutParams.bottomMargin = 100;
                                linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);
                            } else {
                                btncarritocompra.setVisibility(View.GONE);
                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                                layoutParams.bottomMargin = 0;
                                linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);
                                limpiarDatos();
                            }
                            return 0;
                        }

                    });

                    articuloAdapter.setOnDeseleccionarArticuloListener(new ArticuloAdapter.OnDeseleccionarProductoListener() {
                        @Override
                        public void onDeseleccionarProducto(Articulo articulo) {
                            /**
                             * @DESELECCIONARPRODUCTO:RestablecerProductosInicial
                             */
                            cantidadesSeleccionadas.put(articulo.getArticuloID(), 1);
                            nuevosPrecios.put(articulo.getArticuloID(), articulo.getPrecio_Venta());
                            carritoAdapter.notifyDataSetChanged();
                        }
                    });

                    /**
                     * @MOSTRARPEODUCTOS:Columnas3
                     */
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
                    recyclerArticulo.setLayoutManager(layoutManager);
                    recyclerArticulo.setAdapter(articuloAdapter);
                    articuloAdapter.notifyDataSetChanged();

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Articulo>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Articulo - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:ListaArticulo
     */
    private void getArticulo(){

        Call<List<Articulo>> call = mAPIService.getArticulo();

        call.enqueue(new Callback<List<Articulo>>() {
            @Override
            public void onResponse(Call<List<Articulo>> call, Response<List<Articulo>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Articulo: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    articuloList = response.body();

                    articuloAdapter = new ArticuloAdapter(articuloList, new ArticuloAdapter.OnItemClickListener() {
                        @Override
                        public int onItemClick(Articulo item, boolean isSelected) {

                            /**
                             * @PRODUCTOSELECCIONADO:Agregar_Remover
                             */
                            item.setSeleccionado(isSelected);

                            if (isSelected) {
                                articuloSeleccionados.add(item);
                            } else {
                                articuloSeleccionados.remove(item);
                            }

                            /**
                             * @VISUALIZAR:BotonIrCarritoCompra
                             */
                            if (!articuloSeleccionados.isEmpty()) {
                                btncarritocompra.setVisibility(View.VISIBLE);
                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                                layoutParams.bottomMargin = 100;
                                linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);
                            } else {
                                btncarritocompra.setVisibility(View.GONE);
                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                                layoutParams.bottomMargin = 0;
                                linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);
                                limpiarDatos();
                            }
                            return 0;
                        }

                    });

                    articuloAdapter.setOnDeseleccionarArticuloListener(new ArticuloAdapter.OnDeseleccionarProductoListener() {
                        @Override
                        public void onDeseleccionarProducto(Articulo articulo) {
                            /**
                             * @DESELECCIONARPRODUCTO:RestablecerProductosInicial
                             */
                            cantidadesSeleccionadas.put(articulo.getArticuloID(), 1);
                            nuevosPrecios.put(articulo.getArticuloID(), articulo.getPrecio_Venta());
                            carritoAdapter.notifyDataSetChanged();
                        }
                    });

                    /**
                     * @MOSTRARPEODUCTOS:Columnas3
                     */
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
                    recyclerArticulo.setLayoutManager(layoutManager);
                    recyclerArticulo.setAdapter(articuloAdapter);
                    articuloAdapter.notifyDataSetChanged();

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Articulo>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Articulo - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:ListaFamilia
     */
    private void getFamilia(){

        Call<List<Familia>> call = mAPIService.getFamilia();

        call.enqueue(new Callback<List<Familia>>() {
            @Override
            public void onResponse(Call<List<Familia>> call, Response<List<Familia>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Familia: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    familiaList = response.body();

                    familiaAdapter = new FamiliaAdapter(familiaList, new FamiliaAdapter.OnItemClickListener() {
                        @Override
                        public int onItemClick(Familia item) {

                            articuloFiltrados = new ArrayList<>();

                            for (Articulo articulo : articuloList) {
                                if (articulo.getFamiliaID().equals(item.getFamiliaID())) {
                                    articuloFiltrados.add(articulo);
                                }
                            }

                            articuloAdapter.setProductos(articuloFiltrados);
                            articuloAdapter.notifyDataSetChanged();

                            /**
                             * @BOTON:DesactivarEstado
                             */
                            isTodoProductoSelected = false;
                            btnTodoArticulo.setBackgroundColor(Color.parseColor("#999999"));
                            btnTodoArticulo.setTextColor(Color.parseColor("#FFFFFF"));

                            /**
                             * @BOTON:DesactivarEstado
                             */
                            isTodoProductoSelected = false;
                            btnfiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                            btnfiltrar.setColorFilter(getResources().getColor(R.color.white));

                            return 0;
                        }
                    });

                    recyclerFamilia.setAdapter(familiaAdapter);
                    familiaAdapter.notifyDataSetChanged();

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Familia>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Familia - RED - WIFI", Toast.LENGTH_SHORT).show();
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

                            inputPlaca.setText(item.getNroPLaca());
                            inputTarjeta.setText(item.getTarjetaID());
                            inputCliente.setText(item.getClienteID());
                            inputRazSocial.setText(item.getClienteRZ());
                            inputDireccion.setText(item.getClienteDR());
                            monto = item.getSaldo();

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
     * @Spinner:TipoPago
     */
    private void  TipoPago_Doc(){
        Resources res = getResources();
        tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) GlobalInfo.gettipopagoList10, res);
        SpinnerTPago.setAdapter(tipoPagoAdapter);
    }

    /**
     * @Listado:ClienteDNI
     */
    private void ClienteDNI(){

        Call<List<LClientes>> call = mAPIService.getClienteDNI();

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Cliente DNI: " + response.code(), Toast.LENGTH_SHORT).show();
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
     * @APISERVICE:ClienteDNI
     */
    private  void findClienteDNI(String id){

        Call<List<LClientes>> call = mAPIService.findClienteDNI(id);

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Cliente DNI: " + response.code(), Toast.LENGTH_SHORT).show();
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
     * @APISERVICE:ListadoClienteRUC
     */
    private void ClienteRUC(){

        Call<List<LClientes>> call = mAPIService.getClienteRUC();

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Cliente Ruc: " + response.code(), Toast.LENGTH_SHORT).show();
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
     * @APISERVICE:BuscarClienteRUC
     */
    private  void findClienteRUC(String id){

        Call<List<LClientes>> call = mAPIService.findClienteRUC(id);

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Cliente Ruc: " + response.code(), Toast.LENGTH_SHORT).show();
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
     * @APISERVICE:GrabaraVentaMarketCA
     */
    private void grabarVentaMarketCA(String _tipoDocumento, String _serieDocumento, String _nroDocumento, String _terminalID,
                                     String _clienteID, String _clienteRUC, String _clienteRZ, String _clienteDR, String _fechadocumento,
                                     Double _mtoSubTotal, Double _mtoImpuesto, Double _mtoTotal, String _nroPlaca, String _userID,
                                     Integer _pagoID, Integer _tarjetaID, String _TarjetaDS, Double _mtoPagoPEN, Double _mtoPagoUSD,
                                     String _observacionPag, String _nroTarjetaNotaD, String _observacion, Integer _nroItem){

        /** Cuando los datos se envian vacio se replazara con un ' - ' */

        if (_clienteRUC.isEmpty()) {_clienteRUC = "-";}

        if (_clienteDR.isEmpty()) {_clienteDR = "-";}

        if (_TarjetaDS.isEmpty()) {_TarjetaDS = "-";}

        if (_nroTarjetaNotaD.isEmpty()) {_nroTarjetaNotaD = "-";}

        if (_observacion.isEmpty()) {_observacion = "-";}

        Call<List<VentaMarketCA>> call = mAPIService.getMarketCA(_tipoDocumento, _serieDocumento, _nroDocumento, _terminalID,
                _clienteID, _clienteRUC, _clienteRZ, _clienteDR, _fechadocumento,
                _mtoSubTotal, _mtoImpuesto, _mtoTotal, _nroPlaca, _userID,
                _pagoID, _tarjetaID, _TarjetaDS, _mtoPagoPEN, _mtoPagoUSD, _observacionPag, _nroTarjetaNotaD, _observacion, _nroItem);

        call.enqueue(new Callback<List<VentaMarketCA>>() {
            @Override
            public void onResponse(Call<List<VentaMarketCA>> call, Response<List<VentaMarketCA>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error CA: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<VentaMarketCA>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE CA - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });

    }

    /**
     * @APISERVICE:GrabaraVentaMarketDA
     */
    private void grabarVentaMarketDA(String _tipoDocumento, String _serieDocumento, String _nroDocumento, Integer _nroItem,
                                     String _articuloID, String _productoDs, String _uniMed, String _terminalID, String _fechadocumento,
                                     Double _precio1, Double _cantidad, Double _mtoSubTotal, Double _mtoImpuesto, Double _mtoTotal){

        Call<List<VentaMarketDA>> call = mAPIService.getMarketDA(_tipoDocumento, _serieDocumento, _nroDocumento, _nroItem,
                                                                 _articuloID, _productoDs, _uniMed, _terminalID, _fechadocumento,
                                                                 _precio1, _cantidad, _mtoSubTotal, _mtoImpuesto, _mtoTotal);

        call.enqueue(new Callback<List<VentaMarketDA>>() {
            @Override
            public void onResponse(Call<List<VentaMarketDA>> call, Response<List<VentaMarketDA>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error DA: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<VentaMarketDA>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE DA - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void imprimirMarket(String tipopapel, String _TipoDocumento, String _NroDocumento, String _FechaDocumento, Integer _Turno,
                                String _Cajero, Double _MtoTotal, Double _MtoSubTotal, Double _MtoImpuesto,
                                String _ClienteID, String _ClienteRZ, String _ClienteDR, String _NroPlaca,
                                String _FechaQR, Integer _PagoID, Integer _TarjetaCreditoID, String _OperacionREF, Double _mtoSoles, StringBuilder MarketDABuilder,String _obervacion,String _NTarjND){

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

        switch (_TipoDocumento) {
            case "01" :
                TipoDNI = "6";
                break;
            case "98" :
                TipoDNI = "0";
                break;
        }

        opGratruitas = "0.00";

        if (mnTipoPago.equals("G")){
            opGratruitas = String.format("%.2f",_MtoTotal);
            _MtoTotal = 0.00;
        }

        String MtoSubTotalFF = String.format("%.2f",_MtoSubTotal);

        String MtoImpuestoFF = String.format("%.2f",_MtoImpuesto);

        String MtoTotalFF    = String.format("%.2f",_MtoTotal);

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

        int logoSize = (tipopapel.equals("80mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("65mm") ? GlobalInfo.getTerminalImageW10 : 400);

        Printama.with(getContext()).connect(printama -> {
            switch (tipopapel) {

                case "58mm":
                case "80mm":

                    switch (_TipoDocumento) {

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

                        case "01":
                            printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                            break;

                        case "03":
                            if (mnTipoPago.equals("G")){
                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                            }
                            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                            break;

                        case "99" :
                            printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                            break;

                    }
                    printama.printTextlnBold(_NroDocumento, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha - Hora : " + _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
                    printama.printTextln("Cajero       : " + _Cajero, Printama.LEFT);
                    if (!_NroPlaca.isEmpty()) {
                        printama.printTextln("Nro. PLaca   : " + _NroPlaca, Printama.LEFT);
                    }
                    switch (_TipoDocumento) {

                        case "01":

                            printama.printTextln("RUC          : " + _ClienteID, Printama.LEFT);
                            printama.printTextln("Razon Social : " + _ClienteRZ, Printama.LEFT);

                            if (!_ClienteDR.isEmpty()) {
                                printama.printTextln("Dirección    : " + _ClienteDR, Printama.LEFT);
                            }

                            break;

                        case "03":

                            if (CVarios.equals(_ClienteID)) {

                            } else {

                                printama.printTextln("DNI          : " + _ClienteID, Printama.LEFT);
                                printama.printTextln("Nombres      : " + _ClienteRZ, Printama.LEFT);

                                if (!_ClienteDR.isEmpty()) {
                                    printama.printTextln("Dirección    : " + _ClienteDR, Printama.LEFT);
                                }

                            }

                            break;

                        case "99" :

                            if (!_obervacion.isEmpty()) {
                                printama.printTextln("Observación  : " + _obervacion, Printama.LEFT);
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
                    printama.printTextlnBold(MarketDABuilder.toString(),Printama.RIGHT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    switch (_TipoDocumento) {
                        case "01" :

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

                            }

                            printama.setSmallText();
                            printama.printTextln("SON: " + LetraSoles, Printama.LEFT);
                            printama.printTextln("                 ", Printama.CENTER);
                            if(GlobalInfo.getVistaQR) {
                                QRCodeWriter writer = new QRCodeWriter();
                                BitMatrix bitMatrix;
                                try {
                                    bitMatrix = writer.encode(QRGenerado, BarcodeFormat.QR_CODE, 150, 150);
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
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "https://cpesven.apisven.com");
                            break;
                        case "03" :

                            if (mnTipoPago.equals("G")){
                                printama.printTextlnBold("OP. GRATUITAS: S/ " + opGratruitas, Printama.RIGHT);

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

                            }

                            printama.setSmallText();
                            printama.printTextln("SON: " + LetraSoles, Printama.LEFT);
                            printama.printTextln("                 ", Printama.CENTER);
                            if(GlobalInfo.getVistaQR) {
                                QRCodeWriter writerB = new QRCodeWriter();
                                BitMatrix bitMatrixB;
                                try {
                                    bitMatrixB = writerB.encode(QRGenerado, BarcodeFormat.QR_CODE, 150, 150);
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
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "https://cpesven.apisven.com");
                            break;

                        case "99" :
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

                        case "01":
                            printama.printTextlnBold("FACTURA DE VENTA ELECTRONICA", Printama.CENTER);
                            break;

                        case "03":
                            if (mnTipoPago.equals("G")){
                                printama.printTextlnBold("***** TRANSFERENCIA GRATUITA *****", Printama.CENTER);
                            }
                            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);
                            break;

                        case "99" :
                            printama.printTextlnBold("NOTA DE DESPACHO", Printama.CENTER);
                            break;

                    }
                    printama.printTextln(_NroDocumento, Printama.CENTER);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextln("Fecha - Hora : " + _FechaDocumento + "  Turno: " + _Turno, Printama.LEFT);
                    printama.printTextln("Cajero       : " + _Cajero, Printama.LEFT);
                    if (!_NroPlaca.isEmpty()) {
                        printama.printTextln("Nro. PLaca   : " + _NroPlaca, Printama.LEFT);
                    }
                    switch (_TipoDocumento) {

                        case "01":

                            printama.printTextln("RUC          : " + _ClienteID, Printama.LEFT);
                            printama.printTextln("Razon Social : " + _ClienteRZ, Printama.LEFT);

                            if (!_ClienteDR.isEmpty()) {
                                printama.printTextln("Dirección    : " + _ClienteDR, Printama.LEFT);
                            }

                            break;

                        case "03":

                            if (CVarios.equals(_ClienteID)) {

                            } else {

                                printama.printTextln("DNI          : " + _ClienteID, Printama.LEFT);
                                printama.printTextln("Nombres      : " + _ClienteRZ, Printama.LEFT);

                                if (!_ClienteDR.isEmpty()) {
                                    printama.printTextln("Dirección    : " + _ClienteDR, Printama.LEFT);
                                }

                            }

                            break;

                        case "99" :

                            if (!_obervacion.isEmpty()) {
                                printama.printTextln("Observación  : " + _obervacion, Printama.LEFT);
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
                    printama.printTextln( MarketDABuilder.toString(),Printama.RIGHT);
                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    switch (_TipoDocumento) {
                        case "01" :

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

                            }

                            printama.setSmallText();
                            printama.printTextln("SON: " + LetraSoles, Printama.LEFT);
                            printama.printTextln("                 ", Printama.CENTER);
                            if(GlobalInfo.getVistaQR) {
                                QRCodeWriter writer = new QRCodeWriter();
                                BitMatrix bitMatrix;
                                try {
                                    bitMatrix = writer.encode(QRGenerado, BarcodeFormat.QR_CODE, 150, 150);
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
                                        printama.printImage(Printama.RIGHT, bitmap, 150);
                                    }
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            }
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "https://cpesven.apisven.com");
                            break;
                        case "03" :

                                if (mnTipoPago.equals("G")){
                                    printama.printTextln("OP. GRATUITAS: S/ " + opGratruitas, Printama.RIGHT);

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

                            }

                            printama.setSmallText();
                            printama.printTextln("SON: " + LetraSoles, Printama.LEFT);
                            printama.printTextln("                 ", Printama.CENTER);
                            if(GlobalInfo.getVistaQR) {
                                QRCodeWriter writerB = new QRCodeWriter();
                                BitMatrix bitMatrixB;
                                try {
                                    bitMatrixB = writerB.encode(QRGenerado, BarcodeFormat.QR_CODE, 150, 150);
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
                                        printama.printImage(Printama.RIGHT, bitmap, 150);
                                    }
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            }
                            printama.setSmallText();
                            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "https://cpesven.apisven.com");
                            break;

                        case "99" :
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
                            break;
                    }

                    break;

            }
            printama.feedPaper();
            printama.cutPaper();
            printama.close();

            for (Articulo articulo : articuloSeleccionados) {
                articulo.setSeleccionado(false);
            }

            articuloSeleccionados.clear();
            cantidadesSeleccionadas.clear();

            articuloAdapter.notifyDataSetChanged();
            carritoAdapter.notifyDataSetChanged();

            modalCarrito.dismiss();
            modal_CV_Articulo.dismiss();
            btncarritocompra.setVisibility(View.GONE);

        }, this::showToast);

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
    public void onResume() {
        super.onResume();
        nfcUtil.onResume();

        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(getActivity(), this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V, null);
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

    /**
     * @SCANNEAR:CódigoProductos
     */

    private void onBackPressed() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Realmente deseas retroceder?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                })
                .show();
    }

    private void startScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("ESCANEAR CODIGO");
        options.setCameraId(0);
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        options.setCaptureActivity(CaptureActivityPortrait.class);
        options.setBarcodeImageEnabled(false);
        barcodeLauncher.launch(options);
    }

}