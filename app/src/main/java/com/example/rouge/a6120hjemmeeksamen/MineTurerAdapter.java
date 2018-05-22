package com.example.rouge.a6120hjemmeeksamen;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MineTurerAdapter extends RecyclerView.Adapter<MineTurerAdapter.ViewHolder> {

    private Context context;
    private List<Tur> list;
    private List<String> turInfo;

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
        // turInfo brukes som 'placeholder' for all info om èn tur i onclick
        turInfo = new ArrayList<>();

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

        holder.passasjer_liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] passasjerer = new String[]{tur.getSjafor(),tur.getPassasjer1(),tur.getPassasjer2(),tur.getPassasjer3(),tur.getPassasjer4()};

                visPassasjerListe(passasjerer);

            }
        });

        holder.parent_turer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tømmer lista
                turInfo.clear();
                // Tilegner lista informasjon om den markerte turen for å få tilgang til all informasjon i andre metoder
                turInfo.add(String.valueOf(tur.getTurID()));        // 0
                turInfo.add(tur.getSjafor());                       // 1
                turInfo.add(tur.getAvreiseSted());                  // 2
                turInfo.add(tur.getReiseMaal());                    // 3
                turInfo.add(tur.getDato());                         // 4
                turInfo.add(tur.getAvreiseKl());                    // 5
                turInfo.add(tur.getReiseTidMinutter());             // 6
                turInfo.add(tur.getPassasjer1());                   // 7
                turInfo.add(tur.getPassasjer2());                   // 8
                turInfo.add(tur.getPassasjer3());                   // 9
                turInfo.add(tur.getPassasjer4());                   // 10
                turInfo.add(tur.getBilmerke());                     // 11
                turInfo.add(String.valueOf(tur.getAarsModell()));   // 12
                turInfo.add(tur.getEpost());                        // 13
                turInfo.add(String.valueOf(tur.getTelefonNr()));    // 14
                // TODO bytte ut navn med innlogget bruker
                avlystur(turInfo.get(0), turInfo.get(1), "Gregers Gram");

            }
        });

    }

    private void avlystur(final String turID, final String sjafor, final String navn) {

        final AlertDialog.Builder reiseInfo = new AlertDialog.Builder(context);
        reiseInfo.setTitle("Turvalg");

        String dialogValg = "Meld meg av";

        if(sjafor.equals(navn)) {
            dialogValg = "Avlys tur";
            reiseInfo.setTitle("Vil du avlyse denne turen?");
        }
        // Kaller på metoden som sjekker hvilken plass brukeren er registrert med
        final String passasjerPlass = sjekkPassasjerPlass(turInfo, navn);

        reiseInfo.setPositiveButton(
                dialogValg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String jsonUrl = "http://gakk.one/6120-hjemmeeksamen/fjernTur.php";

                        StringRequest fjernTur = new StringRequest(Request.Method.POST, jsonUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (response.equals("Suksess")) {
                                    Toast.makeText(context, "Suksess", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Feil på databasen..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(context, "Det skjedde en feil..", Toast.LENGTH_SHORT).show();
                                Log.d("Volleyfeil", "" + error)
;                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("turID", turID);
                                params.put("sjafor", sjafor);
                                params.put("navn", navn);
                                params.put("passasjer", passasjerPlass);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(fjernTur);

                    }
                }
        );
        reiseInfo.setNeutralButton(
                "Kontakt sjafør",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int i) {
                        AlertDialog.Builder kontaktDialog = new AlertDialog.Builder(context);
                        kontaktDialog.setTitle("Kontakt sjafør");
                        kontaktDialog.setPositiveButton(
                                "SMS",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        sendMelding(parseInt(turInfo.get(14)));
                                        dialog.cancel();
                                    }
                                }
                        );
                        kontaktDialog.setNegativeButton(
                                "Ring",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        ringSjafor(parseInt(turInfo.get(14)));
                                        dialog.cancel();
                                    }
                                }
                        );
                        kontaktDialog.setNeutralButton(
                                "Mail",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        sendMail(turInfo.get(13));
                                        dialog.cancel();
                                    }
                                }
                        );
                        kontaktDialog.show();
                        dialog.cancel();
                    }
                }
        );
        reiseInfo.setNegativeButton(
                "Avbryt",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                }
        );

        AlertDialog visReise = reiseInfo.create();
        visReise.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        visReise.getWindow().setGravity(Gravity.BOTTOM);
        visReise.show();

    }

    private String sjekkPassasjerPlass(List<String> turInfo, String navn) {
        String passasjerPlass;

        if (turInfo.get(7).equals(navn)) {
            passasjerPlass = "passasjer1";
        } else if (turInfo.get(8).equals(navn)) {
            passasjerPlass = "passasjer2";
        } else if (turInfo.get(9).equals(navn)) {
            passasjerPlass = "passasjer3";
        } else if (turInfo.get(10).equals(navn)) {
            passasjerPlass = "passasjer4";
        } else {
            passasjerPlass = "sjafor";
        }
        return passasjerPlass;
    }

    private void visPassasjerListe(String[] passasjerer) {

        String passasjerInfo = "";
        // Her igjen sammenligner jeg med !=, siden jeg vet en tom passasjer er representert med "null"
        for(String passasjer : passasjerer) {
            if(passasjer != "null") {
                passasjerInfo += passasjer + "\n";


            }
        }

        // Åpner et dialogivindu med passasjerliste
        AlertDialog.Builder reiseInfo = new AlertDialog.Builder(context);
        reiseInfo.setTitle("Passasjerliste");
        reiseInfo.setMessage(passasjerInfo);

        reiseInfo.setNegativeButton(
                "Lukk",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog visReise = reiseInfo.create();
        visReise.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        visReise.getWindow().setGravity(Gravity.BOTTOM);
        visReise.show();

    }

    public void sendMelding(int telefonNummer) {
        // Intent for å sende sende melding til sjaføren, med catch for om brukeren ikke kan sende sms
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", String.valueOf(telefonNummer), null)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Du har ingen SMS-app", Toast.LENGTH_SHORT).show();
        }
    }

    public void ringSjafor(int telefonNummer) {
        // Intent for å ringe sjaføren, med catch for om brukeren ikke kan ringe fra en app
        try {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(telefonNummer), null)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Fant ingen app du kan ringe fra", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMail(String mail) {
        // Intent for å sende sjaføren mail, med catch for om brukeren ikke har en app som kan sende mail
        try {
            context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+mail)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Fant ingen app du kan sende mail fra", Toast.LENGTH_SHORT).show();
        }
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