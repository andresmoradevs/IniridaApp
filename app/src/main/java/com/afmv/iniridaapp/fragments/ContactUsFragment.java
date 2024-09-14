package com.afmv.iniridaapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afmv.iniridaapp.R;


public class ContactUsFragment extends Fragment {

    ImageView fbImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contactanos, container, false);
        fbImage = (ImageView) view.findViewById(R.id.fb);
        fbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // URL de la página de Facebook
                String facebookUrl = "https://www.facebook.com/profile.php?id=61564142385186";

                // Crear un Intent para abrir el navegador o la app de Facebook
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));

                // Verificar si hay una aplicación que pueda manejar el Intent
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "No hay una aplicación disponible para abrir el enlace", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}