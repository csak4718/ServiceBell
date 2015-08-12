package com.yahoo.mobile.intern.nest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

import com.yahoo.mobile.intern.nest.activity.MainActivity;

/**
 * Created by cmwang on 8/12/15.
 */
public class Utils {
    static public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    /*
     * goto activity helper function
     */
    static public void gotoMainActivity(Context context) {
        Intent it = new Intent(context, MainActivity.class);
        context.startActivity(it);
    }
}
