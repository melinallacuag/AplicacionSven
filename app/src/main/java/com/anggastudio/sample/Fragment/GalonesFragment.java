package com.anggastudio.sample.Fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.anggastudio.sample.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class GalonesFragment extends DialogFragment {

    Button btncancelar,agregargalones;
    TextInputEditText galones;
    TextInputLayout alertgalones;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_galones, container, false);


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

                if (galones == null){
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

                        Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }


            }
        });
        return view;
    }


}