package com.sop.cacapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
    private EditText etName;
    private EditText etAge;
    private EditText etHeight;
    private EditText etWeight;
    private RadioGroup rgGender;
    private Button btnRegister;

    private String email;
    private String pass;
    private String name;
    private int age;
    private int height;
    private double weight;
    private String gender;

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
        etName = findViewById(R.id.etUserName);
        etAge = findViewById(R.id.etUserAge);
        etHeight = findViewById(R.id.etUserHeight);
        etWeight = findViewById(R.id.etUserWeight);
        rgGender = findViewById(R.id.genderOptions);
        btnRegister = findViewById(R.id.btnToRegister);

        email = "";
        pass = "";
        name = "";
        age = 0;
        height = 0;
        weight = 0.0;
        gender = "female";
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
                name = etName.getText().toString();
                age =  Integer.parseInt(etAge.getText().toString());
                height = Integer.parseInt(etHeight.getText().toString());
                weight = Double.parseDouble(etWeight.getText().toString());
                gender = checkGender();
                if (!email.isEmpty() && !pass.isEmpty() && !name.isEmpty()) {
                    registerUser();
                } else {
                    Toast.makeText(RegisterActivity.this, "Los campos con * no son obligatorios", Toast.LENGTH_SHORT).show();
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

                    Profile profile = new Profile(name,
                            email,
                            age,
                            height,
                            weight,
                            gender);

                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(Uri.parse(gender)) // Set url gender
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Perfil creado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Perfil no se pudo crear", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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

    private String checkGender() {
        String gender = "female";

        if (rgGender.getCheckedRadioButtonId() == R.id.rbMale) {
            gender = "male";
        }
        return gender;
    }
}
