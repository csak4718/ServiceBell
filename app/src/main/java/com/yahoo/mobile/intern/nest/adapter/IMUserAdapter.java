package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dwkung on 8/18/15.
 */
public class IMUserAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ParseUser> mFriendsList;

    static class ViewHolder {

        @Bind(R.id.img_friend_pic) public CircleImageView imgPic;
        @Bind(R.id.txt_friend_name) public TextView txtFriendName;
        @Bind(R.id.txt_message) public TextView txtMessage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public IMUserAdapter(Context context, List<ParseUser> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mFriendsList = list;
    }

    @Override
    public int getCount() {
        return mFriendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFriendsList.get(position).hashCode();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_friend, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseUser friend = mFriendsList.get(position);
        holder.txtFriendName.setText(friend.getString(Common.OBJECT_USER_NICK));
        displayUserParseImage(holder, friend);

        return convertView;
    }
}
