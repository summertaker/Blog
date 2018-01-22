package com.summertaker.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.summertaker.blog.common.BaseActivity;
import com.summertaker.blog.common.BaseApplication;
import com.summertaker.blog.common.Config;
import com.summertaker.blog.data.Article;
import com.summertaker.blog.data.Member;
import com.summertaker.blog.parser.Keyakizaka46Parser;
import com.summertaker.blog.parser.Nogizaka46Parser;
import com.summertaker.blog.util.Util;

import java.util.ArrayList;

public class ArticleListActivity extends BaseActivity implements ArticleListInterface {

    private String mBlogUrl;
    private ArrayList<Article> mArticles;
    private ArticleListAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_activity);

        mContext = ArticleListActivity.this;

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        mBlogUrl = intent.getStringExtra("blogUrl");

        initToolbar(name);

        mArticles = new ArrayList<>();
        mAdapter = new ArticleListAdapter(mContext, mArticles, this);

        mListView = findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.article_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_open_in_new:
                Intent groupList = new Intent(Intent.ACTION_VIEW, Uri.parse(mBlogUrl));
                startActivity(groupList);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        String fileName = Util.getUrlToFileName(mBlogUrl) + ".html";

        //File file = new File(Config.DATA_PATH, fileName);
        parseData(mBlogUrl, Util.readFile(fileName));
    }

    private void parseData(String url, String response) {
        if (!response.isEmpty()) {
            ArrayList<Article> articles = new ArrayList<>();
            if (url.contains("nogizaka46")) {
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                nogizaka46Parser.parseBlogDetail(response, articles);
            } else if (url.contains("keyakizaka46")) {
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                keyakizaka46Parser.parseBlogDetail(response, articles);
            }

            mArticles.clear();
            mArticles.addAll(articles);
        }

        renderData();
    }

    public void renderData() {
        mAdapter.notifyDataSetChanged();

        //--------------------------
        // 최종 블로그 일자 저장하기
        //--------------------------
        if (mArticles.size() > 0) {
            boolean isDataChanged = false;

            Article article = mArticles.get(0);
            ArrayList<Member> favorites = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_FAVORITES);

            for (Member member : favorites) {
                if (member.getBlogUrl().equals(mBlogUrl)) {
                    if (!member.getLastDate().equals(article.getDate())) { // 최종 날짜가 다르면
                        member.setLastDate(article.getDate());
                        member.setUpdated(false);
                        isDataChanged = true;
                        break;
                    }
                }
            }

            if (isDataChanged) {
                BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_FAVORITES, favorites);
                setResult(RESULT_OK, getIntent().putExtra("isDataChanged", true));
            }
        }
    }

    public void onToolbarClick() {
        mListView.setSelection(0);
    }

    public void gotoDetail(Article article) {
        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
        intent.putExtra("name", article.getName());
        intent.putExtra("title", article.getTitle());
        intent.putExtra("date", article.getDate());
        intent.putExtra("html", article.getHtml());
        startActivity(intent);
    }

    @Override
    public void onTitleClick(Article article) {
        gotoDetail(article);
    }

    @Override
    public void onContentClick(Article article) {
        gotoDetail(article);
    }

    @Override
    public void onImageClick(Article article, String imageUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivity(intent);
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
