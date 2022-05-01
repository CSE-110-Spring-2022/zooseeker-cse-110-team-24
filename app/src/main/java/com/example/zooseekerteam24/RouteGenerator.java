package com.example.zooseekerteam24;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    // TODO
    public ZooData.Node getEntranceNode(){
        return null;
    }

    // TODO
    public ZooData.Node getExitNode(){
        return null;
    }

    // TODO

    public List<ZooData.Node> nearestNode(ZooData.Node source, List<ZooData.Node> targets)
    {

        ShortestPathAlgorithm.SingleSourcePaths<String,IdentifiedWeightedEdge> ssPaths =
                new DijkstraShortestPath<>(g).getPaths(source.name);

        // TODO find a way to iterate through the paths in ssPaths to find the shortest path
        // This path can be converted into a graphpath or list of vertices or whatever

        return null;
    }


    public List<ZooData.Node> pathGenerator(List<ZooData.Node> targets){
        // used to return the correct order of exhibits
        List<ZooData.Node> route = new ArrayList<ZooData.Node>();

        route.add(getEntranceNode());

        ZooData.Node source;
        while(!targets.isEmpty()) {
            // set source to the last element in route
            source = route.get(route.size()-1);
            //removes last value of route to prevent duplicates
            route.remove(route.size()-1);
            route.addAll(nearestNode(source, targets));
            // removes whatever target was added to the route from the targets list
            targets.remove(route.get(route.size()-1));
        }
        source = route.get(route.size()-1);
        route.remove(route.size()-1);

        // find the path to the exit from the last exhibit
        targets.add(getExitNode());
        route.addAll(nearestNode(source, targets));

        return route;
    }

    //TODO direction edges from route nodes
}
