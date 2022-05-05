package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionActivity extends AppCompatActivity {

    private BottomNavigationView btmNavi;

    private String edgeFile = "sample_edge_info.json";
    private String graphFile = "sample_zoo_graph.json";
    private String nodeFile = "sample_exhibits.json";

    private RouteGenerator generator;

    private List<ZooData.Node> targets;
    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Edge> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    private List<ZooData.Node> exhibits = Collections.emptyList();
    private List<ZooData.Node> route = Collections.emptyList();

    private Map<ZooData.Node, Double> distanceMap = new HashMap<ZooData.Node, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        // LOGIC FOR DIRECTIONS
        // --------------->

        nodes = ZooData.loadNodesFromJSON(this, nodeFile);
        edges = ZooData.loadEdgesFromJSON(this, edgeFile);
        g = ZooData.loadZooGraphJSON(this, graphFile);

        generator = new RouteGenerator(this, exhibits, nodes, edges ,g );

        NodeDao nodeDao = NodeDatabase.getSingleton(this).nodeDao();
        exhibits = nodeDao.getAll();
        generator.setTargets(exhibits);
        route = generator.pathGenerator();

        distanceMap = generator.generateDistances(route);

        generateDirections(exhibits,route,distanceMap);

        // <---------------


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

    private List<Pair<String, Boolean>> generateDirections(List<ZooData.Node> targets,
                                                           List<ZooData.Node> route,
                                                           Map<ZooData.Node, Double> distanceMap) {
        StringBuilder sb = new StringBuilder();
        List<Pair<String, Boolean>> directionList = new ArrayList<Pair<String, Boolean>>();

        // used to check if the user has reached one of the target exhibits
        int exCount = 0;
        Boolean atExhibit;

        for (int i = 0; i < route.size()-1; i++) {
            atExhibit = false;
            sb.append(i+1); // direction number
            sb.append(") Walk ");
            sb.append(distanceMap.get(route.get(i))); // distance to walk
            sb.append(" meters along ");
            // street name  |  this line will have to be thoroughly tested
            sb.append(edges.get((g.getEdge(route.get(i).id,
                                          route.get(i+1).id)).getId()).street);

            sb.append(" from ");
            sb.append(route.get(i).name); // vertex 1 name
            sb.append(" to ");
            sb.append(route.get(i+1).name); // vertex 2 name
            sb.append(".");

            //TODO figure out why targets is empty when calling the method
            //if(route.get(i+1).id.equals(targets.get(exCount).id)) {
            //    atExhibit = true;
            //    exCount++;
            //}

            System.out.println(sb.toString());
            directionList.add(new Pair<String, Boolean>(sb.toString(),atExhibit));

            sb.delete(0,sb.length());
        }
        return directionList;
    }

}