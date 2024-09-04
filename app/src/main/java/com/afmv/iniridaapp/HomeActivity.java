package com.afmv.iniridaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afmv.iniridaapp.fragments.ContactUsFragment;
import com.afmv.iniridaapp.fragments.HomeFragment;
import com.afmv.iniridaapp.fragments.ProfileFragment;
import com.afmv.iniridaapp.fragments.IncidentReportsFragment;
import com.afmv.iniridaapp.fragments.ServicesFragment;
import com.afmv.iniridaapp.fragments.AboutUsFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FirebaseDatabase database;
    DatabaseReference usersReference;
    FirebaseAuth mAuth;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (!preferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up user info in nav header
        View headerView = navigationView.getHeaderView(0);
        //TextView userName = headerView.findViewById(R.id.tv_user_name);
        TextView userEmail = headerView.findViewById(R.id.tv_user_email);
        ImageView profileImage = headerView.findViewById(R.id.image_view);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //userName.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());
            // Load profile image here if needed
        }

        navigationView.setNavigationItemSelectedListener(this);

        // Show HomeFragment by default
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.nav_inicio:
                selectedFragment = new HomeFragment();
                break;
            case R.id.nav_sobre_nosotros:
                selectedFragment = new AboutUsFragment();
                break;
            case R.id.nav_perfil:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.nav_servicios:
                selectedFragment = new ServicesFragment();
                break;
            case R.id.nav_contactanos:
                selectedFragment = new ContactUsFragment();
                break;
            case R.id.nav_reporta_incidentes:
                selectedFragment = new IncidentReportsFragment();
                break;
            case R.id.nav_logout:
                logout();
                return true;
        }
        if (selectedFragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, selectedFragment).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // Sign out from Firebase
        mAuth.signOut();

        // Clear login state
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userUID");
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}


