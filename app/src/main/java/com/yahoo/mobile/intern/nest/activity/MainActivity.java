package com.yahoo.mobile.intern.nest.activity;

import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.FragmentBuyer;
import com.yahoo.mobile.intern.nest.fragment.FragmentMyNewTask;
import com.yahoo.mobile.intern.nest.fragment.FragmentSeller;

public class MainActivity extends AppCompatActivity {

    /*
     DrawerLayout
     */
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ActionBar mActionBar;
    private FrameLayout frameContent;

    /*
     Fragment
     */
    private Fragment fragmentContent;
    private FragmentBuyer fragmentBuyer;
    private FragmentSeller fragmentSeller;

    /*
     Drawer
     */

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                            R.string.app_name,
                            R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_my_task:
                        switchContent(fragmentBuyer);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.menu_catch_task:
                        switchContent(fragmentSeller);
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

    }

    private void switchContent(Fragment fragment) {
        if(fragment != fragmentContent) {
            fragmentContent = fragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_content, fragment)
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupActionBar();
        setupDrawer();

        fragmentBuyer = new FragmentBuyer();
        fragmentSeller = new FragmentSeller();
        fragmentContent = fragmentBuyer;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, fragmentBuyer)
                .commit();

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
}
