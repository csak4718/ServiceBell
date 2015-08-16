package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/16/15.
 */
public class MyTaskEvent {
    public final List<ParseObject> questionList;
    public MyTaskEvent(List<ParseObject> list) {
        questionList = list;
    }
}
