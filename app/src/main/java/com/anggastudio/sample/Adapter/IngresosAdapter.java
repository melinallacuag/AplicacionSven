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
import com.anggastudio.sample.WebApiSVEN.Models.Egreso;
import com.anggastudio.sample.WebApiSVEN.Models.Ingresos;

import java.util.ArrayList;
import java.util.List;

public class IngresosAdapter extends RecyclerView.Adapter<IngresosAdapter.ViewHolder> {

    public List<Ingresos> ingresosList;
    private Context context;
    final IngresosAdapter.OnItemClickListener listener;
    private int selectedItem;

    ArrayList<Ingresos> listaOriginal;

    public interface  OnItemClickListener{

        void onItemClick(Ingresos item);

    }

    public IngresosAdapter(List<Ingresos> ingresosList, Context context, IngresosAdapter.OnItemClickListener listener){

        this.ingresosList = ingresosList;
        this.context   = context;
        this.listener  = listener;
        selectedItem   = -1;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(ingresosList);

    }

    @NonNull
    @Override
    public IngresosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlistaingreso,parent,false);
        return new IngresosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngresosAdapter.ViewHolder holder, int position) {

        Ingresos ingresos = ingresosList.get(position);

        holder.textIID.setText(String.valueOf(ingresosList.get(position).getId()));
        holder.textIMoneda.setText(String.valueOf(ingresosList.get(position).getMonedaDs()));
        holder.textIMonto.setText(String.valueOf(String.format("%.2f",ingresosList.get(position).getMtoTotal())));
        holder.textIAnulado.setText(ingresosList.get(position).getAnulado());

        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));

        if (selectedItem == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#0dcaf0"));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);

                listener.onItemClick(ingresos);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ingresosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textIID;
        private TextView textIMoneda;
        private TextView textIMonto;
        private TextView textIAnulado;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView       = itemView.findViewById(R.id.cardlistaEgresos);
            textIID        = itemView.findViewById(R.id.textIID);
            textIMoneda    = itemView.findViewById(R.id.textIMoneda);
            textIMonto     = itemView.findViewById(R.id.textIMonto);
            textIAnulado   = itemView.findViewById(R.id.textIAnulado);
        }
    }
}
