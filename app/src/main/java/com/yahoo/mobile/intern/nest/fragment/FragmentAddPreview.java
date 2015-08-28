package com.yahoo.mobile.intern.nest.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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
    @Bind(R.id.txt_category) TextView txtCategory;

    @Bind(R.id.img_view_question_picture)ImageView imgViewQuestionPicture;

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
        txtCategory.setText(activity.category);

        Date current = new Date();
        txtRemaining.setText(Utils.getRemainingTime(current, activity.expire));


        imgViewQuestionPicture.setVisibility(View.VISIBLE);
        imgViewQuestionPicture.setImageBitmap(activity.image);
        imgViewQuestionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_image);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;


                Bitmap bmp = ((BitmapDrawable) imgViewQuestionPicture.getDrawable())
                        .getBitmap();

                ImageView picture = (ImageView) dialog.findViewById(R.id.img_view_dialog_picture);
                ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.img_btn_close);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                picture.setImageBitmap(bmp);
                dialog.show();
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor
                        ("#80000000")));
            }
        });

        return mView;
    }
}
