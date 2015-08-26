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

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.Date;
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

        @Bind(R.id.img_pic) public CircleImageView imgProfile;
        @Bind(R.id.txt_title) public TextView txtTitle;
        @Bind(R.id.txt_time) public TextView txtTime;
        @Bind(R.id.txt_remaining) public TextView txtRemaining;
        @Bind(R.id.txt_num_accepted) public TextView txtNumAccepted;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_catch_new_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject task = mList.get(position);

        ParseUser buyer = task.getParseUser(Common.OBJECT_QUESTION_USER);
        buyer.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject buyer, ParseException e) {
                if (e == null) {
                    ParseFile imgFile = buyer.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                    Picasso.with(mContext)
                            .load(imgFile.getUrl())
                            .into(holder.imgProfile);
                }
            }
        });


        holder.txtTitle.setText(task.getString(Common.OBJECT_QUESTION_TITLE));
        holder.txtTime.setText(task.getString(Common.OBJECT_QUESTION_TIME));

        Date expireDate = task.getDate(Common.OBJECT_QUESTION_EXPIRE_DATE);
        Date current = new Date();
        holder.txtRemaining.setText(Utils.getRemainingTime(current, expireDate));

        taskAcceptedCountAsync(task, holder);
        return convertView;
    }

    private void taskAcceptedCountAsync(ParseObject task, final ViewHolder holder) {
        ParseRelation<ParseObject> relation = task.getRelation(Common.OBJECT_QUESTION_ACCEPTED_USER);
        relation.getQuery().countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                holder.txtNumAccepted.setText(Integer.toString(count));
            }
        });
    }
}
