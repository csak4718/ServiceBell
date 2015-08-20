package com.yahoo.mobile.intern.nest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.yahoo.mobile.intern.nest.fragment.FragmentTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ytli on 8/15/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<FragmentTask> mFragmentList;
    private List<String> mFragmentTitleList;

    public void refreshAllTabs() {
        for(FragmentTask fragmentTask : mFragmentList) {
            fragmentTask.getNewData();
        }
    }

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        mFragmentList = new ArrayList<>();
        mFragmentTitleList = new ArrayList<>();
    }

    @Override
    public FragmentTask getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return mFragmentList.get(position).hashCode();
    }

    public void addFrag(FragmentTask fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void clear() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}