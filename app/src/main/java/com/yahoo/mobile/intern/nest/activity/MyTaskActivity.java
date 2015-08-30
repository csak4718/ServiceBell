package com.yahoo.mobile.intern.nest.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.AcceptedUserAdapter;
import com.yahoo.mobile.intern.nest.dialog.ConfirmDialog;
import com.yahoo.mobile.intern.nest.dialog.DeleteDialogFragment;
import com.yahoo.mobile.intern.nest.dialog.DialogFragmentSellerProfile;
import com.yahoo.mobile.intern.nest.dialog.RatingDialog;
import com.yahoo.mobile.intern.nest.event.AcceptedUserEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;
import com.yahoo.mobile.intern.nest.view.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyTaskActivity extends BaseActivity implements ConfirmDialog.ConfirmDialogListener, DeleteDialogFragment.DeleteDialogListener, RatingDialog.RatingDialogListener, SinchService.StartFailedListener {
    private ProgressDialog mSpinner;

    private String taskId;
    private ParseObject mTask;
    private int mType;
    private ParseGeoPoint mGeoPoint;
    private Boolean rated;
    private ParseUser doneUser;


    private AcceptedUserAdapter mAdapter;
    private List<ParseUser> mList;

    @Bind(R.id.txt_location) TextView txtAddress;
    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.list_view_accepted_seller)ExpandableHeightListView mListView;
    @Bind(R.id.txt_task_time) TextView txtTaskTime;
    @Bind(R.id.txt_remaining) TextView txtRemaining;
    @Bind(R.id.txt_status) TextView txtStatus;
    @Bind(R.id.txt_category) TextView txtCategory;
    @Bind(R.id.ratingBar)RatingBar ratingBar;

    @Bind(R.id.new_task_section) ViewGroup newTaskSection;
    @Bind(R.id.done_task_section) ViewGroup doneTaskSection;
    @Bind(R.id.done_task_section2) ViewGroup doneTaskSection2;
    @Bind(R.id.img_view_question_picture)ImageView imgViewQuestionPicture;
    @Bind(R.id.btn_chat) Button btnChat;
    /*
      Done user field
     */
    @Bind(R.id.txt_name) TextView txtName;
    @Bind(R.id.img_pic) CircleImageView imgPic;


    @OnClick(R.id.img_addres) void viewMap() {
        Utils.gotoMapsActivityCurLocation(this, new LatLng(mGeoPoint.getLatitude(), mGeoPoint.getLongitude()), txtTitle.getText().toString());
    }

    @OnClick(R.id.btn_chat) void chat() {
        chatClick();
    }

    @OnClick(R.id.btn_rate) void rate() {
        if (rated==null||rated==false){
            RatingDialog rd = new RatingDialog();
            rd.show(getSupportFragmentManager(),"RatingDialog");
        }else{
            Utils.makeToast(this, "已經評分過了");
        }
    }

    @Override
    protected void onServiceConnected(){
        btnChat.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause(){
        if(mSpinner!=null){
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
        Utils.gotoMessagingActivity(this, doneUser.getObjectId());
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }


    private void chatClick(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userName = currentUser.getObjectId();
        if (!getSinchServiceInterface().isStarted()){
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        }
        else {
            Utils.gotoMessagingActivity(MyTaskActivity.this, doneUser.getObjectId());
        }
    }

    void doneTaskShowSellerProfile() {
        DialogFragmentSellerProfile.newInstance(MyTaskActivity.this, doneUser, mType)
                                .show(getSupportFragmentManager(), "Profile");
    }

    private void setupTask() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Common.OBJECT_QUESTION);
        query.getInBackground(taskId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject task, ParseException e) {
                if (e == null) {
                    mTask = task;

                    String title = task.getString(Common.OBJECT_QUESTION_TITLE);
                    String content = task.getString(Common.OBJECT_QUESTION_CONTENT);
                    String time = task.getString(Common.OBJECT_QUESTION_TIME);
                    String category = task.getString(Common.OBJECT_QUESTION_CATEGORY);

                    rated = task.getBoolean(Common.OBJECT_QUESTION_RATED);

                    txtTitle.setText(title);
                    txtContent.setText(content);
                    txtTaskTime.setText(time);
                    txtCategory.setText(category);

                    Date expire = task.getDate(Common.OBJECT_QUESTION_EXPIRE_DATE);
                    Date current = new Date();
                    txtRemaining.setText(Utils.getRemainingTime(current, expire));

                    txtAddress.setText(task.getString(Common.OBJECT_QUESTION_ADDRESS));
                    mGeoPoint = task.getParseGeoPoint(Common.OBJECT_QUESTION_PIN);


                    setupAcceptedSellers();
                    // task is not done
                    if (mType == Common.BUYER_NEW) {
                        txtStatus.setText("等待中");
                        txtStatus.setBackgroundResource(R.drawable.round_corner_red);
                        txtStatus.setTextColor(getResources().getColor(R.color.nest_status_red));
                        ParseUtils.getTaskAcceptedUser(task);
                    } else {
                        txtStatus.setText("已成交");
                        txtStatus.setBackgroundResource(R.drawable.round_corner_green);
                        txtStatus.setTextColor(getResources().getColor(R.color.nest_status_green));
                        ParseUser seller = task.getParseUser(Common.OBJECT_QUESTION_DONE_USER);
                        try {
                            seller = seller.fetch();
                            doneUser = seller;
                            ratingBar.setRating(doneUser.getNumber(Common.OBJECT_USER_RATING).floatValue());
                            txtName.setText(seller.getString(Common.OBJECT_USER_NICK));

                            ParseFile imgFile = seller.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                            ParseUtils.displayParseImage(imgFile, imgPic);

                            doneTaskSection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doneTaskShowSellerProfile();
                                }
                            });

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    ParseFile questionPicture = task.getParseFile(Common.OBJECT_QUESTION_PICTURE);
                    if(questionPicture != null) {
                        imgViewQuestionPicture.setVisibility(View.VISIBLE);
                        Picasso.with(MyTaskActivity.this).load(questionPicture.getUrl())
                                .into(imgViewQuestionPicture);
                        imgViewQuestionPicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(MyTaskActivity.this);
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
                DialogFragmentSellerProfile dfsp;
                if (mTask.getParseUser(Common.OBJECT_QUESTION_DONE_USER) != null) {
                    dfsp = DialogFragmentSellerProfile.newInstance(MyTaskActivity.this, mList.get
                            (position), mType);
                } else {
                    dfsp = DialogFragmentSellerProfile.newInstance(MyTaskActivity.this, mList.get
                            (position), mType);
                }
                dfsp.show(getSupportFragmentManager(), "Profile");
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_new_task);
        ButterKnife.bind(this);
        btnChat.setEnabled(false);

        mListView.setExpanded(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setBuyerColor(this);

        taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);
        mType = getIntent().getIntExtra(Common.EXTRA_TYPE, Common.BUYER_NEW);
        if(mType == Common.BUYER_NEW) {
            doneTaskSection.setVisibility(View.GONE);
            doneTaskSection2.setVisibility(View.GONE);
        }
        if(mType == Common.BUYER_DONE) {
            newTaskSection.setVisibility(View.GONE);
        }

        setupTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            //closeActivity();
            finish();
        }
        if(id == R.id.action_delete) {
            DeleteDialogFragment df = new DeleteDialogFragment();
            df.show(getSupportFragmentManager(),"DeleteDialog");
        }

        return super.onOptionsItemSelected(item);
    }


    public void closeAuction(ParseUser seller){
        ParseUtils.doneTask(mTask, ParseUser.getCurrentUser(), seller);
        closeActivity();
    }

    public void closeActivity(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",true);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onFinishDeleteDialog(boolean i) {
        mTask.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Utils.showLoadingDialog(MyTaskActivity.this);
                finish();
            }
        });
    }

    @Override
    public void onFinishConfirmDialog(String inputText, ParseUser seller) {
        Log.d("asd", inputText);
        closeAuction(seller);
    }

    @Override
    public void onFinishDialog(Float rating) {
        upDateRating(rating);
    }
    public void upDateRating(Float rating){
        rated = true;
        mTask.put(Common.OBJECT_QUESTION_RATED, true);
        mTask.saveInBackground();
        Float curRating = doneUser.getNumber(Common.OBJECT_USER_RATING).floatValue();
        Number num = doneUser.getNumber(Common.OBJECT_USER_RATENUM);
        final int rateNum;
        if (num==null) {
            rateNum = 0;
        }else{
            rateNum = num.intValue();
        }
        final Float update = (curRating*rateNum+rating)/(rateNum+1);
        ratingBar.setRating(update);
        ParseUtils.updateRating(doneUser, update, rateNum + 1);
        Utils.makeToast(this,"已評分");
    }
}
