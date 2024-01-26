package com.anggastudio.sample.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
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
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.PasswordChecker;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;
import com.anggastudio.sample.WebApiSVEN.Models.ClienteCredito;
import com.anggastudio.sample.WebApiSVEN.Models.ClientePrecio;
import com.anggastudio.sample.WebApiSVEN.Models.Familia;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticuloFragment extends Fragment {

    private APIService mAPIService;

    private NFCUtil nfcUtil;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    LRegistroClienteAdapter lRegistroClienteAdapter;

    List<ClientePrecio> clientePrecioList;

    boolean isTodoProductoSelected = false;

    List<Users> usersListNFC;

    List<Familia> familiaList;
    FamiliaAdapter familiaAdapter;

    List<Articulo> articuloList;
    ArticuloAdapter articuloAdapter;

    List<Articulo> articuloFiltrados;

    List<Articulo> articuloSeleccionados = new ArrayList<Articulo>();

    RecyclerView recyclerFamilia, recyclerArticulo, recyclerCarrito, recyclerLCliente,recyclerListaClientesAfiliados,recyclerLClienteCredito;
    Button btnTodoArticulo,
            btnCancelarNFC,btnAceptarNFC,buscarListNFC,
            btnBoleta,btnCancelarBoleta,btnAgregarBoleta,btnGenerarBoleta,buscarPlacaBoleta,buscarDNIBoleta,btnCancelarLCliente,
            btnFactura,buscarRUCFactura,buscarPlacaFactura,btnCancelarFactura,btnAgregarFactura,
            btnNotaDespacho,btnCancelarNotaDespacho,btnAgregarNotaDespacho;

    CardView btncarritocompra;

    LinearLayout linearLayoutRecyclerArticulo,btnVolverCompra,btnconfirmarventa,btnSeleccionCliente,btnGuardarVenta,btnCancelarVenta,btnImprimirVenta,btnNuevaVenta;

    Map<String, Integer> cantidadesSeleccionadas = new HashMap<>();
    Map<String, Double> nuevosPrecios = new HashMap<>();

    TextView totalmontoCar,textMensajePEfectivo,nombreCliente,text_guardarC,text_inprimirC,titleconfirmar,titleimprimir;
    Dialog modal_CV_Articulo,modalCarrito, modalBoleta, modalClienteDNI, modalNFCLogin, modallistNFC,modalFactura,modalClienteRUC,modalNotaDespacho,modalClienteCredito;

    CarritoAdapter carritoAdapter;
    SearchView BuscarProducto,btnBuscadorClienteRZ,BuscarRazonSocial;

    TextInputLayout alertuserNFC,alertpasswordNFC,alertPlaca,alertDNI,alertNombre,alertPEfectivo,alertOperacion,alertSelectTPago,
            alertRUC,alertRazSocial,
            alertCPlaca,alertCTarjeta,alertCCliente,alertCRazSocial;

    TextInputEditText usuarioNFC,contraseñaNFC,inputNFC,inputPlaca,inputDNI,inputNombre,inputDireccion,inputObservacion,inputOperacion,inputPEfectivo,
            inputRUC,inputRazSocial,
            inputCPlaca,input_CNTarjeta,inputCCliente,inputCRazSocial,inputCDireccion,inputCKilometraje,inputCObservacion;

    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioNombreFormaPago;

    Spinner SpinnerTPago;
    LClienteAdapter lclienteAdapter;
    TipoPagoAdapter tipoPagoAdapter;

    TipoPago tipoPago;

    String usuarioUserNFC,contraseñaUserNFC,campoDNI,campoNombre,campoDireccion,campoObservacion,campoPEfectivo,campoOperacion,CharNombreFormaPago,tipoPagoO;

    ClienteCreditoAdapter clienteCreditoAdapter;

    Double monto;

    ImageButton btnfiltrar;

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
        btnTodoArticulo  = view.findViewById(R.id.btnTodoArticulo);
        btncarritocompra = view.findViewById(R.id.btncarritocompra);
        linearLayoutRecyclerArticulo = view.findViewById(R.id.linearLayoutRecyclerArticulo);

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
        recyclerArticulo = view.findViewById(R.id.recyclerProducto);
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

                nombreCliente       = modalCarrito.findViewById(R.id.nombreCliente);

                btnBoleta.setVisibility(View.GONE);
                btnFactura.setVisibility(View.GONE);
                btnNotaDespacho.setVisibility(View.GONE);
                btnSeleccionCliente.setVisibility(View.VISIBLE);

                btnVolverCompra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalCarrito.dismiss();
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
                        buscarListNFC     = modalBoleta.findViewById(R.id.buscarListNFC);

                        inputDNI.setEnabled(true);
                        inputNombre.setEnabled(true);
                        alertDNI.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertNombre.setBoxBackgroundColorResource(R.color.transparentenew);

                        /**
                         * @MODAL:LoginDescuentoNFC
                         */
                        modalNFCLogin = new Dialog(getContext());
                        modalNFCLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalNFCLogin.setContentView(R.layout.modal_nfc_login);
                        modalNFCLogin.setCancelable(false);

                        buscarListNFC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                modalNFCLogin.show();

                                btnCancelarNFC    = modalNFCLogin.findViewById(R.id.btnCancelarAnular);
                                btnAceptarNFC     = modalNFCLogin.findViewById(R.id.btnAceptarIngreso);
                                usuarioNFC        = modalNFCLogin.findViewById(R.id.inputUserAnulado);
                                contraseñaNFC     = modalNFCLogin.findViewById(R.id.inputContraseñaAnulado);
                                alertuserNFC      = modalNFCLogin.findViewById(R.id.alertUserAnulado);
                                alertpasswordNFC  = modalNFCLogin.findViewById(R.id.alertContraseñaAnulado);

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

                                        usuarioUserNFC    = usuarioNFC.getText().toString();
                                        contraseñaUserNFC = contraseñaNFC.getText().toString();

                                        if (usuarioUserNFC.isEmpty()) {
                                            alertuserNFC.setError("El campo usuario es obligatorio");
                                            return;
                                        } else if (contraseñaUserNFC.isEmpty()) {
                                            alertpasswordNFC.setError("El campo contraseña es obligatorio");
                                            return;
                                        }

                                        /**
                                         * @Usuario-Autorizado
                                         */
                                        findUsers(usuarioUserNFC);

                                        alertuserNFC.setErrorEnabled(false);
                                        alertpasswordNFC.setErrorEnabled(false);

                                    }
                                });

                            }
                        });

                        /**
                         * @DETECTAR:EtiquietaNFC
                         */
                        inputNFC.setKeyListener(null);
                        insertNFC();

                        /**
                         * @DETECTAR:NFC
                         * @OBTENER:DatosClientes
                         */
                        inputNFC.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.length() == 16) {
                                    String nfcCode = s.toString();

                                    findClientePrecioDNI(nfcCode, String.valueOf(GlobalInfo.getterminalCompanyID10));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

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

                                String campoPlaca     = inputPlaca.getText().toString();
                                String campoDNI       = inputDNI.getText().toString();
                                String campoNombre    = inputNombre.getText().toString();
                                String campoPEfectivo = inputPEfectivo.getText().toString();
                                String campoOperacion = inputOperacion.getText().toString();
                                String nfc            = inputNFC.getText().toString();

                                    int checkedRadioButtonId = radioFormaPago.getCheckedRadioButtonId();

                                    if (campoPlaca.isEmpty()) {

                                        alertPlaca.setError("* El campo Placa es obligatorio");
                                        return;
                                    } else if (campoDNI.isEmpty()) {

                                        alertDNI.setError("* El campo DNI es obligatorio");
                                        return;
                                    } else if (campoDNI.length() < 8) {

                                        alertDNI.setError("* El DNI debe tener 8 dígitos");
                                        return;
                                    } else if (campoNombre.isEmpty()) {

                                        alertNombre.setError("* El campo Nombre es obligatorio");
                                        return;
                                    } else if (campoNombre.length() < 8) {

                                        alertNombre.setError("* El Nombre debe tener mínino 8 dígitos");
                                        return;
                                    } else if (checkedRadioButtonId == radioTarjeta.getId()) {

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

                                nombreCliente.setText(inputNombre.getText().toString());

                                  /*  detalleVenta.setNroPlaca(inputPlaca.getText().toString());
                                    detalleVenta.setClienteID(inputDNI.getText().toString());
                                    detalleVenta.setClienteRUC("");
                                    detalleVenta.setClienteRS(inputNombre.getText().toString());
                                    detalleVenta.setClienteDR(inputDireccion.getText().toString());

                                    detalleVenta.setObservacion(inputObservacion.getText().toString());

                                    detalleVenta.setTipoPago(radioNombreFormaPago.getText().toString().substring(0, 1));

                                    detalleVenta.setMtoSaldoCredito(0.00);
                                    detalleVenta.setTarjetaND("");
                                    detalleVenta.setTarjetaCredito("");
                                    detalleVenta.setOperacionREF("");
                                    detalleVenta.setMontoSoles(0.00);
                                    detalleVenta.setRfid("1");*/


                                    String NombreFormaPago = radioNombreFormaPago.getText().toString();

                                    if (NombreFormaPago.equals("Tarjeta")) {

                                        Double dosDecimales = Double.valueOf(campoPEfectivo);
                                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                     //   detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                     //   detalleVenta.setOperacionREF(inputOperacion.getText().toString());

                                        if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))) {
                                            alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                            return;
                                        } else {
                                           // detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                        }


                                    } else if (NombreFormaPago.equals("Credito")) {

                                        Double dosDecimales = Double.valueOf(campoPEfectivo);
                                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                        if (dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))) {
                                            alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                            return;
                                        } else {
                                          //  detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                        }
                                    }

                                    if (!nfc.isEmpty() && nfc.equals(GlobalInfo.getRfIdCPrecio10)) {
                                       // detalleVenta.setRfid(GlobalInfo.getRfIdCPrecio10);
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
                        buscarListNFC      = modalFactura.findViewById(R.id.buscarListNFC);

                        inputRUC.setEnabled(true);
                        inputRazSocial.setEnabled(true);
                        alertRUC.setBoxBackgroundColorResource(R.color.transparentenew);
                        alertRazSocial.setBoxBackgroundColorResource(R.color.transparentenew);

                        /**
                         * @MODAL:LoginDescuentoNFC
                         */
                        modalNFCLogin = new Dialog(getContext());
                        modalNFCLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalNFCLogin.setContentView(R.layout.modal_nfc_login);
                        modalNFCLogin.setCancelable(false);

                        buscarListNFC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                modalNFCLogin.show();

                                btnCancelarNFC    = modalNFCLogin.findViewById(R.id.btnCancelarAnular);
                                btnAceptarNFC     = modalNFCLogin.findViewById(R.id.btnAceptarIngreso);
                                usuarioNFC        = modalNFCLogin.findViewById(R.id.inputUserAnulado);
                                contraseñaNFC     = modalNFCLogin.findViewById(R.id.inputContraseñaAnulado);
                                alertuserNFC      = modalNFCLogin.findViewById(R.id.alertUserAnulado);
                                alertpasswordNFC  = modalNFCLogin.findViewById(R.id.alertContraseñaAnulado);

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

                                        usuarioUserNFC    = usuarioNFC.getText().toString();
                                        contraseñaUserNFC = contraseñaNFC.getText().toString();

                                        if (usuarioUserNFC.isEmpty()) {
                                            alertuserNFC.setError("El campo usuario es obligatorio");
                                            return;
                                        } else if (contraseñaUserNFC.isEmpty()) {
                                            alertpasswordNFC.setError("El campo contraseña es obligatorio");
                                            return;
                                        }

                                        /**
                                         * @Usuario-Autorizado
                                         */
                                        findUsers(usuarioUserNFC);

                                        alertuserNFC.setErrorEnabled(false);
                                        alertpasswordNFC.setErrorEnabled(false);

                                    }
                                });

                            }
                        });

                        /**
                         * @DETECTAR:EtiquietaNFC
                         */
                        inputNFC.setKeyListener(null);
                        insertNFC();

                        /**
                         * @DETECTAR:NFC
                         * @OBTENER:DatosClientes
                         */
                        inputNFC.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.length() == 16) {
                                    String nfcCode = s.toString();

                                    findClientePrecioRUC(nfcCode, String.valueOf(GlobalInfo.getterminalCompanyID10));

                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

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

                        /**
                         * @AGREGAR:Factura
                         */
                        btnAgregarFactura.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

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
                                        }  else if (campoRazSocial.length() < 11 ) {

                                            alertRazSocial.setError("* La Razon Social debe tener mínino 11 dígitos");
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

                                       /* detalleVenta.setNroPlaca(inputPlaca.getText().toString());
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
                                        detalleVenta.setRfid("1");*/


                                        String NombreFormaPago = radioNombreFormaPago.getText().toString();

                                        if (NombreFormaPago.equals("Tarjeta")){

                                           // detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                           // detalleVenta.setOperacionREF(inputOperacion.getText().toString());

                                            Double dosDecimales = Double.valueOf(campoPEfectivo);
                                            DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                            //detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                            //detalleVenta.setOperacionREF(inputOperacion.getText().toString());

                                            if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                                return;
                                            }else{
                                               // detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                            }


                                        }else if (NombreFormaPago.equals("Credito")) {

                                            Double dosDecimales = Double.valueOf(campoPEfectivo);
                                            DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                            if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                                alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                                return;
                                            }else{
                                               // detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));
                                            }

                                        }

                                        if(!nfc.isEmpty() && nfc.equals(GlobalInfo.getRfIdCPrecio10)){
                                           // detalleVenta.setRfid(GlobalInfo.getRfIdCPrecio10);
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

                        modalNotaDespacho.show();

                        btnCancelarNotaDespacho   = modalNotaDespacho.findViewById(R.id.btnCancelarNotaDespacho);
                        btnAgregarNotaDespacho    = modalNotaDespacho.findViewById(R.id.btnAgregarNotaDespacho);

                        inputCPlaca               = modalNotaDespacho.findViewById(R.id.inputCPlaca);
                        input_CNTarjeta           = modalNotaDespacho.findViewById(R.id.input_CNTarjeta);
                        inputCCliente             = modalNotaDespacho.findViewById(R.id.inputCCliente);
                        inputCRazSocial           = modalNotaDespacho.findViewById(R.id.inputCRazSocial);
                        inputCDireccion           = modalNotaDespacho.findViewById(R.id.inputCDireccion);
                        inputCKilometraje         = modalNotaDespacho.findViewById(R.id.inputCKilometraje);
                        inputCObservacion         = modalNotaDespacho.findViewById(R.id.inputCObservacion);

                        alertCPlaca               = modalNotaDespacho.findViewById(R.id.alertPlaca);
                        alertCTarjeta             = modalNotaDespacho.findViewById(R.id.alertTarjeta);
                        alertCCliente             = modalNotaDespacho.findViewById(R.id.alertCCliente);
                        alertCRazSocial           = modalNotaDespacho.findViewById(R.id.alertCRazSocial);

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

                                inputCPlaca.setText("000-0000");
                                input_CNTarjeta.getText().clear();
                                inputCCliente.getText().clear();
                                inputCRazSocial.getText().clear();
                                inputCDireccion.getText().clear();
                                inputCKilometraje.getText().clear();
                                inputCObservacion.getText().clear();

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

                                        String campoNCliente   = inputCCliente.getText().toString();
                                        String campoCRazSocial = inputCRazSocial.getText().toString();
                                        String campoPlaca      = inputCPlaca.getText().toString();

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

                                     /*   detalleVenta.setNroPlaca(inputCPlaca.getText().toString());
                                        detalleVenta.setTipoPago("C");
                                        detalleVenta.setClienteID(inputCCliente.getText().toString());
                                        detalleVenta.setClienteRUC("");
                                        detalleVenta.setClienteRS(inputCRazSocial.getText().toString());
                                        detalleVenta.setClienteDR(inputCDireccion.getText().toString());
                                        detalleVenta.setObservacion(inputCObservacion.getText().toString());
                                        detalleVenta.setKilometraje(inputCKilometraje.getText().toString());
                                        detalleVenta.setTarjetaND(input_CNTarjeta.getText().toString());
                                        detalleVenta.setMtoSaldoCredito(monto);
                                        detalleVenta.setTarjetaCredito("");
                                        detalleVenta.setOperacionREF("");
                                        detalleVenta.setMontoSoles(0.00);
                                        detalleVenta.setRfid("1");*/

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
                                /**
                                 * @Guardar:Venta
                                 */
                                btnGuardarVenta.setVisibility(View.GONE);
                                btnCancelarVenta.setVisibility(View.GONE);
                                text_guardarC.setVisibility(View.GONE);
                                titleconfirmar.setVisibility(View.GONE);
                                btnImprimirVenta.setVisibility(View.VISIBLE);
                                btnNuevaVenta.setVisibility(View.VISIBLE);
                                text_inprimirC.setVisibility(View.VISIBLE);
                                titleimprimir.setVisibility(View.VISIBLE);

                                Toast.makeText(getContext(), "Se Guardo Venta Correctamente", Toast.LENGTH_SHORT).show();
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

                                  imprimirComprobante(articuloSeleccionados, totalmontoCar.getText().toString(),  campoDNI, campoNombre,campoDireccion,campoObservacion,campoPEfectivo,campoOperacion,CharNombreFormaPago,tipoPagoO);

                                  ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                                  layoutParams.bottomMargin = 0;
                                  linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);

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

        return view;
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
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
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
     * @Listado:ClienteDNI
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
     * @Spinner:TipoPago
     */
    private void  TipoPago_Doc(){
        Resources res = getResources();
        tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) GlobalInfo.gettipopagoList10, res);
        SpinnerTPago.setAdapter(tipoPagoAdapter);
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
     * @IMPRIMIR:ComprobanteVenta
     */
    private void imprimirComprobante(List<Articulo> articuloSeleccionados,String totalmontoCar,  String campoDNI, String campoNombre, String campoDireccion, String campoObservacion, String campoPEfectivo, String campoOperacion, String CharNombreFormaPago,String tipoPagoO){

        File file = new File("/storage/emulated/0/appSven/logo.jpg");
        String rutaImagen="/storage/emulated/0/appSven/logo.jpg";
        if(!file.exists()){
            rutaImagen = "/storage/emulated/0/appSven/logo.png";
        }
        Bitmap logoRobles = BitmapFactory.decodeFile(rutaImagen);
        String TipoDNI = "1";
        String CVarios = "11111111";

        String NameCompany = GlobalInfo.getNameCompany10;
        String RUCCompany = GlobalInfo.getRucCompany10;

        String AddressCompany = (GlobalInfo.getAddressCompany10 != null) ? GlobalInfo.getAddressCompany10 : "";
        String[] partesAddress = AddressCompany.split(" - " , 2);
        String Address1 = partesAddress[0];
        String Address2 = partesAddress[1];

        String BranchCompany = (GlobalInfo.getBranchCompany10 != null) ? GlobalInfo.getBranchCompany10 : "";
        String[] partesBranch = BranchCompany.split(" - " , 2);
        String Branch1 = partesBranch[0];
        String Branch2 = partesBranch[1];


        Printama.with(getContext()).connect(printama -> {

            printama.printTextln("                 ", Printama.CENTER);
            printama.printImage(logoRobles, 400);
            printama.setSmallText();
            printama.printTextlnBold(NameCompany, Printama.CENTER);
            printama.printTextlnBold("PRINCIPAL: " + Address1, Printama.CENTER);
            printama.printTextlnBold(Address2, Printama.CENTER);
            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
            printama.printTextlnBold(Branch2, Printama.CENTER);
            printama.printTextlnBold("RUC: " + RUCCompany, Printama.CENTER);

            printama.printTextlnBold("BOLETA DE VENTA ELECTRONICA", Printama.CENTER);

            printama.printTextlnBold("0001",Printama.CENTER);
            printama.setSmallText();
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextln("Fecha - Hora : "+ 18/20/23 + "  Turno: "+ 02,Printama.LEFT);
            printama.printTextln("Cajero       : "+ "Cajero" , Printama.LEFT);
            printama.printTextln("DNI          : "+ campoDNI , Printama.LEFT);
            printama.printTextln("Nombres      : "+ campoNombre, Printama.LEFT);
            printama.printTextln("Dirección    : "+ campoDireccion, Printama.LEFT);
            printama.printTextln("Observación  : "+ campoObservacion, Printama.LEFT);
            printama.printDoubleDashedLine();
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextlnBold("PRODUCTO      "+"U/MED   "+"PRECIO   "+"CANTIDAD  "+"IMPORTE",Printama.RIGHT);
            printama.setSmallText();

            StringBuilder ReporteVendedorBuilder = new StringBuilder();


            for (Articulo articulo : articuloSeleccionados) {

                String nombre = articulo.getArticuloDS1();
                String unidadMedida = articulo.getUniMed();
                double precio  = articulo.getPrecio_Venta();
                int  cantidad   = cantidadesSeleccionadas.get(articulo.getArticuloID());
                double importe = precio * cantidad;

                String linnesS = String.format(Locale.getDefault(), "%-48s\n%-9s %4s %8s %7s %12s", nombre, "", unidadMedida, String.format("%.2f", precio), cantidad, String.format("%.2f", importe));
                ReporteVendedorBuilder.append(linnesS).append("\n");

            }
            printama.printTextlnBold( ReporteVendedorBuilder.toString() + "---------",Printama.RIGHT);
            printama.printTextlnBold("TOTAL VENTA: S/ " + totalmontoCar , Printama.RIGHT);
            printama.setSmallText();
            printama.feedPaper();
            printama.close();
            printama.cutPaper();

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

    /** Alerta de Conexión de Bluetooth */
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcUtil.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcUtil.onPause();
    }

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
}