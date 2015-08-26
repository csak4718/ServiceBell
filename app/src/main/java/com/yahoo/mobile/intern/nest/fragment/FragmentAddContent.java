package com.yahoo.mobile.intern.nest.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.activity.AddTaskActivity;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cmwang on 8/21/15.
 */
public class FragmentAddContent extends Fragment {

    private View mView;
    private AddTaskActivity activity;
    private Uri mImageUri;

    @Bind(R.id.edt_title) EditText edtTitle;
    @Bind(R.id.edt_content) EditText edtContent;

    @Bind(R.id.img_preview_root) FrameLayout imgPreviewRoot;
    @Bind(R.id.img_view_upload)ImageView imgViewUpload;
    @Bind(R.id.img_btn_preview_delete) ImageButton imgBtnPreviewDelete;

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

        activity.image = ((BitmapDrawable) imgViewUpload.getDrawable()).getBitmap();

        getFragmentManager().beginTransaction()
                .replace(R.id.frame_content, new FragmentAddLocationDate())
                .commit();
    }



    public void setImgViewUpload(Uri uri) {

        mImageUri = uri;

        Picasso.with(getActivity())
                .load(uri)
                .resize(640, 480)
                .centerInside()
                .into(imgViewUpload);
        imgPreviewRoot.setVisibility(View.VISIBLE);
        activity.imageUri = mImageUri;
//        postWithPicture = true;
    }

    public void setImgViewUpload() {

        getActivity().getContentResolver().notifyChange(mImageUri, null);

        Picasso.with(getActivity())
                .load(mImageUri)
                .resize(640, 480)
                .centerInside()
                .into(imgViewUpload);
        imgPreviewRoot.setVisibility(View.VISIBLE);
        activity.imageUri = mImageUri;
//        postWithPicture = true;
    }


    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= getActivity().getExternalCacheDir();
        return File.createTempFile(part, ext, tempDir);
    }

    @OnClick(R.id.img_btn_preview_delete) void deleteImage() {
        activity.imageUri = null;
        imgPreviewRoot.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_btn_camera) void getPictureFromCamera() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photo;
        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
            mImageUri = Uri.fromFile(photo);
            i.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            getActivity().startActivityForResult(i, Common.CAMERA_REQUEST);
        }
        catch(Exception e)
        {
            Log.v("guagua", "Can't create file to take picture!");
            Toast.makeText(getActivity(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT);
        }
    }
    @OnClick(R.id.img_btn_picture) void getPictureFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(i, Common.ACTIVITY_SELECT_IMAGE);
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
