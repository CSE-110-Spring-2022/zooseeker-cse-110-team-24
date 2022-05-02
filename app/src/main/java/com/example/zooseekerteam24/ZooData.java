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

public class ZooData {


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

        @Override
        public String toString() {
            return "Node{" +
                    "rtId=" + rtId +
                    ", id='" + id + '\'' +
                    ", kind=" + kind +
                    ", name='" + name + '\'' +
                    ", tags=" + tags +
                    ", added=" + added +
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

    public static List<Node> loadExhibitsFromJSON(Context context, String filename){
        Map<String, Node> indexedNodes = loadNodesFromJSON(context, filename);
        List<Node> exhibits = indexedNodes.values()
                .stream()
                .filter(node -> node.kind.equals(Node.Kind.EXHIBIT))
                .collect(Collectors.toList());
        return exhibits;
    }

//    String id;
//    String itemType;
//    List<String> tags;
//
//    public ZooData(String id, String itemType, List<String> tags) {
//        this.id = id;
//        this.itemType = itemType;
//        this.tags = tags;
//    }
//
//    @Override
//    public String toString() {
//        return "Node{" +
//                "id='" + id + '\'' +
//                ", itemType='" + itemType + '\'' +
//                ", tags=" + tags +
//                '}';
//    }
//
//
}
