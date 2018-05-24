package com.example.rouge.a6120hjemmeeksamen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class LeggTilTurFragment extends Fragment {

    EditText editAdresse, editMaal, editDato, editKl;
    SharedPreferences pref;
    Button leggTilTur, velgDato, velgKlSlett;
    Boolean ikkeErTom;
    private int aar, mnd, dag, time, minutt;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        getActivity().setTitle("Opprett ny tur");

        editAdresse = view.findViewById(R.id.avreise_adresse);
        editMaal = view.findViewById(R.id.reisemaal);

        velgDato = view.findViewById(R.id.velgdato);
        velgKlSlett = view.findViewById(R.id.velgklslett);
        editDato = view.findViewById(R.id.avreisedato);
        editKl = view.findViewById(R.id.avreisekl);

        velgDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Henter dagens dato til kalenderdialoget
                final Calendar c = Calendar.getInstance();
                aar = c.get(Calendar.YEAR);
                mnd = c.get(Calendar.MONTH);
                dag = c.get(Calendar.DAY_OF_MONTH);

                // Starter DatePicker dialogvinduet
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int aar,
                                                  int mnd, int dag) {

                                editDato.setText(aar + "-" + mnd + "-" + dag);

                            }
                        }, aar, mnd, dag);
                datePickerDialog.show();

            }
        });

        velgKlSlett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Henter nåværende klokkeslett
                final Calendar c = Calendar.getInstance();
                time = c.get(Calendar.HOUR_OF_DAY);
                minutt = c.get(Calendar.MINUTE);

                // Starter TimePicker dialogvinduet
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int timer,
                                                  int minutter) {

                                editKl.setText(timer + ":" + minutter);
                            }
                        }, time, minutt, false);
                timePickerDialog.show();

            }
        });

        leggTilTur = view.findViewById(R.id.registrertur);

        leggTilTur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String adresse = editAdresse.getText().toString();
                String maal = editMaal.getText().toString();
                String dato = editDato.getText().toString();
                String klslett = editKl.getText().toString();
                // En metode som sjekker om feltene har innhold
                sjekkOmErTom(adresse, maal, dato, klslett);

                if (ikkeErTom) {
                    opprettTur(adresse, maal, dato, klslett);
                } else {
                    Toast.makeText(getActivity(), "Fyll ut alle feltene", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void opprettTur(final String adresse, final String maal, final String dato, final String klslett) {

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
                pref = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                // TODO bruke directions-api for å kunne finne ut ca ankomsttid
                final String navn = pref.getString("navn", "");
                Map<String, String> params = new HashMap<>();
                params.put("sjafor", navn);
                params.put("adresse", adresse);
                params.put("maal", maal);
                params.put("dato", dato);
                params.put("klslett", klslett);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(opprettBrukerRequest);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater layouten for fragmentet
        return inflater.inflate(R.layout.fragment_legg_til_tur, container, false);
    }

    // Metode for å sjekke om teksfeltene har innhold
    private void sjekkOmErTom(String epost, String passord, String navn, String tlf) {

        if (TextUtils.isEmpty(epost) || TextUtils.isEmpty(passord) || TextUtils.isEmpty(navn) || TextUtils.isEmpty(tlf)) {
            ikkeErTom = false;
        } else {
            ikkeErTom = true;
        }

    }

}