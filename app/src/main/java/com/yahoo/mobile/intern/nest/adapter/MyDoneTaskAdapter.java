package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.Utils;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cmwang on 8/20/15.
 */
public class MyDoneTaskAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    private List<ParseObject> mList;

    static class ViewHolder {

        @Bind(R.id.txt_title) public TextView txtTitle;
        @Bind(R.id.txt_time) public TextView txtTime;
        @Bind(R.id.txt_seller_name) public TextView txtSellerName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MyDoneTaskAdapter(Context context, List<ParseObject> list) {
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
            convertView = mInflater.inflate(R.layout.card_my_done_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject task = mList.get(position);
        holder.txtTitle.setText(task.getString(Common.OBJECT_QUESTION_TITLE));
        holder.txtTime.setText(task.getString(Common.OBJECT_QUESTION_TIME));
        setupSellerName(holder, task);

        return convertView;
    }
    private void setupSellerName(final ViewHolder holder, final ParseObject task) {
        ParseUser seller = task.getParseUser(Common.OBJECT_QUESTION_DONE_USER);
        if(seller != null) {
            seller.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject seller, ParseException e) {
                    if (e == null) {
                        holder.txtSellerName.setText(seller.getString(Common.OBJECT_USER_NICK));
                    }
                }
            });
        }
    }
}
