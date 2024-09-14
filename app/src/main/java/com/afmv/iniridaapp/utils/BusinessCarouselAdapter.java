package com.afmv.iniridaapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.models.StoreItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class BusinessCarouselAdapter extends RecyclerView.Adapter<BusinessCarouselAdapter.BusinessViewHolder> {

    private List<StoreItem> businessList;
    private Context context;
    private OnCallButtonClickListener callButtonClickListener;

    public interface OnCallButtonClickListener {
        void onCallButtonClick(String phoneNumber);
    }

    public BusinessCarouselAdapter(Context context, List<StoreItem> businessList, OnCallButtonClickListener listener) {
        this.context = context;
        this.businessList = businessList;
        this.callButtonClickListener = listener;
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business_carousel, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        StoreItem business = businessList.get(position);
        holder.titleTextView.setText(business.getNombre());
        holder.phoneTextView.setText(business.getContacto());

        // Cargar la primera imagen usando Glide
        if (business.getImagenes() != null && business.getImagenes().containsKey("image1")) {
            Glide.with(context)
                    .load(business.getImagenes().get("image1"))
                    .into(holder.imageView);
        }

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callButtonClickListener != null) {
                    callButtonClickListener.onCallButtonClick(business.getContacto());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public void updateList(List<StoreItem> newList) {
        this.businessList = newList;
        notifyDataSetChanged();
    }

    static class BusinessViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView phoneTextView;
        ImageView callButton;

        BusinessViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.businessImageView);
            titleTextView = itemView.findViewById(R.id.businessTitleTextView);
            phoneTextView = itemView.findViewById(R.id.businessPhoneTextView);
            callButton = itemView.findViewById(R.id.callButton);
        }
    }
}
