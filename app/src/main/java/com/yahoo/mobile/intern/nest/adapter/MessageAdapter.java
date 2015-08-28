package com.yahoo.mobile.intern.nest.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dwkung on 8/17/15.
 */
public class MessageAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 0;

    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<WritableMessage, Integer>> mWritableMessages;
    private List<Date> mDateTime;
    private List<String> mSenderId;
    private List<Boolean> isPictureList;
    private List<Bitmap> bitMapList;
    private Activity messageActivity;

    private Set<String> messageIdSet;

    private SimpleDateFormat mFormatter;

    private LayoutInflater mInflater;

    public MessageAdapter(Activity activity) {
        messageActivity = activity;
        mInflater = activity.getLayoutInflater();
        mWritableMessages = new ArrayList<Pair<WritableMessage, Integer>>();
        mDateTime = new ArrayList<>();
        mSenderId = new ArrayList<>();
        isPictureList = new ArrayList<>();
        bitMapList = new ArrayList<>();
        messageIdSet = new HashSet<>();
        mFormatter = new SimpleDateFormat("HH:mm");
    }

    public void addMessage(WritableMessage msg, int direction, Date dateTime, String senderId, String messageId) {
        if(!messageIdSet.contains(messageId)){
            messageIdSet.add(messageId);

            String hasPic = msg.getTextBody().substring(0, 1);
            String text = msg.getTextBody().substring(1);
            WritableMessage writableMessage = new WritableMessage(msg.getRecipientIds().get(0), text);
            mWritableMessages.add(new Pair(writableMessage, direction));
            mDateTime.add(dateTime);
            mSenderId.add(senderId);

            if (hasPic.equals("T")){
                Log.d("is PICTURE", writableMessage.getTextBody());
                isPictureList.add(true); // is picture message

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
                query.whereEqualTo("messageId", messageId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> messageList, ParseException e) {
                        if (e == null) {
                            ParseFile msgImage = (ParseFile) messageList.get(0).get("picture");
                            msgImage.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException err) {
                                    if (err == null) {
                                        // bytes has the bytes for the msgImage
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                                                bytes.length);
                                        if (bmp != null) {
                                            bitMapList.add(bmp);
                                            notifyDataSetChanged();
                                        }
                                    } else {
                                        // something went wrong
                                    }
                                }
                            });

                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });
            }
            else {
                Log.d("is TEXT", writableMessage.getTextBody());
                isPictureList.add(false); // is text message
                Bitmap bmp = BitmapFactory.decodeResource(messageActivity.getResources(), R.drawable.ic_add_black_24dp);
                bitMapList.add(bmp);
                notifyDataSetChanged();
            }

        }
    }

    @Override
    public int getCount() {
        return mWritableMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return mWritableMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mWritableMessages.get(i).hashCode();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return mWritableMessages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);

        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_left;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_right;
            }
            convertView = mInflater.inflate(res, viewGroup, false);
        }

        WritableMessage writableMessage = mWritableMessages.get(i).first;
        String name = mSenderId.get(i);
        boolean isPicture = isPictureList.get(i);

        TextView txtSender = (TextView) convertView.findViewById(R.id.txtSender);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        ImageView imgMessage = (ImageView) convertView.findViewById(R.id.imgMessage);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);


        txtSender.setText(name);
        if (isPicture){
            txtMessage.setVisibility(View.GONE);
            imgMessage.setImageBitmap(bitMapList.get(i));
            imgMessage.setVisibility(View.VISIBLE);
        }
        else {
            imgMessage.setVisibility(View.GONE);
            txtMessage.setText(writableMessage.getTextBody());
            txtMessage.setVisibility(View.VISIBLE);
        }

        txtDate.setText(mFormatter.format(mDateTime.get(i)));

        return convertView;
    }


}