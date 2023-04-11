package com.anggastudio.sample.Fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Picos;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LibreFragment extends DialogFragment {

    private APIService mAPIService;

    Button btnactivarLibre,btndesactivar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_libre, container, false);

        btnactivarLibre  = view.findViewById(R.id.btnlibresi);
        btndesactivar    = view.findViewById(R.id.btnlibreno);

        mAPIService = GlobalInfo.getAPIService();

        btndesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnactivarLibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GlobalInfo.getPistola10 == null){
                    Toast.makeText(getContext(), "Seleccionar Cara y Manguera", Toast.LENGTH_SHORT).show();
                }else{
                    guardar_modolibre(GlobalInfo.getPistola10);
                    Toast.makeText(getContext(), "SE ACTIVO EL MODO LIBRE", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

            }
        });

        return view;
    }

    /** Guardar - MODO LIBRE */
    private void guardar_modolibre(String manguera){

        final Picos picos = new Picos(manguera,"01","1","05","DB5","G",999.00);

        Call<Picos> call = mAPIService.postPicos(picos);

        call.enqueue(new Callback<Picos>() {
            @Override
            public void onResponse(Call<Picos> call, Response<Picos> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<Picos> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión APICORE - Modo Libre", Toast.LENGTH_SHORT).show();
            }
        });

    }
}