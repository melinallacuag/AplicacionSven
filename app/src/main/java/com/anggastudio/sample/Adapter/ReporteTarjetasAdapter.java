package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;

import java.util.List;
import java.util.Locale;

public class ReporteTarjetasAdapter  extends RecyclerView.Adapter<ReporteTarjetasAdapter.ViewHolder> {

    public List<ReporteTarjetas> reporteTarjetasList;
    private Context context;

    public ReporteTarjetasAdapter(List<ReporteTarjetas> reporteTarjetasList, Context context){
        this.reporteTarjetasList = reporteTarjetasList;
        this.context    = context;

    }
    @NonNull
    @Override
    public ReporteTarjetasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardreportetarjeta,parent,false);
        return new ReporteTarjetasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteTarjetasAdapter.ViewHolder holder, int position) {
        holder.textReferencia.setText(reporteTarjetasList.get(position).getNroReferencia());
        holder.textNumDocumento.setText(reporteTarjetasList.get(position).getNroDocumento());
        holder.textTipo.setText(reporteTarjetasList.get(position).getTipo());
        holder.textMonto.setText(String.valueOf(reporteTarjetasList.get(position).getMonto()));

    }

    @Override
    public int getItemCount() {
        return reporteTarjetasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textReferencia;
        private TextView textNumDocumento;
        private TextView textTipo;
        private TextView textMonto;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            textReferencia     = itemView.findViewById(R.id.textReferencia);
            textNumDocumento   = itemView.findViewById(R.id.textNumDocumento);
            textTipo           = itemView.findViewById(R.id.textTipo);
            textMonto          = itemView.findViewById(R.id.textMonto);
        }
    }
}
