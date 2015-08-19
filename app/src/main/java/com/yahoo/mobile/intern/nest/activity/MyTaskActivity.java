package com.yahoo.mobile.intern.nest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.AcceptedUserAdapter;
import com.yahoo.mobile.intern.nest.event.AcceptedUserEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MyTaskActivity extends AppCompatActivity {

    private String taskId;
    private ParseObject mTask;

    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.list_view_accepted_seller) ListView mListView;
    @Bind(R.id.btn_deal) Button btnDeal;
    @Bind(R.id.select_seller) LinearLayout selectSeller;
    @Bind(R.id.btn_confirm) Button btnConfirm;
    @Bind(R.id.btn_cancel) Button btnCancel;

    private AcceptedUserAdapter mAdapter;
    private List<ParseUser> mList;


    @OnClick(R.id.btn_deal) void dealOnClick() {
        mAdapter.setSelectable(true);
        btnDeal.setVisibility(View.GONE);
        selectSeller.setVisibility(View.VISIBLE);
    }
    @OnClick(R.id.btn_cancel) void cancelOnClick() {
        mAdapter.setSelectable(false);
        btnDeal.setVisibility(View.VISIBLE);
        selectSeller.setVisibility(View.GONE);
    }
    @OnClick(R.id.btn_confirm) void confirmOnClick() {
        ParseUser seller = mAdapter.getCheckedUser();
        if(seller != null) {
            ParseUtils.doneTask(mTask, ParseUser.getCurrentUser(), seller);
        }
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject task, ParseException e) {
                if(e == null) {
                    mTask = task;
                    String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                    String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                    txtTitle.setText(title);
                    txtContent.setText(content);
                    setupAcceptedSellers();
                    ParseUtils.getTaskAcceptedUser(task);
                }
            }
        });
    }

    private void setupAcceptedSellers() {
        mList = new ArrayList<>();
        mAdapter = new AcceptedUserAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(AcceptedUserEvent event) {
        mList.addAll(event.userList);
        mAdapter.receivedAcceptedUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task);
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
