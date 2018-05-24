package com.example.rouge.a6120hjemmeeksamen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DashboardFragment extends Fragment {

    TextView avreiseSted, ankomstSted, avreiseDato, avreiseKl;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        avreiseSted = getActivity().findViewById(R.id.avreise_sted);
        ankomstSted = getActivity().findViewById(R.id.ankomst_sted);
        avreiseDato = getActivity().findViewById(R.id.avreise_dato);
        avreiseKl = getActivity().findViewById(R.id.avreisekl);

        getActivity().setTitle("Dashboard");
        // TODO fylle dashboardet med informasjon om brukerens første oppkommende tur

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater layouten for fragmentet
        return inflater.inflate(R.layout.fragment_dashboard, container, false);

    }

}