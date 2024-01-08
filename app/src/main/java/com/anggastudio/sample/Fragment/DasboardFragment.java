package com.anggastudio.sample.Fragment;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.anggastudio.sample.Login;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.CDia;
import com.anggastudio.sample.WebApiSVEN.Models.CTurno;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTurno;
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

    private static final int TU_CODIGO_DE_SOLICITUD_DE_PERMISO = 123;

    private APIService mAPIService;
    private NFCUtil nfcUtil;

    TextView ventas,nombre_grifero,fecha_inicio_grifero,turno_grifero,nombre_empresa,sucursal_empresa,slogan_empresa;

    CardView btn_Venta,btn_Cierrex,btn_Cambioturno,btn_Iniciodia,btn_Salir;

    Button btnCancelarTurno,btnCancelarInicio,btnAceptarTurno,btnAceptarInicio,btncancelarsalida,btnsalir,btn_CancelarIngreso,btn_AceptarIngreso;

    Dialog modalCambioTurno,modalInicioDia,modalAlertaVentaPendiente,modalSalir,modalAlertaDiaActual,
            modalAlertaCTurnoActual,modalAlertaIngreso,modalInicioDiaGenerado;

    ShapeableImageView img_Logo;
    ImageView imageee;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcUtil =  new NFCUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dasboard, container, false);

        mAPIService = GlobalInfo.getAPIService();

        nombre_grifero        = view.findViewById(R.id.nombre_grifero);
        fecha_inicio_grifero  = view.findViewById(R.id.fecha_inicio);
        turno_grifero         = view.findViewById(R.id.turno_grifero);

        nombre_empresa        = view.findViewById(R.id.nombre_empresa);
        sucursal_empresa      = view.findViewById(R.id.sucursal_empresa);
        slogan_empresa        = view.findViewById(R.id.slogan_empresa);

        btn_Venta             = view.findViewById(R.id.btnVenta);
        btn_Cierrex           = view.findViewById(R.id.btnCierreX);
        btn_Cambioturno       = view.findViewById(R.id.btnCambioTurno);
        btn_Iniciodia         = view.findViewById(R.id.btnInicioDia);
        btn_Salir             = view.findViewById(R.id.btnSalir);
        img_Logo              = view.findViewById(R.id.logo_dashboard);

        ventas                  = view.findViewById(R.id.ventas);
        imageee                 = view.findViewById(R.id.imageee);

        /**
         * @OBTENER:DatoGerealUserTerminal
         */
        nombre_grifero.setText(GlobalInfo.getuserName10);
        fecha_inicio_grifero.setText("FECHA : " + GlobalInfo.getterminalFecha10);
        turno_grifero.setText("TURNO : " + GlobalInfo.getterminalTurno10);

        /**
         * @MOSTRAR:LogoEmpresa_Dasboard
         */
        String rutaImagen="/storage/emulated/0/appSven/" + GlobalInfo.getsettingRutaLogo110;
        File file = new File(rutaImagen);

        if(!file.exists()){
            rutaImagen = "/storage/emulated/0/appSven/sinfoto.jpg";
        }

        Uri logoUri = Uri.parse("file://" + rutaImagen);
        img_Logo.setImageURI(logoUri);

        /**
         * @OBTENER:DatoGeneralCompania
         */
        String DirSucursal = (GlobalInfo.getBranchCompany10 != null) ? GlobalInfo.getBranchCompany10 : "";
        DirSucursal = DirSucursal.replace("-","");
        nombre_empresa.setText(GlobalInfo.getNameCompany10);
        sucursal_empresa.setText(DirSucursal);
        slogan_empresa.setText(GlobalInfo.getSloganCompany10);

        /**
         * @MODAL:MostrarAlertaInicioDía_Ingresar
         */
        modalAlertaIngreso = new Dialog(getContext());
        modalAlertaIngreso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaIngreso.setContentView(R.layout.alerta_iniciodia_ingreso);
        modalAlertaIngreso.setCancelable(true);

        if (GlobalInfo.getterminalVentaPlaya10) {
            ventas.setText("Grifo");
            imageee.setImageResource(R.drawable.icon_salefuel);
        } else if (GlobalInfo.getterminalVentaTienda10) {
            ventas.setText("Tienda");
            imageee.setImageResource(R.drawable.iconcaja);
        }
        /**
         * @PANTALLA:Venta
         */
        btn_Venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(GlobalInfo.getterminalVentaPlaya10){

                    if ( GlobalInfo.getCDiaList10 != null && !GlobalInfo.getCDiaList10.isEmpty() ){
                        FragmentManager fragmentManagerVenta = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransactionVenta = fragmentManagerVenta.beginTransaction();
                        int fragmentContainerVenta  = R.id.fragment_container;
                        VentaFragment ventaFragment = new VentaFragment();

                        fragmentTransactionVenta.replace(fragmentContainerVenta, ventaFragment);
                        fragmentTransactionVenta.addToBackStack(null);
                        fragmentTransactionVenta.commit();

                    }else{

                        modalAlertaIngreso.show();

                        btn_CancelarIngreso  = modalAlertaIngreso.findViewById(R.id.btnCancelarIngreso);
                        btn_AceptarIngreso   = modalAlertaIngreso.findViewById(R.id.btnAceptarIngreso);

                        btn_CancelarIngreso.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modalAlertaIngreso.dismiss();
                            }
                        });

                        btn_AceptarIngreso.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                FragmentManager fragmentManagerVenta = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransactionVenta = fragmentManagerVenta.beginTransaction();
                                int fragmentContainerVenta  = R.id.fragment_container;
                                VentaFragment ventaFragment = new VentaFragment();

                                fragmentTransactionVenta.replace(fragmentContainerVenta, ventaFragment);
                                fragmentTransactionVenta.addToBackStack(null);
                                fragmentTransactionVenta.commit();

                                modalAlertaIngreso.dismiss();
                            }
                        });
                    }

                }else if(GlobalInfo.getterminalVentaTienda10){

                    FragmentManager fragmentManagerCarrito = getActivity().getSupportFragmentManager();

                    FragmentTransaction fragmentTransactionCarrito = fragmentManagerCarrito.beginTransaction();

                    int fragmentContainerCarrito = R.id.fragment_container;
                    ProductosFragment productosFragment = new ProductosFragment();
                    fragmentTransactionCarrito.replace(fragmentContainerCarrito, productosFragment);
                    fragmentTransactionCarrito.addToBackStack(null);
                    fragmentTransactionCarrito.commit();
                }

            }
        });

        /**
         * @PANTALLA:CierreX
         */
        btn_Cierrex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(GlobalInfo.getterminalVentaPlaya10){
                    FragmentManager fragmentManagerCierreX = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransactionCierreX = fragmentManagerCierreX.beginTransaction();
                    int fragmentContainerCierreX    = R.id.fragment_container;
                    CierreXFragment cierreXFragment = new CierreXFragment();

                    fragmentTransactionCierreX.replace(fragmentContainerCierreX, cierreXFragment);
                    fragmentTransactionCierreX.addToBackStack(null);
                    fragmentTransactionCierreX.commit();
                }else if(GlobalInfo.getterminalVentaTienda10){
                    FragmentManager fragmentManagerCierreXTienda = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransactionCierreXTienda = fragmentManagerCierreXTienda.beginTransaction();
                    int fragmentContainerCierreXTienda    = R.id.fragment_container;
                    CierreXTiendaFragment cierreXTiendaFragment = new CierreXTiendaFragment();

                    fragmentTransactionCierreXTienda.replace(fragmentContainerCierreXTienda, cierreXTiendaFragment);
                    fragmentTransactionCierreXTienda.addToBackStack(null);
                    fragmentTransactionCierreXTienda.commit();
                }

            }
        });

        /**
         * @MODAL:MostrarAlerta_VentaPendiente
         */
        modalAlertaVentaPendiente = new Dialog(getContext());
        modalAlertaVentaPendiente.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaVentaPendiente.setContentView(R.layout.cambioturno_inciodia_alerta);
        modalAlertaVentaPendiente.setCancelable(true);

        /**
         * @MODAL:MostrarAlerta_InicioDíaGenerado
         */
        modalInicioDiaGenerado = new Dialog(getContext());
        modalInicioDiaGenerado.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalInicioDiaGenerado.setContentView(R.layout.alerta_cdia);
        modalInicioDiaGenerado.setCancelable(true);

        /**
         * @MODAL:MostrarAlerta_InicioDíaFueraFecha
         */
        modalAlertaDiaActual = new Dialog(getContext());
        modalAlertaDiaActual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaDiaActual.setContentView(R.layout.alerta_iniciodia_fechaactual);
        modalAlertaDiaActual.setCancelable(true);

        /**
         * @MODAL:MostrarAlerta_CambioTurnoFueraFecha
         */
        modalAlertaCTurnoActual = new Dialog(getContext());
        modalAlertaCTurnoActual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaCTurnoActual.setContentView(R.layout.alerta_cambioturno_fechaactual);
        modalAlertaCTurnoActual.setCancelable(true);

        /**
         * @MODAL:Mostrar_GenerarCambioTurno
         */
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

                                modalAlertaVentaPendiente.show();

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

                }

            }
        });

        /**
         * @MODAL:Mostrar_GenerarInicioDía
         */
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

                            Calendar calendarprint = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                            SimpleDateFormat formatdate = new SimpleDateFormat("HHmmss");
                            String FechaHoraImpresion = formatdate.format(calendarprint.getTime());
                            Integer HoraActual = Integer.valueOf(FechaHoraImpresion);


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

                            if (GlobalInfo.getCDiaList10 != null && !GlobalInfo.getCDiaList10.isEmpty()){

                                /**  No puede realizar Inicio de Día. Porque ya esta genero. **/
                                modalInicioDiaGenerado.show();

                            }else {

                                modalInicioDia.show();

                                btnCancelarInicio = modalInicioDia.findViewById(R.id.btncancelariniciodia);
                                btnAceptarInicio  = modalInicioDia.findViewById(R.id.btnagregariniciodia);

                                btnCancelarInicio.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        modalInicioDia.dismiss();
                                    }
                                });

                                btnAceptarInicio.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        modalAlertaVentaPendiente.show();

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

                            }

                        }

                        break;
                    }
                }

        });

        /**
         * @MODAL:MostrarModalSalir
         * */
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

        /**
         * @OBTENER:CambioDía
         */
        findCDia(GlobalInfo.getterminalID10);

        /**
         * @OBTENER:ListadoTurno
         */
        getSettingTurno();

        return view;

    }

    /**
     * @APISERVICE:InicioDía
     */
    private void findCDia(String terminalid){
        Call<List<CDia>> call = mAPIService.findCDia(terminalid);

        call.enqueue(new Callback<List<CDia>>() {
            @Override
            public void onResponse(Call<List<CDia>> call, Response<List<CDia>> response) {
                try {
                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error Dia: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                        GlobalInfo.getCDiaList10 = response.body();

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CDia>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Dia - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:SettingTurno
     */
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

    /**
     * @APISERVICE:OptranCambioTurno
     */
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
                        modalAlertaVentaPendiente.show();
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

    /**
     * @APISERVICE:OptranInicioDía
     */
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
                        modalAlertaVentaPendiente.show();
                        modalInicioDia.dismiss();
                        return;
                    }

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

    /**
     * @APISERVICE:CerrarTurno
     */
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

    /**
     * @APISERVICE:InicioDía
     */
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