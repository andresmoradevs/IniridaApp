package com.afmv.iniridaapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import com.afmv.iniridaapp.utils.BusinessCarouselAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.models.StoreItem;
import com.afmv.iniridaapp.utils.BusinessAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements BusinessCarouselAdapter.OnCallButtonClickListener {

    RecyclerView recyclerView;
    List<StoreItem> businessList = new ArrayList<>();
    BusinessAdapter adapter;
    FirebaseDatabase mDatabase;
    DatabaseReference storeReference;
    private String selectedCategory;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    BusinessCarouselAdapter carouselAdapter;
    ViewPager2 viewPager;
    TextView titleCategory;

    Handler handler;
    Runnable runnable;
    int currentPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        titleCategory = view.findViewById(R.id.tv_category);
        recyclerView = view.findViewById(R.id.recyclerViewCategoryItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewPager = view.findViewById(R.id.viewPager);
        carouselAdapter = new BusinessCarouselAdapter(getContext(), businessList, this);

        // Inicializar el adaptador
        adapter = new BusinessAdapter(getContext(), businessList);
        recyclerView.setAdapter(adapter);
        viewPager.setAdapter(carouselAdapter);

        // Obtener el nombre de la categoría seleccionada desde el bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedCategory = bundle.getString("selected_category");
            titleCategory.setText(selectedCategory);
            Log.d("category -> ",""+selectedCategory);

        }

        // Inicializar SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("BusinessData", Context.MODE_PRIVATE);

        // Ver si los datos ya están guardados en SharedPreferences
        String businessJson = sharedPreferences.getString("businessList", null);
        if (businessJson != null) {
            // Si los datos ya están guardados, cargar desde SharedPreferences
            Type type = new TypeToken<List<StoreItem>>() {}.getType();
            businessList = gson.fromJson(businessJson, type);
            filterBusinessesByCategory();
        } else {
            // Si no están, obtener los datos de Firebase y guardarlos en SharedPreferences
            fetchBusinessesFromFirebase();
        }

        return view;
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
                handler.postDelayed(this, 5000);  // Cambia las imágenes cada 3 segundos
            }
        };
        handler.postDelayed(runnable, 4000);  // Inicia el auto-scroll después de 3 segundos
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedCategory = bundle.getString("selected_category");
        }
        //Log.d("results-> ", ""+selectedCategory.toString());
        setupAutoScroll();  // Inicia el scroll automático
    }

    // Método para obtener negocios desde Firebase
    private void fetchBusinessesFromFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        storeReference = mDatabase.getReference().child("store");

        storeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                businessList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StoreItem storeItem = dataSnapshot.getValue(StoreItem.class);
                    businessList.add(storeItem);
                }

                // Guardar los datos en SharedPreferences
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                String jsson = gson.toJson(businessList);
                editor.putString("businessList", jsson);
                editor.apply();

                filterBusinessesByCategory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al obtener locales, por favor intente mas tarde..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Modificar el método filterBusinessesByCategory
    private void filterBusinessesByCategory() {
        List<StoreItem> filteredList = new ArrayList<>();
        for (StoreItem item : businessList) {
            if (item.getCategoria().equals(selectedCategory)) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
        carouselAdapter.updateList(filteredList);

    }

    @Override
    public void onCallButtonClick(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
