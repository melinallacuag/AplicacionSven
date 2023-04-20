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

    TextView TotalDocAnulados,DocAnulados,NroDespacho,Cajero,Turno,FechaTrabajo,
            FechaHoraFin,FechaHoraIni,TotalVolumenContometro,textSucural,textNombreEmpresa,
            TotalSolesproducto,TotalMontoPago,TotalMtogalones,TotalDescuento,totalpagobruto;

    RecyclerView recyclerVProducto,recyclerVTipoPago,recyclerVContometro,recyclerReporteTarj;

    ReporteTarjetasAdapter reporteTarjetasAdapter;
    List<ReporteTarjetas> reporteTarjetasList;

    VContometroAdapter vContometroAdapter;

    VProductoAdapter vProductoAdapter;

    VTipoPagoAdapter vTipoPagoAdapter;

    Double RContometrosTotalGLL, RProductosTotalGLL, RProductosTotalSoles, RProductosTotalDesc, RPagosTotalSoles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cierre_x, container, false);

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

        RContometrosTotalGLL = 0.00;
        RProductosTotalGLL   = 0.00;
        RProductosTotalSoles = 0.00;
        RProductosTotalDesc  = 0.00;
        RPagosTotalSoles     = 0.00;

        view.findViewById(R.id.imprimircierrex).setOnClickListener(v -> cierrex());

        /** Fecha de Impresión */
        Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());

        /** Datos de Cierre Parcial de Caja (X) */
        textNombreEmpresa.setText(GlobalInfo.getNameCompany10);
        textSucural.setText("SUCURSAL: " + GlobalInfo.getBranchCompany10);
        FechaHoraIni.setText(GlobalInfo.getterminalFecha10);
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText(GlobalInfo.getterminalFecha10);
        Turno.setText(String.valueOf(GlobalInfo.getterminalTurno10));
        Cajero.setText(GlobalInfo.getuserName10);
        NroDespacho.setText("0");
        DocAnulados.setText("0");
        TotalDocAnulados.setText("0.00");

        /** Listado de Venta por Contometros  */
        recyclerVContometro = view.findViewById(R.id.recyclerVContometro);
        recyclerVContometro.setLayoutManager(new LinearLayoutManager(getContext()));
        vContometro();

        /** Listado de Venta por Productos  */
        recyclerVProducto = view.findViewById(R.id.recyclerVProductos);
        recyclerVProducto.setLayoutManager(new LinearLayoutManager(getContext()));
        vProducto();

        /** Listado de Venta por Tipo de Pago  */
        recyclerVTipoPago = view.findViewById(R.id.recyclerVTipoPago);
        recyclerVTipoPago.setLayoutManager(new LinearLayoutManager(getContext()));
        vTipoPago();

        /** Reporte por Tarjetas */
        recyclerReporteTarj = view.findViewById(R.id.recyclerReporteTarj);
        recyclerReporteTarj.setLayoutManager(new LinearLayoutManager(getContext()));
        vReporteTarjeta();

        return view;
    }

    private void vContometro(){

        for(VContometro vContometro: GlobalInfo.getvContometroList10) {

            RContometrosTotalGLL += Double.valueOf(vContometro.getGalones());
        }

        String TVolumenContometro = String.format(Locale.getDefault(), "%,.3f" ,RContometrosTotalGLL);

        TotalVolumenContometro.setText(TVolumenContometro);

        vContometroAdapter = new VContometroAdapter(GlobalInfo.getvContometroList10, getContext());

        recyclerVContometro.setAdapter(vContometroAdapter);
    }

    private void vProducto(){

        for(VProducto vProducto: GlobalInfo.getvProductoList10) {

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

        vProductoAdapter = new VProductoAdapter(GlobalInfo.getvProductoList10, getContext());

        recyclerVProducto.setAdapter(vProductoAdapter);
    }

    private void vTipoPago(){

        for(VTipoPago vTipoPago: GlobalInfo.getvTipoPagoList10) {

            RPagosTotalSoles += Double.valueOf(vTipoPago.getSoles());

        }

        String TotalPagosSoles = String.format(Locale.getDefault(), "%,.2f" ,RPagosTotalSoles);

        TotalMontoPago.setText(TotalPagosSoles);

        totalpagobruto.setText(TotalPagosSoles);

        vTipoPagoAdapter = new VTipoPagoAdapter(GlobalInfo.getvTipoPagoList10, getContext());

        recyclerVTipoPago.setAdapter(vTipoPagoAdapter);
    }

    private void vReporteTarjeta(){

        reporteTarjetasList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            reporteTarjetasList.add(new ReporteTarjetas("1245","B001-000254","VISA",25.00));
            reporteTarjetasList.add(new ReporteTarjetas("7845","B008-000478","VISA",30.00));
        }

        reporteTarjetasAdapter = new ReporteTarjetasAdapter(reporteTarjetasList, getContext());

        recyclerReporteTarj.setAdapter(reporteTarjetasAdapter);

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