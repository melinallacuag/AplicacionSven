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
import com.anggastudio.sample.WebApiSVEN.Models.Productos;

import java.util.List;
import java.util.Map;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<Productos> productosEnCarrito;

    private Map<Integer, Integer> cantidadesSeleccionadas;
    private Map<Integer, Double> nuevosPrecios;

    private TextView totalmontoCar;
    private Dialog modalCarrito;
    private CardView btncarritocompra;
    private LinearLayout linearLayoutRecyclerProducto;

    public interface OnProductoEliminadoListener {
        void onProductoEliminado(List<Productos> productosEnCarrito);
    }

    private OnProductoEliminadoListener productoEliminadoListener;

    public void setOnProductoEliminadoListener(OnProductoEliminadoListener listener) {
        this.productoEliminadoListener = listener;
    }

    public CarritoAdapter(List<Productos> productosEnCarrito,Map<Integer, Integer> cantidadesSeleccionadas, Map<Integer, Double> nuevosPrecios, TextView totalmontoCar, Dialog modalCarrito, CardView btncarritocompra, LinearLayout linearLayoutRecyclerProducto) {
        this.productosEnCarrito      = productosEnCarrito;
        this.cantidadesSeleccionadas = cantidadesSeleccionadas;
        this.nuevosPrecios           = nuevosPrecios;
        this.totalmontoCar           = totalmontoCar;
        this.modalCarrito            = modalCarrito;
        this.btncarritocompra        = btncarritocompra;
        this.linearLayoutRecyclerProducto = linearLayoutRecyclerProducto;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {

        Productos producto = productosEnCarrito.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(String.valueOf(String.format("%.2f",producto.getPrecio())));

        /** @INTERACTUAR:Datos **/
        TextView tvCantidad      = holder.itemView.findViewById(R.id.tv_cantidad);
        TextView tvCantidadTotal = holder.itemView.findViewById(R.id.tv_totalprecio_carrito);
        ImageButton btnSumar     = holder.itemView.findViewById(R.id.btn_sumar);
        ImageButton btnRestar    = holder.itemView.findViewById(R.id.btn_restar);
        ImageButton btnEliminar  = holder.itemView.findViewById(R.id.btn_eliminar);

        Integer idProducto = producto.getId();

        /** Actualizar Seleccion de Cantidades **/
       if (cantidadesSeleccionadas.containsKey(idProducto)) {

           int cantidadSeleccionada = cantidadesSeleccionadas.get(idProducto);
           tvCantidad.setText(String.valueOf(cantidadSeleccionada));

           btnRestar.setEnabled(cantidadSeleccionada > 1);
           btnSumar.setEnabled(cantidadSeleccionada < producto.getCantidad());

           btnRestar.setBackgroundTintList(ColorStateList.valueOf(cantidadSeleccionada > 1 ? Color.parseColor("#FFC107") : Color.parseColor("#A19E9E")));
           btnSumar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));

       }else {
           tvCantidad.setText("1");
           cantidadesSeleccionadas.put(producto.getId(), 1);
           nuevosPrecios.put(producto.getId(), producto.getPrecio());
           btnSumar.setEnabled(true);
           btnRestar.setEnabled(false);
           btnSumar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
           btnRestar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A19E9E")));
       }

        /** Calcular Total Precio **/
        double totalPrecio = calcularTotal();
        actualizarTotalEnInterfaz(totalPrecio);

        /** Actualizar Precios **/
        double nuevoPrecio = nuevosPrecios.containsKey(idProducto) ? nuevosPrecios.get(idProducto) : producto.getPrecio();
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

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < productosEnCarrito.size()) {

                            Productos producto  = productosEnCarrito.get(adapterPosition);
                            int stockDisponible = producto.getCantidad();

                            if (cantidadActual < stockDisponible) {

                                int nuevaCantidad = cantidadActual + 1;

                                /** Calcular Cantidad Total - Precio**/
                                cantidadesSeleccionadas.put(producto.getId(), nuevaCantidad);
                                nuevosPrecios.put(producto.getId(), producto.getPrecio() * nuevaCantidad);
                                tvCantidad.setText(String.valueOf(nuevaCantidad));
                                tvCantidadTotal.setText(String.valueOf(String.format("%.2f", producto.getPrecio() * nuevaCantidad)));

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

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < productosEnCarrito.size()) {

                            Productos producto = productosEnCarrito.get(adapterPosition);

                            if (cantidadActual > 1) {
                                int nuevaCantidad = cantidadActual - 1;

                                /** Calcular Cantidad Total - Precio**/
                                cantidadesSeleccionadas.put(producto.getId(), nuevaCantidad);
                                nuevosPrecios.put(producto.getId(), producto.getPrecio() * nuevaCantidad);
                                tvCantidad.setText(String.valueOf(nuevaCantidad));
                                tvCantidadTotal.setText(String.valueOf(String.format("%.2f", producto.getPrecio() * nuevaCantidad)));

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

                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < productosEnCarrito.size()) {

                    Productos productoEliminado = productosEnCarrito.get(adapterPosition);
                    productosEnCarrito.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                    /** Restablecer Estado Producto Eliminido **/
                    productoEliminado.setSeleccionado(false);

                    /** Calcular Total Precio **/
                    double totalPrecio = calcularTotal();
                    actualizarTotalEnInterfaz(totalPrecio);

                    /** Actualizar Total Precio **/
                    Integer idProducto = productoEliminado.getId();
                    cantidadesSeleccionadas.remove(idProducto);
                    nuevosPrecios.remove(idProducto);
                    /* cantidadesSeleccionadas.put(idProducto, 1);
                    nuevosPrecios.put(producto.getId(), producto.getPrecio());*/

                    /** Eliminar Producto **/
                    if (productoEliminadoListener != null) {
                        productoEliminadoListener.onProductoEliminado(productosEnCarrito);
                    }

                    /** Ocultar boton y cerrar modal Carrito de Compra **/
                    if (productosEnCarrito.isEmpty()) {
                        modalCarrito.dismiss();
                        btncarritocompra.setVisibility(View.GONE);

                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayoutRecyclerProducto.getLayoutParams();
                        layoutParams.bottomMargin = 0;
                        linearLayoutRecyclerProducto.setLayoutParams(layoutParams);

                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productosEnCarrito.size();
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
        for (Productos productos : productosEnCarrito) {
            Integer idProducto = productos.getId();
            if (cantidadesSeleccionadas.containsKey(idProducto)) {
                Integer nuevaCantidad = cantidadesSeleccionadas.get(idProducto);
                totalPrecio += productos.getPrecio() * nuevaCantidad;
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
