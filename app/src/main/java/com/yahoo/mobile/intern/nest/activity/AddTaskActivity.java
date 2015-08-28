package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddChooseCategory;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddContent;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddLocationDate;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    LatLng mLocation;

    /*
       task info
     */
    public String category;
    public String title;
    public String content;
    public int mYear, mMonth, mDay;
    public int mHour, mMinute;
    public Date expire;
    public String time;
    public String address;

    public Uri imageUri = null;
    public Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增服務");
        Utils.setBuyerColor(this);

        setContentView(R.layout.activity_add_task);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, new FragmentAddChooseCategory())
                .commit();
    }

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
                fragment.stLocation.setImageResource(R.drawable.check_blue);
            }
        }
        if (data != null &&  resultCode == RESULT_OK && requestCode == Common.ACTIVITY_SELECT_IMAGE) {
            FragmentAddContent fragment = (FragmentAddContent)
                    getSupportFragmentManager().findFragmentById(R.id.frame_content);
            Uri selectedImageUri = data.getData();
            fragment.setImgViewUpload(selectedImageUri);
        }
        else if(resultCode == RESULT_OK && requestCode == Common.CAMERA_REQUEST) {
            FragmentAddContent fragment = (FragmentAddContent)
                    getSupportFragmentManager().findFragmentById(R.id.frame_content);
            fragment.setImgViewUpload();
        }
    }

    public void savePictureToPostSync(ParseObject task, Bitmap picture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytearray= stream.toByteArray();
        ParseFile questionPicture = new ParseFile(task.getObjectId() + "_picture.jpg", bytearray);
        try {
            questionPicture.save();
            task.put(Common.OBJECT_QUESTION_PICTURE, questionPicture);
            task.save();
        } catch (Exception e) {
            e.printStackTrace();
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
        task.put(Common.OBJECT_QUESTION_CATEGORY, category);

        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseObject> myNewQuestions = user.getRelation(Common.OBJECT_USER_MY_NEW_QUESTIONS);
                    myNewQuestions.add(task);
                    user.saveInBackground();

                    if(imageUri != null) {
                        savePictureToPostSync(task, image);
                    }
                }
            }
        });
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
