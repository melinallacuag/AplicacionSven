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
import com.anggastudio.sample.WebApiSVEN.Models.Tipotarjeta;

import java.util.ArrayList;

public class TipoTarjetaAdapter  extends ArrayAdapter<Tipotarjeta> {
    private Context context;
    private ArrayList<Tipotarjeta> tipotarjeta;
    public Resources res;
    Tipotarjeta currRowVal = null;
    LayoutInflater inflater;

    public TipoTarjetaAdapter(@NonNull Context context, int textViewResourceId,ArrayList<Tipotarjeta> tipotarjeta,Resources resLocal) {
        super(context, textViewResourceId, tipotarjeta);
        this.context = context;
        this.tipotarjeta = tipotarjeta;
        this.res = resLocal;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        return tipotarjeta.size();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.item, parent, false);
        currRowVal = null;
        currRowVal = (Tipotarjeta) tipotarjeta.get(position);
        TextView label = (TextView) row.findViewById(R.id.spinnerItem);

        label.setText(currRowVal.getNombreTarjeta());

        return row;
    }

}
