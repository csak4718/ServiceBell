package com.yahoo.mobile.intern.nest.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.SinchService;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;
import com.yahoo.mobile.intern.nest.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ytli on 8/18/15.
 */
public class DialogFragmentSellerProfile extends DialogFragment implements SinchService.StartFailedListener, ServiceConnection {
    private SinchService.SinchServiceInterface mSinchServiceInterface;
    private ProgressDialog mSpinner;
    private Activity mActivity;

    ParseUser user;
    Boolean done,buyer;
    View mView;
    int type;

    @Bind(R.id.txt_firstSentence)TextView txtFirstSentence;
    @Bind(R.id.img_pic)CircleImageView mImgProfilePic;
    @Bind(R.id.txt_name)TextView txtName;
    @Bind(R.id.txt_address)TextView txtAdd;
    @Bind(R.id.txt_others)TextView txtOthers;
    @Bind(R.id.txt_phone)TextView txtPhone;
    @Bind(R.id.btn_im)Button btnIM;
    @Bind(R.id.btn_divider)View divider;
    @Bind(R.id.btn_confirm)Button btnConfirm;
    @Bind(R.id.linear_btn)LinearLayout linearBtn;
    @Bind(R.id.ratingBar)RatingBar ratingBar;
    @Bind(R.id.txt_category)TextView txtCategory;

    @OnClick(R.id.btn_confirm) void confirm(){
        ConfirmDialog cd = ConfirmDialog.newInstance(txtName.getText().toString(),mImgProfilePic.getDrawable(),ratingBar.getRating(),user);
        cd.show(getFragmentManager(),"123");
        this.dismiss();
    }

    @OnClick(R.id.btn_im) void im(){
        btnToMessagingClicked();
    }


    public static DialogFragmentSellerProfile newInstance(Activity activity, ParseUser user,int type){
        DialogFragmentSellerProfile dfsp = new DialogFragmentSellerProfile();
        dfsp.user = user;
        dfsp.type = type;
        dfsp.mActivity = activity;
        return dfsp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.getApplicationContext().bindService(new Intent(mActivity, SinchService.class), this,
                mActivity.getApplicationContext().BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_seller_profile, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, mView);
        setupButton();
        getProfile();
        return mView;
    }

    public void getProfile(){
        String nickName,phone, address, others ="";
        nickName = user.getString(Common.OBJECT_USER_NICK);
        phone = user.getString(Common.OBJECT_USER_PHONE);
        address = user.getString(Common.OBJECT_USER_ADDRESS);
        others = user.getString(Common.OBJECT_USER_OTHERS);

        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        ParseUtils.displayParseImage(imgFile, mImgProfilePic);
        if (nickName == null || nickName.equals("")){txtName.setVisibility(View.GONE);}else{txtName.setText(nickName);}
        if (address == null || address.equals("")) {txtAdd.setVisibility(View.GONE);}else{txtAdd.setText(address);}
        if (phone == null || phone.equals("")) {txtPhone.setVisibility(View.GONE);}else{txtPhone.setText(phone);}
        if (others == null || others.equals("")){txtOthers.setVisibility(View.GONE);}else{txtOthers.setText(others);}
        ratingBar.setRating(user.getNumber(Common.OBJECT_USER_RATING).floatValue());
        setCategory();
    }
    public void setupButton(){

        Log.d("test",String.valueOf(done));
        if(type!=Common.BUYER_NEW){
            btnConfirm.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if((type!=Common.BUYER_DONE)&&(type!=Common.BUYER_NEW)){
            txtFirstSentence.setVisibility(View.GONE);
        }
        disableButton();
    }
    public void disableButton(){
        if(type==Common.SELLER_NEW||type==Common.SELLER_ACCEPTED||type==Common.SELLER_ACCEPTED){
            linearBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    // implements SinchService.StartFailedListener functions
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        Utils.gotoMessagingActivity(mActivity, user.getObjectId());
    }

    private void btnToMessagingClicked() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        String userName = currentUser.getObjectId();
        if (getSinchServiceInterface()==null)Log.d("DFSP", "GET is NULL");
        if (mSinchServiceInterface==null ) Log.d("DFSP", "mSINCH is NULL");

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            Utils.gotoMessagingActivity(mActivity, user.getObjectId());
        }
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(mActivity);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
        getSinchServiceInterface().setStartListener(this);
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }
    public void setCategory(){
        txtCategory.setText(user.getString(Common.OBJECT_USER_CATEGORY));
    }
}

