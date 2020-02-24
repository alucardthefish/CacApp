package com.sop.cacapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sop.cacapp.Object.Profile;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private Button btnRegister;

    private String email;
    private String pass;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.eTRegisterEmail);
        etPass = findViewById(R.id.eTRegisterPassword);
        btnRegister = findViewById(R.id.btnToRegister);

        email = "";
        pass = "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()) {
                    registerUser();
                } else {
                    Toast.makeText(RegisterActivity.this, "Los campos no deben ser vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser() {

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registrado exitosamente!", Toast.LENGTH_LONG).show();
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", email);
                    map.put("pass", pass);

                    Profile profile = new Profile();

                    FirebaseUser user = mAuth.getCurrentUser();
                    String id = user.getUid();

                    mDataBase.collection("users")
                            .document(id)
                            .collection("data")
                            .document("profile")
                            .set(profile)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Ya existe una cuenta asociada a este correo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
