package com.example.rouge.a6120hjemmeeksamen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class TurerFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener {

    public static GoogleMap mMap;

    private List<Tur> turListe;
    private List<Tur> resultat;
    private List<Tur> reset;
    private RecyclerView.Adapter adapter;
    SharedPreferences pref;

    public TurerFragment() {
        // Tom konstruktør
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflater layouten for fragmentet
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_turer, container, false);

        getActivity().setTitle("Turer");

        RecyclerView mRecyclerview = view.findViewById(R.id.tur_recycler);

        turListe = new ArrayList<>();
        reset = new ArrayList<>();
        adapter = new TurAdapter(getContext(), turListe);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.setAdapter(adapter);

        hentData();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflater søke-ikonet
        inflater.inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    // Søkefunksjon for turer
    public boolean onQueryTextChange(String query) {
        // Array for resultat av søk
        resultat = new ArrayList<>();
        // For hver tur, Hvis avreisested eller reisemål inneholder søkeordet. Legg til turen i resultat
        for(Tur tur : reset){
            if(tur.getAvreiseSted().toLowerCase().contains(query) || tur.getReiseMaal().toLowerCase().contains(query)){
                resultat.add(tur);
            }
        }
        // Tømmer turListe
        turListe.clear();
        // Legger til søkeresultatet
        turListe.addAll(resultat);
        // Hvis brukeren sletter søket, tilbakestilles listen
        if(query.equals("")){
            turListe.addAll(reset);
        }
        // oppdater View
        adapter.notifyDataSetChanged();

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Måtte implementeres, men trengs ikke
        return false;
    }

    private void hentData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Laster...");
        progressDialog.show();

        pref = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        final String navn = pref.getString("navn", "");

        final String JSON_URL = "http://gakk.one/6120-hjemmeeksamen/seTurer.php";
        StringRequest jsonStringRequest = new StringRequest(Request.Method.POST, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonTurArray = jsonObject.optJSONArray("Turer");

                for (int i = 0; i < jsonTurArray.length(); i++) {
                    try {
                        JSONObject jsonTur = (JSONObject)jsonTurArray.get(i);
                        Tur tur = new Tur(jsonTur);

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
                        // Ekstra array til søk
                        reset.add(tur);

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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("navn", navn);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonStringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Oppretter en default LatLng så kartets kamera viser hele norge ved oppstart
        LatLng norge = new LatLng(64.190122, 11.852735);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(3));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(norge));
    }

}