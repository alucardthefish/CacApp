package com.sop.cacapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sop.cacapp.Fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    private Button btnExit;
    private TextView tvUserName;
    private ImageView ivUserPic;
    private FirebaseAuth mAuth;

    // New
    private TextView tvHeaderUserName;
    private TextView tvHeaderUserEmail;
    private ImageView ivHeaderUserIcon;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    // variables for loading fragment
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    // End new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        // Load main fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new MainFragment());
        fragmentTransaction.commit();

        tvHeaderUserName = findViewById(R.id.tvHeaderUserEmail);
        tvHeaderUserEmail = findViewById(R.id.tvHeaderUserEmail);
        ivHeaderUserIcon = findViewById(R.id.ivHeaderImage);

        /*tvUserName = findViewById(R.id.tvUserName);

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
        });*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            tvUserName.setText("Usuario: " + currentUser.getDisplayName());
            ivUserPic.setImageResource(R.drawable.icons8femaleprofile100);
            if (currentUser.getPhotoUrl().toString().equals("male")) {
                ivUserPic.setImageResource(R.drawable.icons8maleuser100);
            }

        }*/
        if (currentUser != null) {
            tvHeaderUserName.setText(currentUser.getDisplayName());
            tvHeaderUserEmail.setText(currentUser.getEmail());
            if (currentUser.getPhotoUrl().toString().equals("male")) {
                ivHeaderUserIcon.setImageResource(R.drawable.icons8maleuser100);
            } else {
                ivHeaderUserIcon.setImageResource(R.drawable.icons8femaleprofile100);
            }
        }
    }
}
