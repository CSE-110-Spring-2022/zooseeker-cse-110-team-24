package com.example.zooseekerteam24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView btmNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btmNavi = findViewById(R.id.btmNavi);

        btmNavi.setSelectedItemId(R.id.icSearch);

        btmNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.icDirection:
                        Intent iDirection = new Intent(getApplicationContext(), DirectionActivity.class);
                        startActivity(iDirection);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.icPlanner:
                        Intent iPlanner = new Intent(getApplicationContext(), PlannerActivity.class);
                        startActivity(iPlanner);
                        overridePendingTransition(0, 0);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
}