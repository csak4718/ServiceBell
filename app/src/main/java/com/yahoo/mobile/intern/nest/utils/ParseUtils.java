package com.yahoo.mobile.intern.nest.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.greenrobot.event.EventBus;
import com.yahoo.mobile.intern.nest.event.QuestionEvent;

/**
 * Created by cmwang on 8/12/15.
 */
public class ParseUtils {

    /*
     Question related
     */
    static public void getAllQuestions() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_QUESTION);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, ParseException e) {
                if (e == null) {
                    Log.d("questions", "Retrieved " + questionList.size() + " questions");
                    EventBus.getDefault().post(new QuestionEvent(questionList));
                } else {
                    Log.d("questions", "Error: " + e.getMessage());
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
}
