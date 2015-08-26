package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.adapter.CategoryAdapter;
import com.yahoo.mobile.intern.nest.event.CategoryEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 8/21/15.
 */
public class FragmentAddChooseCategory extends Fragment {

    private View mView;
    private AddTaskActivity activity;
    @Bind(R.id.listview_catogory) ListView mListView;
    private CategoryAdapter mAdapter;
    private List<ParseObject> mCategory = new ArrayList<ParseObject>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (AddTaskActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_add_choose_tag, container, false);
        ButterKnife.bind(this, mView);
        ParseUtils.getCategoryList();

        /*
        mList = new ArrayList<>(Arrays.asList(
                new CategoryItem(0, "居家清潔/整理"),
                new CategoryItem(1, "汽車保養/美容"),
                new CategoryItem(2, "美容/美甲"),
                new CategoryItem(3, "育兒服務"),
                new CategoryItem(4, "寵物服務"),
                new CategoryItem(5, "其他")
        ));
        */

        return mView;
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

    public void onEvent(final CategoryEvent event){
        Log.d("eventbus", "get category");
        mCategory.addAll(event.categoryList);

        mAdapter = new CategoryAdapter(getActivity(), mCategory);
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.category = mCategory.get(position).getString(Common.OBJECT_CATEGORY_TITLE);
                FragmentAddContent fragment = new FragmentAddContent();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_content, fragment)
                        .commit();
            }
        });
    }

}
