package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Egreso;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteEgreso;

import java.util.ArrayList;
import java.util.List;

public class ReporteEgresoAdapter extends RecyclerView.Adapter<ReporteEgresoAdapter.ViewHolder>{

    public List<ReporteEgreso> reporteEgresoList;
    private Context context;

    public ReporteEgresoAdapter(List<ReporteEgreso> reporteEgresoList, Context context){
        this.reporteEgresoList = reporteEgresoList;
        this.context   = context;
    }

    @NonNull
    @Override
    public ReporteEgresoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardreporteegresos,parent,false);
        return new ReporteEgresoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteEgresoAdapter.ViewHolder holder, int position) {
        holder.textIDEgreso.setText(String.valueOf(reporteEgresoList.get(position).getId()));
        holder.textTEgreso.setText(String.valueOf(reporteEgresoList.get(position).getEgresoDs()));
        holder.textSoles.setText(String.valueOf(String.format("%.2f",reporteEgresoList.get(position).getMtoTotal())));
    }

    @Override
    public int getItemCount() {
        return reporteEgresoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textIDEgreso;
        private TextView textTEgreso;
        private TextView textSoles;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView      = itemView.findViewById(R.id.cardlistaEgresos);
            textIDEgreso    = itemView.findViewById(R.id.textIDEgreso);
            textTEgreso   = itemView.findViewById(R.id.textTEgreso);
            textSoles     = itemView.findViewById(R.id.textSoles);
        }
    }
}
