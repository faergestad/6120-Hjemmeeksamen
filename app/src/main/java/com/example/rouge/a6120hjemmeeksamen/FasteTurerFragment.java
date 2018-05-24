package com.example.rouge.a6120hjemmeeksamen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;


public class FasteTurerFragment extends Fragment {

    Button leggTilBtn;
    private RecyclerView.Adapter adapter;
    SharedPreferences pref, pref2;
    AlertDialog.Builder velgDager;
    // String tabell med valgmulighetene når man oppretter en fast reise
    String[] dager = new String[]{
            "Man",
            "Tir",
            "Ons",
            "Tor",
            "Fre",
            "Man-Fre",
            "Lør",
            "Søn"
    };
    List<String> dagerTilListe;
    // Tabell som sendes med dager-tabellen for å sette utgangspunktet til hver
    // checkbox til false
    boolean[] valgtDag = new boolean[]{
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
    };
    private List<Tur> turListe;

    public FasteTurerFragment() {
        // Tom konstruktør
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflater layouten for fragmentet
        View view = inflater.inflate(R.layout.fragment_faste_turer, container, false);

        getActivity().setTitle("Faste turer");

        RecyclerView mRecyclerview = view.findViewById(R.id.fasteturer_recycler);

        turListe = new ArrayList<>();
        adapter = new FasteTurerAdapter(getContext(), turListe);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        leggTilBtn = view.findViewById(R.id.leggTilFastTur);
        leggTilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lager et nytt alertdialog-vindu med multiple choice
                velgDager = new AlertDialog.Builder(getActivity());

                dagerTilListe = Arrays.asList(dager);
                // Sender med tabellene for å gi hvert valg navn og verdi
                velgDager.setMultiChoiceItems(dager, valgtDag, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });

                velgDager.setCancelable(true);

                velgDager.setTitle("Velg faste dager");

                velgDager.setPositiveButton(
                        "Velg",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String dager = "";
                                int i = 0;
                                while (i < valgtDag.length) {
                                    boolean value = valgtDag[i];

                                    if (value) {
                                        // Legget til alle valgte muligheter i lista
                                        dager += dagerTilListe.get(i) + " ";
                                    }

                                    i++;
                                }
                                // Formaterer strengen med dagene til et penere format
                                String formaterteDager = dager.replace(" ", ", ");
                                Log.d("dager", formaterteDager);

                                // For å sjekke om brukeren har registrert fast info
                                pref2 = getActivity().getSharedPreferences("fastInfo", Context.MODE_PRIVATE);
                                String fastinfo = pref2.getString("avreise", "");
                                if (fastinfo.equals("")) {
                                    Toast.makeText(getActivity(), "Du må lagre fast informasjon i innstillinger", Toast.LENGTH_SHORT).show();
                                } else {
                                    leggtilFastTur(formaterteDager);
                                }

                            }
                        });

                velgDager.setNeutralButton("Avbryt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = velgDager.create();

                dialog.show();
            }
        });


        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.setAdapter(adapter);
        // Hent data henter data fra databasen
        hentData();

        return view;
    }

    private void hentData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Laster...");
        progressDialog.show();
        // Henter informasjonen som ble lagret om brukeren da brukeren logget inn
        pref = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        final String navn = pref.getString("navn", "");
        // URl som kjører php-scriptet som bare returnerer faste turer
        final String JSON_URL = "http://gakk.one/6120-hjemmeeksamen/bareMineTurer.php";
        // Her bruker jeg Volley-bibliotekets StringRequest for å gjøre en POST-request
        StringRequest jsonStringRequest = new StringRequest(Request.Method.POST, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                assert jsonObject != null;
                // Definerer navnet på jsonarray'en som serveren returnerer
                JSONArray jsonTurArray = jsonObject.optJSONArray("MineTurer");

                for (int i = 0; i < jsonTurArray.length(); i++) {
                    try {
                        JSONObject jsonTur = (JSONObject) jsonTurArray.get(i);
                        Tur tur = new Tur(jsonTur);
                        // Tilegner objektet 'tur' verdier
                        tur.setTurID(parseInt(jsonTur.getString("turID")));
                        tur.setSjafor(jsonTur.getString("sjafor"));
                        tur.setAvreiseSted(jsonTur.getString("avreiseSted"));
                        tur.setReiseMaal(jsonTur.getString("reiseMaal"));
                        tur.setDato(jsonTur.getString("dato"));
                        tur.setAvreiseKl(jsonTur.getString("avreiseKl"));
                        tur.setReiseTidMinutter(jsonTur.getString("reiseTidMinutter"));
                        tur.setPassasjer1(jsonTur.getString("passasjer1"));
                        tur.setPassasjer2(jsonTur.getString("passasjer2"));
                        tur.setPassasjer3(jsonTur.getString("passasjer3"));
                        tur.setPassasjer4(jsonTur.getString("passasjer4"));
                        tur.setEpost(jsonTur.getString("epost"));
                        tur.setTelefonNr(parseInt(jsonTur.getString("telefonnr")));
                        tur.setBilmerke(jsonTur.getString("bilmerke"));
                        tur.setAarsModell(jsonTur.getInt("aarsModell"));

                        turListe.add(tur);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Sender med brukerens navn som parameter for å bruke navnet i en sql-spørring
                HashMap<String, String> params = new HashMap<>();
                params.put("navn", navn);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonStringRequest);
    }

    private void leggtilFastTur(final String formaterteDager) {

        String opprettBrukerURL = "http://gakk.one/6120-hjemmeeksamen/opprettTur.php";

        StringRequest opprettBrukerRequest = new StringRequest(Request.Method.POST, opprettBrukerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Suksess")) {

                    Toast.makeText(getActivity(), "Tur opprettet", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));

                } else {

                    Toast.makeText(getActivity(), "Det skjedde en feil", Toast.LENGTH_SHORT).show();
                    Log.d("Response", "" + response);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volleyfeil", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // For å hente brukerens navn
                pref = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                // For å hente brukerens info om brukerens faste turer
                pref2 = getActivity().getSharedPreferences("fastInfo", Context.MODE_PRIVATE);

                final String navn = pref.getString("navn", "");
                final String fra = pref2.getString("avreise", "");
                final String til = pref2.getString("mål", "");
                final String tid = pref2.getString("tidspunkt", "");
                Map<String, String> params = new HashMap<>();
                params.put("sjafor", navn);
                params.put("adresse", fra);
                params.put("maal", til);
                params.put("dato", formaterteDager);
                params.put("klslett", tid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(opprettBrukerRequest);

    }

}