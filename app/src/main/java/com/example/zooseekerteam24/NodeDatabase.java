package com.example.zooseekerteam24;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ZooData.Node.class}, version = 1)
public abstract class NodeDatabase extends RoomDatabase {
    public abstract NodeDao nodeDao();
}
