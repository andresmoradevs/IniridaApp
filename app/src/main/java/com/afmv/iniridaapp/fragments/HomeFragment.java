package com.afmv.iniridaapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.utils.ImageCarouselAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bumptech.glide.Glide.with;

public class HomeFragment extends Fragment {

    ViewPager2 viewPager;
    List<String> imageUrls = new ArrayList<>();
    ImageCarouselAdapter adapter;
    ImageView iv1, iv2, iv3, iv4;
    DatabaseReference imagesReference;
    FirebaseDatabase mDatabase;

    Handler handler;
    Runnable runnable;
    int currentPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        iv1 = view.findViewById(R.id.iv_categoria1);
        iv2 = view.findViewById(R.id.iv_categoria2);
        iv3 = view.findViewById(R.id.iv_categoria3);
        iv4 = view.findViewById(R.id.iv_categoria4);

        mDatabase = FirebaseDatabase.getInstance();
        viewPager = view.findViewById(R.id.viewPagerHome);
        adapter = new ImageCarouselAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);

        imagesReference = mDatabase.getReference("store");
        imagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageUrls.clear();  // Limpia la lista antes de agregar nuevos elementos
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("imagenes").child("image1").exists()) {
                        String imageUrl = dataSnapshot.child("imagenes").child("image1").getValue(String.class);
                        imageUrls.add(imageUrl);
                    }
                }
                adapter.notifyDataSetChanged();
                setupAutoScroll();  // Inicia el scroll automático
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("result", "Error: " + error.getMessage());
            }
        });

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCategoryFragment("Alimentos y hospitalidad");
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCategoryFragment("Movilidad y transporte");
            }
        });

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCategoryFragment("Servicios especializados");
            }
        });

        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCategoryFragment("Hogar, artículos y servicios");
            }
        });




        return view;
    }
    // Método para navegar al nuevo fragmento con la categoría seleccionada
    private void navigateToCategoryFragment(String category) {
        CategoryFragment categoryFragment = new CategoryFragment();

        // Crea un bundle para pasar la categoría
        Bundle bundle = new Bundle();
        bundle.putString("selected_category", category);
        categoryFragment.setArguments(bundle);

        // Realiza la transacción del fragmento
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, categoryFragment) // Usa el ID del contenedor de tu fragmento
                .addToBackStack(null) // Agrega a la pila de retroceso para que el usuario pueda regresar
                .commit();
    }

    private void setupAutoScroll() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                // Mueve al siguiente ítem del ViewPager2
                if (currentPosition == adapter.getItemCount()) {
                    currentPosition = 0;  // Si es la última imagen, vuelve a la primera
                }
                viewPager.setCurrentItem(currentPosition++, true);  // Activa el scroll suave
                handler.postDelayed(this, 3000);  // Cambia las imágenes cada 3 segundos
            }
        };
        handler.postDelayed(runnable, 3000);  // Inicia el auto-scroll después de 3 segundos
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);  // Detén el carrusel automático cuando el fragmento se pausa
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null && runnable != null) {
            handler.postDelayed(runnable, 3000);  // Reinicia el auto-scroll cuando el fragmento se reanuda
        }
    }
}

