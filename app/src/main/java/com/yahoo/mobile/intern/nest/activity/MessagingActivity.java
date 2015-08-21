package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.MessageAdapter;
import com.yahoo.mobile.intern.nest.event.RecipientEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

public class MessagingActivity extends BaseActivity implements MessageClientListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();

    private MessageAdapter mMessageAdapter;
    private TextView recipientNickname;
    private EditText mTxtTextBody;
    private Button mBtnSend;
    private ParseUser currentUser;
    private ParseUser recipient;
    private String recipientObjectId;
//    private boolean afterLoadMessageHistory;
//    private Set<String> messageIdHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        afterLoadMessageHistory = false;
//        messageIdHistory = new HashSet<>();

        recipientNickname = (TextView) findViewById(R.id.recipient_nickname);

        currentUser = ParseUser.getCurrentUser();
        Intent it = getIntent();
        recipientObjectId = it.getStringExtra(Common.EXTRA_RECIPIENT_OBJECT_ID);
        ParseUtils.getRecipient(recipientObjectId);


        mTxtTextBody = (EditText) findViewById(R.id.txtTextBody);

        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.lstMessages);
        messagesList.setAdapter(mMessageAdapter);

        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });



        String[] userIds = {currentUser.getObjectId(), recipientObjectId};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereContainedIn("senderId", Arrays.asList(userIds));
        query.whereContainedIn("recipientId", Arrays.asList(userIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        WritableMessage writableMessage = new WritableMessage(messageList.get(i).get("recipientId").toString(), messageList.get(i).get("messageText").toString());
                        if (messageList.get(i).get("senderId").toString().equals(currentUser.getObjectId())) {
                            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, messageList.get(i).getDate("msgTimeStamp"), messageList.get(i).get("senderId").toString());
                        } else {
                            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING, messageList.get(i).getDate("msgTimeStamp"), messageList.get(i).get("senderId").toString());
                        }
                    }
//                    afterLoadMessageHistory = true;
                }
            }
        });
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(RecipientEvent event){
        recipient = event.recipient;
        recipientNickname.setText(recipient.getString("nickname"));
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().removeMessageClientListener(this);
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceConnected() {
        getSinchServiceInterface().addMessageClientListener(this);
        setButtonEnabled(true);
    }

    @Override
    public void onServiceDisconnected() {
        setButtonEnabled(false);
    }

    private void sendMessage() {

        String textBody = mTxtTextBody.getText().toString();
        if (recipientObjectId.isEmpty()) {
            Toast.makeText(this, "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textBody.isEmpty()) {
            Toast.makeText(this, "No text message", Toast.LENGTH_SHORT).show();
            return;
        }

        getSinchServiceInterface().sendMessage(recipientObjectId, textBody);
        mTxtTextBody.setText("");
    }

    private void setButtonEnabled(boolean enabled) {
        mBtnSend.setEnabled(enabled);
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {

        if (message.getSenderId().equals(recipientObjectId)) { // && afterLoadMessageHistory
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING, message.getTimestamp(), message.getSenderId());
        }

    }

    @Override
    public void onMessageSent(MessageClient client, final Message message, String recipientId) {
        final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

        //only add message to parse database if it doesn't already exist there
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("messageId", message.getMessageId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    if (messageList.size() == 0) {
                        ParseObject msg = new ParseObject("Message");
                        msg.put("senderId", currentUser.getObjectId());
                        msg.put("recipientId", writableMessage.getRecipientIds().get(0));
                        msg.put("messageText", writableMessage.getTextBody());
                        msg.put("messageId", writableMessage.getMessageId());
                        msg.put("msgTimeStamp", message.getTimestamp());
                        msg.saveInBackground();
                        mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, message.getTimestamp(), currentUser.getObjectId());
                    }
                }
            }
        });

        ParseUtils.createChatConnection(currentUser, recipient);
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d(TAG, "onDelivered");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
