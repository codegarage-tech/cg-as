package com.reversecoder.mh.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.mh.R;
import com.reversecoder.mh.adapter.CommonSpinnerAdapter;
import com.reversecoder.mh.adapter.MusicListViewAdapter;
import com.reversecoder.mh.model.City;
import com.reversecoder.mh.model.Music;
import com.reversecoder.mh.model.MusicCategoryData;
import com.reversecoder.mh.model.ResponseCountry;
import com.reversecoder.mh.model.ResponseMusic;
import com.reversecoder.mh.model.ResponseMusicCategory;
import com.reversecoder.mh.model.SpinnerItem;
import com.reversecoder.mh.model.TimeZone;
import com.reversecoder.mh.model.UserData;
import com.reversecoder.mh.service.MediaService;
import com.reversecoder.mh.util.AllConstants;
import com.reversecoder.mh.util.AllUrls;
import com.reversecoder.mh.util.AppUtils;
import com.reversecoder.mh.util.HttpRequestManager;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import static com.reversecoder.mh.util.AllConstants.INTENT_FILTER_ACTIVITY_UPDATE;
import static com.reversecoder.mh.util.AllConstants.INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU;
import static com.reversecoder.mh.util.AllConstants.INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER;
import static com.reversecoder.mh.util.AllConstants.KEY_INTENT_EXTRA_MUSIC_UPDATE;
import static com.reversecoder.mh.util.AllConstants.SESSION_CITY_WITH_COUNTRY;
import static com.reversecoder.mh.util.AllConstants.SESSION_IS_USER_LOGGED_IN;
import static com.reversecoder.mh.util.AllConstants.SESSION_MUSIC_CATEGORY;
import static com.reversecoder.mh.util.AllConstants.SESSION_SELECTED_CITY;
import static com.reversecoder.mh.util.AllConstants.SESSION_SELECTED_ZONE;
import static com.reversecoder.mh.util.AllConstants.SESSION_USER_DATA;
import static com.reversecoder.mh.util.AppUtils.isServiceRunning;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class HomeActivity extends AppCompatActivity {

    private static final long RIPPLE_DURATION = 250;

    Toolbar toolbar;
    FrameLayout root;
    ImageView contentHamburger;
    GuillotineAnimation guillotineAnimation;
    TextView tvTitle;
    LinearLayout llLogOut, llHome, llProfile, llOwnMusic, llBoughtMusic;
    private static final String TAG = HomeActivity.class.getSimpleName();
    Spinner spinnerMusicCategory, spinnerCity;
    CommonSpinnerAdapter spinnerMusicCategoryAdapter, spinnerCityAdapter;
    MusicCategoryData wrapperMusicCategoryData;
    ResponseCountry wrapperCityWithCountryData;
    UserData user;
    Button btnConfirm;
    ListView lvMusic;
    ProgressDialog loadingDialog;
    MusicListViewAdapter musicListViewAdapter;
    TimeZone timeZone;
    String strTimeZone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        initMenuAction();
    }

    private void initUI() {

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(HomeActivity.this, SESSION_USER_DATA));
            user = UserData.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_USER_DATA), UserData.class);
        }

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_ZONE))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_ZONE));
            strTimeZone = SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_ZONE);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (FrameLayout) findViewById(R.id.root);
        contentHamburger = (ImageView) findViewById(R.id.content_hamburger);
        tvTitle = (TextView) findViewById(R.id.text_title);

        contentHamburger.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_activity_home));

        initMenu();

        llLogOut = (LinearLayout) findViewById(R.id.ll_logout);
        llHome = (LinearLayout) findViewById(R.id.ll_home);
        llProfile = (LinearLayout) findViewById(R.id.ll_profile);
        llOwnMusic = (LinearLayout) findViewById(R.id.ll_own_music);
        llBoughtMusic = (LinearLayout) findViewById(R.id.ll_bought_music);

        initSpinnerData();

        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        lvMusic = (ListView) findViewById(R.id.lv_music);

        musicListViewAdapter = new MusicListViewAdapter(HomeActivity.this);
        lvMusic.setAdapter(musicListViewAdapter);
    }

    private void initMenu() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

//        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.menu_screen, null);
        root.addView(guillotineMenu);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.menu_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();
    }

    private void initMenuAction() {

        llLogOut.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(HomeActivity.this, MediaService.class)) {
                    Intent intentMediaService = new Intent(HomeActivity.this, MediaService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    stopService(intentMediaService);
                }
                SessionManager.setBooleanSetting(HomeActivity.this, SESSION_IS_USER_LOGGED_IN, false);
                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });

        llHome.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                closeMenu();
            }
        });

        llOwnMusic.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(HomeActivity.this, MediaService.class)) {
                    Toast.makeText(HomeActivity.this, getString(R.string.toast_please_stop_music_before_checking_list), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!NetworkManager.isConnected(HomeActivity.this)) {
                    Toast.makeText(HomeActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentOwnMusicList = new Intent(HomeActivity.this, OwnMusicListActivity.class);
                intentOwnMusicList.putExtra(INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER, user);
                intentOwnMusicList.putExtra(INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU, true);
                startActivity(intentOwnMusicList);
            }
        });

        llBoughtMusic.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Toast.makeText(HomeActivity.this, "Feature under development at server side", Toast.LENGTH_SHORT).show();
            }
        });

        llProfile.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City item = (City) parent.getItemAtPosition(position);
//                Toast.makeText(SignUpActivity.this, item.getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(HomeActivity.this, MediaService.class)) {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_please_stop_music_before_searching), Toast.LENGTH_SHORT).show();
                    return;
                }
                String spinnerSelectedMusicCategoryId = ((SpinnerItem) spinnerMusicCategory.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_music_category)) ? "" : ((SpinnerItem) spinnerMusicCategory.getSelectedItem()).getId();
                String spinnerSelectedCityId = ((City) spinnerCity.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_city)) ? "" : ((City) spinnerCity.getSelectedItem()).getId();

                if (spinnerSelectedMusicCategoryId.equalsIgnoreCase("")) {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_empty_music_category_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinnerSelectedCityId.equalsIgnoreCase("")) {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_empty_city_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetworkManager.isConnected(HomeActivity.this)) {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                new GetMusicList(HomeActivity.this, user.getId(), spinnerSelectedMusicCategoryId, spinnerSelectedCityId).execute();
            }
        });

    }

    private void closeMenu() {
        if (guillotineAnimation.isOpened()) {
            guillotineAnimation.close();
        }
    }

    @Override
    public void onBackPressed() {
        if (guillotineAnimation.isOpened()) {
            guillotineAnimation.close();
        } else if (isServiceRunning(HomeActivity.this, MediaService.class)) {
            Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_please_stop_music_before_closing), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    /***********************
     * Update spinner data *
     ***********************/
    private void initSpinnerData() {
        spinnerMusicCategory = (Spinner) findViewById(R.id.spinner_music_category);
        spinnerCity = (Spinner) findViewById(R.id.spinner_city);

        spinnerMusicCategoryAdapter = new CommonSpinnerAdapter(HomeActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY);
        spinnerMusicCategory.setAdapter(spinnerMusicCategoryAdapter);

        spinnerCityAdapter = new CommonSpinnerAdapter(HomeActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.CITY);
        spinnerCity.setAdapter(spinnerCityAdapter);

        //Get spinner data
        if (NetworkManager.isConnected(HomeActivity.this)) {
            new GetAllMusicCategory(HomeActivity.this).execute();
        } else {
            if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY))) {
                SessionManager.setStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY, AllConstants.getDefaultMusicCategoryData());
            }
            setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY);

            if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY))) {
                SessionManager.setStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY, AllConstants.getDefaultCountryData());
            }

            Toast.makeText(HomeActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }

        setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE.CITY);
        spinnerCity.setSelection(spinnerCityAdapter.getItemPosition(SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_CITY)));
    }

    public class GetAllMusicCategory extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;

        public GetAllMusicCategory(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doGetRequest(AllUrls.getMusicCategoriesUrl());
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseMusicCategory responseData = ResponseMusicCategory.getResponseObject(result.getResult().toString(), ResponseMusicCategory.class);
                Log.d(TAG, "success response from object: " + responseData.toString());

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from cityWithCountry: " + responseData.getData().toString());
                    String modifiedMusicCategory = "{" + "data=" + responseData.getData().toString() + "}";
                    SessionManager.setStringSetting(mContext, SESSION_MUSIC_CATEGORY, modifiedMusicCategory);
                    Log.d(TAG, "success response from session: " + SessionManager.getStringSetting(mContext, SESSION_MUSIC_CATEGORY));

                    setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY);
                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
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
            loadingDialog = new ProgressDialog(mContext);
            loadingDialog.setMessage(getResources().getString(
                    R.string.dialog_loading));
            loadingDialog.setIndeterminate(false);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    if (loadingDialog != null
                            && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getMusicListUrl(), AllUrls.getMusicListParameters(mUserId, mMusicCategory, mStateId), null);
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {

            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }

            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseMusic responseData = ResponseMusic.getResponseObject(result.getResult().toString(), ResponseMusic.class);

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from MUSIC: " + responseData.getData().toString());
//                    String modifiedMusicList = "{" + "data=" + responseData.getData().toString() + "}";
//                    SessionManager.setStringSetting(mContext, SESSION_MUSIC_LIST, modifiedMusicList);
//                    Log.d(TAG, "success response from session: " + SessionManager.getStringSetting(mContext, SESSION_MUSIC_LIST));

                    musicListViewAdapter.setData(responseData.getData());
                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE adapterType) {

        if (adapterType == CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY) {
            //set music category spinner data
            if (SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY) != null) {
                wrapperMusicCategoryData = MusicCategoryData.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY), MusicCategoryData.class);

                spinnerMusicCategoryAdapter.setData(wrapperMusicCategoryData.getMusicCategory());
                spinnerMusicCategory.setSelection(0);
            }
        } else if (adapterType == CommonSpinnerAdapter.ADAPTER_TYPE.CITY) {
            //set city spinner data
            if (SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY) != null) {
                wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);
                Log.d(TAG, "before setting spinner: " + wrapperCityWithCountryData.toString());

                timeZone = wrapperCityWithCountryData.getAnyTimezone(strTimeZone);
                spinnerCityAdapter.setData(timeZone.getCity());
                spinnerCity.setSelection(0);

//                if (!AppUtils.isNullOrEmpty(user.getCity())) {
//                    spinnerCity.setSelection(spinnerCityAdapter.getItemPosition(user.getCity()));
//                }
            }
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
