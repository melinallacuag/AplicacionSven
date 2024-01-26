package com.anggastudio.sample.Adapter;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Familia;

import java.util.List;

public class FamiliaAdapter extends RecyclerView.Adapter<FamiliaAdapter.ViewHolder>{

    final FamiliaAdapter.OnItemClickListener listener;
    private List<Familia> familiaList;

    private boolean isTodoSelected = false;
    private int selectedItem;

    public interface  OnItemClickListener{
        int onItemClick(Familia item);
    }

    public FamiliaAdapter(List<Familia> familiaList, FamiliaAdapter.OnItemClickListener listener){
        this.familiaList = familiaList;
        this.listener    = listener;
        selectedItem     = -1;
    }

    public void setTodoSelected(boolean selected) {
        isTodoSelected = selected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FamiliaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria,parent,false);
        return new FamiliaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamiliaAdapter.ViewHolder holder, int position) {
        Familia familia = familiaList.get(position);

        holder.btncategorias.setText(familia.getFamiliaDS());

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
                listener.onItemClick(familia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return familiaList != null ? familiaList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public Button btncategorias;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            btncategorias  = itemView.findViewById(R.id.btncategorias);
        }

    }

}
