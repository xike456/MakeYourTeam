package com.smile.makeyourteam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (!MainActivity.teamId.isEmpty()) {
                        loadTasks();
                    } else {
                        loadTeamInfo();
                    }

                    Log.d("Authentication", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
            }
        };

        btnAddTask.setOnClickListener(this);
    }



    private void loadTeamInfo() {
        FirebaseUser currentUser = Firebase.firebaseAuth.getCurrentUser();
        DatabaseReference database = Firebase.database.getReference("users")
                .child(currentUser.getUid()).child("teamId");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String team = (String) dataSnapshot.getValue();
                MainActivity.teamId = team;
                loadTasks();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadTasks() {

        DatabaseReference mDatabase = Firebase.database.getReference()
                .child("teams").child(MainActivity.teamId).child("tasks");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Task task = ds.getValue(Task.class);
                    taskList.add(task);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            //Toast.makeText(getContext(), "New", Toast.LENGTH_SHORT).show();
        } else if (i == 1) {
            //Toast.makeText(getContext(), "Active", Toast.LENGTH_SHORT).show();
        } else if (i == 2) {
            //Toast.makeText(getContext(), "Close", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
