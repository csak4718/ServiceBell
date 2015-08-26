package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cmwang on 8/21/15.
 */
public class CategoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<ParseObject> mList;
    private LayoutInflater mInflater;

    public CategoryAdapter(Context context, List<ParseObject> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        mList = list;
    }

    static class ViewHolder {
        @Bind(R.id.txt_title) public TextView mTitle;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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
            convertView = mInflater.inflate(R.layout.category_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject item = mList.get(position);
        holder.mTitle.setText(item.getString(Common.OBJECT_CATEGORY_TITLE));

        return convertView;
    }
}
