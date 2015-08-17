package com.yahoo.mobile.intern.nest.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.greenrobot.event.EventBus;

import com.yahoo.mobile.intern.nest.event.CatchTaskEvent;
import com.yahoo.mobile.intern.nest.event.MyTaskEvent;

/**
 * Created by cmwang on 8/12/15.
 */
public class ParseUtils {

    /*
     Question related
     */
    static public void getAllTasks() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_QUESTION);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, ParseException e) {
                if (e == null) {
                    Log.d("questions", "Retrieved " + questionList.size() + " questions");
                    EventBus.getDefault().post(new MyTaskEvent(questionList));
                } else {
                    Log.d("questions", "Error: " + e.getMessage());
                }
            }
        });
    }
    static public void getMyTasks() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_QUESTION);
        query.orderByDescending("updatedAt");
        query.whereEqualTo(Common.OBJECT_QUESTION_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new MyTaskEvent(list));
                }
            }
        });
    }
    static public void getCatchedTasks() {

        ParseUser user = ParseUser.getCurrentUser();

        ParseGeoPoint servicePosition = user.getParseGeoPoint(Common.OBJECT_USER_PIN);
        int radius = user.getInt(Common.OBJECT_USER_RADIUS);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_QUESTION);
        query.orderByDescending("updatedAt");
        query.whereWithinKilometers(Common.OBJECT_QUESTION_PIN, servicePosition, radius);
        query.whereNotEqualTo(Common.OBJECT_QUESTION_USER, user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new CatchTaskEvent(list));
                }
            }
        });
    }

    /*
     User profile related
     */
    static public boolean isSellerNetSeted() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getParseGeoPoint(Common.OBJECT_USER_PIN) != null;
    }
    static public void updateUserProfile(final String nickName, final String mFbId, Bitmap profilePic) {
        final ParseUser user = ParseUser.getCurrentUser();
        user.put(Common.OBJECT_USER_FB_ID, mFbId);
        updateUserProfile(nickName, profilePic);
    }

    static public void updateUserProfile(final String nickName, Bitmap profilePic) {
        final ParseUser user = ParseUser.getCurrentUser();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytearray= stream.toByteArray();
        final ParseFile imgFile = new ParseFile(user.getUsername() + "_profile.jpg", bytearray);
        imgFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.put(Common.OBJECT_USER_NICK, nickName);
                user.put(Common.OBJECT_USER_PROFILE_PIC, imgFile);
                user.saveInBackground();
            }
        });
    }
    /*
     User accept task
     */
    static public boolean isUserAcceptTask(ParseUser user, ParseObject task) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_ACCEPTED_TASKS);
        query.whereEqualTo(Common.OBJECT_ACCEPTED_TASKS_USER, user);
        query.whereEqualTo(Common.OBJECT_ACCEPTED_TASKS_TASK, task);
        try {
            return query.count() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
