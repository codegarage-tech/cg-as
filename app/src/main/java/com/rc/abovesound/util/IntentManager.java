package com.rc.abovesound.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class IntentManager {

    public static void openFacebookPageOrProfile(Context context, String facebookPageOrProfileID) {
        String socialLink = "https://www.facebook.com/" + facebookPageOrProfileID;

        try {
            int versionCode = context.getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            String facebookIntentUri = "";

            if (versionCode >= 3002850) {
                //newer versions of fb app
                Log.d("facebook api", "new");
                facebookIntentUri = "fb://facewebmodal/f?href=" + socialLink;
            } else {
                //older versions of fb app
                Log.d("facebook api", "old");
                facebookIntentUri = "fb://page/" + facebookPageOrProfileID;
            }

            Uri uri = Uri.parse(facebookIntentUri);
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW, uri);
            facebookIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(facebookIntent);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("facebook api", "exception");
            // Facebook is not installed. Open in the browser
            Uri uri = Uri.parse(socialLink);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    public static void openTwitterPageOrProfile(Context context, String twitterPageOrProfileID) {
        String socialLink = "https://twitter.com/" + twitterPageOrProfileID;

        try {
            // Twitter is installed.
            int versionCode = context.getApplicationContext().getPackageManager().getPackageInfo("com.twitter.android", 0).versionCode;

            String twitterIntentUri = "twitter://user?screen_name=" + twitterPageOrProfileID;

            Uri uri = Uri.parse(twitterIntentUri);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, uri);
            twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(twitterIntent);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("twittere api", "exception");
            // Twitter is not installed. Open in the browser
            Uri uri = Uri.parse(socialLink);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    public static void openYoutubePageOrProfile(Context context, String youtubePageOrChannelID) {
        String socialLink = "http://www.youtube.com/user/" + youtubePageOrChannelID;

        try {
            // Youtube is installed.
            int versionCode = context.getApplicationContext().getPackageManager().getPackageInfo("com.google.android.youtube", 0).versionCode;

            Uri uri = Uri.parse(socialLink);
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
            youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(youtubeIntent);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("twittere api", "exception");
            // Youtube is not installed. Open in the browser
            Uri uri = Uri.parse(socialLink);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
