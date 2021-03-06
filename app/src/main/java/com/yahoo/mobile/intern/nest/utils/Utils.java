package com.yahoo.mobile.intern.nest.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.activity.BSInfoSettingActivity;
import com.yahoo.mobile.intern.nest.activity.CatchTaskActivity;
import com.yahoo.mobile.intern.nest.activity.IMListActivity;
import com.yahoo.mobile.intern.nest.activity.LoginActivity;
import com.yahoo.mobile.intern.nest.activity.MainActivity;
import com.yahoo.mobile.intern.nest.activity.MapsActivity;
import com.yahoo.mobile.intern.nest.activity.MessagingActivity;
import com.yahoo.mobile.intern.nest.activity.MyTaskActivity;
import com.yahoo.mobile.intern.nest.activity.ProfileSettingActivity;
import com.yahoo.mobile.intern.nest.activity.SellerProfileActivity;
import com.yahoo.mobile.intern.nest.activity.SpinnerActivity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    static public String getRemainingTime(Date current, Date expireDate) {
        long duration = expireDate.getTime() - current.getTime();
        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        diffInDays = diffInDays > 0 ? diffInDays : 0;
        diffInHours = diffInHours > 0 ? diffInHours : 0;
        diffInMinutes = diffInMinutes > 0 ? diffInMinutes : 0;

        String ret = "";
        if(diffInDays == 0) {
            ret = String.format("%d小時%d分", diffInHours, diffInMinutes);
        }
        else {
            ret = String.format("%d日%d小時%d分", diffInDays, diffInHours, diffInMinutes);
        }

        return ret;
    }
    static public void showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    /*
     * Actionbar and statusbar setup
     */
    static public void setBuyerColor(AppCompatActivity activity) {
        setActionBarColor(activity, activity.getResources().getColor(R.color.nest_blue_2));
        setStatusBarColor(activity, activity.getResources().getColor(R.color.nest_blue_3));
    }
    static public void setSellerColor(AppCompatActivity activity) {
        setActionBarColor(activity, activity.getResources().getColor(R.color.nest_blue_2));
        setStatusBarColor(activity, activity.getResources().getColor(R.color.nest_blue_3));
    }
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

    static public void gotoMapsActivityCurLocation(Activity activity, LatLng latlng, String locationTitle) {
        Intent it = new Intent(activity, MapsActivity.class);
        it.putExtra(Common.EXTRA_HAS_PIN, true);
        it.putExtra(Common.EXTRA_LAT, latlng.latitude);
        it.putExtra(Common.EXTRA_LONG, latlng.longitude);
        it.putExtra(Common.EXTRA_TITLE, locationTitle);
        activity.startActivity(it);
    }

    static public void gotoMapsActivityForResult(Activity activity) {
        Intent it = new Intent(activity, MapsActivity.class);
        if(activity instanceof ProfileSettingActivity)
            it.putExtra(Common.EXTRA_SEEKBAR, true);
        else
            it.putExtra(Common.EXTRA_SEEKBAR,false);

        it.putExtra(Common.EXTRA_HAS_PIN,false);
        activity.startActivityForResult(it, Common.REQUEST_LOCATION);
    }
    static public void gotoProfileSettingActivity(Activity activity) {
        Intent it = new Intent(activity, ProfileSettingActivity.class);
        activity.startActivity(it);
    }
    static public void gotoBSInfoSettingActivity(Activity activity) {
        Intent it = new Intent(activity, BSInfoSettingActivity.class);
        activity.startActivity(it);
    }
    static public void gotoMyTaskActivity(Activity activity, String taskId, int type) {
        Intent it = new Intent(activity, MyTaskActivity.class);
        it.putExtra(Common.EXTRA_TYPE, type);
        it.putExtra(Common.EXTRA_TASK_ID, taskId);
        //activity.startActivity(it);
        activity.startActivityForResult(it, Common.REQUEST_MY_TASK);
    }
    static public void gotoCatchTaskActivity(Activity activity, String taskId, int state) {
        Intent it = new Intent(activity, CatchTaskActivity.class);
        it.putExtra(Common.EXTRA_TASK_ID, taskId);
        it.putExtra(Common.EXTRA_STATE, state);
        activity.startActivityForResult(it, Common.REQUEST_CATCH_TASK);
    }
    static public void gotoSellerProfileActivity(Activity activity, String userId, String taskId) {
        Intent it = new Intent(activity, SellerProfileActivity.class);
        it.putExtra(Common.EXTRA_USER_ID, userId);
        it.putExtra(Common.EXTRA_TASK_ID, taskId);
        activity.startActivity(it);
    }
    static public void gotoIMListActivity(Activity activity){
        Intent it = new Intent(activity, IMListActivity.class);
        activity.startActivity(it);
    }
    static public void gotoMessagingActivity(Activity activity, String recipientObjectId){
        Intent it = new Intent(activity, MessagingActivity.class);
        it.putExtra(Common.EXTRA_RECIPIENT_OBJECT_ID, recipientObjectId);
        activity.startActivity(it);
    }
    static public void gotoSpinnerActivity(Activity activity, String recipientObjectId){
        Intent it = new Intent(activity, SpinnerActivity.class);
        it.putExtra(Common.EXTRA_RECIPIENT_OBJECT_ID, recipientObjectId);
        activity.startActivity(it);
    }
}
