package com.DetectWell.Appllication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.DetectWell.Appllication.ui.userDataViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavController;

import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.DetectWell.Appllication.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private TextView username;
    private TextView useremail;

    private MenuItem logout;

    private userDataViewModel viewModel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();



        if (null != mAuth.getCurrentUser()) {

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());


            setSupportActionBar(binding.appBarMain.toolbar);


            // Initialize TextViews after setContentView()
            username = binding.navView.getHeaderView(0).findViewById(R.id.userName_nav);
            useremail = binding.navView.getHeaderView(0).findViewById(R.id.userEmail_nav);
            logout = binding.navView.getMenu().findItem(R.id.nav_logout);



            viewModel = new ViewModelProvider(this).get(userDataViewModel.class);

            // Observe changes in user data
            viewModel.getUserName().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String userName) {
                    username.setText(userName);
                }
            });

            viewModel.getUserEmail().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String userEmail) {
                    useremail.setText(userEmail);
                }
            });

            logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    Toast.makeText(MainActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_skinCancer, R.id.nav_diabetes, R.id.nav_heartDisease, R.id.nav_parkinson, R.id.nav_aboutUs, R.id.nav_contactUs, R.id.nav_logout)
                    .setOpenableLayout(drawer)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);




        }
        else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




}