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
import com.anggastudio.sample.WebApiSVEN.Models.Cliente;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteAdapter  extends RecyclerView.Adapter<ClienteAdapter.ViewHolder>{

    public List<Cliente> clienteList;
    private Context context;

    ArrayList<Cliente> listaOriginal;



    public interface  OnItemClickListener{
        void onItemClick(Cliente item);
    }

    final ClienteAdapter.OnItemClickListener listener;

    public ClienteAdapter(List<Cliente> clienteList, Context context,ClienteAdapter.OnItemClickListener listener){
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
        return new ClienteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ViewHolder holder, int position) {
        Cliente cliente = clienteList.get(position);
        holder.textID.setText(clienteList.get(position).getClienteID());
        holder.textRUC.setText(clienteList.get(position).getClienteRUC());
        holder.textRazoSocial.setText(clienteList.get(position).getClienteRZ());
        holder.textDireccon.setText(clienteList.get(position).getClienteDR());
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
                List<Cliente> collecion = clienteList.stream()
                        .filter(i -> i.getClienteRZ().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                clienteList.clear();
                clienteList.addAll(collecion);
            } else {
                for (Cliente c : listaOriginal) {
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
        private TextView textID;
        private TextView textRUC;
        private TextView textRazoSocial;
        private TextView textDireccon;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView        = itemView.findViewById(R.id.cardCliente);
            textID          = itemView.findViewById(R.id.textID);
            textRUC         = itemView.findViewById(R.id.textRUC);
            textRazoSocial  = itemView.findViewById(R.id.textRazoSocial);
            textDireccon    = itemView.findViewById(R.id.textDireccon);
        }
    }
}
