package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class ProfileSettingActivity extends AppCompatActivity {

    private LatLng position;
    @Bind(R.id.swtich_task) Switch mSwitch;
    @Bind(R.id.img_map) ImageView mImgMap;
    @Bind(R.id.lt_map_setting) LinearLayout mSettingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        loadedMapImg();
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
        loadedMapImg();

        Boolean acc = false;
        ParseUser user = ParseUser.getCurrentUser();
        if( user.get(Common.OBJECT_USER_ACCEPT) != null)
             acc = (Boolean)user.get(Common.OBJECT_USER_ACCEPT);

        setSwitch(mSwitch,acc);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.REQUEST_LOCATION) {
            if(resultCode == RESULT_OK) {
                position = data.getParcelableExtra(Common.EXTRA_LOCATION);
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadedMapImg(){
        ParseFile imgFile = ParseUser.getCurrentUser().getParseFile(Common.OBJECT_USER_MAP_PIC);
        ParseUtils.displayUserMap(imgFile, mImgMap);
    }
}
