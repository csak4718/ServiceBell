package com.yahoo.mobile.intern.nest.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.fragment.DialogFragmentSellerProfile;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cmwang on 8/17/15.
 */
public class AcceptedUserAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ParseUser> mList;
    private List<Boolean> checkStatus;
    private int mCheck;
    private boolean mSelectable;

    static class ViewHolder {

        @Bind(R.id.img_pic) public CircleImageView imgPic;
        @Bind(R.id.txt_name) public TextView txtName;
        @Bind(R.id.txt_title) public TextView txtTitle;
        @Bind(R.id.radio_select) public RadioButton rdSelect;
        @Bind(R.id.btn_chat) public ImageButton btnChat;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void receivedAcceptedUser() {
        checkStatus = new ArrayList<>();
        for(int i = 0; i < mList.size(); i++) {
            checkStatus.add(false);
        }
        notifyDataSetChanged();
    }

    public AcceptedUserAdapter(Context context, List<ParseUser> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
        mCheck = -1;
        mSelectable = false;
    }

    public ParseUser getCheckedUser() {
        if(mCheck != -1) {
            return mList.get(mCheck);
        }
        return null;
    }

    public void setSelectable(boolean state) {
        mSelectable = state;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    private void displayUserParseImage(final ViewHolder holder, final ParseUser user) {
        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        imgFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        holder.imgPic.setImageBitmap(bmp);
                    }
                }
            }
        });
    }

    private void checkPosition(int position) {
        checkStatus.set(position, true);
        mCheck = position;
        for(int i = 0; i < mList.size(); i++) {
            if(i == position) {
                continue;
            }
            checkStatus.set(i, false);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_accepted_user, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseUser acceptedUser = mList.get(position);
        holder.txtName.setText(acceptedUser.getString(Common.OBJECT_USER_NICK));
        displayUserParseImage(holder, acceptedUser);
        holder.imgPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentSellerProfile dfsp = DialogFragmentSellerProfile.newInstance(acceptedUser);
                dfsp.show(((AppCompatActivity) mContext).getSupportFragmentManager(),"lol");
            }
        });

        if(!mSelectable) {
            holder.btnChat.setVisibility(View.VISIBLE);
            holder.rdSelect.setVisibility(View.GONE);
            holder.btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.gotoSpinnerActivity((Activity)mContext, acceptedUser.getObjectId()); // acceptedUser is recipient
                }
            });
        }
        else {
            holder.btnChat.setVisibility(View.GONE);
            holder.rdSelect.setVisibility(View.VISIBLE);
            holder.rdSelect.setChecked(checkStatus.get(position));

            holder.rdSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPosition(position);
                }
            });
        }

        return convertView;
    }
}
