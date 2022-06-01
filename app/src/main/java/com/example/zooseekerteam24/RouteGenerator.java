/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * File   : RouteGenerator.java
 * Authors: Dylan Govic and Jose Valdivia
 * Desc   : Contains methods necessary in order to handle the Zoo route
 *          Can generate the route from a list of target ZooData.Node's
 *          and find the distances along the path
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.zooseekerteam24;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteGenerator {

    Context context;
    List<ZooData.Node> targets;
    Map<String, ZooData.Node> nodes;
    Map<String, ZooData.Edge> edges;
    Graph<String, IdentifiedWeightedEdge> g;

    static List<ZooData.Node> staticroute; // Static so it can be referenced between activities

    public RouteGenerator(@NonNull Context context,
                          @NonNull List<ZooData.Node> targets,
                          @NonNull Map<String,ZooData.Node> nodes,
                          @NonNull Map<String,ZooData.Edge> edges,
                          @NonNull Graph<String,IdentifiedWeightedEdge> g){
        this.context = context;
        this.targets = targets;
        this.nodes = nodes;
        this.edges = edges;
        this.g = g;
    }

    /**
     * Method: setTargets
     * Desc  : sets the targets instance variable to targets
     * @param targets   targets to be set
     */
    public void setTargets(List<ZooData.Node> targets){
        this.targets = copyZooList(targets);
    }

    /**
     * Method: copyZooList
     * Desc  : performs a deep copy of a list of ZooData.Nodes so elements can be safely removed
     * @param nodes   list of nodes to be copied
     * @return        the copied list of nodes
     */
    public List<ZooData.Node> copyZooList(List<ZooData.Node> nodes){
        List<ZooData.Node> newList = new ArrayList<ZooData.Node>();
        for(int i = 0; i < nodes.size(); i++) {
            newList.add(nodes.get(i));
        }
        return newList;
    }

    /**
     * Method: getEntranceExitNode
     * Desc  : Finds the ZooData.Node value of the entrance/exit gate of the Zoo by
     *         iterating through the nodes and finding the kind "Gate"
     * @return The Node of the entrance/exit gate
     */
    public ZooData.Node getEntranceExitNode() {
        // Iterates through each entry of the list of nodes
        Iterator<Map.Entry<String, ZooData.Node>> nodesIterator = nodes.entrySet().iterator();

        while(nodesIterator.hasNext()) {
            Map.Entry<String, ZooData.Node> mapElement = nodesIterator.next();
            ZooData.Node.Kind kind = mapElement.getValue().kind;

            // The kind Gate signifies entrance/exit, so return it if true
            if(kind == (ZooData.Node.Kind.GATE)){
                return mapElement.getValue();
            }
        }

        return null;
    }

    /**
     * Method: nearestNode
     * Desc  : Performs Dijkstra's algorithm to find the nearest target node from source
     *         Returns the path through the zoo from path to the chosen node.
     * @param source   Starting point node
     * @return         A List of ZooData.Nodes that contains the shortest path through
     *                 the zoo from source to target
     */
    public List<ZooData.Node> nearestNode(ZooData.Node source)
    {
        // Gives us all of the shortest paths from source to every node on the graph
        ShortestPathAlgorithm.SingleSourcePaths<String,IdentifiedWeightedEdge> ssPaths =
                new DijkstraShortestPath<>(g).getPaths(source.id);

        double currMinWeight = Double.MAX_VALUE;
        double currWeight;
        List<String> currMinPath = new ArrayList<String>();


        // Iterate through each of the paths to TARGET NODES and find the shortest one
        for(int i = 0; i < targets.size(); i++){
            currWeight = ssPaths.getWeight(getGroupOrDefaultId((targets.get(i))));

            if(currWeight < currMinWeight) {
                currMinWeight = currWeight;
                currMinPath = new ArrayList<>(ssPaths.getPath
                                (getGroupOrDefaultId(targets.get(i)))
                        .getVertexList());
            }
            // TODO: edge case when source and target in the same group
            if (currWeight == 0 && targets.get(i).group_id != null){
                currMinPath.add(getGroupOrDefaultId(targets.get(i)));
            }
        }86.0

        List<ZooData.Node> returnPath = new ArrayList<ZooData.Node>();

        // Turn the list of node names into the list of nodes
        for(int i = 0; i < currMinPath.size(); i++){
            returnPath.add(nodes.get(currMinPath.get(i)));
        }

        return returnPath;
    }

    /**
     * Method: pathGeneratorFromNode
     * Desc  : Generates an approximately optimal path between the target exhibits that the
     *         user wants to visit. This is done by repeatedly finding the nearest unvisited
     *         target node from where the user currently is using nearestNode
     * @param  start  the the node that the route starts from
     * @return A List of ZooData.Node's, in order from start to end, starting at the start and
     *         ending at the entrance/exit gate
     */
    public List<ZooData.Node> pathGeneratorFromNode(ZooData.Node start){
        // Used to return the correct order of exhibits
        List<ZooData.Node> route = new ArrayList<ZooData.Node>();

        // Route always starts at the entrance gate
        route.add(start);

        ZooData.Node source;
        while(!(targets.isEmpty())) {

            // Set source to the last element in route
            source = route.get(route.size()-1);

            // Removes last value of route to prevent duplicates
            route.remove(route.size()-1);
            route.addAll(nearestNode(source));

            // Removes whatever target was added to the route from the targets list
            targets
                    .remove(
                            targets.stream().filter(target -> route.stream().map(e -> e.id)
                                            .collect(Collectors.toList())
                                            .contains(getGroupOrDefaultId(target)))
                                    .findFirst()
                                    .get()
                    );


//            targets.removeIf(target -> route.stream().map(e -> e.id).collect(Collectors.toList())
//                    .contains(getGroupOrDefaultId(target)));

            // This causes an infinite loop because targets never removed
//            for(int i = 0; i < targets.size(); i++){
//                if(targets.get(i).id.equals(route.get(route.size() - 1).id)){
//                    targets.remove(i);
//                }
//            }
        }
        source = route.get(route.size()-1);
        route.remove(route.size()-1);

        // find the path to the exit from the last exhibit
        targets.add(getEntranceExitNode());;
        route.addAll(nearestNode(source));

        staticroute = route;
        return route;
    }

    /**
     * Method: pathGenerator
     * Desc  : Generates the path, starting at the entrance/exit node
     * @return route through all of the target exhibits, starting at the entrance/exit node
     */
    public List<ZooData.Node> pathGenerator(){
        return pathGeneratorFromNode(getEntranceExitNode());
    }

    /**
     * Method: generateCumDistances
     * Desc  : Generates the Cumulative distances along the path ;)
     * @param route   The route used to generate the cumulative distances
     * @return        List of Doubles containing the cumulative distances
     */
    public List<Double> generateCumDistances(List<ZooData.Node> route){
        List<Double> returnList = new ArrayList<Double>();

        // return if the route has less than 2 nodes, as that means it doesn't have edges
        if(route.size() < 2){
            return returnList;
        }

        // Add the first edge, then iterate through remaining ones

        //TODO: treat child-exhibit as their parent
        Log.d("TAG", "generateCumDistances: " + route.get(0).id);
        Log.d("TAG", "generateCumDistances: " + getGroupOrDefaultId(route.get(1)));
        returnList.add(
                (g.getEdge(route.get(0).id, getGroupOrDefaultId(route.get(1)))).getWeight()
        );
        for(int i = 1; i < route.size()-1; i++){
            // Add the current edge weight plus the previous
            // returnList entry to make it cumulative
            // TODO: account for the case that two consecutive exhibits in same group,
            //  so in the route, their parent_id appears twice
            String fromId = route.get(i).id;
            String toId = route.get(i+1).id;
            double currentEdgeWeight = 0;
            if (fromId != toId){
                currentEdgeWeight = g.getEdge(fromId, toId).getWeight();
            }
            returnList.add(returnList.get(i-1) + currentEdgeWeight);
//            returnList.add(g.getEdge(route.get(i).id, getGroupOrDefaultId(route.get(i+1))).getWeight()
//                    + returnList.get(i-1));
        }
        return returnList;
    }

    private String getGroupOrDefaultId(ZooData.Node node){
        if (node.hasGroup()){
            return node.group_id;
        }
        return node.id;
    }

    /**
     * Method: generateDistances
     * Desc  : Generates the distances of each edge along the path
     * @param route   The route used to generate each of the distances
     * @return        List of Doubles containing the individual distances
     */
    public List<Double> generateDistances(List<ZooData.Node> route){
        List<Double> returnDists = new ArrayList<Double>();

        // return if the route has less than 2 nodes, as that means it doesn't have edges
        if(route.size() < 2){
            return returnDists;
        }

        //TODO: treat child-exhibit as their parent
        for(int i = 0; i < route.size()-1; i++){
            String fromId = route.get(i).id;
            String toId = route.get(i+1).id;
            double currentEdgeWeight = 0;
            if (fromId != toId){
                System.out.println(fromId);
                System.out.println(toId);
                currentEdgeWeight = g.getEdge(fromId, toId).getWeight();
            }
            returnDists.add(currentEdgeWeight);
        }
        return returnDists;
    }



    /**
     * Method: exhibitDistances
     * Desc  : Maps cumulative distances to each of the target exhibits
     *         that the user wants to visit
     * @param exhibits   The list of exhibits that the user wants to visit
     * @return           Map of exhibit id's to their respective cumulative distance
     */
    public Map<String, Double> exhibitDistances(List<ZooData.Node> exhibits){
        List<Double> distances;
        Map<String, Double> distanceMap = new HashMap<String, Double>();
        List<ZooData.Node> exCopy = copyZooList(exhibits); // Copied so we can remove safely

        staticroute = pathGenerator();

        // Generate cumulative distances
        distances = generateCumDistances(staticroute);

        // Compare with the parameter
        for(int i = 0; i < staticroute.size()-1; i++) {
            // Iterate through all targets
            for(int j = 0; j < exCopy.size(); j++){
                // If the current node on the route is an exhibit, add its distance
                if(staticroute.get(i+1).id.equals(getGroupOrDefaultId(exCopy.get(j)))){
                    distanceMap.put(exCopy.get(j).id, distances.get(i));
                    exCopy.remove(j);
                    break;
                }
//                if(staticroute.get(i+1).id.equals(exCopy.get(j).id)){
//                    distanceMap.put(staticroute.get(i + 1).id, distances.get(i));
//                    exCopy.remove(j);
//                    break;
//                }
            }
        }
        return distanceMap;
    }

    /**
     * Method: nextExhibitInRoute
     * Desc  : Finds the next exhibit in the current route
     * @param currNode the node that the user is currently at
     * @return         the ZooData.Node value of the next exhibit in the route
     */
    public ZooData.Node nextExhibitInRoute(ZooData.Node currNode){

        if(staticroute.contains(currNode)){


            for(int i = staticroute.indexOf(currNode); i < staticroute.size(); i++){

                for(int j = 0; j < targets.size(); j++){
                    if(targets.get(j).id.equals(staticroute.get(i).id)){
                        //System.out.println("FINAL THING: " + staticroute.get(i));
                        return staticroute.get(i);
                    }
                }
            }
        }


        return getEntranceExitNode();
    }

    /**
     * Method: distanceBetweenNodes
     * Desc  : Provides the distance (in feet) along the route of two nodes
     * @param startNode The first node to be compared
     * @param endNode   The second node to be compared
     * @return          The distance between these nodes, as as double
     */
    public double distanceBetweenNodes(ZooData.Node startNode, ZooData.Node endNode){
        int startIndex = -1; // index in route of startNode
        int endIndex = -1; // index in route of endNode
        double returnDist = 0; // distance between the nodes
        List<Double> distances = generateDistances(staticroute);

        for(int i = 0; i < staticroute.size(); i++){
            // Get the id of the startNode
            if(staticroute.get(i).id.equals(startNode.id)) {
                startIndex = i;
            }

            //Get the id of the endNode
            if(staticroute.get(i).id.equals(endNode.id)){
                // ensure that endIndex comes after startIndex
                if(startIndex >= 0){
                    endIndex = i;
                    break;
                }
            }
        }

        // Return early for edge case if either start or end node aren't in route
        if(startIndex < 0 || endIndex < 0 ) {
            return -1;
        }

        // Now, iterate between the indices to get the total distance
        for(int i = startIndex; i < endIndex; i++){
            returnDist += distances.get(i);
        }

        return returnDist;

    }

    /**


     * Method: clearRouteFromIndex
     * Desc  : Clears all of the nodes of the route after a specific index, so a new route
     *         can be appended to it
     * @param route the route to be chopped
     * @param index the index to clear the route after
     * @return      A list of ZooData.Node's that contains the edited route
     */
    public List<ZooData.Node> clearRouteFromIndex(List<ZooData.Node> route, int index){
        for(int i = index; i < route.size();){
            route.remove(i);
        }
        return route;
    }

}
