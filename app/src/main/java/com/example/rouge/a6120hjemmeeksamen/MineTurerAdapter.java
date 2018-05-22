package com.example.rouge.a6120hjemmeeksamen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MineTurerAdapter extends RecyclerView.Adapter<MineTurerAdapter.ViewHolder> {

    private Context context;
    private List<Tur> list;

    public MineTurerAdapter(Context context, List<Tur> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_turer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Tur tur= list.get(position);

        // Sjekk for å velge hvilket ikon som skal vises frem, og hvor mange ledige plasser det er i bilen
        // Jeg sammenligner med == fordi databasen returnerer "null" hvis passasjerfeltet er tomt
        if(tur.getPassasjer1() == "null") {
            tur.setLedigeSeter(4);
            holder.seter.setImageResource(R.drawable.fireledige);
        } else if(tur.getPassasjer2() == "null") {
            tur.setLedigeSeter(3);
            holder.seter.setImageResource(R.drawable.treledige);
        } else if(tur.getPassasjer3() == "null") {
            tur.setLedigeSeter(2);
            holder.seter.setImageResource(R.drawable.toledige);
        } else if(tur.getPassasjer4() == "null") {
            tur.setLedigeSeter(1);
            holder.seter.setImageResource(R.drawable.enledig);
        } else {
            tur.setLedigeSeter(0);
        }

        holder.avreiseSted.setText(tur.getAvreiseSted());
        holder.ankomstSted.setText(tur.getReiseMaal());
        holder.sjafor.setText(tur.getSjafor());
        holder.avreiseDato.setText(tur.getDato());
        holder.avreiseKl.setText(tur.getAvreiseKl());

        String bilInfo = tur.getBilmerke() + " " + tur.getAarsModell();
        holder.bilmodell.setText(bilInfo);

    }

    public void sendMelding(int telefonNummer) {

    }

    public void ringSjafor(int telefonNummer) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView seter;
        TextView avreiseSted, ankomstSted, sjafor, avreiseDato, avreiseKl, bilmodell;
        LinearLayout parent_turer, passasjer_liste;

        public ViewHolder(View itemView) {
            super(itemView);
            seter           = itemView.findViewById(R.id.seter);
            avreiseSted     = itemView.findViewById(R.id.avreise_sted);
            ankomstSted     = itemView.findViewById(R.id.ankomst_sted);
            sjafor          = itemView.findViewById(R.id.sjafor);
            avreiseDato     = itemView.findViewById(R.id.avreise_dato);
            avreiseKl       = itemView.findViewById(R.id.avreise_kl);
            bilmodell       = itemView.findViewById(R.id.bilmodell);
            parent_turer    = itemView.findViewById(R.id.tur_item_layout);
            passasjer_liste = itemView.findViewById(R.id.passasjerliste);
        }
    }

}