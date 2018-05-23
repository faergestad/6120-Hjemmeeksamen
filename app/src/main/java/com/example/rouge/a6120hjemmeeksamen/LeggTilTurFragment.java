package com.example.rouge.a6120hjemmeeksamen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Integer.parseInt;


public class LeggTilTurFragment extends Fragment {

    String melding, ID_Holder, Beskrivelse_Holder, Pris_Holder, LagerAnt_Holder;
    EditText leggTilId, leggTilBeskrivelse, leggTilPris, leggTilLagerAntall;
    Button leggTilVare;
    JSONObject jsonVare;
    Boolean CheckEditText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        getActivity().setTitle("Opprett ny tur");

        leggTilId = view.findViewById(R.id.varekode);
        leggTilBeskrivelse = view.findViewById(R.id.betegnelse);
        leggTilPris = view.findViewById(R.id.pris);
        leggTilLagerAntall = view.findViewById(R.id.lager_antall);
        leggTilVare = view.findViewById(R.id.registrerVare);

        leggTilVare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {

                    int id = Integer.parseInt(leggTilId.getText().toString());
                    String desc = leggTilBeskrivelse.getText().toString();
                    double pris = Double.parseDouble(leggTilPris.getText().toString());
                    int lagerAntall = parseInt(leggTilLagerAntall.getText().toString());

                    //Tur tur = new Tur(id, desc, pris, lagerAntall, 1);
                    //jsonVare = tur.lagJSONObject();

                    LeggTilTurFragment.NyVare nyVare = new LeggTilTurFragment.NyVare();
                    nyVare.execute();
                } else {
                    Toast.makeText(getContext(), "Fyll ut alle feltene", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_legg_til_tur, container, false);
    }

    public class NyVare extends AsyncTask<String, String, Long> {

        HttpURLConnection connection = null;

        @Override
        protected Long doInBackground(String... params) {
            try {
                URL url = new URL("http://10.0.2.2/hobbyhuset/api.php/Tur");
                //URL url = new URL("http://192.168.10.171/hobbyhuset/api.php/Vare");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setChunkedStreamingMode(0);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.connect();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsonVare.toString());
                out.close();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String responseString;
                StringBuilder sb = new StringBuilder();
                while ((responseString = reader.readLine()) != null) {
                    sb = sb.append(responseString);
                }
                String response = sb.toString();
                melding = response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("IOException");
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert connection != null;
                connection.disconnect();
            } return (0L);
        }

        @Override
        protected void onPostExecute(Long result) {

            leggTilId.setText("");
            leggTilBeskrivelse.setText("");
            leggTilPris.setText("");
            leggTilLagerAntall.setText("");

            if (melding.equals("null") ) {
                Toast.makeText(getActivity(), "Varen eksisterer allerede", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "Tur Opprettet", Toast.LENGTH_SHORT).show();
                Log.d("feil", "" + melding);
            }
        }

    }

    public void CheckEditTextIsEmptyOrNot() {

        ID_Holder = leggTilId.getText().toString();
        Beskrivelse_Holder = leggTilBeskrivelse.getText().toString();
        Pris_Holder = leggTilPris.getText().toString();
        LagerAnt_Holder = leggTilLagerAntall.getText().toString();

        if(TextUtils.isEmpty(ID_Holder) || TextUtils.isEmpty(Beskrivelse_Holder) || TextUtils.isEmpty(Pris_Holder) || TextUtils.isEmpty(LagerAnt_Holder))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;
        }
    }

}