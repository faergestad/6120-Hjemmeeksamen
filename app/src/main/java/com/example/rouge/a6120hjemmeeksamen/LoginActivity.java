package com.example.rouge.a6120hjemmeeksamen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {

    EditText Email, Password;
    Button loggInnBtn;

    Boolean ikkeErTom;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.userField);
        Password = findViewById(R.id.passField);
        loggInnBtn = findViewById(R.id.login);

        // Oppretter en fil som heter "login" for å lagre epost og passord
        pref = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = pref.edit();

        String husketEpost = pref.getString("epost", "");
        String husketPassord = pref.getString("passord", "");


        if (!(husketEpost.equals("") && husketPassord.equals(""))) {
            loggInn(husketEpost, husketPassord);
        }

        loggInnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String epost = Email.getText().toString();
                String passord = Password.getText().toString();

                sjekkOmErTom(epost,passord);

                if (ikkeErTom) {
                    // Legger innlogging i sharedpreferences for å automatisk logge inn etter første gang
                    editor.putString("epost", epost);
                    editor.putString("passord", passord);
                    editor.apply();

                    loggInn(epost, passord);

                } else {

                    Toast.makeText(LoginActivity.this, "Fyll ut alle feltene", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    // Metode for å sjekke om teksfeltene har innhold
    private void sjekkOmErTom(String epost, String passord) {

        if (TextUtils.isEmpty(epost) || TextUtils.isEmpty(passord)) {
            ikkeErTom = false;
        } else {
            ikkeErTom = true;
        }

    }

    public void loggInn(final String epost, final String passord) {

        String opprettBrukerURL = "http://gakk.one/6120-hjemmeeksamen/loggInn.php";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, opprettBrukerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (!(response.equals("Feil"))) {

                    editor.putString("navn", response);
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                } else {

                    Toast.makeText(LoginActivity.this, "Feil brukernavn eller passord", Toast.LENGTH_SHORT).show();
                    Log.d("Response", "" + response);
                    // TODO legg til håndtering for om serveren er nede. Siden one.com er så ustabil
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
                // Sender med brukerens epost og passord for å autorisere brukeren
                Map<String, String> params = new HashMap<>();
                params.put("epost", epost);
                params.put("passord", passord);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);

    }

    public void registrerBruker(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrerBruker.class);
        startActivity(intent);
    }

}
