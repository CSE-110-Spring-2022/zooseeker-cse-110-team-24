/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * File   : RouteGenerator.java
 * Authors: Dylan Govic and Jose Valdivia
 * Desc   : Contains methods necessary in order to handle the Zoo route
 *          Can generate the route from a list of target ZooData.Node's
 *          and find the distances along the path
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.zooseekerteam24;

import android.content.Context;

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
        System.err.println("No Entrance/Exit gate found.");
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
            currWeight = ssPaths.getWeight(getParentOrDefaultId((targets.get(i))));
            if(currWeight < currMinWeight) {
                currMinWeight = currWeight;
                currMinPath = (ssPaths.getPath
                        (getParentOrDefaultId(targets.get(i)))
                        .getVertexList());
            }
        }

        List<ZooData.Node> returnPath = new ArrayList<ZooData.Node>();

        // Turn the list of node names into the list of nodes
        for(int i = 0; i < currMinPath.size(); i++){
            returnPath.add(nodes.get(currMinPath.get(i)));
        }

        return returnPath;
    }

    /**
     * Method: pathGenerator
     * Desc  : Generates an approximately optimal path between the target exhibits that the
     *         user wants to visit. This is done by repeatedly finding the nearest unvisited
     *         target node from where the user currently is using nearestNode
     * @return A List of ZooData.Node's, in order from start to end, starting and ending at the
     *         entrance/exit gate.
     */
    public List<ZooData.Node> pathGenerator(){
        // Used to return the correct order of exhibits
        List<ZooData.Node> route = new ArrayList<ZooData.Node>();

        // Route always starts at the entrance gate
        route.add(getEntranceExitNode());

        ZooData.Node source;
        while(!(targets.isEmpty())) {

            // Set source to the last element in route
            source = route.get(route.size()-1);

            // Removes last value of route to prevent duplicates
            route.remove(route.size()-1);
            route.addAll(nearestNode(source));

            // Removes whatever target was added to the route from the targets list
            for (ZooData.Node target: targets){
                if (route.stream().map(e -> e.id).collect(Collectors.toList()).contains(getParentOrDefaultId(target))){
                    targets.remove(target);
                }
            }

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
        returnList.add((g.getEdge(route.get(0).id, getParentOrDefaultId(route.get(1))))
                .getWeight());
        for(int i = 1; i < route.size()-1; i++){
            // Add the current edge weight plus the previous
            // returnList entry to make it cumulative
            returnList.add((g.getEdge(route.get(i).id,
                    getParentOrDefaultId(route.get(i+1)))).getWeight()
                    + returnList.get(i-1));
        }
        return returnList;
    }

    private String getParentOrDefaultId(ZooData.Node node){
        if (node.parent_id != null){
            return node.parent_id;
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

        for(int i = 0; i < route.size()-1; i++){
            returnDists.add((g.getEdge(route.get(i).id,
                    route.get(i+1).id)).getWeight());
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
                if(staticroute.get(i+1).id.equals(getParentOrDefaultId(exCopy.get(j)))){
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
}
