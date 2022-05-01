package com.example.zooseekerteam24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {

    private static final String TAG = "PlannerActivity";
    private BottomNavigationView btmNavi;
    private ArrayList<ZooData.Node> exhibits = new ArrayList<>();
    private RecyclerView rvPlanner;
    private PlannerAdapter adapter;

    private PlannerAdapter.OnDeleteListener onDeleteListener = new PlannerAdapter.OnDeleteListener(){

        @Override
        public void performOnDelete(int position) {
            Log.d(TAG, "deleted: " + exhibits.get(position).name);
            exhibits.remove(position);
            adapter.notifyItemRemoved(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        rvPlanner = findViewById(R.id.rvPlanner);


        // TODO: get data from db to populate exhibits
        ZooData.Node n1 = new ZooData.Node();
        n1.name = "Dragon";
        n1.tags = Arrays.asList("mammal", "precious");

        ZooData.Node n2 = new ZooData.Node();
        n2.name = "Unicorn";

        ZooData.Node n3 = new ZooData.Node();
        n3.name = "Ditto";

        exhibits.addAll(Arrays.asList(n1, n2, n3));

        // TODO: avoid duplicate insert
        adapter = new PlannerAdapter(this, onDeleteListener, exhibits);
        rvPlanner.setAdapter(adapter);
        rvPlanner.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        // TODO: populate data here

//        lvResults.setAdapter(adapter);

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