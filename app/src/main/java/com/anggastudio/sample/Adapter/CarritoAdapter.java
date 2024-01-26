package com.anggastudio.sample.Adapter;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;

import java.util.List;
import java.util.Map;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<Articulo> articuloEnCarrito;

    private Map<String, Integer> cantidadesSeleccionadas;
    private Map<String, Double> nuevosPrecios;

    private TextView totalmontoCar;
    private Dialog modalCarrito;
    private CardView btncarritocompra;
    private LinearLayout linearLayoutRecyclerArticulo;

    public interface OnArticuloEliminadoListener {
        void onArticuloEliminado(List<Articulo> articuloEnCarrito);
    }

    private OnArticuloEliminadoListener articuloEliminadoListener;

    public void setOnProductoEliminadoListener(OnArticuloEliminadoListener listener) {
        this.articuloEliminadoListener = listener;
    }

    public CarritoAdapter(List<Articulo> articuloEnCarrito,Map<String, Integer> cantidadesSeleccionadas, Map<String, Double> nuevosPrecios, TextView totalmontoCar, Dialog modalCarrito, CardView btncarritocompra, LinearLayout linearLayoutRecyclerArticulo) {
        this.articuloEnCarrito       = articuloEnCarrito;
        this.cantidadesSeleccionadas = cantidadesSeleccionadas;
        this.nuevosPrecios           = nuevosPrecios;
        this.totalmontoCar           = totalmontoCar;
        this.modalCarrito            = modalCarrito;
        this.btncarritocompra        = btncarritocompra;
        this.linearLayoutRecyclerArticulo = linearLayoutRecyclerArticulo;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {

        Articulo articulo = articuloEnCarrito.get(position);

        holder.tvNombre.setText(articulo.getArticuloDS1());
        holder.tvPrecio.setText(String.valueOf(String.format("%.2f",articulo.getPrecio_Venta())));

        /** @INTERACTUAR:Datos **/
        TextView tvCantidad      = holder.itemView.findViewById(R.id.tv_cantidad);
        TextView tvCantidadTotal = holder.itemView.findViewById(R.id.tv_totalprecio_carrito);
        ImageButton btnSumar     = holder.itemView.findViewById(R.id.btn_sumar);
        ImageButton btnRestar    = holder.itemView.findViewById(R.id.btn_restar);
        ImageButton btnEliminar  = holder.itemView.findViewById(R.id.btn_eliminar);

        String idArticulo = articulo.getArticuloID();

        /** Actualizar Seleccion de Cantidades **/
       if (cantidadesSeleccionadas.containsKey(idArticulo)) {

           int cantidadSeleccionada = cantidadesSeleccionadas.get(idArticulo);
           tvCantidad.setText(String.valueOf(cantidadSeleccionada));

           btnRestar.setEnabled(cantidadSeleccionada > 1);
           btnSumar.setEnabled(cantidadSeleccionada < articulo.getStock_Actual());

           btnRestar.setBackgroundTintList(ColorStateList.valueOf(cantidadSeleccionada > 1 ? Color.parseColor("#FFC107") : Color.parseColor("#A19E9E")));
           btnSumar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));

       }else {
           tvCantidad.setText("1");
           cantidadesSeleccionadas.put(articulo.getArticuloID(), 1);
           nuevosPrecios.put(articulo.getArticuloID(), articulo.getPrecio_Venta());
           btnSumar.setEnabled(true);
           btnRestar.setEnabled(false);
           btnSumar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
           btnRestar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A19E9E")));
       }

        /** Calcular Total Precio **/
        double totalPrecio = calcularTotal();
        actualizarTotalEnInterfaz(totalPrecio);

        /** Actualizar Precios **/
        double nuevoPrecio = nuevosPrecios.containsKey(idArticulo) ? nuevosPrecios.get(idArticulo) : articulo.getPrecio_Venta();
        tvCantidadTotal.setText(String.format("%.2f", nuevoPrecio));

        /**
         * @SUMAR_CANTIDAD
         */
        btnSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                String cantidadActualTexto = tvCantidad.getText().toString();

                if (!cantidadActualTexto.isEmpty()) {

                    int cantidadActual = Integer.parseInt(cantidadActualTexto);

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < articuloEnCarrito.size()) {

                            Articulo articulo  = articuloEnCarrito.get(adapterPosition);
                            double stockDisponible = articulo.getStock_Actual();

                            if (cantidadActual < stockDisponible) {

                                int nuevaCantidad = cantidadActual + 1;

                                /** Calcular Cantidad Total - Precio**/
                                cantidadesSeleccionadas.put(articulo.getArticuloID(), nuevaCantidad);
                                nuevosPrecios.put(articulo.getArticuloID(), articulo.getPrecio_Venta() * nuevaCantidad);
                                tvCantidad.setText(String.valueOf(nuevaCantidad));
                                tvCantidadTotal.setText(String.valueOf(String.format("%.2f", articulo.getPrecio_Venta() * nuevaCantidad)));

                                /** Calcular Total Precio **/
                                double totalPrecio = calcularTotal();
                                actualizarTotalEnInterfaz(totalPrecio);

                                /** Activar Boton Restar **/
                                if (nuevaCantidad > 1) {
                                    btnRestar.setEnabled(true);
                                    btnRestar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                                }

                            } else {
                                Toast.makeText(tvCantidad.getContext(), "No hay más productos disponibles", Toast.LENGTH_SHORT).show();
                            }

                    }
                } else {
                    tvCantidad.setText("1");
                }
            }
        });

        /**
         * @RESTAR_CANTIDAD
         */
        btnRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int adapterPosition = holder.getAdapterPosition();

                String cantidadActualTexto = tvCantidad.getText().toString();

                if (!cantidadActualTexto.isEmpty()) {
                    int cantidadActual = Integer.parseInt(cantidadActualTexto);

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < articuloEnCarrito.size()) {

                            Articulo articulo = articuloEnCarrito.get(adapterPosition);

                            if (cantidadActual > 1) {
                                int nuevaCantidad = cantidadActual - 1;

                                /** Calcular Cantidad Total - Precio**/
                                cantidadesSeleccionadas.put(articulo.getArticuloID(), nuevaCantidad);
                                nuevosPrecios.put(articulo.getArticuloID(), articulo.getPrecio_Venta() * nuevaCantidad);
                                tvCantidad.setText(String.valueOf(nuevaCantidad));
                                tvCantidadTotal.setText(String.valueOf(String.format("%.2f", articulo.getPrecio_Venta() * nuevaCantidad)));

                                /** Calcular Total Precio **/
                                double totalPrecio = calcularTotal();
                                actualizarTotalEnInterfaz(totalPrecio);

                                /** Desactivar Boton Restar **/
                                if (nuevaCantidad  == 1) {
                                    btnRestar.setEnabled(false);
                                    btnRestar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A19E9E")));
                                }

                            }else {
                                tvCantidad.setText("1");
                            }
                        }
                    }
            }
        });

        /**
         * @ELIMINAR_CANTIDAD
         */
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int adapterPosition = holder.getAdapterPosition();

                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < articuloEnCarrito.size()) {

                    Articulo articuloEliminado = articuloEnCarrito.get(adapterPosition);
                    articuloEnCarrito.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                    /** Restablecer Estado Producto Eliminido **/
                    articuloEliminado.setSeleccionado(false);

                    /** Calcular Total Precio **/
                    double totalPrecio = calcularTotal();
                    actualizarTotalEnInterfaz(totalPrecio);

                    /** Actualizar Total Precio **/
                    String idProducto = articuloEliminado.getArticuloID();
                    cantidadesSeleccionadas.remove(idProducto);
                    nuevosPrecios.remove(idProducto);
                    /* cantidadesSeleccionadas.put(idProducto, 1);
                    nuevosPrecios.put(producto.getId(), producto.getPrecio());*/

                    /** Eliminar Producto **/
                    if (articuloEliminadoListener != null) {
                        articuloEliminadoListener.onArticuloEliminado(articuloEnCarrito);
                    }

                    /** Ocultar boton y cerrar modal Carrito de Compra **/
                    if (articuloEnCarrito.isEmpty()) {
                        modalCarrito.dismiss();
                        btncarritocompra.setVisibility(View.GONE);

                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerArticulo.getLayoutParams();
                        layoutParams.bottomMargin = 0;
                        linearLayoutRecyclerArticulo.setLayoutParams(layoutParams);

                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return articuloEnCarrito.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvPrecio;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre        = itemView.findViewById(R.id.tv_nombre_carrito);
            tvPrecio        = itemView.findViewById(R.id.tv_precio_carrito);
        }
    }

    private double calcularTotal() {
        double totalPrecio = 0.0;
        for (Articulo articulo : articuloEnCarrito) {
            String idArticulo = articulo.getArticuloID();
            if (cantidadesSeleccionadas.containsKey(idArticulo)) {
                Integer nuevaCantidad = cantidadesSeleccionadas.get(idArticulo);
                totalPrecio += articulo.getPrecio_Venta() * nuevaCantidad;
            }
        }
        return totalPrecio;
    }

    private void actualizarTotalEnInterfaz(double total) {
        if (totalmontoCar != null) {
            totalmontoCar.setText(String.format("%.2f", total));
        }
    }
}
