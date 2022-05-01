package com.example.zooseekerteam24;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * helper class that store and manage UI-related data in a lifecycle-aware way
 * retain data upon configuration changes
 * if developer correctly acquire and keep data in VM
 *
 * you can access data stored here from an Activity
 */
public class PlannerViewModel extends AndroidViewModel {
    private LiveData<List<ZooData.Node>> nodes;
    private final NodeDao nodeDao;

    public PlannerViewModel(@NonNull Application application) {
        super(application);
//        Log.d("curious", get.equals(getApplication())+"");
        nodeDao = NodeDatabase
                .getSingleton(getApplication().getApplicationContext())
                .nodeDao();
    }


    // Get the newest nodes
    public LiveData<List<ZooData.Node>> getNodes() {
        if (nodes == null) {
            // TODO: Do an asynchronous operation to fetch nodes.
            Log.d("getNodes", "a");
            nodes = nodeDao.getAllLive();
        }
        Log.d("getNodes", "b");
        return nodes;
    }

    // get added nodes
    public LiveData<List<ZooData.Node>> getAddedNodes() {
        if (nodes == null) {
            // TODO: Do an asynchronous operation to fetch nodes.
            nodes = nodeDao.getAllAddedLive();
        }
        return nodes;
    }


    public void addExhibit(ZooData.Node exhibit){
        exhibit.added = !exhibit.added;
        nodeDao.update(exhibit);
    }

//    private void loadNodes() {
//        // Do an asynchronous operation to fetch nodes.
//        nodes = nodeDao.getAllLive();
//    }
}
