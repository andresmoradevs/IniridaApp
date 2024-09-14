package com.afmv.iniridaapp.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.afmv.iniridaapp.FullScreenImageActivity;
import com.afmv.iniridaapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> imageUrls;
    private LayoutInflater layoutInflater;

    public ImagePagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.image_item_layout, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        String imageUrl = imageUrls.get(position);

        // Cargar la imagen con Glide
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        // Configurar el click listener
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("imageUrl", imageUrl);
            context.startActivity(intent);
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}


