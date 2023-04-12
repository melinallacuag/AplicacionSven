package com.anggastudio.sample.Fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.anggastudio.sample.R;


public class LibreFragment extends DialogFragment {


    Button btnactivarLibre,btndesactivar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_libre, container, false);

        btnactivarLibre  = view.findViewById(R.id.btnlibresi);
        btndesactivar    = view.findViewById(R.id.btnlibreno);


        btndesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnactivarLibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Toast.makeText(getContext(), "SE ACTIVO EL MODO LIBRE", Toast.LENGTH_SHORT).show();
                    dismiss();


            }
        });

        return view;
    }

}