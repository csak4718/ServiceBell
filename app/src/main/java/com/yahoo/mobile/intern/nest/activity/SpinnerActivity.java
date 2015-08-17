package com.yahoo.mobile.intern.nest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.yahoo.mobile.intern.nest.R;

public class SpinnerActivity extends BaseActivity implements SinchService.StartFailedListener {
    private Button btnToMessaging;
    private ProgressDialog mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);

        btnToMessaging = (Button) findViewById(R.id.btn_to_Messaging);
        btnToMessaging.setEnabled(false);
        btnToMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToMessagingClicked();
            }
        });
    }




    @Override
    protected void onServiceConnected() {
        btnToMessaging.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }



    // implements SinchService.StartFailedListener functions
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openMessagingActivity();
    }




    private void btnToMessagingClicked() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        String userName = currentUser.getObjectId();

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            openMessagingActivity();
        }
    }

    private void openMessagingActivity() {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        startActivity(messagingActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }






//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_spinner, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
