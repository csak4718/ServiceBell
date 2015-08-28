package com.yahoo.mobile.intern.nest.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fanwang on 8/27/15.
 */

public class MyReceiver extends ParsePushBroadcastReceiver {

    protected void onPushReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));


            //if (action.equalsIgnoreCase("com.packagename.UPDATE_STATUS")) {
            String title = "appname";
            if (json.has("title"))
                title = json.getString("title");

            String content = "mycontent";
            if (json.has("alert"))
                content = json.getString("alert");

            Uri uri = Uri.parse("nest://");
            if (json.has("uri"))
                uri = Uri.parse(json.getString("uri"));
            Log.d("uri",""+uri);

            generateNotification(context, title, uri, content);

        } catch (JSONException e) {
            Log.d("incomingreceiver", "JSONException: " + e.getMessage());
        }
    }

    private void generateNotification(Context context, String title, Uri uri, String contentText) {
        Log.d("incomingreceiver", "generate notification");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri, context, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);


        Uri ringUri = Uri.parse("android.resource://com.yahoo.mobile.intern.nest/raw/ding.mp3");
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo_2)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{1000,1000,1000})
                .setSound(ringUri);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());

    }
}