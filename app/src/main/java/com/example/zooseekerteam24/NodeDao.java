package com.example.zooseekerteam24;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NodeDao {
    @Insert
    long insert (ZooData.Node exhibit);

    /**
     * LiveData is an observable data holder that is lifecycle-aware
     * Observer registered to watch LiveData object gets notified when its active & underlyding data changes
     * so that Observer updates UI (we do so by update Adapter with new db contents
     */
    @Insert
    List<Long> insertAll (List<ZooData.Node> exhibits);

    @Query("SELECT * FROM `node` WHERE `rtId`=:rtId")
    ZooData.Node get(long rtId);

    @Query ("SELECT * FROM `node`")
    List<ZooData.Node> getAll();

    @Query ("SELECT * FROM `node`")
    LiveData<List<ZooData.Node>> getAllLive();

    @Delete
    int delete (ZooData.Node exhibit);
}