package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.ListaComprobanteAdapter;
import com.anggastudio.sample.Login;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.PasswordChecker;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
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

public class ListaComprobantesFragment extends Fragment  {

    TextInputEditText usuario, contraseña;
    TextInputLayout alertuser,alertpassword;
    String usuarioUser,contraseñaUser;

    Dialog modal_ErrorWifi,modalReimpresion,modalAnulacion;
    Button btnAceptarErrorWifi,btnCancelarRImpresion,btnRImpresion,btnAnular,btnCancelarAnular,btnAceptarIngreso;
    TextView campo_correlativo;

    SearchView BuscarRazonSocial;

    List<ListaComprobante> listaComprobanteList;
    ListaComprobanteAdapter listaComprobanteAdapter;

    RecyclerView recyclerLComprobante ;

    List<Users> usersAnuladoList;

    ImageButton btnConsultaComprobantes;

    private APIService mAPIService;

    private NFCUtil nfcUtil;

    Map<String, List<Reimpresion>> mapComprobantes = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcUtil = new NFCUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_lista_comprobantes, container, false);

        mAPIService  = GlobalInfo.getAPIService();

        BuscarRazonSocial       = view.findViewById(R.id.BuscarRazonSocial);
        btnConsultaComprobantes = view.findViewById(R.id.btnConsultaComprobantes);

        BuscarRazonSocial.setIconifiedByDefault(false);

        /**
         * @CONSULTACOMPROBANTES
         */
        btnConsultaComprobantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManagerCComprobante = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionCComprobante = fragmentManagerCComprobante.beginTransaction();

                int fragmentContainerCComprobante  = R.id.fragment_container;
                ConsultaComprobantesFragment consultaComprobantesFragment = new ConsultaComprobantesFragment();
                fragmentTransactionCComprobante.replace(fragmentContainerCComprobante, consultaComprobantesFragment);
                fragmentTransactionCComprobante.addToBackStack(null);
                fragmentTransactionCComprobante.commit();
            }
        });

        /** Listado de Comprobantes  */
        recyclerLComprobante = view.findViewById(R.id.recyclerListaComprobante);
        recyclerLComprobante.setLayoutManager(new LinearLayoutManager(getContext()));

        /** Mostrar Modal de Cambio de Turno */
        modalReimpresion = new Dialog(getContext());
        modalReimpresion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalReimpresion.setContentView(R.layout.modal_reimprimir);
        modalReimpresion.setCancelable(false);

        findConsultarVenta(GlobalInfo.getterminalID10);

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
                if (listaComprobanteAdapter != null) {
                    listaComprobanteAdapter.filtrado(userInput);
                }
                return true;
            }
        });

        /** Modal de Error al Wifi **/
        modal_ErrorWifi = new Dialog(getContext());
        modal_ErrorWifi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modal_ErrorWifi.setContentView(R.layout.alerta_wifi);
        modal_ErrorWifi.setCancelable(false);

        return view;
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

                    listaComprobanteList = response.body();

                    listaComprobanteAdapter = new ListaComprobanteAdapter(listaComprobanteList, getContext(), new ListaComprobanteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ListaComprobante item) {

                            moveToDescription(item);

                            if (!modalReimpresion.isShowing()) {
                                modalReimpresion.show();
                            }

                            btnCancelarRImpresion = modalReimpresion.findViewById(R.id.btnCancelarRImpresion);
                            btnRImpresion         = modalReimpresion.findViewById(R.id.btnRImpresion);
                            btnAnular             = modalReimpresion.findViewById(R.id.btnAnular);
                            campo_correlativo     = modalReimpresion.findViewById(R.id.campo_correlativo);

                            campo_correlativo.setText("NroDocumento: " + GlobalInfo.getconsultaventaSerieDocumento10 + "-" + GlobalInfo.getconsultaventaNroDocumento10);

                            btnCancelarRImpresion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    modalReimpresion.dismiss();
                                }
                            });

                            btnRImpresion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Context context = requireContext();

                                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    if (networkInfo != null && networkInfo.isConnected()) {

                                        Reimpresion(GlobalInfo.getTipoPapel10,GlobalInfo.getconsultaventaTipoDocumentoID10, GlobalInfo.getconsultaventaSerieDocumento10, GlobalInfo.getconsultaventaNroDocumento10);
                                        modalReimpresion.dismiss();

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

                           modalAnulacion = new Dialog(getContext());
                           modalAnulacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                           modalAnulacion.setContentView(R.layout.modal_anulacion);
                           modalAnulacion.setCancelable(false);

                           btnAnular.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Context context = requireContext();

                                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    if (networkInfo != null && networkInfo.isConnected()) {

                                        if (GlobalInfo.getconsultaventaAnulado10.equals("NO")) {

                                            if (!modalAnulacion.isShowing()) {
                                                modalAnulacion.show();
                                            }

                                            btnCancelarAnular = modalAnulacion.findViewById(R.id.btnCancelarAnular);
                                            btnAceptarIngreso = modalAnulacion.findViewById(R.id.btnAceptarIngreso);
                                            usuario           = modalAnulacion.findViewById(R.id.inputUserAnulado);
                                            contraseña        = modalAnulacion.findViewById(R.id.inputContraseñaAnulado);
                                            alertuser         = modalAnulacion.findViewById(R.id.alertUserAnulado);
                                            alertpassword     = modalAnulacion.findViewById(R.id.alertContraseñaAnulado);

                                            btnCancelarAnular.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    modalAnulacion.dismiss();

                                                    usuario.getText().clear();
                                                    contraseña.getText().clear();

                                                    alertuser.setErrorEnabled(false);
                                                    alertpassword.setErrorEnabled(false);

                                                }
                                            });

                                            btnAceptarIngreso.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    usuarioUser = usuario.getText().toString();
                                                    contraseñaUser = contraseña.getText().toString();

                                                    if (usuarioUser.isEmpty()) {
                                                        alertuser.setError("El campo usuario es obligatorio");
                                                        return;
                                                    } else if (contraseñaUser.isEmpty()) {
                                                        alertpassword.setError("El campo contraseña es obligatorio");
                                                        return;
                                                    }

                                                    Context context = requireContext();

                                                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                                    if (networkInfo != null && networkInfo.isConnected()) {

                                                        findUsers(usuarioUser);

                                                        alertuser.setErrorEnabled(false);
                                                        alertpassword.setErrorEnabled(false);

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

                                        } else {
                                            Toast.makeText(getContext(), "Documento se encuntra anulado", Toast.LENGTH_SHORT).show();
                                        }

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

                    listaComprobanteAdapter.notifyDataSetChanged();

                    recyclerLComprobante.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerLComprobante.setAdapter(listaComprobanteAdapter);

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

    public void moveToDescription(ListaComprobante item) {
        GlobalInfo.getconsultaventaTipoDocumentoID10  = item.getTipoDocumento();
        GlobalInfo.getconsultaventaSerieDocumento10   = item.getSerieDocumento();
        GlobalInfo.getconsultaventaNroDocumento10     = item.getNroDocumento();
        GlobalInfo.getconsultaventaAnulado10          = item.getAnulado();
    }

    /** API SERVICE - Usuarios */
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

                    usersAnuladoList = response.body();

                    for (Users user : usersAnuladoList) {

                        GlobalInfo.getuserIDAnular10 = user.getUserID();
                        GlobalInfo.getuserNameAnular10 = user.getNames();
                        GlobalInfo.getuserPassAnular10 = user.getPassword();
                        GlobalInfo.getuserCancelAnular10 = user.getCancel();

                    }

                    if (GlobalInfo.getuserCancelAnular10 == true) {

                        FragmentManager fragmentManager = getFragmentManager();

                        String getName = usuarioUser.trim();
                        String getPass = PasswordChecker.checkpassword(contraseñaUser.trim());

                        if (getName.equals(GlobalInfo.getuserIDAnular10) && getPass.equals(GlobalInfo.getuserPassAnular10)) {

                            Anulars(GlobalInfo.getconsultaventaTipoDocumentoID10, GlobalInfo.getconsultaventaSerieDocumento10, GlobalInfo.getconsultaventaNroDocumento10, GlobalInfo.getuserIDAnular10,GlobalInfo.getterminalID10);

                            Toast.makeText(getContext(), "Se anulo correctamente", Toast.LENGTH_SHORT).show();
                            fragmentManager.popBackStack();

                            modalAnulacion.dismiss();

                        } else {
                            Toast.makeText(getContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "El usuario se encuentra bloqueado", Toast.LENGTH_SHORT).show();
                    }

                    modalReimpresion.dismiss();

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

    /** API SERVICE - Anular Comprobante */
    private void Anulars(String tipodoc, String seriedoc, String nrodoc, String anuladoid, String terminalid) {

        Call<List<Anular>> call = mAPIService.findAnular(tipodoc, seriedoc, nrodoc, anuladoid, terminalid);
        call.enqueue(new Callback<List<Anular>>() {
            @Override
            public void onResponse(Call<List<Anular>> call, Response<List<Anular>> response) {

                try {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Anular>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Anular - RED - WIFI", Toast.LENGTH_SHORT).show();
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

                        for (Map.Entry<String, List<Reimpresion>> entry : mapComprobantes.entrySet()) {

                            List<Reimpresion> productos = entry.getValue();


                            Double finalMtoTotal = mtoTotal1;
                            Double finalMontoCanjeado1 = montoCanjeado1;
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

                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                }
                                                printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                printama.setSmallText();
                                                printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

                                                break;

                                            case "03":

                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                }

                                                if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                    printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                                }

                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                printama.setSmallText();
                                                printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

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

                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                }
                                                printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

                                                break;

                                            case "03":

                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                }

                                                if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                    printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                                }

                                                printama.printTextlnBold("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

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

                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                }
                                                printama.printTextln("OP. GRAVADAS: S/ " + MtoSubTotalFF, Printama.RIGHT);
                                                printama.printTextln("I.G.V. 18%: S/  " + MtoImpuestoFF, Printama.RIGHT);
                                                printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

                                                break;

                                            case "03":

                                                if (finalMtoDescuento > 0) {
                                                    printama.printTextln("DESCUENTO: S/ " + MtoDescuento, Printama.RIGHT);
                                                }

                                                if(finalMtoTotal <= 0.00 && finalMontoCanjeado1 > 0.00 ){
                                                    printama.printTextlnBold("OP. GRATUITAS: S/ " + finalMontoCanjeado, Printama.RIGHT);
                                                }

                                                printama.printTextln("TOTAL VENTA: S/ " + MtoTotalFF, Printama.RIGHT);
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
                                                printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n" + "http://4-fact.com/sven/auth/consulta");

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

    /** Se mantenga en el estado actual */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        boolean isModalReimpresionShowing = modalReimpresion != null && modalReimpresion.isShowing();
        outState.putBoolean("IS_MODAL_REIMPRESION_SHOWING", isModalReimpresionShowing);

        boolean isModalAnulacionShowing = modalAnulacion != null && modalAnulacion.isShowing();
        outState.putBoolean("IS_MODAL_ANULACION_SHOWING", isModalAnulacionShowing);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            boolean isModalReimpresionShowing = savedInstanceState.getBoolean("IS_MODAL_REIMPRESION_SHOWING");
            if (isModalReimpresionShowing && modalReimpresion != null) {
                modalReimpresion.show();
            }

            boolean isModalAnulacionShowing = savedInstanceState.getBoolean("IS_MODAL_ANULACION_SHOWING");
            if (isModalAnulacionShowing && modalAnulacion != null) {
                modalAnulacion.show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerLComprobante.setAdapter(null);
    }
    public void onResume() {
        super.onResume();
        nfcUtil.onResume();
    }

    public void onPause() {
        super.onPause();
        nfcUtil.onPause();
    }
}

