package com.yahoo.mobile.intern.nest.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.utils.Common;

/**
 * Created by cmwang on 8/12/15.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "7daNmGHwHg9PHbM8lfVQhJtvHlnz59CFU55VGZqf", "5SjFWAiFgQUbePYCUy35R98XbxzhWZ5Rhpuy1wEe");
        ParseFacebookUtils.initialize(this);

        ParsePush.subscribeInBackground("");
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        if(ParseUser.getCurrentUser() != null)
            installation.put(Common.INSTALLATION_USER, ParseUser.getCurrentUser());
        installation.saveInBackground();

    }

}
