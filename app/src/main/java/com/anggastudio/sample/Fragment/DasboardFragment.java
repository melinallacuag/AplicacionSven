package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.anggastudio.sample.Login;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.CDia;
import com.anggastudio.sample.WebApiSVEN.Models.CTurno;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DasboardFragment extends Fragment{

    private APIService mAPIService;

    TextView nombre_grifero,fecha_inicio_grifero,turno_grifero,nombre_empresa,sucursal_empresa,slogan_empresa;

    CardView btn_Venta,btn_Cierrex,btn_Cambioturno,btn_Iniciodia,btn_Salir;

    Button btnCancelarTurno,btnCancelarInicio,btnAceptarTurno,btnAceptarInicio,btncancelarsalida,btnsalir;

    Dialog modalCambioTurno,modalInicioDia,modalAlerta,modalSalir,modalAlertaFecha,modalAlertaDiaActual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dasboard, container, false);

        mAPIService = GlobalInfo.getAPIService();

        nombre_grifero        = view.findViewById(R.id.nombre_grifero);
        fecha_inicio_grifero  = view.findViewById(R.id.fecha_inicio);
        turno_grifero         = view.findViewById(R.id.turno_grifero);

        nombre_empresa    = view.findViewById(R.id.nombre_empresa);
        sucursal_empresa  = view.findViewById(R.id.sucursal_empresa);
        slogan_empresa    = view.findViewById(R.id.slogan_empresa);

        btn_Venta         = view.findViewById(R.id.btnVenta);
        btn_Cierrex       = view.findViewById(R.id.btnCierreX);
        btn_Cambioturno   = view.findViewById(R.id.btnCambioTurno);
        btn_Iniciodia     = view.findViewById(R.id.btnInicioDia);
        btn_Salir         = view.findViewById(R.id.btnSalir);

        nombre_grifero.setText(GlobalInfo.getuserName10);
        fecha_inicio_grifero.setText("FECHA : " + GlobalInfo.getterminalFecha10);
        turno_grifero.setText("TURNO : " + String.valueOf(GlobalInfo.getterminalTurno10));

        String DirSucursal = (GlobalInfo.getBranchCompany10 != null) ? GlobalInfo.getBranchCompany10 : "";

        DirSucursal = DirSucursal.replace("-","");

        nombre_empresa.setText(GlobalInfo.getNameCompany10);
        sucursal_empresa.setText(DirSucursal);
        slogan_empresa.setText(GlobalInfo.getSloganCompany10);

        /** Ir a la Pantalla - Venta */
        btn_Venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManagerVenta = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionVenta = fragmentManagerVenta.beginTransaction();
                int fragmentContainerVenta = R.id.fragment_container;
                VentaFragment ventaFragment = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    ventaFragment = new VentaFragment();
                }

                fragmentTransactionVenta.replace(fragmentContainerVenta, ventaFragment);
                fragmentTransactionVenta.addToBackStack(null);
                fragmentTransactionVenta.commit();

            }
        });

        /** Ir a la Pantalla - Cierre X */
        btn_Cierrex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManagerCierreX = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionCierreX = fragmentManagerCierreX.beginTransaction();
                int fragmentContainerCierreX = R.id.fragment_container;
                CierreXFragment cierreXFragment = new CierreXFragment();

                fragmentTransactionCierreX.replace(fragmentContainerCierreX, cierreXFragment);
                fragmentTransactionCierreX.addToBackStack(null);
                fragmentTransactionCierreX.commit();

            }
        });

        /** Mostrar Alerta - Si tiene ninguna venta pendiente */
        modalAlerta = new Dialog(getContext());
        modalAlerta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlerta.setContentView(R.layout.cambioturno_inciodia_alerta);
        modalAlerta.setCancelable(true);

        /** Mostrar Alerta - Cambio de Inicio*/
        modalAlertaFecha = new Dialog(getContext());
        modalAlertaFecha.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaFecha.setContentView(R.layout.alerta_cambioinicio_fecha);
        modalAlertaFecha.setCancelable(true);

        /** Mostrar Alerta - Por fuera de fecha */
        modalAlertaDiaActual = new Dialog(getContext());
        modalAlertaDiaActual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaDiaActual.setContentView(R.layout.alerta_iniciodia_fechaactual);
        modalAlertaDiaActual.setCancelable(true);

        /** Mostrar Modal - Cambio de Turno */
        modalCambioTurno = new Dialog(getContext());
        modalCambioTurno.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalCambioTurno.setContentView(R.layout.fragment_cambio_turno);
        modalCambioTurno.setCancelable(false);

        btn_Cambioturno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** API Retrofit - Cambio de Turno */
                findOptranTurno(GlobalInfo.getterminalImei10);

                modalCambioTurno.show();

                btnCancelarTurno    = modalCambioTurno.findViewById(R.id.btncancelarcambioturno);
                btnAceptarTurno     = modalCambioTurno.findViewById(R.id.btnagregarcambioturno);

                btnCancelarTurno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalCambioTurno.dismiss();
                    }
                });

                btnAceptarTurno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modalAlerta.show();

                        try {
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            cerrarTurno(GlobalInfo.getterminalID10);

                            startActivity(intent);
                            finalize();

                            Toast.makeText(getContext(), "SE GENERO EL CAMBIO DE TURNO ", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        /** Mostrar Modal - Inicio de Día */
        modalInicioDia = new Dialog(getContext());
        modalInicioDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalInicioDia.setContentView(R.layout.fragment_inicio_dia);
        modalInicioDia.setCancelable(false);

        btn_Iniciodia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Hora Actual */
                Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                SimpleDateFormat formatdate  = new SimpleDateFormat("HHmmss");
                String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());
                Integer HoraActual           = Integer.valueOf(FechaHoraImpresion);

                if (HoraActual >= 55000 && HoraActual <= 61000) {

                    /** API Retrofit - Inicio de Día */
                    findOptranDia(GlobalInfo.getterminalImei10);

                    modalInicioDia.show();

                    btnCancelarInicio    = modalInicioDia.findViewById(R.id.btncancelariniciodia);
                    btnAceptarInicio     = modalInicioDia.findViewById(R.id.btnagregariniciodia);

                    btnCancelarInicio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modalInicioDia.dismiss();
                        }
                    });

                    btnAceptarInicio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            modalAlerta.show();

                            try {
                                Intent intent = new Intent(getContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                iniciarDia(GlobalInfo.getterminalID10);

                                startActivity(intent);
                                finalize();

                                Toast.makeText(getContext(), "SE GENERO EL INICIO DE DÍA", Toast.LENGTH_SHORT).show();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    modalAlertaDiaActual.show();
                }

              }
        });

        /** Mostrar Modal - Salir */
        modalSalir = new Dialog(getContext());
        modalSalir.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalSalir.setContentView(R.layout.fragment_salir);
        modalSalir.setCancelable(false);

        btn_Salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalSalir.show();

                btncancelarsalida    = modalSalir.findViewById(R.id.btncancelarsalida);
                btnsalir     = modalSalir.findViewById(R.id.btnsalir);

                btncancelarsalida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalSalir.dismiss();
                    }
                });

                btnsalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finalize();
                            Toast.makeText(getContext(), "SE CERRO SESIÓN", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        return view;

    }

    /** API SERVICE - Optran Cambio de Turno*/
    private void findOptranTurno(String id){

        Call<List<Optran>> call = mAPIService.findOptran(id);

        call.enqueue(new Callback<List<Optran>>() {
            @Override
            public void onResponse(Call<List<Optran>> call, Response<List<Optran>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    GlobalInfo.getpase10 = false;

                    for(Optran optran: optranList) {
                        GlobalInfo.getpase10 = true;
                    }

                    if (GlobalInfo.getpase10 == true){
                        modalAlerta.show();
                        modalCambioTurno.dismiss();
                        return;
                    }

                    modalCambioTurno.show();

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optran>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Optran> optranList = response.body();

                    GlobalInfo.getpase10 = false;

                    for(Optran optran: optranList) {
                        GlobalInfo.getpase10 = true;
                    }

                    if (GlobalInfo.getpase10 == true){
                        modalAlerta.show();
                        modalInicioDia.dismiss();
                        return;
                    }

                    modalInicioDia.show();

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optran>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Cerrar - Turno */
    private void cerrarTurno(String _terminalID){

        Call<CTurno> call = mAPIService.postCTurno(_terminalID);

        call.enqueue(new Callback<CTurno>() {
            @Override
            public void onResponse(Call<CTurno> call, Response<CTurno> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<CTurno> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Inicio - Día */
    private void iniciarDia(String _terminalID){

        Call<CDia> call = mAPIService.postCDia(_terminalID);

        call.enqueue(new Callback<CDia>() {
            @Override
            public void onResponse(Call<CDia> call, Response<CDia> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<CDia> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });

    }

}