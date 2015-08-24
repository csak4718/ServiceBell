package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.AcceptedUserAdapter;
import com.yahoo.mobile.intern.nest.event.AcceptedUserEvent;
import com.yahoo.mobile.intern.nest.dialog.DialogFragmentSellerProfile;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;
import com.yahoo.mobile.intern.nest.view.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MyTaskActivity extends AppCompatActivity implements DialogFragmentSellerProfile.ProfileDialogListener {

    private String taskId;
    private ParseObject mTask;
    private ParseGeoPoint mGeoPoint;

    @Bind(R.id.txt_location) TextView txtAddress;
    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.list_view_accepted_seller)ExpandableHeightListView mListView;
    @Bind(R.id.txt_task_time) TextView txtTaskTime;
    @Bind(R.id.txt_remaining) TextView txtRemaining;
    @Bind(R.id.txt_status) TextView txtStatus;

    @OnClick(R.id.img_addres) void viewMap(){
        Utils.gotoMapsActivityCurLocation(this, new LatLng(mGeoPoint.getLatitude(), mGeoPoint.getLongitude()));
    }

    private AcceptedUserAdapter mAdapter;
    private List<ParseUser> mList;

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject task, ParseException e) {
                if(e == null) {
                    mTask = task;

                    String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                    String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                    String time = task.getString(Common.OBJECT_QUESTION_TIME);
                    txtTitle.setText(title);
                    txtContent.setText(content);
                    txtTaskTime.setText(time);

                    Date expire = task.getDate(Common.OBJECT_QUESTION_EXPIRE_DATE);
                    Date current = new Date();
                    txtRemaining.setText(Utils.getRemainingTime(current, expire));

                    txtAddress.setText(task.getString(Common.OBJECT_QUESTION_ADDRESS));
                    mGeoPoint = task.getParseGeoPoint(Common.OBJECT_QUESTION_PIN);


                    setupAcceptedSellers();
                    // task is not done
                    if(task.getParseUser(Common.OBJECT_QUESTION_DONE_USER) == null) {
                        txtStatus.setText("等待中");
                        ParseUtils.getTaskAcceptedUser(task);
                    }
                    else {
                        txtStatus.setText("已成交");
                        ParseUser seller = task.getParseUser(Common.OBJECT_QUESTION_DONE_USER);
                        try {
                            seller = seller.fetch();
                            mList.add(seller);
                            mAdapter.notifyDataSetChanged();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
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
                DialogFragmentSellerProfile dfsp;
                if (mTask.getParseUser(Common.OBJECT_QUESTION_DONE_USER) != null){
                    dfsp = DialogFragmentSellerProfile.newInstance(mList.get(position),true,false);
                }else{
                    dfsp = DialogFragmentSellerProfile.newInstance(mList.get(position),false,false);
                }
                dfsp.show(getSupportFragmentManager(),"Profile");
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_task);
        ButterKnife.bind(this);
        mListView.setExpanded(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setBuyerColor(this);

        taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);

        setupTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            //closeActivity();
            finish();
        }
        if(id == R.id.action_delete) {
            mTask.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    Utils.showLoadingDialog(MyTaskActivity.this);
                    finish();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishProfileDialog(String inputText, ParseUser seller) {
        Log.d("asd",inputText);
        closeAuction(seller);
    }

    public void closeAuction(ParseUser seller){
        ParseUtils.doneTask(mTask, ParseUser.getCurrentUser(), seller);
        closeActivity();
    }

    public void closeActivity(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",true);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
