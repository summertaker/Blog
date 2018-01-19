package com.summertaker.blog;

import android.widget.CheckBox;

import com.summertaker.blog.data.Member;

public interface MemberListInterface {

    void onPicutreClick(Member member);

    void onLikeClick(CheckBox checkBox, Member member);

    void onNameClick(Member member);
}
