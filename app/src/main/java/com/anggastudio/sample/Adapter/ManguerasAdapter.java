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
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import java.util.List;

public class ManguerasAdapter extends RecyclerView.Adapter<ManguerasAdapter.ViewHolder> {

    final ManguerasAdapter.OnItemClickListener listener;
    private List<Mangueras> mangueraList;
    private Context context;
    private int selectedItem;

    public interface  OnItemClickListener{
        int onItemClick(Mangueras item);
    }

    public void setMangueraList(List<Mangueras> manguera){
        mangueraList = manguera;
        //notifyDataSetChanged();
    }

    public ManguerasAdapter(List<Mangueras> mangueraList, Context context, ManguerasAdapter.OnItemClickListener listener){
        this.mangueraList = mangueraList;
        this.context    = context;
        this.listener   = listener;
        selectedItem    = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_mangueras,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManguerasAdapter.ViewHolder holder, int position) {

        Mangueras mangueras = mangueraList.get(position);

        holder.manguera_ID.setText(mangueraList.get(position).getMangueraID());
        holder.descripcion.setText(mangueraList.get(position).getDescripcion());

        if (selectedItem == position) {
            if (mangueras.getDescripcion().equals("DIESEL DB5")){

                holder.card_Manguera.setCardBackgroundColor(Color.parseColor("#494242"));

            } else if (mangueras.getDescripcion().equals("G-REGULAR")){

                holder.card_Manguera.setCardBackgroundColor(Color.parseColor("#50B955"));

            } else if (mangueras.getDescripcion().equals("G-PREMIUM")){

                holder.card_Manguera.setCardBackgroundColor(Color.parseColor("#3A43FF"));

            }else if (mangueras.getDescripcion().equals("GLP")){

                holder.card_Manguera.setCardBackgroundColor(Color.parseColor("#DF8600"));

            }else {
                holder.card_Manguera.setCardBackgroundColor(Color.parseColor("#35B596"));
            }
        }else {
            holder.card_Manguera.setCardBackgroundColor(context.getResources().getColor(R.color.colorHumo));
        }

        holder.card_Manguera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);

                listener.onItemClick(mangueras);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mangueraList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView card_Manguera;
        private TextView manguera_ID;
        private TextView descripcion;

        public ViewHolder(@NonNull View itemView){

            super(itemView);
            card_Manguera  = itemView.findViewById(R.id.card_Manguera);
            manguera_ID    = itemView.findViewById(R.id.manguera_ID);
            descripcion    = itemView.findViewById(R.id.descripcion);

        }

    }
}
