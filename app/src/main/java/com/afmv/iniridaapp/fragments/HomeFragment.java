package com.afmv.iniridaapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.models.StoreItem;
import com.afmv.iniridaapp.utils.ImageCarouselAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private List<String> imageUrls = new ArrayList<>();
    private ImageCarouselAdapter adapter;
    DatabaseReference imagesReference;
    FirebaseDatabase mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        mDatabase = FirebaseDatabase.getInstance();
        viewPager = view.findViewById(R.id.viewPager);
        imageUrls = new ArrayList<>();  // Asegúrate de inicializar la lista
        adapter = new ImageCarouselAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);

        imagesReference = mDatabase.getReference("store");

        imagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageUrls.clear();  // Limpia la lista antes de agregar nuevos elementos
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Accede al campo 'imagenes' y a 'image1'
                    if (dataSnapshot.child("imagenes").child("image1").exists()) {
                        String imageUrl = dataSnapshot.child("imagenes").child("image1").getValue(String.class);
                        imageUrls.add(imageUrl);
                    }
                }
                // Notifica al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("result", "Error: " + error.getMessage());
            }
        });


        // Obtener las imágenes desde Firebase
        /*DatabaseReference storeItemsReference = FirebaseDatabase.getInstance().getReference("store");
        storeItemsReference.child("imagenes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.getValue(String.class);
                    imageUrls.add(imageUrl);
                    Log.d("result",""+snapshot.getValue());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores
            }
        });*/


        return view;
    }
}
