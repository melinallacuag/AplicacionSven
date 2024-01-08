package com.anggastudio.sample;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Company;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.Setting;
import com.anggastudio.sample.WebApiSVEN.Models.Terminal;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity{

    private APIService mAPIService;
    private NFCUtil nfcUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAPIService = GlobalInfo.getAPIService();

        nfcUtil = new NFCUtil(this);

        btniniciar      = findViewById(R.id.btnlogin);
        inputUsuario    = findViewById(R.id.usuario);
        inputContraseña = findViewById(R.id.contraseña);
        alertuser       = findViewById(R.id.textusuario);
        alertpassword   = findViewById(R.id.textcontraseña);
        configuracion   = findViewById(R.id.btnconfiguracion);
        imeii           = findViewById(R.id.imei);

        /**
         *  @CONFIGURAR:ImpresoraBluetooth
         */
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( getApplicationContext(),MainActivity.class));
            }
        });

        /**
         * @OBTENER:Imei
         */
        imeii.setText(ObtenerIMEI.getDeviceId(getApplicationContext()));
        imeii.setText("F6036B683498BFDA");
        GlobalInfo.getterminalImei10 = imeii.getText().toString();

        /**
         * @INGRESAR:Login
         */
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

        /**
         * @LISTADO:SpinnerTipoPago
         */
        getTipoPago();

        /**
         * @OBTENER_APISERVICE:Terminal
         */
        findTerminal(GlobalInfo.getterminalImei10.toUpperCase());

    }

    /**
     * @APISERVICE:Users
     */
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

                    if (usersList != null && !usersList.isEmpty()) {

                        Users user = usersList.get(0);

                        inputUsuario.setText(user.getUserID());
                        GlobalInfo.getuserID10     = user.getUserID();
                        GlobalInfo.getuserName10   = user.getNames();
                        GlobalInfo.getuserPass10   = user.getPassword();
                        GlobalInfo.getuserLocked10 = user.getLocked();

                        if (GlobalInfo.getuserLocked10 == false) {
                            Toast.makeText( getApplicationContext(), "El Usuario se encuentra bloqueado.", Toast.LENGTH_SHORT).show();
                        }else {

                            String getName = usuarioUser.trim();
                            String getPass = PasswordChecker.checkpassword(contraseñaUser.trim());

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

    /**
     * @APISERVICE:Terminal
     */
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
                        GlobalInfo.getterminalVentaPlaya10      = terminal.getVenta_Playa();
                        GlobalInfo.getterminalVentaTienda10     = terminal.getVenta_Tienda();
                        GlobalInfo.getReporteVendedor10         = terminal.getCierreX_RVendedor();
                        GlobalInfo.getReporteTarjetas10         = terminal.getCierreX_RTarjeta();
                        GlobalInfo.getVentasTipoPago10          = terminal.getCierreX_VTipoPago();
                        GlobalInfo.getVentasProductos10         = terminal.getCierreX_VProducto();
                        GlobalInfo.getVentasContometros10       = terminal.getCierreX_VContometro();
                        GlobalInfo.getTerminalNameCompany10     = terminal.getNameCompany();
                        GlobalInfo.getTerminalImageW10          = terminal.getImageW();
                        GlobalInfo.getTipoPapel10               = terminal.getImpresora();

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

    /**
     *  @APISERVICE:Empresa
     */
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

    /**
     * @APISERVICE:Lados
     */
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

    /**
     * @APISERVICE:Setting
     */
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
                        GlobalInfo.getsettingRutaLogo110       = String.valueOf(setting.getRutaLogo1());
                        GlobalInfo.getsettingRutaLogo210       = String.valueOf(setting.getRutaLogo2());
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

    /**
     * @APISERVICE:DetalleVentas
     */
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

    /**
     * @APISERVICE:Mangueras
     * */
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
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Mangueras - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:SpinnerTipoPago
     */
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
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Tipo de Pago - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @METODOS:CicloVida
     */
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