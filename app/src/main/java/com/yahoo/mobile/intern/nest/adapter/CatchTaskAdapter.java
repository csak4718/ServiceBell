package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cmwang on 8/17/15.
 */
public class CatchTaskAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ParseObject> mList;

    static class ViewHolder {

        @Bind(R.id.img_pic) public CircleImageView imgPic;
        @Bind(R.id.txt_name) public TextView txtName;
        @Bind(R.id.txt_title) public TextView txtTitle;
        @Bind(R.id.txt_date) public TextView txtDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public CatchTaskAdapter(Context context, List<ParseObject> list) {
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

    private void setupTaskView(final ViewHolder holder, ParseObject task) {
        holder.txtTitle.setText(task.getString(Common.OBJECT_QUESTION_TITLE));
        task.getParseUser(Common.OBJECT_QUESTION_USER)
                .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser postUser, ParseException e) {
                        holder.txtName.setText(postUser.getString(Common.OBJECT_USER_NICK));
                        displayUserParseImage(holder, postUser);
                    }
                });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_catch_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject task = mList.get(position);

        setupTaskView(holder, task);

        return convertView;
    }
}
