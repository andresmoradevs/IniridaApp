package com.afmv.iniridaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;
    ImageView btnBack;
    Button btnLogin;
    EditText email, pass;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (preferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        btnBack = findViewById(R.id.back);
        btnLogin = findViewById(R.id.login);
        email = findViewById(R.id.et_mail);
        pass = findViewById(R.id.et_pass);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        usersReference = mDatabase.getReference("users");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Los campos correo y contraseña son obligatorios...", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser();
                }
            }
        });

    }
    private void loginUser() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save login state
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userUID", mAuth.getCurrentUser().getUid());

                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo iniciar sesión, intenta nuevamente...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}