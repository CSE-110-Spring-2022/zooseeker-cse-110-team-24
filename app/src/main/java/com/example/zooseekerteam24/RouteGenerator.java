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
        return nodes.get("entrance_exit_gate");
        //return null;
    }

    // TODO
    public ZooData.Node getExitNode(){
        return nodes.get("entrance_exit_gate");
        // return null;
    }

    // TODO

    public List<ZooData.Node> nearestNode(ZooData.Node source, List<ZooData.Node> targets)
    {
        System.err.println(source.id);
        ShortestPathAlgorithm.SingleSourcePaths<String,IdentifiedWeightedEdge> ssPaths =
                new DijkstraShortestPath<>(g).getPaths(source.id);

        double currMinWeight = Double.MAX_VALUE;
        double currWeight;
        List<String> currMinPath = new ArrayList<String>();

        // Iterate through each of the paths to TARGET NODES and find the shortest one
        for(int i = 0; i < targets.size(); i++){
            currWeight = ssPaths.getWeight((targets.get(i)).id);
            //System.err.println("target: " + targets.get(i).name);
            //System.err.println("currWeight: " + currWeight);
            if(currWeight < currMinWeight) {
                currMinWeight = currWeight;
                currMinPath = (ssPaths.getPath((targets.get(i)).id)).getVertexList();
            }
        }

        //System.err.println("currMinWeight: " + currMinWeight);

        List<ZooData.Node> returnPath = new ArrayList<ZooData.Node>();

        // Turn the list of node names into the list of nodes
        for(int i = 0; i < currMinPath.size(); i++){
            returnPath.add(nodes.get(currMinPath.get(i)));
        }

        return returnPath;
    }


    public List<ZooData.Node> pathGenerator(List<ZooData.Node> targets){
        // used to return the correct order of exhibits
        List<ZooData.Node> route = new ArrayList<ZooData.Node>();
        List<ZooData.Node> TEMPTARGETS = targets;

        route.add(getEntranceNode());

        ZooData.Node source;
        while(!TEMPTARGETS.isEmpty()) {

            System.err.println("targets.size() = "+TEMPTARGETS.size());
            // set source to the last element in route
            source = route.get(route.size()-1);
            //removes last value of route to prevent duplicates
            route.remove(route.size()-1);
            route.addAll(nearestNode(source, TEMPTARGETS));
            // removes whatever target was added to the route from the targets list
            TEMPTARGETS.remove(route.get(route.size()-1));
        }
        source = route.get(route.size()-1);
        route.remove(route.size()-1);

        //System.err.println("FUCK JUNIT TESTING AFGNASUGBK LOVE SPAMMING");


        // find the path to the exit from the last exhibit
        //targets.add(getExitNode());
        //List<ZooData.Node> TEMPEXIT = new ArrayList<ZooData.Node>();
        //TEMPEXIT.add(getExitNode());
        //route.addAll(nearestNode(source, targets));

        return route;
    }
}
