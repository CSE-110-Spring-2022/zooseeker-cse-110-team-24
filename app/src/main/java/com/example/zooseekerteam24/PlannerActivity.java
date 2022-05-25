package com.example.zooseekerteam24;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jgrapht.Graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerActivity extends AppCompatActivity {

    private static final String TAG = "PlannerActivity";
    private BottomNavigationView btmNavi;
    private List<ZooData.Node> exhibits = Collections.emptyList();
    private RecyclerView rvPlanner;
    private PlannerAdapter adapter;
    private PlannerViewModel plannerViewModel;
    private RouteGenerator generator;


    private Map<String, ZooData.Node> nodes;
    private Map<String, ZooData.Edge> edges;
    private Graph<String, IdentifiedWeightedEdge> g;

    private Map<String, Double> distanceMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        rvPlanner = findViewById(R.id.rvPlanner);

        adapter = new PlannerAdapter();


        // Create graph and routeGenerator
        nodes = ZooData.loadNodesFromJSON(this, ZooData.NODE_FILE);
        edges = ZooData.loadEdgesFromJSON(this, ZooData.EDGE_FILE);
        g = ZooData.loadZooGraphJSON(this, ZooData.GRAPH_FILE);

//        for(IdentifiedWeightedEdge e: g.edgeSet()){
//            Log.d(TAG, "edge: " + e.getWeight());
//        }

        generator = new RouteGenerator(this, exhibits, nodes, edges, g);

        // Register viewModel to observe changes in planner
        plannerViewModel = new ViewModelProvider(this)
                .get(PlannerViewModel.class);
        plannerViewModel.getAddedNodesByDist().observe(this, adapter::populatePlanner);

        generator.setTargets(plannerViewModel.getAddedNodes());
        adapter.setRouteGenerator(generator);
        adapter.setOnDeleteBtnClickedHandler(plannerViewModel::toggleExhibitAdded);
        adapter.setOnOrderCalledHandler(plannerViewModel::orderExhibitsAdded);

        plannerViewModel.getNumOfExhibits().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setCounter(((ObservableInt) sender).get());
            }
        });
        adapter.setOnCountCalled(plannerViewModel::countExhibitsAdded);

        plannerViewModel.orderExhibitsAdded(generator);
        setCounter(plannerViewModel.getNumOfExhibits().get());

        adapter.setHasStableIds(true);
        rvPlanner.setAdapter(adapter);
//        adapter.populatePlanner(exhibits);
        rvPlanner.setLayoutManager(new LinearLayoutManager(this));



        btmNavi = findViewById(R.id.btmNavi);
        btmNavi.setSelectedItemId(R.id.icPlanner);
        btmNavi.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.icSearch:
                    Intent iSearch = new Intent(getApplicationContext(), SearchActivity.class);
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


    /**
     * setCounter
     * set title to be number of added exhibits
     * @param count: number of added exhibits
     */
    private void setCounter(int count) {
        String title = String.format("Planner (%d)", count);
        getSupportActionBar().setTitle(title);
    }

}