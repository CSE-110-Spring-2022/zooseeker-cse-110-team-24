package com.example.zooseekerteam24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SearchActivity extends AppCompatActivity {

    public static final String KEY_EXHIBIT = "KEY_EXHIBIT";
    private static final String TAG = "SearchActivity";
    private BottomNavigationView btmNavi;
//    private AutoCompleteTextView searchBar;
    private SearchView searchView;
    private List<ZooData.Node> exhibits = new ArrayList<>();
    private ListView lvResults;
    private SearchResultAdapter adapter;
    PlannerViewModel plannerViewModel;


//    SearchResultAdapter.OnAddListener onAddListener = new SearchResultAdapter.OnAddListener() {
//        @Override
//        public void performOnAdd(int position) {
//            ZooData.Node exhibitToAdd = exhibits.get(position);
//            exhibitToAdd.added = true;
//            adapter.notifyDataSetChanged();
//            Log.d("testadd", "ready to add: " + exhibitToAdd.name);
////            Intent i = new Intent(getApplicationContext(), PlannerActivity.class);
////            i.putExtra(KEY_EXHIBIT, exhibits.get(position));
////            startActivity(i);
//        }
//    };
//    private static final String[] COUNTRIES = new String[] {
//            "Belgium", "France", "Italy", "Germany", "Spain", "FryRepublic"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "sanity");
        btmNavi = findViewById(R.id.btmNavi);
        searchView = findViewById(R.id.searchView);
        lvResults = findViewById(R.id.lvResults);

//        exhibits = ZooData.loadExhibitsFromJSON(this, "sample_node_info.json");
//        exhibits.forEach(node -> Log.d(TAG, node.toString()));


        adapter = new SearchResultAdapter(this, exhibits);
//        ArrayAdapter<Node> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, indexedExhibits);
        plannerViewModel = new ViewModelProvider(this)
                .get(PlannerViewModel.class);

        adapter.setOnAddBtnClickedHandler(plannerViewModel::addExhibit);
        plannerViewModel.getNodes().observe(this, adapter::populateSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (searchView.getQuery()==""){
                    lvResults.setVisibility(View.INVISIBLE);
                }
                // TODO: if exhibits.contains
                Log.d("onQueryTextSubmit", query);
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("onQueryTextChange", newText);
                // TODO: blank when not searching
                // TODO: suggestion
                // TODO: search add not working
                if (lvResults.getVisibility()== View.INVISIBLE){
                    lvResults.setVisibility(View.VISIBLE);
                }

                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }


        });

//        searchView.oncl
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                lvResults.setVisibility(View.INVISIBLE);
//                return true;
//            }
//        });

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