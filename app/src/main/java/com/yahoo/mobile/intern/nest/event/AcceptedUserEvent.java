package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by cmwang on 8/17/15.
 */
public class AcceptedUserEvent {
    public final List<ParseUser> userList;
    public AcceptedUserEvent(List<ParseUser> list) {
        userList = list;
    }
}
