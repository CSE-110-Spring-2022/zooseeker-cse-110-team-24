package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DirectionActivity extends AppCompatActivity {

    private BottomNavigationView btmNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        btmNavi = findViewById(R.id.btmNavi);

        btmNavi.setSelectedItemId(R.id.icDirection);

        btmNavi.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.icSearch:
                    Intent iSearch  = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(iSearch);
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
        });
    }
}