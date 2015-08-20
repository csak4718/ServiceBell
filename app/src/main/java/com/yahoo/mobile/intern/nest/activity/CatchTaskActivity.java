package com.yahoo.mobile.intern.nest.activity;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CatchTaskActivity extends BaseActivity implements SinchService.StartFailedListener {

    private ParseObject mTask;
    private String taskId;
    private GoogleMap mMap;
    private ParseUser buyer;
    private ProgressDialog mSpinner;

    @Bind(R.id.btn_toMessaging) Button btnToMessaging;
    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.txt_num_people_accepted) TextView txtAcceptedUser;
    @Bind(R.id.btn_accept_task) Button btnAcceptTask;
    @Bind(R.id.img_user_pic)CircleImageView imgUserPic;
    @Bind(R.id.txt_user_name) TextView txtUserName;
    @Bind(R.id.txt_task_date) TextView txtTaskDate;
    @Bind(R.id.txt_task_time) TextView txtTaskTime;
    @Bind(R.id.img_map) ImageView imgMap;

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

        /*
         Use cloud code to notify buyer
         */
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Common.CLOUD_NOTIFY_ACCEPT_BUYERID, mTask.getParseUser(Common.OBJECT_QUESTION_USER).getObjectId());
        ParseCloud.callFunctionInBackground(Common.CLOUD_NOTIFY_ACCEPT, params);
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject task, ParseException e) {
                mTask = task;
                buyer = (ParseUser)task.get(Common.OBJECT_QUESTION_USER);
                String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                ParseGeoPoint geoPoint = (ParseGeoPoint) task.get(Common.OBJECT_QUESTION_LOCATION);

                txtUserName.setText((String) buyer.get(Common.OBJECT_USER_FB_NAME));
                ParseFile imgFile = buyer.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                imgFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                                    bytes.length);
                            if (bmp != null) {
                                imgUserPic.setImageBitmap(bmp);
                            }
                        }
                    }
                });
                txtTaskDate.setText(task.getDate(Common.OBJECT_QUESTION_EXPIRE_DATE).toString());
                txtTaskTime.setText(task.getString(Common.OBJECT_QUESTION_TIME));
                
                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_community_pin))
                        .position(latLng));
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                if (imgMap == null)
                                    imgMap = (ImageView) findViewById(R.id.img_map);
                                imgMap.setImageBitmap(bitmap);

                                SupportMapFragment mapFragment =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_task_location));
                                getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                            }
                        });
                    }
                });

                txtTitle.setText(title);
                txtContent.setText(content);

                ParseRelation<ParseUser> acceptedUser = task.getRelation(Common.OBJECT_QUESTION_ACCEPTED_USER);
                acceptedUser.getQuery().countInBackground(new CountCallback() {
                    @Override
                    public void done(int count, ParseException e) {
                        txtAcceptedUser.setText("現在共有" + Integer.toString(count) + "人接受此任務");
                    }
                });


                btnAcceptTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptTask(task);
                        btnAcceptTask.setVisibility(View.GONE);
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
        btnToMessaging.setEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);
        setupTask();

        setUpMapIfNeeded();

        btnToMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToMessagingClicked();
            }
        });
    }


    @Override
    protected void onServiceConnected() {
        btnToMessaging.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }



    // implements SinchService.StartFailedListener functions
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        Utils.gotoMessagingActivity(CatchTaskActivity.this, buyer.getObjectId());
    }


    private void btnToMessagingClicked() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        String userName = currentUser.getObjectId();

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            Utils.gotoMessagingActivity(CatchTaskActivity.this, buyer.getObjectId());
        }
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
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
        }
    }

}
