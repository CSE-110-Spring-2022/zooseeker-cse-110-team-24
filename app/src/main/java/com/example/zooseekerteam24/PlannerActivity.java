package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;

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

    //Dylan's copy paste
    private List<ZooData.Node> targets;
    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Edge> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    private String edgeFile = "sample_edge_info.json";
    private String graphFile = "sample_zoo_graph.json";
    private String nodeFile = "sample_exhibits.json";


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

        //Assuming exhibits is already the TARGETS
        // < --
        System.err.println("THIS SHOULD BE CALLED EVERYTIME I CLICK Planner");
        System.err.println("EXHIBITS SIZE = " + exhibits.size());
        nodes = ZooData.loadNodesFromJSON(this ,nodeFile);
        edges = ZooData.loadEdgesFromJSON(this, edgeFile);
        g = ZooData.loadZooGraphJSON(this,graphFile);
//        System.err.println("NODES SIZE =" + nodes.size());
//        System.err.println("EDGES SIZE =" + edges.size());
//        System.err.println("Graph SIZE =" + g.vertexSet().size());

        generator = new RouteGenerator(this, exhibits, nodes, edges ,g );
        //ArrayList<ZooData.Node> TEMP = (ArrayList<ZooData.Node>) exhibits;
        NodeDao nodeDao = NodeDatabase.getSingleton(this).nodeDao();
        exhibits = nodeDao.getAll();
        System.err.println("EXHIBITS SIZE = " + exhibits.size());
        exhibits = generator.pathGenerator(exhibits);
        // --- >

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

    // BY: PRPL & BLOO (JOSE AND DYLAN)
    public List<ZooData.Node> getPlannedExhibits(){
        return this.exhibits;
    }



}