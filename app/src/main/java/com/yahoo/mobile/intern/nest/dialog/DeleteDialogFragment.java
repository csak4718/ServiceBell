package com.yahoo.mobile.intern.nest.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yahoo.mobile.intern.nest.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ytli on 8/24/15.
 */
public class DeleteDialogFragment extends DialogFragment {
    View mView;
    @OnClick(R.id.btn_cancel) void cancel(){
        dismiss();
    }
    @OnClick(R.id.btn_delete) void delete(){
        DeleteDialogListener dl = (DeleteDialogListener) getActivity();
        dl.onFinishDeleteDialog(true);
        dismiss();
    }
    public interface DeleteDialogListener {
        void onFinishDeleteDialog(boolean i);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_delete, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,mView);
        return mView;
    }
}
