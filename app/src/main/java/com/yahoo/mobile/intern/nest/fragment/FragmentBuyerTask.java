package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.MainActivity;


import com.yahoo.mobile.intern.nest.adapter.MyDoneTaskAdapter;
import com.yahoo.mobile.intern.nest.adapter.MyNewTaskAdapter;

import com.yahoo.mobile.intern.nest.event.MyDoneTaskEvent;
import com.yahoo.mobile.intern.nest.event.MyNewTaskEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 8/15/15.
 */
public class FragmentBuyerTask extends FragmentTask {

    private int mType;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseObject> mList;
    private BaseAdapter mAdapter;

    private MainActivity activity;

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

    public void onEvent(MyNewTaskEvent event) {
        if(mType == Common.BUYER_NEW) {
            refreshList(event.questionList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void onEvent(MyDoneTaskEvent event) {
        if(mType == Common.BUYER_DONE) {
            refreshList(event.taskList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public static FragmentBuyerTask newInstance(int type) {
        FragmentBuyerTask fragment = new FragmentBuyerTask();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mType = args.getInt("type");
    }

    @Override
    public void getNewData() {
        if(mType == Common.BUYER_NEW) {
            ParseUtils.getMyNewTasks();
        }
        else if(mType == Common.BUYER_DONE) {
            ParseUtils.getMyDoneTasks();
        }
    }

    private void setupAdapter() {
        if(mType == Common.BUYER_NEW) {
            mAdapter = new MyNewTaskAdapter(getActivity(), mList);
        }
        else if(mType == Common.BUYER_DONE) {
            mAdapter = new MyDoneTaskAdapter(getActivity(), mList);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        mView = inflater.inflate(R.layout.fragment_my_new_task, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_my_task);
        mList = new ArrayList<>();
        setupAdapter();
        mListView.setAdapter(mAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewData();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject task = (ParseObject) mAdapter.getItem(position);
                Utils.gotoMyTaskActivity(getActivity(), task.getObjectId(), mType);
            }
        });
        getNewData();

        return mView;
    }
}
