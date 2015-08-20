package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.AcceptedUserAdapter;
import com.yahoo.mobile.intern.nest.event.AcceptedUserEvent;
import com.yahoo.mobile.intern.nest.fragment.DialogFragmentSellerProfile;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.view.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MyTaskActivity extends AppCompatActivity implements DialogFragmentSellerProfile.ProfileDialogListener {

    private String taskId;
    private ParseObject mTask;
    private GoogleMap mMap;

    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.list_view_accepted_seller)ExpandableHeightListView mListView;
    @Bind(R.id.btn_deal) Button btnDeal;
    @Bind(R.id.select_seller) LinearLayout selectSeller;
    @Bind(R.id.btn_confirm) Button btnConfirm;
    @Bind(R.id.btn_cancel) Button btnCancel;
    @Bind(R.id.txt_task_date) TextView txtTaskDate;
    @Bind(R.id.txt_task_time) TextView txtTaskTime;
    @Bind(R.id.img_map)ImageView imgMap;

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
            //ParseUtils.doneTask(mTask, ParseUser.getCurrentUser(), seller);
            closeAuction(seller);
        }
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject task, ParseException e) {
                if(e == null) {
                    mTask = task;
                    ParseGeoPoint geoPoint = (ParseGeoPoint) task.get(Common.OBJECT_QUESTION_LOCATION);
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
                    String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                    String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                    String time = task.getString(Common.OBJECT_QUESTION_TIME);
                    txtTitle.setText(title);
                    txtContent.setText(content);
                    txtTaskDate.setText(task.getDate(Common.OBJECT_QUESTION_EXPIRE_DATE).toString());
                    txtTaskTime.setText(time);
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
        mListView.setExpanded(true);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            //closeActivity();
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
