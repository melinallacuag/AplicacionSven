package com.anggastudio.sample.Fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anggastudio.sample.Adapter.LadosAdapter;
import com.anggastudio.sample.Adapter.ManguerasAdapter;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;

import java.util.ArrayList;
import java.util.List;


public class VentaFragment extends Fragment{

    RecyclerView recyclerLados,recyclerMangueras;

    List<Lados> ladosList;
    LadosAdapter ladosAdapter;

    List<Mangueras> manguerasList;
    ManguerasAdapter manguerasAdapter;

    TextView  terminalID;
    Button    btnlibre,btnsoles,btngalones,btnboleta,btnfactura,btnnotadespacho,btnserafin,btnpuntos,automatiStop,imprimirserafin;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_venta, container, false);

        terminalID      = view.findViewById(R.id.terminalID);
        automatiStop    = view.findViewById(R.id.automatiStop);
        btnlibre        = view.findViewById(R.id.btnlibre);
        btnsoles        = view.findViewById(R.id.btnsoles);
        btngalones      = view.findViewById(R.id.btngalones);
        btnboleta       = view.findViewById(R.id.btnboleta);
        btnfactura      = view.findViewById(R.id.btnfactura);
        btnnotadespacho = view.findViewById(R.id.btnnotadespacho);
        btnserafin      = view.findViewById(R.id.btnserafin);
        btnpuntos       = view.findViewById(R.id.btnpuntos);

        /** Listado de Card - Lado */
        recyclerLados = view.findViewById(R.id.recyclerLado);
        recyclerLados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ladosList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            ladosList.add(new Lados("01"));
            ladosList.add(new Lados("02"));
            ladosList.add(new Lados("03"));
            ladosList.add(new Lados("04"));
            ladosList.add(new Lados("05"));
        }

        ladosAdapter = new LadosAdapter(ladosList, getContext(), new LadosAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(Lados item) {
                return 0;
            }
        });

        recyclerLados.setAdapter(ladosAdapter);

        /** Listado de Card - Manguera */
        recyclerMangueras = view.findViewById(R.id.recyclerMangueras);
        recyclerMangueras.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        manguerasList = new ArrayList<>();

        for (int i = 0; i < 1; i++){
            manguerasList.add(new Mangueras("G-REGULAR","01"));
            manguerasList.add(new Mangueras("G-PREMIUM","02"));
            manguerasList.add(new Mangueras("GLP","03"));
            manguerasList.add(new Mangueras("DIESEL","04"));
        }

        manguerasAdapter = new ManguerasAdapter(manguerasList, getContext(), new ManguerasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Mangueras item) {

            }
        });

        recyclerMangueras.setAdapter(manguerasAdapter);

        /** Modal Libre */
        btnlibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibreFragment libreFragment = new LibreFragment();
                libreFragment.show(getActivity().getSupportFragmentManager(), "Libre");
                libreFragment.setCancelable(false);
            }
        });


        btnsoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SolesFragment solesFragment = new SolesFragment();
                solesFragment.show(getActivity().getSupportFragmentManager(), "Soles");
                solesFragment.setCancelable(false);
            }
        });

        btngalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalonesFragment galonesFragment = new GalonesFragment();
                galonesFragment.show(getActivity().getSupportFragmentManager(), "Galones");
                galonesFragment.setCancelable(false);
            }
        });


        return view;
    }


}