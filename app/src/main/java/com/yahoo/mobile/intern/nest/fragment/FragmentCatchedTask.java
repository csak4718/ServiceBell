package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.QuestionCardAdapter;
import com.yahoo.mobile.intern.nest.event.CatchTaskEvent;
import com.yahoo.mobile.intern.nest.event.MyTaskEvent;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 8/15/15.
 */
public class FragmentCatchedTask extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseObject> mList;
    private QuestionCardAdapter mAdapter;

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
        Log.d("eventbus", "" + event.taskList.size());
        refreshList(event.taskList);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void refreshList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_catched_task, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_my_task);
        mList = new ArrayList<>();
        mAdapter = new QuestionCardAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ParseUtils.getCatchedTasks();

        return mView;
    }
}
