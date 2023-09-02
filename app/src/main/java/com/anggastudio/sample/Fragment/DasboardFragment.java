package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.anggastudio.sample.Login;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.CDia;
import com.anggastudio.sample.WebApiSVEN.Models.CTurno;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTurno;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DasboardFragment extends Fragment{

    private APIService mAPIService;

    public  static List<SettingTurno> getsettingTurnoList10;

    TextView nombre_grifero,fecha_inicio_grifero,turno_grifero,nombre_empresa,sucursal_empresa,slogan_empresa;

    CardView btn_Venta,btn_Cierrex,btn_Cambioturno,btn_Iniciodia,btn_Salir;

    Button btnCancelarTurno,btnCancelarInicio,btnAceptarTurno,btnAceptarInicio,btncancelarsalida,btnsalir;

    Dialog modalCambioTurno,modalInicioDia,modalAlerta,modalSalir,modalAlertaFecha,modalAlertaDiaActual,modalAlertaCTurnoActual,modalAlertaRIDiaActual;

    ShapeableImageView img_Logo;

    /*============================== Manejo pasivo de lecturas NFC ===============================*/
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext());
        Intent intent = new Intent(requireContext(), getActivity().getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFilters = new IntentFilter[]{tagIntentFilter};
        techLists = new String[][]{new String[]{NfcA.class.getName(), NfcB.class.getName(),
                NfcF.class.getName(), NfcV.class.getName(), IsoDep.class.getName(),
                MifareClassic.class.getName(), MifareUltralight.class.getName(),
                Ndef.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(requireActivity(), pendingIntent, intentFilters, techLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(requireActivity());
    }
    /*============================== Manejo pasivo de lecturas NFC - FIN =========================*/


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
        img_Logo           = view.findViewById(R.id.logo_dashboard);

        nombre_grifero.setText(GlobalInfo.getuserName10);
        fecha_inicio_grifero.setText("FECHA : " + GlobalInfo.getterminalFecha10);
        turno_grifero.setText("TURNO : " + String.valueOf(GlobalInfo.getterminalTurno10));

    /* ============================ OBTENER LOGO DESDE ALMACENAMIENTO LOCAL ===================== */
        File file = new File("/storage/emulated/0/appSven/logo.jpg");
        String rutaImagen="/storage/emulated/0/appSven/logo.jpg";
        if(!file.exists()){
            rutaImagen = "/storage/emulated/0/appSven/logo.png";
        }
        Uri logoUri = Uri.parse("file://" + rutaImagen);
        img_Logo.setImageURI(logoUri);
    /* ============================ OBTENER LOGO DESDE ALMACENAMIENTO LOCAL - FIN =============== */

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

        /** Lista de Turno - Filtrado por Turno*/
        getSettingTurno();

        /** Mostrar Alerta Inicio Día- Por fuera de fecha */
        modalAlertaDiaActual = new Dialog(getContext());
        modalAlertaDiaActual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaDiaActual.setContentView(R.layout.alerta_iniciodia_fechaactual);
        modalAlertaDiaActual.setCancelable(true);

        /** Mostrar Alerta Cambio Turno- Por fuera de fecha */
        modalAlertaCTurnoActual = new Dialog(getContext());
        modalAlertaCTurnoActual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaCTurnoActual.setContentView(R.layout.alerta_cambioturno_fechaactual);
        modalAlertaCTurnoActual.setCancelable(true);

        /** Mostrar Alerta - Realizar Inicio Día */
        modalAlertaRIDiaActual = new Dialog(getContext());
        modalAlertaRIDiaActual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaRIDiaActual.setContentView(R.layout.alert_realizar_iniciodia);
        modalAlertaRIDiaActual.setCancelable(true);

        /** Mostrar Modal - Cambio de Turno */
        modalCambioTurno = new Dialog(getContext());
        modalCambioTurno.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalCambioTurno.setContentView(R.layout.fragment_cambio_turno);
        modalCambioTurno.setCancelable(false);

        btn_Cambioturno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (SettingTurno settingTurno : GlobalInfo.getsettingTurnoList10 ) {

                    GlobalInfo.getSettingCompanyId10 = settingTurno.getCompanyID();
                    GlobalInfo.getSettingTurno10     = settingTurno.getTurno();
                    GlobalInfo.getSettingRango110    = settingTurno.getRango1();
                    GlobalInfo.getSettingRango210    = settingTurno.getRango2();

                    /** Hora Actual */
                    Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatdate  = new SimpleDateFormat("HHmmss");
                    String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());
                    Integer HoraActual           = Integer.valueOf(FechaHoraImpresion);

                    Calendar calendarfecha        = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    SimpleDateFormat formatfecha  = new SimpleDateFormat("dd");
                    String FechasImpresion        = formatfecha.format(calendarfecha.getTime());
                    Integer FechaActual           = Integer.valueOf(FechasImpresion);

                    Boolean xPase                 = false;

                    if (FechaActual == 1) {

                        if (GlobalInfo.getSettingTurno10.equals(0) && GlobalInfo.getterminalTurno10.equals(1)) {

                            if (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210) {
                                findOptranTurno(GlobalInfo.getterminalImei10);
                                xPase = true;
                            } else {
                                modalAlertaCTurnoActual.show();
                                return;
                            }

                        } else if (GlobalInfo.getSettingTurno10.equals(1) && GlobalInfo.getterminalTurno10.equals(2)) {

                            if (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210) {
                                findOptranTurno(GlobalInfo.getterminalImei10);
                                xPase = true;
                            } else {
                                modalAlertaCTurnoActual.show();
                                return;
                            }

                        } else if (GlobalInfo.getSettingTurno10.equals(2) && GlobalInfo.getterminalTurno10.equals(3)) {

                            if (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210) {
                                findOptranTurno(GlobalInfo.getterminalImei10);
                                xPase = true;
                            } else {
                                modalAlertaCTurnoActual.show();
                                return;
                            }

                        }

                    } else {

                        if (GlobalInfo.getSettingTurno10.equals(GlobalInfo.getterminalTurno10)) {

                            if (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210) {
                                findOptranTurno(GlobalInfo.getterminalImei10);
                                xPase = true;
                            } else {
                                modalAlertaCTurnoActual.show();
                                return;
                            }

                        }

                    }

                    if (xPase == true) {

                        modalCambioTurno.show();

                        btnCancelarTurno = modalCambioTurno.findViewById(R.id.btncancelarcambioturno);
                        btnAceptarTurno = modalCambioTurno.findViewById(R.id.btnagregarcambioturno);

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
                        break;
                    }



                /*    if ( GlobalInfo.getSettingTurno10.equals(GlobalInfo.getterminalTurno10)) {

                        if (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210) {


                            findOptranTurno(GlobalInfo.getterminalImei10);

                            modalCambioTurno.show();

                            btnCancelarTurno = modalCambioTurno.findViewById(R.id.btncancelarcambioturno);
                            btnAceptarTurno = modalCambioTurno.findViewById(R.id.btnagregarcambioturno);

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


                        }else{
                            modalAlertaCTurnoActual.show();
                            return;
                        }


                    }else if( GlobalInfo.getSettingTurno10.equals(0)){

                        if ((FechaActual == 1 && HoraActual >= 55000 && HoraActual <= 62000) ||
                                (FechaActual == 1 && HoraActual >= 135000 && HoraActual <= 142000) ||
                                   (FechaActual == 1 && HoraActual >= 215000 && HoraActual <= 222000) ){

                            findOptranTurno(GlobalInfo.getterminalImei10);

                            modalCambioTurno.show();

                            btnCancelarTurno = modalCambioTurno.findViewById(R.id.btncancelarcambioturno);
                            btnAceptarTurno = modalCambioTurno.findViewById(R.id.btnagregarcambioturno);

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


                        }else{
                            modalAlertaCTurnoActual.show();
                            return;
                        }

                    }
*/


                }

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

                for (SettingTurno settingTurno : GlobalInfo.getsettingTurnoList10 ) {

                    GlobalInfo.getSettingCompanyId10 = settingTurno.getCompanyID();
                    GlobalInfo.getSettingTurno10     = settingTurno.getTurno();
                    GlobalInfo.getSettingRango110    = settingTurno.getRango1();
                    GlobalInfo.getSettingRango210    = settingTurno.getRango2();

                    if (GlobalInfo.getSettingTurno10 .equals(0)) {

                        /** Hora Actual */
                        Calendar calendarprint = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                        SimpleDateFormat formatdate = new SimpleDateFormat("HHmmss");
                        String FechaHoraImpresion = formatdate.format(calendarprint.getTime());
                        Integer HoraActual = Integer.valueOf(FechaHoraImpresion);

                        /** Fecha Actual */
                        Calendar calendarfecha      = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                        SimpleDateFormat formatfecha  = new SimpleDateFormat("dd");
                        String FechasImpresion    = formatfecha.format(calendarfecha.getTime());
                        Integer FechaActual = Integer.valueOf(FechasImpresion);

                        if (FechaActual == 1) {

                            if (HoraActual >= 0 && HoraActual <= 3000) {
                                findOptranDia(GlobalInfo.getterminalImei10);
                            } else {
                                modalAlertaDiaActual.show();
                                return;
                            }

                        } else {

                            if (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210) {
                                findOptranDia(GlobalInfo.getterminalImei10);
                            } else {
                                modalAlertaDiaActual.show();
                                return;
                            }

                        }

                        modalInicioDia.show();

                        btnCancelarInicio = modalInicioDia.findViewById(R.id.btncancelariniciodia);
                        btnAceptarInicio = modalInicioDia.findViewById(R.id.btnagregariniciodia);

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

                      /*  if ((FechaActual == 1 && HoraActual >= 0 && HoraActual <= 3000 ) ||
                                (HoraActual >= GlobalInfo.getSettingRango110 && HoraActual <= GlobalInfo.getSettingRango210)) {

                            findOptranDia(GlobalInfo.getterminalImei10);

                            modalInicioDia.show();

                            btnCancelarInicio = modalInicioDia.findViewById(R.id.btncancelariniciodia);
                            btnAceptarInicio = modalInicioDia.findViewById(R.id.btnagregariniciodia);

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
                        }*/
                    }

                    break;
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

    /** API SERVICE - Setting Turno */
    private void getSettingTurno(){

        Call<List<SettingTurno>> call = mAPIService.getSettingTurno();

        call.enqueue(new Callback<List<SettingTurno>>() {
            @Override
            public void onResponse(Call<List<SettingTurno>> call, Response<List<SettingTurno>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getsettingTurnoList10 = response.body();

                   /* for(SettingTurno settingTurno: GlobalInfo.getsettingTurnoList10) {
                        if ((settingTurno.getTurno().equals(GlobalInfo.getterminalTurno10))
                                ) {
                            GlobalInfo.getSettingCompanyId10 = settingTurno.getCompanyID();
                            GlobalInfo.getSettingTurno10     = settingTurno.getTurno();
                            GlobalInfo.getSettingRango110    = settingTurno.getRango1();
                            GlobalInfo.getSettingRango210    = settingTurno.getRango2();
                            break;
                        }
                    }*/

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SettingTurno>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Tarjetas - RED - WIFI", Toast.LENGTH_SHORT).show();
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