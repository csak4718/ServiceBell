package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/16/15.
 */
public class MyNewTaskEvent {
    public final List<ParseObject> questionList;
    public MyNewTaskEvent(List<ParseObject> list) {
        questionList = list;
    }
}
