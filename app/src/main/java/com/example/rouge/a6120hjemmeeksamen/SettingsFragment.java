package com.example.rouge.a6120hjemmeeksamen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SettingsFragment extends Fragment {

    EditText editAvreise, editTidspunkt, editMaal;
    Button lagre, glemBruker;
    SharedPreferences pref, pref2;
    SharedPreferences.Editor editor, editor2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        getActivity().setTitle("Innstillinger");

        pref = getActivity().getSharedPreferences("fastInfo", Context.MODE_PRIVATE);
        editor = pref.edit();

        editAvreise = view.findViewById(R.id.husksted);
        editAvreise.setFocusable(true);
        editTidspunkt = view.findViewById(R.id.huskTidspunkt);
        editTidspunkt.setFocusable(true);
        editMaal = view.findViewById(R.id.huskMaal);
        editMaal.setFocusable(true);

        String husketAvreise = pref.getString("avreise", "");
        String husketTidspunkt = pref.getString("tidspunkt", "");
        String husketMaal = pref.getString("mål", "");

        if (!(husketAvreise.equals("") && husketTidspunkt.equals(""))) {
            editAvreise.setText(husketAvreise);
            editTidspunkt.setText(husketTidspunkt);
            editMaal.setText(husketMaal);
            editor.apply();
        }

        lagre = view.findViewById(R.id.lagre);
        lagre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lagrer informasjonen brukeren skrev inn i sharedpreferences
                String fastAvreise = editAvreise.getText().toString();
                editAvreise.setText(fastAvreise);
                editAvreise.setFocusable(false);

                String fastTidspunkt = editTidspunkt.getText().toString();
                editTidspunkt.setText(fastTidspunkt);
                editTidspunkt.setFocusable(false);

                String fastMaal = editMaal.getText().toString();
                editMaal.setText(fastMaal);
                editMaal.setFocusable(false);

                editor.putString("avreise", fastAvreise);
                editor.putString("tidspunkt", fastTidspunkt);
                editor.putString("mål", fastMaal);
                editor.apply();

                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        glemBruker = view.findViewById(R.id.glem_meg);
        glemBruker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glemInnloggetBruker();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater layouten for fragmentet
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }

    private void glemInnloggetBruker() {
        // Henter den huskede login-informasjonen
        pref2 = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor2 = pref2.edit();
        // Erstatter informasjonen med tomme strenger
        editor2.putString("epost", "");
        editor2.putString("passord", "");
        editor2.apply();
        // Sender brukeren tilbake til innlogging
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

}