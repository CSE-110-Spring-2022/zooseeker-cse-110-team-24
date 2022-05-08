package com.example.zooseekerteam24;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NodeActivityTest {
    NodeDatabase testDb;
    NodeDao nodeDao;

    //to be implemented for full integration test
    /*
    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0,0,1080,2280);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        NodeDatabase.injectTestDatabase(testDb);

        List<ZooData.Node> exhibits = ZooData.Node.loadJson(context, "replace_name.json");
        nodeDao = testDb.nodeDao();
        nodeDao.insertAll(exhibits);
    }

    @Test
    public void testAddExibit() {
        String nextText = "Ensure all tests pass";

        ActivityScenario<NodeActivity> scenario = ActivityScenario.launch(NodeActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            List<ZooData.Node> beforeList = NodeDao.getAllAdded();

            List<ZooData.Node> afterList = NodeDao.getAllAdded();
            assertEquals(beforeList.size() + 1, afterList.size());
            assertEquals("new exhibit added name", afterList.get(afterList.size() - 1).name);
        });
    }
    */
}
