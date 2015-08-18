package com.yahoo.mobile.intern.nest.event;

import com.parse.ParseUser;

/**
 * Created by dwkung on 8/18/15.
 */
public class RecipientEvent {
    public final ParseUser recipient;
    public RecipientEvent(ParseUser r){
        recipient = r;
    }
}
