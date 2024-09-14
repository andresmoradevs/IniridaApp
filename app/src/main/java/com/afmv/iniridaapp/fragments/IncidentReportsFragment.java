package com.afmv.iniridaapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afmv.iniridaapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class IncidentReportsFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private ImageView imageView;
    private EditText etTitle, etDescription;
    private Button btnSubmit;
    private Uri imageUri;
    FirebaseAuth mAuth;

    private DatabaseReference databaseRef;
    private StorageReference storageRef, fileReference;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(this).load(imageUri).into(imageView);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reporta_incidentes, container, false);

        mAuth = FirebaseAuth.getInstance();
        imageView = view.findViewById(R.id.image);
        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        btnSubmit = view.findViewById(R.id.btn_submit);

        databaseRef = FirebaseDatabase.getInstance().getReference("incident_reports");
        storageRef = FirebaseStorage.getInstance().getReference("incident_images");

        imageView.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnSubmit.setOnClickListener(v -> submitReport());



        return view;
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        mGetContent.launch("image/*");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitReport() {
        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // El usuario no ha iniciado sesión, redirígelo al login o muéstrale un mensaje.
            Log.e("AuthError", "Usuario no autenticado");
        } else {
            // El usuario está autenticado, continúa con el proceso de subir o descargar archivos.
            String uid = currentUser.getUid();
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(getContext(), "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            String reportId = databaseRef.push().getKey();
            fileReference = storageRef.child(reportId + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        IncidentReport report = new IncidentReport(reportId, title, description, uri.toString());
                        databaseRef.child(reportId).setValue(report)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Reporte enviado con éxito", Toast.LENGTH_SHORT).show();
                                        clearForm();
                                    } else {
                                        Toast.makeText(getContext(), "Error al enviar el reporte", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error a lcargar imagen " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }

    }

    private void clearForm() {
        etTitle.setText("");
        etDescription.setText("");
        imageView.setImageResource(R.drawable.no_fotos);
        imageUri = null;
    }

    private static class IncidentReport {
        public String id;
        public String title;
        public String description;
        public String imageUrl;

        public IncidentReport(String id, String title, String description, String imageUrl) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
        }
    }

    @Override
    public void onStart() {
         // Check if user is signed in (non-null) and update UI accordingly
         super.onStart();

    }
}