package com.example.rouge.a6120hjemmeeksamen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AnsattAdapter extends RecyclerView.Adapter<AnsattAdapter.ViewHolder> {

    private Context context;
    private List<Ansatt> list;

    public AnsattAdapter(Context context, List<Ansatt> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ansatte, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Ansatt ansatt= list.get(position);

        holder.textFnavn.setText(ansatt.getFornavn());
        holder.textLnavn.setText(ansatt.getEtternavn());
        holder.textEpost.setText(ansatt.getEpost());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, ansatt.getEpost(), Toast.LENGTH_SHORT).show();
                sendMail(ansatt.getEpost());
            }
        });

    }

    public void sendMail(String epost) {
        String mottaker = epost;
        String emne = "Vedr jobb - sendt fra Hobbyhusets app";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, mottaker);
        intent.putExtra(Intent.EXTRA_SUBJECT, emne);

        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Velg epost klient"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textFnavn, textLnavn, textEpost;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textFnavn = itemView.findViewById(R.id.ansatt_fnavn);
            textLnavn = itemView.findViewById(R.id.ansatt_lnavn);
            textEpost = itemView.findViewById(R.id.ansatt_epost);
            linearLayout = itemView.findViewById(R.id.ansatt_item_layout);

        }
    }

}