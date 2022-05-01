package com.example.zooseekerteam24;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NodeDao {
    @Insert
    long insert (ZooData.Node exhibit);

    @Query("SELECT * FROM `node` WHERE `rtId`=:rtId")
    ZooData.Node get(long rtId);

    @Query ("SELECT * FROM `node`")
    List<ZooData.Node> getAll();

    @Delete
    int delete (ZooData.Node exhibit);
}
