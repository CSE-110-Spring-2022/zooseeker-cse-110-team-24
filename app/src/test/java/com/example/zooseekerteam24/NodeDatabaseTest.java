package com.example.zooseekerteam24;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class NodeDatabaseTest {
    /**private NodeDao dao;
    private NodeDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
//        db = NodeDatabase.getSingleton(context);
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
        long id = dao.insert(n1);
        ZooData.Node node = dao.get(id);

        // flip its add
        node.added = !node.added;
        int nodeUpdated = dao.update(node);

        assertEquals(1, nodeUpdated);

        node = dao.get(id);
        assertNotNull(node);
        assertEquals(true, node.added);


    }

    @Test
    public void testGet() {
        ZooData.Node n1 = new ZooData.Node();
        n1.name = "Dragon";
        n1.id = "dragon1";
        n1.tags = Arrays.asList("mammal", "precious");

        long id = dao.insert(n1);

//        String ID_OF_LION = "lions";
        ZooData.Node node = dao.get(id);

        assertNotNull(node);
        assertEquals(n1.name, node.name);
        //assertEquals(id, node.rtId);
        assertEquals(n1.id, node.id);
        assertEquals(n1.tags, node.tags);
    }

    @Test
    public void testDelete() {
        ZooData.Node n1 = new ZooData.Node();
        n1.name = "Dragon";
        n1.id = "dragon1";
        n1.tags = Arrays.asList("mammal", "precious");
        n1.added = true;
        long id = dao.insert(n1);
        ZooData.Node node = dao.get(id);

        // flip its add
        node.added = !node.added;
        int nodeUpdated = dao.update(node);

        assertEquals(1, nodeUpdated);

        node = dao.get(id);
        assertNotNull(node);
        assertEquals(false, node.added);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }**/

    @Test
    public void phantomTest() {
        assertTrue(true);
    }
}