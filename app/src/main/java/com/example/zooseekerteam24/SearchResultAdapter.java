package com.example.zooseekerteam24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultAdapter extends ArrayAdapter<ZooData.Node> {

    Context context;
    List<ZooData.Node> allExhibits;

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
                    .inflate(R.layout.node_item, parent, false);
        }

        ZooData.Node exhibit = getItem(position);

        TextView tvName = itemView.findViewById(R.id.tvName);
        tvName.setText(exhibit.name);
        tvName.setOnClickListener(view -> {
            Toast.makeText(context, ((TextView)view).getText() + " added", Toast.LENGTH_SHORT).show();;
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
                        .filter(e-> e.tags.stream().anyMatch(tag -> tag.startsWith(filterPattern)))
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
