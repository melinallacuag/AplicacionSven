package com.anggastudio.sample;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.sample.Adapter.DetalleVentaAdapter;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ListaComprobanteAdapter;
import com.anggastudio.sample.Adapter.TipoPagoAdapter;
import com.anggastudio.sample.Adapter.VContometroAdapter;
import com.anggastudio.sample.Adapter.VProductoAdapter;
import com.anggastudio.sample.Adapter.VTipoPagoAdapter;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Company;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.Setting;
import com.anggastudio.sample.WebApiSVEN.Models.Terminal;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.anggastudio.sample.WebApiSVEN.Parameters.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity{

    ImageButton configuracion;
    Button btniniciar;
    TextInputEditText inputUsuario, inputContraseña;
    TextInputLayout alertuser,alertpassword;
    TextView imeii;
    String usuarioUser,contraseñaUser;

    List<Users> usersList;
    List<Terminal> terminalList;
    List<Company> companyList;

    List<Setting> settingList;

    private APIService mAPIService;

    /*============================== Manejo pasivo de lecturas NFC ===============================*/
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;
    /*============================== Manejo pasivo de lecturas NFC - FIN =========================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         mAPIService = GlobalInfo.getAPIService();

        btniniciar      = findViewById(R.id.btnlogin);
        inputUsuario    = findViewById(R.id.usuario);
        inputContraseña = findViewById(R.id.contraseña);
        alertuser       = findViewById(R.id.textusuario);
        alertpassword   = findViewById(R.id.textcontraseña);

    /*============================== Manejo pasivo de lecturas NFC ===============================*/
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            Intent intent = new Intent(this, this.getClass())
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            intentFilters = new IntentFilter[]{tagIntentFilter};
            techLists = new String[][]{new String[]{NfcA.class.getName(), NfcB.class.getName(),
                    NfcF.class.getName(), NfcV.class.getName(), IsoDep.class.getName(),
                    MifareClassic.class.getName(), MifareUltralight.class.getName(),
                    Ndef.class.getName()}};
    /*============================== Manejo pasivo de lecturas NFC - FIN =========================*/

        /*** Boton par configurar la impresión bluetooth.*/

        configuracion = findViewById(R.id.btnconfiguracion);
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( getApplicationContext(),MainActivity.class));
            }
        });

        /** Detectar el IMEI*/
        imeii = findViewById(R.id.imei);
        imeii.setText(ObtenerIMEI.getDeviceId(getApplicationContext()));

        GlobalInfo.getterminalImei10 = imeii.getText().toString();

        btniniciar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                usuarioUser    = inputUsuario.getText().toString();
                contraseñaUser = inputContraseña.getText().toString();

                if(usuarioUser.isEmpty()){
                    alertuser.setError("El campo usuario es obligatorio");
                    return;
                }else if(contraseñaUser.isEmpty()){
                    alertpassword.setError("El campo contraseña es obligatorio");
                    return;
                }

                alertuser.setErrorEnabled(false);
                alertpassword.setErrorEnabled(false);

                GlobalInfo.getuserID10 = "";
                GlobalInfo.getuserName10 = "";
                GlobalInfo.getuserPass10 = "";
                GlobalInfo.getuseridentFID10 = "";

                findUsers(usuarioUser);

            }
        });

        /** Mostrar el listado de Datos*/
      /*  getClienteDNI();
        getClienteRUC();*/
        getTipoPago();

        findTerminal(GlobalInfo.getterminalImei10.toUpperCase());

    }

    /*============================== Manejo pasivo de lecturas NFC ===============================*/
    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
    /*============================== Manejo pasivo de lecturas NFC - FIN =========================*/

    /** API SERVICE - Usuarios */
    private void findUsers(String id){

        Call<List<Users>> call = mAPIService.findUsers(id);

        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText( getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usersList = response.body();

                  /*  for(Users user: usersList) {

                        usuario.setText(user.getUserID());
                        GlobalInfo.getuserID10 = user.getUserID();
                        GlobalInfo.getuserName10 = user.getNames();
                        GlobalInfo.getuserPass10  = user.getPassword();
                        GlobalInfo.getuserLocked10  = user.getLocked();
                        GlobalInfo.getuseridentFID10 = user.getIdentFID();

                    }

                    if (GlobalInfo.getuserLocked10 == false) {
                        Toast.makeText( getApplicationContext(), "El usuario se encuentra bloqueado", Toast.LENGTH_SHORT).show();
                    }else {

                        String getName = usuario.getText().toString();
                        String getPass = checkpassword(contraseña.getText().toString());

                        if( getName.equals(GlobalInfo.getuserName10)  || getPass.equals(GlobalInfo.getuserPass10)){

                            Toast.makeText( getApplicationContext(), "Bienvenido al Sistema SVEN", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent( getApplicationContext(),Menu.class));

                        }
                        else {
                            Toast.makeText( getApplicationContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    }*/

                    if (usersList != null && !usersList.isEmpty()) {

                        Users user = usersList.get(0);

                        inputUsuario.setText(user.getUserID());
                        GlobalInfo.getuserID10 = user.getUserID();
                        GlobalInfo.getuserName10 = user.getNames();
                        GlobalInfo.getuserPass10 = user.getPassword();
                        GlobalInfo.getuserLocked10 = user.getLocked();

                        if (GlobalInfo.getuserLocked10 == false) {
                            Toast.makeText( getApplicationContext(), "El Usuario se encuentra bloqueado.", Toast.LENGTH_SHORT).show();
                        }else {

                            String getName = usuarioUser.trim();
                            String getPass = checkpassword(contraseñaUser.trim());

                            if(getName.equals(GlobalInfo.getuserID10) && getPass.equals(GlobalInfo.getuserPass10)){
                                Toast.makeText( getApplicationContext(), "Bienvenido al Sistema SVEN", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent( getApplicationContext(),Menu.class));
                            }
                            else {
                                Toast.makeText( getApplicationContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "El Usuario o la Contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception ex){
                    Toast.makeText( getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText( getApplicationContext(), "Error de conexión APICORE Users - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Terminal */
    private void findTerminal(String id) {

        Call<List<Terminal>> call = mAPIService.findTerminal(id);

        call.enqueue(new Callback<List<Terminal>>() {
            @Override
            public void onResponse(Call<List<Terminal>> call, Response<List<Terminal>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText( getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    terminalList = response.body();

                    for(Terminal terminal: terminalList) {

                        GlobalInfo.getterminalID10              = String.valueOf(terminal.getTerminalID());
                        GlobalInfo.getterminalImei10            = String.valueOf(terminal.getImei());
                        GlobalInfo.getterminalFecha10           = String.valueOf(terminal.getFecha_Proceso());
                        GlobalInfo.getterminalTurno10           = Integer.valueOf(terminal.getTurno());
                        GlobalInfo.getterminalCompanyID10       = Integer.valueOf(terminal.getCompanyID());
                        GlobalInfo.getterminalAlmacenID10       = Integer.valueOf(terminal.getAlmacenID());
                        GlobalInfo.getterminalFechaHoraCierre10 = String.valueOf(terminal.getFecha_Hora_Cierre());

                        /** Mostrar el listado de Datos*/
                        findCompany(GlobalInfo.getterminalCompanyID10);

                        findSetting(GlobalInfo.getterminalCompanyID10);

                        findLados(GlobalInfo.getterminalImei10);

                        findDetalleVenta(GlobalInfo.getterminalImei10);

                        getManguerasByTerminal(GlobalInfo.getterminalID10);

                    }

                    if (GlobalInfo.getterminalID10.isEmpty() || GlobalInfo.getterminalID10 == null) {

                        imeii.setTextColor(getResources().getColor(R.color.colorError));

                        Toast.makeText( getApplicationContext(), "Terminal no configurado, comuniquese con el administrador.", Toast.LENGTH_SHORT).show();

                        return;

                    }

                }catch (Exception ex){
                    Toast.makeText( getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Terminal>> call, Throwable t) {
                Toast.makeText( getApplicationContext(), "Error de conexión APICORE Terminal - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Empresa */
    private void findCompany(Integer id){

        Call<List<Company>> call = mAPIService.findCompany(id);

        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    companyList = response.body();

                    for(Company company: companyList){

                        GlobalInfo.getNameCompany10    = String.valueOf(company.getNames());
                        GlobalInfo.getRucCompany10     = String.valueOf(company.getRuc());
                        GlobalInfo.getAddressCompany10 = String.valueOf(company.getAddress());
                        GlobalInfo.getBranchCompany10  = String.valueOf(company.getBranch());
                        GlobalInfo.getPhoneCompany10   = String.valueOf(company.getPhone());
                        GlobalInfo.getMailCompany10    = String.valueOf(company.getMail());
                        GlobalInfo.getManagerCompany10 = String.valueOf(company.getManager());
                        GlobalInfo.getSloganCompany10  = String.valueOf(company.getEslogan());

                    }

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE - RED - WIFI", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getladosList10 = response.body();


                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lados>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Cara - RED - WIFI", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    settingList = response.body();

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
                        GlobalInfo.getsettingPassAnula10       = String.valueOf(setting.getPassAnula());
                    }

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Setting>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Setting - RED - WIFI", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getdetalleVentaList10 = response.body();

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DetalleVenta>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Detalle Venta - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**  API SERVICE - Mangueras */
    private void getManguerasByTerminal(String TerminalID){

        Call<List<Mangueras>> call = mAPIService.findManguerasByTerminal(TerminalID);

        call.enqueue(new Callback<List<Mangueras>>() {
            @Override
            public void onResponse(Call<List<Mangueras>> call, Response<List<Mangueras>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getmanguerasList10 = response.body();

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mangueras>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Listado de Clientes con DNI */
    private void getClienteDNI(){

        Call<List<LClientes>> call = mAPIService.getClienteDNI();

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesList10 = response.body();


                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientes>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Listado de Clientes con DNI */
    private void getClienteRUC(){

        Call<List<LClientes>> call = mAPIService.getClienteRUC();

        call.enqueue(new Callback<List<LClientes>>() {
            @Override
            public void onResponse(Call<List<LClientes>> call, Response<List<LClientes>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlclientesList10 = response.body();


                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LClientes>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Spinner de Tipo de Pago */
    private void getTipoPago(){

        Call<List<TipoPago>> call = mAPIService.getTipoPago();

        call.enqueue(new Callback<List<TipoPago>>() {
            @Override
            public void onResponse(Call<List<TipoPago>> call, Response<List<TipoPago>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.gettipopagoList10 = response.body();

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TipoPago>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Hash de la Contraseña */
    private String checkpassword(String clave){

        String lResult = "";
        String lasc1 = "";

        int lValor = 0;
        int lTam = 0;
        int lCar = 0;
        int lasc2 = 0;

        lTam = clave.length();

        for(int lcont = 1 ; lcont <= lTam; lcont += 1){

            switch (lcont){
                case 1:
                    lCar = 1;
                    lasc1 = clave.substring(0,1);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 2:
                    lCar = 3;
                    lasc1 = clave.substring(1,2);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 3:
                    lCar = 5;
                    lasc1 = clave.substring(2,3);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 4:
                    lCar = 7;
                    lasc1 = clave.substring(3,4);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 5:
                    lCar = 9;
                    lasc1 = clave.substring(4,5);
                    lasc2 = lasc1.charAt(0);
                    break;
                case 6:
                    lCar = 11;
                    lasc1 = clave.substring(5,6);
                    lasc2 = lasc1.charAt(0);
                    break;
            }

            lValor = lValor + lasc2 * lCar;

        }

        lResult = String.valueOf(lValor);

        return lResult;
    }

}