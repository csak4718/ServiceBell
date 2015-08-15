package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.MainActivity;
import com.yahoo.mobile.intern.nest.adapter.QuestionCardAdapter;
import com.yahoo.mobile.intern.nest.event.QuestionEvent;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 8/15/15.
 */
public class FragmentMyNewTask extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseObject> mList;
    private QuestionCardAdapter mAdapter;

    private FloatingActionButton btnAddTask;

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

    public void onEvent(QuestionEvent event) {
        Log.d("eventbus", "" + event.questionList.size());
        refreshList(event.questionList);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void refreshList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public static FragmentMyNewTask newInstance() {
        FragmentMyNewTask fragment = new FragmentMyNewTask();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        mView = inflater.inflate(R.layout.fragment_my_new_task, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_my_task);
        mList = new ArrayList<>();
        mAdapter = new QuestionCardAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);

        btnAddTask = (FloatingActionButton) mView.findViewById(R.id.btn_add_post);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParseUtils.getAllQuestions();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoAddTaskActivity(getActivity());
            }
        });

        ParseUtils.getAllQuestions();

        return mView;
    }
}
