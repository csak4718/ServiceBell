package com.yahoo.mobile.intern.nest.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.MessagingActivity;
import com.yahoo.mobile.intern.nest.utils.Common;

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

    public void addMessage(WritableMessage msg, int direction, Date dateTime, String senderId, String messageId, boolean fromSendMessage, Bitmap fromSendMessageBmp) {
        if(!messageIdSet.contains(messageId)){
            if (!messageId.equals(Common.TEMP_MESSAGE_ID)) messageIdSet.add(messageId);

            String hasPic = msg.getTextBody().substring(0, 1);
            String text = msg.getTextBody().substring(1);
            WritableMessage writableMessage = new WritableMessage(msg.getRecipientIds().get(0), text);
            mWritableMessages.add(new Pair(writableMessage, direction));
            mDateTime.add(dateTime);
            mSenderId.add(senderId);

            if (hasPic.equals("T")){
                Log.d("is PICTURE", writableMessage.getTextBody());
                isPictureList.add(true); // is picture message

                if (fromSendMessage) bitMapList.add(fromSendMessageBmp);
                else{
                    List<ParseObject> msgList = new ArrayList<>();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
                    query.whereEqualTo("messageId", messageId);

                    while (msgList.size()!=1) {
                        Log.d("LOOP", "in While loop");
                        try {
                            msgList = query.find();
                            Log.d("IN_ADD_MESSAGE", String.valueOf(msgList.size()));
                        } catch (ParseException e) {

                        }
                    }

                    ParseFile msgImage = (ParseFile) msgList.get(0).get("picture");
                    try {
                        byte[] bytes = msgImage.getData();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);
                        if (bmp != null) {
                            bitMapList.add(bmp);
                            notifyDataSetChanged();
                        }
                    }
                    catch (ParseException e){

                    }

//                    query.findInBackground(new FindCallback<ParseObject>() {
//                        public void done(List<ParseObject> messageList, ParseException e) {
//                            if (e == null) {
//                                Log.d("IN_ADD_MESSAGE", String.valueOf(messageList.size()));
//
//
//                                ParseFile msgImage = (ParseFile) messageList.get(0).get("picture");
//                                msgImage.getDataInBackground(new GetDataCallback() {
//                                    @Override
//                                    public void done(byte[] bytes, ParseException err) {
//                                        if (err == null) {
//                                            // bytes has the bytes for the msgImage
//                                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
//                                                    bytes.length);
//                                            if (bmp != null) {
//                                                bitMapList.add(bmp);
//                                                notifyDataSetChanged();
//                                            }
//                                        } else {
//                                            // something went wrong
//                                        }
//                                    }
//                                });
//
//                            } else {
//                                Log.d("score", "Error: " + e.getMessage());
//                            }
//                        }
//                    });
                }
            }
            else {
                Log.d("is TEXT", writableMessage.getTextBody());
                isPictureList.add(false); // is text message
                Bitmap trivialBitmap = BitmapFactory.decodeResource(messageActivity.getResources(), R.drawable.ic_add_black_24dp);
                bitMapList.add(trivialBitmap);
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
//        String name = mSenderId.get(i);
        boolean isPicture = isPictureList.get(i);

//        final TextView txtSender = (TextView) convertView.findViewById(R.id.txtSender);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        final ImageView imgMessage = (ImageView) convertView.findViewById(R.id.imgMessage);
//        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
        final ImageView imgSender = (ImageView) convertView.findViewById(R.id.img_pic);


        if (isPicture){
            txtMessage.setVisibility(View.GONE);

            if (bitMapList.get(i).getHeight() > bitMapList.get(i).getWidth() + 20) {
                imgMessage.requestLayout();
                imgMessage.getLayoutParams().height = 450;
                imgMessage.getLayoutParams().width = 350;
            }
            else if (bitMapList.get(i).getWidth() > bitMapList.get(i).getHeight() + 20){
                imgMessage.requestLayout();
                imgMessage.getLayoutParams().height = 350;
                imgMessage.getLayoutParams().width = 450;
            }
            imgMessage.setImageBitmap(bitMapList.get(i));
            imgMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(messageActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_image);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;


                    Bitmap bmp = ((BitmapDrawable) imgMessage.getDrawable())
                            .getBitmap();

                    ImageView picture = (ImageView) dialog.findViewById(R.id.img_view_dialog_picture);
                    ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.img_btn_close);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    picture.setImageBitmap(bmp);
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor
                            ("#80000000")));
                }
            });

            imgMessage.setVisibility(View.VISIBLE);
        }
        else {
            imgMessage.setVisibility(View.GONE);
            txtMessage.setText(writableMessage.getTextBody());
            txtMessage.setVisibility(View.VISIBLE);
        }

//        txtDate.setText(mFormatter.format(mDateTime.get(i)));
        if (!mSenderId.get(i).equals(ParseUser.getCurrentUser().getObjectId())) {

            final TextView txtName = (TextView) convertView.findViewById(R.id.txtName);

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(mSenderId.get(i), new GetCallback<ParseUser>() {
                public void done(ParseUser sender, ParseException e) {
                    if (e == null) {
                        // The query was successful.
//                    txtSender.setText(sender.getString("nickname"));
                        txtName.setText(sender.getString(Common.OBJECT_USER_NICK));
                        ParseFile imgFile = sender.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                        Picasso.with(messageActivity)
                                .load(imgFile.getUrl())
                                .into(imgSender);


                    } else {
                        // Something went wrong.
                    }
                }
            });
        }

        return convertView;
    }


}