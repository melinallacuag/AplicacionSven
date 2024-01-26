package com.anggastudio.sample.Adapter;

import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ViewHolder>{

    final ArticuloAdapter.OnItemClickListener listener;
    List<Articulo> articuloList;

    ArrayList<Articulo> listaOriginal;

    /** @LISTADO:TodoProductos  **/
    public void setProductos(List<Articulo> productosFiltrados) {
        this.articuloList = productosFiltrados;
    }

    /** @SELECCIONAR:ProductosAgregados **/
    public interface  OnItemClickListener{
        int onItemClick(Articulo item, boolean isSelected);
    }

    public ArticuloAdapter(List<Articulo> articuloList, ArticuloAdapter.OnItemClickListener listener){
        this.articuloList = articuloList;
        this.listener      = listener;

        /** @BUSCADOR:Productos **/
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(articuloList);
    }

    /** @SELECCIONAR:DeseleccionarProducto **/
    private ArticuloAdapter.OnDeseleccionarProductoListener deselectionListener;

    public void setOnDeseleccionarArticuloListener(ArticuloAdapter.OnDeseleccionarProductoListener listener) {
        this.deselectionListener = listener;
    }

    public interface OnDeseleccionarProductoListener {
        void onDeseleccionarProducto(Articulo item);
    }

    @NonNull
    @Override
    public ArticuloAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto,parent,false);
        return new ArticuloAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloAdapter.ViewHolder holder, int position) {
        Articulo articulo = articuloList.get(position);

        /** Longitud del nombre del producto **/
        String nombreProducto = articulo.getArticuloDS1();
        int maxCaracteres = 28;

        if (nombreProducto.length() > maxCaracteres) {
            nombreProducto = nombreProducto.substring(0, maxCaracteres - 3) + "...";
        }
        holder.nommbreProducto.setText(nombreProducto);

        holder.precioProducto.setText(String.valueOf(String.format("%.2f",articulo.getPrecio_Venta())));
        holder.cantidadProducto.setText(String.valueOf(articulo.getStock_Actual()) + " disponible");

        /** Imagen de PRODUCTOS */
        String rutaImagen = "/storage/emulated/0/appSven/";

        if (!TextUtils.isEmpty(articulo.getImagen_Ruta())) {
            rutaImagen += articulo.getImagen_Ruta();
            File file = new File(rutaImagen);
            if (!file.exists()) {
                rutaImagen = "/storage/emulated/0/appSven/sinarticulo.jpg";
            }
        } else {
            rutaImagen += "sinarticulo.jpg";
        }

        Uri imagenProd = Uri.parse("file://" + rutaImagen);
        holder.imageProducto.setImageURI(imagenProd);

        /** @STOCK:EstadoProducto **/
        if (articulo.getStock_Actual() > 0) {
            holder.cantidadProducto.setTextColor(Color.parseColor("#061240"));
        } else {
            holder.cantidadProducto.setTextColor(Color.RED);
        }

        /** @SELECCIONAR:EstadoProdcuto **/
        boolean isSelected = !articulo.isSeleccionado();
        if(isSelected){
            holder.card_Productos.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }else{
            holder.card_Productos.setCardBackgroundColor(Color.parseColor("#FFC107"));
        }

        holder.card_Productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View  item) {
                /** @SELECCIONAR:Estado **/
                if (articulo.getStock_Actual() > 0) {
                    listener.onItemClick(articulo, isSelected);
                    articulo.setSeleccionado(isSelected);
                    if (!isSelected && deselectionListener != null) {
                        deselectionListener.onDeseleccionarProducto(articulo);
                    }
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articuloList.size();
    }

    public void filtrado(final String txtBuscar, boolean buscarPorCodigoDeBarras) {

        articuloList.clear();

        if (txtBuscar.isEmpty()) {
            articuloList.addAll(listaOriginal);
        } else {
            String txtBuscarLowerCase = txtBuscar.toLowerCase();

            if (buscarPorCodigoDeBarras) {
                // Filtrar por código de barras
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<Articulo> filteredList = listaOriginal.stream()
                            .filter(i -> i.getArticuloID().toLowerCase().contains(txtBuscarLowerCase))
                            .collect(Collectors.toList());
                    articuloList.addAll(filteredList);
                } else {
                    for (Articulo c : listaOriginal) {
                        if (c.getArticuloID().toLowerCase().contains(txtBuscar.toLowerCase())) {
                            articuloList.add(c);
                        }
                    }
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<Articulo> filteredList = listaOriginal.stream()
                            .filter(i -> i.getArticuloDS1().toLowerCase().contains(txtBuscarLowerCase))
                            .collect(Collectors.toList());
                    articuloList.addAll(filteredList);
                } else {
                    for (Articulo c : listaOriginal) {
                        if (c.getArticuloDS1().toLowerCase().contains(txtBuscar.toLowerCase())) {
                            articuloList.add(c);
                        }
                    }
                }
            }

        }

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
