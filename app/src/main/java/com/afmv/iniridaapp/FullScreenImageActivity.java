package com.afmv.iniridaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(fullScreenImageView);
        }

        // Configurar la actividad para pantalla completa
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Configurar click para cerrar la actividad
        fullScreenImageView.setOnClickListener(v -> finish());
    }
}