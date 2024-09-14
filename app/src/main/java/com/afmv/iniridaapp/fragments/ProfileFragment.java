package com.afmv.iniridaapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.afmv.iniridaapp.R;
import com.afmv.iniridaapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText etName, etNumber, etMail, etPass;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference userReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        etName = view.findViewById(R.id.et_name);
        etNumber = view.findViewById(R.id.et_number);
        etMail = view.findViewById(R.id.et_mail);
        etPass = view.findViewById(R.id.et_pass);
        btnUpdate = view.findViewById(R.id.register);
        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = getContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        String userUID = preferences.getString("userUID", "");
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userReference = mDatabase.getReference().child("users").child(userUID);

        loadUserData();

        btnUpdate.setOnClickListener(v -> updateUserProfile());
    }

    private void loadUserData() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        etName.setText(user.getName());
                        etNumber.setText(user.getPhone());
                        etMail.setText(user.getEmail());
                        // No establecemos la contraseña por razones de seguridad
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String name = etName.getText().toString().trim();
        String phone = etNumber.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Actualizar email en Firebase Auth si ha cambiado
            if (!email.equals(user.getEmail())) {
                user.updateEmail(email)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Error al actualizar email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            // Actualizar contraseña si se ha proporcionado una nueva
            if (!password.isEmpty()) {
                user.updatePassword(password)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Error al actualizar contraseña: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            // Actualizar datos en Realtime Database
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("phone", phone);
            updates.put("email", email);

            userReference.updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar perfil: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}