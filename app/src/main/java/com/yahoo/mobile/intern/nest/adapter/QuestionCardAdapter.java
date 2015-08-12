package com.yahoo.mobile.intern.nest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cmwang on 8/12/15.
 */
public class QuestionCardAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<ParseObject> mList;

    static class ViewHolder {

        public TextView txtName;
        public CircleImageView imgProfile;
        public TextView txtDate;
        public TextView txtContent;
        public ImageView imgQuestion;

        public ViewHolder(View view) {
            txtName = (TextView) view.findViewById(R.id.txtName);
            imgProfile = (CircleImageView) view.findViewById(R.id.imgProfile);
            txtDate = (TextView) view.findViewById(R.id.txt_date);
            txtContent = (TextView) view.findViewById(R.id.txt_content);
            imgQuestion = (ImageView) view.findViewById(R.id.img_view_question_picture);
        }
    }

    public QuestionCardAdapter(Context context, List<ParseObject> list) {
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
        ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_question, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject question = mList.get(position);
        holder.txtContent.setText(question.getString(Common.OBJECT_QUESTION_CONTENT));

        return convertView;
    }
}
