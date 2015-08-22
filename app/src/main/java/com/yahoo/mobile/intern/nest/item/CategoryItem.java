package com.yahoo.mobile.intern.nest.item;

import android.graphics.drawable.Drawable;

/**
 * Created by cmwang on 8/21/15.
 */
public class CategoryItem {
    public Drawable mIcon;
    public String mTitle;
    public CategoryItem(Drawable icon, String title) {
        mIcon = icon;
        mTitle = title;
    }
}
