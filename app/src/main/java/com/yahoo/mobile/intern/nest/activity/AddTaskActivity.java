package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
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
    private String mDateTime;
    LatLng mLocation;
    String mAddress;
    @Bind(R.id.edt_task_title) EditText edtTaskTitle;
    @Bind(R.id.edt_task_content) EditText edtTaskContent;
    @Bind(R.id.btn_set_location) LinearLayout btnSetLocation;
    @Bind(R.id.btn_set_date) LinearLayout btnSetDate;
    @Bind(R.id.btn_set_expiretime) LinearLayout btnSetExpTime;
    @Bind(R.id.edt_time) EditText edtTaskTime;
    @Bind(R.id.text_expiretime) TextView textViewExpireTime;
    @Bind(R.id.text_location) TextView textViewLocation;

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


    }

    @OnClick(R.id.btn_set_expiretime) void setExpireTimeBtnSetLocation() {
        final Calendar c = Calendar.getInstance();
        final String datetime;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute  = c.get(Calendar.MINUTE);

        final TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                        mDateTime = mDateTime +" "+ String.format("%02d:%02d",hourOfDay,minute);
                        mHour = hourOfDay;
                        mMinute = minute;
                        textViewExpireTime.setText(mDateTime);
                    }
                },mHour, mMinute, false);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int
                            dayOfMonth) {
                        mDateTime = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                        mDay = dayOfMonth;
                        mMonth = monthOfYear;
                        mYear = year;
                        tpd.show(getFragmentManager(), "Timepickerdialog" );
                    }
                }, mYear, mMonth, mDay);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.REQUEST_LOCATION) {
            if(resultCode == RESULT_OK) {
                LatLng position = data.getParcelableExtra(Common.EXTRA_LOCATION);
                mLocation = position;

                String address = data.getStringExtra(Common.EXTRA_ADDRESS);
                mAddress = address;
                textViewLocation.setText(address);
            }
        }
    }

    private void addTask() {
        String title = edtTaskTitle.getText().toString();
        String content = edtTaskContent.getText().toString();
        String time = edtTaskTime.getText().toString();

        if (checkContent()){
            Date date = new Date(mYear-1900, mMonth, mDay, mHour, mMinute);//minus 1900 because of deprecate "date" usageg

            final ParseObject task = new ParseObject(Common.OBJECT_QUESTION);
            task.put(Common.OBJECT_QUESTION_USER, ParseUser.getCurrentUser());
            task.put(Common.OBJECT_QUESTION_TITLE, title);
            task.put(Common.OBJECT_QUESTION_CONTENT, content);
            task.put(Common.OBJECT_QUESTION_PIN, new ParseGeoPoint(mLocation.latitude, mLocation.longitude));
            task.put(Common.OBJECT_QUESTION_TIME, time);
            task.put(Common.OBJECT_QUESTION_EXPIRE_DATE, date);
            task.put(Common.OBJECT_QUESTION_ADDRESS, mAddress);

            task.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseRelation<ParseObject> myNewQuestions = user.getRelation(Common.OBJECT_USER_MY_NEW_QUESTIONS);
                        myNewQuestions.add(task);
                        user.saveInBackground();
                    }
                }
            });
            finish();
        }
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

    private boolean checkContent(){
        String title = edtTaskTitle.getText().toString();
        String content = edtTaskContent.getText().toString();
        String time = edtTaskTime.getText().toString();
        String expiretime = textViewExpireTime.getText().toString();

        if (title.isEmpty()){
            showToast("標題不能空白");
        } else if(content.isEmpty()){
            showToast("內容不能空白");
        } else if (mLocation == null){
            showToast("請選地點");
        } else if(time.isEmpty()){
            showToast("請敘述任務時段");
        } else if(expiretime.isEmpty()){
            showToast("請選任務截止時間");
        } else {
            return true;
        }
        return false;
    }

    private void showToast(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
