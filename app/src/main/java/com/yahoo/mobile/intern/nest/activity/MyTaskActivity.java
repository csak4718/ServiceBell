package com.yahoo.mobile.intern.nest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyTaskActivity extends AppCompatActivity {

    private String taskId;

    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;


    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject task, ParseException e) {
                String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                txtTitle.setText(title);
                txtContent.setText(content);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);
        setupTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
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