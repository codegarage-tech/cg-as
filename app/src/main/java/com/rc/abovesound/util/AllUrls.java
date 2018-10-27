package com.rc.abovesound.util;

import android.util.Log;

import org.json.JSONObject;

/**
 * Md. Rashadul Alam
 */
public class AllUrls {

    private static String TAG = AllUrls.class.getSimpleName();
    private static final String BASE_URL = "http://abovesoundusa.com/above-sound-users/index.php/";

    public static String getLoginUrl() {
        String url = BASE_URL + "user/login";
        Log.d(TAG, "getLoginUrl: " + url);
        return url;
    }

    public static JSONObject getLoginParameters(String email, String password) {
        JSONObject params = HttpRequestManager.HttpParameter.getInstance()
                .addJSONParam("email", email)
                .addJSONParam("password", password)
                .getJSONParam();
        Log.d(TAG, "getLoginParameters: " + params.toString());
        return params;
    }

    public static String getSignUpUrl() {
        String url = BASE_URL + "user/create";
        Log.d(TAG, "getSignUpUrl: " + url);
        return url;
    }

    public static JSONObject getSignUpParameters(String email, String password, String firstName, String lastName
            , String city, String bio, String facebookID, String twitterID, String youtubeChannelID) {
        JSONObject params = HttpRequestManager.HttpParameter.getInstance()
                .addJSONParam("email", email)
                .addJSONParam("password", password)
                .addJSONParam("first_name", firstName)
                .addJSONParam("last_name", lastName)
                .addJSONParam("city", city)
                .addJSONParam("bio", bio)
                .addJSONParam("facebook", facebookID)
                .addJSONParam("twitter", twitterID)
                .addJSONParam("id", 0)
                .addJSONParam("youtube", youtubeChannelID)
                .getJSONParam();
        Log.d(TAG, "getSignUpParameters: " + params.toString());
        return params;
    }

    public static String getUserDetailUrl(String userId) {
        String url = BASE_URL + "user/details/" + userId;
        Log.d(TAG, "getUserDetailUrl: " + url);
        return url;
    }

    public static String getUpdateUserUrl() {
        String url = BASE_URL + "user/create";
        Log.d(TAG, "getUpdateUserUrl: " + url);
        return url;
    }

    public static JSONObject getUpdateUserParameters(String userID, String email, String password, String firstName, String lastName
            , String city, String bio, String facebookID, String twitterID, String youtubeChannelID) {
        JSONObject params = HttpRequestManager.HttpParameter.getInstance()
                .addJSONParam("id", userID)
                .addJSONParam("email", email)
                .addJSONParam("password", password)
                .addJSONParam("first_name", firstName)
                .addJSONParam("last_name", lastName)
                .addJSONParam("city", city)
                .addJSONParam("bio", bio)
                .addJSONParam("facebook", facebookID)
                .addJSONParam("twitter", twitterID)
                .addJSONParam("youtube", youtubeChannelID)
                .getJSONParam();
        Log.d(TAG, "getUpdateUserParameters: " + params.toString());
        return params;
    }

    public static String getAllCityWithCountryUrl() {
        String url = BASE_URL + "location/get_all_cities_list";
        Log.d(TAG, "getAllCityWithCountryUrl: " + url);
        return url;
    }

    public static String getMusicCategoriesUrl() {
        String url = BASE_URL + "music/category_details";
        Log.d(TAG, "getMusicCategoriesUrl: " + url);
        return url;
    }

    public static String getMusicListUrl() {
        String url = BASE_URL + "music/music_list";
        Log.d(TAG, "getMusicListUrl: " + url);
        return url;
    }

    public static JSONObject getMusicListParameters(String userId, String categoryId, String stateId) {
        JSONObject params = HttpRequestManager.HttpParameter.getInstance()
                .addJSONParam("category_id", categoryId)
                .addJSONParam("user_id", userId)
                .addJSONParam("state_id", stateId)
                .getJSONParam();
        Log.d(TAG, "getMusicListParameters: " + params.toString());
        return params;
    }

    public static String getUserBuyUrl() {
        String url = BASE_URL + "user_buy/add";
        Log.d(TAG, "getUserBuyUrl: " + url);
        return url;
    }

    public static JSONObject getUserBuyParameters(String userId, String musicId, String fullyPaid) {
        JSONObject params = HttpRequestManager.HttpParameter.getInstance()
                .addJSONParam("user_id", userId)
                .addJSONParam("music_id", musicId)
                .addJSONParam("fully_paid", fullyPaid)
                .getJSONParam();
        Log.d(TAG, "getUserBuyParameters: " + params.toString());
        return params;
    }

    public static String getOwnMusicListUrl() {
        String url = BASE_URL + "music/own_music_list";
        Log.d(TAG, "getOwnMusicListUrl: " + url);
        return url;
    }

    public static JSONObject getOwnMusicListParameters(String userId) {
        JSONObject params = HttpRequestManager.HttpParameter.getInstance()
                .addJSONParam("user_id", userId)
                .getJSONParam();
        Log.d(TAG, "getOwnMusicListParameters: " + params.toString());
        return params;
    }

    public static String getBoughtMusicListUrl(String userId) {
        String url = BASE_URL + "music/bought_music_list/"+userId;
        Log.d(TAG, "getBoughtMusicListUrl: " + url);
        return url;
    }
}
