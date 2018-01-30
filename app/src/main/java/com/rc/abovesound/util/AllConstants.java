package com.rc.abovesound.util;

import com.rc.abovesound.model.SpinnerItem;

import java.util.ArrayList;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class AllConstants {

    //Request code
    public static final int REQUEST_CODE_PAYPAL = 420;
    public static final int REQUEST_CODE_ZONE = 4200;

    //Intent extra
    public static final String KEY_INTENT_EXTRA_ACTION = "KEY_INTENT_EXTRA_ACTION";
    public static final String KEY_INTENT_EXTRA_MUSIC = "KEY_INTENT_EXTRA_MUSIC";
    public static final String KEY_INTENT_EXTRA_MUSIC_UPDATE = "KEY_INTENT_EXTRA_MUSIC_UPDATE";
    public static final String KEY_INTENT_EXTRA_ZONE = "KEY_INTENT_EXTRA_ZONE";
    public static final int EXTRA_ACTION_START = 0;
    public static final int EXTRA_ACTION_STOP = 1;

    //Intent filter
    public static final String INTENT_FILTER_ACTIVITY_UPDATE = "INTENT_FILTER_ACTIVITY_UPDATE";

    //Intent key
    public static final String INTENT_KEY_PAYPAL_MUSIC_ITEM = "INTENT_KEY_PAYPAL_MUSIC_ITEM";
    public static final String INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM = "INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM";
    public static final String INTENT_KEY_OWN_MUSIC_LIST_ITEM_MUSIC = "INTENT_KEY_OWN_MUSIC_LIST_ITEM_MUSIC";
    public static final String INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU = "INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU";
    public static final String INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER= "INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER";

    //Media playback
    public static final int MEDIA_PLAYER_RUNNING = 1;
    public static final int MEDIA_PLAYBACK_STOPPED = 2;
    public static final int MEDIA_PLAYBACK_FINISHED = 3;
    public static final int MEDIA_PLAYBACK_PAID = 4;
    public static final int DEFAULT_PAID_PLAYBACK = 30;

    //Session key
    public static final String SESSION_IS_USER_LOGGED_IN = "SESSION_IS_USER_LOGGED_IN";
    public static final String SESSION_USER_DATA = "SESSION_USER_DATA";
    public static final String SESSION_CITY_WITH_COUNTRY = "SESSION_CITY_WITH_COUNTRY";
    public static final String SESSION_MUSIC_CATEGORY = "SESSION_MUSIC_CATEGORY";
    public static final String SESSION_SELECTED_ZONE = "SESSION_SELECTED_ZONE";
    public static final String SESSION_SELECTED_CITY = "SESSION_SELECTED_CITY";

    public static ArrayList<SpinnerItem> getGenderData() {

        ArrayList<SpinnerItem> genders = new ArrayList<SpinnerItem>();
        genders.add(new SpinnerItem("1", "Male"));
        genders.add(new SpinnerItem("2", "Female"));

        return genders;
    }

    public static String getDefaultCountryData() {
        String countryData = "{\"status\":1,\"data\":[{\"id\":\"1\",\"name\":\"USA\",\"timezone\":[{\"id\":\"1\",\"name\":\"Pacific Time Zone\",\"city\":[{\"id\":\"9\",\"name\":\"California\"},{\"id\":\"32\",\"name\":\"Nevada\"},{\"id\":\"41\",\"name\":\"Oregon\"},{\"id\":\"51\",\"name\":\"Washington\"}]},{\"id\":\"2\",\"name\":\"Mountain Time Zone\",\"city\":[{\"id\":\"7\",\"name\":\"Arizona\"},{\"id\":\"10\",\"name\":\"Colorado\"},{\"id\":\"16\",\"name\":\"Idaho\"},{\"id\":\"30\",\"name\":\"Montana\"},{\"id\":\"35\",\"name\":\"New Mexico\"},{\"id\":\"48\",\"name\":\"Utah\"},{\"id\":\"54\",\"name\":\"Wyoming\"}]},{\"id\":\"3\",\"name\":\"Central Time Zone\",\"city\":[{\"id\":\"5\",\"name\":\"Alabama\"},{\"id\":\"8\",\"name\":\"Arkansas\"},{\"id\":\"17\",\"name\":\"Illinois\"},{\"id\":\"19\",\"name\":\"Iowa\"},{\"id\":\"20\",\"name\":\"Kansas\"},{\"id\":\"21\",\"name\":\"Kentucky\"},{\"id\":\"22\",\"name\":\"Louisiana\"},{\"id\":\"27\",\"name\":\"Minnesota\"},{\"id\":\"28\",\"name\":\"Mississippi\"},{\"id\":\"29\",\"name\":\"Missouri\"},{\"id\":\"31\",\"name\":\"Nebraska\"},{\"id\":\"38\",\"name\":\"North Dakota\"},{\"id\":\"40\",\"name\":\"Oklahoma\"},{\"id\":\"45\",\"name\":\"South Dakota\"},{\"id\":\"46\",\"name\":\"Tennessee\"},{\"id\":\"47\",\"name\":\"Texas\"},{\"id\":\"53\",\"name\":\"Wisconsin\"}]},{\"id\":\"4\",\"name\":\"Eastern Time Zone\",\"city\":[{\"id\":\"6\",\"name\":\"Alaska\"},{\"id\":\"11\",\"name\":\"Connecticut\"},{\"id\":\"12\",\"name\":\"Delaware\"},{\"id\":\"13\",\"name\":\"Florida\"},{\"id\":\"14\",\"name\":\"Georgia\"},{\"id\":\"15\",\"name\":\"Hawaii\"},{\"id\":\"18\",\"name\":\"Indiana\"},{\"id\":\"23\",\"name\":\"Maine\"},{\"id\":\"24\",\"name\":\"Maryland\"},{\"id\":\"25\",\"name\":\"Massachusetts\"},{\"id\":\"26\",\"name\":\"Michigan\"},{\"id\":\"33\",\"name\":\"New Hampshire\"},{\"id\":\"34\",\"name\":\"New Jersey\"},{\"id\":\"36\",\"name\":\"New York\"},{\"id\":\"37\",\"name\":\"North Carolina\"},{\"id\":\"39\",\"name\":\"Ohio\"},{\"id\":\"42\",\"name\":\"Pennsylvania\"},{\"id\":\"43\",\"name\":\"Rhode Island\"},{\"id\":\"44\",\"name\":\"South Carolina\"},{\"id\":\"49\",\"name\":\"Vermont\"},{\"id\":\"50\",\"name\":\"Virginia\"},{\"id\":\"52\",\"name\":\"West Virginia\"}]}]}]}";
        return countryData;
    }

    public static String getDefaultMusicCategoryData() {
        String musicCategoryData = "[{\"id\":\"1\",\"name\":\"Hip Hop\"},{\"id\":\"2\",\"name\":\"Jaaz\"},{\"id\":\"3\",\"name\":\"Rock\"},{\"id\":\"4\",\"name\":\"Band\"},{\"id\":\"5\",\"name\":\"Pop\"},{\"id\":\"6\",\"name\":\"Salsa\"},{\"id\":\"7\",\"name\":\"Soul\"},{\"id\":\"8\",\"name\":\"Ragtime\"}]";
        String modifiedMusicCategory = "{" + "data=" + musicCategoryData + "}";
        return modifiedMusicCategory;
    }
}