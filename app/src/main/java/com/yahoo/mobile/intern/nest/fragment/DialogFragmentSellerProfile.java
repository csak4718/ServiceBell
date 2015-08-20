package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ytli on 8/18/15.
 */
public class DialogFragmentSellerProfile extends DialogFragment {
    ParseUser user;
    View mView;
    @Bind(R.id.img_pic)CircleImageView mImgProfilePic;
    @Bind(R.id.txt_name)TextView txtName;
    @Bind(R.id.txt_address)TextView txtAdd;
    @Bind(R.id.txt_others)TextView txtOthers;
    @Bind(R.id.txt_phone)TextView txtPhone;

    @OnClick(R.id.btn_confirm) void confirm(){
        ProfileDialogListener activity = (ProfileDialogListener) getActivity();
        activity.onFinishProfileDialog("Confirm", user);
        this.dismiss();
    }

    public interface ProfileDialogListener {
        void onFinishProfileDialog(String inputText, ParseUser seller);
    }

    public static DialogFragmentSellerProfile newInstance(ParseUser user){
        DialogFragmentSellerProfile dfsp = new DialogFragmentSellerProfile();
        dfsp.user = user;
        return dfsp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_seller_profile, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, mView);
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
        if (nickName == null || nickName == ""){txtName.setVisibility(View.GONE);}else{txtName.setText(nickName);}
        if (address == null || address == "") {txtAdd.setVisibility(View.GONE);}else{txtAdd.setText(address);}
        if (phone == null || phone =="") {txtPhone.setVisibility(View.GONE);}else{txtPhone.setText(phone);}
        if (others == null || others ==""){txtOthers.setVisibility(View.GONE);}else{txtOthers.setText(others);}
    }
}
