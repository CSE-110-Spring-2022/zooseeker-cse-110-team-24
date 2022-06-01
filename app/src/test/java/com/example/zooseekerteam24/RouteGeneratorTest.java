package com.example.zooseekerteam24;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RunWith(AndroidJUnit4.class)
public class RouteGeneratorTest {

//    private String edgeFile = "sample_edge_info.json";
//    private String graphFile = "sample_zoo_graph.json";
//    private String nodeFile = "sample_exhibits.json";

    private String edgeFile = "zoo_edge_info.json";
    private String graphFile = "zoo_graph.json";
    private String nodeFile = "zoo_node_info.json";

    List<ZooData.Node> targets;
    Map<String, ZooData.Node> nodes;
    Map<String, ZooData.Edge> edges;
    Graph<String, IdentifiedWeightedEdge> g;
    private RouteGenerator generator;

    private List<ZooData.Node> targetExhibits = Collections.emptyList();
    private List<ZooData.Node> route = Collections.emptyList();


    @Before
    public void createGenerator(){
        Context context = ApplicationProvider.getApplicationContext();

        nodes = ZooData.loadNodesFromJSON(context, nodeFile);
        edges = ZooData.loadEdgesFromJSON(context, edgeFile);
        g = ZooData.loadZooGraphJSON(context, graphFile);

        generator = new RouteGenerator(context, targetExhibits, nodes, edges ,g );

        //NodeDao nodeDao = NodeDatabase.getSingleton(context).nodeDao();
        targetExhibits = new ArrayList<ZooData.Node>();
        targetExhibits.add(nodes.get("koi"));
        targetExhibits.add(nodes.get("flamingo"));
        generator.setTargets(targetExhibits);

    }



    @Test
    public void testEntranceExitNode(){
        assertEquals(nodes.get("entrance_exit_gate"), generator.getEntranceExitNode());
    }


    @Test
    public void testGetNearestNode(){
        List<ZooData.Node> path = new ArrayList<ZooData.Node>();
        path.add(nodes.get("entrance_exit_gate"));
        path.add(nodes.get("intxn_front_treetops"));
        path.add(nodes.get("intxn_front_monkey"));
        path.add(nodes.get("flamingo"));
        assertEquals(path, generator.nearestNode(nodes.get("entrance_exit_gate")));
    }


    @Test
    public void testPathGenerator(){
        List<ZooData.Node> path = new ArrayList<ZooData.Node>();
        path.add(nodes.get("entrance_exit_gate"));
        path.add(nodes.get("intxn_front_treetops"));
        path.add(nodes.get("intxn_front_monkey"));
        path.add(nodes.get("flamingo"));
        path.add(nodes.get("intxn_front_monkey"));
        path.add(nodes.get("intxn_front_treetops"));
        path.add(nodes.get("intxn_front_lagoon_1"));
        path.add(nodes.get("koi"));
        path.add(nodes.get("intxn_front_lagoon_1"));
        path.add(nodes.get("intxn_front_treetops"));
        path.add(nodes.get("entrance_exit_gate"));
        assertEquals(path, generator.pathGenerator());
    }

    @Test
    public void testOptimalpath(){

        // we add more exhibits to the path
        targetExhibits.add(nodes.get("hippo"));
        targetExhibits.add(nodes.get("crocodile"));
        generator.setTargets(targetExhibits);

        List<ZooData.Node> path = generator.pathGenerator();
        ZooData.Node currNode = nodes.get("entrance_exit_gate");

        for( int i=0 ; i < path.size() ; i++){
            ZooData.Node lastNode = currNode;
            List<ZooData.Node> currPath = generator.nearestNode(currNode);
            currNode = currPath.get(currPath.size()-1);//last item in list

            //asserts that the nearest node from the curr exhibit is the next Exhibit in the route
            //this is exactly how we build our approximately optimal path
            assertEquals(currNode, generator.nextExhibitInRoute(lastNode));
        }

    }

    @Test
    public void testCumDistances(){
        List<Double> distances = new ArrayList<>();
        distances.add(1100.0);
        distances.add(3800.0);
        distances.add(5300.0);
        distances.add(6800.0);
        distances.add(9500.0);
        distances.add(12700.0);
        distances.add(14900.0);
        distances.add(17100.0);
        distances.add(20300.0);
        distances.add(21400.0);

        assertEquals(distances,
                generator.generateCumDistances(generator.pathGenerator()));

    }

    @Test
    public void testDistances(){
        List<Double> distances = new ArrayList<>();
        distances.add(1100.0);
        distances.add(2700.0);
        distances.add(1500.0);
        distances.add(1500.0);
        distances.add(2700.0);
        distances.add(3200.0);
        distances.add(2200.0);
        distances.add(2200.0);
        distances.add(3200.0);
        distances.add(1100.0);

        assertEquals(distances,
                generator.generateDistances(generator.pathGenerator()));

    }

    @Test
    public void testExhibitDistances(){
        Map<String, Double> distanceMap = new HashMap<String, Double>();
        distanceMap.put("koi" ,14900.0 );
        distanceMap.put("flamingo" , 5300.0);
        assertEquals(distanceMap, generator.exhibitDistances(targetExhibits));
    }

}