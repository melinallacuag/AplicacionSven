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
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;

import java.util.List;
import java.util.Locale;

public class VTipoPagoAdapter extends RecyclerView.Adapter<VTipoPagoAdapter.ViewHolder>{

    public List<VTipoPago> vTipoPagoList;
    private Context context;

    public VTipoPagoAdapter(List<VTipoPago> vTipoPagoList, Context context){
        this.vTipoPagoList = vTipoPagoList;
        this.context       = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardventatipopago,parent,false);
        return new VTipoPagoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VTipoPagoAdapter.ViewHolder holder, int position) {
        holder.textventatipopago.setText(vTipoPagoList.get(position).getNames());
        holder.textventatipopagomonto.setText(String.valueOf(String.format(Locale.getDefault(), "%,.2f" ,vTipoPagoList.get(position).getSoles())));

    }

    @Override
    public int getItemCount() {
        return vTipoPagoList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private TextView textventatipopago;
        private TextView textventatipopagomonto;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardView                 = itemView.findViewById(R.id.cardmanguera);
            textventatipopago        = itemView.findViewById(R.id.textventatipopago);
            textventatipopagomonto   = itemView.findViewById(R.id.textventatipopagomonto);

        }
    }
}
