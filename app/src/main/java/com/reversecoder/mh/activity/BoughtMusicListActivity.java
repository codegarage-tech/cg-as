package com.reversecoder.mh.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.mh.R;
import com.reversecoder.mh.adapter.BoughtMusicListViewAdapter;
import com.reversecoder.mh.model.Music;
import com.reversecoder.mh.model.ResponseBoughtMusic;
import com.reversecoder.mh.model.UserData;
import com.reversecoder.mh.service.MediaService;
import com.reversecoder.mh.util.AllUrls;
import com.reversecoder.mh.util.AppUtils;
import com.reversecoder.mh.util.HttpRequestManager;

import static com.reversecoder.mh.util.AllConstants.INTENT_FILTER_ACTIVITY_UPDATE;
import static com.reversecoder.mh.util.AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE;
import static com.reversecoder.mh.util.AllConstants.SESSION_USER_DATA;
import static com.reversecoder.mh.util.AppUtils.isServiceRunning;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class BoughtMusicListActivity extends AppCompatActivity {

    TextView tvTitle;
    ListView lvBoughtMusic;
    private String TAG = BoughtMusicListActivity.class.getSimpleName();
    UserData mUser;
    BoughtMusicListViewAdapter musicListViewAdapter;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_music_list);

        initBoughtMusicListUI();
        initBoughtMusicListAction();
    }

    private void initBoughtMusicListUI() {

        tvTitle = (TextView) findViewById(R.id.text_title);
        tvTitle.setText(getString(R.string.title_activity_bought_music_list));
        lvBoughtMusic = (ListView) findViewById(R.id.lv_bought_music);
        ivBack = (ImageView) findViewById(R.id.menu_hamburger);

        musicListViewAdapter = new BoughtMusicListViewAdapter(BoughtMusicListActivity.this);
        lvBoughtMusic.setAdapter(musicListViewAdapter);

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(BoughtMusicListActivity.this, SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(BoughtMusicListActivity.this, SESSION_USER_DATA));
            mUser = UserData.getResponseObject(SessionManager.getStringSetting(BoughtMusicListActivity.this, SESSION_USER_DATA), UserData.class);

            new GetBoughtMusicList(BoughtMusicListActivity.this, mUser.getId()).execute();
        }
    }

    private void initBoughtMusicListAction() {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
    }

    public class GetBoughtMusicList extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mUserId = "";

        public GetBoughtMusicList(Context context, String singerUserId) {
            this.mContext = context;
            this.mUserId = singerUserId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doGetRequest(AllUrls.getBoughtMusicListUrl(mUserId));
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseBoughtMusic responseData = ResponseBoughtMusic.getResponseObject(result.getResult().toString(), ResponseBoughtMusic.class);

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from own MUSIC: " + responseData.toString());

                    musicListViewAdapter.setData(responseData.getData());
                } else {
                    Toast.makeText(BoughtMusicListActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BoughtMusicListActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (isServiceRunning(getApplicationContext(), MediaService.class)) {
            Toast.makeText(BoughtMusicListActivity.this, getResources().getString(R.string.toast_please_stop_music_before_closing), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    /*****************************
     * Broadcast activity update *
     *****************************/
    private void updateUI(Intent intent) {
        if (intent != null && intent.getParcelableExtra(KEY_INTENT_EXTRA_MUSIC_UPDATE) != null) {

            Music music = intent.getParcelableExtra(KEY_INTENT_EXTRA_MUSIC_UPDATE);

            if (musicListViewAdapter != null) {
                musicListViewAdapter.updateMusic(music);
            }
//            Toast.makeText(HomeActivity.this, AppUtils.milliSecondsToTimer(music.getLastPlayed()) + "/" + AppUtils.milliSecondsToTimer(music.getTotalTime()), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (musicListViewAdapter != null) {
            musicListViewAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }
}
