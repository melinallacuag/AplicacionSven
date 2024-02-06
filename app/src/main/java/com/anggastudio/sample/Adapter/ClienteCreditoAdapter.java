package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.ClienteCredito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteCreditoAdapter extends RecyclerView.Adapter<ClienteCreditoAdapter.ViewHolder>{

    public List<ClienteCredito> clienteCreditoList;
    private Context context;
    ArrayList<ClienteCredito> listaOriginal;

    public interface  OnItemClickListener{
        void onItemClick(ClienteCredito item);
    }

    final ClienteCreditoAdapter.OnItemClickListener listener;

    public ClienteCreditoAdapter(List<ClienteCredito> clienteCreditoList, Context context, ClienteCreditoAdapter.OnItemClickListener listener){
        this.clienteCreditoList = clienteCreditoList;
        this.context    = context;
        this.listener    = listener;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(clienteCreditoList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardclientecredito,parent,false);
        return new ClienteCreditoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteCreditoAdapter.ViewHolder holder, int position) {
        ClienteCredito clienteCredito = clienteCreditoList.get(position);
        holder.clienteC_RZ.setText(clienteCreditoList.get(position).getClienteRZ());
        holder.clienteC_Tarj.setText(clienteCreditoList.get(position).getTarjetaID());
        holder.clienteC_Saldo.setText(String.valueOf(clienteCreditoList.get(position).getSaldo()));
        holder.clienteC_Articulo.setText(String.valueOf(clienteCreditoList.get(position).getArticuloID()));

        holder.clienteC_Tipo.setText(clienteCreditoList.get(position).getTipo());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(clienteCredito);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clienteCreditoList.size();
    }

    public void filtrado(final String txtBuscar) {
        List<ClienteCredito> filteredList = new ArrayList<>();

        if (txtBuscar.isEmpty()) {
            filteredList.addAll(listaOriginal);
        } else {
            String txtBuscarLowerCase = txtBuscar.toLowerCase();

            for (ClienteCredito c : listaOriginal) {
                if (c.getClienteRZ().toLowerCase().contains(txtBuscarLowerCase)) {
                    filteredList.add(c);
                }
            }
        }

        clienteCreditoList.clear();
        clienteCreditoList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView clienteC_RZ;
        private TextView clienteC_Tarj;
        private TextView clienteC_Saldo;
        private TextView clienteC_Tipo;
        private TextView  clienteC_Articulo;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView        = itemView.findViewById(R.id.cardClienteCredito);
            clienteC_RZ     = itemView.findViewById(R.id.clienteC_RZ);
            clienteC_Tarj   = itemView.findViewById(R.id.clienteC_Tarj);
            clienteC_Saldo  = itemView.findViewById(R.id.clienteC_Saldo);
            clienteC_Tipo   = itemView.findViewById(R.id.clienteC_Tipo);
            clienteC_Articulo   = itemView.findViewById(R.id.clienteC_Articulo);
        }
    }
}
