package com.yahoo.mobile.intern.nest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.intern.nest.event.AcceptTaskEvent;
import com.yahoo.mobile.intern.nest.event.AcceptedUserEvent;
import com.yahoo.mobile.intern.nest.event.CatchTaskEvent;
import com.yahoo.mobile.intern.nest.event.CategoryEvent;
import com.yahoo.mobile.intern.nest.event.DoneTaskEvent;
import com.yahoo.mobile.intern.nest.event.FriendsEvent;
import com.yahoo.mobile.intern.nest.event.MyDoneTaskEvent;
import com.yahoo.mobile.intern.nest.event.MyNewTaskEvent;
import com.yahoo.mobile.intern.nest.event.RecipientEvent;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


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
                    EventBus.getDefault().post(new MyNewTaskEvent(questionList));
                } else {
                    Log.d("questions", "Error: " + e.getMessage());
                }
            }
        });
    }
    /*
        Buyer
     */
    static public void getMyNewTasks() {
        ParseRelation<ParseObject> myNewQuestions = ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_MY_NEW_QUESTIONS);
        ParseQuery<ParseObject> query = myNewQuestions.getQuery();
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new MyNewTaskEvent(list));
                }
            }
        });
    }
    static public void getMyDoneTasks() {
        ParseRelation<ParseObject> myDoneQuestions = ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_MY_DONE_QUESTIONS);
        ParseQuery<ParseObject> query = myDoneQuestions.getQuery();
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new MyDoneTaskEvent(list));
                }
            }
        });
    }
    /*
        Seller
     */
    static public void getCatchedTasks() {

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation(Common.OBJECT_USER_CATCH_QUESTIONS);
        ParseQuery<ParseObject> query = relation.getQuery();
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new CatchTaskEvent(list));
                }
            }
        });
    }
    static public void getAcceptedTasks() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation(Common.OBJECT_USER_ACCEPTED_QUESTIONS);
        ParseQuery<ParseObject> query = relation.getQuery();
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new AcceptTaskEvent(list));
                }
            }
        });
    }
    static public void getDoneTasks() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation(Common.OBJECT_USER_DONE_QUESTIONS);
        ParseQuery<ParseObject> query = relation.getQuery();
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new DoneTaskEvent(list));
                }
            }
        });
    }

    static public void getTaskAcceptedUser(ParseObject task) {
        ParseRelation<ParseUser> acceptedUser = task.getRelation(Common.OBJECT_QUESTION_ACCEPTED_USER);
        acceptedUser.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    EventBus.getDefault().post(new AcceptedUserEvent(list));
                }
            }
        });
    }
    /*
     Task transaction
     */
    static public void doneTask(ParseObject task, ParseUser buyer, ParseUser seller) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskId", task.getObjectId());
        params.put("buyerId", buyer.getObjectId());
        params.put("sellerId", seller.getObjectId());
        ParseCloud.callFunctionInBackground("doneTask", params);
    }

    static public void updateRating(ParseUser buyer, Float rating, int num){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buyerId",buyer.getObjectId());
        params.put("rating",rating);
        params.put("votenum",num);
        ParseCloud.callFunctionInBackground("updateRating",params);
    }

    static public void createChatConnection(ParseUser sender, ParseUser recipient){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("senderId", sender.getObjectId());
        params.put("recipientId", recipient.getObjectId());
        ParseCloud.callFunctionInBackground("createChatConnection", params);

    }

    static public void instantMessageNotification(ParseUser sender, ParseUser recipient){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("senderId", sender.getObjectId());
        params.put("recipientId", recipient.getObjectId());
        ParseCloud.callFunctionInBackground("instantMessageNotification", params);
    }

//    TODO: lastest friend at first row
    static public void getFriends(ParseUser currentUser){
        ParseRelation<ParseUser> friends = currentUser.getRelation(Common.OBJECT_USER_FRIENDS);
        friends.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    EventBus.getDefault().post(new FriendsEvent(list));
                }
            }
        });
    }

    static public void getRecipient(String recipientObjectId){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(recipientObjectId, new GetCallback<ParseUser>() {
            public void done(ParseUser recipient, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    EventBus.getDefault().post(new RecipientEvent(recipient));

                } else {
                    // Something went wrong.
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
    static public void updateUserProfile(final Map<String,String> profile, final String mFbId, Bitmap profilePic) {
        final ParseUser user = ParseUser.getCurrentUser();
        user.put(Common.OBJECT_USER_FB_ID, mFbId);
        updateUserProfile(profile, profilePic);
    }

    static public void updateUserProfile(final Map<String,String> profile, Bitmap profilePic) {
        final ParseUser user = ParseUser.getCurrentUser();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytearray= stream.toByteArray();
        final ParseFile imgFile = new ParseFile(user.getUsername() + "_profile.jpg", bytearray);
        imgFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (profile.containsKey(Common.OBJECT_USER_NICK)) {
                    user.put(Common.OBJECT_USER_NICK, profile.get(Common.OBJECT_USER_NICK));
                }
                if (profile.containsKey(Common.OBJECT_USER_ADDRESS)) {
                    user.put(Common.OBJECT_USER_ADDRESS, profile.get(Common.OBJECT_USER_ADDRESS));
                }
                if (profile.containsKey(Common.OBJECT_USER_PHONE)) {
                    user.put(Common.OBJECT_USER_PHONE, profile.get(Common.OBJECT_USER_PHONE));
                }
                if (profile.containsKey(Common.OBJECT_USER_OTHERS)) {
                    user.put(Common.OBJECT_USER_OTHERS, profile.get(Common.OBJECT_USER_OTHERS));
                }
                if (profile.containsKey(Common.OBJECT_USER_CATEGORY)){
                    user.put(Common.OBJECT_USER_CATEGORY, profile.get(Common.OBJECT_USER_CATEGORY));
                }
                user.put(Common.OBJECT_USER_PROFILE_PIC, imgFile);
                user.saveInBackground();
            }
        });
    }
    /*
     Misc
     */
    static public void displayParseImage(final ParseFile imgFile, final CircleImageView imgView) {
        imgFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        imgView.setImageBitmap(bmp);
                    }
                }
            }
        });
    }

    static public void updateUserMap(final Bitmap profilePic) {
        final ParseUser user = ParseUser.getCurrentUser();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytearray= stream.toByteArray();
        final ParseFile imgFile = new ParseFile(user.getUsername() + "_map.jpg", bytearray);
        imgFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.put(Common.OBJECT_USER_MAP_PIC, imgFile);
                user.saveInBackground();
            }
        });
    }

    static public void displayUserMap(ParseFile imgFile,  final ImageView mapImg) {
        if(imgFile == null) {
            return;
        }

        final ParseUser user = ParseUser.getCurrentUser();
        imgFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) {
                        mapImg.setImageBitmap(bmp);
                        mapImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mapImg.getRootView().invalidate();
                    }
                }
            }
        });
    }

    static public void setUserAcceptTask(boolean accept){
        final ParseUser user = ParseUser.getCurrentUser();
        user.put(Common.OBJECT_USER_ACCEPT, accept);
        user.saveInBackground();
    }

    static public void getCategoryList(){
        ParseQuery<ParseObject> category = ParseQuery.getQuery(Common.OBJECT_CATEGORY);
        category.orderByAscending(Common.OBJECT_CATEGORY_ID);
        category.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    EventBus.getDefault().post(new CategoryEvent(list));
                }
            }
        });
    }
}
