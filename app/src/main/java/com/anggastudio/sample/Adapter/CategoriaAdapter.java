package com.anggastudio.sample.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.ProCategorias;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder>{

    final OnItemClickListener listener;
    private List<ProCategorias> proCategoriasList;

    private boolean isTodoSelected = false;
    private int selectedItem;

    public interface  OnItemClickListener{
        int onItemClick(ProCategorias item);
    }

    public CategoriaAdapter(List<ProCategorias> proCategoriasList, OnItemClickListener listener){
        this.proCategoriasList = proCategoriasList;
        this.listener   = listener;
        selectedItem    = -1;
    }

    /** @OBTENER:EstadoSeleccion **/
    public void setTodoSelected(boolean selected) {
        isTodoSelected = selected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProCategorias proCategorias = proCategoriasList.get(position);

        holder.btncategorias.setText(proCategorias.getNomcategoria());

        /** @SELECCIONAR:EstadoCategoria **/
        if (selectedItem == position  && !isTodoSelected) {
            holder.btncategorias.setBackgroundColor(Color.parseColor("#FFC107"));
            holder.btncategorias.setTextColor(Color.parseColor("#000000"));
        }else {
            holder.btncategorias.setBackgroundColor(Color.parseColor("#999999"));
            holder.btncategorias.setTextColor(Color.parseColor("#FFFFFF"));
        }

        holder.btncategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTodoSelected) {
                    isTodoSelected = false;
                }
                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);
                listener.onItemClick(proCategorias);
            }
        });
    }

    @Override
    public int getItemCount() {
        return proCategoriasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public Button btncategorias;

        public ViewHolder(@NonNull View itemView){

            super(itemView);
            btncategorias  = itemView.findViewById(R.id.btncategorias);

        }

    }
}
