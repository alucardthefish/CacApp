package com.sop.cacapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sop.cacapp.CustomViewClasses.Utils;
import com.sop.cacapp.Fragments.FecalDiaryFragment;
import com.sop.cacapp.Fragments.DepositionMainFragment;
import com.sop.cacapp.Fragments.PoopStatisticsFragment;
import com.sop.cacapp.Fragments.ProfileFragment;
import com.sop.cacapp.Fragments.SymptomsMainFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // New Navigation Drawer variables
    private TextView tvHeaderUserName;
    private TextView tvHeaderUserEmail;
    private ImageView ivHeaderUserIcon;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    // variables for loading fragment
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FirebaseUser currentUser;
    // End new

    private LoaderDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAIN", "onCreate");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            if (currentUser.isEmailVerified()) {
                // Load main fragment
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, new ProfileFragment());
                fragmentTransaction.commit();
            } else {
                Intent intent = new Intent(MainActivity.this, EmailVerificationActivity.class);
                startActivity(intent);
                finish();
            }

        }

        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        loaderDialog = new LoaderDialog(MainActivity.this);

        View header = navigationView.getHeaderView(0); // Instance of header view layout
        tvHeaderUserName = header.findViewById(R.id.tvHeaderUserName);
        tvHeaderUserEmail = header.findViewById(R.id.tvHeaderUserEmail);
        ivHeaderUserIcon = header.findViewById(R.id.ivHeaderImage);

        Utils.setSystemBarColor(this, R.color.colorToolbar);

        // Initialize listeners
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MAIN", "onStart");

        if (currentUser != null) {
            tvHeaderUserName.setText(currentUser.getDisplayName());
            tvHeaderUserEmail.setText(currentUser.getEmail());
            if (currentUser.getPhotoUrl().toString().equals("male")) {
                ivHeaderUserIcon.setImageResource(R.drawable.icons8maleuser100);
            } else {
                ivHeaderUserIcon.setImageResource(R.drawable.icons8femaleprofile100);
            }
        } else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void initListeners() {
        // Set event handler for navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(Gravity.START);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        fragmentTransaction.replace(R.id.container, new ProfileFragment());
                        break;
                    case R.id.symptomsHome:
                        fragmentTransaction.replace(R.id.container, new SymptomsMainFragment());
                        break;
                    case R.id.depositionHome:
                        fragmentTransaction.replace(R.id.container, new DepositionMainFragment());
                        break;
                    case R.id.depostionRecords:
                        fragmentTransaction.replace(R.id.container, new FecalDiaryFragment());
                        break;
                    case R.id.depositionStatistics:
                        fragmentTransaction.replace(R.id.container, new PoopStatisticsFragment());
                        break;
                    case R.id.signOut:
                        mAuth.signOut();
                        loaderDialog.startLoading();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
                fragmentTransaction.commit();
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loaderDialog.stopLoading();
        Log.d("MAIN", "onDestroy");
    }
}
