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
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;

import java.util.List;
import java.util.Locale;

public class VContometroAdapter extends RecyclerView.Adapter<VContometroAdapter.ViewHolder> {

    public List<VContometro> vContometroList;
    private Context context;

    public VContometroAdapter(List<VContometro> vContometroList, Context context){
        this.vContometroList = vContometroList;
        this.context    = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardventacontometro,parent,false);
        return new VContometroAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textlado.setText(vContometroList.get(position).getNroLado());
        holder.textproducto.setText(vContometroList.get(position).getArticuloDS());
        holder.textcantidadI.setText(String.valueOf(String.format("%.3f",vContometroList.get(position).getContomInicial())));
        holder.textcantidadF.setText(String.valueOf(String.format("%.3f",vContometroList.get(position).getContomFinal())));
        holder.textgalones.setText(String.valueOf(String.format(Locale.getDefault(), "%,.3f" ,vContometroList.get(position).getGalones())));
    }

    @Override
    public int getItemCount() {

      //  return Math.min(vContometroList.size(), 17);
        return vContometroList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textlado;
        private TextView textproducto;
        private TextView textcantidadI;
        private TextView textcantidadF;
        private TextView textgalones;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView        = itemView.findViewById(R.id.cardmanguera);
            textlado        = itemView.findViewById(R.id.textlado);
            textproducto    = itemView.findViewById(R.id.textproducto);
            textcantidadI   = itemView.findViewById(R.id.textcantidadI);
            textcantidadF   = itemView.findViewById(R.id.textcantidadF);
            textgalones     = itemView.findViewById(R.id.textgalones);
        }
    }
}
