package com.yahoo.mobile.intern.nest.adapter;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.yahoo.mobile.intern.nest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dwkung on 8/17/15.
 */
public class MessageAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 0;

    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<WritableMessage, Integer>> mWritableMessages;
    private List<Date> mDateTime;
    private List<String> mSenderId;

    private SimpleDateFormat mFormatter;

    private LayoutInflater mInflater;

    public MessageAdapter(Activity activity) {
        mInflater = activity.getLayoutInflater();
        mWritableMessages = new ArrayList<Pair<WritableMessage, Integer>>();
        mDateTime = new ArrayList<>();
        mSenderId = new ArrayList<>();
        mFormatter = new SimpleDateFormat("HH:mm");
    }

    public void addMessage(WritableMessage writableMessage, int direction, Date dateTime, String senderId) {
        mWritableMessages.add(new Pair(writableMessage, direction));
        mDateTime.add(dateTime);
        mSenderId.add(senderId);
        notifyDataSetChanged();
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
        return 0;
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

        TextView txtSender = (TextView) convertView.findViewById(R.id.txtSender);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);

        txtSender.setText(name);
        txtMessage.setText(writableMessage.getTextBody());
        txtDate.setText(mFormatter.format(mDateTime.get(i)));

        return convertView;
    }
}