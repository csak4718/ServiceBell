package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTaskActivity extends AppCompatActivity {

    @Bind(R.id.edt_task_title) EditText edtTaskTitle;
    @Bind(R.id.edt_task_content) EditText edtTaskContent;
    @Bind(R.id.btn_set_location) Button btnSetLocation;
    @Bind(R.id.txt_lat) TextView txtLat;
    @Bind(R.id.txt_lng) TextView txtLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);

//        btnSetLocation
    }

    @OnClick(R.id.btn_set_location) void setBtnSetLocation() {
        Utils.gotoMapsActivityForResult(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.REQUEST_LOCATION) {
            if(resultCode == RESULT_OK) {
                LatLng position = data.getParcelableExtra(Common.EXTRA_LOCATION);
                String lat = Double.toString(position.latitude);
                String lng = Double.toString(position.longitude);
                txtLat.setText(lat);
                txtLng.setText(lng);
            }
        }
    }

    private void addTask() {
        String title = edtTaskTitle.getText().toString();
        String content = edtTaskContent.getText().toString();
        Double lat = Double.valueOf(txtLat.getText().toString());
        Double lng = Double.valueOf(txtLng.getText().toString());

        ParseObject task = new ParseObject(Common.OBJECT_QUESTION);
        task.put(Common.OBJECT_QUESTION_USER, ParseUser.getCurrentUser());
        task.put(Common.OBJECT_QUESTION_TITLE, title);
        task.put(Common.OBJECT_QUESTION_CONTENT, content);
        task.put(Common.OBJECT_QUESTION_PIN, new ParseGeoPoint(lat, lng));

        task.saveInBackground();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.action_post) {
            addTask();
        }

        return super.onOptionsItemSelected(item);
    }
}
