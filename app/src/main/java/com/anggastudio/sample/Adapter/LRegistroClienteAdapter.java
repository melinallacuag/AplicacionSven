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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LRegistroClienteAdapter extends RecyclerView.Adapter<LRegistroClienteAdapter.ViewHolder>{

    public List<ClientePrecio> clientePrecioList;
    private Context context;
    final OnItemClickListener listener;
    private int selectedItem;

    ArrayList<ClientePrecio> listaOriginal;

    public void updateData(List<ClientePrecio> newData) {
        clientePrecioList.clear();
        clientePrecioList.addAll(newData);
        notifyDataSetChanged();
    }


    public interface  OnItemClickListener{

        void onItemClick(ClientePrecio item);

    }

    public LRegistroClienteAdapter(List<ClientePrecio> clientePrecioList, Context context, OnItemClickListener listener){

        this.clientePrecioList = clientePrecioList;
        this.context    = context;
        this.listener   = listener;
        selectedItem    = -1;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(clientePrecioList);


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlistaclienteafiliados,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ClientePrecio clientePrecio = clientePrecioList.get(position);

        holder.textNFC.setText(clientePrecioList.get(position).getRfid());
        holder.textArticuloID.setText(clientePrecioList.get(position).getArticuloID());
        holder.textClienteID.setText(clientePrecioList.get(position).getClienteID());
        holder.textTipoCliente.setText(String.valueOf(clientePrecioList.get(position).getTipoCliente()));
        holder.textClienteRZ.setText(clientePrecioList.get(position).getClienteRZ());
        holder.textMontoDescuento.setText(String.valueOf(String.format("%.2f",clientePrecioList.get(position).getMontoDescuento())));

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

                listener.onItemClick(clientePrecio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientePrecioList.size();
    }

    public void filtrado(final String txtBuscar) {
        clientePrecioList.clear();

        if (txtBuscar.isEmpty()) {
            clientePrecioList.addAll(listaOriginal);
        } else {
            String txtBuscarLowerCase = txtBuscar.toLowerCase();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<ClientePrecio> filteredList = listaOriginal.stream()
                        .filter(i -> i.getClienteRZ().toLowerCase().contains(txtBuscarLowerCase))
                        .collect(Collectors.toList());
                clientePrecioList.addAll(filteredList);
            } else {
                for (ClientePrecio comprobante : listaOriginal) {
                    if (comprobante.getClienteRZ().toLowerCase().contains(txtBuscarLowerCase)) {
                        clientePrecioList.add(comprobante);
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textNFC;
        private TextView textArticuloID;
        private TextView textClienteID;
        private TextView textTipoCliente;
        private TextView textClienteRZ;
        private TextView textMontoDescuento;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView            = itemView.findViewById(R.id.cardlistaclienteafiliados);
            textNFC             = itemView.findViewById(R.id.textNFC);
            textArticuloID      = itemView.findViewById(R.id.textArticuloID);
            textClienteID       = itemView.findViewById(R.id.textClienteID);
            textTipoCliente     = itemView.findViewById(R.id.textTipoCliente);
            textClienteRZ       = itemView.findViewById(R.id.textClienteRZ);
            textMontoDescuento  = itemView.findViewById(R.id.textMontoDescuento);
        }
    }
}
