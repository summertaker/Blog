package com.summertaker.blog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.blog.data.Member;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.ArrayList;

public class SortAdapter extends BaseDynamicGridAdapter {

    public SortAdapter(Context context, ArrayList<Member> members, int columnCount) {
        super(context, members, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sort_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Member member = (Member) getItem(position);

        holder.build(member);
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvName;

        private ViewHolder(View view) {
            ivThumbnail = view.findViewById(R.id.ivThumbnail);
            tvName = view.findViewById(R.id.tvName);
        }

        void build(Member member) {
            Picasso.with(getContext()).load(member.getThumbnailUrl()).into(ivThumbnail);
            tvName.setText(member.getName());
        }
    }
}
