package com.yahoo.mobile.intern.nest.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;

import com.yahoo.mobile.intern.nest.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ytli on 8/26/15.
 */
public class RatingDialog extends DialogFragment {
    private View mView;
    @Bind(R.id.ratingBar)RatingBar ratingBar;
    @OnClick(R.id.btn_confirm) void confirm(){
        RatingDialogListener rd = (RatingDialogListener)getActivity();
        rd.onFinishDialog(ratingBar.getRating());
        dismiss();
    }

    public interface RatingDialogListener{
        void onFinishDialog(Float rating);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_rating,container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, mView);
        return mView;
    }
}
