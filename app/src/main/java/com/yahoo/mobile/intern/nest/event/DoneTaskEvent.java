package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 8/18/15.
 */
public class DoneTaskEvent {
    public final List<ParseObject> taskList;
    public DoneTaskEvent(List<ParseObject> list) {
        taskList = list;
    }
}
