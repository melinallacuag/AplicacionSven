package com.anggastudio.sample.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ReporteTarjetasAdapter;
import com.anggastudio.sample.Adapter.VContometroAdapter;
import com.anggastudio.sample.Adapter.VProductoAdapter;
import com.anggastudio.sample.Adapter.VTipoPagoAdapter;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CierreXFragment extends Fragment {

    private APIService mAPIService;

    TextView TotalDocAnulados,DocAnulados,NroDespacho,Cajero,Turno,FechaTrabajo,
            FechaHoraFin,FechaHoraIni,TotalVolumenContometro,textSucural,textNombreEmpresa,
            TotalSolesproducto,TotalMontoPago,TotalMtogalones,TotalDescuento,totalPagoBruto,
            TotalDescuento2,TotalIncremento,GranTotal;

    String TVolumenContometro,SProductosTotalGLL,SProductosTotalSoles,SProductosTotalDesc,
            TotalPagosSoles,MontoBruto,TotalRTarjetasSoles;

    RecyclerView recyclerVProducto,recyclerVTipoPago,recyclerVContometro,recyclerReporteTarj;

    ReporteTarjetasAdapter reporteTarjetasAdapter;
    List<ReporteTarjetas> reporteTarjetasList;

    VContometroAdapter vContometroAdapter;
    List<VContometro> vContometroList;

    VProductoAdapter vProductoAdapter;
    List<VProducto> vProductoList;

    VTipoPagoAdapter vTipoPagoAdapter;
    List<VTipoPago> vTipoPagoList;

    Double RContometrosTotalGLL, RProductosTotalGLL, RProductosTotalSoles, RProductosTotalDesc, RPagosTotalSoles,RTarjetasTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cierre_x, container, false);

        mAPIService = GlobalInfo.getAPIService();

        textNombreEmpresa   = view.findViewById(R.id.textNombreEmpresa);
        textSucural         = view.findViewById(R.id.textSucural);
        FechaHoraIni        = view.findViewById(R.id.FechaHoraIni);
        FechaHoraFin        = view.findViewById(R.id.FechaHoraFin);
        FechaTrabajo        = view.findViewById(R.id.FechaTrabajo);
        Turno               = view.findViewById(R.id.Turno);
        Cajero              = view.findViewById(R.id.Cajero);
        NroDespacho         = view.findViewById(R.id.NroDespacho);
        DocAnulados         = view.findViewById(R.id.DocAnulados);
        TotalDocAnulados    = view.findViewById(R.id.TotalDocAnulados);
        TotalVolumenContometro = view.findViewById(R.id.TotalVolumenContometro);
        TotalMtogalones     = view.findViewById(R.id.textMtogalones);
        TotalSolesproducto  = view.findViewById(R.id.TotalSolesproducto);
        TotalDescuento      = view.findViewById(R.id.TotalDescuento);
        TotalMontoPago      = view.findViewById(R.id.totalpago);
        totalPagoBruto      = view.findViewById(R.id.totalpagobruto);

        TotalDescuento2     = view.findViewById(R.id.TotalDescuento2);
        TotalIncremento     = view.findViewById(R.id.TotalIncremento);

        GranTotal           = view.findViewById(R.id.GranTotal);

        view.findViewById(R.id.imprimircierrex).setOnClickListener(v -> cierrex());

        /** Fecha de Impresión */
        Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());

        /** Datos de Cierre Parcial de Caja (X) */
        textNombreEmpresa.setText(GlobalInfo.getNameCompany10);
        textSucural.setText("SUCURSAL: " + GlobalInfo.getBranchCompany10);
        FechaHoraIni.setText(GlobalInfo.getterminalFechaHoraCierre10);
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText(GlobalInfo.getterminalFecha10);
        Turno.setText(String.valueOf(GlobalInfo.getterminalTurno10));
        Cajero.setText(GlobalInfo.getuserName10);
        NroDespacho.setText("0");
        DocAnulados.setText("0");
        TotalDocAnulados.setText("0.00");

        RContometrosTotalGLL = 0.00;
        RProductosTotalGLL   = 0.00;
        RProductosTotalSoles = 0.00;
        RProductosTotalDesc  = 0.00;
        RPagosTotalSoles     = 0.00;
        RTarjetasTotal       = 0.00;

        /** Listado de Venta por Contometros  */
        recyclerVContometro = view.findViewById(R.id.recyclerVContometro);
        recyclerVContometro.setLayoutManager(new LinearLayoutManager(getContext()));
        findVContometro(GlobalInfo.getterminalID10);

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
    /** API SERVICE - Venta por Contrometro */
    private void findVContometro(String id){

        Call<List<VContometro>> call = mAPIService.findVContometro(id);

        call.enqueue(new Callback<List<VContometro>>() {
            @Override
            public void onResponse(Call<List<VContometro>> call, Response<List<VContometro>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    vContometroList = response.body();

                    for(VContometro vContometro: vContometroList) {

                        RContometrosTotalGLL += Double.valueOf(vContometro.getGalones());
                    }

                    /** Ventas Por Contometro - Volumen */
                    TVolumenContometro = String.format(Locale.getDefault(), "%,.3f" ,RContometrosTotalGLL);
                    TotalVolumenContometro.setText(TVolumenContometro);

                    vContometroAdapter = new VContometroAdapter(vContometroList, getContext());
                    recyclerVContometro.setAdapter(vContometroAdapter);

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VContometro>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Optran - RED - WIFI", Toast.LENGTH_SHORT).show();

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
                    TotalMtogalones.setText(SProductosTotalGLL);

                    /** Ventas Por Productos - Soles */
                    SProductosTotalSoles = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalSoles);
                    TotalSolesproducto.setText(SProductosTotalSoles);

                    /** Ventas Por Productos - Descuento */
                    SProductosTotalDesc = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalDesc);
                    TotalDescuento.setText(SProductosTotalDesc);

                    /** Descuntos y Incrementos */
                    TotalDescuento2.setText(SProductosTotalDesc);
                    TotalIncremento.setText("0.00");

                    /** Pago Bruto - Suma TotalPagosSoles,TotalDesc y TotalIncremento */
                    MontoBruto = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalSoles + RProductosTotalDesc);
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

                        RTarjetasTotal += Double.valueOf(reporteTarjetas.getSoles());

                    }

                    /** Ventas Reporte Tarjetas- Gran Total */
                    TotalRTarjetasSoles = String.format(Locale.getDefault(), "%,.2f" ,RTarjetasTotal);
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

    private void cierrex() {

        Bitmap logoRobles = BitmapFactory.decodeResource(getResources(), R.drawable.logoprincipal);
        View view = getView().findViewById(R.id.contenedorCierreX);
        Printama.with(getContext()).connect(printama -> {
            printama.printImage(logoRobles, 100);
            printama.printFromView(view);
            new Handler().postDelayed(printama::close, 2000);
        }, this::showToast);

    }

    private void showToast(String message) {
        Toast.makeText(getContext(), "Conectar Bluetooth", Toast.LENGTH_SHORT).show();
    }

}