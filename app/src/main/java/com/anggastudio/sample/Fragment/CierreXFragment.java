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
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class CierreXFragment extends Fragment {

    TextView TotalDocAnulados,DocAnulados,NroDespacho,Cajero,Turno,FechaTrabajo,
            FechaHoraFin,FechaHoraIni,TotalVolumenContometro,textSucural,textNombreEmpresa,
            TotalSolesproducto,TotalMontoPago,TotalMtogalones,TotalDescuento,totalpagobruto;

    RecyclerView recyclerVProducto,recyclerVTipoPago,recyclerVContometro,recyclerReporteTarj;

    ReporteTarjetasAdapter reporteTarjetasAdapter;
    List<ReporteTarjetas> reporteTarjetasList;

    VContometroAdapter vContometroAdapter;
    List<VContometro> vContometroList;

    VProductoAdapter vProductoAdapter;
    List<VProducto> vProductoList;

    VTipoPagoAdapter vTipoPagoAdapter;
    List<VTipoPago> vTipoPagoList;

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

        view.findViewById(R.id.imprimircierrex).setOnClickListener(v -> cierrex());

        /** Fecha de Impresión */
        Calendar calendarprint       = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
        SimpleDateFormat formatdate  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String FechaHoraImpresion    = formatdate.format(calendarprint.getTime());

        textNombreEmpresa.setText("SERVICENTRO ROBLES E.I.R.L.");
        textSucural.setText("SUCURSAL: " + "AV. CORONEL PARRA 1239 PILCOMAYO-HUANCAYO-JUNIN");
        FechaHoraIni.setText("12/04/2023");
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText("12/04/2023");
        Turno.setText("01");
        Cajero.setText("Manuel Porras");
        NroDespacho.setText("0");
        DocAnulados.setText("0");
        TotalDocAnulados.setText("0.00");
        TotalVolumenContometro.setText("0.00");

        /** Listado de Venta por Contometros  */
        recyclerVContometro = view.findViewById(R.id.recyclerVContometro);
        recyclerVContometro.setLayoutManager(new LinearLayoutManager(getContext()));

        vContometroList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            vContometroList.add(new VContometro("01","",452.00,47.512,487.256));
            vContometroList.add(new VContometro("02","",452.00,47.512,487.256));
            vContometroList.add(new VContometro("03","",452.00,47.512,487.256));
            vContometroList.add(new VContometro("04","",452.00,47.512,487.256));
            vContometroList.add(new VContometro("05","",452.00,47.512,487.256));
            vContometroList.add(new VContometro("01","",452.00,47.512,487.256));
            vContometroList.add(new VContometro("02","",452.00,47.512,487.256));
        }

        vContometroAdapter = new VContometroAdapter(vContometroList, getContext());

        recyclerVContometro.setAdapter(vContometroAdapter);

        /** Listado de Venta por Productos  */
        recyclerVProducto = view.findViewById(R.id.recyclerVProductos);
        recyclerVProducto.setLayoutManager(new LinearLayoutManager(getContext()));

        vProductoList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            vProductoList.add(new VProducto("GLP",145.12,158.41,0.00));
            vProductoList.add(new VProducto("GLP",145.12,158.41,0.00));
            vProductoList.add(new VProducto("GLP",145.12,158.41,0.00));
            vProductoList.add(new VProducto("GLP",145.12,158.41,0.00));
            vProductoList.add(new VProducto("GLP",145.12,158.41,0.00));
        }

        vProductoAdapter = new VProductoAdapter(vProductoList, getContext());

        recyclerVProducto.setAdapter(vProductoAdapter);

        /** Listado de Venta por Tipo de Pago  */
        recyclerVTipoPago = view.findViewById(R.id.recyclerVTipoPago);
        recyclerVTipoPago.setLayoutManager(new LinearLayoutManager(getContext()));

        vTipoPagoList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            vTipoPagoList.add(new VTipoPago("EFECTIVO",452.00));
            vTipoPagoList.add(new VTipoPago("VISA",10.00));
        }

        vTipoPagoAdapter = new VTipoPagoAdapter(vTipoPagoList, getContext());

        recyclerVTipoPago.setAdapter(vTipoPagoAdapter);

        /** Reporte por Tarjetas */
        recyclerReporteTarj = view.findViewById(R.id.recyclerReporteTarj);
        recyclerReporteTarj.setLayoutManager(new LinearLayoutManager(getContext()));

        reporteTarjetasList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            reporteTarjetasList.add(new ReporteTarjetas("1245","B001-000254","VISA",25.00));
            reporteTarjetasList.add(new ReporteTarjetas("7845","B008-000478","VISA",30.00));
        }

        reporteTarjetasAdapter = new ReporteTarjetasAdapter(reporteTarjetasList, getContext());

        recyclerReporteTarj.setAdapter(reporteTarjetasAdapter);

        return view;
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