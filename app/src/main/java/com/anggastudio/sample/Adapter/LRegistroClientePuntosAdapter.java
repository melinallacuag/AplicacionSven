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
import com.anggastudio.sample.WebApiSVEN.Models.ClientePrecio;
import com.anggastudio.sample.WebApiSVEN.Models.LClientePuntos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LRegistroClientePuntosAdapter extends RecyclerView.Adapter<LRegistroClientePuntosAdapter.ViewHolder>{

    public List<LClientePuntos> clientePuntosList;
    private Context context;
    final OnItemClickListener listener;
    private int selectedItem;

    ArrayList<LClientePuntos> listaOriginal;

    public void updateData(List<LClientePuntos> newData) {
        clientePuntosList.clear();
        clientePuntosList.addAll(newData);
        notifyDataSetChanged();
    }


    public interface  OnItemClickListener{

        void onItemClick(LClientePuntos item);

    }

    public LRegistroClientePuntosAdapter(List<LClientePuntos> clientePuntosList, Context context, OnItemClickListener listener){

        this.clientePuntosList = clientePuntosList;
        this.context    = context;
        this.listener   = listener;
        selectedItem    = -1;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(clientePuntosList);


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlistaclientepuntos,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LClientePuntos clientePuntos = clientePuntosList.get(position);

        holder.textNFC.setText(clientePuntosList.get(position).getRfid());
        holder.textClienteID.setText(clientePuntosList.get(position).getClienteID());
        holder.textClienteRZ.setText(clientePuntosList.get(position).getClienteRZ());
        holder.textDisponible.setText(String.valueOf(clientePuntosList.get(position).getDisponibles()));
        boolean status = clientePuntosList.get(position).getStatus();
        holder.textEstado.setText(status ? "Activo" : "Inactivo");

        if (status) {
            holder.textEstado.setTextColor(context.getResources().getColor(R.color.blue));
            holder.cardView.setClickable(true);
        } else {
            holder.textEstado.setTextColor(context.getResources().getColor(R.color.colorDanger));
            holder.cardView.setClickable(false);
        }

        if (selectedItem == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#0dcaf0"));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status) {
                    int previousItem = selectedItem;
                    selectedItem = position;
                    notifyItemChanged(previousItem);
                    notifyItemChanged(position);

                    listener.onItemClick(clientePuntos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientePuntosList.size();
    }

    public void filtrado(final String txtBuscar) {
        clientePuntosList.clear();

        if (txtBuscar.isEmpty()) {
            clientePuntosList.addAll(listaOriginal);
        } else {
            String txtBuscarLowerCase = txtBuscar.toLowerCase();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<LClientePuntos> filteredList = listaOriginal.stream()
                        .filter(i -> i.getClienteRZ().toLowerCase().contains(txtBuscarLowerCase))
                        .collect(Collectors.toList());
                clientePuntosList.addAll(filteredList);
            } else {
                for (LClientePuntos comprobante : listaOriginal) {
                    if (comprobante.getClienteRZ().toLowerCase().contains(txtBuscarLowerCase)) {
                        clientePuntosList.add(comprobante);
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textNFC;
        private TextView textClienteID;
        private TextView textClienteRZ;
        private TextView textDisponible;
        private TextView textEstado;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView            = itemView.findViewById(R.id.cardlistaclienteafiliados);
            textNFC             = itemView.findViewById(R.id.textNFC);
            textClienteID       = itemView.findViewById(R.id.textClienteID);
            textClienteRZ       = itemView.findViewById(R.id.textClienteRZ);
            textDisponible      = itemView.findViewById(R.id.textDisponible);
            textEstado          = itemView.findViewById(R.id.textEstado);
        }
    }
}
