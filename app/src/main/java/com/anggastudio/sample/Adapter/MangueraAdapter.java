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
import com.anggastudio.sample.WebApiSVEN.Models.Picos;

import java.util.List;

public class MangueraAdapter extends RecyclerView.Adapter<MangueraAdapter.ViewHolder> {

    private List<Picos> mangueraList;
    private Context context;
    final MangueraAdapter.OnItemClickListener listener;
    private int selectedItem;

    public interface  OnItemClickListener{
        void onItemClick(Picos item);
    }
    public MangueraAdapter(List<Picos> mangueraList, Context context,MangueraAdapter.OnItemClickListener listener){
        this.mangueraList = mangueraList;
        this.context    = context;
        this.listener   = listener;
        selectedItem    = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardmanguera,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangueraAdapter.ViewHolder holder, int position) {
        Picos picos = mangueraList.get(position);
        holder.txtmanguera.setText(mangueraList.get(position).getDescripcion());
        holder.txtmangueraID.setText(mangueraList.get(position).getMangueraID());

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

                listener.onItemClick(picos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangueraList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView txtmanguera;
        private TextView txtmangueraID;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView      = itemView.findViewById(R.id.cardmanguera);
            txtmanguera   = itemView.findViewById(R.id.txtmanguera);
            txtmangueraID = itemView.findViewById(R.id.txtmangueraID);
        }
    }
}
