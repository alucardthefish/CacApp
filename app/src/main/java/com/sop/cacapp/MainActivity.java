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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sop.cacapp.Fragments.FecalDiaryFragment;
import com.sop.cacapp.Fragments.MainFragment;
import com.sop.cacapp.Fragments.PoopStatisticsFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Load main fragment
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container, new MainFragment());
            fragmentTransaction.commit();
        }

        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        View header = navigationView.getHeaderView(0); // Instance of header view layout
        tvHeaderUserName = header.findViewById(R.id.tvHeaderUserName);
        tvHeaderUserEmail = header.findViewById(R.id.tvHeaderUserEmail);
        ivHeaderUserIcon = header.findViewById(R.id.ivHeaderImage);

        // Initialize listeners
        initListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();

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
                    case R.id.home:
                        fragmentTransaction.replace(R.id.container, new MainFragment());
                        break;
                    case R.id.diary:
                        fragmentTransaction.replace(R.id.container, new FecalDiaryFragment());
                        break;
                    case R.id.depositionStatistics:
                        fragmentTransaction.replace(R.id.container, new PoopStatisticsFragment());
                        break;
                    case R.id.signOut:
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                }
                fragmentTransaction.commit();
                return false;
            }
        });
    }
}
