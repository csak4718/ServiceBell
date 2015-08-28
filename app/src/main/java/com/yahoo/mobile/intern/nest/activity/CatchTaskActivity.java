package com.yahoo.mobile.intern.nest.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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
import com.parse.SaveCallback;
import com.sinch.android.rtc.SinchError;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.dialog.DialogFragmentSellerProfile;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class CatchTaskActivity extends BaseActivity implements SinchService.StartFailedListener {
    private int mType;

    private ParseObject mTask;
    private String taskId;
    private ParseUser buyer;
    private ProgressDialog mSpinner;
    private ParseGeoPoint mGeoPoint;

    private boolean result = false;

    //@Bind(R.id.btn_toMessaging) Button btnToMessaging;
    @Bind(R.id.txt_status) TextView txtStatus;

    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.txt_location) TextView txtAddress;

    @Bind(R.id.new_task_op_banner) LinearLayout newTaskOpBanner;
    @Bind(R.id.done_task_op_banner) ViewGroup doneTaskOpBanner;

    @Bind(R.id.btn_accept_task) Button btnAcceptTask;
    @Bind(R.id.btn_reject_task) Button btnRejectTask;
    @Bind(R.id.img_user_pic)CircleImageView imgUserPic;
    @Bind(R.id.txt_name) TextView txtUserName;
    @Bind(R.id.txt_task_time) TextView txtTaskTime;
    @Bind(R.id.txt_remaining) TextView txtRemaining;
    @Bind(R.id.txt_category) TextView txtCategory;
    @Bind(R.id.img_view_question_picture)ImageView imgViewQuestionPicture;
    @Bind(R.id.ratingBar)RatingBar ratingBar;
    @Bind(R.id.btn_chat) Button btnChat;

    @OnClick(R.id.img_addres) void viewMap(){
        Utils.gotoMapsActivityCurLocation(this, new LatLng(mGeoPoint.getLatitude(), mGeoPoint.getLongitude()), txtTitle.getText().toString());
    }


    @OnClick(R.id.rlayout_buyer) void buyerProfile(){
        DialogFragmentSellerProfile dfsp = DialogFragmentSellerProfile.newInstance(CatchTaskActivity.this, buyer, mType);
        dfsp.show(getSupportFragmentManager(), "buyerInfo");
    }

    @OnClick(R.id.btn_reject_task) void rejectTask() {

        Utils.showLoadingDialog(this);

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> catchQuestions = user.getRelation(Common.OBJECT_USER_CATCH_QUESTIONS);
        catchQuestions.remove(mTask);
        ParseRelation<ParseObject> acceptedQuestions = user.getRelation(Common.OBJECT_USER_ACCEPTED_QUESTIONS);
        acceptedQuestions.remove(mTask);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_accept_task) void acceptTask() {

        newTaskOpBanner.setVisibility(View.GONE);
        Snackbar.make(findViewById(android.R.id.content), "你接了一個任務", Snackbar.LENGTH_SHORT)
                .show();
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> catchRelation = user.getRelation(Common.OBJECT_USER_CATCH_QUESTIONS);
        ParseRelation<ParseObject> acceptedRelation = user.getRelation(Common.OBJECT_USER_ACCEPTED_QUESTIONS);
        catchRelation.remove(mTask);
        acceptedRelation.add(mTask);
        user.saveInBackground();

        ParseRelation<ParseUser> acceptedUser = mTask.getRelation(Common.OBJECT_QUESTION_ACCEPTED_USER);
        acceptedUser.add(user);
        mTask.saveInBackground();

        /*
         Use cloud code to notify buyer
         */
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Common.CLOUD_NOTIFY_ACCEPT_SENDERNAME, user.getString(Common.OBJECT_USER_NICK));
        params.put(Common.CLOUD_NOTIFY_ACCEPT_BUYERID, mTask.getParseUser(Common.OBJECT_QUESTION_USER).getObjectId());
        ParseCloud.callFunctionInBackground(Common.CLOUD_NOTIFY_ACCEPT, params);

        newTaskOpBanner.setVisibility(View.GONE);
        doneTaskOpBanner.setVisibility(View.VISIBLE);

        result = true;

    }
    @OnClick(R.id.btn_chat) void chat() {
        btnToMessagingClicked();
    }
    @OnClick(R.id.btn_cancel_accept) void cancelAccept() {

    }


    private void setupBuyerProfile(ParseUser buyer) {
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
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject task, ParseException e) {
                mTask = task;
                buyer = (ParseUser) task.get(Common.OBJECT_QUESTION_USER);
                buyer.fetchInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser buyer, ParseException e) {
                        if (e == null) {
                            setupBuyerProfile(buyer);
                        }
                    }
                });
                String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                String category = task.getString(Common.OBJECT_QUESTION_CATEGORY);

                txtTaskTime.setText(task.getString(Common.OBJECT_QUESTION_TIME));
                txtTitle.setText(title);
                txtContent.setText(content);
                txtCategory.setText(category);
                ratingBar.setRating(buyer.getNumber(Common.OBJECT_USER_RATING).floatValue());

                Date expire = task.getDate(Common.OBJECT_QUESTION_EXPIRE_DATE);
                Date current = new Date();
                txtRemaining.setText(Utils.getRemainingTime(current, expire));
                txtAddress.setText(task.getString(Common.OBJECT_QUESTION_ADDRESS));

                mGeoPoint = task.getParseGeoPoint(Common.OBJECT_QUESTION_PIN);
                ParseFile questionPicture = task.getParseFile(Common.OBJECT_QUESTION_PICTURE);
                if(questionPicture != null) {
                    imgViewQuestionPicture.setVisibility(View.VISIBLE);
                    Picasso.with(CatchTaskActivity.this).load(questionPicture.getUrl())
                            .into(imgViewQuestionPicture);
                    imgViewQuestionPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog = new Dialog(CatchTaskActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_image);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;


                            Bitmap bmp = ((BitmapDrawable) imgViewQuestionPicture.getDrawable())
                                    .getBitmap();

                            ImageView picture = (ImageView) dialog.findViewById(R.id.img_view_dialog_picture);
                            ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.img_btn_close);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            picture.setImageBitmap(bmp);
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor
                                    ("#80000000")));
                        }
                    });
                }
            }
        });
    }

    private void setupLayoutForType() {
        switch (mType) {
            case Common.SELLER_NEW:
                newTaskOpBanner.setVisibility(View.VISIBLE);
                txtStatus.setText("等待中");
                break;
            case Common.SELLER_ACCEPTED:
                doneTaskOpBanner.setVisibility(View.VISIBLE);
                txtStatus.setText("洽談中");
                break;
            case Common.SELLER_DONE:
                doneTaskOpBanner.setVisibility(View.VISIBLE);
                txtStatus.setText("已成交");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_catch_task);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSellerColor(this);

        taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);
        mType = getIntent().getIntExtra(Common.EXTRA_STATE, Common.SELLER_NEW);

        setupLayoutForType();
        setupTask();

    }


    @Override
    protected void onServiceConnected() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catch_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            Intent ret = new Intent();
            ret.putExtra("result", result);
            setResult(RESULT_OK, ret);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
