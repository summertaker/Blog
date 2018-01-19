package com.summertaker.blog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.blog.common.BaseDataAdapter;
import com.summertaker.blog.data.Article;
import com.summertaker.blog.util.ProportionalImageView;
import com.summertaker.blog.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ArticleListAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Article> mArticles = new ArrayList<>();
    private ArticleListInterface mCallback;

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;

    String mTodayString = "";

    public ArticleListAdapter(Context context, ArrayList<Article> articles, ArticleListInterface callback) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mArticles = articles;
        mCallback = callback;

        float density = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (272 * density);
        int margin = (int) (1 * density);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        mParams.setMargins(0, margin, 0, 0);
        mParamsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

        mTodayString = Util.getToday("yyyy/MM/dd");
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public Object getItem(int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ArticleListAdapter.ViewHolder holder = null;

        final Article article = mArticles.get(position);

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.article_list_item, null);

            holder = new ArticleListAdapter.ViewHolder();
            holder.loImage = view.findViewById(R.id.loImage);
            holder.tvTitle = view.findViewById(R.id.tvTitle);
            holder.tvName = view.findViewById(R.id.tvName);
            holder.tvToday = view.findViewById(R.id.tvToday);
            holder.tvYesterday = view.findViewById(R.id.tvYesterday);
            holder.tvDate = view.findViewById(R.id.tvDate);
            holder.tvContent = view.findViewById(R.id.tvContent);
            //mContext.registerForContextMenu(holder.tvContent);
            view.setTag(holder);
        } else {
            holder = (ArticleListAdapter.ViewHolder) view.getTag();
        }

        if (article.getImages() == null || article.getImages().size() == 0) {
            holder.loImage.setVisibility(View.GONE);
        } else {
            holder.loImage.removeAllViews();
            holder.loImage.setVisibility(View.VISIBLE);

            for (int i = 0; i < article.getImages().size(); i++) {
                //Log.e(TAG, "url[" + i + "]: " + imageArray[i]);
                final String imageUrl = article.getImages().get(i);
                if (imageUrl.isEmpty()) {
                    continue;
                }

                final ProportionalImageView iv = new ProportionalImageView(mContext);
                //if (i == imageArray.length - 1) {
                if (i == 0) {
                    iv.setLayoutParams(mParamsNoMargin);
                } else {
                    iv.setLayoutParams(mParams);
                }
                //iv.setAdjustViewBounds(true);
                //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onImageClick(article, imageUrl);
                    }
                });
                holder.loImage.addView(iv);

                Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.placeholder).into(iv);

                /*
                String fileName = Util.getUrlToFileName(imageUrl);
                File file = new File(Config.DATA_PATH, fileName);

                if (file.exists()) {
                    Picasso.with(mContext).load(file).into(iv);
                    //Log.d(mTag, fileName + " local loaded.");
                } else {
                    Picasso.with(mContext).load(imageUrl).into(iv, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.e(mTag, "Picasso Image Load Error...");
                        }
                    });

                    Picasso.with(mContext).load(imageUrl).into(getTarget(fileName));
                }
                */
            }
        }

        // 제목
        holder.tvTitle.setText(article.getTitle());
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onTitleClick(article);
            }
        });

        // 이름
        holder.tvName.setText(article.getName());

        // 날짜
        String pubDate = article.getDate();
        holder.tvToday.setVisibility(View.GONE);
        holder.tvYesterday.setVisibility(View.GONE);

        if (pubDate == null || pubDate.isEmpty()) {
            holder.tvDate.setVisibility(View.GONE);
        } else {
            //Log.e(mTag, "pubDate: " + pubDate);
            holder.tvDate.setVisibility(View.VISIBLE);

            Date date = null;
            try {
                DateFormat sdf = null;
                if (pubDate.contains("+")) {
                    sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
                } else if (pubDate.contains("/")) {
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
                } else if (pubDate.contains("-")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd E", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                } else if (pubDate.contains(".") && pubDate.length() <= 10) {
                    sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                }
                pubDate = pubDate.replace("요일", "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDate.setText(pubDate);

            if (date != null) {
                Date today = new Date();
                if (Util.isSameDate(today, date)) {
                    holder.tvToday.setVisibility(View.VISIBLE);
                }

                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.DATE, -1);
                Date yesterday = c.getTime();
                if (Util.isSameDate(yesterday, date)) {
                    holder.tvYesterday.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.tvContent.setText(article.getText());
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onContentClick(article);
            }
        });

        return view;
    }

    /*
    //target to save
    private Target getTarget(final String fileName) {
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean isSuccess;

                        File file = new File(Config.DATA_PATH, fileName);
                        if (file.exists()) {
                            isSuccess = file.delete();
                            //Log.d("==", fileName + " deleted.");
                        }
                        try {
                            isSuccess = file.createNewFile();
                            if (isSuccess) {
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                                //Log.d("==", fileName + " created.");
                            } else {
                                Log.e("==", fileName + " FAILED.");
                            }
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(mTag, "IMAGE SAVE ERROR!!! onBitmapFailed()");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
    */

    static class ViewHolder {
        LinearLayout loImage;
        TextView tvTitle;
        TextView tvName;
        TextView tvToday;
        TextView tvYesterday;
        TextView tvDate;
        TextView tvContent;
    }
}
