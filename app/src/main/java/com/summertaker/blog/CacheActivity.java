package com.summertaker.blog;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.summertaker.blog.common.BaseActivity;
import com.summertaker.blog.common.Config;
import com.summertaker.blog.util.Util;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class CacheActivity extends BaseActivity {

    private TextView mTvCacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);

        mContext = CacheActivity.this;

        initToolbar(null);

        mTvCacheSize = findViewById(R.id.tvCacheSize);

        showCache();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cache, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteCache();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCache() {
        long size = Util.getDirectorySize(new File(Config.DATA_PATH));
        //Log.e(mTag, "size:" + dirSize);

        long mega = size / (1024 * 1024);

        NumberFormat nf = NumberFormat.getInstance();
        String text = (mega == 0) ? nf.format(size) + " Byte" : nf.format(mega) + " MB";

        mTvCacheSize.setText(text);
    }

    private void deleteCache() {
        try {
            Util.deleteFiles(new File(Config.DATA_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(mTag, "ERROR:deleteCache()... " + e.getMessage());
        }

        showCache();
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
