package com.anggastudio.sample.Fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.ReporteTarjetasAdapter;
import com.anggastudio.sample.Adapter.ReporteVendedorAdapter;
import com.anggastudio.sample.Adapter.VContometroAdapter;
import com.anggastudio.sample.Adapter.VProductoAdapter;
import com.anggastudio.sample.Adapter.VTipoPagoAdapter;
import com.anggastudio.sample.NFCUtil;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.RAnulados;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteVendedor;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
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

    TextView TotalDocAnulados,DocAnulados,NroDespacho,TotalDespacho,Cajero,Turno,FechaTrabajo,
            FechaHoraFin,FechaHoraIni,textSucural,textNombreEmpresa,
            TotalSolesproducto,TotalMontoPago,TotalMtogalones,TotalDescuento,totalPagoBruto,
            TotalDescuento2,TotalIncremento,GranTotal;

    String RAnuladosSoles10,RDespachosSoles10,SProductosTotalGLL,SProductosTotalSoles,SProductosTotalDesc,
            TotalPagosSoles,MontoBruto,TotalRTarjetasSoles;

    Button imprimirCierreX;
    Dialog modalAlerta;

    RecyclerView recyclerVProducto,recyclerVTipoPago,recyclerReporteTarj;

    ReporteTarjetasAdapter reporteTarjetasAdapter;
    List<ReporteTarjetas> reporteTarjetasList;

    VProductoAdapter vProductoAdapter;
    List<VProducto> vProductoList;

    VTipoPagoAdapter vTipoPagoAdapter;
    List<VTipoPago> vTipoPagoList;

    List<RAnulados> rAnuladosList;
    List<RAnulados> rDescuentoList;

    ImageView logoCierreX;

    Double AnuladosSoles10,DespachosSoles10, RContometrosTotalGLL, RProductosTotalGLL, RProductosTotalSoles, RProductosTotalDesc, RPagosTotalSoles,RTarjetasTotal,RVendedorTotal;

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
        TotalMtogalones     = view.findViewById(R.id.textMtogalones);
        TotalSolesproducto  = view.findViewById(R.id.TotalSolesproducto);
        TotalDescuento      = view.findViewById(R.id.TotalDescuento);
        TotalMontoPago      = view.findViewById(R.id.totalpago);
        totalPagoBruto      = view.findViewById(R.id.totalpagobruto);

        TotalDescuento2     = view.findViewById(R.id.TotalDescuento2);
        TotalIncremento     = view.findViewById(R.id.TotalIncremento);

        GranTotal           = view.findViewById(R.id.GranTotal);

        imprimirCierreX     = view.findViewById(R.id.imprimircierrex);

        logoCierreX         = view.findViewById(R.id.LogoCierreX);

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
        String rutaImagen="/storage/emulated/0/appSven/" + GlobalInfo.getsettingRutaLogo210;
        File file = new File(rutaImagen);

        if(!file.exists()){
            rutaImagen = "/storage/emulated/0/appSven/logo.png";
        }
        Uri logoUri = Uri.parse("file://" + rutaImagen);
        logoCierreX.setImageURI(logoUri);

        /** Datos de Cierre Parcial de Caja (X) */
        if(GlobalInfo.getTerminalNameCompany10){
            textNombreEmpresa.setText(GlobalInfo.getNameCompany10);
        }else {
            textNombreEmpresa.setVisibility(View.GONE);
        }
        textNombreEmpresa.setVisibility(View.GONE);
        textSucural.setText("SUCURSAL: " + GlobalInfo.getBranchCompany10);
        FechaHoraIni.setText(GlobalInfo.getterminalFechaHoraCierre10);
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText(GlobalInfo.getterminalFecha10);
        Turno.setText(String.valueOf(GlobalInfo.getterminalTurno10));
        Cajero.setText(GlobalInfo.getuserName10);

        RContometrosTotalGLL = 0.00;
        RProductosTotalGLL   = 0.00;
        RProductosTotalSoles = 0.00;
        RProductosTotalDesc  = 0.00;
        RPagosTotalSoles     = 0.00;
        RTarjetasTotal       = 0.00;
        RVendedorTotal       = 0.00;

        /** Listado de R. Despacho */
        findRDespacho(GlobalInfo.getterminalID10, String.valueOf(GlobalInfo.getterminalTurno10), "B");

        /** Listado de R.Anulados */
        findRAnulados(GlobalInfo.getterminalID10, String.valueOf(GlobalInfo.getterminalTurno10), "A");

        /** Listado de Venta por Productos  */
        recyclerVProducto = view.findViewById(R.id.recyclerVProductos);
        recyclerVProducto.setLayoutManager(new LinearLayoutManager(getContext()));
        findVProducto(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        /** Listado de Venta por Tipo de Pago  */
        recyclerVTipoPago = view.findViewById(R.id.recyclerVTipoPago);
        recyclerVTipoPago.setLayoutManager(new LinearLayoutManager(getContext()));
        findVTipoPago(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        /** Reporte por Tarjetas */
        recyclerReporteTarj = view.findViewById(R.id.recyclerReporteTarj);
        recyclerReporteTarj.setLayoutManager(new LinearLayoutManager(getContext()));
        findRTarjetas(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        return view;
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
    private void findVProducto(String id,Integer turno){

        Call<List<VProducto>> call = mAPIService.findVProducto(id,turno);

        call.enqueue(new Callback<List<VProducto>>() {
            @Override
            public void onResponse(Call<List<VProducto>> call, Response<List<VProducto>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    vProductoList = response.body();

                    for(VProducto vProducto: vProductoList) {

                        RProductosTotalGLL += Double.valueOf(vProducto.getCantidad());
                        RProductosTotalSoles += Double.valueOf(vProducto.getSoles());
                        RProductosTotalDesc += Double.valueOf(vProducto.getDescuento());

                    }

                    /** Ventas Por Productos - Volumen */
                    SProductosTotalGLL = String.format(Locale.getDefault(), "%,.3f" ,RProductosTotalGLL);
                    GlobalInfo.getTSProductosTotalGLL10 = SProductosTotalGLL;
                    TotalMtogalones.setText(SProductosTotalGLL);

                    /** Ventas Por Productos - Soles */
                    SProductosTotalSoles = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalSoles);
                    GlobalInfo.getTSProductosTotalSoles10 = SProductosTotalSoles;
                    TotalSolesproducto.setText(SProductosTotalSoles);

                    /** Ventas Por Productos - Descuento */
                    SProductosTotalDesc = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalDesc);
                    GlobalInfo.getTSProductosTotalDesc10 = SProductosTotalDesc;
                    TotalDescuento.setText(SProductosTotalDesc);

                    /** Descuntos y Incrementos */
                    TotalDescuento2.setText(SProductosTotalDesc);
                    TotalIncremento.setText("0.00");

                    /** Pago Bruto - Suma TotalPagosSoles,TotalDesc y TotalIncremento */
                    MontoBruto = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalSoles + RProductosTotalDesc);
                    GlobalInfo.getMontoBruto10 = MontoBruto;
                    totalPagoBruto.setText(MontoBruto);

                    vProductoAdapter = new VProductoAdapter(vProductoList, getContext());
                    recyclerVProducto.setAdapter(vProductoAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VProducto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();

            }
        });

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

        //Bitmap logoRobles = BitmapFactory.decodeResource(getResources(), R.drawable.logoprincipal);

        String rutaImagen="/storage/emulated/0/appSven/" + GlobalInfo.getsettingRutaLogo210;
        File file = new File(rutaImagen);
        if(!file.exists()){
            rutaImagen = "/storage/emulated/0/appSven/sinfoto.jpg";
        }
        Bitmap logoRobles = BitmapFactory.decodeFile(rutaImagen);

        String NameCompany   = GlobalInfo.getNameCompany10;

        String BranchCompany = (GlobalInfo.getBranchCompany10 != null) ? GlobalInfo.getBranchCompany10 : "";
        String[] partesBranch = BranchCompany.split(" - " , 2);
        String Branch1 = partesBranch[0];
        String Branch2 = partesBranch[1];

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

        /**  Venta por Productos **/
        StringBuilder VProductoBuilder = new StringBuilder();

        for(VProducto vProducto: vProductoList) {
            String producto   = vProducto.getArticuloDS();
            String volumen    = String.format("%,10.3f",vProducto.getCantidad());
            String soles      = String.format("%,10.2f", vProducto.getSoles());
            String descuentos = String.format("%,10.2f", vProducto.getDescuento());

            switch (tipopapel) {
                case "65mm":
                case "80mm":
                    String line = String.format(Locale.getDefault(), "%-10s %-10s %12s %12s", producto, volumen, soles, descuentos);
                    VProductoBuilder.append(line).append("\n");
                    break;
                case "58mm":
                    String lineS = String.format(Locale.getDefault(), "%-10s %-8s %10s %10s", producto, volumen, soles, descuentos);
                    VProductoBuilder.append(lineS).append("\n");
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


        /** Totales por ventas por prodcutos*/
        StringBuilder TotalVolumenPro = new StringBuilder();

        String TotalVolumenProC      = "TOTALES :";
        String TSProductosTotalGLL   = GlobalInfo.getTSProductosTotalGLL10;
        String TSProductosTotalSoles = GlobalInfo.getTSProductosTotalSoles10;
        String TSProductosTotalDesc  = GlobalInfo.getTSProductosTotalDesc10;

        switch (tipopapel) {
            case "65mm":
            case "80mm":
                String lines = String.format(Locale.getDefault(), "%-10s %10s %12s %12s", TotalVolumenProC, TSProductosTotalGLL,TSProductosTotalSoles,TSProductosTotalDesc);
                TotalVolumenPro.append(lines);
                break;
            case "58mm":
                String linesS = String.format(Locale.getDefault(), "%-10s %10s %10s %10s", TotalVolumenProC, TSProductosTotalGLL,TSProductosTotalSoles,TSProductosTotalDesc);
                TotalVolumenPro.append(linesS);
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
        String TSProductosTotalDescs  = GlobalInfo.getTSProductosTotalDesc10;

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

        int logoSize = (tipopapel.equals("80mm")) ? GlobalInfo.getTerminalImageW10 : (tipopapel.equals("65mm") ? GlobalInfo.getTerminalImageW10 : 400);

        /** Imprimir Cierre X**/
        Printama.with(getContext()).connect(printama -> {
            switch (tipopapel) {
                case "58mm":
                case "80mm":
                    printama.addNewLine();
                    printama.printImage(logoRobles, logoSize);
                    break;
                case "65mm":
                    printama.printImage(Printama.CENTER,logoRobles, logoSize);
                    break;
            }
            printama.setSmallText();
            if(GlobalInfo.getTerminalNameCompany10){
                printama.printTextlnBold(NameCompany, Printama.CENTER);
            }else {
                printama.printTextlnBold(" ");
            }
            printama.printTextlnBold("SUCURSAL: " + Branch1, Printama.CENTER);
            printama.printTextlnBold(Branch2, Printama.CENTER);
            printama.setSmallText();
            printSeparatorLine(printama, tipopapel);
            printama.addNewLine(1);
            printama.setSmallText();
            printama.printTextlnBold("CIERRE PARCIAL DE CAJA (X)",Printama.CENTER);
            printama.addNewLine(1);
            switch (tipopapel) {
                case "65mm":
                case "80mm":
                    printama.printTextlnBold("Fecha/Hora Inicio : "+ FechaHoraIni,Printama.LEFT);
                    printama.printTextlnBold("Fecha/Hora Fin    : "+ FechaHoraFin , Printama.LEFT);
                    break;
                case "58mm":
                    printama.printTextlnBold("Fec/Hora I : "+ FechaHoraIni,Printama.LEFT);
                    printama.printTextlnBold("Fec/Hora F : "+ FechaHoraFin , Printama.LEFT);
                    break;
            }
            printama.printTextlnBold("Fecha Trabajo     : "+ FechaTrabajo, Printama.LEFT);
            printama.printTextlnBold("Turno             : "+ Turno, Printama.LEFT);
            printama.printTextlnBold("Cajero            : "+ Cajero, Printama.LEFT);
            printama.printTextlnBold("Nro. Despachos    : "+ NroDespacho, Printama.LEFT);
            printama.printTextlnBold("Total        (S/) : "+ TotalDespacho, Printama.LEFT);
            printama.printTextlnBold("Doc. Anulados     : "+ DocAnulados, Printama.LEFT);
            printama.printTextlnBold("Total Doc. Anulados (S/) : "+ TotalDocAnulados, Printama.LEFT);

            if(GlobalInfo.getVentasProductos10) {
                printama.setSmallText();
                printSeparatorLine(printama, tipopapel);
                printama.addNewLine(1);
                printama.setSmallText();
                printama.printTextlnBold("VENTAS POR PRODUCTOS",Printama.CENTER);
                printama.addNewLine(1);
                switch (tipopapel) {
                    case "65mm":
                    case "80mm":
                        printama.printTextlnBold("PRODUCTO      "+"VOLUMEN        "+"SOLES   "+" DESCUENTO",Printama.RIGHT);
                        printama.printTextlnBold( VProductoBuilder.toString() + "---------" + "    " + "---------" + "    " + "---------", Printama.RIGHT);
                        printama.printTextlnBold(TotalVolumenPro.toString(),Printama.RIGHT);
                        break;
                    case "58mm":
                        printama.printTextlnBold("PRODUCTO        "+"VOL.    "+"S/  "+"DTO.",Printama.RIGHT);
                        printama.printTextlnBold( VProductoBuilder.toString()  + "---------" + "  " + "---------", Printama.RIGHT);
                        printama.printTextlnBold(TotalVolumenPro.toString(),Printama.RIGHT);
                        break;
                }
            }

            if(GlobalInfo.getVentasTipoPago10) {
                printama.setSmallText();
                printSeparatorLine(printama, tipopapel);
                printama.addNewLine(1);
                printama.setSmallText();
                printama.printTextlnBold("VENTAS POR TIPO DE PAGO",Printama.CENTER);
                printama.addNewLine(1);
                switch (tipopapel) {
                    case "65mm":
                    case "80mm":
                        printama.printTextlnBold( VTipoPagoBuilder.toString(), Printama.RIGHT);
                        printama.printTextlnBold("Transferencia Gratuito                   "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("Promociones                              "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoNetoTotal.toString(),Printama.RIGHT);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold(DescuentosTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold("Total Incremento                         "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoBrutoTotal.toString(),Printama.RIGHT);
                        break;
                    case "58mm":
                        printama.printTextlnBold( VTipoPagoBuilder.toString(), Printama.RIGHT);
                        printama.printTextlnBold("Transferencia Gratuito    "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("Promociones               "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoNetoTotal.toString(),Printama.RIGHT);
                        printama.addNewLine(1);
                        printama.setSmallText();
                        printama.printTextlnBold(DescuentosTotal.toString(),Printama.RIGHT);
                        printama.printTextlnBold("Total Incremento          "+"  0.00",Printama.RIGHT);
                        printama.printTextlnBold("---------",Printama.RIGHT);
                        printama.printTextlnBold(MontoBrutoTotal.toString(),Printama.RIGHT);
                        break;
                }
            }

            if(GlobalInfo.getReporteTarjetas10) {
                printama.setSmallText();
                printSeparatorLine(printama, tipopapel);
                printama.addNewLine(1);
                printama.setSmallText();
                printama.printTextlnBold("REPORTE POR TARJETAS",Printama.CENTER);
                printama.addNewLine(1);
                switch (tipopapel) {
                    case "65mm":
                    case "80mm":
                        printama.printTextlnBold("NRO DOCUMENTO     "+"TIPO         "+"REF.      "+"  MONTO",Printama.RIGHT);
                        printama.printTextlnBold( ReporteTarjetasBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextlnBold(RTarjetaTotal.toString(),Printama.RIGHT);
                        break;
                    case "58mm":
                        printama.printTextlnBold("NRO DOCUMENTO "+"TIPO   "+"REF. "+" MONTO",Printama.RIGHT);
                        printama.printTextlnBold( ReporteTarjetasBuilder.toString() + "---------", Printama.RIGHT);
                        printama.printTextlnBold(RTarjetaTotal.toString(),Printama.RIGHT);
                        break;
                }
            }

            printama.addNewLine(1);
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