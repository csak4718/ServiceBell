package com.yahoo.mobile.intern.nest.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.DialogFragmentSellerProfile;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfileActivity extends BaseActivity implements SinchService.StartFailedListener {


    private ParseUser seller;

    private ProgressDialog mSpinner;
    private ImageButton btnChat;

    private ParseObject task;
    @Bind(R.id.btn_popup) Button btnPopup;
    @Bind(R.id.img_pic) CircleImageView imgPic;
    @Bind(R.id.txt_name) TextView txtName;

    @OnClick(R.id.btn_popup) void popUp(){
        DialogFragmentSellerProfile dialogFragment = DialogFragmentSellerProfile.newInstance(seller,0);
        dialogFragment.show(getSupportFragmentManager(),"TTT");
    }

    @OnClick(R.id.btn_done) void taskDone() {
        ParseUtils.doneTask(task, ParseUser.getCurrentUser(), seller);
    }

    private void setupView() {
        ParseFile parseImg = seller.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        ParseUtils.displayParseImage(parseImg, imgPic);
        txtName.setText(seller.getString(Common.OBJECT_USER_NICK));

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatClick();
            }
        });
    }

    private void chatClick() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        String userName = currentUser.getObjectId();

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            Utils.gotoMessagingActivity(SellerProfileActivity.this, seller.getObjectId());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_seller_profile);
        ButterKnife.bind(this);

        btnChat = (ImageButton) findViewById(R.id.btn_chat);
        btnChat.setEnabled(false);

        final String userId = getIntent().getStringExtra(Common.EXTRA_USER_ID);
        final String taskId = getIntent().getStringExtra(Common.EXTRA_TASK_ID);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e == null) {
                    seller = parseUser;
                    try {
                        task = ParseQuery.getQuery(Common.OBJECT_QUESTION).get(taskId);
                        setupView();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        btnChat.setEnabled(true);
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
        Utils.gotoMessagingActivity(SellerProfileActivity.this, seller.getObjectId());
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seller_profile, menu);
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
