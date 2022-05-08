package com.example.zooseekerteam24;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;
import java.util.Observable;

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
    private ObservableInt numOfExhibits;

    public PlannerViewModel(@NonNull Application application) {
        super(application);
//        Log.d("curious", get.equals(getApplication())+"");
        nodeDao = NodeDatabase
                .getSingleton(getApplication().getApplicationContext())
                .nodeDao();
    }

    public ObservableInt getNumOfExhibits() {
        if (numOfExhibits == null) {
            numOfExhibits = new ObservableInt(nodeDao.getAllAdded().size());
        }
        Log.d("getNumOfExhibits", numOfExhibits.get()+"");
        return numOfExhibits;
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
            nodes = nodeDao.getAllOrderedAddedLive();
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
        Map<String, Double> distanceMap = generator.exhibitDistances(exhibits);
        exhibits.forEach(ex -> {
            ex.cumDistance = distanceMap.getOrDefault(ex.id, -10.0);
            nodeDao.update(ex);
        });
    }

    public void countExhibitsAdded(int i){
        numOfExhibits.set(i);
        Log.d("countExhibitsAdded", "countExhibitsAdded: " + numOfExhibits.get());
    }

//    private void loadNodes() {
//        // Do an asynchronous operation to fetch nodes.
//        nodes = nodeDao.getAllLive();
//    }
}
