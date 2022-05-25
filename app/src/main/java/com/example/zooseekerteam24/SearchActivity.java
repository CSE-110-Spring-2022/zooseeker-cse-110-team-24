package com.example.zooseekerteam24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private BottomNavigationView btmNavi;

    private SearchView searchView;
    private List<ZooData.Node> exhibits = new ArrayList<>();
    private ListView lvResults;
    private SearchResultAdapter adapter;
    PlannerViewModel plannerViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btmNavi = findViewById(R.id.btmNavi);
        searchView = findViewById(R.id.searchView);
        lvResults = findViewById(R.id.lvResults);


//        List<ZooData.Node> nodes = ZooData.loadListOfNodesFromJSON(this, ZooData.NODE_FILE);
//        nodes.forEach(n -> Log.d(TAG, "node: " + n));


        adapter = new SearchResultAdapter(this, exhibits);
        plannerViewModel = new ViewModelProvider(this)
                .get(PlannerViewModel.class);

        adapter.setOnAddBtnClickedHandler(plannerViewModel::toggleExhibitAdded);
        plannerViewModel.getAllExhibits().observe(this, adapter::populateSearch);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * onQueryTextSubmit
             * Called when the user submits the query.
             * @param query: query text in search bar
             * @return search results
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (TextUtils.isEmpty(searchView.getQuery())){
                    lvResults.setVisibility(View.INVISIBLE);
                }
                // TODO: if exhibits.contains
                Log.d("onQueryTextSubmit", query);
                adapter.getFilter().filter(query);
                return false;
            }

            /**
             * onQueryTextChange:
             * Called when the query text is changed by the user.
             * @param newText
             * @return filter out search results that matches the query text
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("onQueryTextChange", newText);

                // Blank when not searching
                if (lvResults.getVisibility()== View.INVISIBLE){
                    lvResults.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(searchView.getQuery())){
                    lvResults.setVisibility(View.INVISIBLE);
                }

                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }


        });



        lvResults.setAdapter(adapter);

        btmNavi.setSelectedItemId(R.id.icSearch);
        btmNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.icDirection:
                        Intent iDirection = new Intent(getApplicationContext(), DirectionActivity.class);
                        startActivity(iDirection);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.icPlanner:
                        Intent iPlanner = new Intent(getApplicationContext(), PlannerActivity.class);
                        startActivity(iPlanner);
                        overridePendingTransition(0, 0);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
}