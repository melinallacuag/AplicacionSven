package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteVendedor;

import java.util.List;
import java.util.Locale;

public class ReporteVendedorAdapter extends RecyclerView.Adapter<ReporteVendedorAdapter.ViewHolder> {

    public List<ReporteVendedor> reporteVendedorList;
    private Context context;

    public ReporteVendedorAdapter(List<ReporteVendedor> reporteVendedorList, Context context){
        this.reporteVendedorList = reporteVendedorList;
        this.context    = context;

    }

    @NonNull
    @Override
    public ReporteVendedorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardreportevendedor,parent,false);
        return new ReporteVendedorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteVendedorAdapter.ViewHolder holder, int position) {
        holder.textNombres.setText(reporteVendedorList.get(position).getNombres());
        holder.textDespachos.setText(String.valueOf(reporteVendedorList.get(position).getDespachos()));
        holder.textSoles.setText(String.format(Locale.getDefault(), "%,10.2f" ,reporteVendedorList.get(position).getSoles()));
    }

    @Override
    public int getItemCount() {
        return reporteVendedorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textNombres;
        private TextView textDespachos;
        private TextView textSoles;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            textNombres   = itemView.findViewById(R.id.textNombres);
            textDespachos = itemView.findViewById(R.id.textDespachos);
            textSoles     = itemView.findViewById(R.id.textSoles);

        }
    }
}
