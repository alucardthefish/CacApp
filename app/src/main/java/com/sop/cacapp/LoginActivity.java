package com.sop.cacapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mButtonToLogin;
    private LoaderDialog loadingDialog;
    private TextView tvLinkerTowardsRegister;

    private String email = "";
    private String password = "";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoaderDialog(LoginActivity.this);
        tvLinkerTowardsRegister = findViewById(R.id.tvLinkerTowardsRegister);

        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPassword = findViewById(R.id.editTextPassword);

        tvLinkerTowardsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mButtonToLogin = findViewById(R.id.btn_tologin);
        mButtonToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEditTextEmail.getText().toString();
                password = mEditTextPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    // Send data to log in
                    loginUser();
                } else {
                    Toast.makeText(LoginActivity.this, "Datos deben ser llenados", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginUser() {
        Toast.makeText(LoginActivity.this, "Ingresando...", Toast.LENGTH_LONG).show();
        loadingDialog.startLoading();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Send to activity main
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Hubo un error", Toast.LENGTH_LONG).show();
                    Log.d("LoginUser", task.getException().getMessage());
                    loadingDialog.stopLoading();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.stopLoading();
    }
}
