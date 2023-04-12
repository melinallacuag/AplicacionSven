package com.anggastudio.sample.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anggastudio.sample.Adapter.VContometroAdapter;
import com.anggastudio.sample.Adapter.VProductoAdapter;
import com.anggastudio.sample.Adapter.VTipoPagoAdapter;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class CierreXFragment extends Fragment {


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

        textNombreEmpresa.setText("");
        textSucural.setText("SUCURSAL: " );
        FechaHoraIni.setText("");
        FechaHoraFin.setText(FechaHoraImpresion);
        FechaTrabajo.setText("");
        Turno.setText("");
        Cajero.setText("");
        NroDespacho.setText("0");
        DocAnulados.setText("0");
        TotalDocAnulados.setText("0.00");


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


        return view;
    }
    @Override
    public void onPause() {
        super.onPause();

    }


}