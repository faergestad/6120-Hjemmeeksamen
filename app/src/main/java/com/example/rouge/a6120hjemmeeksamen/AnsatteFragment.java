package com.example.rouge.a6120hjemmeeksamen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;


public class AnsatteFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Ansatt> ansattListe;
    private RecyclerView.Adapter adapter;
    public static final String JSON_URL = "http://10.0.2.2/hobbyhuset/api.php/Bruker?order=BNr,desc&transform=1";

    public AnsatteFragment() {
        // Tom konstruktør
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ansatte, container, false);

        getActivity().setTitle("Ansatte");

        mRecyclerview = view.findViewById(R.id.ansatte_recycler);

        ansattListe = new ArrayList<>();
        adapter = new AnsattAdapter(getContext(),ansattListe);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mRecyclerview.getContext(), linearLayoutManager.getOrientation());

        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.addItemDecoration(dividerItemDecoration);
        mRecyclerview.setAdapter(adapter);


        hentData();
        return view;
    }

    private void hentData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Laster...");
        progressDialog.show();

        StringRequest jsonStringRequest = new StringRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonAnsattArray = jsonObject.optJSONArray("Bruker");

                for (int i = 0; i < jsonAnsattArray.length(); i++) {
                    try {
                        //Log.d("Response", "" + response.toString());
                        JSONObject jsonAnsatt = (JSONObject)jsonAnsattArray.get(i);
                        Ansatt ansatt = new Ansatt(jsonAnsatt);

                        ansatt.setbNR(parseInt(jsonAnsatt.getString("BNr")));
                        ansatt.setFornavn(jsonAnsatt.getString("Fornavn"));
                        ansatt.setEtternavn(jsonAnsatt.getString("Etternavn"));
                        ansatt.setEpost(jsonAnsatt.getString("Epost"));
                        ansatt.setPassord(jsonAnsatt.getString("Passord"));
                        ansatt.setPassord(jsonAnsatt.getString("bilde"));


                        ansattListe.add(ansatt);

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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonStringRequest);
    }

}