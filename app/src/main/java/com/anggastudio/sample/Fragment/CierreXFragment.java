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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.DetalleVentaAdapter;
import com.anggastudio.sample.Adapter.VContometroAdapter;
import com.anggastudio.sample.Adapter.VProductoAdapter;
import com.anggastudio.sample.Adapter.VTipoPagoAdapter;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.Setting;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import java.text.DecimalFormat;
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

    TextView TotalImprsionX,TotalDocAnulados,DocAnulados,Total,IGV,SubTotal,NroDespacho,Cajero,Turno,FechaTrabajo,
            FechaHoraFin,FechaHoraIni,TotalVolumenContometro,textSucural,textNombreEmpresa,
            TotalSolesproducto,TotalMontoPago,TotalMtogalones,TotalDescuento,totalpagobruto;

    VContometroAdapter vContometroAdapter;
    RecyclerView recyclerVContometro;
    List<VContometro> vContometroList;

    VProductoAdapter vProductoAdapter;
    RecyclerView recyclerVProducto;
    List<VProducto> vProductoList;

    VTipoPagoAdapter vTipoPagoAdapter;
    RecyclerView recyclerVTipoPago;
    List<VTipoPago> vTipoPagoList;

    Double RContometrosTotalGLL, RProductosTotalGLL, RProductosTotalSoles, RProductosTotalDesc, RPagosTotalSoles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cierre_x, container, false);

        mAPIService  = GlobalInfo.getAPIService();

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
        totalpagobruto      = view.findViewById(R.id.totalpagobruto);


        /** Fecha de Impresión */
        Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());

        textNombreEmpresa.setText(GlobalInfo.getNameCompany10);
        textSucural.setText("SUCURSAL: " + GlobalInfo.getBranchCompany10);
        FechaHoraIni.setText(GlobalInfo.getterminalFecha10);
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText(GlobalInfo.getterminalFecha10);
        Turno.setText(GlobalInfo.getterminalTurno10.toString());
        Cajero.setText(GlobalInfo.getuserName10);
        NroDespacho.setText("0");
        DocAnulados.setText("0");
        TotalDocAnulados.setText("0.00");


        view.findViewById(R.id.imprimircierrex).setOnClickListener(v -> cierrex());

        /** Listado de Venta por Contometros  */
        recyclerVContometro = view.findViewById(R.id.recyclerVContometro);
        recyclerVContometro.setLayoutManager(new LinearLayoutManager(getContext()));

        /** Listado de Venta por Productos  */
        recyclerVProducto = view.findViewById(R.id.recyclerVProductos);
        recyclerVProducto.setLayoutManager(new LinearLayoutManager(getContext()));

        /** Listado de Venta por Tipo de Pago  */
        recyclerVTipoPago = view.findViewById(R.id.recyclerVTipoPago);
        recyclerVTipoPago.setLayoutManager(new LinearLayoutManager(getContext()));

        RContometrosTotalGLL = 0.00;
        RProductosTotalGLL = 0.00;
        RProductosTotalSoles = 0.00;
        RProductosTotalDesc = 0.00;
        RPagosTotalSoles = 0.00;

        /** API Retrofit - Consumiendo */
        findVContometro(GlobalInfo.getterminalID10);
        findVProducto(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);
        findVTipoPago(GlobalInfo.getterminalID10,GlobalInfo.getterminalTurno10);

        return view;
    }
    @Override
    public void onPause() {
        super.onPause();

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

                    String TotalVolumen = String.format(Locale.getDefault(), "%,.3f" ,RContometrosTotalGLL);

                    TotalVolumenContometro.setText(TotalVolumen);

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

                    List<VProducto> vProductoList = response.body();

                    for(VProducto vProducto: vProductoList) {

                        RProductosTotalGLL += Double.valueOf(vProducto.getCantidad());
                        RProductosTotalSoles += Double.valueOf(vProducto.getSoles());
                        RProductosTotalDesc += Double.valueOf(vProducto.getDescuento());

                    }

                    String SProductosTotalGLL = String.format(Locale.getDefault(), "%,.3f" ,RProductosTotalGLL);

                    TotalMtogalones.setText(SProductosTotalGLL);

                    String SProductosTotalSoles = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalSoles);

                    TotalSolesproducto.setText(SProductosTotalSoles);

                    String SProductosTotalDesc = String.format(Locale.getDefault(), "%,.2f" ,RProductosTotalDesc);

                    TotalDescuento.setText(SProductosTotalDesc);

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

                    List<VTipoPago> vTipoPagoList = response.body();

                    for(VTipoPago vTipoPago: vTipoPagoList) {

                        RPagosTotalSoles += Double.valueOf(vTipoPago.getSoles());

                    }

                    String TotalPagosSoles = String.format(Locale.getDefault(), "%,.2f" ,RPagosTotalSoles);

                    TotalMontoPago.setText(TotalPagosSoles);

                    totalpagobruto.setText(TotalPagosSoles);

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

    private void cierrex() {

        Bitmap logoRobles = BitmapFactory.decodeResource(getResources(), R.drawable.logoprincipal);
        View view = getView().findViewById(R.id.linearLayout2);
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