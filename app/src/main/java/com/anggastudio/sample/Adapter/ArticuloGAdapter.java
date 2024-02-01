package com.anggastudio.sample.Adapter;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticuloGAdapter extends RecyclerView.Adapter<ArticuloGAdapter.ViewHolder>{

    private TextView totalmontoCar;

    List<Articulo> articuloGList;

    final ArticuloGAdapter.OnItemClickListener listener;

    public interface  OnItemClickListener{
        void onItemClick(Articulo item);
    }

    private Map<String, Integer> cantidadesSeleccionadas;
    private Map<String, Double> nuevosPrecios;

    public ArticuloGAdapter(List<Articulo> articuloGList, Map<String, Integer> cantidadesSeleccionadas, Map<String, Double> nuevosPrecios,TextView totalmontoCar, ArticuloGAdapter.OnItemClickListener listener){
        this.articuloGList           = articuloGList;
        this.listener                = listener;
        this.totalmontoCar           = totalmontoCar;
        this.cantidadesSeleccionadas = cantidadesSeleccionadas;
        this.nuevosPrecios           = nuevosPrecios;
    }

    @NonNull
    @Override
    public ArticuloGAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pgratuito,parent,false);
        return new ArticuloGAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloGAdapter.ViewHolder holder, int position) {

        Articulo articuloG = articuloGList.get(position);

        holder.nommbreProducto.setText(articuloGList.get(position).getArticuloDS1());

        holder.precioProducto.setText(String.valueOf(String.format("%.2f",articuloGList.get(position).getPrecio_Venta())));
        holder.tv_cantidad.setText("0");

        String idArticulo = articuloG.getArticuloID();

        GlobalInfo.getMarketMontoTotal = calcularTotal();
        actualizarTotalEnInterfaz(GlobalInfo.getMarketMontoTotal);

        //GlobalInfo.getMarketPrecio = nuevosPrecios.containsKey(idArticulo) ? nuevosPrecios.get(idArticulo) : articuloG.getPrecio_Venta();
        GlobalInfo.getMarketPrecio = nuevosPrecios.containsKey(idArticulo) ? nuevosPrecios.get(idArticulo) : 0.0;
        holder.tvCantidadTotal.setText(String.format("%.2f", GlobalInfo.getMarketPrecio));

        holder.btnSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                String cantidadActualTexto = holder.tv_cantidad.getText().toString();

                if (!cantidadActualTexto.isEmpty()) {

                    int cantidadActual = Integer.parseInt(cantidadActualTexto);

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < articuloGList.size()) {

                        Articulo articuloG  = articuloGList.get(adapterPosition);
                        double stockDisponible = articuloG.getStock_Actual();

                        if (cantidadActual < stockDisponible) {

                            int nuevaCantidad = cantidadActual + 1;

                            /** Calcular Cantidad Total - Precio**/
                            cantidadesSeleccionadas.put(articuloG.getArticuloID(), nuevaCantidad);
                            nuevosPrecios.put(articuloG.getArticuloID(), articuloG.getPrecio_Venta() * nuevaCantidad);
                            holder.tv_cantidad.setText(String.valueOf(nuevaCantidad));
                            holder.tvCantidadTotal.setText(String.valueOf(String.format("%.2f", articuloG.getPrecio_Venta() * nuevaCantidad)));

                            /** Calcular Total Precio **/
                            GlobalInfo.getMarketMontoTotal = calcularTotal();
                            actualizarTotalEnInterfaz(GlobalInfo.getMarketMontoTotal);

                            /** Activar Boton Restar **/
                            if (nuevaCantidad > 0) {
                                holder.btnRestar.setEnabled(true);
                                holder.btnRestar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                            }

                        } else {
                            Toast.makeText(holder.tv_cantidad.getContext(), "No hay más productos disponibles", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    holder.tv_cantidad.setText("0");
                }
            }
        });

        holder.btnRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                String cantidadActualTexto = holder.tv_cantidad.getText().toString();

                if (!cantidadActualTexto.isEmpty()) {
                    int cantidadActual = Integer.parseInt(cantidadActualTexto);

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < articuloGList.size()) {

                        Articulo articuloG = articuloGList.get(adapterPosition);

                        if (cantidadActual > 0) {
                            int nuevaCantidad = cantidadActual - 1;

                            /** Calcular Cantidad Total - Precio**/
                            cantidadesSeleccionadas.put(articuloG.getArticuloID(), nuevaCantidad);
                            nuevosPrecios.put(articuloG.getArticuloID(), articuloG.getPrecio_Venta() * nuevaCantidad);
                            holder.tv_cantidad.setText(String.valueOf(nuevaCantidad));
                            holder.tvCantidadTotal.setText(String.valueOf(String.format("%.2f", articuloG.getPrecio_Venta() * nuevaCantidad)));

                            /** Calcular Total Precio **/
                            GlobalInfo.getMarketMontoTotal = calcularTotal();
                            actualizarTotalEnInterfaz(GlobalInfo.getMarketMontoTotal);

                            /** Desactivar Boton Restar **/
                            if (nuevaCantidad == 0) {
                                holder.btnRestar.setEnabled(false);
                                holder.btnRestar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A19E9E")));
                            }

                        }else {
                            holder.tv_cantidad.setText("0");
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return articuloGList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView card_Productos;
        private TextView nommbreProducto;
        private TextView precioProducto;
        private TextView tv_cantidad;
        private ImageButton btnSumar;
        private ImageButton btnRestar;
        private TextView tvCantidadTotal;

        public ViewHolder(@NonNull View itemView){

            super(itemView);
            card_Productos    = itemView.findViewById(R.id.card_Productos);
            nommbreProducto   = itemView.findViewById(R.id.tv_nombre_carrito);
            precioProducto    = itemView.findViewById(R.id.tv_precio_carrito);
            tv_cantidad       = itemView.findViewById(R.id.tv_cantidad);
            btnSumar          = itemView.findViewById(R.id.btn_sumar);
            btnRestar         = itemView.findViewById(R.id.btn_restar);
            tvCantidadTotal   = itemView.findViewById(R.id.tv_totalprecio_carrito);

        }
    }

    private double calcularTotal() {
        GlobalInfo.getMarketMontoTotal = 0.0;
        for (Articulo articulo : articuloGList) {
            String idArticulo = articulo.getArticuloID();
            if (cantidadesSeleccionadas.containsKey(idArticulo)) {
                Integer nuevaCantidad = cantidadesSeleccionadas.get(idArticulo);
                GlobalInfo.getMarketMontoTotal += articulo.getPrecio_Venta() * nuevaCantidad;
            }
        }
        return GlobalInfo.getMarketMontoTotal ;
    }

    private void actualizarTotalEnInterfaz(double total) {
        if (totalmontoCar != null) {
            totalmontoCar.setText(String.format("%.2f", total));
        }
    }
}
