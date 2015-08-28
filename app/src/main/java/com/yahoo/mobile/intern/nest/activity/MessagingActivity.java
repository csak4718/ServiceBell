package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.adapter.MessageAdapter;
import com.yahoo.mobile.intern.nest.event.RecipientEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class MessagingActivity extends BaseActivity implements MessageClientListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();
    private MessageAdapter mMessageAdapter;
    private TextView recipientNickname;
    private EditText mTxtTextBody;
    private ImageButton mBtnSend;
    private ParseUser currentUser;
    private ParseUser recipient;
    private String recipientObjectId;
    private boolean afterLoadHistory = false;




    ImageButton imgBtnCamera;
    ImageButton imgBtnPicture;
    ImageButton imgBtnPreviewDelete;
    boolean postWithPicture = false;

    public static final int CAMERA_REQUEST = 12345;
    public static final int ACTIVITY_SELECT_IMAGE = 1234;
    FrameLayout imgPreviewRoot;
    ImageView imgViewUpload;
    Uri mImageUri;
    Bitmap bitmap = null;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);
        afterLoadHistory = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipientNickname = (TextView) findViewById(R.id.recipient_nickname);

        imgBtnCamera = (ImageButton) findViewById(R.id.img_btn_camera);
        imgBtnPicture = (ImageButton) findViewById(R.id.img_btn_picture);
        imgPreviewRoot = (FrameLayout) findViewById(R.id.img_preview_root);
        imgViewUpload = (ImageView) findViewById(R.id.img_view_upload);
        imgBtnPreviewDelete = (ImageButton) findViewById(R.id.img_btn_preview_delete);

        currentUser = ParseUser.getCurrentUser();
        Intent it = getIntent();
        recipientObjectId = it.getStringExtra(Common.EXTRA_RECIPIENT_OBJECT_ID);
        ParseUtils.getRecipient(recipientObjectId);


        mTxtTextBody = (EditText) findViewById(R.id.txtTextBody);

        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.lstMessages);
        messagesList.setAdapter(mMessageAdapter);

        mBtnSend = (ImageButton) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        imgBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromCamera();
            }
        });
        imgBtnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromGallery();
            }
        });
        imgBtnPreviewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postWithPicture = false;
                imgPreviewRoot.setVisibility(View.GONE);
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
                            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, messageList.get(i).getDate("msgTimeStamp"), messageList.get(i).getString("senderId"), messageList.get(i).getString("messageId"));
                        } else {
                            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING, messageList.get(i).getDate("msgTimeStamp"), messageList.get(i).getString("senderId"), messageList.get(i).getString("messageId"));
                        }
                    }
                    afterLoadHistory = true;
                }
            }
        });
    }

    @Override
    public void onStart() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessagingActivity", true);
        installation.saveInBackground();

        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessagingActivity", true);
        installation.saveInBackground();

        super.onResume();
    }

    @Override
    public void onPause() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessagingActivity", false);
        installation.saveInBackground();

        super.onPause();
    }

    @Override
    public void onStop() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessagingActivity", false);
        installation.saveInBackground();

        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(RecipientEvent event){
        recipient = event.recipient;
        recipientNickname.setText(recipient.getString("nickname"));
    }

    @Override
    public void onDestroy() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessagingActivity", false);
        installation.saveInBackground();

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
        String textBody;

        if (recipientObjectId.isEmpty()) {
            Toast.makeText(this, "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }

        if(postWithPicture) {
            bitmap = ((BitmapDrawable) imgViewUpload.getDrawable()).getBitmap();
//            +mImageUri.toURL();
            try{
                URI mImageURI =new URI(mImageUri.toString());

            }
            catch (URISyntaxException e){
                Log.d("URI", "URISyntaxException");
            }

            textBody = "T";
        }
        else {
            textBody = "F" + mTxtTextBody.getText().toString();
        }

        if (textBody.isEmpty()) {
            Toast.makeText(this, "No text message or picture uri string", Toast.LENGTH_SHORT).show();
            return;
        }

        Date temporaryDate=new Date(1111);

        WritableMessage writableMessage = new WritableMessage(recipientObjectId, textBody);

        mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, temporaryDate, currentUser.getObjectId(), Common.TEMP_MESSAGE_ID);

        getSinchServiceInterface().sendMessage(recipientObjectId, textBody);
        mTxtTextBody.setText("");
        postWithPicture = false; // reset
        imgPreviewRoot.setVisibility(View.GONE); // reset
    }

    private void setButtonEnabled(boolean enabled) {
        mBtnSend.setEnabled(enabled);
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        if (message.getSenderId().equals(recipientObjectId) && afterLoadHistory) {

            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
//            Log.d("ON_INCOMING_MSG", writableMessage.getTextBody());
            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING, message.getTimestamp(), message.getSenderId(), message.getMessageId());
        }
    }

    @Override
    public void onMessageSent(MessageClient client, final Message message, String recipientId) {
        final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

        // only add message to parse database if it doesn't already exist there
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
                        msg.put("messageId", message.getMessageId());
                        msg.put("msgTimeStamp", message.getTimestamp());
                        if (bitmap != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] bytearray = stream.toByteArray();
                            ParseFile messagePicture = new ParseFile(message.getMessageId() + "_picture.jpg", bytearray);
                            try {
                                messagePicture.save();
                                msg.put("picture", messagePicture);
                                msg.save();
                                bitmap = null; // reset
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        } else {
                            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] bytearray = stream.toByteArray();
                            ParseFile messagePicture = new ParseFile(message.getMessageId() + "_picture.jpg", bytearray);
                            try {
                                messagePicture.save();
                                msg.put("picture", messagePicture);
                                msg.save();
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }

                        ParseUtils.instantMessageNotification(currentUser, recipient);
//                        Log.d("ON_MESSAGE_SENT", writableMessage.getTextBody());
//                        mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, message.getTimestamp(), currentUser.getObjectId(), message.getMessageId());
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





    // For changing camera_btn img
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null &&  resultCode == RESULT_OK && requestCode == MessagingActivity.ACTIVITY_SELECT_IMAGE) {
            Uri selectedImageUri = data.getData();
            setImgViewUpload(selectedImageUri);
        }
        else if(resultCode == RESULT_OK && requestCode == MessagingActivity.CAMERA_REQUEST) {
            setImgViewUpload();
        }
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= getExternalCacheDir();
        return File.createTempFile(part, ext, tempDir);
    }

    private void getPictureFromCamera() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photo;
        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
            mImageUri = Uri.fromFile(photo);
            i.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(i, CAMERA_REQUEST);
        }
        catch(Exception e)
        {
            Log.v("nest", "Can't create file to take picture!");
            Toast.makeText(this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show();
        }
    }
    private void getPictureFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    public void setImgViewUpload(Uri uri) {
        mImageUri = uri;

        Picasso.with(this)
                .load(mImageUri)
                .resize(640, 480)
                .centerInside()
                .into(imgViewUpload);
        imgPreviewRoot.setVisibility(View.VISIBLE);
        postWithPicture = true;
    }

    public void setImgViewUpload() {

        getContentResolver().notifyChange(mImageUri, null);

        Picasso.with(this)
                .load(mImageUri)
                .resize(640, 480)
                .centerInside()
                .into(imgViewUpload);
        imgPreviewRoot.setVisibility(View.VISIBLE);
        postWithPicture = true;
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
