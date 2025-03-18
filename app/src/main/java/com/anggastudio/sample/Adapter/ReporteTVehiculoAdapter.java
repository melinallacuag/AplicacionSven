package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTVehiculo;

import java.util.List;
import java.util.Locale;

public class ReporteTVehiculoAdapter   extends RecyclerView.Adapter<ReporteTVehiculoAdapter.ViewHolder>{

    public List<ReporteTVehiculo> reporteTVehiculoList;
    private Context context;

    public ReporteTVehiculoAdapter(List<ReporteTVehiculo> reporteTVehiculoList, Context context){
        this.reporteTVehiculoList = reporteTVehiculoList;
        this.context    = context;

    }

    @NonNull
    @Override
    public ReporteTVehiculoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardreportetvehiculo,parent,false);
        return new ReporteTVehiculoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteTVehiculoAdapter.ViewHolder holder, int position) {
        holder.textTVehiculo.setText(reporteTVehiculoList.get(position).getTipovehiculo());
        holder.textVolumen.setText(String.format(Locale.getDefault(), "%10.3f" ,reporteTVehiculoList.get(position).getVolumen()));
        holder.textSoles.setText(String.format(Locale.getDefault(), "%,10.2f" ,reporteTVehiculoList.get(position).getSoles()));

    }

    @Override
    public int getItemCount() {
        return reporteTVehiculoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textTVehiculo;
        private TextView textVolumen;
        private TextView textSoles;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            textTVehiculo   = itemView.findViewById(R.id.textTVehiculo);
            textVolumen     = itemView.findViewById(R.id.textVolumen);
            textSoles       = itemView.findViewById(R.id.textSoles);
        }
    }
}
