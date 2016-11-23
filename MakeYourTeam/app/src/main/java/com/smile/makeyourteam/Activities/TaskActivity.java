package com.smile.makeyourteam.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Task;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner spinnerAssign;
    private Spinner spinnerState;
    private SpinnerAdapter assignAdapter;
    private ArrayList<String> memberList;
    private Button btnCancel;
    private Button btnSave;
    private EditText editTaskTitle;
    private EditText editEstimateTime;
    private EditText editRemainingTime;
    private EditText editCompletedTime;
    private List<User> userList = new ArrayList<>();
    private String assignTo = "";
    private String assignToId = "";
    private String taskId = "";
    private String state = "";
    private SpinnerAdapter stateAdapter;
    private List<String> stateList = new ArrayList<>();
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        task = intent.getParcelableExtra(Config.TASK_OBJECT);

        spinnerAssign = (Spinner) findViewById(R.id.spinnerAssignTo);
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        btnCancel = (Button) findViewById(R.id.btnCancelTask);
        btnSave = (Button) findViewById(R.id.btnSaveTask);
        editTaskTitle = (EditText) findViewById(R.id.editTaskTitle) ;
        editEstimateTime = (EditText) findViewById(R.id.editEstimateTime) ;
        editRemainingTime = (EditText) findViewById(R.id.editRemainingTime) ;
        editCompletedTime = (EditText) findViewById(R.id.editCompletedTime) ;


        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        memberList = new ArrayList<String>();


        // Creating adapter for spinner
        assignAdapter = new SpinnerAdapter(this, memberList);
        stateAdapter = new SpinnerAdapter(this, stateList);

        // attaching data adapter to spinner
        spinnerAssign.setAdapter(assignAdapter);
        spinnerState.setAdapter(stateAdapter);
        spinnerAssign.setOnItemSelectedListener(this);
        spinnerState.setOnItemSelectedListener(this);
        LoadMemberToAssignList();
        LoadState();
    }

    private void LoadState() {
        stateList.add("New");
        stateList.add("Active");
        stateList.add("Close");
        spinnerState.setSelection(0);
        stateAdapter.notifyDataSetChanged();
    }

    void LoadMemberToAssignList(){
        DatabaseReference database = Firebase.database.getReference("users");
        Query myTopPostsQuery = database.orderByChild("teamId").startAt(MainActivity.teamId).endAt(MainActivity.teamId);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                memberList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    userList.add(user);
                    memberList.add(getDisplayName(user));
                }

                if (!memberList.isEmpty()) {
                    spinnerAssign.setSelection(0);
                    loadTaskToUI();
                }

                assignAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadTaskToUI() {
        if (task != null) {
            getSupportActionBar().setTitle("Edit task");
            taskId = task.id;
            editTaskTitle.setText(task.title);
            editEstimateTime.setText( String.valueOf(task.estimate));
            editRemainingTime.setText(String.valueOf(task.remaining));
            editCompletedTime.setText(String.valueOf(task.completed));
            spinnerState.setSelection(stateList.indexOf(task.state));
            spinnerAssign.setSelection(memberList.indexOf(task.assignTo));
        } else {
            getSupportActionBar().setTitle("Create new task");
            taskId = "";
            editTaskTitle.requestFocus();
        }
    }

    private String getDisplayName(User user) {
        if (!user.displayName.isEmpty()) {
            return user.displayName;
        }
        if (!user.nickName.isEmpty()) {
            return user.nickName;
        }
        return user.email;
    }

    @Override
    public void onClick(View view) {
        if (view == btnCancel) {
            this.finish();
        } else if (view == btnSave) {
            saveTask();
        }
    }

    private void saveTask() {
        String taskName = String.valueOf(editTaskTitle.getText());
        if (taskName.isEmpty()) {
            editTaskTitle.setError("Please input task");
            return;
        }

        String estimateTime = String.valueOf(editEstimateTime.getText());
        if (estimateTime.isEmpty()) {
            editEstimateTime.setError("Please input estimate");
            return;
        }

        String remainingTime = String.valueOf(editRemainingTime.getText());
        if (remainingTime.isEmpty()) {
            editRemainingTime.setError("Please input remaining");
            return;
        }

        String completedTime = String.valueOf(editCompletedTime.getText());
        if (completedTime.isEmpty()) {
            editCompletedTime.setError("Please input remaining");
            return;
        }

        if (assignTo.isEmpty() || assignToId.isEmpty()) {
            Toast.makeText(this, "Please choose member", Toast.LENGTH_SHORT).show();
            return;
        }

        if (state.isEmpty()) {
            Toast.makeText(this, "Please choose state", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mDatabase = Firebase.database.getReference()
                .child("teams").child(MainActivity.teamId).child("tasks");

        if (taskId == null || taskId.isEmpty()) {
            taskId = mDatabase.push().getKey();
        }

        Task newTask = new Task(taskId, taskName, assignTo, assignToId, state,
                Integer.valueOf(estimateTime), Integer.valueOf(remainingTime), Integer.valueOf(completedTime));
        mDatabase.child(taskId).setValue(newTask);
        this.finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == spinnerAssign) {
            assignTo = adapterView.getAdapter().getItem(i).toString();
            assignToId = userList.get(i).id;
        } else if (adapterView == spinnerState) {
            state = adapterView.getAdapter().getItem(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class SpinnerAdapter extends BaseAdapter {
        Context context;
        List<String> list;
        LayoutInflater inflater;

        public SpinnerAdapter(Context applicationContext, List<String> members) {
            this.context = applicationContext;
            this.list = members;
            inflater = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.spinner_task_item, null);
            TextView name = (TextView) view.findViewById(R.id.txtSpinnerText);
            name.setText(list.get(i));
            return view;
        }
    }
}
