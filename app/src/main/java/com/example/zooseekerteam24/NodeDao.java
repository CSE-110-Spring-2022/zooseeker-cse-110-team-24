package com.example.zooseekerteam24;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM `node` WHERE `rtId`=:rtid")
    ZooData.Node get(long rtid);

    @Query ("SELECT * FROM `node`")
    List<ZooData.Node> getAll();

    @Query ("SELECT * FROM `node` WHERE `kind`='EXHIBIT'")
    LiveData<List<ZooData.Node>> getAllLive();

    @Query ("SELECT * FROM `node` WHERE `added`")
    LiveData<List<ZooData.Node>> getAllAddedLive();

    @Update
    int update(ZooData.Node exhibit);

//    @Delete
//    List<Long> deleteAll (ZooData.Node exhibit);
}
