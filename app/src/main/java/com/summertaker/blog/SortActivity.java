package com.summertaker.blog;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.summertaker.blog.common.BaseApplication;
import com.summertaker.blog.common.Config;
import com.summertaker.blog.data.Member;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;

public class SortActivity extends AppCompatActivity {

    private static final String TAG = SortActivity.class.getSimpleName();

    private String[] someViEnAlphabets = {"A", "Ă", "Â", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "Ô", "Ơ", "P", "Q", "R", "S", "T", "U", "V", "X", "Y"};

    private ArrayList<Member> mOrigins;
    private ArrayList<Member> mMembers;
    private SortAdapter mAdapter;
    private DynamicGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMembers = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_FAVORITES);

        mOrigins = new ArrayList<>();
        mOrigins.addAll(mMembers);

        mAdapter = new SortAdapter(this, mMembers, 3);

        // https://github.com/askerov/DynamicGrid
        // http://www.devexchanges.info/2016/02/android-tip-making-draggable-gridview.html
        gridView = (DynamicGridView) findViewById(R.id.dynamic_grid);
        gridView.setAdapter(mAdapter);

        //Active dragging mode when long click at each Grid view item
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                gridView.startEditMode(position);

                return true;
            }
        });

        //Handling click event of each Grid view item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                new AlertDialog.Builder(SortActivity.this)
                        .setTitle("Item information")
                        .setMessage("You clicked at position: " + position + "\n"
                                + "The letter is: " + parent.getItemAtPosition(position).toString())
                        .setPositiveButton(android.R.string.yes, null)

                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (gridView.isEditMode()) {
            gridView.stopEditMode();
            saveData();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveData() {
        boolean isDataChanged = false;

        ArrayList<Member> members = new ArrayList<>();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            Member origin = mOrigins.get(i);
            Member member = (Member) mAdapter.getItem(i);
            //Log.e(TAG, (i + 1) + ". " + member.getName());

            if (!origin.getBlogUrl().equals(member.getBlogUrl())) {
                isDataChanged = true;
            }

            members.add(member);
        }

        if (isDataChanged) {
            BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_FAVORITES, members);
            setResult(RESULT_OK, getIntent().putExtra("isDataChanged", true));
        }
    }
}
