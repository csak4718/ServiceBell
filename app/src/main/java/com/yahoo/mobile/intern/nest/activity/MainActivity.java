package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.FragmentTab;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    /*
     DrawerLayout
     */
    @Bind(R.id.navigation_view) LinearLayout navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.btn_add_post) FloatingActionButton btnAddPost;
    @Bind(R.id.drawer_img_profile) CircleImageView imgProfile;
    @Bind(R.id.drawer_txt_name)TextView txtName;

    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;

    /*
     Fragment
     */
    FragmentTab fragmentTab;


    @OnClick(R.id.btn_find_service) void findService() {
        mActionBar.setTitle("找服務");
        Utils.setBuyerColor(MainActivity.this);
        fragmentTab.switchTab(R.id.menu_my_task);
        btnAddPost.setVisibility(View.VISIBLE);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_get_service) void getService() {
        mActionBar.setTitle("接服務");
        Utils.setSellerColor(MainActivity.this);
        fragmentTab.switchTab(R.id.menu_catch_task);
        btnAddPost.setVisibility(View.GONE);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_setting) void goSetting() {
        Utils.gotoProfileSettingActivity(MainActivity.this);
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                            R.string.app_name,
                            R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*
         setup header layout
         */
        ParseFile imgFile = ParseUser.getCurrentUser().getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        ParseUtils.displayParseImage(imgFile, imgProfile);
        txtName.setText(ParseUser.getCurrentUser().getString(Common.OBJECT_USER_NICK));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fragmentTab != null) {
            fragmentTab.refreshAllTab();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @OnClick(R.id.btn_add_post) void addPost() {
        Utils.gotoAddTaskActivity(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setTitle("找服務");
        Utils.setBuyerColor(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("1234", "22344");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupActionBar();
        setupDrawer();

        fragmentTab = FragmentTab.newInstance(R.id.menu_my_task);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, fragmentTab)
                .commit();


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessagingActivity", false);
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("test",String.valueOf(requestCode));
        if (requestCode == Common.REQUEST_MY_TASK) {
            if (data!=null){
                Boolean result=data.getBooleanExtra("result",false);
                Log.d("test",result.toString());
                if (result == true){
                    fragmentTab.setCurrentPage(1);
                    fragmentTab.refreshAllTab();
                }
            }
        }
    }
}
