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
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;

import java.util.List;

public class DetalleVentaAdapter extends RecyclerView.Adapter<DetalleVentaAdapter.ViewHolder>{

    public List<DetalleVenta> detalleVentaList;
    private Context context;

    public DetalleVentaAdapter(List<DetalleVenta> detalleVentaList, Context context){
        this.detalleVentaList = detalleVentaList;
        this.context    = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carddetalleventa,parent,false);
        return new DetalleVentaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DetalleVenta detalleVenta = detalleVentaList.get(position);

        holder.textLado.setText(detalleVentaList.get(position).getCara());
        holder.textTpago.setText(detalleVentaList.get(position).getTipoPago());
        holder.textImpuesto.setText(String.valueOf(detalleVentaList.get(position).getImpuesto()));
        holder.textNplaca.setText(detalleVentaList.get(position).getNroPlaca());
        holder.textTarjetaPuntos.setText(detalleVentaList.get(position).getTarjetaPuntos());
        holder.textIdCliente.setText(detalleVentaList.get(position).getClienteID());
        holder.textRUC.setText(detalleVentaList.get(position).getClienteRUC());
        holder.textRazonSocial.setText(detalleVentaList.get(position).getClienteRS());
        holder.textDireccion.setText(detalleVentaList.get(position).getClienteDR());
        holder.textTarjetaND.setText(detalleVentaList.get(position).getTarjetaND());
        holder.textTarjCredito.setText(detalleVentaList.get(position).getTarjetaCredito());
        holder.textNroOpe.setText(detalleVentaList.get(position).getOperacionREF());
        holder.textObservacion.setText(detalleVentaList.get(position).getObservacion());
        holder.textKilometraje.setText(detalleVentaList.get(position).getKilometraje());
        holder.textMtoSoles.setText(String.valueOf(String.format("%.2f",detalleVentaList.get(position).getMontoSoles())));
        holder.textSaldoCredito.setText(String.valueOf(detalleVentaList.get(position).getMtoSaldoCredito()));
        holder.textPtosDisponibles.setText(String.valueOf(detalleVentaList.get(position).getPtosDisponible()));

    }

    @Override
    public int getItemCount() {
        return detalleVentaList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textLado;
        private TextView textNplaca;
        private TextView textImpuesto;
        private TextView textTarjetaPuntos;
        private TextView textIdCliente;
        private TextView textRUC;
        private TextView textRazonSocial;
        private TextView textDireccion;
        private TextView textTpago;
        private TextView textTarjetaND;
        private TextView textTarjCredito;
        private TextView textNroOpe;
        private TextView textObservacion;
        private TextView textKilometraje;
        private TextView textMtoSoles;
        private TextView textSaldoCredito;
        private TextView textPtosDisponibles;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView             = itemView.findViewById(R.id.carddetalleventa);
            textLado             = itemView.findViewById(R.id.textLado);
            textNplaca           = itemView.findViewById(R.id.textNplaca);
            textImpuesto         = itemView.findViewById(R.id.textImpuesto);
            textTarjetaPuntos    = itemView.findViewById(R.id.textTarjetaPuntos);
            textIdCliente        = itemView.findViewById(R.id.textIdCliente);
            textRUC              = itemView.findViewById(R.id.textRUC);
            textRazonSocial      = itemView.findViewById(R.id.textRazonSocial);
            textDireccion        = itemView.findViewById(R.id.textDireccion);
            textTpago            = itemView.findViewById(R.id.textTpago);
            textTarjetaND        = itemView.findViewById(R.id.textTarjetaND);
            textTarjCredito      = itemView.findViewById(R.id.textTarjCredito);
            textNroOpe           = itemView.findViewById(R.id.textNroOpe);
            textObservacion      = itemView.findViewById(R.id.textObservacion);
            textKilometraje      = itemView.findViewById(R.id.textKilometraje);
            textMtoSoles         = itemView.findViewById(R.id.textMtoSoles);
            textSaldoCredito     = itemView.findViewById(R.id.textSaldoCredito);
            textPtosDisponibles  = itemView.findViewById(R.id.textPtosDisponibles);
        }
    }
}
