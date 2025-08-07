package com.anggastudio.sample.Fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.ReporteEgresoAdapter;
import com.anggastudio.sample.Adapter.ReporteTarjetasAdapter;
import com.anggastudio.sample.Adapter.ReporteVendedorAdapter;
import com.anggastudio.sample.Adapter.VContometroAdapter;
import com.anggastudio.sample.Adapter.VProductoAdapter;
import com.anggastudio.sample.Adapter.VProductoTiendaAdapter;
import com.anggastudio.sample.Adapter.VTipoPagoAdapter;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.RAnulados;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteEgreso;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteVendedor;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VProductoTienda;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CierreXTiendaFragment extends Fragment {

    private APIService mAPIService;
    private NFCUtil nfcUtil;

    TextView GranEgresoTotal,TotalDocAnulados,DocAnulados,NroDespacho,TotalDespacho,Cajero,Turno,FechaTrabajo,
            FechaHoraFin,FechaHoraIni,textSucural,textNombreEmpresa,
            TotalMontoPago,totalPagoBruto,
            TotalDescuento2,TotalIncremento,GranTotal,
            TotalCantidadProdTienda,TotalMontoProdTienda;

    String TotalREgresoSoles,RAnuladosSoles10,RDespachosSoles10,SProductosTotalDescTienda,SProductosTotalIncrementoTienda,
            TotalPagosSoles,MontoBruto,TotalRTarjetasSoles,
            SProductosTotalCantidadTienda,SProductosTotalSolesTienda;

    Button imprimirCierreX;
    Dialog modalAlerta;

    RecyclerView recyclerReporteEgreso,recyclerVProductosTienda,recyclerVTipoPago,recyclerReporteTarj;

    ReporteEgresoAdapter reporteEgresoAdapter;
    List<ReporteEgreso> reporteEgresoList;

    ReporteTarjetasAdapter reporteTarjetasAdapter;
    List<ReporteTarjetas> reporteTarjetasList;

    VProductoTiendaAdapter vProductoTiendaAdapter;
    List<VProductoTienda> vProductoTiendaList;

    VTipoPagoAdapter vTipoPagoAdapter;
    List<VTipoPago> vTipoPagoList;

    List<RAnulados> rAnuladosList;
    List<RAnulados> rDescuentoList;

    ImageView logoCierreX;

    LinearLayout reporteVendedor;

    Double REgresoTotal,AnuladosSoles10,DespachosSoles10, RContometrosTotalGLL, RProductosTotalIncremento,RProductosTotalDesc, RPagosTotalSoles,RTarjetasTotal,RVendedorTotal,
            RProductosCantidadTienda,RProductosTotalSolesTienda,RProductosDescTienda,RProductosIncrementoTienda,MontoBrutoTienda,montoBrutoTotal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcUtil = new NFCUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cierre_x_tienda, container, false);

        mAPIService = GlobalInfo.getAPIService();

        textNombreEmpresa   = view.findViewById(R.id.textNombreEmpresa);
        textSucural         = view.findViewById(R.id.textSucural);
        FechaHoraIni        = view.findViewById(R.id.FechaHoraIni);
        FechaHoraFin        = view.findViewById(R.id.FechaHoraFin);
        FechaTrabajo        = view.findViewById(R.id.FechaTrabajo);
        Turno               = view.findViewById(R.id.Turno);
        Cajero              = view.findViewById(R.id.Cajero);
        NroDespacho         = view.findViewById(R.id.NroDespacho);
        TotalDespacho       = view.findViewById(R.id.TotalDespacho);
        DocAnulados         = view.findViewById(R.id.DocAnulados);
        TotalDocAnulados    = view.findViewById(R.id.TotalDocAnulados);
        TotalMontoPago      = view.findViewById(R.id.totalpago);
        totalPagoBruto      = view.findViewById(R.id.totalpagobruto);
        TotalCantidadProdTienda  = view.findViewById(R.id.textMtoProductoTienda);
        TotalMontoProdTienda     = view.findViewById(R.id.TotalSolesProductoTienda);

        TotalDescuento2     = view.findViewById(R.id.TotalDescuento2);
        TotalIncremento     = view.findViewById(R.id.TotalIncremento);

        GranTotal           = view.findViewById(R.id.GranTotal);
        GranEgresoTotal     = view.findViewById(R.id.GranEgresoTotal);

        imprimirCierreX     = view.findViewById(R.id.imprimircierrex);

        logoCierreX         = view.findViewById(R.id.LogoCierreX);

        reporteVendedor = view.findViewById(R.id.reporteVendedor);

        reporteVendedor.setVisibility(View.VISIBLE);
        if(!GlobalInfo.getsettingByImei10){
            reporteVendedor.setVisibility(View.GONE);
        }

        /** Mostrar Alerta - Sino tiene ninguna venta pendiente */
        modalAlerta = new Dialog(getContext());
        modalAlerta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlerta.setContentView(R.layout.cambioturno_inciodia_alerta);
        modalAlerta.setCancelable(true);

        imprimirCierreX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** API Retrofit - Cambio de Turno */
                findOptranTurno(GlobalInfo.getterminalImei10);

            }
        });


        /** Fecha de Impresión */
        Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());

        /**
         * @MOSTRAR:LogoEmpresa
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

        Uri imagenProd = Uri.parse("file://" + rutaImagen);
        logoCierreX.setImageURI(imagenProd);

        /** Datos de Cierre Parcial de Caja (X) */
        if(GlobalInfo.getTerminalNameCompany10){
            textNombreEmpresa.setText(GlobalInfo.getNameCompany10);
        }else {
            textNombreEmpresa.setVisibility(View.GONE);
        }

        String DirSucursal = "";
        if (GlobalInfo.getBranchCompany10 != null && !GlobalInfo.getBranchCompany10.isEmpty()) {
            DirSucursal = GlobalInfo.getBranchCompany10.replace("-", "");
            textSucural.setText("SUCURSAL: " + DirSucursal);
        } else {

            if (GlobalInfo.getAddressCompany10 != null && !GlobalInfo.getAddressCompany10.isEmpty()) {
                DirSucursal = GlobalInfo.getAddressCompany10.replace("-", "");
                textSucural.setText("PRINCIPAL: " + DirSucursal);
            }else {
                textSucural.setText("");
            }

        }
        FechaHoraIni.setText(GlobalInfo.getterminalFechaHoraCierre10);
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText(GlobalInfo.getterminalFecha10);
        Turno.setText(String.valueOf(GlobalInfo.getterminalTurno10));
        Cajero.setText(GlobalInfo.getuserName10);

        RContometrosTotalGLL = 0.00;
        RProductosCantidadTienda   = 0.00;
        RProductosTotalSolesTienda = 0.00;
        RProductosTotalDesc  = 0.00;
        RProductosTotalIncremento = 0.00;
        RPagosTotalSoles     = 0.00;
        RTarjetasTotal       = 0.00;
        RVendedorTotal       = 0.00;
        RProductosDescTienda = 0.00;
        RProductosIncrementoTienda = 0.00;
        REgresoTotal         = 0.00;

        MontoBrutoTienda = 0.00;
        montoBrutoTotal  = 0.00;

        /** Listado de R. Despacho */
        findRDespacho(GlobalInfo.getterminalID10, String.valueOf(GlobalInfo.getterminalTurno10), "B");

        /** Listado de R.Anulados */
        findRAnulados(GlobalInfo.getterminalID10, String.valueOf(GlobalInfo.getterminalTurno10), "A");

        /** Listado de Venta por Productos Tienda  */
        recyclerVProductosTienda  = view.findViewById(R.id.recyclerVProductosTienda);
        recyclerVProductosTienda.setLayoutManager(new LinearLayoutManager(getContext()));
        findVProductoTienda(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        /** Listado de Venta por Tipo de Pago  */
        recyclerVTipoPago = view.findViewById(R.id.recyclerVTipoPago);
        recyclerVTipoPago.setLayoutManager(new LinearLayoutManager(getContext()));
        findVTipoPago(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        /** Reporte por Tarjetas */
        recyclerReporteTarj = view.findViewById(R.id.recyclerReporteTarj);
        recyclerReporteTarj.setLayoutManager(new LinearLayoutManager(getContext()));
        findRTarjetas(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        /** Reporte por Egreso */
        recyclerReporteEgreso = view.findViewById(R.id.recyclerReporteEgreso);
        recyclerReporteEgreso.setLayoutManager(new LinearLayoutManager(getContext()));
        findREgreso(GlobalInfo.getterminalID10, GlobalInfo.getterminalTurno10);

        return view;
    }
    /** API SERVICE - R. Egreso */
    private void findREgreso(String id,Integer turno){

        Call<List<ReporteEgreso>> call = mAPIService.findReporteEgresos(id,turno);

        call.enqueue(new Callback<List<ReporteEgreso>>() {
            @Override
            public void onResponse(Call<List<ReporteEgreso>> call, Response<List<ReporteEgreso>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error R.egreso: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    reporteEgresoList = response.body();

                    for(ReporteEgreso reporteEgreso: reporteEgresoList) {
                        REgresoTotal += reporteEgreso.getMtoTotal();
                    }

                    TotalREgresoSoles = String.format(Locale.getDefault(), "%,.2f" ,REgresoTotal);
                    GlobalInfo.getTotalREgresoSoles10 = TotalREgresoSoles;
                    GranEgresoTotal.setText(TotalREgresoSoles);

                    reporteEgresoAdapter = new ReporteEgresoAdapter(reporteEgresoList, getContext());
                    recyclerReporteEgreso.setAdapter(reporteEgresoAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReporteEgreso>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE r.Egreso - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /** API SERVICE - R. Despacho */
    private void findRDespacho(String terminalId, String turno, String tipo){

        Call<List<RAnulados>> call = mAPIService.findRAnulados(terminalId,turno,tipo);

        call.enqueue(new Callback<List<RAnulados>>() {
            @Override
            public void onResponse(Call<List<RAnulados>> call, Response<List<RAnulados>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    rDescuentoList = response.body();

                    for(RAnulados rAnulados: rDescuentoList){
                        GlobalInfo.getrDespachosCantidad10 = rAnulados.getCantidad();
                        DespachosSoles10     = rAnulados.getSoles();
                    }

                    RDespachosSoles10 = String.format(Locale.getDefault(), "%,.2f", DespachosSoles10);
                    GlobalInfo.getrDespachosSoles10 = RDespachosSoles10;
                    TotalDespacho.setText(RDespachosSoles10);

                    NroDespacho.setText(String.valueOf(GlobalInfo.getrDespachosCantidad10));


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RAnulados>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE R Anulados - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /** API SERVICE - R. Anulados */
    private void findRAnulados(String terminalId, String turno, String tipo){

        Call<List<RAnulados>> call = mAPIService.findRAnulados(terminalId,turno,tipo);

        call.enqueue(new Callback<List<RAnulados>>() {
            @Override
            public void onResponse(Call<List<RAnulados>> call, Response<List<RAnulados>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    rAnuladosList = response.body();

                    for(RAnulados rAnulados: rAnuladosList){
                        GlobalInfo.getrAnuladosCantidad10 = rAnulados.getCantidad();
                        AnuladosSoles10    = rAnulados.getSoles();
                    }

                    RAnuladosSoles10 = String.format(Locale.getDefault(), "%,.2f", AnuladosSoles10);
                    GlobalInfo.getrAnuladosSoles10 = RAnuladosSoles10;
                    TotalDocAnulados.setText(RAnuladosSoles10);


                    DocAnulados.setText(String.valueOf(GlobalInfo.getrAnuladosCantidad10));

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RAnulados>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE R Anulados - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /** API SERVICE - Venta por Contrometro */
    private void findVProductoTienda(String id,Integer turno){

        Call<List<VProductoTienda>> call = mAPIService.findVProductoTienda(id,turno);

        call.enqueue(new Callback<List<VProductoTienda>>() {
            @Override
            public void onResponse(Call<List<VProductoTienda>> call, Response<List<VProductoTienda>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    vProductoTiendaList = response.body();

                    for(VProductoTienda vProductoTienda: vProductoTiendaList) {

                        RProductosCantidadTienda   += Double.valueOf(vProductoTienda.getCantidad());
                        RProductosTotalSolesTienda += Double.valueOf(vProductoTienda.getSoles());
                        RProductosDescTienda       += Double.valueOf(vProductoTienda.getDescuento());
                        RProductosIncrementoTienda += Double.valueOf(vProductoTienda.getIncremento());

                    }

                    /** Ventas Por Productos - Volumen */
                    SProductosTotalCantidadTienda = String.format(Locale.getDefault(), "%,.2f" ,RProductosCantidadTienda);
                    GlobalInfo.getTSProductosTotalCantidadTienda10 = SProductosTotalCantidadTienda;
                    TotalCantidadProdTienda.setText(SProductosTotalCantidadTienda);

                    /** Ventas Por Productos - Soles */
                    SProductosTotalSolesTienda = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalSolesTienda);
                    GlobalInfo.getTSProductosTotalSolesTienda10 = SProductosTotalSolesTienda;
                    TotalMontoProdTienda.setText(SProductosTotalSolesTienda);

                    /** Ventas Por Productos - Descuento */
                    SProductosTotalDescTienda = String.format(Locale.getDefault(), "%,.2f" ,RProductosDescTienda);
                    GlobalInfo.getTSProductosTotalDescTienda10 = SProductosTotalDescTienda;
                    TotalDescuento2.setText(SProductosTotalDescTienda);

                    /** Ventas Por Productos - Incremento */
                    SProductosTotalIncrementoTienda = String.format(Locale.getDefault(), "%,.2f" ,RProductosIncrementoTienda);
                    GlobalInfo.getTSProductosTotalIncrementoTienda10 = SProductosTotalIncrementoTienda;
                    TotalIncremento.setText(SProductosTotalIncrementoTienda);

                    /** Pago Bruto - Suma TotalPagosSoles,TotalDesc y TotalIncremento */
                    MontoBrutoTienda = RProductosTotalSolesTienda + RProductosDescTienda - RProductosIncrementoTienda;
                    calcularMontoBrutoTotal();

                    vProductoTiendaAdapter = new VProductoTiendaAdapter(vProductoTiendaList, getContext());
                    recyclerVProductosTienda.setAdapter(vProductoTiendaAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VProductoTienda>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void calcularMontoBrutoTotal() {
        montoBrutoTotal = MontoBrutoTienda;
        GlobalInfo.getMontoBruto10 = String.format(Locale.getDefault(), "%,.2f", montoBrutoTotal);
        totalPagoBruto.setText(GlobalInfo.getMontoBruto10);
    }

    /** API SERVICE - Venta por Tipo de Pago */
    private void findVTipoPago(String id,Integer turno){

        Call<List<VTipoPago>> call = mAPIService.findVTipoPago(id,turno);

        call.enqueue(new Callback<List<VTipoPago>>() {
            @Override
            public void onResponse(Call<List<VTipoPago>> call, Response<List<VTipoPago>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    vTipoPagoList = response.body();

                    for(VTipoPago vTipoPago: vTipoPagoList) {

                        RPagosTotalSoles += Double.valueOf(vTipoPago.getSoles());

                    }

                    /** Ventas Por Tipo de Pago - Total Neto */
                    TotalPagosSoles = String.format(Locale.getDefault(), "%,.2f" ,RPagosTotalSoles);
                    GlobalInfo.getTotalPagosSoles10 = TotalPagosSoles;
                    TotalMontoPago.setText(TotalPagosSoles);



                    vTipoPagoAdapter = new VTipoPagoAdapter(vTipoPagoList, getContext());
                    recyclerVTipoPago.setAdapter(vTipoPagoAdapter);


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VTipoPago>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /** API SERVICE - Venta por Tipo de Pago */
    private void findRTarjetas(String id,Integer turno){

        Call<List<ReporteTarjetas>> call = mAPIService.findRTarjetas(id,turno);

        call.enqueue(new Callback<List<ReporteTarjetas>>() {
            @Override
            public void onResponse(Call<List<ReporteTarjetas>> call, Response<List<ReporteTarjetas>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    reporteTarjetasList = response.body();

                    for(ReporteTarjetas reporteTarjetas: reporteTarjetasList) {

                        RTarjetasTotal += reporteTarjetas.getSoles();

                    }

                    /** Ventas Reporte Tarjetas- Gran Total */
                    TotalRTarjetasSoles = String.format(Locale.getDefault(), "%,.2f" ,RTarjetasTotal);
                    GlobalInfo.getTotalRTarjetasSoles10 = TotalRTarjetasSoles;
                    GranTotal.setText(TotalRTarjetasSoles);

                    reporteTarjetasAdapter = new ReporteTarjetasAdapter(reporteTarjetasList, getContext());
                    recyclerReporteTarj.setAdapter(reporteTarjetasAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReporteTarjetas>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();

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
                        return;
                    }

                    String tipoPapel = GlobalInfo.getTipoPapel10;

                    if (tipoPapel != null) {
                        cierrex(tipoPapel);
                    } else {
                        Toast.makeText(getContext(), "No se seleccionó ningún tipo de papel", Toast.LENGTH_SHORT).show();
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

    private void cierrex(String tipopapel) {

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

        String NameCompany   = GlobalInfo.getNameCompany10;

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

        String FechaHoraIni  = GlobalInfo.getterminalFechaHoraCierre10;

        /** Fecha de Impresión */
        Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());
        String FechaHoraFin          = FechaHoraImpresion;

        String FechaTrabajo     = GlobalInfo.getterminalFecha10;
        String Turno            = String.valueOf(GlobalInfo.getterminalTurno10);
        String Cajero           = GlobalInfo.getuserName10;
        String NroDespacho      = String.valueOf(GlobalInfo.getrDespachosCantidad10);
        String TotalDespacho    = String.valueOf(GlobalInfo.getrDespachosSoles10);
        String DocAnulados      = String.valueOf(GlobalInfo.getrAnuladosCantidad10);
        String TotalDocAnulados = String.valueOf(GlobalInfo.getrAnuladosSoles10);

        /**  Venta por Productos Tienda **/
        StringBuilder VProductoTiendaBuilder = new StringBuilder();

        for(VProductoTienda vProductoTienda: vProductoTiendaList) {
            String producto   = vProductoTienda.getArticuloDS();
            String cantidad   = String.format("%,10.2f",vProductoTienda.getCantidad());
            String soles      = String.format("%,10.2f", vProductoTienda.getSoles());

            switch (tipopapel) {
                case "65mm":
                case "80mm":
                    String line = String.format(Locale.getDefault(), "%-24s %4s %12s", producto, cantidad, soles);
                    VProductoTiendaBuilder.append(line).append("\n");
                    break;
                case "58mm":
                    String lineS = String.format(Locale.getDefault(), "%-10s %4s %10s", producto, cantidad, soles);
                    VProductoTiendaBuilder.append(lineS).append("\n");
                    break;
            }

        }

       /** Ventas por Tipo de Pago **/
        StringBuilder VTipoPagoBuilder = new StringBuilder();

        for(VTipoPago vTipoPago: vTipoPagoList) {
            String tipopago = vTipoPago.getNames();
            String soles    = String.format("%,10.2f",vTipoPago.getSoles());

            switch (tipopapel) {
                case "65mm":
                case "80mm":
                    String line = String.format(Locale.getDefault(), " %-36s %10s", tipopago, soles);
                    VTipoPagoBuilder.append(line).append("\n");
                    break;
                case "58mm":
                    String lineS = String.format(Locale.getDefault(), " %-20s %10s", tipopago, soles);
                    VTipoPagoBuilder.append(lineS).append("\n");
                    break;
            }
        }

        /**  Reporte por Tarjetas **/
        StringBuilder ReporteTarjetasBuilder = new StringBuilder();

        for(ReporteTarjetas reporteTarjetas: reporteTarjetasList) {
            String ndocumento = reporteTarjetas.getDocumento();
            String tipo       = reporteTarjetas.getTipo();
            String ref        = reporteTarjetas.getRef();
            String monto      = String.format("%,10.2f",reporteTarjetas.getSoles());

            switch (tipopapel) {
                case "65mm":
                case "80mm":
                    String line = String.format(Locale.getDefault(), "%-15s  %-8s %8s %12s", ndocumento, tipo,ref,monto);
                    ReporteTarjetasBuilder.append(line).append("\n");
                    break;
                case "58mm":
                    String Lline = String.format(Locale.getDefault(), "%-12s  %-6s %4s %12s", ndocumento, tipo,ref,monto);
                    ReporteTarjetasBuilder.append(Lline).append("\n");
                    break;
            }

        }


        /** Totales por ventas por PRODUCTOS*/
        StringBuilder TotalProTienda = new StringBuilder();

        String TotalProTiendaC      = "TOTALES :";
        String TSProductosTotalCantidadT   = GlobalInfo.getTSProductosTotalCantidadTienda10;
        String TSProductosTotalSolesT = GlobalInfo.getTSProductosTotalSolesTienda10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linest = String.format(Locale.getDefault(), "%-26s %8s %12s", TotalProTiendaC,TSProductosTotalCantidadT, TSProductosTotalSolesT);
                TotalProTienda.append(linest);
                break;
            case "58mm":
                String linesST = String.format(Locale.getDefault(), "%-14s %4s %10s", TotalProTiendaC,TSProductosTotalCantidadT, TSProductosTotalSolesT);
                TotalProTienda.append(linesST);
                break;
        }

        /** Monto NETO*/
        StringBuilder MontoNetoTotal = new StringBuilder();

        String MontoNetoTotalC  = "TOTAL NETO S/ : ";
        String TotalPagosSoles  = GlobalInfo.getTotalPagosSoles10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linessS = String.format(Locale.getDefault(), "%36s  %10s", MontoNetoTotalC, TotalPagosSoles);
                MontoNetoTotal.append(linessS);
                break;
            case "58mm":
                String linssS = String.format(Locale.getDefault(), "%20s  %10s", MontoNetoTotalC, TotalPagosSoles);
                MontoNetoTotal.append(linssS);
                break;
        }

        /** Total de Descuento*/
        StringBuilder DescuentosTotal = new StringBuilder();

        String DescuentosTotalC       = "Total Descuento";
        String TSProductosTotalDescs  = GlobalInfo.getTSProductosTotalDescTienda10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linees = String.format(Locale.getDefault(), "%-35s  %10s", DescuentosTotalC, TSProductosTotalDescs);
                DescuentosTotal.append(linees);
                break;
            case "58mm":
                String linNees = String.format(Locale.getDefault(), "%-20s  %10s", DescuentosTotalC, TSProductosTotalDescs);
                DescuentosTotal.append(linNees);
                break;
        }

        /** Total de Incremento*/
        StringBuilder IncrementoTotal = new StringBuilder();

        String IncrementoTotalC       = "Total Incremento";
        String TSProductosTotalIncrementos  = GlobalInfo.getTSProductosTotalIncrementoTienda10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linees = String.format(Locale.getDefault(), "%-35s  %10s", IncrementoTotalC, TSProductosTotalIncrementos);
                IncrementoTotal.append(linees);
                break;
            case "58mm":
                String linNees = String.format(Locale.getDefault(), "%-20s  %10s", IncrementoTotalC, TSProductosTotalIncrementos);
                IncrementoTotal.append(linNees);
                break;
        }

        /** Monto BRUTO */
        StringBuilder MontoBrutoTotal = new StringBuilder();

        String MontoBrutoTotalC  = "TOTAL BRUTO S/ :";
        String MontoBruto        = GlobalInfo.getMontoBruto10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linesz = String.format(Locale.getDefault(), "%36s  %10s", MontoBrutoTotalC, MontoBruto);
                MontoBrutoTotal.append(linesz);
                break;
            case "58mm":
                String lineSsz = String.format(Locale.getDefault(), "%20s  %10s", MontoBrutoTotalC, MontoBruto);
                MontoBrutoTotal.append(lineSsz);
                break;
        }

        /** Gran Total de Reporte por Tarjeta*/
        StringBuilder RTarjetaTotal = new StringBuilder();

        String RTarjetaTotalC      = "GRAN TOTAL :";
        String TotalRTarjetasSoles = GlobalInfo.getTotalRTarjetasSoles10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linesS = String.format(Locale.getDefault(), "%-36s  %10s", RTarjetaTotalC, TotalRTarjetasSoles);
                RTarjetaTotal.append(linesS);
                break;
            case "58mm":
                String lnesS = String.format(Locale.getDefault(), "%-20s  %10s", RTarjetaTotalC, TotalRTarjetasSoles);
                RTarjetaTotal.append(lnesS);
                break;
        }

        /**  Reporte por Egreso **/
        StringBuilder ReporteEgresoBuilder = new StringBuilder();

        for(ReporteEgreso reporteEgreso: reporteEgresoList) {
            String IDEgreso   = String.valueOf(reporteEgreso.getId());
            String tipoEgreso     = reporteEgreso.getEgresoDs();
            String solesEgreso    = String.format("%,10.2f",reporteEgreso.getMtoTotal());

            switch (tipopapel) {
                case "65mm":
                case "80mm":
                    String linneEgreso = String.format(Locale.getDefault(), "%-5s %27s %14s", IDEgreso,tipoEgreso,solesEgreso);
                    ReporteEgresoBuilder.append(linneEgreso).append("\n");
                    break;
                case "58mm":
                    String linnesEgreso = String.format(Locale.getDefault(), "%-3s %9s %8s",IDEgreso, tipoEgreso,solesEgreso);
                    ReporteEgresoBuilder.append(linnesEgreso).append("\n");
                    break;
            }
        }

        /** Gran Total de R Egreso */

        StringBuilder GranREgresoTotal = new StringBuilder();

        String GranREgresoTotalC   = "GRAN TOTAL :";
        String TotalSolesE           = GlobalInfo.getTotalREgresoSoles10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String linneesSEgreso = String.format(Locale.getDefault(),"%-36s %11s", GranREgresoTotalC, TotalSolesE);
                GranREgresoTotal.append(linneesSEgreso);
                break;
            case "58mm":
                String linneeSEgreso = String.format(Locale.getDefault(),"%-20s %11s", GranREgresoTotalC, TotalSolesE);
                GranREgresoTotal.append(linneeSEgreso);
                break;
        }

        int logoSize = (tipopapel.equals("80mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("65mm") ? GlobalInfo.getTerminalImageW10 : 400);

        /** Imprimir Cierre X**/

        Printama.with(getContext()).connect(printama -> {

            switch (tipopapel) {

                case "58mm":

                    printama.addNewLine();
                    printama.printImage(logoRobles, logoSize);
                    printama.addNewLine(GlobalInfo.getterminalFCabecera);
                    printama.setSmallText();
                    if(GlobalInfo.getTerminalNameCompany10){
                        printama.printTextlnBold(NameCompany, Printama.CENTER);
                    }else {
                        printama.addNewLine();
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

                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("CIERRE PARCIAL DE CAJA (X)",Printama.CENTER);
                    printama.addNewLine(1);
                    printama.printTextlnBold("Fecha/Hora Inicio : "+ FechaHoraIni,Printama.LEFT);
                    printama.printTextlnBold("Fecha/Hora Fin    : "+ FechaHoraFin , Printama.LEFT);
                    printama.printTextlnBold("Fecha Trabajo     : "+ FechaTrabajo, Printama.LEFT);
                    printama.printTextlnBold("Turno             : "+ Turno, Printama.LEFT);
                    printama.printTextlnBold("Cajero            : "+ Cajero, Printama.LEFT);
                    printama.printTextlnBold("Nro. Despachos    : "+ NroDespacho, Printama.LEFT);
                    printama.printTextlnBold("Total        (S/) : "+ TotalDespacho, Printama.LEFT);
                    printama.printTextlnBold("Doc. Anulados     : "+ DocAnulados, Printama.LEFT);
                    printama.printTextlnBold("Total Doc. Anulados (S/) : "+ TotalDocAnulados, Printama.LEFT);

                    if(GlobalInfo.getVentasProductosTienda10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("VENTAS OTROS PRODUCTOS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("PRODUCTO        " + "CANT.      " + "SOLES",Printama.RIGHT);
                        printama.printTextlnBold( VProductoTiendaBuilder.toString() + "-------" + "    " + "-------", Printama.RIGHT);
                        printama.printTextlnBold(TotalProTienda.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getVentasTipoPago10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("VENTAS POR TIPO DE PAGO",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold( VTipoPagoBuilder.toString(), Printama.RIGHT);
                        printama.printTextlnBold("Transferencia Gratuito    "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("Promociones               "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoNetoTotal.toString(),Printama.RIGHT);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold(DescuentosTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold(IncrementoTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoBrutoTotal.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getReporteTarjetas10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("REPORTE POR TARJETAS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("NRO DOCUMENTO "+"TIPO   "+"REF. "+" MONTO",Printama.RIGHT);
                        printama.printTextlnBold( ReporteTarjetasBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextlnBold(RTarjetaTotal.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getReporteEgreso10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("REPORTE DE EGRESOS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("ID   "+"TIPO DE EGRESOS      " + " MONTO", Printama.RIGHT);
                        printama.printTextlnBold(ReporteEgresoBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextlnBold(GranREgresoTotal.toString(), Printama.RIGHT);

                    }

                    break;

                case "80mm":

                    printama.addNewLine();
                    printama.printImage(logoRobles, logoSize);
                    printama.addNewLine(GlobalInfo.getterminalFCabecera);
                    printama.setSmallText();
                    if(GlobalInfo.getTerminalNameCompany10){
                        printama.printTextlnBold(NameCompany, Printama.CENTER);
                    }else {
                        printama.addNewLine();
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

                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("CIERRE PARCIAL DE CAJA (X)",Printama.CENTER);
                    printama.addNewLine(1);
                    printama.printTextlnBold("Fec/Hora I : "+ FechaHoraIni,Printama.LEFT);
                    printama.printTextlnBold("Fec/Hora F : "+ FechaHoraFin , Printama.LEFT);
                    printama.printTextlnBold("Fecha Trabajo     : "+ FechaTrabajo, Printama.LEFT);
                    printama.printTextlnBold("Turno             : "+ Turno, Printama.LEFT);
                    printama.printTextlnBold("Cajero            : "+ Cajero, Printama.LEFT);
                    printama.printTextlnBold("Nro. Despachos    : "+ NroDespacho, Printama.LEFT);
                    printama.printTextlnBold("Total        (S/) : "+ TotalDespacho, Printama.LEFT);
                    printama.printTextlnBold("Doc. Anulados     : "+ DocAnulados, Printama.LEFT);
                    printama.printTextlnBold("Total Doc. Anulados (S/) : "+ TotalDocAnulados, Printama.LEFT);

                    if(GlobalInfo.getVentasProductosTienda10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("VENTAS OTROS PRODUCTOS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("PRODUCTO                   "+"CANTIDAD        "+"SOLES",Printama.RIGHT);
                        printama.printTextlnBold( VProductoTiendaBuilder.toString() + "---------" + "    " + "---------", Printama.RIGHT);
                        printama.printTextlnBold(TotalProTienda.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getVentasTipoPago10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("VENTAS POR TIPO DE PAGO",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold( VTipoPagoBuilder.toString(), Printama.RIGHT);
                        printama.printTextlnBold("Transferencia Gratuito                   "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("Promociones                              "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoNetoTotal.toString(),Printama.RIGHT);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold(DescuentosTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold(IncrementoTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoBrutoTotal.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getReporteTarjetas10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("REPORTE POR TARJETAS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("NRO DOCUMENTO     "+"TIPO         "+"REF.      "+"  MONTO",Printama.RIGHT);
                        printama.printTextlnBold( ReporteTarjetasBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextlnBold(RTarjetaTotal.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getReporteEgreso10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("REPORTE DE EGRESOS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("ID              "+"TIPO DE EGRESOS          " + "  MONTO", Printama.RIGHT);
                        printama.printTextlnBold(ReporteEgresoBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextlnBold(GranREgresoTotal.toString(), Printama.RIGHT);

                    }

                    break;

                case "65mm":

                    printama.printImage(Printama.RIGHT,logoRobles, logoSize);
                    printama.addNewLine(GlobalInfo.getterminalFCabecera);
                    printama.setSmallText();
                    if(GlobalInfo.getTerminalNameCompany10){
                        printama.printTextlnBold(NameCompany, Printama.CENTER);
                    }else {
                        printama.addNewLine();
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

                    printama.setSmallText();
                    printSeparatorLine(printama, tipopapel);
                    printama.addNewLine(1);
                    printama.setSmallText();
                    printama.printTextlnBold("CIERRE PARCIAL DE CAJA (X)",Printama.CENTER);
                    printama.addNewLine(1);
                    printama.printTextln("Fecha/Hora Inicio : "+ FechaHoraIni,Printama.LEFT);
                    printama.printTextln("Fecha/Hora Fin    : "+ FechaHoraFin , Printama.LEFT);
                    printama.printTextln("Fecha Trabajo     : "+ FechaTrabajo, Printama.LEFT);
                    printama.printTextln("Turno             : "+ Turno, Printama.LEFT);
                    printama.printTextln("Cajero            : "+ Cajero, Printama.LEFT);
                    printama.printTextln("Nro. Despachos    : "+ NroDespacho, Printama.LEFT);
                    printama.printTextln("Total        (S/) : "+ TotalDespacho, Printama.LEFT);
                    printama.printTextln("Doc. Anulados     : "+ DocAnulados, Printama.LEFT);
                    printama.printTextln("Total Doc. Anulados (S/) : "+ TotalDocAnulados, Printama.LEFT);

                    if(GlobalInfo.getVentasProductosTienda10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("VENTAS OTROS PRODUCTOS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("PRODUCTO                   "+"CANTIDAD        "+"SOLES",Printama.RIGHT);
                        printama.printTextln( VProductoTiendaBuilder.toString() + "---------" + "    " + "---------", Printama.RIGHT);
                        printama.printTextln(TotalProTienda.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getVentasTipoPago10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("VENTAS POR TIPO DE PAGO",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextln( VTipoPagoBuilder.toString(), Printama.RIGHT);
                        printama.printTextln("Transferencia Gratuito                   "+"  0.00",Printama.RIGHT);
                        printama.printTextln("Promociones                              "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextln(MontoNetoTotal.toString(),Printama.RIGHT);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextln(DescuentosTotal.toString(),Printama.RIGHT);
                        printama.printTextln(IncrementoTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextln(MontoBrutoTotal.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getReporteTarjetas10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("REPORTE POR TARJETAS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold("NRO DOCUMENTO     "+"TIPO         "+"REF.      "+"  MONTO",Printama.RIGHT);
                        printama.printTextln( ReporteTarjetasBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextln(RTarjetaTotal.toString(),Printama.RIGHT);

                    }

                    if(GlobalInfo.getReporteEgreso10) {

                        printama.setSmallText();
                        printSeparatorLine(printama, tipopapel);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold("REPORTE DE EGRESOS",Printama.CENTER);
                        printama.addNewLine(1);
                        printama.printTextlnBold( "ID              "+"TIPO DE EGRESOS         " + "  MONTO", Printama.RIGHT);
                        printama.printTextln(ReporteEgresoBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextln(GranREgresoTotal.toString(), Printama.RIGHT);

                    }

                    break;

            }
            printama.feedPaper();
            printama.close();
            printama.cutPaper();

        }, this::showToast);

    }

    private void printSeparatorLine(Printama printama, String tipopapel) {
        if ("80mm".equals(tipopapel) || "65mm".equals(tipopapel)) {
            printama.printDoubleDashedLine();
        } else if ("58mm".equals(tipopapel)) {
            printama.printDoubleDashedLines();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

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