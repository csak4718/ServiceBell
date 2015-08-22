package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddChooseCategory;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddLocationDate;
import com.yahoo.mobile.intern.nest.utils.Common;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    LatLng mLocation;
//    @Bind(R.id.edt_task_title) EditText edtTaskTitle;
//    @Bind(R.id.edt_task_content) EditText edtTaskContent;
//    @Bind(R.id.btn_set_location) LinearLayout btnSetLocation;
//    @Bind(R.id.btn_set_date) LinearLayout btnSetDate;
//    @Bind(R.id.btn_set_expiretime) LinearLayout btnSetExpTime;
//    @Bind(R.id.edt_time) EditText edtTaskTime;
//    @Bind(R.id.text_expiretime) TextView textViewExpireTime;
//    @Bind(R.id.text_location) TextView textViewLocation;


    /*
       task info
     */
    public String title;
    public String content;
    public int mYear, mMonth, mDay;
    public int mHour, mMinute;
    public Date expire;
    public String time;
    public String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增服務");

        setContentView(R.layout.activity_add_task);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, new FragmentAddChooseCategory())
                .commit();
//        ButterKnife.bind(this);

//        btnSetLocation
    }


//
//    @OnClick(R.id.btn_set_date) void setDateBtnSetLocation() {
//
//
//    }


//
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.REQUEST_LOCATION) {
            if(resultCode == RESULT_OK) {
                LatLng position = data.getParcelableExtra(Common.EXTRA_LOCATION);
                mLocation = position;
                address = data.getStringExtra(Common.EXTRA_ADDRESS);

                FragmentAddLocationDate fragment = (FragmentAddLocationDate)
                        getSupportFragmentManager().findFragmentById(R.id.frame_content);
                fragment.txtLocationHolder.setText(address);
            }
        }
    }

    public void addTask() {

        Date date = new Date(mYear-1900, mMonth, mDay, mHour, mMinute);//minus 1900 because of deprecate "date" usageg

        final ParseObject task = new ParseObject(Common.OBJECT_QUESTION);
        task.put(Common.OBJECT_QUESTION_USER, ParseUser.getCurrentUser());
        task.put(Common.OBJECT_QUESTION_TITLE, title);
        task.put(Common.OBJECT_QUESTION_CONTENT, content);
        task.put(Common.OBJECT_QUESTION_PIN, new ParseGeoPoint(mLocation.latitude, mLocation.longitude));
        task.put(Common.OBJECT_QUESTION_TIME, time);
        task.put(Common.OBJECT_QUESTION_EXPIRE_DATE, date);
        task.put(Common.OBJECT_QUESTION_ADDRESS, address);

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
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_task, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if(id == android.R.id.home) {
//            finish();
//        }
//        else if(id == R.id.action_post) {
//            addTask();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private boolean checkContent(){
//        String title = edtTaskTitle.getText().toString();
//        String content = edtTaskContent.getText().toString();
//        String time = edtTaskTime.getText().toString();
//        String expiretime = textViewExpireTime.getText().toString();
//
//        if (title.isEmpty()){
//            showToast("標題不能空白");
//        } else if(content.isEmpty()){
//            showToast("內容不能空白");
//        } else if (mLocation == null){
//            showToast("請選地點");
//        } else if(time.isEmpty()){
//            showToast("請敘述任務時段");
//        } else if(expiretime.isEmpty()){
//            showToast("請選任務截止時間");
//        } else {
//            return true;
//        }
//        return false;
//    }
//
//    private void showToast(String str){
//        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
//    }
}
