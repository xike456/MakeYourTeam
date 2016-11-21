package com.smile.makeyourteam.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smile.makeyourteam.Models.Task;
import com.smile.makeyourteam.R;

import java.util.List;

/**
 * Created by mp_ng on 11/20/2016.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private Context mContext;
    private List<Task> tasks;

    public TaskAdapter(Context mContext, List<Task> tasks) {
        this.mContext = mContext;
        this.tasks = tasks;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.txtTitle.setText(task.title);
        holder.txtRemaining.setText("Remaining " + task.remaining + " hours");
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTitle,  txtRemaining;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.txtTaskName);
            txtRemaining = (TextView) itemView.findViewById(R.id.txtTaskRemaining);

            txtTitle.setOnClickListener(this);
            txtRemaining.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, "Test", Toast.LENGTH_SHORT).show();
        }
    }
}
