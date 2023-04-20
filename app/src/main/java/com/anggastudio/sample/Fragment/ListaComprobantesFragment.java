package com.anggastudio.sample.Fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ListaComprobanteAdapter;
import com.anggastudio.sample.Numero_Letras;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaComprobantesFragment extends Fragment  {

    private APIService mAPIService;

    RecyclerView recyclerLComprobante ;
    ListaComprobanteAdapter listaComprobanteAdapter;
    List<ListaComprobante> listaComprobanteList;

    Dialog modalReimpresion;
    Button btnCancelarRImpresion,btnRImpresion,btnAnular;
    TextView campo_correlativo;

    SearchView BuscarRazonSocial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_comprobantes, container, false);

        mAPIService  = GlobalInfo.getAPIService();

        BuscarRazonSocial   = view.findViewById(R.id.BuscarRazonSocial);

        /** Listado de Comprobantes  */
        recyclerLComprobante = view.findViewById(R.id.recyclerListaComprobante);
        recyclerLComprobante.setLayoutManager(new LinearLayoutManager(getContext()));

        /** Mostrar Modal de Cambio de Turno */
        modalReimpresion = new Dialog(getContext());
        modalReimpresion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalReimpresion.setContentView(R.layout.modal_reimprimir);
        modalReimpresion.setCancelable(false);

        findConsultarVenta(GlobalInfo.getterminalID10);

        /** Buscador por Razon Social */
        BuscarRazonSocial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (listaComprobanteList.isEmpty()) {

                    Toast.makeText(getContext(), "No se encontró el dato", Toast.LENGTH_SHORT).show();

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaComprobanteAdapter.filtrado(newText);
                return false;
            }
        });

        return view;
    }
    /** API SERVICE - Card Consultar Venta */
    private void findConsultarVenta(String id){

        Call<List<ListaComprobante>> call = mAPIService.findConsultarVenta(id);

        call.enqueue(new Callback<List<ListaComprobante>>() {
            @Override
            public void onResponse(Call<List<ListaComprobante>> call, Response<List<ListaComprobante>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GlobalInfo.getlistacomprobanteList10 = response.body();

                    ListaComprobante();


                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ListaComprobante>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Consulta Venta - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void ListaComprobante(){

        listaComprobanteAdapter = new ListaComprobanteAdapter(GlobalInfo.getlistacomprobanteList10, getContext(), new ListaComprobanteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListaComprobante item) {

                moveToDescription(item);

                modalReimpresion.show();

                final FragmentManager fragmentManager = getFragmentManager();

                btnCancelarRImpresion    = modalReimpresion.findViewById(R.id.btnCancelarRImpresion);
                btnRImpresion       = modalReimpresion.findViewById(R.id.btnRImpresion);
                btnAnular           = modalReimpresion.findViewById(R.id.btnAnular);
                campo_correlativo   = modalReimpresion.findViewById(R.id.campo_correlativo);

                campo_correlativo.setText("NroDocumento: " + "B0001" + "-" + "0001542");

                btnCancelarRImpresion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalReimpresion.dismiss();
                    }
                });

                btnRImpresion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Reimpresion(GlobalInfo.getconsultaventaTipoDocumentoID10,GlobalInfo.getconsultaventaSerieDocumento10 ,GlobalInfo.getconsultaventaNroDocumento10);

                    }
                });

                btnAnular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (GlobalInfo.getconsultaventaAnulado10.equals("NO")) {

                            Anular(GlobalInfo.getconsultaventaTipoDocumentoID10,GlobalInfo.getconsultaventaSerieDocumento10 ,GlobalInfo.getconsultaventaNroDocumento10,GlobalInfo.getuserID10);

                            fragmentManager.popBackStack();

                            modalReimpresion.dismiss();

                        } else {
                            Toast.makeText(getContext(), "Documento se encuntra anulado", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        recyclerLComprobante.setAdapter(listaComprobanteAdapter);
    }

    public void moveToDescription(ListaComprobante item) {

        GlobalInfo.getconsultaventaTipoDocumentoID10  = item.getTipoDocumento();
        GlobalInfo.getconsultaventaSerieDocumento10   = item.getSerieDocumento();
        GlobalInfo.getconsultaventaNroDocumento10     = item.getNroDocumento();
        GlobalInfo.getconsultaventaAnulado10          = item.getAnulado();

    }

    private void Anular(String tipodoc, String seriedoc, String nrodoc, String anuladoid) {

        Call<Anular> call = mAPIService.postAnular(tipodoc,seriedoc,nrodoc,anuladoid);

        call.enqueue(new Callback<Anular>() {
            @Override
            public void onResponse(Call<Anular> call, Response<Anular> response) {

                if(!response.isSuccessful()){

                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;

                }

            }

            @Override
            public void onFailure(Call<Anular> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Anular", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Reimpresion(String tipodoc, String seriedoc, String nrodoc) {

        Call<List<Reimpresion>> call = mAPIService.findReimpresion(tipodoc, seriedoc, nrodoc);

        call.enqueue(new Callback<List<Reimpresion>>() {
            @Override
            public void onResponse(Call<List<Reimpresion>> call, Response<List<Reimpresion>> response) {

                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    modalReimpresion.dismiss();

                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Reimpresion>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE Reimpresion - RED - WIFI", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
