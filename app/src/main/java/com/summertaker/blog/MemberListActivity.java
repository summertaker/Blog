package com.summertaker.blog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.blog.common.BaseActivity;
import com.summertaker.blog.common.BaseApplication;
import com.summertaker.blog.common.Config;
import com.summertaker.blog.data.Group;
import com.summertaker.blog.data.Member;
import com.summertaker.blog.parser.Keyakizaka46Parser;
import com.summertaker.blog.parser.Nogizaka46Parser;
import com.summertaker.blog.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberListActivity extends BaseActivity implements MemberListInterface {

    private Group mGroup;

    RelativeLayout mLoLoading;
    LinearLayout mLoContent;

    private ArrayList<Member> mMembers;
    private MemberListAdapter mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list_activity);

        mContext = MemberListActivity.this;

        Intent intent = getIntent();
        String groupId = intent.getStringExtra("groupId");
        mGroup = BaseApplication.getInstance().getGroupById(groupId);

        initToolbar(mGroup.getName());

        mLoLoading = findViewById(R.id.loLoading);

        mMembers = new ArrayList<>();
        mGridView = findViewById(R.id.gridView);

        //loadGroup();

        requestData(mGroup.getUrl());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        //if (id == R.id.action_refresh) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        BaseApplication.getInstance().cancelPendingRequests(mVolleyTag);

        //ArrayList<Member> favorites = BaseApplication.getInstance().getFavorites();
        //BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_FAVORITES, favorites);
        //BaseApplication.getInstance().setFavorites(favorites);
    }

    private void requestData(final String url) {
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                //writeData(url, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.alert(mContext, getString(R.string.error), error.getMessage(), null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", mGroup.getUserAgent());
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void parseData(String url, String response) {
        if (response.isEmpty()) {
            Util.alert(mContext, getString(R.string.error), "response is empty.", null);
        } else {
            if (mGroup.getId().equals("nogizaka46")) {
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                nogizaka46Parser.parseBlogList(response, mGroup, mMembers);
            } else if (mGroup.getId().equals("keyakizaka46")) {
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                keyakizaka46Parser.parseBlogList(response, mGroup, mMembers);
            }

            renderData();
        }
    }

    private void renderData() {
        mLoLoading.setVisibility(View.GONE);

        ArrayList<Member> favorites = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_FAVORITES);
        for (Member m : mMembers) {
            for (Member o : favorites) {
                if (m.getBlogUrl().equals(o.getBlogUrl())) {
                    m.setFavorite(true);
                }
            }
        }

        mAdapter = new MemberListAdapter(mContext, mMembers, this);
        mGridView.setAdapter(mAdapter);
        mGridView.setVisibility(View.VISIBLE);
    }

    public void onToolbarClick() {
        //mGridView.setSelection(0);
        mGridView.smoothScrollToPosition(0);
    }

    @Override
    public void onPicutreClick(Member member) {
        saveData(member);
    }

    @Override
    public void onLikeClick(CheckBox checkBox, Member member) {
        saveData(member);
    }

    @Override
    public void onNameClick(Member member) {
        saveData(member);
    }

    private void saveData(Member member) {
        member.setFavorite(!member.isFavorite());
        boolean favorite = member.isFavorite();

        ArrayList<Member> favorites = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_FAVORITES);

        if (favorite) { // 추가
            boolean isExist = false;
            for (Member m : favorites) {
                if (m.getBlogUrl().equals(member.getBlogUrl())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                favorites.add(member);
            }
        } else { // 제거
            ArrayList<Member> members = new ArrayList<>();
            for (Member m : favorites) {
                if (!m.getBlogUrl().equals(member.getBlogUrl())) {
                    members.add(m);
                }
            }
            favorites = members;
        }

        BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_FAVORITES, favorites);

        mAdapter.notifyDataSetChanged();

        //---------------------------------------------------------------------
        // 이전 Activity 에 데이터 전달하기
        // onPuase(), onStop(), onDestroy() 모두에 적용시키기 위해 미리 실행
        //---------------------------------------------------------------------
        setResult(RESULT_OK, getIntent().putExtra("isFavoriteChanged", true));
    }
}
