package com.example.rouge.a6120hjemmeeksamen;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.example.rouge.a6120hjemmeeksamen.TurerFragment.mMap;


public class TurAdapter extends RecyclerView.Adapter<TurAdapter.ViewHolder> implements RoutingListener {

    private Context context;
    private List<Tur> liste;
    private static double fraLat = 0, fraLng = 0, tilLat = 0, tilLng = 0;
    private List<Polyline> polylines;
    private List<Integer> turReferanse;
    private List<String> turInfo;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    public TurAdapter(Context context, List<Tur> liste) {
        this.context = context;
        this.liste = liste;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_turer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Tur tur = liste.get(position);
        turReferanse = new ArrayList<>();
        turInfo = new ArrayList<>();
        polylines = new ArrayList<>();

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
        holder.ankomstKl.setText(tur.getReiseTidMinutter());

        String bilInfo = tur.getBilmerke() + " " + tur.getAarsModell();
        holder.bilmodell.setText(bilInfo);

        holder.passasjer_liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] passasjerer = new String[]{tur.getSjafor(),tur.getPassasjer1(),tur.getPassasjer2(),tur.getPassasjer3(),tur.getPassasjer4()};

                visPassasjerListe(passasjerer);

            }
        });

        holder.parent_turer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                turInfo.clear();

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

                String fraAdresse = tur.getAvreiseSted();
                String fraAdresseUrlFormat = fraAdresse.replaceAll("\\s", "+");
                // Formaterer til/fra-adressene til et format som kan sendes til Google API'et
                String tilAdresse = tur.getReiseMaal();
                String tilAdresseUrlFormat = tilAdresse.replaceAll("\\s", "+");

                Log.d("Adresse: ", fraAdresseUrlFormat);
                mMap.clear();
                fjernTegnetRute();


                if(!(turReferanse.isEmpty())) {
                    turReferanse.clear();
                }
                turReferanse.add(tur.getTurID());
                turReferanse.add(tur.getLedigeSeter());

                avreiseKoordinater(fraAdresseUrlFormat, tilAdresseUrlFormat);
            }
        });

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

    @Override
    public int getItemCount() {
        return liste.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView seter;
        TextView avreiseSted, ankomstSted, sjafor, avreiseDato, avreiseKl, ankomstKl, bilmodell;
        LinearLayout parent_turer, passasjer_liste;

        public ViewHolder(View itemView) {
            super(itemView);
            seter           = itemView.findViewById(R.id.seter);
            avreiseSted     = itemView.findViewById(R.id.avreise_sted);
            ankomstSted     = itemView.findViewById(R.id.ankomst_sted);
            sjafor          = itemView.findViewById(R.id.sjafor);
            avreiseDato     = itemView.findViewById(R.id.avreise_dato);
            avreiseKl       = itemView.findViewById(R.id.avreise_kl);
            ankomstKl       = itemView.findViewById(R.id.ankomst_kl);
            bilmodell       = itemView.findViewById(R.id.bilmodell);
            parent_turer    = itemView.findViewById(R.id.tur_item_layout);
            passasjer_liste = itemView.findViewById(R.id.passasjerliste);
        }
    }


    public void avreiseKoordinater(String fraAdresse, String tilAdresse) {
        final String fraUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + fraAdresse + "&key=AIzaSyCJp85Mu9GCseaaEVgRu88PwyZ4ZU5K4Os";
        final String tilUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + tilAdresse + "&key=AIzaSyCJp85Mu9GCseaaEVgRu88PwyZ4ZU5K4Os";
        // Volley-request for å hente koordinatene til startpunktet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fraUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    fraLat = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    fraLng = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    // Volley-request for å hente koordinatene til sluttpunktet
                    JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, tilUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                tilLat = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                tilLng = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                // Setter markørene på kartet til mine egne ikoner
                                BitmapDescriptor markorIkon = BitmapDescriptorFactory.fromResource(R.drawable.biltilkart);
                                Marker startMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(fraLat,fraLng))
                                        .title("Avreise")
                                        .icon(markorIkon));
                                // Setter startmarkørens tittel til synlig for lett å kunne se hvor ruten starter
                                startMarker.showInfoWindow();

                                Marker stoppMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(tilLat,tilLng))
                                        .title("Reisemål")
                                        .icon(markorIkon));

                                // Logikk og verktøy for å gjøre hele ruten synlig på kartet
                                ArrayList<Marker> markers = new ArrayList<>();
                                markers.add(startMarker);
                                markers.add(stoppMarker);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : markers) {
                                    builder.include(marker.getPosition());
                                }
                                LatLngBounds begrensninger = builder.build();

                                int padding = 120; // padding fra kartets kanter i piksler
                                CameraUpdate oppdaterKamera = CameraUpdateFactory.newLatLngBounds(begrensninger, padding);
                                mMap.moveCamera(oppdaterKamera);

                                // Oppretter to LatLng objekter for å ryddig kunne sende koordinater til funksjonen 'hentRute'
                                LatLng startLatLng = new LatLng(fraLat,fraLng);
                                LatLng stoppLatLng = new LatLng(tilLat, tilLng);
                                hentRute(startLatLng, stoppLatLng);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    RequestQueue requestQueue2 = Volley.newRequestQueue(context);
                    requestQueue2.add(jsonObjectRequest1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
    // Metoden hentRute implementerer 'Google-Directions-Android'-biblioteket
    // *se dokumentasjon
    private void hentRute(LatLng startLatLng, LatLng stoppLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(startLatLng, stoppLatLng)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(context, "Feil: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "Noe gikk galt, prøv igjen", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> rute, int kortesteRuteIndeks) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        // Legger til ruten på kartet
        for (int i = 0; i <rute.size(); i++) {

            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color((COLORS[colorIndex]));
            polyOptions.width(13 + i * 5);
            polyOptions.addAll(rute.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            // Toast brukt for å teste om informasjonen om ruten er korrekt
            //Toast.makeText(context,"Rute "+ (i+1) +": avstand - "+ rute.get(i).getDistanceValue()+": tid - "+ rute.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            visOgVelgRute(rute.get(i).getDurationValue());
        }
    }


    public void visOgVelgRute(int ruteTid) {
        int ruteTimer = ruteTid / 3600;
        int ruteMinutter = (ruteTid % 3600) / 60;
        int minTotalt = ruteTid / 60;
        int ruteSekunder = ruteTid % 60;

        turInfo.set(6, String.valueOf(minTotalt));

        String informasjon;
        // Skriver ikke ut timer i dialogvinduet hvis turen tar under 1 time
        if(ruteTimer == 0) {
            informasjon = "Denne reisen tar: " + ruteMinutter + " minutter" + " og " + ruteSekunder + " sekunder" +
                    "\n\nVil du legge til denne turen?";
        } else {
            informasjon = "Denne reisen tar: " + ruteTimer + " timer " + ruteMinutter + " minutter" + " og " + ruteSekunder + " sekunder" +
                    "\n\nVil du legge til denne turen?";
        }

        // Åpner et dialogivindu med informasjon om turen, og mulighet for å "melde seg på"
        AlertDialog.Builder reiseInfo = new AlertDialog.Builder(context);
        reiseInfo.setTitle("Informasjon om reise");
        reiseInfo.setMessage(informasjon);

        reiseInfo.setPositiveButton(
                "Legg til reise",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final int turID = turReferanse.get(0);
                        final int ledigeSeter = turReferanse.get(1);
                        // TODO bytte ut navn med innlogget bruker
                        final String navn = "Gregers Gram";
                        final String leggTilPassasjerURL = "http://gakk.one/6120-hjemmeeksamen/oppdaterTur.php";

                        if(turInfo.contains(navn)) {
                            Toast.makeText(context, "Du er allerede registrert på denne turen", Toast.LENGTH_SHORT).show();
                        } else {

                            StringRequest leggTilPassasjer = new StringRequest(Request.Method.POST, leggTilPassasjerURL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if(response.equals("Suksess")) {
                                        Toast.makeText(context, "Lagt til i \"Mine turer\"", Toast.LENGTH_SHORT).show();

                                    // Legg til turen i brukerens kalender
                                    Intent turTilKalender = new Intent(Intent.ACTION_INSERT);
                                    turTilKalender.setType("vnd.android.cursor.item/event");
                                    turTilKalender.putExtra(CalendarContract.Events.TITLE, "Du haiker med " + turInfo.get(1));
                                    turTilKalender.putExtra(CalendarContract.Events.EVENT_LOCATION, turInfo.get(2));
                                    turTilKalender.putExtra(CalendarContract.Events.DESCRIPTION, "Reise fra: " + turInfo.get(2) + "\nTil: " + turInfo.get(3));
                                    turTilKalender.putExtra(CalendarContract.Events.ALLOWED_REMINDERS, true);

                                    String turDato = turInfo.get(4);
                                    String turAvreiseKl = turInfo.get(5);
                                    Calendar kalender = Calendar.getInstance();
                                        Date dato = null;
                                        try {
                                            dato = new SimpleDateFormat("yyyy-MM-dd-HH-mm").parse(
                                                    turDato+"-"+turAvreiseKl.charAt(0)+turAvreiseKl.charAt(1)+"-"+turAvreiseKl.charAt(3)+turAvreiseKl.charAt(4));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("dato", "" + dato);
                                        kalender.setTime(dato);

                                        turTilKalender.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, kalender.getTimeInMillis());

                                    context.startActivity(turTilKalender);

                                    } else {
                                        Toast.makeText(context, "Det var rart.. Prøv igjen", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(context, "Det skjedde en feil..", Toast.LENGTH_SHORT).show();
                                    Log.d("Volleyfeil", "" + error);

                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("turID", String.valueOf(turID));
                                    params.put("ledigeSeter", String.valueOf(ledigeSeter));
                                    params.put("navn", navn);
                                    return params;
                                }
                            };

                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            requestQueue.add(leggTilPassasjer);

                            dialog.cancel();

                        }

                    }
                });

        reiseInfo.setNegativeButton(
                "Avbryt",
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


    @Override
    public void onRoutingCancelled() {

    }

    private void fjernTegnetRute() {
        for(Polyline linje : polylines) {
            linje.remove();
        }
        polylines.clear();
    }

}