package com.example.zooseekerteam24;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Collections;
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

    public void setTargets(List<ZooData.Node> targets){
        this.targets = targets;
    }

    // TODO
    public ZooData.Node getEntranceNode(){
        return nodes.get("entrance_exit_gate");
        //return null;
    }

    // TODO
    public ZooData.Node getExitNode(){
        return nodes.get("entrance_exit_gate");
        // return null;
    }

    // TODO

    public List<ZooData.Node> nearestNode(ZooData.Node source)
    {
        ShortestPathAlgorithm.SingleSourcePaths<String,IdentifiedWeightedEdge> ssPaths =
                new DijkstraShortestPath<>(g).getPaths(source.id);

        double currMinWeight = Double.MAX_VALUE;
        double currWeight;
        List<String> currMinPath = new ArrayList<String>();

        // Iterate through each of the paths to TARGET NODES and find the shortest one
        for(int i = 0; i < targets.size(); i++){
            currWeight = ssPaths.getWeight((targets.get(i)).id);
            if(currWeight < currMinWeight) {
                currMinWeight = currWeight;
                currMinPath = (ssPaths.getPath((targets.get(i)).id)).getVertexList();
            }
        }

        List<ZooData.Node> returnPath = new ArrayList<ZooData.Node>();

        // Turn the list of node names into the list of nodes
        for(int i = 0; i < currMinPath.size(); i++){
            returnPath.add(nodes.get(currMinPath.get(i)));
        }

        return returnPath;
    }

    public List<ZooData.Node> pathGenerator(){
        // used to return the correct order of exhibits
        List<ZooData.Node> route = new ArrayList<ZooData.Node>();

        route.add(getEntranceNode());

        ZooData.Node source;
        while(!(targets.isEmpty())) {

            // set source to the last element in route
            source = route.get(route.size()-1);
            // removes last value of route to prevent duplicates
            route.remove(route.size()-1);
            route.addAll(nearestNode(source));
            // removes whatever target was added to the route from the targets list
            for(int i = 0; i < targets.size(); i++){
                if(targets.get(i).id.equals(route.get(route.size() - 1).id)){
                    targets.remove(i);
                }
            }
        }
        //source = route.get(route.size()-1);
        //route.remove(route.size()-1);


        // find the path to the exit from the last exhibit
        //targets.add(getExitNode());
        //List<ZooData.Node> TEMPEXIT = new ArrayList<ZooData.Node>();
        //TEMPEXIT.add(getExitNode());
        //route.addAll(nearestNode(source, targets));

        return route;
    }

    // Generates Cumulative Distances ;)
    public Map<ZooData.Node, Double> generateCumDistances(List<ZooData.Node> route){
        Map<ZooData.Node, Double> returnMap = new HashMap<ZooData.Node, Double>();

        returnMap.put(route.get(0),(g.getEdge(route.get(0).id,route.get(1).id)).getWeight());
        for(int i = 1; i < route.size()-1; i++){
            //holy FUCK

            returnMap.put(route.get(i),(g.getEdge(route.get(i).id,
                    route.get(i+1).id)).getWeight() +
                    returnMap.get(route.get(i-1)));
        }
        return returnMap;
    }

    public Map<String, Double> fakeMethod(){
//        List<ZooData.Node> route = pathGenerator();
        GraphPath<String, IdentifiedWeightedEdge> path;
        HashMap<String, Double> distancedMap = new HashMap<>();
        targets.add(0, getEntranceNode());
        targets.add(getEntranceNode());
        double totalDistance = 0;
//        targets.add(getEntranceNode());

//        path = DijkstraShortestPath
//                    .findPathBetween(g, targets.get(0).id, targets.get(targets.size()-1).id);
//        System.out.printf("The shortest path from '%s' to '%s' is:\n", targets
//                .get(0).name, targets.get(targets.size()-1).name);
//        int j = 1;
//        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
//            System.out.printf("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
//                    j,
//                    g.getEdgeWeight(e),
//                    edges.get(e.getId()).street,
//                    nodes.get(g.getEdgeSource(e).toString()).name,
//                    nodes.get(g.getEdgeTarget(e).toString()).name);
//            j++;
//        }
        for (int i = 0; i < targets.size()-1; i++){
            ZooData.Node start = targets.get(i);
            ZooData.Node end = targets.get(i+1);
            path = DijkstraShortestPath
                    .findPathBetween(g, start.id, end.id);
            System.out.printf("The shortest path from '%s' to '%s' is:\n", start.name, end.name);
            int j = 1;
            for (IdentifiedWeightedEdge e : path.getEdgeList()) {
                System.out.printf("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                        j,
                        g.getEdgeWeight(e),
                        edges.get(e.getId()).street,
                        nodes.get(g.getEdgeSource(e).toString()).name,
                        nodes.get(g.getEdgeTarget(e).toString()).name);
                j++;
            }
            if (end.kind == ZooData.Node.Kind.EXHIBIT){
                totalDistance += path.getWeight();
                distancedMap.put(end.id, totalDistance);
            }
            System.out.printf("total weight = %.0f\n", path.getWeight());
        }
        return distancedMap;
    }

    // Generates Individual Distances
    public Map<ZooData.Node, Double> generateDistances(List<ZooData.Node> route){
        Map<ZooData.Node, Double> returnMap = new HashMap<ZooData.Node, Double>();
        for(int i = 0; i < route.size()-1; i++){
            //holy FUCK

            returnMap.put(route.get(i),(g.getEdge(route.get(i).id,
                    route.get(i+1).id)).getWeight());
        }
        return returnMap;
    }
}
