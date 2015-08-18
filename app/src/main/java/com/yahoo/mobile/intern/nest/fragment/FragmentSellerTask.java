package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;



//import com.yahoo.mobile.intern.nest.activity.SinchService;
//import com.yahoo.mobile.intern.nest.adapter.QuestionCardAdapter;





import com.yahoo.mobile.intern.nest.adapter.CatchTaskAdapter;
//import com.yahoo.mobile.intern.nest.adapter.MyTaskAdapter;


import com.yahoo.mobile.intern.nest.event.AcceptTaskEvent;
import com.yahoo.mobile.intern.nest.event.CatchTaskEvent;
import com.yahoo.mobile.intern.nest.event.DoneTaskEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 8/15/15.
 */
public class FragmentSellerTask extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseObject> mList;
    private CatchTaskAdapter mAdapter;

    private int mType;

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

    public void onEvent(CatchTaskEvent event) {
        if(mType == Common.SELLER_NEW) {
            refreshList(event.taskList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void onEvent(AcceptTaskEvent event) {
        if(mType == Common.SELLER_ACCEPTED) {
            refreshList(event.taskList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void onEvent(DoneTaskEvent event) {
        if(mType == Common.SELLER_DONE) {
            refreshList(event.taskList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public static FragmentSellerTask newInstance(int type) {
        FragmentSellerTask fragment = new FragmentSellerTask();
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

    private void getNewData() {
        if(mType == Common.SELLER_NEW) {
            ParseUtils.getCatchedTasks();
        }
        else if(mType == Common.SELLER_ACCEPTED) {
            ParseUtils.getAcceptedTasks();
        }
        else if(mType == Common.SELLER_DONE) {
            ParseUtils.getDoneTasks();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_catched_task, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_my_task);
        mList = new ArrayList<>();
        mAdapter = new CatchTaskAdapter(getActivity(), mList);
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
                Utils.gotoCatchTaskActivity(getActivity(), task.getObjectId());
            }
        });

        getNewData();

        return mView;
    }
}
