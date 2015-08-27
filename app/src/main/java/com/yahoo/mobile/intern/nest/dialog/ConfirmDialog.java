package com.yahoo.mobile.intern.nest.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ytli on 8/25/15.
 */
public class ConfirmDialog extends DialogFragment{
    private View mView;
    private String name;
    private Drawable img;
    private Float rating;
    private ParseUser user;
    @Bind(R.id.txt_name)TextView txtName;
    @Bind(R.id.ratingBar)RatingBar ratingBar;
    @Bind(R.id.btn_close)ImageView btnClose;
    @Bind(R.id.img_pic)CircleImageView imgPic;
    @OnClick(R.id.btn_close) void close(){
        this.dismiss();
    }
    @OnClick(R.id.btn_confirm) void confirm(){
        ConfirmDialogListener activity = (ConfirmDialogListener) getActivity();
        activity.onFinishConfirmDialog("Confirm", user);
        dismiss();
    }
    public interface ConfirmDialogListener {
        void onFinishConfirmDialog(String inputText, ParseUser seller);
    }

    public static ConfirmDialog newInstance(String name, Drawable img, Float rating, ParseUser user) {
        ConfirmDialog fg = new ConfirmDialog();
        fg.name = name;
        fg.img = img;
        fg.rating = rating;
        fg.user = user;
        return fg;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_confirm, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, mView);
        txtName.setText(name);
        imgPic.setImageDrawable(img);
        ratingBar.setRating(rating);
        return mView;
    }
}
