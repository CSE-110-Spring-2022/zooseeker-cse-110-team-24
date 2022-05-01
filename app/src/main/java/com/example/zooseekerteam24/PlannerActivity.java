package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {

    private static final String TAG = "PlannerActivity";
    private BottomNavigationView btmNavi;
    private List<ZooData.Node> exhibits = Collections.emptyList();
    private RecyclerView rvPlanner;
    private PlannerAdapter adapter;
    private PlannerViewModel plannerViewModel;

//    private PlannerAdapter.OnDeleteListener onDeleteListener = new PlannerAdapter.OnDeleteListener(){
//
//        @Override
//        public void performOnDelete(int position) {
//
//            Log.d(TAG, "deleted: " + exhibits.get(position).name);
//            exhibits.remove(position);
//            adapter.notifyItemRemoved(position);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        rvPlanner = findViewById(R.id.rvPlanner);


        // TODO: get data from db to populate exhibits
//        ZooData.Node n1 = new ZooData.Node();
//        n1.name = "Dragon";
//        n1.id = "dragon1";
//        n1.tags = Arrays.asList("mammal", "precious");
//
//        ZooData.Node n2 = new ZooData.Node();
//        n2.name = "Unicorn";
//
//        ZooData.Node n3 = new ZooData.Node();
//        n3.name = "Ditto";
//
//        exhibits.addAll(Arrays.asList(n1, n2, n3));

//        exhibits = NodeDatabase.getSingleton(this).nodeDao().getAll();
//        Log.d(TAG, ""+exhibits.size());
        adapter = new PlannerAdapter();

        // Fetch the lasted date and repopulate the IU
        plannerViewModel = new ViewModelProvider(this)
                .get(PlannerViewModel.class);
        plannerViewModel.getAddedNodes().observe(this, adapter::populatePlanner);
        adapter.setOnDeleteBtnClickedHandler(plannerViewModel::toggleExhibitAdded);
//         The observer here is adapter::populatePlanner, which receives event only when owner is active

        // TODO: avoid duplicate insert

//        adapter.setHasStableIds(true);
        rvPlanner.setAdapter(adapter);
//        adapter.populatePlanner(exhibits);
        rvPlanner.setLayoutManager(new LinearLayoutManager(this));


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