package com.rc.abovesound.activity;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rc.abovesound.R;
import com.rc.abovesound.adapter.OwnMusicListViewAdapter;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.model.ResponseOwnMusic;
import com.rc.abovesound.model.UserData;
import com.rc.abovesound.service.MediaService;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AllUrls;
import com.rc.abovesound.util.AppUtils;
import com.rc.abovesound.util.HttpRequestManager;
import com.rc.abovesound.util.IntentManager;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.util.AllSettingsManager;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class OwnMusicListActivity extends AppCompatActivity {

    TextView tvTitle;
    ListView lvOwnMusic;
    private String TAG = OwnMusicListActivity.class.getSimpleName();
    public Music mMusic;
    UserData intentUser, mUser;
    OwnMusicListViewAdapter musicListViewAdapter;
    ImageView ivBack;
    public boolean isFromMenu = false;
    ImageView ivProfileImage;
    TextView tvUserName, tvUserBio;
    ImageView ivFacebook, ivTwitter, ivYoutube;
    LinearLayout llSocialProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_music_list);

        initOwnMusicListUI();
        initOwnMusicListAction();
    }

    private void initOwnMusicListUI() {

        Intent data = getIntent();

        isFromMenu = data.getBooleanExtra(AllConstants.INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU, false);

        if (isFromMenu) {
            intentUser = data.getParcelableExtra(AllConstants.INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER);
            Log.d(TAG, "user: " + intentUser);
        } else {
            mMusic = data.getParcelableExtra(AllConstants.INTENT_KEY_OWN_MUSIC_LIST_ITEM_MUSIC);
            Log.d(TAG, "music: " + mMusic);
        }

        tvTitle = (TextView) findViewById(R.id.text_title);
        lvOwnMusic = (ListView) findViewById(R.id.lv_own_music);
        ivBack = (ImageView) findViewById(R.id.menu_hamburger);
        ivProfileImage = (ImageView) findViewById(R.id.iv_profile_image);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserBio = (TextView) findViewById(R.id.tv_user_bio);
        ivFacebook = (ImageView) findViewById(R.id.iv_facebook);
        ivTwitter = (ImageView) findViewById(R.id.iv_twitter);
        ivYoutube = (ImageView) findViewById(R.id.iv_youtube);
        llSocialProfile = (LinearLayout) findViewById(R.id.ll_social_profile);

        initUserInfo(intentUser);

        musicListViewAdapter = new OwnMusicListViewAdapter(OwnMusicListActivity.this);
        lvOwnMusic.setAdapter(musicListViewAdapter);

        if (isFromMenu) {
            if (intentUser != null) {
                tvTitle.setText(intentUser.getFirst_name() + " " + intentUser.getLast_name());
                new GetOwnMusicList(OwnMusicListActivity.this, intentUser.getId()).execute();
            }
        } else {
            if (mMusic != null) {
                tvTitle.setText(mMusic.getFirst_name() + " " + mMusic.getLast_name());
                new GetOwnMusicList(OwnMusicListActivity.this, mMusic.getUser_id()).execute();
            }
        }
    }

    private void initUserInfo(final UserData user) {
        if (user != null) {

            Glide
                    .with(OwnMusicListActivity.this)
                    .load(user.getProfile_image())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .apply(new RequestOptions().circleCropTransform())
                    .into(ivProfileImage);

            if (AllSettingsManager.isNullOrEmpty(user.getFirst_name())
                    && AllSettingsManager.isNullOrEmpty(user.getLast_name())) {
                tvUserName.setVisibility(View.GONE);
            } else {
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(user.getFirst_name() + " " + user.getLast_name());
            }

            if (AllSettingsManager.isNullOrEmpty(user.getBio())) {
                tvUserBio.setVisibility(View.GONE);
            } else {
                tvUserBio.setVisibility(View.VISIBLE);
                tvUserBio.setText(user.getBio());
            }

            if (AllSettingsManager.isNullOrEmpty(user.getFacebook())
                    && AllSettingsManager.isNullOrEmpty(user.getTwitter())
                    && AllSettingsManager.isNullOrEmpty(user.getYoutube())) {

                llSocialProfile.setVisibility(View.GONE);
            } else {
                llSocialProfile.setVisibility(View.VISIBLE);

                if (!AllSettingsManager.isNullOrEmpty(user.getFacebook())) {
                    ivFacebook.setVisibility(View.VISIBLE);
                    ivFacebook.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            IntentManager.openFacebookPageOrProfile(OwnMusicListActivity.this, user.getFacebook());
                        }
                    });
                } else {
                    ivFacebook.setVisibility(View.GONE);
                }

                if (!AllSettingsManager.isNullOrEmpty(user.getTwitter())) {
                    ivTwitter.setVisibility(View.VISIBLE);
                    ivTwitter.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            IntentManager.openTwitterPageOrProfile(OwnMusicListActivity.this, user.getTwitter());
                        }
                    });
                } else {
                    ivTwitter.setVisibility(View.GONE);
                }

                if (!AllSettingsManager.isNullOrEmpty(user.getYoutube())) {
                    ivYoutube.setVisibility(View.VISIBLE);
                    ivYoutube.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            IntentManager.openYoutubePageOrProfile(OwnMusicListActivity.this, user.getYoutube());
                        }
                    });
                } else {
                    ivYoutube.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initOwnMusicListAction() {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
    }

    public class GetOwnMusicList extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mSingerUserId = "";

        public GetOwnMusicList(Context context, String singerUserId) {
            this.mContext = context;
            this.mSingerUserId = singerUserId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getOwnMusicListUrl(), AllUrls.getOwnMusicListParameters(mSingerUserId), null);
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseOwnMusic responseData = ResponseOwnMusic.getResponseObject(result.getResult().toString(), ResponseOwnMusic.class);

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from own MUSIC: " + responseData.toString());

                    if (responseData.getProfile().size() > 0) {
                        mUser = responseData.getProfile().get(0);
                        initUserInfo(mUser);
                    }

                    musicListViewAdapter.setData(responseData.getData());
                } else {
                    Toast.makeText(OwnMusicListActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(OwnMusicListActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
//        if (AppUtils.isServiceRunning(getApplicationContext(), MediaService.class)) {
//            Toast.makeText(OwnMusicListActivity.this, getResources().getString(R.string.toast_please_stop_music_before_closing), Toast.LENGTH_SHORT).show();
//        } else {
            super.onBackPressed();
//        }
    }

    /*****************************
     * Broadcast activity update *
     *****************************/
    private void updateUI(Intent intent) {
        if (intent != null && intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE) != null) {

            Music music = intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE);

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
            registerReceiver(broadcastReceiver, new IntentFilter(AllConstants.INTENT_FILTER_OWN_MUSIC_UPDATE));
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
