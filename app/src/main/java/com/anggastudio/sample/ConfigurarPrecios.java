package com.anggastudio.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anggastudio.sample.Adapter.CLadosAdapter;
import com.anggastudio.sample.Adapter.PreciosAdapter;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.CambioPrecios;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurarPrecios extends AppCompatActivity {

    private APIService mAPIService;
    Button btnActualizarPrecio,btnCancelarCambioPrecio,btnAceptarCambioPrecio;
    Dialog modalAlertaCambioPrecio;
    TextInputEditText inputPrecioRegular,inputPrecioPremium,inputPrecioGlp,inputPrecioDiesel,inputPreciourea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_precios);
        mAPIService = GlobalInfo.getAPIService();

        btnActualizarPrecio  = findViewById(R.id.btnActualizarPrecio);
        inputPrecioRegular   = findViewById(R.id.inputPrecioRegular);
        inputPrecioPremium   = findViewById(R.id.inputPrecioPremium);
        inputPrecioGlp       = findViewById(R.id.inputPrecioGlp);
        inputPrecioDiesel    = findViewById(R.id.inputPrecioDiesel);
        inputPreciourea      = findViewById(R.id.inputPreciourea);

        modalAlertaCambioPrecio = new Dialog(this);
        modalAlertaCambioPrecio.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modalAlertaCambioPrecio.setContentView(R.layout.alerta_cambio_precio);
        modalAlertaCambioPrecio.setCancelable(false);

        obtenerPrecios();

        btnActualizarPrecio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                modalAlertaCambioPrecio.show();

                btnCancelarCambioPrecio= modalAlertaCambioPrecio.findViewById(R.id.btnCancelarCambioPrecio);
                btnAceptarCambioPrecio  = modalAlertaCambioPrecio.findViewById(R.id.btnAceptarCambioPrecio);


                btnCancelarCambioPrecio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        modalAlertaCambioPrecio.dismiss();

                    }
                });

                btnAceptarCambioPrecio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        double regular = Double.parseDouble(inputPrecioRegular.getText().toString());
                        double premium = Double.parseDouble(inputPrecioPremium.getText().toString());
                        double glp = Double.parseDouble(inputPrecioGlp.getText().toString());
                        double diesel = Double.parseDouble(inputPrecioDiesel.getText().toString());
                        double urea = Double.parseDouble(inputPreciourea.getText().toString());
                        guardarPrecios(regular, premium, glp, diesel, urea);
                        modalAlertaCambioPrecio.dismiss();
                    }
                });

            }
        });
    }
    private void obtenerPrecios() {
        Call<List<CambioPrecios>> call = mAPIService.findConfirmacionPrecio();
        call.enqueue(new Callback<List<CambioPrecios>>() {
            @Override
            public void onResponse(Call<List<CambioPrecios>> call, Response<List<CambioPrecios>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    CambioPrecios precios = response.body().get(0);

                    inputPrecioRegular.setText(String.valueOf(precios.getRegular()));
                    inputPrecioPremium.setText(String.valueOf(precios.getPremium()));
                    inputPrecioGlp.setText(String.valueOf(precios.getGlp()));
                    inputPrecioDiesel.setText(String.valueOf(precios.getDiesel()));
                    inputPreciourea.setText(String.valueOf(precios.getUrea()));
                } else {
                    Toast.makeText(ConfigurarPrecios.this, "Error al obtener los precios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CambioPrecios>> call, Throwable t) {
                Toast.makeText(ConfigurarPrecios.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarPrecios(Double regular, Double premium, Double glp, Double diesel, Double urea) {
        CambioPrecios precios = new CambioPrecios(regular, premium, glp, diesel, urea );

        Call<CambioPrecios> call = mAPIService.post(precios);
        call.enqueue(new Callback<CambioPrecios>() {
            @Override
            public void onResponse(Call<CambioPrecios> call, Response<CambioPrecios> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConfigurarPrecios.this, "Precios actualizados correctamente", Toast.LENGTH_SHORT).show();
                    inputPrecioRegular.setText(String.valueOf(regular));
                    inputPrecioPremium.setText(String.valueOf(premium));
                    inputPrecioGlp.setText(String.valueOf(glp));
                    inputPrecioDiesel.setText(String.valueOf(diesel));
                    inputPreciourea.setText(String.valueOf(urea));
                } else {
                    Toast.makeText(ConfigurarPrecios.this, "Error al actualizar precios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CambioPrecios> call, Throwable t) {
                Toast.makeText(ConfigurarPrecios.this, "Error de conexión Precios", Toast.LENGTH_SHORT).show();
            }
        });
    }

}




