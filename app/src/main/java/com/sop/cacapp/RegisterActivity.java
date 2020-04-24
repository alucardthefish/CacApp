package com.sop.cacapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sop.cacapp.Classes.Profile;
import com.sop.cacapp.Persistence.ProfilePersistent;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private EditText etName;
    private EditText etDateOfBirth;
    private RadioGroup rgGender;
    private Button btnRegister;
    private LoaderDialog loadingDialog;

    private String email;
    private String pass;
    private String name;
    private String gender;
    private Timestamp dateOfBirth;

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
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        rgGender = findViewById(R.id.genderOptions);
        btnRegister = findViewById(R.id.btnToRegister);
        loadingDialog = new LoaderDialog(RegisterActivity.this);

        email = "";
        pass = "";
        name = "";
        gender = "female";
        dateOfBirth = null;

        initListeners();
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
                gender = checkGender();
                if (!email.isEmpty() && !pass.isEmpty() && !name.isEmpty()) {
                    registerUser();
                } else {
                    Toast.makeText(RegisterActivity.this, "Los campos con * son obligatorios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser() {
        loadingDialog.startLoading();
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registrando...", Toast.LENGTH_LONG).show();

                    sendVerificationEmail();

                    final Profile profile = new Profile();
                    profile.setName(name);
                    profile.setEmail(email);
                    profile.setGender(gender);
                    if (dateOfBirth != null) {
                        profile.setBirthDate(dateOfBirth);
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(Uri.parse(gender)) // Set url gender
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                                createUserProfile(profile);
                            } else {
                                loadingDialog.stopLoading();
                                Toast.makeText(RegisterActivity.this, "Perfil no se pudo crear y actualizar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    loadingDialog.stopLoading();
                    Toast.makeText(RegisterActivity.this, "Ya existe una cuenta asociada a este correo o: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUserProfile(Profile profile) {
        loadingDialog.startLoading();
        ProfilePersistent profilePersistent = new ProfilePersistent();
        profilePersistent.saveProfileAndInit(profile, new ProfilePersistent.OnCreateProfileListener() {
            @Override
            public void onCallBack(boolean isSuccess) {
                if (isSuccess) {
                    Toast.makeText(RegisterActivity.this, "Perfil de usuario creado!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    loadingDialog.stopLoading();
                    Toast.makeText(RegisterActivity.this, "No se pudo crear el perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.stopLoading();
    }

    private String checkGender() {
        String gender = "female";

        if (rgGender.getCheckedRadioButtonId() == R.id.rbMale) {
            gender = "male";
        }
        return gender;
    }

    private void initListeners() {
        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String pattern = "dd MMMM yyyy";
                        Locale current = getResources().getConfiguration().locale;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        dateOfBirth = new Timestamp(newDate.getTime());
                        etDateOfBirth.setText(new SimpleDateFormat(pattern, current).format(newDate.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });
    }

    private void sendVerificationEmail() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, String.format("Email enviado a %s. Verifica en tu correo el link para activar cuenta", user.getEmail()), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("VerificationEmail", "onFailure: Email no fue enviado. " + e.getMessage());
            }
        });
    }
}
