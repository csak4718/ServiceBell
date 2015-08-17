package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cmwang on 8/12/15.
 */
public class MyTaskAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<ParseObject> mList;
    private Handler mHandler;

    static class ViewHolder {

        @Bind(R.id.txt_title) public TextView txtTitle;
        @Bind(R.id.txt_date) public TextView txtDate;
        @Bind(R.id.txt_num_accepted) public TextView txtNumAccepted;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MyTaskAdapter(Context context, List<ParseObject> list, Handler handler) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        mList = list;
        mHandler = handler;
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
            convertView = mInflater.inflate(R.layout.card_my_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject task = mList.get(position);
        holder.txtTitle.setText(task.getString(Common.OBJECT_QUESTION_TITLE));
//        holder.txtDate.setText(task.getCreatedAt().toString());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int numAccepted = ParseUtils.taskAcceptedCount(task);
                holder.txtNumAccepted.setText(Integer.toString(numAccepted));
            }
        });
        return convertView;
    }
}