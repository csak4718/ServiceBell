package com.yahoo.mobile.intern.nest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.activity.LoginActivity;
import com.yahoo.mobile.intern.nest.activity.MainActivity;
import com.yahoo.mobile.intern.nest.activity.MapsActivity;
import com.yahoo.mobile.intern.nest.activity.ProfileSettingActivity;

/**
 * Created by cmwang on 8/12/15.
 */
public class Utils {
    /*
     * Misc helper function
     */
    static public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    static public void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }
    /*
     * Actionbar and statusbar setup
     */
    static public void setActionBarColor(AppCompatActivity activity, int color) {
        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }
    static public void setStatusBarColor(AppCompatActivity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
    /*
     * goto activity helper function
     */
    static public void gotoMainActivity(Context context) {
        Intent it = new Intent(context, MainActivity.class);
        context.startActivity(it);
    }
    static public void gotoLoginActivity(Context context) {
        Intent it = new Intent(context, LoginActivity.class);
        context.startActivity(it);
    }
    static public void gotoAddTaskActivity(Context context) {
        Intent it = new Intent(context, AddTaskActivity.class);
        context.startActivity(it);
    }
    static public void gotoMapsActivityForResult(Activity activity) {
        Intent it = new Intent(activity, MapsActivity.class);
        activity.startActivityForResult(it, Common.REQUEST_LOCATION);
    }
    static public void gotoProfileSettingActivity(Activity activity) {
        Intent it = new Intent(activity, ProfileSettingActivity.class);
        activity.startActivity(it);
    }
}
