package com.anggastudio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Company;
import com.anggastudio.sample.WebApiSVEN.Models.Terminal;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    ImageButton configuracion;
    Button btniniciar;
    TextInputEditText usuario, contraseña;
    TextInputLayout alertuser,alertpassword;
    TextView imeii;
    String usuarioUser,contraseñaUser;

    EditText editText;

    private APIService mAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAPIService = GlobalInfo.getAPIService();

        btniniciar     = findViewById(R.id.btnlogin);
        usuario        = findViewById(R.id.usuario);
        contraseña     = findViewById(R.id.contraseña);
        alertuser      = findViewById(R.id.textusuario);
        alertpassword  = findViewById(R.id.textcontraseña);

        /**
         * Boton par configurar la impresión bluetooth.
         */

        configuracion = findViewById(R.id.btnconfiguracion);
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( getApplicationContext(),MainActivity.class));
            }
        });

        /**
         * Detectar el IMEI
         */

        imeii = findViewById(R.id.imei);
        imeii.setText(ObtenerIMEI.getDeviceId(getApplicationContext()));

        GlobalInfo.getterminalImei10 = imeii.getText().toString();
        findTerminal(GlobalInfo.getterminalImei10.toUpperCase());


        btniniciar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                usuarioUser    = usuario.getText().toString();
                contraseñaUser = contraseña.getText().toString();

                if(usuarioUser.isEmpty()){
                    alertuser.setError("El campo usuario es obligatorio");

                }else if(contraseñaUser.isEmpty()){
                    alertpassword.setError("El campo contraseña es obligatorio");

                }else{
                    alertuser.setErrorEnabled(false);
                    alertpassword.setErrorEnabled(false);

                    findUsers(usuario.getText().toString());

                }
            }
        });
    }

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

                    List<Users> usersList = response.body();

                    for(Users user: usersList){
                        usuario.setText(user.getUserID());
                        GlobalInfo.getuserID10 = user.getUserID();
                        GlobalInfo.getuserName10 = user.getNames();
                        GlobalInfo.getuserPass10  = user.getPassword();
                    }

                    String getName = usuario.getText().toString();
                    String getPass = checkpassword(contraseña.getText().toString());

                    if(getPass.equals(GlobalInfo.getuserPass10) || getName.equals(GlobalInfo.getuserName10 )){

                        Toast.makeText( getApplicationContext(), "Bienvenido al Sistema SVEN", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent( getApplicationContext(),Menu.class));

                    }
                    else {
                        Toast.makeText( getApplicationContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
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

    private void findTerminal(String id){

        Call<List<Terminal>> call = mAPIService.findTerminal(id);

        call.enqueue(new Callback<List<Terminal>>() {
            @Override
            public void onResponse(Call<List<Terminal>> call, Response<List<Terminal>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText( getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Terminal> terminalList = response.body();

                    for(Terminal terminal: terminalList){
                        GlobalInfo.getterminalID10              = String.valueOf(terminal.getTerminalID());
                        GlobalInfo.getterminalImei10            = String.valueOf(terminal.getImei());
                        GlobalInfo.getterminalFecha10           = String.valueOf(terminal.getFecha_Proceso());
                        GlobalInfo.getterminalTurno10           = Integer.valueOf(terminal.getTurno());
                        GlobalInfo.getterminalCompanyID10       = Integer.valueOf(terminal.getCompanyID());
                        GlobalInfo.getterminalAlmacenID10       = Integer.valueOf(terminal.getAlmacenID());
                        GlobalInfo.getterminalConsultaSunat10   = Boolean.valueOf(terminal.getConsulta_Sunat());
                        GlobalInfo.getterminalVentaAutomatica10 = Boolean.valueOf(terminal.getVenta_Automatica());
                        GlobalInfo.getterminalVentaPlaya10      = Boolean.valueOf(terminal.getVenta_Playa());
                        GlobalInfo.getterminalVentaTienda10     = Boolean.valueOf(terminal.getVenta_Tienda());
                        GlobalInfo.getterminalVentaCredito10    = Boolean.valueOf(terminal.getVenta_Credito());
                        GlobalInfo.getterminalVentaTarjeta10    = Boolean.valueOf(terminal.getVenta_Tarjeta());
                        GlobalInfo.getterminalVentaGratuita10   = Boolean.valueOf(terminal.getVenta_Gratuita());
                        GlobalInfo.getterminalVentaSerafin10    = Boolean.valueOf(terminal.getVenta_Serafin());

                        GlobalInfo.getterminalFacturaSerie10    = terminal.getFactura_Serie();
                        GlobalInfo.getterminalFacturaNumero10   = terminal.getFactura_Numero();
                        GlobalInfo.getterminalBoletaSerie10     = terminal.getBoleta_Serie();
                        GlobalInfo.getterminalBoletaNumero10    = terminal.getBoleta_Numero();
                        GlobalInfo.getterminalCreditoSerie10    = terminal.getnCredito_Serie();
                        GlobalInfo.getterminalCreditoNumero10   = terminal.getnCredito_Numero();
                        GlobalInfo.getterminalDebitoSerie10     = terminal.getnDebito_Serie();
                        GlobalInfo.getterminalDebitoNumero10    = terminal.getnDebito_Numero();
                        GlobalInfo.getterminalSerafinSerie10    = terminal.getSerafin_Serie();
                        GlobalInfo.getterminalSerafinNumero10   = terminal.getSerafin_Numero();
                        GlobalInfo.getterminalDespachoSerie10   = terminal.getnDespacho_Serie();
                        GlobalInfo.getterminalDespachoNumero10  = terminal.getnDespacho_Numero();

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