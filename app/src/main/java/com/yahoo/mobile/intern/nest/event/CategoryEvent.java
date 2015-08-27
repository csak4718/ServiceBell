package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseObject;

import java.util.List;


public class CategoryEvent {
    public final List<ParseObject> categoryList;
    public CategoryEvent(List<ParseObject> list) {
        categoryList = list;
    }
}
