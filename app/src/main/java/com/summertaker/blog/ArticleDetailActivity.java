package com.summertaker.blog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.summertaker.blog.common.BaseActivity;
import com.summertaker.blog.util.ImageUtil;
import com.summertaker.blog.util.Util;

import java.util.Calendar;
import java.util.Date;

public class ArticleDetailActivity extends BaseActivity {

    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        mContext = ArticleDetailActivity.this;

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String html = intent.getStringExtra("html");

        initToolbar(name);

        mScrollView = findViewById(R.id.scrollView);

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(Util.parseDate(date));

        TextView tvToday = findViewById(R.id.tvToday);
        Date today = new Date();
        Date blogDate = Util.getDate(date);
        if (Util.isSameDate(today, blogDate)) {
            tvToday.setVisibility(View.VISIBLE);
        }

        TextView tvYesterday = findViewById(R.id.tvYesterday);
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, -1);
        Date yesterday = c.getTime();
        if (Util.isSameDate(today, yesterday)) {
            tvYesterday.setVisibility(View.VISIBLE);
        }

        // https://medium.com/@rajeefmk/android-textview-and-image-loading-from-url-part-1-a7457846abb6
        TextView tvContent = findViewById(R.id.tvContent);
        Spannable spannable = ImageUtil.getSpannableHtmlWithImageGetter(mContext, tvContent, html);
        tvContent.setText(spannable);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance()); // URL 클릭 시 이동
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onToolbarClick() {
        mScrollView.scrollTo(0, 0);
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
