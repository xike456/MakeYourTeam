package com.smile.makeyourteam.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Models.Task;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    PieChart taskChart;
    private List<Task> taskList = new ArrayList<>();
    ArrayList<PieEntry> entries = new ArrayList<>();
    private List<User> uList = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    protected void generatePieData() {
        entries.clear();



        for (User user: uList) {
            float remainingTime = 0;
            for (Task task :taskList) {
                if (task.assignToId.equals(user.id) && (task.state.equals("Active") || (task.state.equals("New")))) {
                    remainingTime += task.remaining;
                }
            }
            if (remainingTime != 0) {
                entries.add(new PieEntry(remainingTime, getUserName(user)));
            }
        }

        int colors[] = new int[entries.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = getRandomColor();
        }

        PieDataSet dataSet = new PieDataSet(entries, "Task Remaining");

        dataSet.notifyDataSetChanged();
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(12f);
        taskChart.setData(new PieData(dataSet));
        taskChart.startLayoutAnimation();
        taskChart.invalidate();
    }

    public int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    void LoadUser(){
        DatabaseReference database = Firebase.database.getReference("users");
        Query myTopPostsQuery = database.orderByChild("teamId").startAt(MainActivity.currentUser.teamId).endAt(MainActivity.currentUser.teamId);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    uList.add(user);
                }
                loadTasks();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getUserName(User user){
        if(!user.displayName.isEmpty())
            return user.displayName;
        if(!user.nickName.isEmpty())
            return user.nickName;

        return user.email;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        taskChart = (PieChart) getActivity().findViewById(R.id.taskChart);
        taskChart.setCenterText("Time remaining (hours)");
        taskChart.setDescription(null);
        taskChart.animateX(800);
        taskChart.getLegend().setWordWrapEnabled(true);
        taskChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        taskChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);

        LoadUser();
    }

    public void loadTasks() {

        DatabaseReference mDatabase = Firebase.database.getReference()
                .child("teams").child(MainActivity.currentUser.teamId).child("tasks");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Task task = ds.getValue(Task.class);
                    taskList.add(task);
                }
                generatePieData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
