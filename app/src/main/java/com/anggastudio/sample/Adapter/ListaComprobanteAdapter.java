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
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaComprobanteAdapter extends RecyclerView.Adapter<ListaComprobanteAdapter.ViewHolder>{

    public List<ListaComprobante> listaComprobanteList;
    private Context context;
    final OnItemClickListener listener;
    private int selectedItem;

    ArrayList<ListaComprobante> listaOriginal;

    public interface  OnItemClickListener{

        void onItemClick(ListaComprobante item);

    }

    public ListaComprobanteAdapter(List<ListaComprobante> listaComprobanteList, Context context, OnItemClickListener listener){

        this.listaComprobanteList = listaComprobanteList;
        this.context   = context;
        this.listener  = listener;
        selectedItem   = -1;

        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaComprobanteList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlistacomprobantes,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ListaComprobante listaComprobante = listaComprobanteList.get(position);

        holder.textCFechaEmision.setText(listaComprobanteList.get(position).getFecha());
        holder.textCRUC.setText(listaComprobanteList.get(position).getClienteID());
        holder.textCRazonSocial.setText(listaComprobanteList.get(position).getClienteRZ());
        holder.textCTotal.setText(String.valueOf(listaComprobanteList.get(position).getMtoTotal()));
        holder.textCAnulado.setText(listaComprobanteList.get(position).getAnulado());

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

                listener.onItemClick(listaComprobante);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaComprobanteList.size();
    }

    public void filtrado(final String txtBuscar) {

        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaComprobanteList.clear();
            listaComprobanteList.addAll(listaOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<ListaComprobante> collecion = listaComprobanteList.stream()
                        .filter(i -> i.getClienteRZ().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaComprobanteList.clear();
                listaComprobanteList.addAll(collecion);
            } else {
                for (ListaComprobante a : listaOriginal) {
                    if (a.getClienteRZ().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaComprobanteList.add(a);

                    }
                }

            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textCFechaEmision;
        private TextView textCRUC;
        private TextView textCRazonSocial;
        private TextView textCTotal;
        private TextView textCAnulado;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView             = itemView.findViewById(R.id.cardlistacomprobante);
            textCFechaEmision    = itemView.findViewById(R.id.textCFechaEmision);
            textCRUC             = itemView.findViewById(R.id.textCRUC);
            textCRazonSocial     = itemView.findViewById(R.id.textCRazonSocial);
            textCTotal           = itemView.findViewById(R.id.textCTotal);
            textCAnulado         = itemView.findViewById(R.id.textCAnulado);
        }
    }
}
