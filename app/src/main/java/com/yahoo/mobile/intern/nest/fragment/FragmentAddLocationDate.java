package com.yahoo.mobile.intern.nest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.dialog.DialogServiceTime;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cmwang on 8/21/15.
 */
public class FragmentAddLocationDate extends Fragment {

    private View mView;
    private AddTaskActivity activity;


    @Bind(R.id.txt_location_holder) public TextView txtLocationHolder;
    @Bind(R.id.txt_time_holder) public TextView txtTimeHolder;
    @Bind(R.id.txt_expire_holder) public TextView txtExpireHolder;

    @OnClick(R.id.btn_preview) void onClickPreview() {

        if(activity.address == null) {
            Utils.makeToast(getActivity(), "位置不可為空喔");
            return;
        }
        if(activity.time == null) {
            Utils.makeToast(getActivity(), "一定要設定服務時間喔");
            return;
        }
        if(activity.expire == null) {
            Utils.makeToast(getActivity(), "請設定截止日期");
            return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.frame_content, new FragmentAddPreview())
                .commit();
    }

    @OnClick(R.id.btn_set_location) void setLocaionBtnSetLocation() {
        Utils.gotoMapsActivityForResult(activity);
    }

    @OnClick(R.id.btn_set_time) void setTime() {
        new DialogServiceTime().show(getChildFragmentManager().beginTransaction(), "dialog");
    }

    @OnClick(R.id.btn_set_expiretime) void setExpireTimeBtnSetLocation() {
        final Calendar c = Calendar.getInstance();
        activity.mYear = c.get(Calendar.YEAR);
        activity.mMonth = c.get(Calendar.MONTH);
        activity.mDay = c.get(Calendar.DAY_OF_MONTH);
        activity.mHour = c.get(Calendar.HOUR_OF_DAY);
        activity.mMinute  = c.get(Calendar.MINUTE);

        final TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                        activity.mHour = hourOfDay;
                        activity.mMinute = minute;

                        activity.expire = new Date(activity.mYear-1900, activity.mMonth, activity.mDay, activity.mHour, activity.mMinute);
                        Date current = new Date();
                        txtExpireHolder.setText(Utils.getRemainingTime(current, activity.expire));

                    }
                }, activity.mHour, activity.mMinute, false);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int
                            dayOfMonth) {
                        activity.mDay = dayOfMonth;
                        activity.mMonth = monthOfYear;
                        activity.mYear = year;
                        tpd.show(activity.getFragmentManager(), "Timepickerdialog" );
                    }
                }, activity.mYear, activity.mMonth, activity.mDay);
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (AddTaskActivity) getActivity();

        mView = inflater.inflate(R.layout.fragment_add_location_date, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }
}
