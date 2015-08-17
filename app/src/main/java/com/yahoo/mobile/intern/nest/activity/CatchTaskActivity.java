package com.yahoo.mobile.intern.nest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CatchTaskActivity extends AppCompatActivity {

    private String taskId;

    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.btn_accept_task) Button btnAcceptTask;
    @Bind(R.id.txt_msg_accepted) TextView txtMsgAccepted;


    private boolean isTaskAccepted(ParseObject task) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_ACCEPTED_TASKS);
        query.whereEqualTo(Common.OBJECT_ACCEPTED_TASKS_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Common.OBJECT_ACCEPTED_TASKS_TASK, task);
        try {
            return query.count() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject task, ParseException e) {
                String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                txtTitle.setText(title);
                txtContent.setText(content);

                if(isTaskAccepted(task)) {
                    txtMsgAccepted.setVisibility(View.VISIBLE);
                }
                else {
                    btnAcceptTask.setVisibility(View.VISIBLE);
                    btnAcceptTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParseObject acceptedTasks = new ParseObject(Common.OBJECT_ACCEPTED_TASKS);
                            acceptedTasks.put(Common.OBJECT_ACCEPTED_TASKS_USER, ParseUser.getCurrentUser());
                            acceptedTasks.put(Common.OBJECT_ACCEPTED_TASKS_TASK, task);
                            acceptedTasks.saveInBackground();
                            btnAcceptTask.setVisibility(View.GONE);
                            txtMsgAccepted.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_task);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);
        setupTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catch_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}