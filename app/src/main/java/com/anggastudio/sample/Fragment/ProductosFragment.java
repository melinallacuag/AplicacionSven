package com.anggastudio.sample.Fragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.CarritoAdapter;
import com.anggastudio.sample.Adapter.CategoriaAdapter;
import com.anggastudio.sample.Adapter.ClienteCreditoAdapter;
import com.anggastudio.sample.Adapter.LClienteAdapter;
import com.anggastudio.sample.Adapter.ProductosAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.CaptureActivityPortrait;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.ClienteCredito;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.ProCategorias;
import com.anggastudio.sample.WebApiSVEN.Models.Productos;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosFragment extends Fragment {

    private APIService mAPIService;
    private NFCUtil nfcUtil;

    boolean isTodoProductoSelected = false;

    Map<Integer, Integer> cantidadesSeleccionadas = new HashMap<>();
    Map<Integer, Double> nuevosPrecios = new HashMap<>();

    List<Productos> productosList;
    List<Productos> productosFiltrados;
    List<Productos> productosSeleccionados = new ArrayList<>();
    ProductosAdapter productosAdapter;

    List<ProCategorias> proCategoriasList;
    CategoriaAdapter categoriaAdapter;

    CarritoAdapter carritoAdapter;

    RecyclerView recyclerProducto,recyclerCategoria,recyclerCarrito,  recyclerLCliente,recyclerLClienteCredito;

    SearchView BuscarProducto, btnBuscadorClienteRZ;

    Button  btntodoproducto,btnconfirmarventa,btnSeleccionCliente;
    CardView btncarritocompra;

    Dialog modalCarrito,modalFactura,modalNotaDespacho,modalClienteRUC,modalClienteCredito;
    TextView totalmontoCar;

    String totalmontoCarCar,campoDNI,campoNombre,campoDireccion,campoObservacion,campoPEfectivo,campoOperacion,CharNombreFormaPago,tipoPagoO;

    ClienteCreditoAdapter clienteCreditoAdapter;

    Double monto;
    /****/
    LClienteAdapter lclienteAdapter;

    /** **/

    Dialog modalBoleta,modalClienteDNI;

    Button btnAutomatico,btnListadoComprobante,btnLibre,btnCancelarLibre,btnAceptarLibre,btnSoles,btnCancelarSoles,btnAgregarSoles,btnGalones,btnCancelarGalones,btnAgregarGalones,
            btnBoleta,btnCancelarBoleta,btnAgregarBoleta,btnGenerarBoleta,buscarPlacaBoleta,buscarDNIBoleta,btnCancelarLCliente,
            btnFactura,buscarRUCFactura,buscarPlacaFactura,btnCancelarFactura,btnAgregarFactura,btnNotaDespacho,btnCancelarNotaDespacho,btnAgregarNotaDespacho,btnSerafin,btnCancelarSerafin,btnAgregarSerafin;
    TextInputLayout textNFC,alertSoles,alertGalones,alertPlaca,alertDNI,alertRUC,alertNombre,alertRazSocial,alertPEfectivo,alertOperacion,alertSelectTPago,
            alertCPlaca,alertCTarjeta,alertCCliente,alertCRazSocial;

    TextInputEditText inputNFC,inputMontoSoles,inputCantidadGalones,inputPlaca,inputDNI,inputRUC,inputNombre,inputRazSocial,inputDireccion,
            inputObservacion,inputOperacion,inputPEfectivo,inputCPlaca,input_CNTarjeta,inputCCliente,inputCRazSocial,inputCDireccion,inputCKilometraje,inputCObservacion;

    RadioGroup radioFormaPago;
    RadioButton radioEfectivo,radioTarjeta,radioCredito,radioNombreFormaPago;

    Spinner SpinnerTPago;
    TextView  datos_terminal,textMensajePEfectivo;
    TipoPagoAdapter tipoPagoAdapter;
    TipoPago tipoPago;
    ImageButton btnscanear,btnFiltrar,btnVolverCompra;
    private boolean mostrarTodos = true;

    LinearLayout linearLayoutRecyclerProducto;
    private ProCategorias lastSelectedCategoria;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        mAPIService = GlobalInfo.getAPIService();

        linearLayoutRecyclerProducto = view.findViewById(R.id.linearLayoutRecyclerProducto);
        btntodoproducto  = view.findViewById(R.id.btntodo);
        btncarritocompra = view.findViewById(R.id.btncarritocompra);
        BuscarProducto   = view.findViewById(R.id.btnBuscadorProducto);
        btnscanear       = view.findViewById(R.id.btnscanear);
        btnFiltrar       = view.findViewById(R.id.btnfiltrar);

        /**
         * @FILTRAR:ProductosMasVendidos
         */
        btnFiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
        btnFiltrar.setColorFilter(Color.parseColor("#FFFFFF"));

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mostrarTodos) {
                    productosAdapter.filtrarMuyVendidos(true);
                    btnFiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                    btnFiltrar.setColorFilter(Color.parseColor("#000000"));
                } else {
                    productosAdapter.filtrarMuyVendidos(false);
                    btnFiltrar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
                    btnFiltrar.setColorFilter(Color.parseColor("#FFFFFF"));
                }

                mostrarTodos = !mostrarTodos;
            }
        });

        /**
         * @ESCANEAR:CodigoQR-CodigoBarra
         */
        BuscarProducto.setIconifiedByDefault(false);

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
        btntodoproducto.setBackgroundColor(Color.parseColor("#FFC107"));
        btntodoproducto.setTextColor(Color.parseColor("#000000"));

        /**
         * @BUSCADOR:ProductosNombres
         */
        BuscarProducto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                String userInput = newText.toLowerCase();
                if (productosAdapter != null) {
                    // Buscar por nombre
                    productosAdapter.filtrado(userInput, false);

                    // Buscar código de barras
                    if (productosList.isEmpty()) {
                        productosAdapter.filtrado(userInput, true);

                        if (productosList.isEmpty()) {
                            Toast.makeText(getContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                return true;
            }
        });

        /**
         * @FILTRAR:BotonTodosProductos
         */
        btntodoproducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * @BOTON:ActivarDesactivarEstado
                 */
                if (isTodoProductoSelected) {
                    btntodoproducto.setBackgroundColor(Color.parseColor("#999999"));
                    btntodoproducto.setTextColor(Color.parseColor("#FFFFFF"));
                }else {
                    btntodoproducto.setBackgroundColor(Color.parseColor("#FFC107"));
                    btntodoproducto.setTextColor(Color.parseColor("#000000"));
                }

                productosAdapter.setProductos(productosList);
                productosAdapter.notifyDataSetChanged();

                /**
                 * @BOTON:ActivarCategoriaSeleccionado
                 */
                categoriaAdapter.setTodoSelected(true);

            }
        });

        /**
         * @LISTADO:Categorias
         */
        recyclerCategoria = view.findViewById(R.id.recyclerCategoria);
        recyclerCategoria.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        proCategoriasList = new ArrayList<>();

        proCategoriasList.add(new ProCategorias("Bebidas"));
        proCategoriasList.add(new ProCategorias("Snack"));
        proCategoriasList.add(new ProCategorias("Aseo"));
        proCategoriasList.add(new ProCategorias("Galletas"));

        categoriaAdapter = new CategoriaAdapter(proCategoriasList, new CategoriaAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(ProCategorias item) {

                /**
                 * @FILTRADO:MostrarTodoProductos
                 */
                productosFiltrados = new ArrayList<>();

                for (Productos productos : productosList) {
                    if (productos.getCategoria().equals(item.getNomcategoria())) {
                        productosFiltrados.add(productos);
                    }
                }

                productosAdapter.setProductos(productosFiltrados);
                productosAdapter.notifyDataSetChanged();

                /**
                 * @BOTON:DesactivarEstado
                 */
                isTodoProductoSelected = false;
                btntodoproducto.setBackgroundColor(Color.parseColor("#999999"));
                btntodoproducto.setTextColor(Color.parseColor("#FFFFFF"));

                return 0;
            }
        });
        recyclerCategoria.setAdapter(categoriaAdapter);
        categoriaAdapter.notifyDataSetChanged();

        /**
         * Ruta de imagen
         */

        /**
         *  @LISTADO:Productos
         */
        recyclerProducto = view.findViewById(R.id.recyclerProducto);
        recyclerProducto.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        productosList = new ArrayList<>();

        productosList.add(new Productos("Inca Kola 500 ml","7750885016537", 2.50, 2, "Bebidas", "sito.png", 1,true));
        productosList.add(new Productos("Galleta Oreo", "7750885016507", 1.00, 1, "Galletas","oreo.jpg", 2,true));
        productosList.add(new Productos("Detergente marsella  1000 ml","7750085016537",  5.00, 9, "Aseo"," ", 3,false));
        productosList.add(new Productos("Papa Lays", "7750885016587",2.00,  0, "Snack","lays.png", 4,true));
        productosList.add(new Productos("Botella de aceite 500 ml", "1750885016537", 3.50, 6, "Aseo"," ", 5,false));
        productosList.add(new Productos("Sprick 500 ml","7250885016537",  3.00, 0, "Bebidas"," ", 6,false));
        productosList.add(new Productos("Detergente Opal 500g","7750085006537",  8.00, 9, "Aseo"," ", 7,false));
        productosList.add(new Productos("Chisito 9g", "7750885016507",1.00,  1, "Snack","lays.png", 8,true));
        productosList.add(new Productos("Leche Gloria ", "1050885016537", 4.00, 6, "Aseo"," ", 9,false));
        productosList.add(new Productos("Gaseosa Fanta 500 ml","7050885016537",  2.50, 0, "Bebidas"," ", 10,false));

        productosAdapter = new ProductosAdapter(productosList, new ProductosAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Productos item, boolean isSelected) {

                /**
                 * @PRODUCTOSELECCIONADO:Agregar_Remover
                 */
                item.setSeleccionado(isSelected);

                if (isSelected) {
                    productosSeleccionados.add(item);
                } else {
                    productosSeleccionados.remove(item);
                }

                /**
                 * @VISUALIZAR:BotonIrCarritoCompra
                 */
                if (!productosSeleccionados.isEmpty()) {
                    btncarritocompra.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerProducto.getLayoutParams();
                    layoutParams.bottomMargin = 100;
                    linearLayoutRecyclerProducto.setLayoutParams(layoutParams);
                } else {
                    btncarritocompra.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerProducto.getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    linearLayoutRecyclerProducto.setLayoutParams(layoutParams);
                }

                return 0;
            }

        });

        productosAdapter.setOnDeseleccionarProductoListener(new ProductosAdapter.OnDeseleccionarProductoListener() {
            @Override
            public void onDeseleccionarProducto(Productos producto) {

                /**
                 * @DESELECCIONARPRODUCTO:RestablecerProductosInicial
                 */
                cantidadesSeleccionadas.put(producto.getId() , 1);
                nuevosPrecios.put(producto.getId(), producto.getPrecio());
                carritoAdapter.notifyDataSetChanged();

            }
        });

        /**
         * @MOSTRARPEODUCTOS:Columnas3
         */
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        recyclerProducto.setLayoutManager(layoutManager);
        recyclerProducto.setAdapter(productosAdapter);
        productosAdapter.notifyDataSetChanged();

        /**
         * @Modal:CarritoCompra
         */
        modalCarrito = new Dialog(getContext());
        modalCarrito.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalCarrito.setContentView(R.layout.fragment_carrito);
        modalCarrito.setCancelable(false);

        /**
         * @DATOS:InteractuarCarritoCompras
         */
        totalmontoCar = modalCarrito.findViewById(R.id.totalmontoCar);


        btncarritocompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalCarrito.show();

                btnBoleta       = modalCarrito.findViewById(R.id.btnBoleta);
                btnFactura      = modalCarrito.findViewById(R.id.btnFactura);
                btnNotaDespacho = modalCarrito.findViewById(R.id.btnnotadespacho);
                btnSeleccionCliente = modalCarrito.findViewById(R.id.btnSeleccionCliente);
                btnconfirmarventa   = modalCarrito.findViewById(R.id.btnconfirmarventa);
                btnVolverCompra     = modalCarrito.findViewById(R.id.btnvolvercompra);

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

                btnconfirmarventa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        imprimirComprobante(productosSeleccionados, totalmontoCar.getText().toString(),  campoDNI, campoNombre,campoDireccion,campoObservacion,campoPEfectivo,campoOperacion,CharNombreFormaPago,tipoPagoO);
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerProducto.getLayoutParams();
                        layoutParams.bottomMargin = 0;
                        linearLayoutRecyclerProducto.setLayoutParams(layoutParams);
                    }
                });

                modalBoleta = new Dialog(getContext());
                modalBoleta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalBoleta.setContentView(R.layout.fragment_boleta);
                modalBoleta.setCancelable(false);

                btnBoleta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modalBoleta.show();

                        alertPlaca        = modalBoleta.findViewById(R.id.alertPlaca);
                        alertDNI          = modalBoleta.findViewById(R.id.alertDNI);
                        alertNombre       = modalBoleta.findViewById(R.id.alertNombre);
                        alertPEfectivo    = modalBoleta.findViewById(R.id.alertPEfectivo);
                        alertOperacion    = modalBoleta.findViewById(R.id.alertOperacion);
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

                        radioFormaPago    = modalBoleta.findViewById(R.id.radioFormaPago);
                        radioEfectivo     = modalBoleta.findViewById(R.id.radioEfectivo);
                        radioTarjeta      = modalBoleta.findViewById(R.id.radioTarjeta);
                        radioCredito      = modalBoleta.findViewById(R.id.radioCredito);

                        buscarDNIBoleta   = modalBoleta.findViewById(R.id.buscarDNIBoleta);
                        buscarPlacaBoleta = modalBoleta.findViewById(R.id.buscarPlacaBoleta);
                        btnGenerarBoleta  = modalBoleta.findViewById(R.id.btnGenerarBoleta);
                        btnCancelarBoleta = modalBoleta.findViewById(R.id.btnCancelarBoleta);
                        btnAgregarBoleta  = modalBoleta.findViewById(R.id.btnAgregarBoleta);

                        textNFC.setVisibility(View.GONE);

                        /**
                         * Mostrar Listado de Cliente para Boleta y Seleccionar - DNI y Nombre
                         * */
                        modalClienteDNI = new Dialog(getContext());
                        modalClienteDNI.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalClienteDNI.setContentView(R.layout.fragment_clientes);
                        modalClienteDNI.setCancelable(false);

                        /** Listado de Card - Cliente DNI */
                        recyclerLCliente = modalClienteDNI.findViewById(R.id.recyclerLCliente);
                        recyclerLCliente.setLayoutManager(new LinearLayoutManager(getContext()));
                        ClienteDNI();

                        /** Inicio Doble click para abrir modal - Cliente DNI */
                        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                            @Override
                            public boolean onDoubleTap(MotionEvent e) {

                                modalClienteDNI.show();

                                btnCancelarLCliente   = modalClienteDNI.findViewById(R.id.btnCancelarLCliente);
                                btnBuscadorClienteRZ  = modalClienteDNI.findViewById(R.id.btnBuscadorClienteRZ);

                                /** Buscardor por Nombre del Cliente */
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

                        btnAgregarBoleta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                campoDNI            = inputDNI.getText().toString();
                                campoNombre         = inputNombre.getText().toString();
                                campoDireccion      = inputDireccion.getText().toString();
                                campoObservacion    = inputObservacion.getText().toString();
                                campoPEfectivo      = inputPEfectivo.getText().toString();
                                campoOperacion      = inputOperacion.getText().toString();

                                int checkedRadioButtonId   = radioFormaPago.getCheckedRadioButtonId();

                                if (campoDNI.isEmpty() ) {

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

                                alertDNI.setErrorEnabled(false);
                                alertNombre.setErrorEnabled(false);
                                alertOperacion.setErrorEnabled(false);
                                alertPEfectivo.setErrorEnabled(false);

                                tipoPagoO      = " ";
                                campoOperacion = " ";
                                campoPEfectivo = " ";

                                CharNombreFormaPago = radioNombreFormaPago.getText().toString().substring(0,1);

                                String NombreFormaPago = radioNombreFormaPago.getText().toString();

                                if (NombreFormaPago.equals("Tarjeta")){

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    tipoPagoO        = String.valueOf(Integer.valueOf(tipoPago.getCardID()));
                                    campoOperacion   = inputOperacion.getText().toString();

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else {
                                        campoPEfectivo  = inputPEfectivo.getText().toString();
                                    }

                                }else if (NombreFormaPago.equals("Credito")) {

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        campoPEfectivo  = inputPEfectivo.getText().toString();
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
                         * Mostrar Listado de Cliente para Factura y Seleccionar RUC - Raz. Social
                         */
                        modalClienteRUC = new Dialog(getContext());
                        modalClienteRUC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalClienteRUC.setContentView(R.layout.fragment_clientes);
                        modalClienteRUC.setCancelable(false);

                        /** Listado de Card - Cliente RUC */
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
                                }

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

                                    /*    detalleVenta.setNroPlaca(inputPlaca.getText().toString());
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

                                           /* detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                            detalleVenta.setOperacionREF(inputOperacion.getText().toString());*/

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                        /*    detalleVenta.setTarjetaCredito(String.valueOf(Integer.valueOf(tipoPago.getCardID())));
                                            detalleVenta.setOperacionREF(inputOperacion.getText().toString());*/

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        /* detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));*/
                                    }


                                }else if (NombreFormaPago.equals("Credito")) {

                                    Double dosDecimales = Double.valueOf(campoPEfectivo);
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                    if(dosDecimales != Double.parseDouble(decimalFormat.format(dosDecimales))){
                                        alertPEfectivo.setError("* Por favor ingrese un valor con dos decimales solamente");
                                        return;
                                    }else{
                                        /*  detalleVenta.setMontoSoles(Double.parseDouble(inputPEfectivo.getText().toString()));*/
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


                        modalClienteCredito = new Dialog(getContext());
                        modalClienteCredito.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        modalClienteCredito.setContentView(R.layout.modal_cliente_credito);
                        modalClienteCredito.setCancelable(false);

                        /** Listado de Card - Cliente Credito */
                        recyclerLClienteCredito = modalClienteCredito.findViewById(R.id.recyclerLClienteCredito);
                        recyclerLClienteCredito.setLayoutManager(new LinearLayoutManager(getContext()));

                        ClienteCredito();

                        /** Inicio Doble click para abrir modal - Cliente Credito */
                        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onDoubleTap(MotionEvent e) {

                                modalClienteCredito.show();

                                btnCancelarLCliente   = modalClienteCredito.findViewById(R.id.btnCancelarLClienteCredito);
                                btnBuscadorClienteRZ  = modalClienteCredito.findViewById(R.id.btnBuscadorClienteRZ);

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

                        /** Boton Cancelar - Nota de Despacho */
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

                        /** Boton Agregar - Agregar Operacion */
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

                                   /*     detalleVenta.setNroPlaca(inputCPlaca.getText().toString());
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
            }
        });

        /**
         * @LISTADO:CarritoCompra
         */
        recyclerCarrito = modalCarrito.findViewById(R.id.recyclerCarrito);
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        carritoAdapter = new CarritoAdapter(productosSeleccionados,cantidadesSeleccionadas,nuevosPrecios,totalmontoCar,modalCarrito,btncarritocompra,linearLayoutRecyclerProducto);

        carritoAdapter.setOnProductoEliminadoListener(new CarritoAdapter.OnProductoEliminadoListener() {
            @Override
            public void onProductoEliminado(List<Productos> productosEnCarrito) {
                productosAdapter.notifyDataSetChanged();
            }
        });

        recyclerCarrito.setAdapter(carritoAdapter);
        carritoAdapter.notifyDataSetChanged();

        return view;
    }

    /**
     * @SCANNEAR:CódigoProductos
     */
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

    /**
     * @IMPRIMIR:ComprobanteVenta
     */
    private void imprimirComprobante(List<Productos> productosSeleccionados,String totalmontoCar,  String campoDNI, String campoNombre, String campoDireccion, String campoObservacion, String campoPEfectivo, String campoOperacion, String CharNombreFormaPago,String tipoPagoO){

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


            for (Productos producto : productosSeleccionados) {

                String nombre = producto.getNombre();
                String unidadMedida = "UND";
                double precio  = producto.getPrecio();
                int  cantidad   = cantidadesSeleccionadas.get(producto.getId());
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

            for (Productos producto : productosSeleccionados) {

               // productosSeleccionados.contains(false);
                producto.setSeleccionado(false);
            }

            productosSeleccionados.clear();
            cantidadesSeleccionadas.clear();

            productosAdapter.notifyDataSetChanged();
            carritoAdapter.notifyDataSetChanged();

            modalCarrito.dismiss();
            btncarritocompra.setVisibility(View.GONE);

        }, this::showToast);

    }

    /** Alerta de Conexión de Bluetooth */
    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

    /** Tipo de Pago */
    private void  TipoPago_Doc(){

        Resources res = getResources();
        tipoPagoAdapter = new TipoPagoAdapter(getContext(), R.layout.item, (ArrayList<TipoPago>) GlobalInfo.gettipopagoList10, res);
        SpinnerTPago.setAdapter(tipoPagoAdapter);

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

    /** Listado - CLIENTE Credito */
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
}