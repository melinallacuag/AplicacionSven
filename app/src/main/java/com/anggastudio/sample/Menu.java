package com.anggastudio.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.anggastudio.sample.Fragment.CierreXFragment;
import com.anggastudio.sample.Fragment.DasboardFragment;
import com.anggastudio.sample.Fragment.VentaFragment;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.CDia;
import com.anggastudio.sample.WebApiSVEN.Models.CTurno;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private APIService mAPIService;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    Button btncancelar,btnagregar,btnsalir;

    private Dialog modalCambioTurno,modalIcioDia,modalAlerta,modalSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAPIService = GlobalInfo.getAPIService();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DasboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dasboard);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        /** Mostrar Modal de Cambio de Turno */
        modalAlerta = new Dialog(this);
        modalAlerta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlerta.setContentView(R.layout.cambioturno_inciodia_alerta);
        modalAlerta.setCancelable(true);

        switch (item.getItemId()) {

            case R.id.nav_dasboard:

                FragmentManager fragmentManager = getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                int fragmentContainerId = R.id.fragment_container;
                DasboardFragment dasboardFragment = new DasboardFragment();
                fragmentTransaction.replace(fragmentContainerId, dasboardFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;

            case R.id.nav_venta:

                FragmentManager fragmentManagerVenta = getSupportFragmentManager();

                FragmentTransaction fragmentTransactionVenta = fragmentManagerVenta.beginTransaction();

                int fragmentContainerVenta = R.id.fragment_container;
                VentaFragment ventaFragment = new VentaFragment();
                fragmentTransactionVenta.replace(fragmentContainerVenta, ventaFragment);
                fragmentTransactionVenta.addToBackStack(null);
                fragmentTransactionVenta.commit();

                break;

            case R.id.nav_cierrex:

                FragmentManager fragmentManagerCierreX = getSupportFragmentManager();

                FragmentTransaction fragmentTransactionCierreX = fragmentManagerCierreX.beginTransaction();

                int fragmentContainerCierreX = R.id.fragment_container;
                CierreXFragment cierreXFragment = new CierreXFragment();
                fragmentTransactionCierreX.replace(fragmentContainerCierreX, cierreXFragment);
                fragmentTransactionCierreX.addToBackStack(null);
                fragmentTransactionCierreX.commit();

                break;

            case R.id.nav_cambioturno:

                /** Mostrar Modal de Cambio de Turno */
                modalCambioTurno = new Dialog(this);
                modalCambioTurno.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalCambioTurno.setContentView(R.layout.fragment_cambio_turno);
                modalCambioTurno.setCancelable(false);

                /** API Retrofit - Cambio de Turno */
                findOptranTurno(GlobalInfo.getterminalImei10);

                btncancelar    = modalCambioTurno.findViewById(R.id.btncancelarcambioturno);
                btnagregar     = modalCambioTurno.findViewById(R.id.btnagregarcambioturno);

                btncancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalCambioTurno.dismiss();
                    }
                });
                btnagregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            cerrarTurno(GlobalInfo.getterminalID10);

                            startActivity(intent);
                            finalize();

                            Toast.makeText(getApplicationContext(), "SE GENERO EL CAMBIO DE TURNO ", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;

            case R.id.nav_iniciodia:

                /** Mostrar Modal de Inicio de Día */
                modalIcioDia = new Dialog(this);
                modalIcioDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalIcioDia.setContentView(R.layout.fragment_inicio_dia);
                modalIcioDia.setCancelable(false);

                /** API Retrofit - Inicio de Día */
                findOptranDia(GlobalInfo.getterminalImei10);

                btncancelar    = modalIcioDia.findViewById(R.id.btncancelariniciodia);
                btnagregar     = modalIcioDia.findViewById(R.id.btnagregariniciodia);

                btncancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalIcioDia.dismiss();
                    }
                });
                btnagregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            iniciarDia(GlobalInfo.getterminalID10);

                            startActivity(intent);
                            finalize();

                            Toast.makeText(getApplicationContext(), "SE GENERO EL INICIO DE DÍA", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;

            case R.id.nav_salir:

                /** Mostrar Modal de Inicio de Día */
                modalSalir = new Dialog(this);
                modalSalir.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalSalir.setContentView(R.layout.fragment_salir);
                modalSalir.setCancelable(false);

                modalSalir.show();

                btncancelar  = modalSalir.findViewById(R.id.btncancelarsalida);
                btnsalir     = modalSalir.findViewById(R.id.btnsalir);

                btncancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalSalir.dismiss();
                    }
                });
                btnsalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finalize();

                            Toast.makeText(getApplicationContext(), "CERRAR SESIÓN", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DasboardFragment()).commit();
        }

    }

    private void iniciarDia(String _terminalID){

        Call<CDia> call = mAPIService.postCDia(_terminalID);

        call.enqueue(new Callback<CDia>() {
            @Override
            public void onResponse(Call<CDia> call, Response<CDia> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<CDia> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cerrarTurno(String _terminalID){

        Call<CTurno> call = mAPIService.postCTurno(_terminalID);

        call.enqueue(new Callback<CTurno>() {
            @Override
            public void onResponse(Call<CTurno> call, Response<CTurno> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<CTurno> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API SERVICE - Optran Inicio de Día */
    private void findOptranDia(String id){

        Call<List<Optran>> call = mAPIService.findOptran(id);

        call.enqueue(new Callback<List<Optran>>() {
            @Override
            public void onResponse(Call<List<Optran>> call, Response<List<Optran>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    GlobalInfo.getpase10 = false;

                    for(Optran optran: optranList) {
                        GlobalInfo.getpase10 = true;
                    }

                    if (GlobalInfo.getpase10 == true){
                        modalAlerta.show();
                    }else{
                        modalIcioDia.show();
                    }

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optran>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** API SERVICE - Optran Cambio de Turno*/
    private void findOptranTurno(String id){

        Call<List<Optran>> call = mAPIService.findOptran(id);

        call.enqueue(new Callback<List<Optran>>() {
            @Override
            public void onResponse(Call<List<Optran>> call, Response<List<Optran>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    GlobalInfo.getpase10 = false;

                    for(Optran optran: optranList) {
                        GlobalInfo.getpase10 = true;
                    }

                    if (GlobalInfo.getpase10 == true){
                        modalAlerta.show();
                    }else{
                        modalCambioTurno.show();
                    }

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optran>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

}