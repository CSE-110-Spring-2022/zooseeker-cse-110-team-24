/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * File   : DirectionActivity.java
 * Authors: Dylan Govic, Jose Valdivia, and Yiran Wan
 * Desc   : Contains the necessary methods to handle the Direction Activity of
 *          the ZooSeeker app.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.zooseekerteam24.location.Coords;
import com.example.zooseekerteam24.location.LocationModel;
import com.example.zooseekerteam24.location.LocationPermissionChecker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DirectionActivity extends AppCompatActivity {

    private static final String TAG = "DirectionActivity";
    private BottomNavigationView btmNavi;

    private RouteGenerator generator;

    private Map<String,ZooData.Node> nodes;
    private Map<String,ZooData.Edge> edges;
    private Graph<String,IdentifiedWeightedEdge> g;

    private LocationModel model;
    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";
    private boolean useLocationService;

    private List<ZooData.Node> targets = Collections.emptyList();
    private List<ZooData.Node> route = Collections.emptyList();
    private List<ZooData.Node> remainingTargets;

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
    @SuppressLint("VisibleForTests")
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

        // TODO: location
        useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);
        // Set up the model.
        model = new ViewModelProvider(this).get(LocationModel.class);

        // If GPS is enabled, then update the model from the Location service.
        if (useLocationService) {
            var permissionChecker = new LocationPermissionChecker(this);
            permissionChecker.ensurePermissions();

            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var provider = LocationManager.GPS_PROVIDER;
            model.addLocationProviderSource(locationManager, provider);
        }
        // else, only mocked location updates will be shown, and location permissions will not be requested.
        // This is appropriate for testing purposes.

        // Flower powder
        // Observe the model and place a blue pin whenever the location is updated.
        model.getLastKnownCoords().observe(this, (coord) -> {
            Log.i(TAG, String.format("Observing lastKnownCoord update to %s", coord));
        });

        // Test the above by mocking movement...
        var route = Coords
                .interpolate(generator.getEntranceExitNode().getCoords(), targets.get(0).getCoords(), 3)
                .collect(Collectors.toList());

        if (!useLocationService) {
            model.mockRoute(route, 500, TimeUnit.MILLISECONDS);
        }


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

            if(detailedOn){
                returnDirection = detailedDirectionsHelper(i,direction,route);
            } else {
                Pair<String, Integer> briefDirectionPair = briefDirectionsHelper(i,direction,route);
                returnDirection = briefDirectionPair.first;
                if(direction > 0) {
                    i += briefDirectionPair.second;
                    this.currIndex += briefDirectionPair.second;
                } else {
                    i -= briefDirectionPair.second;
                    this.currIndex -= briefDirectionPair.second;
                }
            }

            /*
            If you're wondering why we didn't just use .contains, it seems like the rtId value
            of the ZooData.Node does not play well with contains, and we have to do this to
            iterate through the id's instead.
             */
            if(direction > 0) {
                //System.out.println("route get i+1: " + route.get(i+1));
                // 🤢🤢🤢🤢🤢🤢🤢🤢🤢🤢
                for(int j = 0; j < remainingTargets.size(); j++) {
                    if (remainingTargets.get(j).id.equals((route.get(i + 1)).id)){
                        //System.out.println("YEA YEA YEA");
                        remainingTargets.remove(j);
                    }
                }
            } else {
                for(int j = 0; j < targets.size(); j++) {
                    if (targets.get(j).id.equals((route.get(i)).id)) {
                        // I HATE IT HERE I HATE IT HERE
                        boolean containsTarget = false;
                        for(int k = 0; k < remainingTargets.size(); k++){
                            if (remainingTargets.get(k).id.equals(targets.get(j).id)){
                                containsTarget = true;
                            }
                        }
                        if(!containsTarget) {
                            remainingTargets.add(route.get(i));
                        }
                    }
                }
            }
        }

        //System.out.println("direction remaining targets: " + remainingTargets);

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
            TextView goingPreviousText = (TextView) findViewById(R.id.goingPreviousText);
            goingPreviousText.setText("");
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
            System.out.println("currindex " + currIndex);
            // Updates the directions to the next on the list
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            TextView goingPreviousText = (TextView) findViewById(R.id.goingPreviousText);
            directionsText.setText(generateDirections(currIndex,
                    RouteGenerator.staticroute, distanceList, -1));
            goingPreviousText.setText("Navigating Backwards!");
            currNode = route.get(--currIndex);
        }
    }

    public void onSkipButtonClicked(View view) {

        /*
        List<ZooData.Node> newSkippedRoute = new ArrayList<>();
        if(!remainingTargets.isEmpty()) {
            System.out.println("SKIPPED");
            System.out.println("Remaining Targets: " + remainingTargets);
            System.out.println("currNode " + currNode);
            System.out.println("-----------");
            //System.out.println("Next Exhibit " + generator.nextExhibitInRoute(currNode));
            // Reset the targets so it can remove the next in list
            generator.setTargets(targets);
            ZooData.Node nextExhibit = generator.nextExhibitInRoute(currNode);
            for(int i = 0; i < targets.size(); i++){
                if(targets.get(i).id.equals(nextExhibit.id)){
                    targets.remove(i);
                }
            }
            for(int i = 0; i < remainingTargets.size(); i++){
                if(remainingTargets.get(i).id.equals(nextExhibit.id)){
                    remainingTargets.remove(i);
                }
            }
            // Set the remaining targets so you can perform the new route
            generator.setTargets(remainingTargets);
            //Generate the new route and append it to the first half
            newSkippedRoute = generator.pathGeneratorFromNode(currNode);
            route = generator.clearRouteFromIndex(route, currIndex);
            route.addAll(newSkippedRoute);
            RouteGenerator.staticroute = route;
            TextView directionsText = (TextView) findViewById(R.id.directionsText);
            directionsText.setText(generateDirections(currIndex-1,route,distanceList,1));
        }
        */

    }

    private Pair<String, Integer> briefDirectionsHelper
            (int i, int direction, List<ZooData.Node> route){
        int dirLength = 0; // how many directions are being compressed
        double pathDist = 0;
        String street = Objects.requireNonNull(edges.get((g.getEdge(route.get(i).id,
                route.get(i + direction).id)).getId())).street;
        StringBuilder sb = new StringBuilder();

        sb.append("Walk ");
        // Grab the distanceList element depending on direction
        // If the user is going forward in the path
        if(direction > 0) {
            while(i + dirLength + 2 < route.size()){

                // If the current street name is the same as the next street name
                if((street).equals((Objects.requireNonNull
                        (edges.get((g.getEdge(route.get(i+dirLength+1).id,
                                route.get(i + dirLength+2).id)).getId())).street))){

                    // Then you're travelling along a straight road
                    // Add to the total distance of the road

                    // TODO check if it's an exhibit and return early

                    pathDist += distanceList.get(i+dirLength);
                    dirLength += 1;

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

        sb.append(" meters along\n");
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

    private String detailedDirectionsHelper(int i, int direction, List<ZooData.Node> route){

        StringBuilder sb = new StringBuilder();
        sb.append("Walk ");
        // Grab the distanceList element depending on direction
        double distance = 0.0;
        if(direction > 0) {
            distance = distanceList.get(i);
//            sb.append(distanceList.get(i)); // distance to walk
        } else {
            distance = distanceList.get(i-1);
//            sb.append(distanceList.get(i-1));
        }

        String fromId = route.get(i).id;
        String toId = route.get(i + direction).id;

        if (distance > 0){
            sb.append(distance + " meters along\n");
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


}