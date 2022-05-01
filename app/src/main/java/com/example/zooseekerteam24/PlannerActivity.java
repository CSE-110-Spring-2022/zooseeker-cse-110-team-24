package com.example.zooseekerteam24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

public class PlannerActivity extends AppCompatActivity {

    private BottomNavigationView btmNavi;
    private List<ZooData.Node> targets;
    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Node> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        btmNavi = findViewById(R.id.btmNavi);

        btmNavi.setSelectedItemId(R.id.icPlanner);

        btmNavi.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.icSearch:
                    Intent iSearch  = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(iSearch);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.icDirection:
                    Intent iDirection = new Intent(getApplicationContext(), DirectionActivity.class);
                    startActivity(iDirection);
                    overridePendingTransition(0, 0);
                    return true;
                default:
                    return true;
            }

        });
    }
}