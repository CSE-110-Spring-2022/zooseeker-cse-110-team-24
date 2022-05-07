package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

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

    private List<Double> distanceList = new ArrayList<Double>();
    private int directionsIndex = -1;
    private List<Pair<String, Boolean>> directionsList;
    //public TextView directionsText = (TextView) findViewById(R.id.directionsText);


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
        if(RouteGenerator.staticroute == null){
            route = generator.pathGenerator();
        } else {
            route = RouteGenerator.staticroute;
        }

        distanceList = generator.generateDistances(route);

        this.directionsList = generateDirections(exhibits,route,distanceList);

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
                                                           List<Double> distanceList) {
        StringBuilder sb = new StringBuilder();
        List<Pair<String, Boolean>> directionList = new ArrayList<Pair<String, Boolean>>();
        List<ZooData.Node> targCopy = generator.copyZooList(targets);

        // used to check if the user has reached one of the target exhibits
        Boolean atExhibit;

        for (int i = 0; i < route.size()-1; i++) {
            atExhibit = false;
            sb.append("("+(i+1)); // direction number
            sb.append(")\n Walk ");
            sb.append(distanceList.get(i)); // distance to walk
            sb.append(" meters along\n");
            // street name  |  this line will have to be thoroughly tested
            sb.append(edges.get((g.getEdge(route.get(i).id,
                                          route.get(i+1).id)).getId()).street);

            sb.append(" from\n");
            sb.append(route.get(i).name); // vertex 1 name
            sb.append(" to\n");
            sb.append(route.get(i+1).name); // vertex 2 name
            sb.append(".");

            //TODO figure out why targets is empty when calling the method
            for(int j = 0; j < targCopy.size(); j++){
                if (route.get(i + 1).id.equals(targCopy.get(j).id)) {
                    atExhibit = true;
                    targCopy.remove(j);
                    //sb.append(" EXHIBIT");
                }
            }

            System.out.println(sb.toString());
            directionList.add(new Pair<String, Boolean>(sb.toString(),atExhibit));

            sb.delete(0,sb.length());
        }
        return directionList;
    }

    public void onNextButtonClicked(View view) {

        if(directionsIndex < directionsList.size()-1){
            this.directionsIndex++;
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(directionsList.get(directionsIndex).first);

            TextView arrivedText = (TextView) findViewById(R.id.atExhibitText);
            //if it is a target exhibit
            if(directionsList.get(directionsIndex).second){
                arrivedText.setText("ARRIVING AT EXHIBIT");
            }
            else{
                arrivedText.setText("");
            }
        }

    }

    public void onPrevButtonClicked(View view) {

        if(directionsIndex > 0){
            this.directionsIndex--;
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(directionsList.get(directionsIndex).first);

            TextView arrivedText = (TextView) findViewById(R.id.atExhibitText);
            //if it is a target exhibit
            if(directionsList.get(directionsIndex).second){
               arrivedText.setText("ARRIVING AT EXHIBIT");
            }
            else{
                arrivedText.setText("");
            }
        }

    }
}