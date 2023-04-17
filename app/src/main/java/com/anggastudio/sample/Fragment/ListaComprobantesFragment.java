package com.anggastudio.sample.Fragment;

import android.app.Dialog;
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

import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ListaComprobanteAdapter;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

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

        ListaComprobante();

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

    public void ListaComprobante(){

        listaComprobanteAdapter = new ListaComprobanteAdapter(GlobalInfo.getlistacomprobanteList10, getContext(), new ListaComprobanteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListaComprobante item) {

                modalReimpresion.show();

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
                        modalReimpresion.dismiss();
                        Toast.makeText(getContext(), "Se Reimprimio", Toast.LENGTH_SHORT).show();

                    }
                });

                btnAnular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalReimpresion.dismiss();
                        Toast.makeText(getContext(), "Documento se encuntra anulado", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        recyclerLComprobante.setAdapter(listaComprobanteAdapter);
    }



}
