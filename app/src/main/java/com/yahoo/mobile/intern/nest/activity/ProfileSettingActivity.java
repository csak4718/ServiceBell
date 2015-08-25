package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class ProfileSettingActivity extends AppCompatActivity {

    private int mRadius;
    private LatLng position;
    private String mAddress;
    //private String[] serviceCategory = {"水電","美容","打砸"};

    @Bind(R.id.swtich_task) Switch mSwitch;
    @Bind(R.id.img_map) ImageView mImgMap;
    @Bind(R.id.lt_map_setting) LinearLayout mSettingLayout;
    @Bind(R.id.txt_setting_address) TextView mAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        loadedMapImg();

        Boolean acc = false;
        ParseUser user = ParseUser.getCurrentUser();
        if( user.get(Common.OBJECT_USER_ACCEPT) != null)
            acc = (Boolean)user.get(Common.OBJECT_USER_ACCEPT);
        setSwitch(mSwitch, acc);

        if( user.get(Common.OBJECT_USER_RADIUS) != null)
            mRadius = user.getInt(Common.OBJECT_USER_RADIUS);

        mAddress = user.getString(Common.OBJECT_USER_ADDRESS);
        if(mAddress != null)
            mAddressTextView.setText(mAddress+mRadius);


       // ArrayAdapter<String> lunchList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serviceCategory);
        //mCategorySpinner.setAdapter(lunchList);

    }
    @OnCheckedChanged(R.id.swtich_task) void setSwitch(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mSettingLayout.setVisibility(View.VISIBLE);
        } else {
            mSettingLayout.setVisibility(View.GONE);
        }
        buttonView.setChecked(isChecked);
        ParseUtils.setUserAcceptTask(isChecked);
    }

    @OnClick(R.id.setting_edit) void setServiceLocation() {
        Utils.gotoMapsActivityForResult(this);
    }
    @OnClick(R.id.setting_profile) void setProfile(){
        Utils.gotoBSInfoSettingActivity(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //loadedMapImg();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.REQUEST_LOCATION) {
            if(resultCode == RESULT_OK) {
                position = data.getParcelableExtra(Common.EXTRA_LOCATION);
                mRadius  = data.getIntExtra(Common.EXTRA_RADIUS, 0);

                mAddress = data.getStringExtra(Common.EXTRA_ADDRESS);
                mAddressTextView.setText(mAddress);

                String path= data.getStringExtra(Common.EXTRA_MAP_PATH);
                loadMapFromStorage(path);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            /*
            ParseUser user = ParseUser.getCurrentUser();
            if(position != null) {
                ParseGeoPoint pin = new ParseGeoPoint(position.latitude, position.longitude);
                user.put(Common.OBJECT_USER_PIN, pin);
            }
            user.put(Common.OBJECT_USER_RADIUS, seekBarRadius.getProgress());
            user.saveInBackground();
            finish();
            return true;*/
        }
        if (id == android.R.id.home) {
            ParseUser user = ParseUser.getCurrentUser();
            if(position != null) {
                ParseGeoPoint pin = new ParseGeoPoint(position.latitude, position.longitude);
                user.put(Common.OBJECT_USER_PIN, pin);
            }
            if(mRadius != -1) {
                user.put(Common.OBJECT_USER_RADIUS, mRadius);
            }
            if(mAddress != null) {
                user.put(Common.OBJECT_USER_ADDRESS, mAddress);
            }

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    finish();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadedMapImg(){
        ParseFile imgFile = ParseUser.getCurrentUser().getParseFile(Common.OBJECT_USER_MAP_PIC);
        if(imgFile == null)
            mImgMap.setVisibility(View.GONE);
        else
            mImgMap.setVisibility(View.VISIBLE);
        ParseUtils.displayUserMap(imgFile, mImgMap);
    }

    private void loadMapFromStorage(String path) {
        try {
            File f=new File(path, Common.PATH_MAP);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            mImgMap.setImageBitmap(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
