package com.example.customerrecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private ImageButton btnLogout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView.setSelectedItemId(R.id.menuAddCustomer);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();

            }
        });


    }

    AddFragment addFragment = new AddFragment();
    SearchFragment searchFragment = new SearchFragment();



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuAddCustomer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, addFragment).commit();
                return true;
            case R.id.menuSearchCustomer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, searchFragment).commit();
                return true;
        }
        return false;
    }
}