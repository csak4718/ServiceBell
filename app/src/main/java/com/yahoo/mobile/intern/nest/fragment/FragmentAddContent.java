package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cmwang on 8/21/15.
 */
public class FragmentAddContent extends Fragment {

    private View mView;
    private AddTaskActivity activity;

    @Bind(R.id.edt_title) EditText edtTitle;
    @Bind(R.id.edt_content) EditText edtContent;

    @OnClick(R.id.btn_next) void onClickNext() {

        activity.title = edtTitle.getText().toString();
        activity.content = edtContent.getText().toString();

        if(activity.title.length() == 0) {
            Utils.makeToast(activity, "標題一定要填喔");
            return;
        }
        if(activity.content.length() == 0) {
            Utils.makeToast(activity, "問題內容一定要填喔");
            return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.frame_content, new FragmentAddLocationDate())
                .commit();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (AddTaskActivity) getActivity();

        mView = inflater.inflate(R.layout.fragment_add_content, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }
}
