package com.anggastudio.sample.Fragment;

import static android.text.TextUtils.isEmpty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.anggastudio.sample.R;
import com.google.android.material.textfield.TextInputLayout;


public class SolesFragment extends DialogFragment {

    Button btncancelar, agregar;
    EditText montosoles;
    TextInputLayout alertsoles;
    TextView textsol;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_soles, container, false);

        btncancelar   = view.findViewById(R.id.btncancelarsoles);
        agregar       = view.findViewById(R.id.btnagregarsoles);
        montosoles    = view.findViewById(R.id.inputmontosoles);
        alertsoles    = view.findViewById(R.id.alertsoles);

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { dismiss();}
        });

        agregar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    String MontoSoles = montosoles.getText().toString();
                    if(isEmpty(MontoSoles)){
                        alertsoles.setError("El campo soles es obligatorio");
                        return;
                    }

                    Double DoubleMontoSoles = Double.parseDouble(MontoSoles);

                    Integer NumIntSoles     = Integer.parseInt(MontoSoles);

                    if (NumIntSoles < 5){
                        alertsoles.setError("El valor debe ser mínimo 5.00");
                    }else if(9999 < NumIntSoles){
                        alertsoles.setError("El valor debe ser maximo 9999");
                    }else {
                        alertsoles.setErrorEnabled(false);
                        Toast.makeText(getContext(), "SE AGREGO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }

        });

        return view;
    }


}