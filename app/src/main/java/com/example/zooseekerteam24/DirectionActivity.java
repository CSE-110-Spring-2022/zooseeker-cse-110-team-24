/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * File   : DirectionActivity.java
 * Authors: Dylan Govic, Jose Valdivia, and Yiran Wan
 * Desc   : Contains the necessary methods to handle the Direction Activity of
 *          the ZooSeeker app.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DirectionActivity extends AppCompatActivity {

    private BottomNavigationView btmNavi;

    private RouteGenerator generator;

    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Edge> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    private List<ZooData.Node> exhibits = Collections.emptyList();
    private List<ZooData.Node> route = Collections.emptyList();

    private List<Double> distanceList = new ArrayList<Double>();
    private int directionsIndex = -1;

    private List<Pair<String, Boolean>> directionsList;

    /**
     * Method: onCreate
     * Desc  : Handles everything that the DirectionActivity must do on creation
     * @param savedInstanceState the state used to check if any data is to be restored
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        // Converts each JSON data file into relevant data types
        nodes = ZooData.loadNodesFromJSON(this, ZooData.NODE_FILE);
        edges = ZooData.loadEdgesFromJSON(this, ZooData.EDGE_FILE);
        g = ZooData.loadZooGraphJSON(this, ZooData.GRAPH_FILE);

        // Creates empty routeGenerator object to be used to find the path
        generator = new RouteGenerator(this, exhibits, nodes, edges ,g );

        // Sets the targets in the route generator to the exhibits that
        // the user wishes to see
        NodeDao nodeDao = NodeDatabase.getSingleton(this).nodeDao();
        exhibits = nodeDao.getAllAdded();
        generator.setTargets(exhibits);

        // Generates a new path ONLY if a path doesn't already exist
        if(RouteGenerator.staticroute == null){
            route = generator.pathGenerator();
        } else {
            route = RouteGenerator.staticroute;
        }

        // Generates the directions using the distances between each node in the route
        distanceList = generator.generateDistances(route);
        this.directionsList = generateDirections(exhibits,route,distanceList);

        // Switches the activity the user is viewing if they click a button on the
        // bottom navigation bar
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

    /**
     * Method: generateDirections
     * Desc  : Generates a list of directions, and whether that direction leads the user
     *         to one of the target exhibits that they wish to see
     * @param targets      The list of exhibits that the user wishes to see
     * @param route        The route that the user is set to take
     * @param distanceList The list of distances between each exhibit on the route
     * @return             A List of pairs of Strings that contain each direction, and
     *                     Booleans that contain whether the given direction leads them
     *                     to a target exhibit
     */
    private List<Pair<String, Boolean>> generateDirections(List<ZooData.Node> targets,
                                                           List<ZooData.Node> route,
                                                           List<Double> distanceList) {
        StringBuilder sb = new StringBuilder();
        List<Pair<String, Boolean>> directionList = new ArrayList<Pair<String, Boolean>>();
        // Performs a deep copy of targets so elements can be safely removed
        List<ZooData.Node> targCopy = generator.copyZooList(targets);

        // Used to check if the user has reached one of the target exhibits
        Boolean atExhibit;

        // Iterates through each node in the route
        for (int i = 0; i < route.size()-1; i++) {
            atExhibit = false;
            sb.append("("+(i+1)+")\n"); // direction number
            sb.append("Walk ");
            sb.append(distanceList.get(i)); // distance to walk
            sb.append(" meters along\n");
            sb.append(Objects.requireNonNull(edges.get((g.getEdge(route.get(i).id,
                    route.get(i + 1).id)).getId())).street); // street name
            sb.append(" from\n");
            sb.append(route.get(i).name); // vertex 1 name
            sb.append("\nto");
            sb.append(route.get(i+1).name); // vertex 2 name
            sb.append(".");

            // Check if the direction leads you to a target exhibit
            for(int j = 0; j < targCopy.size(); j++){
                // If true, set the atExhibit bool to true and remove from copy of targets
                if (route.get(i + 1).id.equals(targCopy.get(j).id)) {
                    atExhibit = true;
                    targCopy.remove(j);
                }
            }

            // Add the new direction to the list
            directionList.add(new Pair<String, Boolean>(sb.toString(),atExhibit));
            // Clear the stringbuilder for the next iteration
            sb.delete(0,sb.length());
        }
        return directionList;
    }

    /**
     * Method: onNextButtonClicked
     * Desc  : Handles the clicking of the "Next" button
     *         When clicked, iterates to the next direction in the list
     * @param view   The button to be clicked
     */
    public void onNextButtonClicked(View view) {
        // Only iterate to the next direction if one exists
        if(directionsIndex < directionsList.size()-1){
            this.directionsIndex++;
            // Updates the directions to the next on the list
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(directionsList.get(directionsIndex).first);

            TextView arrivedText = (TextView) findViewById(R.id.atExhibitText);

            // If it is a target exhibit
            if(directionsList.get(directionsIndex).second){
                arrivedText.setText("Arriving at Exhibit!");
            } else {
                arrivedText.setText("");
            }
        }
    }

    /**
     * Method: onPrevButtonClicked
     * Desc  : Handles the clicking of the "Prev" button
     *         When clicked, iterates to the previous direction in the list
     * @param view   The button to be clicked
     */
    public void onPrevButtonClicked(View view) {
        //Only go to the previous button if you aren't at the start
        if(directionsIndex > 0){
            this.directionsIndex--;
            // Updates the directions to the previous on the list
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(directionsList.get(directionsIndex).first);

            TextView arrivedText = (TextView) findViewById(R.id.atExhibitText);

            // If it is a target exhibit
            if(directionsList.get(directionsIndex).second){
               arrivedText.setText("ARRIVING AT EXHIBIT");
            } else {
                arrivedText.setText("");
            }
        }

    }
}