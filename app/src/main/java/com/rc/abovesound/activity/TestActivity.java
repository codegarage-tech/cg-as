package com.rc.abovesound.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.rc.abovesound.R;
import com.rc.abovesound.adapter.DownloadInfoAdapter;
import com.rc.abovesound.adapter.MusicListViewAdapter;
import com.rc.abovesound.fragment.FilterFragment;
import com.rc.abovesound.model.City;
import com.rc.abovesound.model.DownloadInfo;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.model.MusicCategory;
import com.rc.abovesound.model.MusicCategoryData;
import com.rc.abovesound.model.ResponseCountry;
import com.rc.abovesound.model.ResponseMusic;
import com.rc.abovesound.model.TimeZone;
import com.rc.abovesound.model.UserData;
import com.rc.abovesound.service.MediaService;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AllUrls;
import com.rc.abovesound.util.AppUtils;
import com.rc.abovesound.util.HttpRequestManager;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rc.abovesound.util.AllConstants.INTENT_FILTER_ACTIVITY_UPDATE;
import static com.rc.abovesound.util.AllConstants.INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU;
import static com.rc.abovesound.util.AllConstants.INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER;
import static com.rc.abovesound.util.AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE;
import static com.rc.abovesound.util.AllConstants.KEY_INTENT_EXTRA_ZONE;
import static com.rc.abovesound.util.AllConstants.REQUEST_CODE_PAYPAL;
import static com.rc.abovesound.util.AllConstants.REQUEST_CODE_ZONE;
import static com.rc.abovesound.util.AllConstants.SESSION_CITY_WITH_COUNTRY;
import static com.rc.abovesound.util.AllConstants.SESSION_IS_USER_LOGGED_IN;
import static com.rc.abovesound.util.AllConstants.SESSION_MUSIC_CATEGORY;
import static com.rc.abovesound.util.AllConstants.SESSION_SELECTED_CITY;
import static com.rc.abovesound.util.AllConstants.SESSION_SELECTED_ZONE;
import static com.rc.abovesound.util.AllConstants.SESSION_USER_DATA;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class TestActivity extends AppCompatActivity {

    private final static String TAG = TestActivity.class.getSimpleName();
    ListView listView;
    DownloadInfoAdapter downloadInfoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = (ListView) findViewById(R.id.lv_music);

        List<DownloadInfo> downloadInfo = new ArrayList<DownloadInfo>();
        for(int i = 0; i < 50; ++i) {
            downloadInfo.add(new DownloadInfo("File " + i, "http://ntstx.com/music_app/uploads/04.Aashona-Arijit_Singh_And_Prashmita_Paul_FusionBD.Com.mp3"));
        }
        downloadInfoAdapter = new DownloadInfoAdapter(getApplicationContext(), downloadInfo);
        listView.setAdapter(downloadInfoAdapter);
    }

    public class GetMusicList extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mUserId = "", mMusicCategory = "", mStateId = "";

        public GetMusicList(Context context, String userId, String musicCategory, String stateId) {
            this.mContext = context;
            this.mUserId = userId;
            this.mMusicCategory = musicCategory;
            this.mStateId = stateId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getMusicListUrl(), AllUrls.getMusicListParameters(mUserId, mMusicCategory, mStateId), null);
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseMusic responseData = ResponseMusic.getResponseObject(result.getResult().toString(), ResponseMusic.class);

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from MUSIC: " + responseData.getData().toString());
//                    String modifiedMusicList = "{" + "data=" + responseData.getData().toString() + "}";
//                    SessionManager.setStringSetting(mContext, SESSION_MUSIC_LIST, modifiedMusicList);
//                    Log.d(TAG, "success response from session: " + SessionManager.getStringSetting(mContext, SESSION_MUSIC_LIST));

           } else {
//                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TestActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*****************************
     * Broadcast activity update *
     *****************************/
    private void updateUI(Intent intent) {
        if (intent != null && intent.getParcelableExtra(KEY_INTENT_EXTRA_MUSIC_UPDATE) != null) {

            DownloadInfo music = intent.getParcelableExtra(KEY_INTENT_EXTRA_MUSIC_UPDATE);

            if (downloadInfoAdapter != null) {
                downloadInfoAdapter.updateDownloadInfo(music);
            }
        }
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(broadcastReceiver, new IntentFilter(INTENT_FILTER_ACTIVITY_UPDATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
