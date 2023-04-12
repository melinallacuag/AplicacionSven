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
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LClienteAdapter extends RecyclerView.Adapter<LClienteAdapter.ViewHolder>{

    public List<LClientes> clienteList;
    private Context context;
    ArrayList<LClientes> listaOriginal;

    public interface  OnItemClickListener{
        void onItemClick(LClientes item);
    }

    final LClienteAdapter.OnItemClickListener listener;

    public LClienteAdapter(List<LClientes> clienteList, Context context, LClienteAdapter.OnItemClickListener listener){
        this.clienteList = clienteList;
        this.context    = context;
        this.listener    = listener;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(clienteList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardcliente,parent,false);
        return new LClienteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LClienteAdapter.ViewHolder holder, int position) {
        LClientes cliente = clienteList.get(position);
        holder.cliente_ID.setText(clienteList.get(position).getClienteID());
        holder.cliente_RUC.setText(clienteList.get(position).getClienteRUC());
        holder.cliente_RZ.setText(clienteList.get(position).getClienteRZ());
        holder.cliente_DR.setText(clienteList.get(position).getClienteDR());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(cliente);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clienteList.size();
    }

    public void filtrado(final String txtBuscar) {

        int longitud = txtBuscar.length();
        if (longitud == 0) {
            clienteList.clear();
            clienteList.addAll(listaOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<LClientes> collecion = clienteList.stream()
                        .filter(i -> i.getClienteRZ().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                clienteList.clear();
                clienteList.addAll(collecion);
            } else {
                for (LClientes c : listaOriginal) {
                    if (c.getClienteRZ().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        clienteList.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView cliente_ID;
        private TextView cliente_RUC;
        private TextView cliente_RZ;
        private TextView cliente_DR;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView        = itemView.findViewById(R.id.cardCliente);
            cliente_ID      = itemView.findViewById(R.id.cliente_ID);
            cliente_RUC     = itemView.findViewById(R.id.cliente_RUC);
            cliente_RZ      = itemView.findViewById(R.id.cliente_RZ);
            cliente_DR     = itemView.findViewById(R.id.cliente_DR);
        }
    }
}
