package com.yahoo.mobile.intern.nest.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.FragmentAddLocationDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ytli on 8/27/15.
 */
public class DateDialog extends DialogFragment {
    View mView;
    String day;
    String time;
    @Bind(R.id.group1)RadioGroup rg1;
    @Bind(R.id.group2)RadioGroup rg2;
    @OnClick(R.id.btn_confirm) void onConfirm(){
        dismiss();
        if (!(day==null||time==null)) {
            FragmentAddLocationDate fd = (FragmentAddLocationDate) getFragmentManager().findFragmentById(R.id.frame_content);
            fd.onDateDialogFinish(day + " " + time);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_date,container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,mView);
        setUpListener();
        return mView;
    }
    public void setUpListener(){
        RadioGroup.OnCheckedChangeListener ls1 = new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.normal:
                        day = "平日";
                        break;
                    case R.id.weekend:
                        day = "週末";
                        break;
                    case R.id.allweek:
                        day = "平日＆週末";
                        break;
                }
            }
        };
        RadioGroup.OnCheckedChangeListener ls2 = new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.day:
                        time = "白天";
                        break;
                    case R.id.afternoon:
                        time = "下午";
                        break;
                    case R.id.night:
                        time = "晚上";
                        break;
                    case R.id.allday:
                        time = "都可以";
                        break;
                }
            }
        };
        rg1.setOnCheckedChangeListener(ls1);
        rg2.setOnCheckedChangeListener(ls2);
    }
}