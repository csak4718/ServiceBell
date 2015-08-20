package com.yahoo.mobile.intern.nest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.event.FbPictureEvent;
import com.yahoo.mobile.intern.nest.event.UserProfileEvent;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.FbUtils;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class LoginActivity extends AppCompatActivity {

    private Button mBtnLoginFacebook;
    private ImageView mImgSplashLogo;
    private Handler mHandler = new Handler();
    private String mNickName;
    private String mFbId;

    @Bind(R.id.progressbar_login) ProgressBar mProgressBar;

    private void setupLoginButton() {

        mBtnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                final List<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
//              permissions.add("user_status");
                permissions.add("user_friends");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else {
                            if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                                FbUtils.getUserProfile(AccessToken.getCurrentAccessToken());
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                Utils.gotoMainActivity(LoginActivity.this);
                            }

                        }
                    }
                });
            }
        });
    }

    private void showLoginAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -200);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBtnLoginFacebook.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImgSplashLogo.setAnimation(animation);

        animation.startNow();
    }
    public void onEvent(UserProfileEvent event) {
        mNickName = event.mNickName;
        mFbId = event.mFbId;
        FbUtils.getFbProfilePicture(mFbId);
    }
    public void onEvent(final FbPictureEvent event) {
        ParseUser.getCurrentUser().put(Common.OBJECT_USER_FB_NAME, mNickName);
        Map<String,String> profile = new HashMap<String,String>();
        profile.put(Common.OBJECT_USER_NICK, mNickName);
        ParseUtils.updateUserProfile(profile, mFbId, event.mPic);
        Utils.gotoMainActivity(this);
        finish();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
        mImgSplashLogo = (ImageView) findViewById(R.id.img_splash_logo);

        if(ParseUser.getCurrentUser() == null) {
            setupLoginButton();
            showLoginAnimation();
        }
        else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.gotoMainActivity(LoginActivity.this);
                }
            }, 700);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
