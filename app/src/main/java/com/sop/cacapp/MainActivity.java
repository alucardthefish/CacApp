package com.sop.cacapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnExit;
    private TextView tvUserName;
    private ImageView ivUserPic;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        tvUserName = findViewById(R.id.tvUserName);

        ivUserPic = findViewById(R.id.ivLogo);

        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close or log out
                Toast.makeText(MainActivity.this, "Adios, que se diviertan", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            tvUserName.setText("Usuario: " + currentUser.getDisplayName());
            ivUserPic.setImageResource(R.drawable.icons8femaleprofile100);
            if (currentUser.getPhotoUrl().toString().equals("male")) {
                ivUserPic.setImageResource(R.drawable.icons8maleuser100);
            }

        }
    }
}
