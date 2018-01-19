package com.summertaker.blog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.summertaker.blog.common.BaseActivity;
import com.summertaker.blog.common.BaseApplication;
import com.summertaker.blog.common.Config;
import com.summertaker.blog.data.Group;

import java.util.ArrayList;

public class GroupListActivity extends BaseActivity {

    private Group mGroup;

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_activity);

        mContext = GroupListActivity.this;

        initToolbar(null);

        ArrayList<Group> groups = BaseApplication.getInstance().getGroups();

        GroupListAdapter adapter = new GroupListAdapter(mContext, groups);

        mGridView = findViewById(R.id.gridView);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGroup = (Group) parent.getItemAtPosition(position);

                Intent intent = new Intent(mContext, MemberListActivity.class);
                intent.putExtra("groupId", mGroup.getId());
                startActivityForResult(intent, Config.REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.REQUEST_CODE && data != null) { // && resultCode == Activity.RESULT_OK) {
            boolean isFavoriteChanged = data.getBooleanExtra("isFavoriteChanged", false);
            //Log.e(mTag, "isFavoriteChanged: " + isFavoriteChanged);

            setResult(RESULT_OK, getIntent().putExtra("isFavoriteChanged", isFavoriteChanged));
        }
    }

    public void onToolbarClick() {
        //mGridView.setSelection(0);
        mGridView.smoothScrollToPosition(0);
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
