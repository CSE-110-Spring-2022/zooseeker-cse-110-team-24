/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * File   : PlannerAdapter.java
 * Authors: Yiran Wan, Rahul Puranam
 * Desc   : An Adapter that tells android how to create and manage each item in the view
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.zooseekerteam24;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.ViewHolder> {

    private List<ZooData.Node> exhibits = new ArrayList<ZooData.Node>();
    private Consumer<ZooData.Node> onDeleteBtnClicked;
    private Consumer<RouteGenerator> onOrderCalled;
    private Consumer<Integer> onCountCalled;
    private RouteGenerator generator;

    public void setRouteGenerator(RouteGenerator generator){
        this.generator = generator;
    }

    public void setOnCountCalled(Consumer<Integer> onCountCalled) {
        this.onCountCalled = onCountCalled;
    }

    public void setOnDeleteBtnClickedHandler(Consumer<ZooData.Node> onDeleteBtnClicked){
        this.onDeleteBtnClicked = onDeleteBtnClicked;
    }

    public void setOnOrderCalledHandler(Consumer<RouteGenerator> onOrderCalled){
        this.onOrderCalled = onOrderCalled;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.planner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDataAndMethods(position);
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    public void populatePlanner(List<ZooData.Node> exhibits){
        this.exhibits.clear();
        this.exhibits.addAll(exhibits);
        exhibits.forEach(n -> {
            Log.d("populatePlanner", n.toString());
        });
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return exhibits.get(position).rtId;
    }

    // Hold one row of a planner
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvDelete;
        TextView tvDist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            tvDist = itemView.findViewById(R.id.tvDist);

        }

        // Set data and methods to all subViews of the row
        public void setDataAndMethods(int position){
            ZooData.Node exhibit = exhibits.get(position);
            tvName.setText(exhibit.name);
            tvDist.setText(String.valueOf(exhibit.cumDistance));

            // When user delete an exhibit from planner, distance attached to each exhibit
            // is recalculated and reordered, and counter updates accordingly
            tvDelete.setOnClickListener( v -> {
                if (onDeleteBtnClicked == null || onOrderCalled == null) return;
                onDeleteBtnClicked.accept(exhibit);
                onOrderCalled.accept(generator);
                onCountCalled.accept(exhibits.size()-1);
                notifyDataSetChanged();
            });
        }
    }



}
