package com.reversecoder.mh.util;

import android.util.Log;

import org.json.JSONObject;

/**
 * Md. Rashadul Alam
 */
public class AllUrls {

    private static String TAG = AllUrls.class.getSimpleName();
    private static final String BASE_URL = "http://ntstx.com/music_app/index.php/";

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

//    {
//        "status": 1,
//            "data": [
//        {
//            "music_title": "08.Phir_Wohi-Qaidi_Band_FusionBD.Com.mp3",
//                "description": "test music descriptin",
//                "file_path": "http://ntstx.com/music_app/uploads/08.Phir_Wohi-Qaidi_Band_FusionBD.Com.mp3",
//                "is_paid": "1",
//                "price": "5.00",
//                "id": "1",
//                "full_paid_price": "21.00"
//        },
//        {
//            "music_title": "Mere Payari Bindu ",
//                "description": "test music description",
//                "file_path": "http://ntstx.com/music_app/uploads/Haareya-Meri_Pyaari_Bindu_FusionBD.Com.mp3",
//                "is_paid": "0",
//                "price": "2.00",
//                "id": "2",
//                "full_paid_price": "4.00"
//        },
//        {
//            "music_title": "Gulabi Noor",
//                "description": "test music description",
//                "file_path": "http://ntstx.com/music_app/uploads/Gulabi_2.0-Noor_FusionBD.Com.mp3",
//                "is_paid": "0",
//                "price": "2.70",
//                "id": "4",
//                "full_paid_price": "7.00"
//        },
//        {
//            "music_title": "Ashona by Arijit singh",
//                "description": "",
//                "file_path": "http://ntstx.com/music_app/uploads/04.Aashona-Arijit_Singh_And_Prashmita_Paul_FusionBD.Com.mp3",
//                "is_paid": "0",
//                "price": "1.40",
//                "id": "6",
//                "full_paid_price": "3.00"
//        },
//        {
//            "music_title": "Sorry Sorry ",
//                "description": "nice song",
//                "file_path": "http://ntstx.com/music_app/uploads/07.ABCD-Sorry_Sorry.mp3",
//                "is_paid": "1",
//                "price": "12.00",
//                "id": "9",
//                "full_paid_price": "65.00"
//        }
//	],
//        "profile": [
//        {
//            "id": "1",
//                "first_name": "nissoy",
//                "last_name": "B",
//                "email": "niloy.cste@gmail.com",
//                "city": "dhaka",
//                "password": "123",
//                "state": "1",
//                "zipcode": "s",
//                "website": "d",
//                "youtube": "test",
//                "facebook": "test",
//                "twitter": "test",
//                "instragram": "sd",
//                "bio": "test"
//        }
//	]
//    }
}
