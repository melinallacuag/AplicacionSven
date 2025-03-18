package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.CambioPrecios;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;

import java.util.List;

public class PreciosAdapter extends RecyclerView.Adapter<PreciosAdapter.ViewHolder>{

    final PreciosAdapter.OnItemClickListener listener;
    private List<CambioPrecios> cambioPreciosList;
    private Context context;
    private int selectedItem;

    public interface  OnItemClickListener{
        int onItemClick(Mangueras item);
    }

    public PreciosAdapter(List<CambioPrecios> cambioPreciosList, Context context, PreciosAdapter.OnItemClickListener listener){
        this.cambioPreciosList = cambioPreciosList;
        this.context    = context;
        this.listener   = listener;
        selectedItem    = -1;
    }


    @NonNull
    @Override
    public PreciosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_precios,parent,false);
        return new PreciosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreciosAdapter.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return cambioPreciosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView card_productoPrecio;
        private TextView text_producto;
        private TextView input_precios;

        public ViewHolder(@NonNull View itemView){

            super(itemView);
            card_productoPrecio  = itemView.findViewById(R.id.card_productoPrecio);
            text_producto    = itemView.findViewById(R.id.text_producto);
            input_precios    = itemView.findViewById(R.id.input_precios);

        }

    }
}
