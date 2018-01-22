package com.summertaker.blog;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.blog.common.BaseActivity;
import com.summertaker.blog.common.BaseApplication;
import com.summertaker.blog.common.Config;
import com.summertaker.blog.data.Article;
import com.summertaker.blog.data.Member;
import com.summertaker.blog.parser.Keyakizaka46Parser;
import com.summertaker.blog.parser.Nogizaka46Parser;
import com.summertaker.blog.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private boolean mIsPermissionGranted = false;

    protected String mTag = "== " + getClass().getSimpleName();
    protected String mVolleyTag = mTag;

    protected Context mContext;

    private boolean mIsFirst = true;

    private int mNavItemId = 0;

    private LinearLayout mLoLoading;
    private TextView mTvProgressCount;
    private ProgressBar mPbHorizontal;

    private SwipeRefreshLayout mSwipeRefresh;
    private int mLoadCount = 0;
    private ArrayList<Member> mFavorites;
    private MainAdapter mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToolbarClick();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //Log.e(mTag, "mNavItemId: " + mNavItemId);
                switch (mNavItemId) {
                    //case R.id.nav_member_settings:
                    //    Intent groupIntent = new Intent(mContext, GroupListActivity.class);
                    //    startActivityForResult(groupIntent, Config.REQUEST_CODE);
                    //    break;
                    case R.id.nav_cache_settings:
                        Intent cacheIntent = new Intent(mContext, CacheActivity.class);
                        startActivity(cacheIntent);
                        break;
                }
                mNavItemId = 0;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLoLoading = findViewById(R.id.loLoading);
        mTvProgressCount = findViewById(R.id.tvProgressCount);
        mPbHorizontal = findViewById(R.id.pbHorizontal);

        mSwipeRefresh = findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(this);

        mFavorites = new ArrayList<>();
        mAdapter = new MainAdapter(mContext, mFavorites);

        mGridView = findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Member member = (Member) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(mContext, ArticleListActivity.class);
                intent.putExtra("name", member.getName());
                intent.putExtra("blogUrl", member.getBlogUrl());
                startActivityForResult(intent, Config.REQUEST_CODE);
            }
        });

        //----------------------------------------------------------------------------
        // 런타임에 권한 요청
        // https://developer.android.com/training/permissions/requesting.html?hl=ko
        //----------------------------------------------------------------------------
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String path = Config.DATA_PATH;
                    File dir = new File(path);
                    if (!dir.exists()) {
                        boolean isSuccess = dir.mkdirs(); // 캐쉬 파일 저장 위치 생성
                    }
                    mIsPermissionGranted = true;
                } else {
                    onPermissionDenied();
                }
            }
        }
    }

    void onPermissionDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(R.string.access_denied);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add:
                Intent groupList = new Intent(mContext, GroupListActivity.class);
                startActivityForResult(groupList, Config.REQUEST_CODE);
                return true;
            //case R.id.action_refresh:
            //    refresh();
            //    return true;
            case R.id.action_sort:
                Intent sort = new Intent(mContext, SortActivity.class);
                startActivityForResult(sort, Config.REQUEST_CODE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //int id = item.getItemId();
        //if (id == R.id.nav_member_settings) {
        //
        //} else if (id == R.id.nav_cache_settings) {
        //
        //}

        mNavItemId = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsPermissionGranted) {

            if (mIsFirst) {
                mIsFirst = false;

                ArrayList<Member> members = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_FAVORITES);

                if (members.size() == 0) {
                    renderData();
                } else {
                    // https://stackoverflow.com/questions/15422120/notifydatasetchange-not-working-from-custom-adapter
                    mFavorites.clear();
                    mFavorites.addAll(members);

                    loadData();
                }
            }
        }
    }

    private void loadData() {
        //Log.e(mTag, "loadData()...");

        mLoLoading.setVisibility(View.VISIBLE);
        mSwipeRefresh.setVisibility(View.GONE);

        mTvProgressCount.setText("");
        mPbHorizontal.setProgress(0);

        for (Member member : mFavorites) {
            //Log.e(mTag, member.getName() + " " + member.getLastDate());
            member.setUpdated(false);
            requestData(member.getBlogUrl());
        }
    }

    private void requestData(final String url) {
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, response.toString());
                writeData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "VolleyError: " + error.getMessage());
                //Util.alert(mContext, getString(R.string.error), error.getMessage(), null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", Config.USER_AGENT_DESKTOP);
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void writeData(String url, String response) {
        Util.writeToFile(Util.getUrlToFileName(url) + ".html", response);
        parseData(url, response);
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

            //Log.e(mTag, "articles.size(): " + articles.size());

            Member currentMember = null;
            for (Member member : mFavorites) {
                if (url.equals(member.getBlogUrl())) {
                    currentMember = member;
                    break;
                }
            }

            if (currentMember != null) {
                if (articles.size() > 0) {
                    if (currentMember.getLastDate() == null || currentMember.getLastDate().isEmpty()) {
                        // 마지막 체크 날짜가 없는 경우
                        currentMember.setUpdated(true);
                    } else {
                        Date articleDate = Util.getDate(articles.get(0).getDate());
                        Date lastDate = Util.getDate(currentMember.getLastDate());
                        //Log.e(mTag, articleDate.toString() + " " + lastDate.toString());

                        // https://stackoverflow.com/questions/22039991/how-to-compare-two-dates-along-with-time-in-java
                        int compareTo = articleDate.compareTo(lastDate);
                        if (compareTo > 0) {
                            currentMember.setUpdated(true);
                        }
                    }
                }
            }

            mLoadCount++;

            if (mLoadCount < mFavorites.size()) {
                updateProgress();
            } else {
                renderData();
            }
        }
    }

    private void updateProgress() {
        int count = mLoadCount + 1;

        String text = "( " + count + " / " + mFavorites.size() + " )";
        mTvProgressCount.setText(text);

        float progress = (float) count / (float) mFavorites.size();
        int progressValue = (int) (progress * 100.0);

        mPbHorizontal.setProgress(progressValue);
    }

    public void renderData() {
        //Log.e(mTag, "renderData().mFavorites.size() = " + mFavorites.size());

        mLoLoading.setVisibility(View.GONE);
        mSwipeRefresh.setVisibility(View.VISIBLE);

        mAdapter.notifyDataSetChanged();

        mLoadCount = 0;
        mSwipeRefresh.setRefreshing(false);

        // updated 정보 저장
        BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_FAVORITES, mFavorites);
    }

    public void onToolbarClick() {
        //mGridView.setSelection(0);
        mGridView.smoothScrollToPosition(0);
    }

    private void refresh() {
        loadData();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.REQUEST_CODE && data != null) { // && resultCode == Activity.RESULT_OK) {
            boolean isFavoriteChanged = data.getBooleanExtra("isFavoriteChanged", false);
            boolean isDataChanged = data.getBooleanExtra("isDataChanged", false);
            //Log.e(mTag, "isFavoriteChanged: " + isFavoriteChanged + ", isDataChanged: " + isDataChanged);

            if (isFavoriteChanged || isDataChanged) {
                // https://stackoverflow.com/questions/15422120/notifydatasetchange-not-working-from-custom-adapter
                ArrayList<Member> members = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_FAVORITES);
                mFavorites.clear();
                mFavorites.addAll(members);
            }

            if (isFavoriteChanged) {
                loadData();
            } else if (isDataChanged) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
