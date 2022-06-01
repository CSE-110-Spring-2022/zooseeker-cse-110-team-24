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
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DirectionActivity extends AppCompatActivity {

    private BottomNavigationView btmNavi;

    private RouteGenerator generator;

    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Edge> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    private List<ZooData.Node> targets = Collections.emptyList();
    private List<ZooData.Node> route = Collections.emptyList();
    private List<ZooData.Node> remainingTargets;

    private ZooData.Node currNextExhibit; // The exhibit the user is navigating to
    private ZooData.Node currNode; // The node that the user is currently at
    private int currIndex; // curr index of route user is at, used to help against duplicates

    private List<Double> distanceList = new ArrayList<Double>();
    private List<ZooData.Node> exhibitsInGroup =  new ArrayList<>();

    private boolean detailedOn = false;

    //private List<Pair<String, Boolean>> directionsList;

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
        generator = new RouteGenerator(this, targets, nodes, edges ,g );

        // Sets the targets in the route generator to the exhibits that
        // the user wishes to see
        NodeDao nodeDao = NodeDatabase.getSingleton(this).nodeDao();
        targets = nodeDao.getAllAdded();
        exhibitsInGroup = new ArrayList<>(targets.stream().filter(n -> n.hasGroup()).collect(Collectors.toList()));
        generator.setTargets(targets);
        remainingTargets = generator.copyZooList(targets);

        // Generates a new path ONLY if a path doesn't already exist
        if(RouteGenerator.staticroute == null){
            route = generator.pathGenerator();
        } else {
            route = RouteGenerator.staticroute;
        }

        // TODO make this better when we figure out how to actually find their current position
        currNode = RouteGenerator.staticroute.get(0);
        currIndex = 0;

        // Generates the directions using the distances between each node in the route
        distanceList = generator.generateDistances(RouteGenerator.staticroute);

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
     * @param currIndex    The index of the node that the user is currently at
     * @param route        The route that the user is set to take
     * @param distanceList The list of distances between each exhibit on the route
     * @return             A List of pairs of Strings that contain each direction, and
     *                     Booleans that contain whether the given direction leads them
     *                     to a target exhibit
     */
    private String generateDirections(int currIndex,
                                      List<ZooData.Node> route,
                                      List<Double> distanceList,
                                      int direction) {
        String returnDirection = "NO DIRECTION";

        int i; // index of currNode in the route
        i = currIndex;

        // Iterates through each node in the route
        if (i >= 0) {

            /*
            If you're wondering why we didn't just use .contains, it seems like the rtId value
            of the ZooData.Node does not play well with contains, and we have to do this to
            iterate through the id's instead.
             */

            // If the user is moving forwards through the path
            if(direction > 0) {
                for(int j = 0; j < remainingTargets.size(); j++) {
                    // If they reach a target, remove it from the remaining targets
                    if (remainingTargets.get(j).id.equals((route.get(i)).id)){
                        remainingTargets.remove(j);
                    }
                }
            } else {
                // If they are instead moving backwards through the path...
                for(int j = 0; j < targets.size(); j++) {
                    if (targets.get(j).id.equals((route.get(i-1)).id)) {
                        boolean containsTarget = false;
                        // ... Then add targets back if they reach them
                        for(int k = 0; k < remainingTargets.size(); k++){
                            if (remainingTargets.get(k).id.equals(targets.get(j).id)){
                                containsTarget = true;
                            }
                        }
                        if(!containsTarget) {
                            remainingTargets.add(route.get(i-1));
                        }
                    }
                }
            }

            // Generate the directions depending on if the user has brief or detailed selected
            if(detailedOn){
                returnDirection = detailedDirectionsHelper(i,direction,route);
            } else {
                Pair<String, Integer> briefDirectionPair = briefDirectionsHelper(i,direction,route);
                returnDirection = briefDirectionPair.first;
                if(direction > 0) {
                    // Update i if they are skipping nodes with brief
                    i += briefDirectionPair.second;
                    this.currIndex += briefDirectionPair.second;
                } else {
                    i -= briefDirectionPair.second;
                    this.currIndex -= briefDirectionPair.second;
                }
            }
        }

        return returnDirection;
    }

    /**
     * Method: onSwitchClicked
     * Desc  : Handles the clicking of the directions switch
     *         When clicked while off, should change direction type to detailed
     *         When clicked while on, should change direction type to brief
     * @param view   The button to be clicked
     */
    public void onSwitchClicked(View view) {
        // Only iterate to the next direction if one exists

        Switch directionSwitch = findViewById(R.id.directionSwitch);
        if(directionSwitch.isChecked()) {
            //directions should now be set to detailed mode
            detailedOn = true;
            directionSwitch.setText("Detailed");
        } else {
            //directions should now be set to brief mode
            detailedOn = false;
            directionSwitch.setText("Brief");
        }
    }

    /**
     * Method: onNextButtonClicked
     * Desc  : Handles the clicking of the "Next" button
     *         When clicked, iterates to the next direction in the list
     * @param view   The button to be clicked
     */
    public void onNextButtonClicked(View view) {
        // Only iterate to the next direction if one exists

        if(currIndex < RouteGenerator.staticroute.size()-1){
            // Updates the directions to the next on the list
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(generateDirections(currIndex,route,distanceList,1));

            // Updates the TextView so the user knows they are not going backwards
            TextView goingPreviousText = (TextView) findViewById(R.id.goingPreviousText);
            goingPreviousText.setText("");

            // Updates the next exhibit that the user is navigating to
            updateDistToNextExhibit();

            currNode = RouteGenerator.staticroute.get(++currIndex);
        }
    }

    /**
     * Method: onPrevButtonClicked
     * Desc  : Handles the clicking of the "Prev" button
     *         When clicked, iterates to the previous direction in the list
     * @param view   The button to be clicked
     */
    public void onPrevButtonClicked(View view) {
        // Only iterate to the next direction if one exists

        if (currIndex > 0) {

            // Updates the directions to navigate backwards
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(generateDirections(currIndex,
                    RouteGenerator.staticroute, distanceList, -1));

            // Updates text to show that they are going backwards
            TextView goingPreviousText = (TextView) findViewById(R.id.goingPreviousText);
            goingPreviousText.setText("Navigating Backwards!");

            // Update the distance to the next exhibit
            updateDistToNextExhibit();

            currNode = route.get(--currIndex);
        }
    }

    /**
     * Method: onSkipButtonClicked
     * Desc  : Handles the clicking of the "Skip" button
     *         When clicked, skips the next exhibit in targets and appends a new
     *         optimal route with the remaining target exhibits
     * @param view   The button to be clicked
     */

    public void onSkipButtonClicked(View view) {
        List<ZooData.Node> newSkippedRoute = new ArrayList<>();
        if(!remainingTargets.isEmpty()) {

            // Reset the targets so it can remove the next in list
            generator.setTargets(targets);
            currNextExhibit = generator.nextExhibitInRoute(currNode);

            // Remove the next exhibit in the route
            for(int i = 0; i < targets.size(); i++){
                if(targets.get(i).id.equals(currNextExhibit.id)){
                    targets.remove(i);
                }
            }
            for(int i = 0; i < remainingTargets.size(); i++){
                if(remainingTargets.get(i).id.equals(currNextExhibit.id)){
                    remainingTargets.remove(i);
                }
            }

            // Set the remaining targets so you can perform the new route
            generator.setTargets(remainingTargets);

            // Generate the new route and append it to the first half
            newSkippedRoute = generator.pathGeneratorFromNode(currNode);
            route = generator.clearRouteFromIndex(route, currIndex);
            route.addAll(newSkippedRoute);
            RouteGenerator.staticroute = route;

            // Update the new directions
            distanceList = generator.generateDistances(RouteGenerator.staticroute);

            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(generateDirections(currIndex-1,route,distanceList,1));

            // Finally, update the new next exhibit
            updateDistToNextExhibit();
        }
    }

    /**
     * Method: briefDirectionsHelper
     * Desc  : Generates a brief direction for the user, skipping over nodes that
     *         share the same edge name, unless they arrive at an exhibit
     * @param i         The button to be clicked
     * @param direction The direction the user is navigating
     * @param route     The route the user is navigating with
     * @return          A pair with a string of the direction, and an integer for how many nodes
     *                  along the path were skipped
     */
    private Pair<String, Integer> briefDirectionsHelper
            (int i, int direction, List<ZooData.Node> route){
        int dirLength = 0; // how many directions are being compressed
        double pathDist = 0;
        String street = Objects.requireNonNull(edges.get((g.getEdge(route.get(i).id,
                route.get(i + direction).id)).getId())).street;
        StringBuilder sb = new StringBuilder();

        // Used to break out of while loop if user hits a target
        boolean notAtTarget = true;

        sb.append("Walk ");
        // Grab the distanceList element depending on direction
        // If the user is going forward in the path
        if(direction > 0) {
            while(i + dirLength + 2 < route.size() && notAtTarget){

                // If the current street name is the same as the next street name
                if((street).equals((Objects.requireNonNull
                        (edges.get((g.getEdge(route.get(i+dirLength+1).id,
                                route.get(i + dirLength+2).id)).getId())).street))){

                    // Then you're travelling along a straight road
                    // Add to the total distance of the road


                    // TODO check if it's an exhibit and return early
                    for(int j = 0; j < remainingTargets.size(); j++) {
                        if (remainingTargets.get(j).id.equals((route.get(i + dirLength + 1)).id)){
                            notAtTarget = false;
                            break;
                        }
                    }

                    if(notAtTarget) {
                        pathDist += distanceList.get(i + dirLength);
                        dirLength += 1;
                    }
                } else {
                    break;
                }
            }
            pathDist += distanceList.get(i+dirLength);
        }
        // Else, the user is travelling backwards
        else if (direction < 0) {
            while(i - dirLength - 2 >= 0) {
                // If the street name equals the next (previous) street name
                if((street).equals((Objects.requireNonNull
                        (edges.get((g.getEdge(route.get(i-dirLength-1).id,
                                route.get(i - dirLength-2).id)).getId())).street))){
                    pathDist += distanceList.get(i-dirLength-1);
                    dirLength += 1;
                } else {
                    break;
                }
            }
            pathDist += distanceList.get(i-dirLength-1);
        }

        sb.append(pathDist); // distance to walk

        sb.append(" feet along\n");
        sb.append(street); // street name
        sb.append(" from\n");
        sb.append(route.get(i).name); // vertex 1 name
        sb.append("\nto ");
        if (direction > 0) {
            sb.append(route.get(i + direction + dirLength).name); // vertex 2 name
        } else {
            sb.append(route.get(i + direction - dirLength).name); // vertex 2 name (prev)
        }
        sb.append(".");

        return new Pair<String, Integer>(sb.toString(), dirLength);
    }

//    private String getExhibitFromGroupId(String groupId){
//        ZooData.Node exhibit = exhibitsInGroup.stream().filter(n -> n.group_id.equals(groupId)).findFirst().get();
//        exhibitsInGroup.remove(exhibit);
//        return exhibit.name;
//    }

    /**
     * Method: detailedDirectionsHelper
     * Desc  : Generates a detailed direction for the user
     * @param i         The button to be clicked
     * @param direction The direction the user is navigating
     * @param route     The route the user is navigating with
     * @return          A pair with a string of the direction
     */
    private String detailedDirectionsHelper(int i, int direction, List<ZooData.Node> route){

        StringBuilder sb = new StringBuilder();
        sb.append("Walk ");
        // Grab the distanceList element depending on direction
        double distance = 0.0;
        if(direction > 0) {
            distance = distanceList.get(i);
        } else {
            distance = distanceList.get(i-1);
        }

        String fromId = route.get(i).id;
        String toId = route.get(i + direction).id;

        if (distance > 0){
            sb.append(distance + " feet along\n");
            sb.append(Objects.requireNonNull(edges.get((g.getEdge(fromId, toId)).getId())).street); // street name
            sb.append(" from\n");
            sb.append(route.get(i).name); // vertex 1 name
            sb.append("\nto ");
            sb.append(route.get(i + direction).name); // vertex 2 name
        } else{
            sb.append(" around " + route.get(i).name + "\n");
            sb.append(" from\n");
            sb.append(route.get(i).name); // vertex 1 name
            sb.append("\nto ");
            sb.append(route.get(i + direction).name); // vertex 2 name
        }



        // Edge is null when two exhibits in route are in the same group,
        // this is resolved, but
        // In route we treat subexhibits as group id, so now say from parker aviary to parter aviary
        // instead of from mot-mot to toucan


//        sb.append(Objects.requireNonNull(edges.get((g.getEdge(route.get(i).id,
//                route.get(i + direction).id)).getId())).street); // street name



//        sb.append(" from\n");
//        sb.append(route.get(i).name); // vertex 1 name
//        sb.append("\nto ");
//        sb.append(route.get(i + direction).name); // vertex 2 name
        sb.append(".");

        return sb.toString();
    }

    /**
     * Method: updateDistToNextExhibit
     * Desc  : Updates the distances to the next exhibit of the text view
     *         Used to inform the user of how far they are from the next exhibit in the route
     */
    private void updateDistToNextExhibit(){
        double distToNext;

        generator.setTargets(remainingTargets);
        currNextExhibit = generator.nextExhibitInRoute(currNode);

        distToNext = generator.distanceBetweenNodes(currNode, currNextExhibit);

        TextView nextExhibitText = (TextView) findViewById(R.id.nextExhibit);
        nextExhibitText.setText("Navigating To: " + currNextExhibit.name +
                "\nDistance: " + distToNext + " ft");
    }


}