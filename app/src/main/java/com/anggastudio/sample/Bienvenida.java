package com.anggastudio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Bienvenida extends AppCompatActivity {

    Animation logoAnim, tituloAnim, parrafoAnim;
    ImageView logo;
    TextView  titulo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        logoAnim    = AnimationUtils.loadAnimation(this,R.anim.logo_animation);
        tituloAnim  = AnimationUtils.loadAnimation(this,R.anim.titulo_animation);
        parrafoAnim = AnimationUtils.loadAnimation(this,R.anim.parrafo_animation);

        logo        = findViewById(R.id.logoapp);
        titulo      = findViewById(R.id.bienvenida);
        slogan      = findViewById(R.id.parrafo);

        logo.startAnimation(logoAnim);
        titulo.startAnimation(tituloAnim);
        slogan.startAnimation(parrafoAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Bienvenida.this,Login.class));
                finish();
            }
        },3000);
    }

}