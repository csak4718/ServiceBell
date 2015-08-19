package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by dwkung on 8/18/15.
 */
public class FriendsEvent {
    public final List<ParseUser> friendsList;
    public FriendsEvent(List<ParseUser> list){
        friendsList = list;
    }
}
