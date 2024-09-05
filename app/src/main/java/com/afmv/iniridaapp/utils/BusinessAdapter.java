package com.afmv.iniridaapp.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.afmv.iniridaapp.R;

import com.afmv.iniridaapp.fragments.BusinessDetailFragment;
import com.afmv.iniridaapp.models.StoreItem;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {

    private Context context;
    private List<StoreItem> businessList;

    public BusinessAdapter(Context context, List<StoreItem> businessList) {
        this.context = context;
        this.businessList = businessList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.business_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreItem business = businessList.get(position);
        holder.businessName.setText(business.getNombre());

        // Al hacer clic en el negocio, se abre el fragmento de detalles
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBusinessDetailsFragment(business);
            }
        });
    }
    public void updateList(List<StoreItem> filteredList) {
        this.businessList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView businessName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            businessName = itemView.findViewById(R.id.businessName);
        }
    }

    private void openBusinessDetailsFragment(StoreItem business) {
        BusinessDetailFragment detailsFragment = new BusinessDetailFragment();

        // Pasar el objeto completo StoreItem
        Bundle bundle = new Bundle();
        bundle.putSerializable("business_details", business);
        detailsFragment.setArguments(bundle);

        // Reemplazar el fragmento
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, detailsFragment)
                .addToBackStack(null)
                .commit();
    }
}


