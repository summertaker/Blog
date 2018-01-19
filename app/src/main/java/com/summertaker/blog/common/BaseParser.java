package com.summertaker.blog.common;

import com.summertaker.blog.data.Group;
import com.summertaker.blog.data.Member;
import com.summertaker.blog.data.Team;

import java.util.ArrayList;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "== " + this.getClass().getSimpleName();
    }

    public void parseTeam(String html, ArrayList<Team> teamList) {

    }

    public void parseMember(String html, Group group, Team team, ArrayList<Member> members) {

    }
}