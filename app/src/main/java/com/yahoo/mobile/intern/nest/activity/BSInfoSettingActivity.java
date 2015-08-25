package com.yahoo.mobile.intern.nest.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.event.FbPictureEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by ytli on 8/19/15.
 */
public class BSInfoSettingActivity extends AppCompatActivity {
    ParseUser user;
    //@Bind(R.id.btn_log_out)Button mBtnLogout;
    @Bind(R.id.edt_setting_nickname) EditText mEdtNickName;
    @Bind(R.id.edt_phone) EditText mEdtPhone;
    //@Bind(R.id.edt_address) EditText mEdtAddress;
    @Bind(R.id.edt_others) EditText mEdtOthers;
    @Bind(R.id.img_profile_pic) ImageView mImgProfilePic;
    @Bind(R.id.spinner_category) Spinner mCategorySpinner;

    Handler mHandler = new Handler();

    /*
    @OnClick(R.id.btn_log_out) void setmBtnLogout(){
        ParseUser.getCurrentUser().logOut();
        Utils.gotoLoginActivity(this);
        finish();
    }*/

    @OnClick(R.id.btn_confirm) void setConfirm() {
        final String nickName = mEdtNickName.getText().toString();
        Bitmap profilePic = ((BitmapDrawable)mImgProfilePic.getDrawable()).getBitmap();
        Map<String,String> profile = new HashMap<String,String>();
        profile.put(Common.OBJECT_USER_NICK,mEdtNickName.getText().toString());
        //profile.put(Common.OBJECT_USER_ADDRESS, mEdtAddress.getText().toString());
        profile.put(Common.OBJECT_USER_PHONE ,mEdtPhone.getText().toString());
        profile.put(Common.OBJECT_USER_OTHERS, mEdtOthers.getText().toString());
        ParseUtils.updateUserProfile(profile, profilePic);
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsinfosetting);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = ParseUser.getCurrentUser();
        getProfile();

        String[] category = {"居家清潔/整理","汽車保養/美容","美容/美甲","育兒服務","寵物服務","其他"};
        ArrayAdapter categoryList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);
        categoryList.setDropDownViewResource(R.layout.drop_down_item);
        mCategorySpinner.setAdapter(categoryList);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_setting, menu);
        return true;
    }
    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(final FbPictureEvent event) {
        Log.d("eventbus", "get fb pic");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mImgProfilePic.setImageBitmap(event.mPic);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            final String nickName = mEdtNickName.getText().toString();
            Bitmap profilePic = ((BitmapDrawable)mImgProfilePic.getDrawable()).getBitmap();
            Map<String,String> profile = new HashMap<String,String>();
            profile.put(Common.OBJECT_USER_NICK,mEdtNickName.getText().toString());
            //profile.put(Common.OBJECT_USER_ADDRESS, mEdtAddress.getText().toString());
            profile.put(Common.OBJECT_USER_PHONE ,mEdtPhone.getText().toString());
            profile.put(Common.OBJECT_USER_OTHERS, mEdtOthers.getText().toString());
            ParseUtils.updateUserProfile(profile, profilePic);
            finish();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getProfile(){
        String nickName,phone, address, others ="";
        nickName = user.getString(Common.OBJECT_USER_NICK);
        phone = user.getString(Common.OBJECT_USER_PHONE);
        address = user.getString(Common.OBJECT_USER_ADDRESS);
        others = user.getString(Common.OBJECT_USER_OTHERS);

        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Log.d("parse imgfile url", imgFile.getUrl());
        Uri imgUri = Uri.parse(imgFile.getUrl());

        if(mImgProfilePic != null) {
            Picasso.with(this).load(imgUri.toString()).into(mImgProfilePic);
        }
        mEdtNickName.setText(nickName);
        //mEdtAddress.setText(address);
        mEdtPhone.setText(phone);
        mEdtOthers.setText(others);
    }
}
