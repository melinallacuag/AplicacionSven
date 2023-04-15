package com.anggastudio.sample.Fragment;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.anggastudio.sample.Login;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DasboardFragment extends Fragment{

    TextView nombre_grifero,fecha_inicio_grifero,turno_grifero,nombre_empresa,sucursal_empresa,slogan_empresa;
    CardView btn_Venta,btn_Cierrex,btn_Cambioturno,btn_Iniciodia,btn_Salir;
    Button btnCancelarTurno,btnCancelarInicio,btnAceptarTurno,btnAceptarInicio,btncancelarsalida,btnsalir;
    Dialog modalCambioTurno,modalInicioDia,modalAlerta,modalSalir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dasboard, container, false);

        nombre_grifero        = view.findViewById(R.id.nombre_grifero);
        fecha_inicio_grifero  = view.findViewById(R.id.fecha_inicio);
        turno_grifero         = view.findViewById(R.id.turno_grifero);

        nombre_empresa    = view.findViewById(R.id.nombre_empresa);
        sucursal_empresa  = view.findViewById(R.id.sucursal_empresa);
        slogan_empresa    = view.findViewById(R.id.slogan_empresa);

        btn_Venta         = view.findViewById(R.id.btnVenta);
        btn_Cierrex       = view.findViewById(R.id.btnCierreX);
        btn_Cambioturno   = view.findViewById(R.id.btnCambioTurno);
        btn_Iniciodia     = view.findViewById(R.id.btnInicioDia);
        btn_Salir         = view.findViewById(R.id.btnSalir);

        nombre_grifero.setText(GlobalInfo.getuserName10);
        fecha_inicio_grifero.setText("FECHA : " + GlobalInfo.getterminalFecha10);
        turno_grifero.setText("TURNO : " + String.valueOf(GlobalInfo.getterminalTurno10));

        String DirSucursal = GlobalInfo.getBranchCompany10;

        DirSucursal = DirSucursal.replace("-","");

        nombre_empresa.setText(GlobalInfo.getNameCompany10);
        sucursal_empresa.setText(DirSucursal);
        slogan_empresa.setText(GlobalInfo.getSloganCompany10);

        btn_Venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManagerVenta = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionVenta = fragmentManagerVenta.beginTransaction();
                int fragmentContainerVenta = R.id.fragment_container;
                VentaFragment ventaFragment = new VentaFragment();

                fragmentTransactionVenta.replace(fragmentContainerVenta, ventaFragment);
                fragmentTransactionVenta.addToBackStack(null);
                fragmentTransactionVenta.commit();

            }
        });

        btn_Cierrex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManagerCierreX = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionCierreX = fragmentManagerCierreX.beginTransaction();
                int fragmentContainerCierreX = R.id.fragment_container;
                CierreXFragment cierreXFragment = new CierreXFragment();

                fragmentTransactionCierreX.replace(fragmentContainerCierreX, cierreXFragment);
                fragmentTransactionCierreX.addToBackStack(null);
                fragmentTransactionCierreX.commit();

            }
        });

        modalAlerta = new Dialog(getContext());
        modalAlerta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlerta.setContentView(R.layout.cambioturno_inciodia_alerta);
        modalAlerta.setCancelable(true);

        modalCambioTurno = new Dialog(getContext());
        modalCambioTurno.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalCambioTurno.setContentView(R.layout.fragment_cambio_turno);
        modalCambioTurno.setCancelable(false);

        btn_Cambioturno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalCambioTurno.show();

                btnCancelarTurno    = modalCambioTurno.findViewById(R.id.btncancelarcambioturno);
                btnAceptarTurno     = modalCambioTurno.findViewById(R.id.btnagregarcambioturno);

                btnCancelarTurno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalCambioTurno.dismiss();
                    }
                });

                btnAceptarTurno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modalAlerta.show();

                        try {
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finalize();
                            Toast.makeText(getContext(), "SE GENERO EL CAMBIO DE TURNO ", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        modalInicioDia = new Dialog(getContext());
        modalInicioDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalInicioDia.setContentView(R.layout.fragment_inicio_dia);
        modalInicioDia.setCancelable(false);

        btn_Iniciodia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalInicioDia.show();

                btnCancelarInicio    = modalInicioDia.findViewById(R.id.btncancelariniciodia);
                btnAceptarInicio     = modalInicioDia.findViewById(R.id.btnagregariniciodia);

                btnCancelarInicio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalInicioDia.dismiss();
                    }
                });

                btnAceptarInicio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        modalAlerta.show();

                        try {
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finalize();
                            Toast.makeText(getContext(), "SE GENERO EL INICIO DE DÍA", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });

              }
        });

        modalSalir = new Dialog(getContext());
        modalSalir.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalSalir.setContentView(R.layout.fragment_salir);
        modalSalir.setCancelable(false);

        btn_Salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalSalir.show();

                btncancelarsalida    = modalSalir.findViewById(R.id.btncancelarsalida);
                btnsalir     = modalSalir.findViewById(R.id.btnsalir);

                btncancelarsalida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalSalir.dismiss();
                    }
                });

                btnsalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finalize();
                            Toast.makeText(getContext(), "SE CERRO SESIÓN", Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        return view;

    }

}