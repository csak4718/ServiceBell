package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.ViewPagerAdapter;
import com.yahoo.mobile.intern.nest.utils.Common;

/**
 * Created by ytli on 8/15/15.
 */
public class FragmentTab extends Fragment {
    private View mView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    static public FragmentTab newInstance(int id) {
        FragmentTab fragment = new FragmentTab();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupAdapter(int id) {
        if(id == R.id.menu_my_task) {
            adapter.addFrag(FragmentBuyerTask.newInstance(Common.BUYER_NEW), "等待中任務");
            adapter.addFrag(FragmentBuyerTask.newInstance(Common.BUYER_DONE), "已成交任務");
        }
        else if(id == R.id.menu_catch_task) {
            adapter.addFrag(FragmentSellerTask.newInstance(Common.SELLER_NEW), "New");
            adapter.addFrag(FragmentSellerTask.newInstance(Common.SELLER_ACCEPTED), "Accepted");
            adapter.addFrag(FragmentSellerTask.newInstance(Common.SELLER_DONE), "Done");
        }
    }

    public void refreshAllTab() {
        adapter.refreshAllTabs();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        int id = args.getInt("id");
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        setupAdapter(id);
    }

    public void switchTab(int id) {
        adapter.clear();
        setupAdapter(id);
        adapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
