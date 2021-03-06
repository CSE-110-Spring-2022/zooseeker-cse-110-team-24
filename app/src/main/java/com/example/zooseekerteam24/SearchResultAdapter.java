package com.example.zooseekerteam24;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SearchResultAdapter
 * tell Android how to populate search result
 * and add exhibits from search results to planner
 */
public class SearchResultAdapter extends ArrayAdapter<ZooData.Node> {

    Context context;
    List<ZooData.Node> allExhibits;


    private Consumer<ZooData.Node> onAddBtnClicked;

    public void setOnAddBtnClickedHandler(Consumer<ZooData.Node> onAddBtnClicked){
        this.onAddBtnClicked = onAddBtnClicked;
    }


    public void populateSearch(List<ZooData.Node> nodes){
        this.allExhibits.clear();
        this.allExhibits.addAll(nodes);
        allExhibits.forEach(n -> {
            Log.d("populateSearch", n.toString());
        });
        notifyDataSetChanged();
    }

    public SearchResultAdapter(@NonNull Context context, @NonNull List<ZooData.Node> exhibits) {
        super(context, 0, exhibits);
        this.context = context;
        this.allExhibits = new ArrayList<>(exhibits);
    }



    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.search_result_item, parent, false);
        }

        ZooData.Node exhibit = getItem(position);

        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvAdded = itemView.findViewById(R.id.tvAdded);
        tvName.setText(exhibit.name);

        if (exhibit.added){
            tvAdded.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            tvAdded.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));

        }

        // Add/Unadd clicked exhibit to planner
        tvAdded.setOnClickListener(v->{
            Log.d("to plan", exhibit.name);
            if (onAddBtnClicked==null) return;
            onAddBtnClicked.accept(exhibit);
            notifyDataSetChanged();
        });



        return itemView;
    }

    private Filter filter = new Filter(){

        // logic of filtering results
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ZooData.Node> filteredExhibits = Collections.emptyList();

            if (constraint != null){
                String filterPattern = constraint.toString().toLowerCase().trim();
                filteredExhibits = allExhibits.stream()
                        .filter(e-> e.name.toLowerCase().contains(filterPattern)|| e.tags.stream().anyMatch(tag -> tag.contains(filterPattern)))
                        .collect(Collectors.toList());

            }
            results.values = filteredExhibits;
            results.count = filteredExhibits.size();

            return results;
        }

        // notify filtered result on UI
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List<ZooData.Node>)results.values);
            notifyDataSetChanged();
        }

        // tell adapter what to put in the input box when you click a suggestion
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ZooData.Node)resultValue).name;
        }
    };
}
