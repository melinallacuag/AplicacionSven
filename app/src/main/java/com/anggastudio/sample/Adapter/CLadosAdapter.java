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
import com.anggastudio.sample.WebApiSVEN.Models.Lados;

import java.util.List;

public class CLadosAdapter extends RecyclerView.Adapter<CLadosAdapter.ViewHolder>{

    final CLadosAdapter.OnItemClickListener listener;
    private List<Lados> CladosList;
    private Context context;
    private int selectedItem;

    public interface  OnItemClickListener{
        void onItemClick(Lados item);
    }

    public CLadosAdapter(List<Lados> CladosList, Context context, CLadosAdapter.OnItemClickListener listener){
        this.CladosList  = CladosList;
        this.context   = context;
        this.listener  = listener;
        selectedItem   = -1;
    }

    @NonNull
    @Override
    public CLadosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_clados,parent,false);
        return new CLadosAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CLadosAdapter.ViewHolder holder, int position) {
        Lados Clados = CladosList.get(position);

        holder.puntoVenta.setText(CladosList.get(position).getTerminalID());
        holder.nro_Lado.setText(CladosList.get(position).getNroLado());

        holder.card_CLado.setCardBackgroundColor(context.getResources().getColor(R.color.white));

        if (selectedItem == position) {
            holder.card_CLado.setCardBackgroundColor(Color.parseColor("#0dcaf0"));
        }

        holder.card_CLado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);

                listener.onItemClick(Clados);

            }
        });
    }

    @Override
    public int getItemCount() {
        return CladosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView card_CLado;
        private TextView puntoVenta;
        private TextView nro_Lado;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            card_CLado  = itemView.findViewById(R.id.card_CLado);
            puntoVenta  = itemView.findViewById(R.id.puntoVenta);
            nro_Lado    = itemView.findViewById(R.id.lado);
        }

    }
}
