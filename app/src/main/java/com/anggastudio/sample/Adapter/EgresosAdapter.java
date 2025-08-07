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
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;

import java.util.ArrayList;
import java.util.List;

public class EgresosAdapter extends RecyclerView.Adapter<EgresosAdapter.ViewHolder>{

    public List<Egreso> egresosList;
    private Context context;
    final EgresosAdapter.OnItemClickListener listener;
    private int selectedItem;

    ArrayList<Egreso> listaOriginal;

    public interface  OnItemClickListener{

        void onItemClick(Egreso item);

    }

    public EgresosAdapter(List<Egreso> egresosList, Context context, EgresosAdapter.OnItemClickListener listener){

        this.egresosList = egresosList;
        this.context   = context;
        this.listener  = listener;
        selectedItem   = -1;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(egresosList);

    }

    @NonNull
    @Override
    public EgresosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlistaegreso,parent,false);
        return new EgresosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EgresosAdapter.ViewHolder holder, int position) {

        Egreso egreso = egresosList.get(position);

        holder.textEID.setText(String.valueOf(egresosList.get(position).getId()));
        holder.textEMoneda.setText(String.valueOf(egresosList.get(position).getMonedaDs()));
        holder.textRTEgreso.setText(String.valueOf(egresosList.get(position).getEgresoDs()));
        holder.textEMonto.setText(String.valueOf(String.format("%.2f",egresosList.get(position).getMtoTotal())));
        holder.textEAnulado.setText(egresosList.get(position).getAnulado());

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

                listener.onItemClick(egreso);
            }
        });

    }

    @Override
    public int getItemCount() {
        return egresosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textEID;
        private TextView textEMoneda;
        private TextView textRTEgreso;
        private TextView textEMonto;
        private TextView textEAnulado;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView       = itemView.findViewById(R.id.cardlistaEgresos);
            textEID        = itemView.findViewById(R.id.textEID);
            textEMoneda    = itemView.findViewById(R.id.textEMoneda);
            textRTEgreso   = itemView.findViewById(R.id.textRTEgreso);
            textEMonto     = itemView.findViewById(R.id.textEMonto);
            textEAnulado   = itemView.findViewById(R.id.textEAnulado);
        }
    }
}
