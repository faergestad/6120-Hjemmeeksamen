package com.example.rouge.a6120hjemmeeksamen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DashboardFragment extends Fragment {

    private static final String TOTANTALLURL = "http://10.0.2.2/hobbyhuset/dashboardTotalAntVarer.php";
    private static final String LAGERTOTAL = "lagertotal", TOTALLAGER = "TotalLager";

    private static final String BRUTTOKOSTURL = "http://10.0.2.2/hobbyhuset/dashboardSumBrutto.php";
    private static final String BRUTTOKOST = "BruttoTotal", BRUTTOTOTAL = "TotalPris";

    private static final String NETTOURL = "http://10.0.2.2/hobbyhuset/dashboardSumNetto.php";
    private static final String NETTO = "NettoTotal", NETTOTOTAL = "TotalNetto";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        TextView totAntallTxt = getActivity().findViewById(R.id.totAntall);
        TextView bruttoKostTxt = getActivity().findViewById(R.id.bruttoKost);
        TextView nettoFortjeneste = getActivity().findViewById(R.id.nettoFortjeneste);

        getActivity().setTitle("Dashboard");
        hentData(TOTANTALLURL, totAntallTxt, LAGERTOTAL, TOTALLAGER);
        hentData(BRUTTOKOSTURL, bruttoKostTxt, BRUTTOKOST, BRUTTOTOTAL);
        hentData(NETTOURL, nettoFortjeneste, NETTO, NETTOTOTAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);

    }

    public void hentData(String url, final TextView view, final String jsonArrayNavn, final String jsonArrayInnhold) {
        StringRequest jsonStringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonVareArray = jsonObject.optJSONArray(jsonArrayNavn);

                for (int i = 0; i < jsonVareArray.length(); i++) {
                    try {
                        JSONObject jsonObj = (JSONObject)jsonVareArray.get(i);

                        view.setText(jsonObj.getString(jsonArrayInnhold));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonStringRequest);
    }

}