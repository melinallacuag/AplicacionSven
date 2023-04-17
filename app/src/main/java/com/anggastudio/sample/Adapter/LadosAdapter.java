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
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import java.util.List;


public class LadosAdapter extends RecyclerView.Adapter<LadosAdapter.ViewHolder>{

    final LadosAdapter.OnItemClickListener listener;
    private List<Lados> ladosList;
    private Context context;
    private int selectedItem;

    public interface  OnItemClickListener{
        int onItemClick(Lados item);
    }

    public LadosAdapter(List<Lados> caraList, Context context, LadosAdapter.OnItemClickListener listener){

        this.ladosList  = caraList;
        this.context   = context;
        this.listener  = listener;
        selectedItem   = -1;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lados,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Lados lados = ladosList.get(position);

        holder.nro_Lado.setText(ladosList.get(position).getNroLado());

        if (selectedItem == position) {
            holder.card_Lado.setCardBackgroundColor(Color.parseColor("#5EA7DE"));
        }else {
            holder.card_Lado.setCardBackgroundColor(context.getResources().getColor(R.color.colorHumo));
        }

        holder.card_Lado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);

                listener.onItemClick(lados);

            }
        });
    }

    @Override
    public int getItemCount() {
        return ladosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView card_Lado;
        private TextView nro_Lado;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            card_Lado = itemView.findViewById(R.id.card_Lado);
            nro_Lado  = itemView.findViewById(R.id.nro_Lado);
        }

    }
}