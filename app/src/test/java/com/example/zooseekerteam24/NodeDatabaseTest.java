package com.example.zooseekerteam24;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NodeDatabaseTest {
    private NodeDao dao;
    private NodeDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.nodeDao();
    }

    @Test
    public void testInsert() {
        ZooData.Node n1 = new ZooData.Node();
        n1.name = "Dragon";
        n1.id = "dragon1";
        n1.tags = Arrays.asList("mammal", "precious");

        ZooData.Node n2 = new ZooData.Node();
        n2.name = "Ditto";
        n2.id = "ditto1";
        n2.tags = Arrays.asList("pokemon", "morphs");

        long id1 = dao.insert(n1);
        long id2 = dao.insert(n2);

        // Check that these have all been inserted with unique IDs
        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        ZooData.Node n1 = new ZooData.Node();
        n1.name = "Dragon";
        n1.id = "dragon1";
        n1.tags = Arrays.asList("mammal", "precious");

        long id = dao.insert(n1);

        ZooData.Node nget = dao.get(id);
        assertEquals(n1.name, nget.name);
        assertEquals(n1.tags, nget.tags);
        assertEquals(n1.rtId, nget.rtId);
    }

    @Test
    public void testDelete() {
        ZooData.Node n1 = new ZooData.Node();
        n1.name = "Dragon";
        n1.id = "dragon1";
        n1.tags = Arrays.asList("mammal", "precious");

        long id = dao.insert(n1);

        ZooData.Node nget = dao.get(id);

        //int itemsDeleted = dao.delete(nget);
        //assertEquals(1, itemsDeleted);
        assertNull(dao.get(id));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
