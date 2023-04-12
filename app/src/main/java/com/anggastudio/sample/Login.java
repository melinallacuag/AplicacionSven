package com.anggastudio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    ImageButton configuracion;
    Button btniniciar;
    TextInputEditText usuario, contraseña;
    TextInputLayout alertuser,alertpassword;
    TextView imeii;
    String usuarioUser,contraseñaUser;

    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
                    Toast.makeText( getApplicationContext(), "Bienvenido al Sistema SVEN", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent( getApplicationContext(),Menu.class));

                }
            }
        });
    }


}