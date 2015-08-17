package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 8/17/15.
 */
public class AcceptTaskEvent {
    public final List<ParseObject> taskList;
    public AcceptTaskEvent(List<ParseObject> list) {
        taskList = list;
    }
}
