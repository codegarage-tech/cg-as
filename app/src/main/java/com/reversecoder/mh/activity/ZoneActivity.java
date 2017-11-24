package com.reversecoder.mh.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.mh.R;
import com.reversecoder.mh.adapter.StateListAdapter;
import com.reversecoder.mh.model.City;
import com.reversecoder.mh.model.ResponseCountry;
import com.reversecoder.mh.model.ResponseMusicCategory;
import com.reversecoder.mh.model.TimeZone;
import com.reversecoder.mh.util.AllConstants;
import com.reversecoder.mh.util.AllUrls;
import com.reversecoder.mh.util.AppUtils;
import com.reversecoder.mh.util.HttpRequestManager;

import static com.reversecoder.mh.util.AllConstants.SESSION_CITY_WITH_COUNTRY;
import static com.reversecoder.mh.util.AllConstants.SESSION_MUSIC_CATEGORY;
import static com.reversecoder.mh.util.AllConstants.SESSION_SELECTED_CITY;
import static com.reversecoder.mh.util.AllConstants.SESSION_SELECTED_ZONE;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class ZoneActivity extends AppCompatActivity {

    LinearLayout llPst, llMst, llCst, llEst;
    String[] zones;
    TextView tvTitle;
    private String TAG = ZoneActivity.class.getSimpleName();
    String strTimeZone;
    TimeZone timeZone;
    ImageView ivBack;
    ResponseCountry wrapperCityWithCountryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        initZoneUI();
        initZoneAction();
    }

    private void initZoneUI() {

        tvTitle = (TextView) findViewById(R.id.text_title);
        tvTitle.setText(getString(R.string.title_activity_zone));

        ivBack = (ImageView) findViewById(R.id.menu_hamburger);

        llPst = (LinearLayout) findViewById(R.id.ll_pst);
        llMst = (LinearLayout) findViewById(R.id.ll_mst);
        llCst = (LinearLayout) findViewById(R.id.ll_cst);
        llEst = (LinearLayout) findViewById(R.id.ll_est);

        zones = getResources().getStringArray(R.array.zone_name);

        if (NetworkManager.isConnected(ZoneActivity.this)) {
            new GetCityWithCountry(ZoneActivity.this).execute();
            new GetAllMusicCategory(ZoneActivity.this).execute();
        } else {
            setDefaultCityWithCountry();
            setDefaultMusicCategory();
        }
    }

    private void setDefaultCityWithCountry(){
        if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(ZoneActivity.this, SESSION_CITY_WITH_COUNTRY))) {
            SessionManager.setStringSetting(ZoneActivity.this, SESSION_CITY_WITH_COUNTRY, AllConstants.getDefaultCountryData());
        }
    }

    private void setDefaultMusicCategory(){
        if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(ZoneActivity.this, SESSION_MUSIC_CATEGORY))) {
            SessionManager.setStringSetting(ZoneActivity.this, SESSION_MUSIC_CATEGORY, AllConstants.getDefaultMusicCategoryData());
        }
    }

    private void initZoneAction() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llPst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.setStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE, zones[0]);
                strTimeZone = SessionManager.getStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE);

                showStateDialog();
            }
        });

        llMst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.setStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE, zones[1]);
                strTimeZone = SessionManager.getStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE);

                showStateDialog();
            }
        });

        llCst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.setStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE, zones[2]);
                strTimeZone = SessionManager.getStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE);

                showStateDialog();
            }
        });

        llEst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.setStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE, zones[3]);
                strTimeZone = SessionManager.getStringSetting(ZoneActivity.this, SESSION_SELECTED_ZONE);

                showStateDialog();
            }
        });

    }

    private void showStateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_state_list);
        dialog.setCancelable(false);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_left_right;
        Button btnClose = (Button) dialog.getWindow().findViewById(R.id.btn_close);
        Button btnSuccessOk = (Button) dialog.getWindow().findViewById(R.id.btn_ok);
        ListView lvState = (ListView) dialog.getWindow().findViewById(R.id.lv_state);

        final StateListAdapter stateAdapter = new StateListAdapter(ZoneActivity.this);
        lvState.setAdapter(stateAdapter);
        if (SessionManager.getStringSetting(ZoneActivity.this, SESSION_CITY_WITH_COUNTRY) != null) {
            wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(ZoneActivity.this, SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);
            Log.d(TAG, "before setting listview: " + wrapperCityWithCountryData.toString());

            timeZone = wrapperCityWithCountryData.getAnyTimezone(strTimeZone);
            stateAdapter.setData(timeZone.getCity());
        }

        btnSuccessOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stateAdapter.isAnyItemSelected()) {
                    SessionManager.setStringSetting(ZoneActivity.this, SESSION_SELECTED_CITY, ((City) stateAdapter.getSelectedItem()).getName());

                    dialog.dismiss();

                    goHome();

                } else {
                    Toast.makeText(ZoneActivity.this, getString(R.string.toast_please_select_any_city), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void goHome() {
        Intent intent = new Intent(ZoneActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public class GetCityWithCountry extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;

        public GetCityWithCountry(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doGetRequest(AllUrls.getAllCityWithCountryUrl());
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseCountry responseData = ResponseCountry.getResponseObject(result.getResult().toString(), ResponseCountry.class);
                Log.d(TAG, "success response from object: " + responseData.toString());

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from cityWithCountry: " + responseData.toString());
                    SessionManager.setStringSetting(mContext, SESSION_CITY_WITH_COUNTRY, responseData.toString());
                    Log.d(TAG, "success response from session: " + SessionManager.getStringSetting(mContext, SESSION_CITY_WITH_COUNTRY));
                } else {
                    setDefaultCityWithCountry();
                    Toast.makeText(ZoneActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                setDefaultCityWithCountry();
                Toast.makeText(ZoneActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
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
                } else {
                    setDefaultMusicCategory();
                    Toast.makeText(ZoneActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                setDefaultMusicCategory();
                Toast.makeText(ZoneActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
