package com.example.zooseekerteam24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private BottomNavigationView btmNavi;
    private AutoCompleteTextView searchBar;

    private List<ZooData.Node> exhibits;

//    private static final String[] COUNTRIES = new String[] {
//            "Belgium", "France", "Italy", "Germany", "Spain", "FryRepublic"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "sanity");
        btmNavi = findViewById(R.id.btmNavi);
        searchBar = findViewById(R.id.searchBar);

        exhibits = ZooData.loadExhibitsFromJSON(this, "sample_exhibits.json");
//        indexedExhibits.forEach((id, node) -> Log.d(TAG, node.toString()));


        SearchResultAdapter adapter = new SearchResultAdapter(this, exhibits);
//        ArrayAdapter<Node> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, indexedExhibits);
        searchBar.setAdapter(adapter);


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