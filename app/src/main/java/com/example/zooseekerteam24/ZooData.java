package com.example.zooseekerteam24;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;


public class ZooData {

    public static final String EDGE_FILE = "sample_edge_info.json";
    public static final String GRAPH_FILE = "sample_zoo_graph.json";
    public static final String NODE_FILE = "sample_exhibits.json";

    @Entity(tableName = "node")
    public static class Node {

        enum Kind{
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("intersection") INTERSECTION
        }

        @PrimaryKey(autoGenerate = true)
        public long rtId;

        @NonNull
        public String id = "";

        public Kind kind;
        public String name;

        @TypeConverters({Converters.class})
        public List<String> tags;

        public boolean added = false;
        public double cumDistance = -1;

        @Override
        public String toString() {
            return "Node{" +
                    "rtId=" + rtId +
                    ", id='" + id + '\'' +
                    ", kind=" + kind +
                    ", name='" + name + '\'' +
                    ", tags=" + tags +
                    ", added=" + added +
                    ", cumDistance=" + cumDistance +
                    '}';
        }
    }

    public static class Edge {
        public String id;
        public String street;
    }


    public static Map<String, Node> loadNodesFromJSON(Context context, String filename){
        Map<String, ZooData.Node> indexedNodes = Collections.emptyMap();
        try {
            InputStream inputStream = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Node>>(){}.getType();
            List<Node> nodes = gson.fromJson(reader, listType);

            // index nodes
            indexedNodes = nodes.stream()
                    .collect(Collectors.toMap(node -> node.id, node->node));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexedNodes;
    }

    public static List<Node> loadListOfNodesFromJSON(Context context, String filename){
        List<Node> nodes = Collections.emptyList();
        try {
            InputStream inputStream = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Node>>(){}.getType();
            nodes = gson.fromJson(reader, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }


    public static Map<String, ZooData.Edge> loadEdgesFromJSON(Context context, String filename) {

        //InputStream inputStream = App.class.getClassLoader().getResourceAsStream(path);
        //InputStreamReader reader = new InputStreamReader(inputStream);
        Map<String, ZooData.Edge> indexedEdges = Collections.emptyMap();
        List<ZooData.Edge> zooData = Collections.emptyList();

        try{
            InputStream inputStream = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooData.Edge>>(){}.getType();
            zooData = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        indexedEdges = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        return indexedEdges;
    }

    public static Graph<String, IdentifiedWeightedEdge> loadZooGraphJSON
            (Context context ,String filename) {
        // Create an empty graph to populate.
        Graph<String, IdentifiedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        // On Android, you would use context.getAssets().open(path) here like in Lab 5.
        //InputStream inputStream = App.class.getClassLoader().getResourceAsStream(path);
        //Reader reader = new InputStreamReader(inputStream);
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = context.getAssets().open(filename);
            reader = new InputStreamReader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //

        // And now we just import it!
        importer.importGraph(g, reader);

        return g;
    }

}
