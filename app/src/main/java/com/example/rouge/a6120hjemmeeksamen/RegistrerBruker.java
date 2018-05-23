package com.example.rouge.a6120hjemmeeksamen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegistrerBruker extends AppCompatActivity {

    private EditText editEpost, editPassord, editNavn, editTlf, editBilmerke, editAarsModell;
    private Button registrerBtn;
    private Boolean ikkeErTom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_bruker);

        editEpost = findViewById(R.id.editepost);
        editPassord = findViewById(R.id.editpassord);
        editNavn = findViewById(R.id.editnavn);
        editTlf = findViewById(R.id.edittlf);
        editBilmerke = findViewById(R.id.editbilmerke);
        editAarsModell = findViewById(R.id.editaarsModell);
        registrerBtn = findViewById(R.id.registrer);

        registrerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String epost = editEpost.getText().toString();
                String passord = editPassord.getText().toString();
                String navn = editNavn.getText().toString();
                String tlf = editTlf.getText().toString();
                String bilmerke = editBilmerke.getText().toString();
                String aarsModell = editAarsModell.getText().toString();

                sjekkOmErTom(epost,passord,navn,tlf);

                if (ikkeErTom) {
                    opprettBruker(epost, passord, navn, tlf, bilmerke, aarsModell);
                } else {
                    Toast.makeText(RegistrerBruker.this, "Fyll ut alle feltene", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    // Metode for å sjekke om teksfeltene har innhold
    private void sjekkOmErTom(String epost, String passord, String navn, String tlf) {

        if (TextUtils.isEmpty(epost) || TextUtils.isEmpty(passord) || TextUtils.isEmpty(navn) || TextUtils.isEmpty(tlf) ) {
            ikkeErTom = false;
        } else {
            ikkeErTom = true;
        }

    }

    public void opprettBruker(final String epost, final String passord, final String navn, final String tlf, final String bilmerke, final String aarsModell) {

        String opprettBrukerURL = "http://gakk.one/6120-hjemmeeksamen/opprettBruker.php";

        StringRequest opprettBrukerRequest = new StringRequest(Request.Method.POST, opprettBrukerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Suksess")) {

                    Toast.makeText(RegistrerBruker.this, "Bruker opprettet", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrerBruker.this, MainActivity.class));

                } else {

                    Toast.makeText(RegistrerBruker.this, "Det skjedde en feil", Toast.LENGTH_SHORT).show();
                    Log.d("Response", "" + response);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volleyfeil", "" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("epost", epost);
                params.put("passord", passord);
                params.put("navn", navn);
                params.put("telefonnr", tlf);
                params.put("bilmerke", bilmerke);
                params.put("aarsModell", aarsModell);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(opprettBrukerRequest);

    }
}
