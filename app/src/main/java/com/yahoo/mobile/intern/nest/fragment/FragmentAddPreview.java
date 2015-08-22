package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cmwang on 8/21/15.
 */
public class FragmentAddPreview extends Fragment {
    private View mView;
    private AddTaskActivity activity;

    @Bind(R.id.txt_status) TextView txtStatus;
    @Bind(R.id.txt_title) TextView txtTitle;
    @Bind(R.id.txt_content) TextView txtContent;
    @Bind(R.id.txt_location) TextView txtAddress;
    @Bind(R.id.txt_task_time) TextView txtTaskTime;
    @Bind(R.id.txt_remaining) TextView txtRemaining;

    @OnClick(R.id.btn_confirm) void onClickConfirm() {
        activity.addTask();
        activity.finish();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (AddTaskActivity) getActivity();

        mView = inflater.inflate(R.layout.fragment_add_preview, container, false);
        ButterKnife.bind(this, mView);

        txtStatus.setVisibility(View.GONE);

        txtTitle.setText(activity.title);
        txtContent.setText(activity.content);
        txtAddress.setText(activity.address);
        txtTaskTime.setText(activity.time);

        Date current = new Date();
        txtRemaining.setText(Utils.getRemainingTime(current, activity.expire));

        return mView;
    }
}
