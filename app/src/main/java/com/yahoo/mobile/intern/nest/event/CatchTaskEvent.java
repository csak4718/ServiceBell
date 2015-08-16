package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 8/16/15.
 */
public class CatchTaskEvent {
    public final List<ParseObject> taskList;
    public CatchTaskEvent(List<ParseObject> list) {
        taskList = list;
    }
}
