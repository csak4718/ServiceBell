package com.yahoo.mobile.intern.nest.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddLocationDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cmwang on 8/22/15.
 */
public class DialogServiceTime extends DialogFragment {

    private FragmentAddLocationDate parent;
    private AddTaskActivity activity;

    private View mView;
    @Bind(R.id.edt_time) EditText edtTime;


    @OnClick(R.id.btn_confirm) void confirmOnClick() {
        parent.txtTimeHolder.setText(edtTime.getText().toString());
        activity.time = edtTime.getText().toString();
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mView = inflater.inflate(R.layout.dialog_service_time, container);
        ButterKnife.bind(this, mView);
        parent = (FragmentAddLocationDate)getParentFragment();
        activity = (AddTaskActivity) getActivity();

        return mView;
    }
}
