package com.reversecoder.mh.activity;

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
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.mh.R;
import com.reversecoder.mh.adapter.MusicListViewAdapter;
import com.reversecoder.mh.fragment.FilterFragment;
import com.reversecoder.mh.model.City;
import com.reversecoder.mh.model.Music;
import com.reversecoder.mh.model.MusicCategory;
import com.reversecoder.mh.model.MusicCategoryData;
import com.reversecoder.mh.model.ResponseCountry;
import com.reversecoder.mh.model.ResponseMusic;
import com.reversecoder.mh.model.TimeZone;
import com.reversecoder.mh.model.UserData;
import com.reversecoder.mh.service.MediaService;
import com.reversecoder.mh.util.AllConstants;
import com.reversecoder.mh.util.AllUrls;
import com.reversecoder.mh.util.AppUtils;
import com.reversecoder.mh.util.HttpRequestManager;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class HomeActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    private static final long RIPPLE_DURATION = 250;

    Toolbar toolbar;
    FrameLayout root;
    ImageView contentHamburger;
    GuillotineAnimation guillotineAnimation;
    TextView tvTitle;
    LinearLayout llLogOut, llHome, llProfile, llOwnMusic, llBoughtMusic, llZone;
    private static final String TAG = HomeActivity.class.getSimpleName();
    //    Spinner spinnerMusicCategory, spinnerCity;
//    CommonSpinnerAdapter spinnerMusicCategoryAdapter, spinnerCityAdapter;
    MusicCategoryData wrapperMusicCategoryData;
    ResponseCountry wrapperCityWithCountryData;
    UserData user;
    //    Button btnConfirm;
    ListView lvMusic;
    ProgressDialog loadingDialog;
    MusicListViewAdapter musicListViewAdapter;
    TimeZone timeZone;
    String strTimeZone;
    String selectedMusicCategory = "", selectedState = "";

    //Fabulous Filter
    private ArrayMap<String, List<String>> appliedFilters = new ArrayMap<>();
    FilterFragment dialogFrag;
    FloatingActionButton fabFilter;
    List<Music> mList = new ArrayList<>();
    List<String> mCategoryKey = new ArrayList<>();
    List<String> mStateKey = new ArrayList<>();
    ArrayList<MusicCategory> mCategory = new ArrayList<>();
    ArrayList<City> mState = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        initActions();
    }

    private void initUI() {

        //Set data from session
        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(HomeActivity.this, SESSION_USER_DATA));
            user = UserData.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_USER_DATA), UserData.class);
        }
        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_ZONE))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_ZONE));
            strTimeZone = SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_ZONE);
        }
        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY))) {
            wrapperMusicCategoryData = MusicCategoryData.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY), MusicCategoryData.class);
            mCategory = wrapperMusicCategoryData.getMusicCategory();
            mCategoryKey = getUniqueCategoryKeys(mCategory);
        }
        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY))) {
            wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);
            Log.d(TAG, "before setting spinner: " + wrapperCityWithCountryData.toString());

            timeZone = wrapperCityWithCountryData.getAnyTimezone(strTimeZone);
            mState = timeZone.getCity();
            mStateKey = getUniqueStateKeys(mState);
        }
        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_CITY))) {
            selectedState = SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_CITY);

            String currentFilterKey = "state";
            if (appliedFilters.get(currentFilterKey) != null && !appliedFilters.get(currentFilterKey).contains(selectedState)) {
                appliedFilters.get(currentFilterKey).add(selectedState);
            } else {
                List<String> temp = new ArrayList<>();
                temp.add(selectedState);
                appliedFilters.put(currentFilterKey, temp);
            }
        }

        //Initialize views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fabFilter = (FloatingActionButton) findViewById(R.id.fab_filter);
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
        llZone = (LinearLayout) findViewById(R.id.ll_zone);

//        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        lvMusic = (ListView) findViewById(R.id.lv_music);

        musicListViewAdapter = new MusicListViewAdapter(HomeActivity.this);
        lvMusic.setAdapter(musicListViewAdapter);

//        initSpinnerData();
        searchMusic(getSelectedMusicCategory(selectedMusicCategory).getId(), getSelectedCity(selectedState).getId());
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

        ((TextView) guillotineMenu.findViewById(R.id.text_title)).setText(getString(R.string.title_menu));
    }

    private void initActions() {
        initMenuAction();

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isServiceRunning(getApplicationContext(), MediaService.class)) {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_please_stop_music_before_searching), Toast.LENGTH_SHORT).show();
                    return;
                }

                dialogFrag = FilterFragment.newInstance(appliedFilters);
                dialogFrag.setParentFab(fabFilter);

                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
    }

    private void initMenuAction() {

        llLogOut.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(getApplicationContext(), MediaService.class)) {
                    Intent intentMediaService = new Intent(getApplicationContext(), MediaService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    getApplicationContext().stopService(intentMediaService);
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
                if (isServiceRunning(getApplicationContext(), MediaService.class)) {
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
                if (isServiceRunning(getApplicationContext(), MediaService.class)) {
                    Toast.makeText(HomeActivity.this, getString(R.string.toast_please_stop_music_before_checking_list), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!NetworkManager.isConnected(HomeActivity.this)) {
                    Toast.makeText(HomeActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentBoughtMusicList = new Intent(HomeActivity.this, BoughtMusicListActivity.class);
                intentBoughtMusicList.putExtra(INTENT_KEY_OWN_MUSIC_LIST_ITEM_USER, user);
                intentBoughtMusicList.putExtra(INTENT_KEY_OWN_MUSIC_LIST_FROM_MENU, true);
                startActivity(intentBoughtMusicList);
            }
        });

        llProfile.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        llZone.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent zoneIntent = new Intent(HomeActivity.this, ZoneActivity.class);
                startActivity(zoneIntent);
            }
        });

//        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                City item = (City) parent.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        spinnerMusicCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                SpinnerItem item = (SpinnerItem) parent.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        btnConfirm.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                searchMusic();
//            }
//        });

    }

    private void searchMusic(String category, String state) {
        if (isServiceRunning(getApplicationContext(), MediaService.class)) {
            Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_please_stop_music_before_searching), Toast.LENGTH_SHORT).show();
            return;
        }
//        String spinnerSelectedMusicCategoryId = ((MusicCategory) spinnerMusicCategory.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_music_category)) ? "" : ((MusicCategory) spinnerMusicCategory.getSelectedItem()).getId();
//        String spinnerSelectedCityId = ((City) spinnerCity.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_city)) ? "" : ((City) spinnerCity.getSelectedItem()).getId();
//
//                if (spinnerSelectedMusicCategoryId.equalsIgnoreCase("")) {
//                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_empty_music_category_field), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//        if (spinnerSelectedCityId.equalsIgnoreCase("")) {
//            Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_empty_city_field), Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (!NetworkManager.isConnected(HomeActivity.this)) {
            Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        new GetMusicList(HomeActivity.this, user.getId(), category, state).execute();
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
        } else if (isServiceRunning(getApplicationContext(), MediaService.class)) {
            Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_please_stop_music_before_closing), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    /***********************
     * Update spinner data *
     ***********************/
//    private void initSpinnerData() {
//        spinnerMusicCategory = (Spinner) findViewById(R.id.spinner_music_category);
//        spinnerCity = (Spinner) findViewById(R.id.spinner_city);
//
//        spinnerMusicCategoryAdapter = new CommonSpinnerAdapter(HomeActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY);
//        spinnerMusicCategory.setAdapter(spinnerMusicCategoryAdapter);
//
//        spinnerCityAdapter = new CommonSpinnerAdapter(HomeActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.CITY);
//        spinnerCity.setAdapter(spinnerCityAdapter);
//
//        //Get spinner data
//        setMusicCategory();
//        setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY);
//
//        setCityWithCountry();
//        setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE.CITY);
//
//        searchMusic();
//    }
    private void setCityWithCountry() {
        if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY))) {
            SessionManager.setStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY, AllConstants.getDefaultCountryData());
        }
    }

    private void setMusicCategory() {
        if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY))) {
            SessionManager.setStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY, AllConstants.getDefaultMusicCategoryData());
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

//    private void setSpinnerData(CommonSpinnerAdapter.ADAPTER_TYPE adapterType) {
//
//        if (adapterType == CommonSpinnerAdapter.ADAPTER_TYPE.MUSIC_CATEGORY) {
//            //set music category spinner data
//            if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY))) {
//                wrapperMusicCategoryData = MusicCategoryData.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_MUSIC_CATEGORY), MusicCategoryData.class);
//
//                spinnerMusicCategoryAdapter.setData(wrapperMusicCategoryData.getMusicCategory());
//                spinnerMusicCategory.setSelection(0);
//            }
//        } else if (adapterType == CommonSpinnerAdapter.ADAPTER_TYPE.CITY) {
//            //set city spinner data
//            if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY))) {
//                wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(HomeActivity.this, SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);
//                Log.d(TAG, "before setting spinner: " + wrapperCityWithCountryData.toString());
//
//                timeZone = wrapperCityWithCountryData.getAnyTimezone(strTimeZone);
//                spinnerCityAdapter.setData(timeZone.getCity());
//                spinnerCity.setSelection(spinnerCityAdapter.getItemPosition(SessionManager.getStringSetting(HomeActivity.this, SESSION_SELECTED_CITY)));
//            }
//        }
//    }

    /*****************************
     * Broadcast activity update *
     *****************************/
    private void updateUI(Intent intent) {
        if (intent != null && intent.getParcelableExtra(KEY_INTENT_EXTRA_MUSIC_UPDATE) != null) {

            Music music = intent.getParcelableExtra(KEY_INTENT_EXTRA_MUSIC_UPDATE);

            if (musicListViewAdapter != null) {
                musicListViewAdapter.updateMusic(music);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (musicListViewAdapter != null) {
            musicListViewAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }

    /***************************
     * Fabulous Filter methods *
     ***************************/

    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());

        if (result != null) {

            if (result.toString().equalsIgnoreCase("swiped_down")) {
                //do something or nothing
            } else {

                appliedFilters = (ArrayMap<String, List<String>>) result;
                ArrayMap<String, List<String>> appliedFilters = (ArrayMap<String, List<String>>) result;
                if (appliedFilters.size() != 0) {

                    if (appliedFilters.get("category") != null) {
                        if (appliedFilters.get("category").size() == 1) {
                            selectedMusicCategory = appliedFilters.get("category").get(0);
                        } else {
                            selectedMusicCategory = "";
                        }
                    } else {
                        selectedMusicCategory = "";
                    }

                    if (appliedFilters.get("state") != null) {
                        if (appliedFilters.get("state").size() == 1) {
                            selectedState = appliedFilters.get("state").get(0);
                        }
                    }

                    searchMusic(getSelectedMusicCategory(selectedMusicCategory).getId(), getSelectedCity(selectedState).getId());
                }
            }
            //handle result
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }
    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");
    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");
    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");
    }

    public List<String> getUniqueCategoryKeys(ArrayList<MusicCategory> musicCategories) {
        List<String> categories = new ArrayList<>();
        for (MusicCategory musicCategory : musicCategories) {
            categories.add(musicCategory.getName());
        }
        Collections.sort(categories);
        return categories;
    }

    public List<String> getUniqueStateKeys(ArrayList<City> states) {
        List<String> cities = new ArrayList<>();
        for (City city : states) {
            cities.add(city.getName());
        }
        Collections.sort(cities);
        return cities;
    }

    public List<String> getCategoryKey() {
        return mCategoryKey;
    }

    public List<String> getStateKey() {
        return mStateKey;
    }

    public MusicCategory getSelectedMusicCategory(String category) {
        for (MusicCategory musicCategory : mCategory) {
            if (musicCategory.getName().equalsIgnoreCase(category)) {
                return musicCategory;
            }
        }
        return new MusicCategory("", "");
    }

    public City getSelectedCity(String city) {
        for (City mCity : mState) {
            if (mCity.getName().equalsIgnoreCase(city)) {
                return mCity;
            }
        }
        return new City("", "");
    }
}
