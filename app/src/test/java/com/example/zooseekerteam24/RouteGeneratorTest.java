package com.example.zooseekerteam24;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RunWith(AndroidJUnit4.class)
public class RouteGeneratorTest {

    private String edgeFile = "sample_edge_info.json";
    private String graphFile = "sample_zoo_graph.json";
    private String nodeFile = "sample_exhibits.json";

    List<ZooData.Node> targets;
    Map<String, ZooData.Node> nodes;
    Map<String, ZooData.Edge> edges;
    Graph<String, IdentifiedWeightedEdge> g;
    private RouteGenerator generator;

    private List<ZooData.Node> exhibits = Collections.emptyList();
    private List<ZooData.Node> route = Collections.emptyList();


    @Before
    public void createGenerator(){
        Context context = ApplicationProvider.getApplicationContext();

        nodes = ZooData.loadNodesFromJSON(context, nodeFile);
        edges = ZooData.loadEdgesFromJSON(context, edgeFile);
        g = ZooData.loadZooGraphJSON(context, graphFile);

        generator = new RouteGenerator(context, exhibits, nodes, edges ,g );

        //NodeDao nodeDao = NodeDatabase.getSingleton(context).nodeDao();
        exhibits = new ArrayList<ZooData.Node>();
        exhibits.add(nodes.get("arctic_foxes"));
        exhibits.add(nodes.get("gorillas"));
        generator.setTargets(exhibits);

    }

    @Test
    public void testEntranceExitNode(){
        assertEquals(nodes.get("entrance_exit_gate"), generator.getEntranceExitNode());
    }

    @Test
    public void testGetNearestNode(){
        List<ZooData.Node> path = new ArrayList<ZooData.Node>();
        path.add(nodes.get("entrance_exit_gate"));
        path.add(nodes.get("entrance_plaza"));
        path.add(nodes.get("gorillas"));
        assertEquals(path, generator.nearestNode(nodes.get("entrance_exit_gate")));
    }


    @Test
    public void testPathGenerator(){
        List<ZooData.Node> path = new ArrayList<ZooData.Node>();
        path.add(nodes.get("entrance_exit_gate"));
        path.add(nodes.get("entrance_plaza"));
        path.add(nodes.get("gorillas"));
        path.add(nodes.get("entrance_plaza"));
        path.add(nodes.get("arctic_foxes"));
        path.add(nodes.get("entrance_plaza"));
        path.add(nodes.get("entrance_exit_gate"));
        assertEquals(path, generator.pathGenerator());
    }

    @Test
    public void testCumDistances(){
        List<Double> distances = new ArrayList<>();
        distances.add(10.0);
        distances.add(210.0);
        distances.add(410.0);
        distances.add(710.0);
        distances.add(1010.0);
        distances.add(1020.0);
        assertEquals(distances,
                generator.generateCumDistances(generator.pathGenerator()));

    }

    @Test
    public void testDistances(){
        List<Double> distances = new ArrayList<>();
        distances.add(10.0);
        distances.add(200.0);
        distances.add(200.0);
        distances.add(300.0);
        distances.add(300.0);
        distances.add(10.0);
        assertEquals(distances,
                generator.generateDistances(generator.pathGenerator()));

    }

    @Test
    public void testExhibitDistances(){
        Map<String, Double> distanceMap = new HashMap<String, Double>();
        distanceMap.put("gorillas" ,210.0 );
        distanceMap.put("arctic_foxes" , 710.0);
        assertEquals(distanceMap, generator.exhibitDistances(exhibits));
    }

}