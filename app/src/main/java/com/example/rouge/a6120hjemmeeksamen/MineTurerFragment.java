package com.example.rouge.a6120hjemmeeksamen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;


public class MineTurerFragment extends Fragment {

    private List<Tur> turListe;
    private RecyclerView.Adapter adapter;

    public MineTurerFragment() {
        // Tom konstruktør
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflater layouten for fragmentet
        View view = inflater.inflate(R.layout.fragment_mine_turer, container, false);

        getActivity().setTitle("Mine turer");

        RecyclerView mRecyclerview = view.findViewById(R.id.mineturer_recycler);

        turListe = new ArrayList<>();
        adapter = new MineTurerAdapter(getContext(), turListe);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.setAdapter(adapter);

        hentData();

        return view;
    }

    private void hentData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Laster...");
        progressDialog.show();

        // TODO bytte ut navn med innlogget bruker
        final String navn = "Gregers Gram";

        final String JSON_URL = "http://gakk.one/6120-hjemmeeksamen/mineTurer.php";
        StringRequest jsonStringRequest = new StringRequest(Request.Method.POST, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonTurArray = jsonObject.optJSONArray("MineTurer");

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

}
