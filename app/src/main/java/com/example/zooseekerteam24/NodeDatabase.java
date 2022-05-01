package com.example.zooseekerteam24;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Database(entities = {ZooData.Node.class}, version = 1)
public abstract class NodeDatabase extends RoomDatabase {
    private static final String TAG = "NodeDatabase";
    private static NodeDatabase singleton = null;

    public abstract NodeDao nodeDao();

    // Factory pattern
    public synchronized static NodeDatabase getSingleton(Context context){
        if (singleton == null){
            singleton = createDatabase(context);
        }
        return singleton;
    }

    private static NodeDatabase createDatabase(Context context){
        return Room.databaseBuilder(context, NodeDatabase.class, "zoo3.db")
                .allowMainThreadQueries()
                .addCallback(new Callback(){
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<ZooData.Node> nodes = ZooData.loadNodesFromJSON(context, "db2.json")
                                        .values()
                                        .stream().collect(Collectors.toList());
                                Log.d("nodes to be added", "run: " + nodes.size());
                                getSingleton(context).nodeDao().insertAll(nodes);
                            }
                        });
                    }
                })
                .build();

    }

}
