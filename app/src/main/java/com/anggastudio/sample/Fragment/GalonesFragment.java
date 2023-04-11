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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GalonesFragment extends DialogFragment {

    Button btncancelar,agregargalones;
    TextInputEditText galones;
    TextInputLayout alertgalones;

    private APIService mAPIService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_galones, container, false);

        mAPIService = GlobalInfo.getAPIService();

        btncancelar    = view.findViewById(R.id.btncancelargalones);
        agregargalones = view.findViewById(R.id.btnagregargalones);
        galones        = view.findViewById(R.id.inputmontogalones);
        alertgalones   = view.findViewById(R.id.alertgalones);

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        agregargalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GlobalInfo.getPistola10 == null){
                    Toast.makeText(getContext(), "Seleccionar Cara y Manguera", Toast.LENGTH_SHORT).show();
                }else{
                    String textgalones = galones.getText().toString().trim();

                    if(textgalones.isEmpty()){
                        alertgalones.setError("El campo galones es obligatorio");
                        return;
                    }
                    Double galonesmonto  = Double.parseDouble(textgalones);

                    Integer numgalones   = Integer.parseInt(textgalones);

                    if (numgalones < 1){
                        alertgalones.setError("El valor debe ser minimo 1 ");
                    }else if(999 < numgalones){
                        alertgalones.setError("El valor debe ser maximo 999");
                    }else {
                        alertgalones.setErrorEnabled(false);
                        guardar_galones(GlobalInfo.getPistola10,galonesmonto);

                        Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }


            }
        });
        return view;
    }

    /** Guardar - GALONES */
    private void guardar_galones(String manguera, Double valor){

        final Picos picos = new Picos(manguera,"01","1","05","DB5","G",valor);

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
                Toast.makeText(getContext(), "Error de conexión APICORE", Toast.LENGTH_SHORT).show();
            }
        });

    }
}