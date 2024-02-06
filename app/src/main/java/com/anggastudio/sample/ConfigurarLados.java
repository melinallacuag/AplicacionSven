package com.anggastudio.sample;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anggastudio.sample.Adapter.CLadosAdapter;
import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurarLados extends AppCompatActivity {

    private APIService mAPIService;
    CLadosAdapter CLadosAdapter;
    RecyclerView recyclerCLados;
    List<Lados> CladosList;
    TextInputEditText inputTerminalId,inputNroLado;
    TextInputLayout alertNroLado,alertTerminalId;
    Button btnEliminaCLado,btnAgregarCLado;
    String TerminalId,NroLado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_configurar_lados);
        mAPIService = GlobalInfo.getAPIService();

        inputTerminalId  = findViewById(R.id.inputTerminalId);
        inputNroLado     = findViewById(R.id.inputNroLado);
        alertNroLado     = findViewById(R.id.alertNroLado);
        alertTerminalId  = findViewById(R.id.alertTerminalId);
        btnEliminaCLado  = findViewById(R.id.btnEliminaCLado);
        btnAgregarCLado  = findViewById(R.id.btnAgregarCLado);

        /**
         * @OBTENER:ListadoLados
         */
        recyclerCLados = findViewById(R.id.recyclerCLados);
        recyclerCLados.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ListaLados();

        /**
         * @ELIMINAR:CLados
         */
        btnEliminaCLado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TerminalId  = inputTerminalId.getText().toString();
                NroLado     = inputNroLado.getText().toString();

                if(TerminalId.isEmpty()){
                    alertTerminalId.setError("El campo terminal es obligatorio");
                    return;
                }else if(NroLado.isEmpty()){
                    alertNroLado.setError("El campo nro. lado es obligatorio");
                    return;
                }
                alertTerminalId.setErrorEnabled(false);
                alertNroLado.setErrorEnabled(false);

                findLadosQuitar(NroLado,TerminalId);

                inputTerminalId.getText().clear();
                inputNroLado.getText().clear();

                Toast.makeText(ConfigurarLados.this, "Se elimino correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * @GUARDAR:CLados
         */
        btnAgregarCLado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TerminalId  = inputTerminalId.getText().toString();
                NroLado     = inputNroLado.getText().toString();

                if(TerminalId.isEmpty()){
                    alertTerminalId.setError("El campo terminal es obligatorio");
                    return;
                }else if(NroLado.isEmpty()){
                    alertNroLado.setError("El campo nro. lado es obligatorio");
                    return;
                }
                alertTerminalId.setErrorEnabled(false);
                alertNroLado.setErrorEnabled(false);

                findLadosAgregar(NroLado,TerminalId);

                inputTerminalId.getText().clear();
                inputNroLado.getText().clear();

                Toast.makeText(ConfigurarLados.this, "Se guardo correctamente", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * @APISERVICE:Lados
     */
    private void ListaLados() {

        Call<List<Lados>> call = mAPIService.getLados();

        call.enqueue(new Callback<List<Lados>>() {
            @Override
            public void onResponse(Call<List<Lados>> call, Response<List<Lados>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CladosList = response.body();

                    CLadosAdapter = new CLadosAdapter(CladosList, getApplicationContext(), new CLadosAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Lados item) {
                            inputTerminalId.setText(item.getTerminalID());
                            inputNroLado.setText(item.getNroLado());
                        }
                    });
                    recyclerCLados.setAdapter(CLadosAdapter);

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lados>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE Configuración Cara - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * @APISERVICE:EliminarLados
     */

    private void findLadosQuitar(String nrolado, String idTerminal){

        Call<List<Lados>> call = mAPIService.findLadosQuitar(nrolado,idTerminal);

        call.enqueue(new Callback<List<Lados>>() {
            @Override
            public void onResponse(Call<List<Lados>> call, Response<List<Lados>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error CLado Quitar: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CladosList = response.body();

                    ListaLados();

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lados>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE CLado Quitar- RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @APISERVICE:GuardarLados
     */

    private void findLadosAgregar(String idTerminal, String lado){

        Call<List<Lados>> call = mAPIService.findLadosAgregar(idTerminal,lado);

        call.enqueue(new Callback<List<Lados>>() {
            @Override
            public void onResponse(Call<List<Lados>> call, Response<List<Lados>> response) {
                try {

                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Codigo de error CLado Agregar: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CladosList = response.body();

                    ListaLados();

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lados>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión APICORE CLado Agregar - RED - WIFI", Toast.LENGTH_SHORT).show();
            }
        });
    }
}