package com.example.zooseekerteam24;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public void toggleExhibitAdded(ZooData.Node exhibit){
        exhibit.added = !exhibit.added;
        nodeDao.update(exhibit);
    }

    public void orderExhibitsAdded(RouteGenerator generator){
        Log.d("orderExhibitsAdded", "is called");
        List<ZooData.Node> exhibits = nodeDao.getAllAdded();
        generator.setTargets(exhibits);
        Map<String, Double> distanceMap = generator.fakeMethod();
        exhibits.forEach(ex -> {
            ex.cumDistance = distanceMap.getOrDefault(ex.id, -10.0);
            nodeDao.update(ex);
        });
    }

    public void reOrder(List<ZooData.Node> oldExhibits, List<ZooData.Node> newExhibits) {
        oldExhibits = newExhibits;
    }

//    private void loadNodes() {
//        // Do an asynchronous operation to fetch nodes.
//        nodes = nodeDao.getAllLive();
//    }
}
