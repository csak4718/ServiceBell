package com.yahoo.mobile.intern.nest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfileActivity extends AppCompatActivity {


    private ParseUser seller;
    private ParseObject task;

    @Bind(R.id.img_pic) CircleImageView imgPic;
    @Bind(R.id.txt_name) TextView txtName;
    @OnClick(R.id.btn_chat) void chatClick() {

    }
    @OnClick(R.id.btn_done) void taskDone() {
        ParseUtils.doneTask(task, ParseUser.getCurrentUser(), seller);
    }

    private void setupView() {
        ParseFile parseImg = seller.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        ParseUtils.displayParseImage(parseImg, imgPic);
        txtName.setText(seller.getString(Common.OBJECT_USER_NICK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_seller_profile);
        ButterKnife.bind(this);

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
