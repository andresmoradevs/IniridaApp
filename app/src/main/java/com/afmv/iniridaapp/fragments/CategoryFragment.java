package com.afmv.iniridaapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.SharedPreferences;
import android.widget.Toast;

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

public class CategoryFragment extends Fragment {

    RecyclerView recyclerView;
    List<StoreItem> businessList = new ArrayList<>();
    BusinessAdapter adapter;
    FirebaseDatabase mDatabase;
    DatabaseReference storeReference;
    private String selectedCategory;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategoryItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar el adaptador
        adapter = new BusinessAdapter(getContext(), businessList);
        recyclerView.setAdapter(adapter);

        // Obtener el nombre de la categoría seleccionada desde el bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedCategory = bundle.getString("selected_category");
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

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedCategory = bundle.getString("selected_category");
        }
        //Log.d("results-> ", ""+selectedCategory.toString());
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

    // Filtrar los negocios por la categoría seleccionada
    private void filterBusinessesByCategory() {
        List<StoreItem> filteredList = new ArrayList<>();
        for (StoreItem item : businessList) {
            if (item.getCategoria().equals(selectedCategory)) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }
}
