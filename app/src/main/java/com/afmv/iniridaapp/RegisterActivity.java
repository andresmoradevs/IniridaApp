package com.afmv.iniridaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

//    SharedPreferences preferences;
    ImageView btnBack;
    Button btnRegister;
    DrawerLayout drawerLayout;
    SharedPreferences preferences;
    ProgressBar progressBar;
    EditText name, number, email, pass;
    TextView haveAccount;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference usersReference;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        @SuppressLint("CommitPrefEdits")
//        SharedPreferences.Editor editor = preferences.edit();
//        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (preferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
            return;
        }

        btnBack = findViewById(R.id.back);
        btnRegister = findViewById(R.id.register);
        progressBar = findViewById(R.id.progress_bar);
        mDatabase = FirebaseDatabase.getInstance();
        usersReference = mDatabase.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.et_name);
        number = findViewById(R.id.et_number);
        email = findViewById(R.id.et_mail);
        pass = findViewById(R.id.et_pass);
        haveAccount = findViewById(R.id.have_account);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();

                                    HashMap<String, Object> userData = new HashMap<>();
                                    userData.put("name", name.getText().toString());
                                    userData.put("phone", number.getText().toString());
                                    userData.put("email", email.getText().toString());
                                    userData.put("password", pass.getText().toString());
                                    userData.put("authID", userId);
                                    userData.put("role", "client");

                                    usersReference.child(userId).setValue(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task2) {

                                                    if (task2.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Se registro correctamente!", Toast.LENGTH_SHORT).show();
//                                                        editor.putString("userUID", userId);
//                                                        editor.apply();
                                                        name.setText("");
                                                        number.setText("");
                                                        email.setText("");
                                                        pass.setText("");
                                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "No se logró realizar el registro!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "No se logró realizar el registro!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}