package com.example.zooseekerteam24;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.ViewHolder> {

//    private Context context;
    private List<ZooData.Node> exhibits = new ArrayList<ZooData.Node>();
    private Consumer<ZooData.Node> onDeleteBtnClicked;
//    private OnDeleteListener onDeleteListener;

//    public interface OnDeleteListener{
//        public void performOnDelete(int position);
//    }

//    public PlannerAdapter(Context context) {
////        this.exhibits = exhibits;
//        this.context = context;
//        this.onDeleteListener = onDeleteListener;
//    }

    public void setOnDeleteBtnClickedHandler(Consumer<ZooData.Node> onDeleteBtnClicked){
        this.onDeleteBtnClicked = onDeleteBtnClicked;
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

//    public PlannerAdapter(@NonNull Context context, ) {
//        super(context);
//    }

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

        // TODO: Set data and methods to all subViews of the row
        public void setDataAndMethods(int position){
            ZooData.Node exhibit = exhibits.get(position);
            tvName.setText(exhibit.name);
            tvDist.setText("100ft");
            // TODO: delete
            tvDelete.setOnClickListener( v -> {
                if (onDeleteBtnClicked == null) return;
                onDeleteBtnClicked.accept(exhibit);
                notifyDataSetChanged();
            });
        }

    }



}
