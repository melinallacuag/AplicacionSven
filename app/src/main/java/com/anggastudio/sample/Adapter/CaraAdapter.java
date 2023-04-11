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


public class CaraAdapter extends RecyclerView.Adapter<CaraAdapter.ViewHolder>{
    private List<Lados> caraList;
    private Context context;
    final CaraAdapter.OnItemClickListener listener;
    private int selectedItem;

    public interface  OnItemClickListener{
        int onItemClick(Lados item);
    }

    public CaraAdapter(List<Lados> caraList,Context context,CaraAdapter.OnItemClickListener listener){
        this.caraList  = caraList;
        this.context   = context;
        this.listener  = listener;
        selectedItem   = -1;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardcara,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lados lados = caraList.get(position);
        holder.txtcara.setText(caraList.get(position).getNroLado());

        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));

        if (selectedItem == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#5EA7DE"));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);

                listener.onItemClick(lados);
            }
        });
    }

    @Override
    public int getItemCount() {
        return caraList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView txtcara;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.cardcara);
            txtcara = itemView.findViewById(R.id.txtcara);
        }
    }
}