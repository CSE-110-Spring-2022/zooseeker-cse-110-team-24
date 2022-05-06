package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PlannerActivity extends AppCompatActivity {

    private static final String TAG = "PlannerActivity";
    private BottomNavigationView btmNavi;

    private List<ZooData.Node> exhibits = Collections.emptyList();
    private RecyclerView rvPlanner;
    private Button btnOrder;
    private PlannerAdapter adapter;
    private PlannerViewModel plannerViewModel;
    private RouteGenerator generator;
    private Consumer<RouteGenerator> onOrderClicked;

    public void setOnOrderClickedHandler(Consumer<RouteGenerator> onOrderClicked){
        this.onOrderClicked = onOrderClicked;
    }

    private String edgeFile = "sample_edge_info.json";
    private String graphFile = "sample_zoo_graph.json";
    private String nodeFile = "sample_exhibits.json";

    private List<ZooData.Node> targets;
    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Edge> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    private NodeDao nodeDao;
    private Map<String, Double> distanceMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        rvPlanner = findViewById(R.id.rvPlanner);
        btnOrder = findViewById(R.id.btnOrder);


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

        //Assuming exhibits is already the TARGETS
        // < --

        nodes = ZooData.loadNodesFromJSON(this, nodeFile);
        edges = ZooData.loadEdgesFromJSON(this, edgeFile);
        g = ZooData.loadZooGraphJSON(this, graphFile);


        generator = new RouteGenerator(this, exhibits, nodes, edges ,g);
        //ArrayList<ZooData.Node> TEMP = (ArrayList<ZooData.Node>) exhibits;

        // --- >

        // Fetch the lasted date and repopulate the IU
        plannerViewModel = new ViewModelProvider(this)
                .get(PlannerViewModel.class);
        plannerViewModel.getAddedNodes().observe(this, adapter::populatePlanner);
        adapter.setOnDeleteBtnClickedHandler(plannerViewModel::toggleExhibitAdded);

//        onOrderClicked.accept(generator);
//        setOnOrderClickedHandler(plannerViewModel::orderExhibitsAdded);

        btnOrder.setOnClickListener(v -> plannerViewModel.orderExhibitsAdded(generator));
        btnOrder.performClick();

//        nodeDao = NodeDatabase.getSingleton(this).nodeDao();
//        exhibits = nodeDao.getAllAdded();
//        exhibits.forEach(n -> Log.d("yoadd", "" + n.toString()));
////
//        generator.setTargets(nodeDao.getAllAdded());
//        distanceMap = generator.fakeMethod();
//        distanceMap.forEach((s, d) -> Log.d("yomap", d + " " + s));
//
//        exhibits.forEach(ex -> {
//            ex.cumDistance = distanceMap.getOrDefault(ex.id, -10.0);
//            nodeDao.update(ex);
//        });
//        adapter.populatePlanner(exhibits);
//        List<ZooData.Node> route = generator.pathGenerator();
//        route.forEach(n -> Log.d("yogen", "" + n.toString()));
//
//        Map<ZooData.Node, Double> map = generator.generateDistances(route);

//        btnOrder.setOnClickListener( v -> {
//            onOrderClicked.accept(map);
//            setOnOrderClickedHandler(plannerViewModel::orderExhibitsAdded);
//        });



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


    public List<ZooData.Node> getPlannedExhibits(){
        return this.exhibits;
    }

}