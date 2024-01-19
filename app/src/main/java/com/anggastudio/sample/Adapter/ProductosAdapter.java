package com.anggastudio.sample.Adapter;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Productos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    final OnItemClickListener listener;
    List<Productos> productosList;


    /** @BUSCADOR:Productos **/
    ArrayList<Productos> listaOriginal;

    /** @LISTADO:TodoProductos  **/
    public void setProductos(List<Productos> productosFiltrados) {
        this.productosList = productosFiltrados;
    }

    /** @SELECCIONAR:ProductosAgregados **/
    public interface  OnItemClickListener{
        int onItemClick(Productos item, boolean isSelected);
    }

    public ProductosAdapter(List<Productos> productosList, OnItemClickListener listener){
        this.productosList = productosList;
        this.listener      = listener;

        /** @BUSCADOR:Productos **/
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(productosList);
    }

    /** @SELECCIONAR:DeseleccionarProducto **/
    private OnDeseleccionarProductoListener deselectionListener;

    public void setOnDeseleccionarProductoListener(OnDeseleccionarProductoListener listener) {
        this.deselectionListener = listener;
    }

    public interface OnDeseleccionarProductoListener {
        void onDeseleccionarProducto(Productos item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Productos productos = productosList.get(position);

        /** Longitud del nombre del producto **/
        String nombreProducto = productos.getNombre();
        int maxCaracteres = 28;

        if (nombreProducto.length() > maxCaracteres) {
            nombreProducto = nombreProducto.substring(0, maxCaracteres - 3) + "...";
        }
        holder.nommbreProducto.setText(nombreProducto);

        holder.precioProducto.setText(String.valueOf(String.format("%.2f",productos.getPrecio())));
        holder.cantidadProducto.setText(String.valueOf(productos.getCantidad()) + " disponible");

        /** Imagen de PRODUCTOS */
            String nombreImagen = productos.getImagen();

        File file = new File("/storage/emulated/0/appSven/" + nombreImagen);
        String rutaImagen="/storage/emulated/0/appSven/" + nombreImagen;

        if (!file.exists()) {
            rutaImagen = "/storage/emulated/0/appSven/sinfoto.jpg";
        }

        Uri imagenProd = Uri.parse("file://" + rutaImagen);
        holder.imageProducto.setImageURI(imagenProd);

        /** @STOCK:EstadoProducto **/
        if (productos.getCantidad() > 0) {
            holder.cantidadProducto.setTextColor(Color.parseColor("#061240"));
        } else {
            holder.cantidadProducto.setTextColor(Color.RED);
        }

        /** @SELECCIONAR:EstadoProdcuto **/
        boolean isSelected = !productos.isSeleccionado();
        if(isSelected){
            holder.card_Productos.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }else{
            holder.card_Productos.setCardBackgroundColor(Color.parseColor("#FFC107"));
        }

        holder.card_Productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View  item) {
                /** @SELECCIONAR:Estado **/
                if (productos.getCantidad() > 0) {
                    listener.onItemClick(productos, isSelected);
                    productos.setSeleccionado(isSelected);
                    if (!isSelected && deselectionListener != null) {
                        deselectionListener.onDeseleccionarProducto(productos);
                    }
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public void filtrado(final String txtBuscar, boolean buscarPorCodigoDeBarras) {

        productosList.clear();

        if (txtBuscar.isEmpty()) {
            productosList.addAll(listaOriginal);
        } else {
            String txtBuscarLowerCase = txtBuscar.toLowerCase();

            if (buscarPorCodigoDeBarras) {
                // Filtrar por código de barras
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<Productos> filteredList = listaOriginal.stream()
                            .filter(i -> i.getCodigo().toLowerCase().contains(txtBuscarLowerCase))
                            .collect(Collectors.toList());
                    productosList.addAll(filteredList);
                } else {
                    for (Productos c : listaOriginal) {
                        if (c.getCodigo().toLowerCase().contains(txtBuscar.toLowerCase())) {
                            productosList.add(c);
                        }
                    }
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<Productos> filteredList = listaOriginal.stream()
                            .filter(i -> i.getNombre().toLowerCase().contains(txtBuscarLowerCase))
                            .collect(Collectors.toList());
                    productosList.addAll(filteredList);
                } else {
                    for (Productos c : listaOriginal) {
                        if (c.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                            productosList.add(c);
                        }
                    }
                }
            }

        }

        notifyDataSetChanged();
    }

    public void filtrarMuyVendidos(boolean soloMuyVendidos) {
        List<Productos> productosFiltrados = new ArrayList<>();

        for (Productos producto : listaOriginal) {
            if (soloMuyVendidos && producto.getProdVendido()) {
                productosFiltrados.add(producto);
            } else if (!soloMuyVendidos) {
                productosFiltrados.add(producto);
            }
        }

        productosList.clear();
        productosList.addAll(productosFiltrados);
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView card_Productos;
        private TextView nommbreProducto;
        private TextView precioProducto;
        private TextView cantidadProducto;
        private ImageView imageProducto;

        public ViewHolder(@NonNull View itemView){

            super(itemView);
            card_Productos   = itemView.findViewById(R.id.card_Productos);
            nommbreProducto   = itemView.findViewById(R.id.nommbreProducto);
            precioProducto    = itemView.findViewById(R.id.precioProducto);
            cantidadProducto  = itemView.findViewById(R.id.cantidadProducto);
            imageProducto     = itemView.findViewById(R.id.imageProducto);
        }
    }
}
