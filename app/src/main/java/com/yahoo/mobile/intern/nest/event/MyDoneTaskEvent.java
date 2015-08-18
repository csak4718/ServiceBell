package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 8/18/15.
 */
public class MyDoneTaskEvent {
    public final List<ParseObject> taskList;
    public MyDoneTaskEvent(List<ParseObject> list) {
        taskList = list;
    }
}
