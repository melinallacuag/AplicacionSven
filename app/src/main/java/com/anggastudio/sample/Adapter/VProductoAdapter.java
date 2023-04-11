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
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;

import java.util.List;
import java.util.Locale;

public class VProductoAdapter extends RecyclerView.Adapter<VProductoAdapter.ViewHolder>{

    public List<VProducto> vProductoList;
    private Context context;

    public VProductoAdapter(List<VProducto> vProductoList, Context context){
        this.vProductoList = vProductoList;
        this.context       = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardventaproducto,parent,false);
        return new VProductoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VProductoAdapter.ViewHolder holder, int position) {
        holder.textPproducto.setText(vProductoList.get(position).getArticuloDS());
        holder.textPvolumen.setText(String.valueOf(String.format(Locale.getDefault(), "%,.3f" ,vProductoList.get(position).getCantidad())));
        holder.textPsoles.setText(String.valueOf(String.format(Locale.getDefault(), "%,.2f" ,vProductoList.get(position).getSoles())));
        holder.textPdescuento.setText(String.valueOf(String.format(Locale.getDefault(), "%,.2f" ,vProductoList.get(position).getDescuento())));
    }

    @Override
    public int getItemCount() {
        return vProductoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textPproducto;
        private TextView textPvolumen;
        private TextView textPsoles;
        private TextView textPdescuento;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView        = itemView.findViewById(R.id.cardmanguera);
            textPproducto        = itemView.findViewById(R.id.textPproducto);
            textPvolumen    = itemView.findViewById(R.id.textPvolumen);
            textPsoles   = itemView.findViewById(R.id.textPsoles);
            textPdescuento   = itemView.findViewById(R.id.textPdescuento);

        }
    }
}
