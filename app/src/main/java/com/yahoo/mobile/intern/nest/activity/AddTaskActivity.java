package com.yahoo.mobile.intern.nest.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTaskActivity extends AppCompatActivity {
    private int mYear, mMonth, mDay;
    private int mHour, mMinute;
    @Bind(R.id.edt_task_title) EditText edtTaskTitle;
    @Bind(R.id.edt_task_content) EditText edtTaskContent;
    @Bind(R.id.btn_set_location) Button btnSetLocation;
    @Bind(R.id.btn_set_date) Button btnSetDate;
    @Bind(R.id.btn_set_time) Button btnSetTime;
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

    @OnClick(R.id.btn_set_location) void setLocaionBtnSetLocation() {
        Utils.gotoMapsActivityForResult(this);
    }

    @OnClick(R.id.btn_set_date) void setDateBtnSetLocation() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // 完成選擇，顯示日期
                        btnSetDate.setText(year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    @OnClick(R.id.btn_set_time) void setTimeBtnSetLocation() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        btnSetTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        tpd.show();
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
        Date myDgitate = new Date(mYear, mMonth, mDay, mHour, mHour, mMinute);


        ParseObject task = new ParseObject(Common.OBJECT_QUESTION);
        task.put(Common.OBJECT_QUESTION_USER, ParseUser.getCurrentUser());
        task.put(Common.OBJECT_QUESTION_TITLE, title);
        task.put(Common.OBJECT_QUESTION_CONTENT, content);
        task.put(Common.OBJECT_QUESTION_PIN, new ParseGeoPoint(lat, lng));
        task.put(Common.OBJECT_QUESTION_DATE, myDate);

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
