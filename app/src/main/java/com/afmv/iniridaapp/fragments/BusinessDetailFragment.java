package com.afmv.iniridaapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.models.StoreItem;
import com.afmv.iniridaapp.utils.ImagePagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BusinessDetailFragment extends Fragment {

    private TextView nombreTextView, contactoTextView, descripcionTextView;
    private ViewPager viewPager;
    private ImageButton callButton;
    private StoreItem business;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_detail, container, false);

        nombreTextView = view.findViewById(R.id.nombreTextView);
        contactoTextView = view.findViewById(R.id.contactoTextView);
        descripcionTextView = view.findViewById(R.id.descripcionTextView);
        viewPager = view.findViewById(R.id.imageViewPager);
        callButton = view.findViewById(R.id.callButton);

        // Obtener los detalles del negocio del bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            business = (StoreItem) bundle.getSerializable("business_details");
        }

        // Mostrar los detalles del negocio
        if (business != null) {
            nombreTextView.setText(business.getNombre());
            contactoTextView.setText(business.getContacto());
            descripcionTextView.setText(business.getDescripcion());

            // Configurar el botón de llamada
            callButton.setOnClickListener(v -> makePhoneCall(business.getContacto()));

            // Cargar las imágenes en el ViewPager
            loadImagesIntoViewPager(business.getImagenes());
        }

        return view;
    }

    private void loadImagesIntoViewPager(Map<String, String> images) {
        List<String> imageUrls = new ArrayList<>(images.values());
        ImagePagerAdapter adapter = new ImagePagerAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
