package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.adapter.CategoryAdapter;
import com.yahoo.mobile.intern.nest.item.CategoryItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cmwang on 8/21/15.
 */
public class FragmentAddChooseCategory extends Fragment {

    private View mView;
    private AddTaskActivity activity;
    @Bind(R.id.listview_catogory) ListView mListView;
    private CategoryAdapter mAdapter;
    private List<CategoryItem> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (AddTaskActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_add_choose_tag, container, false);
        ButterKnife.bind(this, mView);

        mList = new ArrayList<>(Arrays.asList(
                new CategoryItem(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp), "居家清潔/整理"),
                new CategoryItem(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp), "汽車保養/美容"),
                new CategoryItem(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp), "美容/美甲"),
                new CategoryItem(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp), "育兒服務"),
                new CategoryItem(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp), "寵物服務"),
                new CategoryItem(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp), "其他")
        ));
        mAdapter = new CategoryAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.category = mList.get(position).mTitle;
                FragmentAddContent fragment = new FragmentAddContent();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_content, fragment)
                        .commit();
            }
        });
        return mView;
    }
}
