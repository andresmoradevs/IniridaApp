package com.afmv.iniridaapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.models.StoreItem;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<StoreItem, ServiceViewHolder> adapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servicios, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("store");

        FirebaseRecyclerOptions<StoreItem> options =
                new FirebaseRecyclerOptions.Builder<StoreItem>()
                        .setQuery(databaseReference, StoreItem.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<StoreItem, ServiceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ServiceViewHolder holder, int position, StoreItem model) {
                holder.setName(model.getNombre());
                holder.setCategory(model.getCategoria());
                holder.setContact(model.getContacto());
                holder.setDescription(model.getDescripcion());
                holder.setImage(model.getImagenes() != null && !model.getImagenes().isEmpty()
                        ? model.getImagenes().values().iterator().next()
                        : null);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openBusinessDetailsFragment(model);

                    }
                });
            }

            @Override
            public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_store, parent, false);
                return new ServiceViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name) {
            TextView textViewName = mView.findViewById(R.id.text_view_name);
            textViewName.setText(name);
        }

        public void setCategory(String category) {
            TextView textViewCategory = mView.findViewById(R.id.text_view_category);
            textViewCategory.setText(category);
        }

        public void setContact(String contact) {
            TextView textViewContact = mView.findViewById(R.id.text_view_contact);
            textViewContact.setText(contact);
        }

        public void setDescription(String description) {
            TextView textViewDescription = mView.findViewById(R.id.text_view_description);
            textViewDescription.setText(description);
        }

        public void setImage(String imageUrl) {
            ImageView imageView = mView.findViewById(R.id.image_view_service);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(mView.getContext())
                        .load(imageUrl)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.alimentos);
            }
        }

    }
    private void openBusinessDetailsFragment(StoreItem business) {
        BusinessDetailFragment detailsFragment = new BusinessDetailFragment();

        // Pasar el objeto completo StoreItem
        Bundle bundle = new Bundle();
        bundle.putSerializable("business_details", business);
        detailsFragment.setArguments(bundle);

        // Reemplazar el fragmento
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, detailsFragment)
                .addToBackStack(null)
                .commit();
    }
}