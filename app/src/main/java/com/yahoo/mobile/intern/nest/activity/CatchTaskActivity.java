package com.yahoo.mobile.intern.nest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CatchTaskActivity extends AppCompatActivity{

    private String taskId;
    private GoogleMap mMap;

    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.txt_people) TextView txtAcceptedUser;
    @Bind(R.id.btn_accept_task) Button btnAcceptTask;
    @Bind(R.id.txt_msg_accepted) TextView txtMsgAccepted;


    private void acceptTask(ParseObject task) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> catchRelation = user.getRelation(Common.OBJECT_USER_CATCH_QUESTIONS);
        ParseRelation<ParseObject> acceptedRelation = user.getRelation(Common.OBJECT_USER_ACCEPTED_QUESTIONS);
        catchRelation.remove(task);
        acceptedRelation.add(task);
        user.saveInBackground();

        ParseRelation<ParseUser> acceptedUser = task.getRelation(Common.OBJECT_QUESTION_ACCEPTED_USER);
        acceptedUser.add(user);
        task.saveInBackground();
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject task, ParseException e) {
                String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                ParseGeoPoint geoPoint = (ParseGeoPoint) task.get(Common.OBJECT_QUESTION_LOCATION);

                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                txtTitle.setText(title);
                txtContent.setText(content);

                ParseRelation<ParseUser> acceptedUser = task.getRelation(Common.OBJECT_QUESTION_ACCEPTED_USER);
                acceptedUser.getQuery().countInBackground(new CountCallback() {
                    @Override
                    public void done(int count, ParseException e) {
                        txtAcceptedUser.setText(Integer.toString(count)+"人也接受");
                    }
                });


                btnAcceptTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptTask(task);
                        btnAcceptTask.setVisibility(View.GONE);
                        txtMsgAccepted.setVisibility(View.VISIBLE);
                    }
                });
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
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_task_location))
                    .getMap();
            // Check if we were successful in obtaining the map.

        }

    }

}
