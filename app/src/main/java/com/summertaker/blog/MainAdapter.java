package com.summertaker.blog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.blog.common.BaseDataAdapter;
import com.summertaker.blog.data.Member;

import java.util.ArrayList;

public class MainAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Member> mMembers;

    public MainAdapter(Context context, ArrayList<Member> members) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMembers = members;
    }

    @Override
    public int getCount() {
        return (mMembers == null) ? 0 : mMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return mMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MainAdapter.ViewHolder holder = null;

        final Member member = mMembers.get(position);

        if (view == null) {
            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.main_item, null);

            holder = new MainAdapter.ViewHolder();
            holder.ivThumbnail = view.findViewById(R.id.ivThumbnail);
            holder.tvName = view.findViewById(R.id.tvName);
            holder.tvNew = view.findViewById(R.id.tvNew);
            //mContext.registerForContextMenu(holder.tvContent);
            view.setTag(holder);
        } else {
            holder = (MainAdapter.ViewHolder) view.getTag();
        }

        String imageUrl = member.getThumbnailUrl();
        Picasso.with(mContext).load(imageUrl).into(holder.ivThumbnail);

        holder.tvName.setText(member.getName());

        if (member.isUpdated()) {
            holder.tvNew.setVisibility(View.VISIBLE);
        } else {
            holder.tvNew.setVisibility(View.GONE);
        }

        return view;
    }

    static class ViewHolder {
        ImageView ivThumbnail;
        TextView tvName;
        TextView tvNew;
    }
}


