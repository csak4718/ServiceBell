package com.yahoo.mobile.intern.nest.utils;

/**
 * Created by cmwang on 8/12/15.
 */
public class Common {

    public final static int REQUEST_LOCATION = 5566;
    public final static String TEMP_MESSAGE_ID = "1111";

    /*
        Fragment type
     */
    public final static int BUYER_NEW = 0;
    public final static int BUYER_DONE = 1;
    public final static int SELLER_NEW = 2;
    public final static int SELLER_ACCEPTED = 3;
    public final static int SELLER_DONE = 4;

    public static final int CAMERA_REQUEST = 12345;
    public static final int ACTIVITY_SELECT_IMAGE = 1234;

    public final static int REQUEST_MY_TASK = 4444;
    public final static int REQUEST_CATCH_TASK = 4445;

    public final static String EXTRA_RADIUS = "map_radius";
    public final static String EXTRA_MAP_PATH = "map_path";
    public final static String EXTRA_STATE = "state";
    public final static String EXTRA_LOCATION = "location";
    public final static String EXTRA_SEEKBAR = "seekbar";
    public final static String EXTRA_TASK_ID = "task_id";
    public final static String EXTRA_USER_ID = "user_id";
    public final static String EXTRA_ADDRESS = "address";
    public final static String EXTRA_RECIPIENT_OBJECT_ID = "recipientObjectId";
    public final static String EXTRA_HAS_PIN = "given_pin";
    public final static String EXTRA_LAT = "lat";
    public final static String EXTRA_LONG = "long";
    public final static String EXTRA_TYPE = "type";
    public final static String EXTRA_TITLE = "title";

    /*
     Parse object
     */
    public final static String INSTALLATION_USER = "user";

    public final static String OBJECT_USER_NICK = "nickname";
    public final static String OBJECT_USER_PROFILE_PIC = "profile_pic";
    public final static String OBJECT_USER_FB_NAME = "fbName";
    public final static String OBJECT_USER_FB_ID = "fbId";
    public final static String OBJECT_USER_PIN = "pin";
    public final static String OBJECT_USER_RADIUS = "radius";
    public final static String OBJECT_USER_MY_NEW_QUESTIONS = "myNewQuestions";
    public final static String OBJECT_USER_MY_DONE_QUESTIONS = "myDoneQuestions";
    public final static String OBJECT_USER_ACCEPTED_QUESTIONS = "acceptedQuestions";
    public final static String OBJECT_USER_CATCH_QUESTIONS = "catchQuestions";
    public final static String OBJECT_USER_FRIENDS = "friends";
    public final static String OBJECT_USER_DONE_QUESTIONS = "doneQuestions";
    public final static String OBJECT_USER_PHONE = "phone";
    public final static String OBJECT_USER_ADDRESS = "address";
    public final static String OBJECT_USER_CATEGORY = "category";
    public final static String OBJECT_USER_OTHERS = "others";
    public final static String OBJECT_USER_MAP_PIC = "map_pic";
    public final static String OBJECT_USER_ACCEPT="acceptTask";
    public final static String OBJECT_USER_RATING="rating";
    public final static String OBJECT_USER_RATENUM = "votenum";
    public final static String OBJECT_USER_FIRST = "firstsentence";

    public final static String OBJECT_QUESTION = "Question";
    public final static String OBJECT_QUESTION_USER = "user";
    public final static String OBJECT_QUESTION_TITLE = "title";
    public final static String OBJECT_QUESTION_CONTENT = "content";
    public final static String OBJECT_QUESTION_PIN = "pin";
    public final static String OBJECT_QUESTION_EXPIRE_DATE = "date";
    public final static String OBJECT_QUESTION_TIME = "time";
    public final static String OBJECT_QUESTION_ADDRESS = "address";
    public final static String OBJECT_QUESTION_ACCEPTED_USER = "acceptedUser";
    public final static String OBJECT_QUESTION_DONE_USER = "doneUser";
    public final static String OBJECT_QUESTION_CATEGORY = "category";
    public final static String OBJECT_QUESTION_RATED = "rated";
    public final static String OBJECT_QUESTION_PICTURE = "picture";

    public final static String OBJECT_CATEGORY = "Category";
    public final static String OBJECT_CATEGORY_TITLE = "title";
    public final static String OBJECT_CATEGORY_ID = "cid";

    /*
     Cloud code function
     */
    public final static String CLOUD_NOTIFY_ACCEPT = "notifySellerAccept";
    public final static String CLOUD_NOTIFY_ACCEPT_SENDERNAME = "senderName";
    public final static String CLOUD_NOTIFY_ACCEPT_BUYERID = "buyerId";

    public final static String PATH_MAP = "map.png";

}
