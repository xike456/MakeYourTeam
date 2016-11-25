package com.smile.makeyourteam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Activities.TaskActivity;
import com.smile.makeyourteam.Adapters.TaskAdapter;
import com.smile.makeyourteam.Models.Task;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskManagerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private List<Task> filterTaskList;
    private FloatingActionButton btnAddTask;
    private boolean isMyTask = false;
    private String currentState;

    public TaskManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_task_manager, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rcvTask);
        btnAddTask = (FloatingActionButton) getActivity().findViewById(R.id.btnAddTask);

        taskList = new ArrayList<>();
        filterTaskList = new ArrayList<>();
        adapter = new TaskAdapter(getContext(), filterTaskList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        btnAddTask.setOnClickListener(this);

        loadTasks();
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
                filterTask();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void filterTask() {
        filterTaskList.clear();
        for (Task task : taskList) {
            if (task.state.equals(currentState)) {
                if (isMyTask == true) {
                    if (task.assignToId.equals(Firebase.firebaseAuth.getCurrentUser().getUid())) {
                        filterTaskList.add(task);
                    }
                } else {
                    filterTaskList.add(task);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view == btnAddTask) {
            Intent intent = new Intent(getContext(), TaskActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_task_manager, menu);

        MenuItem item = menu.findItem(R.id.state_menu_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_menu_task, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSwitchTask:
                if (isMyTask) {
                    isMyTask = false;
                    item.setIcon(R.drawable.ic_people_white_24dp);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("All tasks");
                    item.setTitle("All tasks");
                } else {
                    isMyTask = true;
                    item.setIcon(R.drawable.ic_person_white_24dp);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My tasks");
                    item.setTitle("My tasks");
                }
                filterTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currentState = adapterView.getAdapter().getItem(i).toString();
        filterTask();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
