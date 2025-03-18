package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VProductoTienda;

import java.util.List;
import java.util.Locale;

public class VProductoTiendaAdapter extends RecyclerView.Adapter<VProductoTiendaAdapter.ViewHolder>{

    public List<VProductoTienda> vProductoTiendaList;
    private Context context;

    public VProductoTiendaAdapter(List<VProductoTienda> vProductoTiendaList, Context context){
        this.vProductoTiendaList = vProductoTiendaList;
        this.context       = context;

    }

    @NonNull
    @Override
    public VProductoTiendaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardventaproductotienda,parent,false);
        return new VProductoTiendaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VProductoTiendaAdapter.ViewHolder holder, int position) {
        holder.textPNombreTienda.setText(vProductoTiendaList.get(position).getArticuloDS());
        holder.textPCantidadTienda.setText(String.valueOf(String.format(Locale.getDefault(), "%,10.2f" ,vProductoTiendaList.get(position).getCantidad())));
        holder.textPSolesTienda.setText(String.valueOf(String.format(Locale.getDefault(), "%,10.2f" ,vProductoTiendaList.get(position).getSoles())));
    }

    @Override
    public int getItemCount() {
        return vProductoTiendaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textPNombreTienda;
        private TextView textPCantidadTienda;
        private TextView textPSolesTienda;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            textPNombreTienda    = itemView.findViewById(R.id.textPNombreTienda);
            textPCantidadTienda  = itemView.findViewById(R.id.textPCantidadTienda);
            textPSolesTienda     = itemView.findViewById(R.id.textPSolesTienda);

        }
    }
}
