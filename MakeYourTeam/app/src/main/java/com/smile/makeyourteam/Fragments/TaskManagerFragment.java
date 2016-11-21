package com.smile.makeyourteam.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.Toast;

import com.smile.makeyourteam.Adapters.TaskAdapter;
import com.smile.makeyourteam.Models.Task;
import com.smile.makeyourteam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskManagerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private FloatingActionButton btnAddTask;

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
        adapter = new TaskAdapter(getContext(), taskList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        loadTasks();

        btnAddTask.setOnClickListener(this);
    }

    private void loadTasks() {

        taskList.add(new Task("" ,"Task 1: Design UI", "", 2, 3, 3));
        taskList.add(new Task("" ,"Task 2: Implement code", "", 2, 3, 3));
        taskList.add(new Task("" ,"Task 3: QC", "", 2, 2, 3));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view == btnAddTask) {
            Toast.makeText(getContext(), "Add new task", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_task_manager, menu);

        MenuItem item = menu.findItem(R.id.menu_task);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_menu_task, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            Toast.makeText(getContext(), "New", Toast.LENGTH_SHORT).show();
        } else if (i == 1) {
            Toast.makeText(getContext(), "Active", Toast.LENGTH_SHORT).show();
        } else if (i == 2) {
            Toast.makeText(getContext(), "Close", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
