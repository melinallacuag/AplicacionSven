package com.anggastudio.sample.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.anggastudio.sample.R;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;

import java.util.ArrayList;

public class TipoPagoAdapter extends ArrayAdapter<TipoPago> {

    private Context context;
    private ArrayList<TipoPago> card;
    public Resources res;
    TipoPago currRowVal = null;
    LayoutInflater inflater;


    public TipoPagoAdapter(@NonNull Context context, int textViewResourceId, ArrayList<TipoPago> card, Resources resLocal) {
        super(context, textViewResourceId, card);
        this.context = context;
        this.card    = card;
        this.res     = resLocal;
        inflater      = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public int getCount() {
        return card.size();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.item, parent, false);
        currRowVal = null;
        currRowVal = (TipoPago) card.get(position);
        TextView label = (TextView) row.findViewById(R.id.spinnerItem);

        label.setText(currRowVal.getNames());

        return row;
    }


}
