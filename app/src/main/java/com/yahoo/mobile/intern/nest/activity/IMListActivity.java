package com.yahoo.mobile.intern.nest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;

import com.yahoo.mobile.intern.nest.adapter.IMUserAdapter;
import com.yahoo.mobile.intern.nest.event.FriendsEvent;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class IMListActivity extends AppCompatActivity {
    private ParseUser currentUser;
    private IMUserAdapter mAdapter;
    private List<ParseUser> mFriendsList;

    @Bind(R.id.list_view_friends) ListView mListView;

    public void setupFriends(){
        mFriendsList = new ArrayList<>();
        mAdapter = new IMUserAdapter(this, mFriendsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser recipient = (ParseUser) mAdapter.getItem(position);
                Utils.gotoMessagingActivity(IMListActivity.this, recipient.getObjectId());
            }
        });


        ParseUtils.getFriends(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imlist);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = ParseUser.getCurrentUser();

        setupFriends();
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

    public void onEvent(FriendsEvent event) {
        mFriendsList.clear();
        mFriendsList.addAll(event.friendsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_imlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
