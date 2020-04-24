package com.sop.cacapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button btnEmailVerification;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initViews();
        initListeners();
    }

    private void initViews() {
        btnEmailVerification = findViewById(R.id.btnEmailVerification);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void initListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoaderDialog loaderDialog = new LoaderDialog(EmailVerificationActivity.this);
                loaderDialog.startLoading();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.reload()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (user.isEmailVerified()) {
                                    loaderDialog.stopLoading();
                                    Toast.makeText(EmailVerificationActivity.this, "La cuenta ya fue verificada", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    loaderDialog.stopLoading();
                                    Toast.makeText(EmailVerificationActivity.this, "La cuenta no ha sido verificada aun", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        btnEmailVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });
    }

    private void sendVerificationEmail() {
        mUser.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EmailVerificationActivity.this, String.format("Email enviado a %s. Verifica en tu correo el link para activar cuenta", mUser.getEmail()), Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EmailVerification", "onFailure: Hubo un error enviando el Email. " + e.getMessage());
            }
        });
    }
}
