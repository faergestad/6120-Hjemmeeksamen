package com.example.rouge.a6120hjemmeeksamen;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FasteTurerAdapter extends RecyclerView.Adapter<FasteTurerAdapter.ViewHolder> {

    private Context context;
    SharedPreferences pref;
    private List<Tur> list;
    private List<String> turInfo;

    public FasteTurerAdapter(Context context, List<Tur> list) {
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
        final Tur tur = list.get(position);

        // turInfo brukes som 'placeholder' for all info om èn tur i onclick
        turInfo = new ArrayList<>();
        pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String navn = pref.getString("navn", "");

        holder.seter.setImageResource(R.drawable.bil);
        // Setter teksten i viewholderen på tilegnet posisjon
        holder.avreiseSted.setText(tur.getAvreiseSted());
        holder.ankomstSted.setText(tur.getReiseMaal());
        holder.sjafor.setText(tur.getSjafor());
        holder.avreiseDato.setText(tur.getDato());
        holder.avreiseKl.setText(tur.getAvreiseKl());
        // Setter sammen informasjonen om bilen i en streng
        String bilInfo = tur.getBilmerke() + " " + tur.getAarsModell();
        holder.bilmodell.setText(bilInfo);

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
            seter = itemView.findViewById(R.id.seter);
            avreiseSted = itemView.findViewById(R.id.avreise_sted);
            ankomstSted = itemView.findViewById(R.id.ankomst_sted);
            sjafor = itemView.findViewById(R.id.sjafor);
            avreiseDato = itemView.findViewById(R.id.avreise_dato);
            avreiseKl = itemView.findViewById(R.id.avreise_kl);
            bilmodell = itemView.findViewById(R.id.bilmodell);
            parent_turer = itemView.findViewById(R.id.tur_item_layout);
            passasjer_liste = itemView.findViewById(R.id.passasjerliste);
        }
    }

}