package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

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

    static class ViewHolder {
        @Bind(R.id.ratingBar) public RatingBar ratingBar;
        @Bind(R.id.img_pic) public CircleImageView imgPic;
        @Bind(R.id.txt_name) public TextView txtName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public AcceptedUserAdapter(Context context, List<ParseUser> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
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
        holder.ratingBar.setRating(acceptedUser.getNumber(Common.OBJECT_USER_RATING).floatValue());

        ParseFile imgFile = acceptedUser.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Picasso.with(mContext)
                .load(imgFile.getUrl())
                .into(holder.imgPic);

        return convertView;
    }
}
