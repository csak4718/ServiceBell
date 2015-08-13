package com.yahoo.mobile.intern.nest.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.QuestionCardAdapter;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import event.QuestionEvent;

public class MainActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    /*
      Card
     */
    private QuestionCardAdapter mAdapter;
    private SwipeFlingAdapterView flingContainer;

    private List<ParseObject> mList;

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

    public void onEvent(QuestionEvent event) {
        Log.d("eventbus", "" + event.questionList.size());
        refreshList(event.questionList);
    }

    private void refreshList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupActionBar();

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        mList = new ArrayList<>();
        mAdapter = new QuestionCardAdapter(this, mList);

        flingContainer.setAdapter(mAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                Collections.swap(mList, 0, mList.size() - 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
            }

            @Override
            public void onRightCardExit(Object dataObject) {
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });

        ParseUtils.getAllQuestions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
