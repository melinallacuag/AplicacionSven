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
        holder.textNumDocumento.setText(reporteTarjetasList.get(position).getDocumento());
        holder.textTipo.setText(reporteTarjetasList.get(position).getTipo());
        holder.textReferencia.setText(reporteTarjetasList.get(position).getRef());
        holder.textMonto.setText(String.format(Locale.getDefault(), "%,10.2f" ,reporteTarjetasList.get(position).getSoles()));
    }

    @Override
    public int getItemCount() {
        return reporteTarjetasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textNumDocumento;
        private TextView textTipo;
        private TextView textReferencia;
        private TextView textMonto;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            textNumDocumento   = itemView.findViewById(R.id.textNumDocumento);
            textTipo           = itemView.findViewById(R.id.textTipo);
            textReferencia     = itemView.findViewById(R.id.textReferencia);
            textMonto          = itemView.findViewById(R.id.textMonto);
        }
    }
}
