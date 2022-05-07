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
    static List<ZooData.Node> staticroute;

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

    // Performs a deep copy of targets
    public void setTargets(List<ZooData.Node> targets){
        this.targets = copyZooList(targets);
    }

    public List<ZooData.Node> copyZooList(List<ZooData.Node> nodes){
        List<ZooData.Node> newList = new ArrayList<ZooData.Node>();
        for(int i = 0; i < nodes.size(); i++) {
            newList.add(nodes.get(i));
        }
        return newList;
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
        source = route.get(route.size()-1);
        route.remove(route.size()-1);

        // find the path to the exit from the last exhibit
        targets.add(getExitNode());;
        route.addAll(nearestNode(source));

        staticroute = route;
        return route;
    }

    // Generates Cumulative Distances ;)
    public List<Double> generateCumDistances(List<ZooData.Node> route){
        List<Double> returnList = new ArrayList<Double>();

        if(route.size() < 2){
            return returnList;
        }

        returnList.add((g.getEdge(route.get(0).id,route.get(1).id)).getWeight());
        for(int i = 1; i < route.size()-1; i++){
            //holy FUCK

            returnList.add((g.getEdge(route.get(i).id,
                    route.get(i+1).id)).getWeight() +
                    returnList.get(i-1));
        }
        return returnList;
    }

    // Generates Individual Distances
    public List<Double> generateDistances(List<ZooData.Node> route){
        List<Double> returnDists = new ArrayList<Double>();
        for(int i = 0; i < route.size()-1; i++){
            //holy FUCK
            returnDists.add((g.getEdge(route.get(i).id,
                    route.get(i+1).id)).getWeight());
        }
        return returnDists;
    }

    public Map<String, Double> exhibitDistances(List<ZooData.Node> exhibits){
        List<Double> distances;
        Map<String, Double> distanceMap = new HashMap<String, Double>();
        List<ZooData.Node> exCopy = copyZooList(exhibits); //copied so we can remove safely

        staticroute = pathGenerator();

        // generate cumulative distances
        distances = generateCumDistances(staticroute);

        // compare with the parameter
        for(int i = 0; i < staticroute.size()-1; i++) {
            //iterate through all targets
            for(int j = 0; j < exCopy.size(); j++){
                if(staticroute.get(i+1).id.equals(exCopy.get(j).id)){
                    distanceMap.put(staticroute.get(i + 1).id, distances.get(i));
                    exCopy.remove(j);
                    break;
                }
            }
        }
        return distanceMap;
    }
}
