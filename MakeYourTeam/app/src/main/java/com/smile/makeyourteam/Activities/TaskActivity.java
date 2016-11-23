package com.smile.makeyourteam.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.smile.makeyourteam.R;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private Spinner spinner;
    private AssignAdapter assignAdapter;
    private ArrayList<String> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        spinner = (Spinner) findViewById(R.id.spinnerAssignTo);

        memberList = new ArrayList<String>();


        // Creating adapter for spinner
        assignAdapter = new AssignAdapter(this, memberList);

        // attaching data adapter to spinner
        spinner.setAdapter(assignAdapter);
        LoadMemberToAssignList();
    }

    private void LoadMemberToAssignList() {
        memberList.add("Automobile");
        memberList.add("Business Services");
        memberList.add("Computers");
        memberList.add("Education");
        memberList.add("Personal");
        memberList.add("Travel");

        assignAdapter.notifyDataSetChanged();
    }

    public class AssignAdapter extends BaseAdapter {
        Context context;
        List<String> members;
        LayoutInflater inflater;

        public AssignAdapter(Context applicationContext, List<String> members) {
            this.context = applicationContext;
            this.members = members;
            inflater = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return members.size();
        }

        @Override
        public Object getItem(int i) {
            return members.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.spinner_task_item, null);
            TextView name = (TextView) view.findViewById(R.id.txtMemberName);
            name.setText(members.get(i));
            return view;
        }
    }
}
